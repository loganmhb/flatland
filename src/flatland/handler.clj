(ns flatland.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [markdown.core :refer [md-to-html-string]]))

(defn- render-post
  "Retrieve post with filename <title>.md and convert it to HTML."
  [title]
  (md-to-html-string (slurp (str "resources/posts/" title ".md"))))

(defroutes app
  (GET "/" [] "Hello, world!")
  (GET "/posts/:title" [title] (render-post title)))

