(defproject flatland "0.1.0-SNAPSHOT"
  :plugins [[lein-ring "0.8.10"]]
  :ring {:handler flatland.handler/app
         :nrepl {:start? true
                 :port 9998}}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]}}
  :description "Flat-file personal blogging CMS."
  :url "http://blog.loganbuckley.com"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ring "1.3.2"]
                 [compojure "1.1.6"]
                 [hiccup "1.0.5"]
                 [markdown-clj "0.9.61" :exclusions [org.clojure/clojure]]
                 [environ "0.5.0"]]
  :main flatland.handler)
