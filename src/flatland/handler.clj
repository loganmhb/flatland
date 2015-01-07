(ns flatland.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [markdown.core :refer [md-to-html-string]]
            [hiccup.core :refer [html]]))

(def template 
  (str "<!DOCTYPE html>" (html [:html [:head [:title "Page Title"]]
              [:body [:div {:class "content"}]]])))

(defn- slurp-post
  "Takes a Markdown file and returns a map with the post data."
  [post-file]
  (let [text (slurp post-file)]
    (let [[data content] (clojure.string/split text #"\n---\n" 2)]
      {:yaml data, :content content})))

;; Read posts from resources/posts and parse them into maps (keys :yaml, :content),
;; storing them in one large map keyed by name (WITHOUT .md extension)
(def posts (reduce #(assoc %1 
                           (clojure.string/replace (.getName %2) ".md" "")
                           (slurp-post %2))
                   {}
                   (rest (file-seq (clojure.java.io/file "resources/posts")))))

(defn- post-exists?
  [name]
  (get posts name))

(defn- render-post
  "Retrieve post with filename <name>.md and convert it to HTML."
  [name]
  (md-to-html-string (:content (get posts name))))

(defn- render-with-template
  "Takes content (as HTML string) and renders it in the <div class=\"content\">
   element of the template."
  [template content]
  ;; using a regex here to avoid parsing the markdown html back into hiccup data
  (clojure.string/replace template #"<div class=\"content\"></div>" content))

(defn- render-post-with-template
  [name]
  (render-with-template template (render-post name)))

(defroutes app
  (GET "/" [] "Hello, world!")
  ;; Only match posts that exist
  (GET ["/posts/:name", :name (re-pattern (clojure.string/join "|" (keys posts)))]
       [name] (render-post-with-template name))
  (ANY "/*" [] (route/not-found "Page not found")))
