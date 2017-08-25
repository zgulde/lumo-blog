(ns lumo-blog.seeder
  (:require [lumo-blog.db.user :as user]
            [lumo-blog.db.post :as post]
            [lumo-blog.db.core :as db]))

(def users
  [{:email "zach@codeup.com" :password "codeup"}])

(doseq [user users]
  (user/insert user))

(js/setTimeout #(.end db/connection) 1500)

