(ns flatland.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.adapter.jetty :as jetty]
            [environ.core :refer [env]]
            [flatland.render :refer :all]
            [flatland.templates :refer [posts pages template]]))

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

(defn -main
  []
  (jetty/run-jetty app {:port (or (Integer/parseInt (env :port))
                                  5000)}))
