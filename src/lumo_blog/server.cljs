(ns lumo-blog.server
  (:require [lumo-blog.controllers.post :as post-controller]
            [lumo-blog.controllers.user :as user-controller]
            [lumo-blog.middlewares :as mw]
            [lumo-blog.util :refer [log-info assign]]))

(def body-parser (js/require "body-parser"))
(def cookie-session (js/require "cookie-session"))
(def static (.-static (js/require "express")))

(defn configure-app [app]
  (-> app
      ;; middlewares
      (.use (static "public"))
      (.use (.json body-parser))
      (.use (cookie-session (clj->js {:name "my-session" :secret "secret"})))
      (.use mw/error-handler)

      ;; routes
      (.post "/login" user-controller/login)
      (.post "/logout" mw/logged-in user-controller/logout)
      (.get "/account" mw/logged-in user-controller/account)

      (.post "/users" user-controller/signup)

      (.get "/posts" post-controller/index)
      (.get "/posts/:id" post-controller/show)
      (.post "/posts" mw/logged-in post-controller/create)
      (.put "/posts/:id" mw/logged-in mw/post-access-control post-controller/update)
      (.delete "/posts/:id" mw/logged-in mw/post-access-control post-controller/destroy)))
