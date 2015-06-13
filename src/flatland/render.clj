(ns flatland.render
  (require [flatland.templates :refer [posts pages config template]]
           [markdown.core :refer [md-to-html-string]]
           [hiccup.core :refer [html]]))

(defn render-post
  "Retrieve post with filename <name>.md and convert it to HTML."
  [name]
  (let [post (get (posts) name)]
    (md-to-html-string (str "###" (:title post) "\n" (:content post)) :reference-links true)))

(defn render-page
  [name]
  (md-to-html-string (:content (get (pages) name)) :reference-links true))

(defn render-with-template
  "Takes content (as HTML string) and renders it in the <div class=\"content\">
   element of the template."
  [template content]
  ;; using a regex here to avoid parsing the markdown html back into hiccup data
  (clojure.string/replace template #"<div class=\"replace-me\"></div>" content))

(defn render-index
  "Display a list of blog posts on the index page."
  []
  (render-with-template template
                        (html (for [[name post]
                                    (map #(find (posts) %) (keys (posts)))]
                                [:section {:class "post"}
                                 [:h3
                                  [:a {:href (str (:base-url config) "posts/" name)}
                                      (:title post)]]
                                 [:p (:content post)]]))))
