(ns flatland.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [markdown.core :refer [md-to-html-string]]
            [hiccup.core :refer [html]]))

(defn- parse-yaml
  "Parses yaml data into a Clojure map."
  [yaml]
  (reduce (fn [m [k v]]
            (assoc m (keyword (clojure.string/lower-case k)) v))
          {}
          (map #(clojure.string/split % #": ")
               (clojure.string/split yaml  #"\n"))))

(defn- slurp-md
  "Takes a Markdown file and returns a map with the content and yaml data."
  [post-file]
  (let [text (slurp post-file)]
    (let [[data content] (clojure.string/split text #"\n---\n" 2)]
      (assoc (parse-yaml data) :content (md-to-html-string content)))))

;; Read posts from resources/posts and parse them into maps (keys :yaml, :content),
;; storing them in one large map keyed by name (WITHOUT .md extension)
(defn load-md [dir]
  (reduce #(assoc %1 
                  (clojure.string/replace (.getName %2) #".md" "")
                  (slurp-md %2))
          {}
          (rest (file-seq (clojure.java.io/file dir)))))

(def posts (load-md "resources/posts"))
(def pages (load-md "resources/pages"))

(def config {:title "Cognitive Jetsam"
             :base-url "http://blog.loganbuckley.com/"
             :pages pages})

(def template
  (str "<!DOCTYPE html>"
       (html [:html
              [:head
               [:title (:title config)]
               [:link {:rel "stylesheet" :href "/css/style.css"}]]
              [:body
               [:header {:id "header"}
                [:div {:class "inner clearfix"}
                 [:h1 [:a {:href (:base-url config)} (:title config)]]
                 [:ul {:class "nav"}
                  (for [page (:pages config)]
                    [:li [:a {:href (get page :url)} (get page :title)]])]]]
               [:section {:id "content"}
                [:div {:class "inner"}
                 [:div {:class "replace-me"}]]] ; workaround to avoid parsing HTML
               [:footer {:id "footer"}
                [:div {:class "inner"}
                 [:a {:href "http://github.com/loganmhb/flatland"}
                     "This blog was created with Flatland."]]]]])))

(defn- render-post
  "Retrieve post with filename <name>.md and convert it to HTML."
  [name]
  (md-to-html-string (:content (get posts name))))

(defn- render-page
  [name]
  (md-to-html-string (:content (get pages name))))

(defn- render-with-template
  "Takes content (as HTML string) and renders it in the <div class=\"content\">
   element of the template."
  [template content]
  ;; using a regex here to avoid parsing the markdown html back into hiccup data
  (clojure.string/replace template #"<div class=\"replace-me\"></div>" content))

(defn- render-index
  []
  (render-with-template template
                        (html (for [[name post]
                                    (map #(find posts %) (keys posts))]
                                [:section {:class "post"}
                                 [:h3
                                  [:a {:href (str (:base-url config) "posts/" name)}
                                      (:title post)]]
                                 [:p (:content post)]]))))

(defroutes app
  (GET "/" [] (render-index))
  ;; Only match posts that exist
  (GET ["/posts/:name", :name (re-pattern (clojure.string/join "|" (keys posts)))]
       [name]
       (render-with-template template (render-post name)))
  (GET ["/:page", :page (re-pattern (clojure.string/join "|" (keys pages)))]
       [page]
       (render-with-template template (render-page page)))
  (route/files "/" {:root "public"})
  (route/not-found "<h1>Page not found.</h1>"))
