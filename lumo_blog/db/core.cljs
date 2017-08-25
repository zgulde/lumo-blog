(ns lumo-blog.db.core
  (:require [lumo-blog.env :as env]))

(def mysql (js/require "mysql"))

(def connection
  (mysql.createConnection
    (clj->js {:host env/host
              :user env/user
              :password env/pass
              :database env/db})))

(.connect connection)

(defn query
  ([sql cb] (query sql [] cb))
  ([sql params cb]
   (.query connection sql (clj->js params)
           (fn [err results fields]
             (cb err results fields)))))

(defn insert
  [sql params cb]
  (.query connection sql (clj->js params)
          (fn [err results fields]
            (cb err (if err nil results.insertId)))))
