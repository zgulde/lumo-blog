(ns lumo-blog.db.seeder
  (:require [lumo-blog.db.user :as user]
            [lumo-blog.db.post :as post]
            [lumo-blog.db.core :as db]
            [lumo-blog.util :as util]))

(def users
  [{:email "zach@codeup.com" :password "codeup"}
   {:email "test@gmail.com" :password "codeup"}])

(def posts
  [{:title "" :body ""}])

(defn seed-users []
  (util/log-info "seeding users...")
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
  (util/log-info "seeding posts...")
  (js/Promise.
    (fn [resolve reject]
      (let [pending-requests (atom 0)
            done (fn [] (swap! pending-requests dec)
                   (when (= 0 @pending-requests) (resolve)))]
        (doseq [post posts]
          (swap! pending-requests inc)
          (.then (post/insert post) done))))))

(defn run []
  (util/ps (seed-users)
           (fn [] (user/by-email "zach@codeup.com"))
           (fn [user] (seed-posts (:id user)))))

(defn -main []
  (util/log-info "Starting Seeder...")
  (.then (run)
         (fn [] (.end db/connection) (util/log-success "Seeder finished!"))
         (fn [err] (util/log-error err))))
