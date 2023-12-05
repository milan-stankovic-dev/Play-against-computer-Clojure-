(defproject seminarski-rad "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [seancorfield/next.jdbc "1.2.659"]
                 [mysql/mysql-connector-java "5.1.6"]
                 [buddy/buddy-hashers "2.0.167"]
                 [org.clojure/tools.reader "1.3.7"]
                 [jline "0.9.94"]]
  :main ^:skip-aot seminarski-rad.core
  ;; :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}}
)
