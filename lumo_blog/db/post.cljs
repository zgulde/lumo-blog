(ns lumo-blog.db.post
  (:require [lumo-blog.db.core :as db]))

(def queries
    {:select "SELECT * FROM posts"
     :find "SELECT * FROM posts WHERE id = ?"
     :insert "INSERT INTO posts(title, body) VALUES (?, ?)"
     :update "UPDATE posts SET title = ?, body = ? WHERE id = ?"
     :delete "DELETE FROM posts WHERE id = ?"})

(defn- row->post [row]
  {:id row.id
   :title row.title
   :body row.body})

(defn all []
  (js/Promise.
    (fn [resolve reject]
      (db/query (:select queries)
                (fn [err rows]
                  (if err
                    (reject err)
                    (resolve (map row->post rows))))))))

(defn find [id]
  (js/Promise. (fn [resolve reject]
                 (db/query (:find queries)
                           (clj->js [id])
                           (fn [err rows]
                             (if err
                               (reject err)
                               (if (= 1 (count rows))
                                (resolve (row->post (first rows)))
                                (resolve nil))))))))

(defn insert
  [{:keys [title body]}]
  (js/Promise.
    (fn [resolve reject]
      (db/insert (:insert queries)
                 [title body]
                 (fn [err id]
                   (if err (reject err) (resolve {:title title
                                                  :body boyd
                                                  :id id})))))))

(defn update
  [{:keys [id title body]}]
  (js/Promise.
    (fn [resolve reject]
      (db/query (:update queries)
                [title body id]
                (fn [err rows]
                  (if err (reject err) (resolve rows)))))))

(defn delete [id]
  (js/Promise.
    (fn [resolve reject]
      (db/query (:delete queries)
                [id]
                (fn [err results]
                  (if err (reject err) (resolve results)))))))
