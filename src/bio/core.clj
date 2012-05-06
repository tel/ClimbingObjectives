(ns bio.core
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [ring.adapter.jetty :as jetty]
            ring.middleware.keyword-params
            ring.middleware.nested-params
            ring.middleware.params
            ring.middleware.session
            [cemerick.drawbridge :as drawbridge]
            [bio.nn :as nn])
  (:import [java.util Date]))


(defn trainer [vals]
  [(into-array Double/TYPE (rest vals)) (first vals)])

(defn tester [vals]
  (into-array Double/TYPE (rest vals)))

(defn read-data [path f]
  (with-open [in-file (io/reader path)]
    (reverse
     (reduce
      (fn [data line]
        (let [vals (map #(Double/parseDouble %) line)]
          (cons (f vals) data)))
      []
      (csv/read-csv in-file)))))

(defonce train-dat (read-data "data/train.csv" trainer))
(defonce test-dat  (read-data "data/test.csv" tester))

(defn app [req]
  {:status 200
   :headers {"Content-Type" "application/html"}
   :body "Hello world"})

(defn -main [& [port]]
  (let [port (Integer. (or port (System/getenv "PORT")))]
    (jetty/run-jetty (-> #'app
                         drawbridge/ring-handler
                         ring.middleware.keyword-params/wrap-keyword-params
                         ring.middleware.nested-params/wrap-nested-params
                         ring.middleware.params/wrap-params
                         ring.middleware.session/wrap-session)
                     {:port port})))