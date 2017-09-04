(ns lumo-blog.db.user
  (:require [lumo-blog.db.core :as db]
            [lumo-blog.util :as util]))

(def queries
    {:select "SELECT * FROM users"
     :find "SELECT * FROM users WHERE id = ?"
     :insert "INSERT INTO users(email, password) VALUES (?, ?)"
     :update "UPDATE users SET email = ?, password = ? WHERE id = ?"
     :delete "DELETE FROM users WHERE id = ?"
     :by-email "SELECT * FROM users WHERE email = ?"})

(defn- row->post [row]
  {:id row.id
   :email row.email
   :password row.password})

(defn by-email [email]
  (js/Promise.
    (fn [resolve reject]
      (db/query (:by-email queries)
                (clj->js [email])
                (fn [err rows]
                  (if err
                    (reject err)
                    (if (= 1 (count rows))
                      (resolve (row->post (first rows)))
                      (resolve nil))))))))

(defn check-password [email plaintext]
  (.then (by-email email)
         (fn [user]
           (if user
             (.then (util/pw-check plaintext (:password user))
                    (fn [success]
                      [success user]))
             [false nil]))))

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
  [{:keys [email password]}]
  (js/Promise.
    (fn [resolve reject]
      (.then (util/pw-hash password)
        (fn [hash]
          (db/insert (:insert queries)
                     [email hash]
                     (fn [err id]
                       (if err (reject err)
                         (resolve {:email email
                                   :password password
                                   :id id})))))))))

(defn update
  [{:keys [id email password]}]
  (js/Promise.
    (fn [resolve reject]
      (db/query (:update queries)
                [email password id]
                (fn [err rows]
                  (if err (reject err) (resolve rows)))))))

(defn delete [id]
  (js/Promise.
    (fn [resolve reject]
      (db/query (:delete queries)
                [id]
                (fn [err results]
                  (if err (reject err) (resolve results)))))))
