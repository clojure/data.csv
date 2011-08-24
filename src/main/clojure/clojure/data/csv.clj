;; Copyright (c) Jonas Enlund. All rights reserved.  The use and
;; distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file epl-v10.html at the root of this
;; distribution.  By using this software in any fashion, you are
;; agreeing to be bound by the terms of this license.  You must not
;; remove this notice, or any other, from this software.

(ns ^{:author "Jonas Enlund"
      :doc "Reading and writing comma separated values."}
  clojure.data.csv
  (:require (clojure [string :as str]))
  (:import (java.io Reader Writer StringReader EOFException)))

;(set! *warn-on-reflection* true)

;; Reading

(def ^{:private true} lf  (int \newline))
(def ^{:private true} cr  (int \return))
(def ^{:private true} eof -1)

(defn- read-quoted-cell [^Reader reader sep quote]
  (let [sb (StringBuilder.)]
    (loop [ch (.read reader)]
      (condp == ch
	  quote (let [next-ch (.read reader)]
		  (condp == next-ch
		      quote (do (.append sb (char quote))
				(recur (.read reader)))
		      sep [(str sb) :sep]
		      lf  [(str sb) :eol]
		      cr  (if (== (.read reader) lf)
			    [(str sb) :eol]
			    (throw (Exception. ^String (format "CSV error (unexpected character: %c)" next-ch))))
		      eof [(str sb) :eof]
		      (throw (Exception. ^String (format "CSV error (unexpected character: %c)" next-ch)))))
	  eof (throw (EOFException. "CSV error (unexpected end of file)"))
	  (do (.append sb (char ch))
	      (recur (.read reader)))))))
	   
(defn- read-cell [^Reader reader sep quote]
  (let [sb (StringBuilder.)]
    (let [first-ch (.read reader)]
      (if (== first-ch quote)
	(read-quoted-cell reader sep quote)
	(loop [ch first-ch]
	  (condp == ch
	      sep [(str sb) :sep]
	      lf  [(str sb) :eol]
	      cr (let [next-ch (.read reader)]
		   (if (== next-ch lf)
		     [(str sb) :eol]
		     (do (.append sb \return)
			 (recur next-ch))))
	      eof [(str sb) :eof]
	      (do (.append sb (char ch))
		  (recur (.read reader)))))))))

(defn- read-record [reader sep quote]
  (loop [record (transient [])]
    (let [[cell sentinel] (read-cell reader sep quote)]
      (case sentinel
	:sep (recur (conj! record cell))
	[(persistent! (conj! record cell)) sentinel]))))

(defprotocol Read-CSV-From
  (read-csv-from [input sep quote]))

(extend-protocol Read-CSV-From
  String
  (read-csv-from [s sep quote]
    (read-csv-from (StringReader. s) sep quote))
  
  Reader
  (read-csv-from [reader sep quote] 
    (lazy-seq
     (let [[record sentinel] (read-record reader sep quote)]
       (case sentinel
	 :eol (cons record (read-csv-from reader sep quote))
	 :eof (when-not (= record [""])
		(cons record nil)))))))

(defn read-csv
  "Reads CSV-data from input (String or java.io.Reader) into a lazy
  sequence of vectors.

   Valid options are
     :separator (default \\,)
     :quote (default \\\")"
  [input & options]
  (let [{:keys [separator quote] :or {separator \, quote \"}} options]
    (read-csv-from input (int separator) (int quote))))



;; Writing

(defn- write-cell [^Writer writer obj sep quote]
  (let [string (str obj)
	must-quote (some #{sep quote \newline} string)]
    (when must-quote (.write writer (int quote)))
    (.write writer (if must-quote
		     (str/escape string
				 {quote (str quote quote)})
		     string))
    (when must-quote (.write writer (int quote)))))

(defn- write-record [^Writer writer record sep quote]
  (loop [record record]
    (when-first [cell record]
      (write-cell writer cell sep quote)
      (when-let [more (next record)]
	(.write writer (int sep))
	(recur more)))))

(defn- write-csv*
  [^Writer writer records sep quote ^String newline]
  (loop [records records]
    (when-first [record records]
      (write-record writer record sep quote)
      (.write writer newline)
      (recur (next records)))))

(defn write-csv
  "Writes data to writer in CSV-format.

   Valid options are
     :separator (default \\,)
     :quote (default \\\")
     :newline (:lf (default) or :cr+lf)"
  [writer data & options]
  (let [{:keys [separator quote newline] :or {separator \, quote \" newline :lf}} options]
    (write-csv* writer
		data
		separator
		quote
		({:lf "\n" :cr+lf "\r\n"} newline))))
