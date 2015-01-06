(ns flatland.handler
  (:use compojure.core)
  (:require [compojure.handler :as handler]))

(defroutes app
  (GET "/" [] "Hello, world!"))


