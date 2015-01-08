(ns flatland.handler-test
  (:require [clojure.test :refer :all]
            [flatland.handler :refer :all]
            [ring.mock.request :refer :all]))

(deftest index-test
  (testing "The homepage has navbar, title, and post."
    (let [response (app (request :get "/"))]
      (is (re-find #"<ul class=\"nav\">" (:body response)))
      (is (re-find #"<title>" (:body response)))
      (is (not (re-find #"<div class=\"replace-me\"></div" (:body response)))))))

(deftest pages-test
  (testing "Pages in resources/pages are rendered."
    (let [response (app (request :get "/about"))]
      (is (= (:status response 200))))))

(deftest posts-test
  (testing "Markdown conversion results in HTML."
    (let [body (:body (app (request :get "/posts/sample_post")))]
      (is (re-find #"<h1>" body))
      (is (re-find #"<em>" body))
      (is (re-find #"<strong>" body))
      (is (re-find #"an amazing post" body)))))

(deftest templates-test
  (testing "that the templates render a full HTML page."
    (let [body (:body (app (request :get "/posts/sample_post")))]
      (is (re-find #"<html>" body)))))

(deftest resilient-urls
  (testing "that garbage urls return 404."
    (let [response (app (request :get "/total/garbage"))]
      (is (= (:status response) 404)))
    (let [response (app (request :get "posts/garbage"))]
      (is (= (:status response) 404)))))


