; 19 21 41 42 62

(ns lumo-blog.test.api
  (:require [cljs.test :as test]
            [lumo-blog.server :refer [configure-app]]
            [lumo-blog.util :as util]
            [lumo-blog.db.core :as db]
            [lumo-blog.db.seeder :as seeder]
            [lumo-blog.db.migration :as migration]))

;; request default options
(def request (.defaults (js/require "request")
                        (clj->js {:jar true
                                  :baseUrl "http://localhost:1312"
                                  :json true})))

(def express (js/require "express"))
(def app (configure-app (express)))
(def server (.listen app 1312 #(util/log-info "server started on port 1312!")))

(defn- req [opts cb]
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

; (defn- login [f]
;   (req {:method "post" :url "/login" :body {:email "zach@codeup.com"
;                                             :password "codeup"}}
;        (fn [err res body]
;          (if (= 200 res.statusCode)
;            (f)
;            (util/log-error "failed to login! " body)))))

(defn test-authentication []
  (.all js/Promise
        [(test-route-is-restricted "get" "/account")
         (test-route-is-restricted "post" "/logout")
         (test-route-is-restricted "post" "/posts")
         (test-route-is-restricted "put" "/posts/1")
         (test-route-is-restricted "delete" "/posts/1")]))

(defn -main []
  (util/log-info "Starting...")
  (.catch (util/ps (migration/run)
                   #(seeder/run)
                   #(test-authentication)
                   #(.end db/connection)
                   #(.close server)
                   #(util/log-success "All Done!"))
          (fn [err] (util/log-error err))))
