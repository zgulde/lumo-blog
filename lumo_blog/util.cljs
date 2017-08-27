(ns lumo-blog.util
  (:require [clojure.string :as string]))

(defn colorize [text code] (str "\033[01;" code "m" text "\033[0m"))
(defn red [text] (colorize text 31))
(defn green [text] (colorize text 32))
(defn yellow [text] (colorize text 33))
(defn blue [text] (colorize text 34))

(defn log-info [& items] (println (green (apply str "[INFO] " items))))
(defn log-success [& items] (println (green (apply str "[SUCCESS] " items))))
(defn log-warning [& items] (println (yellow (apply str "[WARN] " items))))
(defn log-error [& items] (println (red (apply str "[ERROR] " items))))

(def fs (js/require "fs"))

(defn slurp [filename]
  (.toString (fs.readFileSync filename)))

(defn assign [target & sources]
  (apply js/Object.assign target (map clj->js sources)))

(def bcrypt (js/require "bcryptjs"))

(defn pw-hash [plaintext]
  (js/Promise. (fn [resolve reject]
    (.hash bcrypt plaintext 10 (fn [err hash]
                                 (if err (reject err) (resolve hash)))))))

(defn pw-check [plaintext hash]
  (.compare bcrypt plaintext hash))

; FIXME this needs a better name
(defn ps
  "chains .then calls to apply fns to p"
  [p & fns] (reduce #(.then %1 %2) p fns))

; (ps (.resolve js/Promise 1)
;     (fn [n] (inc n))
;     (fn [n] (+ 3 n))
;     println) ; 5
; ## as opposed to ##
; (-> (.resolve js/Promise 1)
;     (.then (fn [n] (inc n)))
;     (.then (fn [n] (+ 3 n)))
;     (.then println))
