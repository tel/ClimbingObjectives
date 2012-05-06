(ns bio.core
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [ring.adapter.jetty :as jetty]
            [clatrix.core :as c]
            ring.middleware.keyword-params
            ring.middleware.nested-params
            ring.middleware.params
            ring.middleware.session
            [cemerick.drawbridge :as drawbridge]
            [bio.nn :as nn])
  (:import [java.util Date]))


(defn trainer [vals]
  [(into-array Double/TYPE (rest vals)) (first vals)])

(defn xs-only [vals]
  (into-array Double/TYPE (rest vals)))

(defn ys-only [vals]
  (first vals))

(defn tester [vals]
  (into-array Double/TYPE vals))

(defn read-data [path f]
  (with-open [in-file (io/reader path)]
    (reverse
     (reduce
      (fn [data line]
        (let [vals (map #(Double/parseDouble %) line)]
          (cons (f vals) data)))
      []
      (csv/read-csv in-file)))))

(defonce train-dat (read-data "data/trainPC.csv" trainer))
(defonce test-dat  (read-data "data/testPC.csv" tester))
(defonce t0        (Date.))
(defonce net (atom (nn/init-network (alength (first (first train-dat)))
                                    75)))

(defonce trainer-thread (nn/train-on-atom train-dat net))

(def drawbridge-handler
  (-> (cemerick.drawbridge/ring-handler)
      (ring.middleware.keyword-params/wrap-keyword-params)
      (ring.middleware.nested-params/wrap-nested-params)
      (ring.middleware.params/wrap-params)
      (ring.middleware.session/wrap-session)))

(defn wrap-drawbridge [handler]
  (fn [req]
    (if (= "/repl" (:uri req))
      (drawbridge-handler req)
      (handler req))))

(defn app [req]
  {:status 200
   :headers {"Content-Type" "application/html"}
   :body "Hello world"})

(defn -main [& [port]]
  (let [port (Integer. (or port (System/getenv "PORT")))]
    (jetty/run-jetty drawbridge-handler
                     {:port port})))