(ns lumo-blog.middlewares
  (:require [lumo-blog.util :refer [assign]]))

(defn error-handler
  [err req res next]
  (.status res 500)
  (.json res (clj->js {:error err})))

(defn logged-in [req res next]
  (if req.session.user_id
    (next)
    (do (.status res 403)
        (.json res (clj->js {:error "Please log in"})))))

(defn post-access-control
  [req res next]
  (if-not (= req.params.id req.session.user_id)
    (do (.status res 403) (.json res (clj->js {:error "you don't have permission for that resource"})))))
