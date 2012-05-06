(defproject bio "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :jvm-opts ["-Xmx1g" "-server"]
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [clatrix/clatrix "0.1.0"]
                 [ring "1.1.0"]
                 [com.cemerick/drawbridge "0.0.3"]
                 [org.clojure/data.csv "0.1.2"]]
  :profiles {:dev {:dependencies [[vimclojure/server "2.3.1"]]}})
