(ns lumo-blog.test.api
  (:require [cljs.test :as test]
            [lumo-blog.server :refer [configure-app]]
            [lumo-blog.util :refer [ppipe] :as util]
            [lumo-blog.db.core :as db]
            [lumo-blog.db.seeder :as seeder]
            [lumo-blog.db.migration :as migration]
            [lumo-blog.validation :as validate]))

;; request default options
(def request (.defaults (js/require "request")
                        (clj->js {:jar true
                                  :baseUrl "http://localhost:1312"
                                  :json true})))

(defn- req [opts]
  (js/Promise.
    (fn [resolve reject]
      (request (clj->js opts)
               (fn [err res body]
                 (if err
                   (reject err)
                   (resolve [res (js->clj body :keywordize-keys true)])))))))

(defn test-route-is-restricted [method url]
  (.then (req {:method method :url url})
         (fn [[res body]]
           (test/is (= 403 res.statusCode) (str method " to " url " gives 403"))
           (test/is (string? (:error body)) (str method " to " url " has an error field in the response body")))))

(defn- login [email password]
  (req
    {:method "post" :url "/login" :body {:email email :password password}}))
(defn- logout [] (req {:method "post" :url "/logout"}))

(defn test-authentication []
  (.all js/Promise
        [(test-route-is-restricted "get" "/account")
         (test-route-is-restricted "post" "/logout")
         (test-route-is-restricted "post" "/posts")
         (test-route-is-restricted "put" "/posts/1")
         (test-route-is-restricted "delete" "/posts/1")]))

(defn test-posts-index []
  (.then (req {:method "get" :url "/posts"})
         (fn [[res posts]]
           (test/is (seq posts) "/posts returns an array of posts")
           (test/is (every? #(:passes (validate/post %)) posts)))))

(defn test-login-with-nonexistent-email []
  (ppipe (login "notavalid@email" "nopass")
         (fn [[res body]]
           (test/is (string? (:error body)))
           (test/is (.test (js/RegExp. "invalid email" "i") (:error body))))))

(defn test-account []
  (ppipe (login "zach@codeup.com" "codeup")
         #(req {:method "get" :url "/account"})
         (fn [[res user]]
           (test/is (= "zach@codeup.com" (:email user)) "account page returns an email")
           (test/is (.test (js/RegExp. "\\d+") (:id user)) "account page returns an id for the user"))
         #(logout)))

(defn test-post-crud []
  (ppipe (login "zach@codeup.com" "codeup")
         ;; create a new post
         #(req {:method "post" :url "/posts" :body {:title "new post"
                                                    :body "test body"}})
         ;; make sure we get back 200 and an id
         (fn [[res post]]
           (test/is (= 200 res.statusCode))
           (test/is (some? (:id post)))
           post)
         ;; get /posts/{new-id} and ensure it matches what we just created
         (fn [{:keys [id title body]}]
           (.then (req {:method "get" :url (str "/posts/" id)})
                  (fn [[res post]]
                    (test/is (= id (:id post)))
                    (test/is (= title (:title post)))
                    (test/is (= body (:body post)))
                    post)))
         ;; update our post
         (fn [post]
           (req {:method "put" :url (str "/posts/" (:id post))
                 :body {:title "new title" :body "new body"}}))
         ;; and make sure it was updated
         (fn [[res body]]
           (test/is (= 200 res.statusCode))
           (.then (req {:method "get" :url (str "/posts/" (:id body))})
                  (fn [[res post]]
                    (test/is (= "new title" (:title post)) "title is updated")
                    (test/is (= "new body" (:body post)) "body is updated")
                    post)))
         ;; delete it
         (fn [post]
           (.then
             (req {:method "delete" :url (str "/posts/" (:id post)) :json false})
             (fn [[res body]]
               (test/is (= 200 res.statusCode) "200 is returned after DELETE request")
               (test/is (= "ok" body) "body of a DELETE request is 'ok'")
               (:id post))))
         ;; and make sure it's gone
         (fn [id] (req {:method "get" :url (str "/posts/" id)}))
         (fn [[res body]]
           (test/is (= 404 res.statusCode) "404 for trying to access a missing post")
           (test/is (string? (:error body)) "tring to access a missing post returns a body with an error"))
         #(logout)))

(defn test-post-access-control []
  (ppipe (login "zach@codeup.com" "codeup")
         ; get the posts and our user id
         (fn []
           (.then (req {:url "/posts" :method "get"})
                  (fn [[res posts]] (.then (req {:url "/account" :method "get"})
                                           (fn [[res user]] [posts (:id user)])))))
         ; find a post that doesn't belong to us
         (fn [[posts user-id]] (first (filter #(not (= user-id (:user_id %))) posts)))
         ; and try to modify it
         (fn [restricted-post]
           (req {:method "put" :url (str "/posts/" (:id restricted-post))
                 :body {:title "altered title" :body "altered body"}}))
         (fn [[res body]]
           (test/is (= 403 res.statusCode))
           (test/is (string? (:error body)))
           (test/is (.test (js/RegExp. "permission") (:error body))))
         #(logout)))

(defn test-post-updates-are-validated []
  (ppipe (login "zach@codeup.com" "codeup")
         (fn []
           (.then (req {:url "/posts" :method "get"})
                  (fn [[res posts]] (.then (req {:url "/account" :method "get"})
                                           (fn [[res user]] [posts (:id user)])))))
         (fn [[posts user-id]] (first (filter #(= user-id (:user_id %)) posts)))
         (fn [post] (req {:method "put" :url (str "/posts/" (:id post))
                          :body {}}))
         (fn [[res body]]
           (test/is (= 422 res.statusCode) "bad update request sends back a 422")
           (test/is (string? (first (:title body))) "bad update request gives a title error message")
           (test/is (string? (first (:body body))) "bad update request gives a body error"))
         #(logout)))

(defn test-login-returns-user-object []
  (.then (login "zach@codeup.com" "codeup")
         (fn [[res body]]
           (test/is (= true (:success body)))
           (let [user (:user body)]
             (test/is (string? (:email user)))
             (test/is (.test (js/RegExp. "\\d+") (:id user)) "login response has an id for the user")))))

(defn run []
  (util/log-info "Running api tests...")
  (let [express (js/require "express")
        app (configure-app (express))
        server (.listen app 1312 #(util/log-info "server started on port 1312!"))]
    (.catch (ppipe (migration/run)
                   #(seeder/run)
                   ;;
                   #(test-post-updates-are-validated)
                   #(test-authentication)
                   #(test-posts-index)
                   #(test-account)
                   #(test-post-crud)
                   #(test-login-with-nonexistent-email)
                   #(test-post-access-control)
                   #(test-login-returns-user-object)
                   ;;
                   #(.end db/connection)
                   #(.close server)
                   #(util/log-info "Finished api tests!"))
            (fn [err] (util/log-error err)))))

(defn -main []
  (run))
