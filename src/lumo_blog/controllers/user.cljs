(ns lumo-blog.controllers.user
  (:require [lumo-blog.db.user :as user]
            [lumo-blog.util :refer [assign] :as util]
            [lumo-blog.validation :as validate]))

(defn account [req res]
  (.then (user/find req.session.user_id)
         (fn [user] (.json res (clj->js (dissoc user :password))))))

(defn login [req res]
  (.then (user/check-password req.body.email req.body.password)
         (fn [[success user]]
           (if success (do (assign req.session {:user_id (:id user)})
                           (.json res (clj->js {:success true :user user})))
             (.json res (clj->js {:error "invalid email or password"}))))
         (fn [error]
           (util/log-error error)
           (.json res (clj->js {:error "invalid email or password"})))))

(defn logout [req res]
  (do (assign req.session {:user_id nil})
      (.send res "ok")))

(defn show
  [req res]
  (.then (user/find req.params.id)
         (fn [user]
           (.json res (clj->js user)))))

(defn signup
  [req res]
  (let [user (js->clj req.body :keywordize-keys true)
        validation (validate/user user)]
    (if (:fails validation)
      (do (.status res 422) (.json res (clj->js (:errors validation))))
      (.then (user/insert (js->clj req.body :keywordize-keys true))
             (fn [user] (.json res (clj->js user)))))))

(defn update
  [req res]
  (.then (user/update {:id req.params.id :email req.body.email :password req.body.password})
         (fn [] (.json res req.body))))

(defn destroy
  [req res]
  (.then (user/delete req.params.id)
         #(.send res "ok")))
