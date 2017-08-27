(ns lumo-blog.db.migration
  (:require [lumo-blog.db.core :as db]
            [lumo-blog.util :as util]))

(def promisify (.-promisify (js/require "util")))

(def create-users
  "CREATE TABLE users(
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    email TEXT,
    password TEXT,
    PRIMARY KEY (id)
  );")

(def create-posts
  "CREATE TABLE posts (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    title TEXT,
    body TEXT,
    user_id INT UNSIGNED,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
  );")

(def query (promisify db/query))

(defn run []
  (util/ps (js/Promise.resolve)
           #(query "DROP TABLE IF EXISTS posts")
           #(query "DROP TABLE IF EXISTS users")
           #(query create-users)
           #(query create-posts)))

(defn -main []
  (util/log-warning "Starting Migration...")
  (.then (run)
         (fn [] (.end db/connection) (util/log-success "Migrated!"))
         (fn [err] (util/log-error err))))
