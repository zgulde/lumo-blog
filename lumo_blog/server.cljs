(ns lumo-blog.server
  (:require [lumo-blog.controllers.post :as post-controller]
            [lumo-blog.controllers.user :as user-controller]
            [lumo-blog.middlewares :as mw]
            [lumo-blog.util :refer [log-info assign]]))

(def express (js/require "express"))
(def app (express))

(def body-parser (js/require "body-parser"))
(def cookie-session (js/require "cookie-session"))

(.use app (.json body-parser))
(.use app (cookie-session (clj->js {:name "my-session" :secret "secret"})))
(.use app mw/error-handler)

(.post app "/login" user-controller/login)
(.post app "/logout" mw/logged-in user-controller/logout)
(.get app "/account" mw/logged-in user-controller/account)

(.post app "/users" user-controller/signup)

(.get app "/posts" post-controller/index)
(.get app "/posts/:id" post-controller/show)
(.post app "/posts" mw/logged-in post-controller/create)
(.put app "/posts/:id" mw/logged-in mw/post-access-control post-controller/update)
(.delete app "/posts/:id" mw/logged-in mw/post-access-control post-controller/destroy)

(.get app "/counter"
      (fn [req res]
        (assign req.session {:views (inc (or req.session.views 0))})
        (.send res (str req.session.views))))

(def server (.listen app 1312 #(log-info "server started!")))

; (.close server)
