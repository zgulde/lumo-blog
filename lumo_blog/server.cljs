(ns lumo-blog.server
  (:require [lumo-blog.controllers.post :as post-controller]
            [lumo-blog.controllers.user :as user-controller]
            [lumo-blog.util :refer [log-info assign]]))

(defn error-handler
  [err req res next]
  (.status res 500)
  (.json res (clj->js {:error err})))

(defn logged-in-mw [req res next]
  (if req.session.user_id
    (next)
    (do (.status res 403)
        (.json res (clj->js {:error "Forbidden"})))))

(def express (js/require "express"))
(def app (express))

(def body-parser (js/require "body-parser"))
(def cookie-session (js/require "cookie-session"))

(.use app (.json body-parser))
(.use app (cookie-session (clj->js {:name "my-session" :secret "secret"})))
(.use app error-handler)

(.post app "/login" user-controller/login)
(.post app "/logout" logged-in-mw user-controller/logout)
(.get app "/account" logged-in-mw user-controller/account)

(.get    app "/posts"     post-controller/index)
(.get    app "/posts/:id" post-controller/show)
(.post   app "/posts"     logged-in-mw post-controller/create)
(.put    app "/posts/:id" logged-in-mw post-controller/update)
(.delete app "/posts/:id" logged-in-mw post-controller/destroy)

(.get app "/counter"
      (fn [req res]
        (assign req.session {:views (inc (or req.session.views 0))})
        (.send res (str req.session.views))))

(def server (.listen app 1312 #(log-info "server started!")))

; (.close server)
