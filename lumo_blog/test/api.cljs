; 19 21 41 42 62

(ns lumo-blog.test.api
  (:require [cljs.test :as test]
            [lumo-blog.server :refer [configure-app]]
            [lumo-blog.util :as util]
            [lumo-blog.db.core :as db]
            [lumo-blog.db.seeder :as seeder]
            [lumo-blog.db.migration :as migration]
            [lumo-blog.validation :as validate]))

;; request default options
(def request (.defaults (js/require "request")
                        (clj->js {:jar true
                                  :baseUrl "http://localhost:1312"
                                  :json true})))

(def express (js/require "express"))
(def app (configure-app (express)))
(def server (.listen app 1312 #(util/log-info "server started on port 1312!")))

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

(defn test-account []
  (util/ps (login "zach@codeup.com" "codeup")
           #(req {:method "get" :url "/account"})
           (fn [[res user]]
             (test/is (= "zach@codeup.com" (:email user))))))

(defn test-post-crud []
  (util/ps (login "zach@codeup.com" "codeup")
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
                 (test/is (= 200 res.statusCode))
                 (test/is (= "ok" body))
                 (:id post))))
           ;; and make sure it's gone
           (fn [id] (req {:method "get" :url (str "/posts/" id)}))
           (fn [[res body]]
             (test/is (= 404 res.statusCode) "404 for trying to access a missing post")
             (test/is (string? (:error body)) "tring to access a missing post returns a body with an error"))))

(defn -main []
  (util/log-info "Starting...")
  (.catch (util/ps (migration/run)
                   #(seeder/run)
                   ;;
                   #(test-authentication)
                   #(test-posts-index)
                   #(test-account)
                   #(test-post-crud)
                   ;;
                   #(.end db/connection)
                   #(.close server)
                   #(util/log-success "api tests done!"))
          (fn [err] (util/log-error err))))
