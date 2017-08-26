(ns lumo-blog.core
  (:require [lumo-blog.server :refer [configure-app]]
            [lumo-blog.util :refer [log-info]]))

(def express (js/require "express"))
(def app (configure-app (express)))

(defn -main []
  (.listen app 1312 #(log-info "app started on port 1312!")))


