(ns flatland.templates
  (:require [hiccup.core :refer [html]]
            [environ.core :refer [env]]
            [markdown.core :refer [md-to-html-string]]))

(defn- parse-yaml
  "Parses yaml data into a Clojure map."
  [yaml]
  (reduce (fn [m [k v]]
            (assoc m (keyword (clojure.string/lower-case k)) v))
          {}
          (map #(clojure.string/split % #": ")
               (clojure.string/split yaml  #"\n"))))

(defn- gistify
  "Replaces %gist[id] in markdown w/ appropriate html."
  [content]
  (clojure.string/replace content
                          #"%gist([\da-z]+)"
                          "<p><code data-gist-id=$1></code></p>"))

(defn- slurp-md
  "Takes a Markdown file and returns a map with the content and yaml data."
  [post-file]
  (let [text (slurp post-file)]
    (let [[data content] (clojure.string/split text #"\n---\n" 2)]
      (assoc (parse-yaml data) :content (md-to-html-string (gistify content))))))

;; Read posts from resources/posts and parse them into maps (keys :yaml, :content),
;; storing them in one large map keyed by name (WITHOUT .md extension)
(defn load-md [dir]
  (reduce #(assoc %1 
                  (clojure.string/replace (.getName %2) #".md" "")
                  (slurp-md %2))
          {}
          (rest (file-seq (clojure.java.io/file dir)))))

(defn posts [] (load-md "resources/posts"))
(defn pages [] (load-md "resources/pages"))

(defn config [] {:title "(un)timely"
                 :base-url (or (env :base-url) "http://localhost:3000/")})

(defn include-script [link]
  [:script {:type "text/javascript"
            :src link}])

(def template
  (str "<!DOCTYPE html>"
       (html [:html
              [:head
               [:title (:title (config))]
               [:link {:rel "stylesheet" :href "/css/style.css"}]
               (include-script "https://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js")
               (include-script "https://cdnjs.cloudflare.com/ajax/libs/gist-embed/2.1/gist-embed.min.js")]
              [:body
               [:header {:id "header"}
                [:div {:class "inner clearfix"}
                 [:h1 [:a {:href (:base-url (config))} (:title (config))]]
                 [:ul {:class "nav"}
                  (for [[name page] (pages)]
                    [:li [:a {:href (str (:base-url (config)) name)}
                          (get page :title)]])]]]
               [:section {:id "content"}
                [:div {:class "inner"}
                 [:div {:class "replace-me"}]]] ; workaround to avoid parsing HTML
               [:footer {:id "footer"}
                [:div {:class "inner"}
                 [:a {:href "http://github.com/loganmhb/flatland"}
                     "This blog was created with Flatland."]]]]])))
