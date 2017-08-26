(ns lumo-blog.test.api
  (:require [cljs.test :as test]
            [lumo-blog.server :refer [configure-app]]
            [lumo-blog.util :refer [log-info]]
            [lumo-blog.db.core :as db]))

(def request (.defaults (js/require "request")
                        (clj->js {:jar true
                                  :baseUrl "http://localhost:1312"
                                  :json true})))

(def express (js/require "express"))
(def app (configure-app (express)))
(def server (.listen app 1312 #(log-info "server started on port 1312!")))

(def pending-requests (atom 0))

(defn- done []
  (when (= 1 @pending-requests)
    (log-info "All done, cleaning up...")
    (.close server)
    (.end db/connection))
  (swap! pending-requests dec))

(defn- req [opts cb]
  (swap! pending-requests inc)
  (request (clj->js opts)
           (fn [err res body]
             (cb err res (js->clj body :keywordize-keys true)))))

(defn test-restricted [method url]
  (req {:method method :url url}
       (fn [err res body]
         (test/is (= 403 res.statusCode))
         (test/is (string? (:error body)))
         (done))))

(test/deftest authentication

  (test/testing "/account"
    (test-restricted "get" "/account"))

  (test/testing "/posts"
    (test-restricted "post" "/posts")
    (req {:method "get" :url "/posts"}
         (fn [err res body]
           (let [id (:id (first body))]
             (test-restricted "put" (str "/posts/" id))
             (test-restricted "delete" (str "/posts/" id)))
           (done)))
    )
  )

(authentication)
