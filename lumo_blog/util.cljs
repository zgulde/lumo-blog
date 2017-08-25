(ns lumo-blog.util
  (:require [clojure.string :as string]))

(defn colorize [text code] (str "\033[01;" code "m" text "\033[0m"))
(defn red [text] (colorize text 31))
(defn green [text] (colorize text 32))
(defn yellow [text] (colorize text 33))
(defn blue [text] (colorize text 34))

(defn log-info [text]
    (println (green (str "[INFO] " text))))

(def fs (js/require "fs"))

(defn slurp [filename]
  (.toString (fs.readFileSync filename)))
