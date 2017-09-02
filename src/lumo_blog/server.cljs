(ns lumo-blog.server
  (:require [lumo-blog.controllers.post :as post-controller]
            [lumo-blog.controllers.user :as user-controller]
            [lumo-blog.middlewares :as mw]
            [lumo-blog.util :refer [log-info assign]]))

(def body-parser (js/require "body-parser"))
(def cookie-session (js/require "cookie-session"))
(def static (.-static (js/require "express")))

(defn configure-app [app]
  ;; middlewares
  (.use app (static "public"))
  (.use app (.json body-parser))
  (.use app (cookie-session (clj->js {:name "my-session" :secret "secret"})))
  (.use app mw/error-handler)

  ;; routes
  (.post app "/login" user-controller/login)
  (.post app "/logout" mw/logged-in user-controller/logout)
  (.get app "/account" mw/logged-in user-controller/account)

  (.post app "/users" user-controller/signup)

  (.get app "/posts" post-controller/index)
  (.get app "/posts/:id" post-controller/show)
  (.post app "/posts" mw/logged-in post-controller/create)
  (.put app "/posts/:id" mw/logged-in mw/post-access-control post-controller/update)
  (.delete app "/posts/:id" mw/logged-in mw/post-access-control post-controller/destroy)
  app)
