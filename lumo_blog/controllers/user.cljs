(ns lumo-blog.controllers.user
  (:require [lumo-blog.db.user :as user]
            [lumo-blog.util :refer [assign]]))

(defn account [req res]
  (.then (user/find req.session.user_id)
         (fn [user] (.json res (clj->js (dissoc user :password))))))

(defn login [req res]
  (.then (user/check-password req.body.email req.body.password)
         (fn [[success id]]
           (if success (do (assign req.session {:user_id id})
                           (.json res (clj->js {:success true})))
               (.json res (clj->js {:error "invalid username or password"}))))))

(defn logout [req res]
  (do (assign req.session {:user_id nil})
      (.send res "ok")))

(defn show
  [req res]
  (.then (user/find req.params.id)
         (fn [user]
           (.json res (clj->js user)))))

(defn create
  [req res]
  (.then (user/insert (js->clj req.body :keywordize-keys true))
         (fn [user] (.json res (clj->js user)))))

(defn update
  [req res]
  (.then (user/update {:id req.params.id :email req.body.email :password req.body.password})
         (fn [] (.json res req.body))))

(defn destroy
  [req res]
  (.then (user/delete req.params.id)
         #(.send res "ok")))
