(ns lumo-blog.server
  (:require [lumo-blog.controllers.post :as post-controller]
            [lumo-blog.util :refer [log-info]]))

(def express (js/require "express"))
(def app (express))
(def body-parser (js/require "body-parser"))

(.use app (.json body-parser))

(.get    app "/posts"     post-controller/index)
(.get    app "/posts/:id" post-controller/show)
(.post   app "/posts"     post-controller/create)
(.put    app "/posts/:id" post-controller/update)
(.delete app "/posts/:id" post-controller/destroy)

(def server (.listen app 1312 #(log-info "server started!")))

; (.close server)
