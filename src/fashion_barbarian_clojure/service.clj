(ns fashion-barbarian-clojure.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [ring.util.response :as ring-resp]
            [clj-http.client :as client]
            [clojure.data.json :as json]
            [clojure.walk :as walk]))

(def shopstyle-base-url
  "http://api.shopstyle.com/api/v2/products")

(def trendy-keywords
  ["crochet dress", "neon yellow", "polka dot blouse", "leather skirt", "mini bag"])

(def category
  "womens-clothes")

(def products-limit
  50)

(def price-categories
  ["p7" "p8"])

(defn trendy-keyword
  [trendy-keywords]
  (rand-nth trendy-keywords))

(defn shopstyle-request
  [trendy-keyword]
  (client/get shopstyle-base-url
    {:query-params {  :pid (System/getenv "SHOPSTYLE_API_KEY"),
                      :fts (trendy-keyword trendy-keywords),
                      :cat category,
                      :offset (rand-int 30),
                      :limit products-limit,
                      :fl price-categories}}))

(defn trendy-products
  [shopstyle-request]
    (let [response (json/read-str (get-in shopstyle-request [:body]))]
      (let [products (walk/keywordize-keys (get-in response ["products"]))]
        (map (fn [product]
          (select-keys product [:brandedName :clickUrl :priceLabel :salePriceLabel :image])) products))))

(defn index
  [request]
  (http/json-response
    {:products (trendy-products (shopstyle-request trendy-keyword))}))

(def common-interceptors [(body-params/body-params) http/html-body])

(def routes #{["/" :get (conj common-interceptors `index)]})

;; Consumed by fashion-barbarian-clojure.server/create-server
;; See http/default-interceptors for additional options you can configure
(def service {:env :prod
              ;; You can bring your own non-default interceptors. Make
              ;; sure you include routing and set it up right for
              ;; dev-mode. If you do, many other keys for configuring
              ;; default interceptors will be ignored.
              ;; ::http/interceptors []
              ::http/routes routes

              ;; Uncomment next line to enable CORS support, add
              ;; string(s) specifying scheme, host and port for
              ;; allowed source(s):
              ;;
              ; "http://localhost:8080"
              ;;
              ::http/allowed-origins ["http://alexshook.com" "https://alexshook.github.io" "null"]

              ;; Root for resource interceptor that is available by default.
              ::http/resource-path "/public"

              ;; Either :jetty, :immutant or :tomcat (see comments in project.clj)
              ::http/type :jetty
              ;;::http/host "localhost"
              ::http/port (Integer/parseInt (get (System/getenv) "PORT" "8080"))
              ;; Options to pass to the container (Jetty)
              ::http/container-options {:h2c? true
                                        :h2? false
                                        ;:keystore "test/hp/keystore.jks"
                                        ;:key-password "password"
                                        ;:ssl-port 8443
                                        :ssl? false}})

