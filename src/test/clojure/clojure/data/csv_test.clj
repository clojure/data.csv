(ns clojure.data.csv-test
  (:use
    [clojure.java.io :only (reader)]
    [clojure.test :only (deftest is testing)]
    [clojure.data.csv :only (read-csv write-csv lines-reducible rows->maps rows->maps-xform)])
  (:require [clojure.string :as str])
  (:import
    [java.io Reader StringReader StringWriter EOFException]))

(def ^{:private true} simple
  "Year,Make,Model
1997,Ford,E350
2000,Mercury,Cougar
")

(def ^{:private true} simple-alt-sep
  "Year;Make;Model
1997;Ford;E350
2000;Mercury;Cougar
")

(def ^{:private true} complicated
  "1997,Ford,E350,\"ac, abs, moon\",3000.00
1999,Chevy,\"Venture \"\"Extended Edition\"\"\",\"\",4900.00
1999,Chevy,\"Venture \"\"Extended Edition, Very Large\"\"\",\"\",5000.00
1996,Jeep,Grand Cherokee,\"MUST SELL!
air, moon roof, loaded\",4799.00")

(def ^{:private true} complicated-no-newlines ;; for testing reducible input
  "1997,Ford,E350,\"ac, abs, moon\",3000.00
1999,Chevy,\"Venture \"\"Extended Edition\"\"\",\"\",4900.00
1999,Chevy,\"Venture \"\"Extended Edition, Very Large\"\"\",\"\",5000.00
1996,Jeep,Grand Cherokee,\"MUST SELL!,air, moon roof, loaded\",4799.00")

(deftest reading
  (time
    (do
      (let [csv (read-csv simple)]
        (is (= (count csv) 3))
        (is (= (count (first csv)) 3))
        (is (= (first csv) ["Year" "Make" "Model"]))
        (is (= (last csv) ["2000" "Mercury" "Cougar"])))
      (let [csv (read-csv simple-alt-sep :separator \;)]
        (is (= (count csv) 3))
        (is (= (count (first csv)) 3))
        (is (= (first csv) ["Year" "Make" "Model"]))
        (is (= (last csv) ["2000" "Mercury" "Cougar"])))
      (let [csv (read-csv complicated)]
        (is (= (count csv) 4))
        (is (= (count (first csv)) 5))
        (is (= (first csv)
               ["1997" "Ford" "E350" "ac, abs, moon" "3000.00"]))
        (is (= (last csv)
               ["1996" "Jeep" "Grand Cherokee", "MUST SELL!\nair, moon roof, loaded" "4799.00"]))))))


(deftest reading-eager
  (time ;; 30% faster even though it does more work!
    (let [uncapitalize (partial map str/lower-case)]

      (let [lines (lines-reducible
                    (reader (StringReader. simple)))
            csv (read-csv lines :xform (map uncapitalize))]
        (is (= (count csv) 3))
        (is (= (count (first csv)) 3))
        (is (= (first csv) ["year" "make" "model"]))
        (is (= (last csv) ["2000" "mercury" "cougar"])))

      (let [lines (lines-reducible
                    (reader (StringReader. simple-alt-sep)))
            csv (read-csv lines
                          :separator \;
                          :xform (map uncapitalize))]
        (is (= (count csv) 3))
        (is (= (count (first csv)) 3))
        (is (= (first csv) ["year" "make" "model"]))
        (is (= (last csv) ["2000" "mercury" "cougar"])))

      ;; can't deal with newlines through this path :(
      (let [lines (lines-reducible
                    (reader (StringReader. complicated-no-newlines)))
            csv (read-csv lines :xform (map uncapitalize))]
        (is (= (count csv) 4))
        (is (= (count (first csv)) 5))
        (is (= (first csv)
               ["1997" "ford" "e350" "ac, abs, moon" "3000.00"]))
        (is (= (last csv)
               ["1996" "jeep" "grand cherokee", "must sell!,air, moon roof, loaded" "4799.00"])))

      ))
  )
        

(deftest reading-and-writing
  (let [string-writer (StringWriter.)]
    (->> simple read-csv (write-csv string-writer))
    (is (= simple
	   (str string-writer)))))

(deftest throw-if-quoted-on-eof
  (let [s "ab,\"de,gh\nij,kl,mn"]
    (try
      (doall (read-csv s))
      (is false "No exception thrown")
      (catch Exception e
        (is (or (instance? java.io.EOFException e)
                (and (instance? RuntimeException e)
                     (instance? java.io.EOFException (.getCause e)))))))))

(deftest parse-line-endings
  (let [csv (read-csv "Year,Make,Model\n1997,Ford,E350")]
    (is (= 2 (count csv)))
    (is (= ["Year" "Make" "Model"] (first csv)))
    (is (= ["1997" "Ford" "E350"] (second csv))))
  (let [csv (read-csv "Year,Make,Model\r\n1997,Ford,E350")]
    (is (= 2 (count csv)))
    (is (= ["Year" "Make" "Model"] (first csv)))
    (is (= ["1997" "Ford" "E350"] (second csv))))
  (let [csv (read-csv "Year,Make,Model\r1997,Ford,E350")]
    (is (= 2 (count csv)))
    (is (= ["Year" "Make" "Model"] (first csv)))
    (is (= ["1997" "Ford" "E350"] (second csv))))
  (let [csv (read-csv "Year,Make,\"Model\"\r1997,Ford,E350")]
    (is (= 2 (count csv)))
    (is (= ["Year" "Make" "Model"] (first csv)))
    (is (= ["1997" "Ford" "E350"] (second csv)))))


(deftest rows->maps-tests
  (let [expected [{:foo "A"
                   :bar "1"
                   :baz "x"}
                  {:foo "B"
                   :bar "2"
                   :baz "y"}
                  {:foo "C"
                   :bar "3"
                   :baz "z"}]]
    (testing "lazy version"
      (time
        (let [sample "foo,bar,baz\nA,1,x\nB,2,y\nC,3,z"
              csv (read-csv sample)]
          (is (= expected (rows->maps csv))))))

    (testing "eager version"
      (time
        (let [sample "A,1,x\nB,2,y\nC,3,z"
              lines (lines-reducible
                      (reader (StringReader. sample)))
              headers ["foo" "bar" "baz"]]
          (is (= expected
                 (read-csv lines :xform (rows->maps-xform headers))))))
      )
    )







  )
