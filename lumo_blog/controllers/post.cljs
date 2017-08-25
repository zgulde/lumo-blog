(ns lumo-blog.controllers.post
  (:require [lumo-blog.db.post :as post]))

(defn index
  [req res]
  (.then (post/all)
         #(.json res (clj->js %))))

(defn show
  [req res]
  (.then (post/find req.params.id)
         (fn [post]
           (.json res (clj->js post)))))

(defn create
  [req res]
  (.then (post/insert (js->clj req.body :keywordize-keys true))
         (fn [post] (.json res (clj->js post)))))

(defn update
  [req res]
  (.then (post/update {:id req.params.id :title req.body.title :body req.body.body})
         (fn [] (.json res req.body))))

(defn destroy
  [req res]
  (.then (post/delete req.params.id)
         #(.send res "ok")))
