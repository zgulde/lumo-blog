(ns lumo-blog.validation)

(defn post
  [{:keys [title body]}]
  {:fails (boolean (not (and title body)))
   :passes (boolean (and title body))
   :errors {:title (when (empty? title) "title must be present")
            :body (when (empty? body) "body must be present")}})
