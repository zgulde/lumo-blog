(ns lumo-blog.controllers.post
  (:require [lumo-blog.db.post :as post]
            [lumo-blog.util :as util]
            [lumo-blog.validation :as validate]))

(defn index
  [req res]
  (.then (post/all)
         #(.json res (clj->js %))))

(defn show
  [req res]
  (.then (post/find req.params.id)
         (fn [post]
           (if post
             (.json res (clj->js post))
             (do (.status res 404)
                 (.json res (clj->js {:error "Not Found"})))))))

(defn create [req res]
  (let [post (assoc (js->clj req.body :keywordize-keys true)
                    :user_id req.session.user_id)
        validation (validate/post post)]
    (if (:passes validation)
      (.then (post/insert post)
             (fn [post] (.json res (clj->js post))))
      (do (.status res 422)
          (.json res (clj->js {:errors (:errors validation)}))))))

(defn update [req res]
  (let [post {:id req.params.id :title req.body.title :body req.body.body}
        validation (validate/post post)]
    (if (:passes validation)
      (.then (post/update post)
             (fn [rows]
               (.json res (clj->js post))))
      (do (.status res 422) (.json res (clj->js (:errors validation)))))))

(defn destroy
  [req res]
  (.then (post/delete req.params.id)
         #(.send res "ok")))
