(ns lumo-blog.validation
  (:require [lumo-blog.util :refer [log-info]]))

(defn- make-validator
  "takes a map of validation rules and returns a validation function"
  [validation-rules]
  (fn [item-to-validate]
    (let [validation-errors
          (reduce (fn [validation [key-name rules]]
                    (let [error-messages
                          (->> (partition 2 rules)
                               (remove (fn [[p _]]
                                         (p (key-name item-to-validate))))
                               (map (fn [[_ msg]] msg)))]
                      (if (seq error-messages)
                        (assoc validation key-name error-messages)
                        validation)))
                  {} validation-rules)]
      {:fails (not (empty? validation-errors))
       :passes (empty? validation-errors)
       :errors validation-errors})))

(def post (make-validator
  {:title [string? "title must be present"]
   :body [string? "body must be present"
          #(> (count %) 5) "body must be at least 6 characters"]}))

(def user (make-validator
  {:email [string? "must provide an email address"
           #(.includes % "@") "must be a valid email address"]
   :password [seq "password cannot be empty"]}))
