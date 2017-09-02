(ns lumo-blog.db.seeder
  (:require [lumo-blog.db.user :as user]
            [lumo-blog.db.post :as post]
            [lumo-blog.db.core :as db]
            [lumo-blog.util :as util]))

(def promisify (.-promisify (js/require "util")))

(def users
  [{:email "zach@codeup.com" :password "codeup"}
   {:email "test@gmail.com" :password "codeup"}])

(def posts
  [{:title "test post" :body "please ignore"}
   {:title "The joys of asynchronous operations"
    :body "It's kinda fun, but can also lead to some nasty-to-debug situations"}
   {:title "the third" :body "we need more data!"}])

(defn seed-users []
  (js/Promise.
    (fn [resolve reject]
      (let [pending-requests (atom 0)
            done (fn [] (swap! pending-requests dec)
                   (when (= 0 @pending-requests)
                     (resolve)))]
        (doseq [user users]
          (swap! pending-requests inc)
          (.then (user/insert user) done))))))

(defn seed-posts [user-id]
  (js/Promise.
    (fn [resolve reject]
      (let [pending-requests (atom 0)
            done (fn [] (swap! pending-requests dec)
                   (when (= 0 @pending-requests) (resolve)))]
        (doseq [post posts]
          (swap! pending-requests inc)
          (.then (post/insert (assoc post :user_id user-id)) done))))))

(def query (promisify db/query))

(defn run []
  (util/log-warning "Running Seeder...")
  (util/ps (.resolve js/Promise)
           #(query "SET FOREIGN_KEY_CHECKS = 0")
           #(query "TRUNCATE users")
           #(query "TRUNCATE posts")
           #(query "SET FOREIGN_KEY_CHECKS = 1")
           #(seed-users)
           (fn [] (user/by-email "zach@codeup.com"))
           (fn [user] (seed-posts (:id user)))
           #(user/by-email "test@gmail.com")
           #(post/insert {:title "access-control" :body "foobar" :user_id (:id %)})))

(defn -main []
  (.then (run)
         (fn [] (.end db/connection) (util/log-success "Seeder finished!"))
         (fn [err] (util/log-error err))))
