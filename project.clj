;; NOTE: This project.clj file exists purely to make it easier to
;; develop and test data.csv locally. The pom.xml file is the
;; "system of record" as far as the project version is concerned.

(defproject org.clojure/data.csv "0.1.4-SNAPSHOT"
  :description "A Clojure library for reading and writing comma separated value (csv) files"
  :parent [org.clojure/pom.contrib "0.1.2"]
  :url "https://github.com/clojure/data.csv"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :source-paths ["src/main/clojure"]
  :test-paths ["src/test/clojure"]
  :dependencies [[org.clojure/clojure "1.8.0"]]
  :min-lein-version "2.0.0")

