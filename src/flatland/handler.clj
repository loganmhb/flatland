(ns flatland.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [markdown.core :refer [md-to-html-string]]
            [hiccup.core :refer [html]]))

(def template 
  (str "<!DOCTYPE html>" (html [:html [:head [:title "Page Title"]]
              [:body [:div {:class "content"}]]])))

(defn- render-post
  "Retrieve post with filename <title>.md and convert it to HTML."
  [title]
  (md-to-html-string (slurp (str "resources/posts/" title ".md"))))

(defn- render-with-template
  "Takes content (as HTML string) and renders it in the <div class=\"content\">
   element of the template."
  [template content]
  (clojure.string/replace template #"<div class=\"content\"></div>" content))

(defn- render-post-with-template
  [title]
  (render-with-template template (render-post title)))

(defroutes app
  (GET "/" [] "Hello, world!")
  (GET "/posts/:title" [title] (render-post-with-template title)))
