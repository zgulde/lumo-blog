(ns lumo-blog.middlewares
  (:require [lumo-blog.util :refer [assign ppipe] :as util]
            [lumo-blog.db.post :as post]))

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
  (let [post-id req.params.id]
    (ppipe (post/find post-id)
           (fn [post]
             (if (and post (= req.session.user_id (:user_id post)))
               (next)
               (do (.status res 403)
                   (.json res (clj->js
                                {:error (if post
                                          "you don't have permission for that resource"
                                          "post does not exist")}))))))))
