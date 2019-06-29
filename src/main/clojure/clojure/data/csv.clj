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
  (:import (java.io PushbackReader Reader Writer StringReader EOFException BufferedReader)
           (clojure.lang IReduceInit)))

;(set! *warn-on-reflection* true)

;; Reading

(def ^{:private true} lf  (int \newline))
(def ^{:private true} cr  (int \return))
(def ^{:private true} eof -1)

(defn- read-quoted-cell [^PushbackReader reader ^StringBuilder sb sep quote]
  (loop [ch (.read reader)]
    (condp == ch
      quote (let [next-ch (.read reader)]
              (condp == next-ch
                quote (do (.append sb (char quote))
                          (recur (.read reader)))
                sep :sep
                lf  :eol
                cr  (let [next-next-ch (.read reader)]
                      (when (not= next-next-ch lf)
                        (.unread reader next-next-ch))
                      :eol)
                eof :eof
                (throw (Exception. ^String (format "CSV error (unexpected character: %c)" next-ch)))))
      eof (throw (EOFException. "CSV error (unexpected end of file)"))
      (do (.append sb (char ch))
          (recur (.read reader))))))

(defn- read-cell [^PushbackReader reader ^StringBuilder sb sep quote]
  (let [first-ch (.read reader)]
    (if (== first-ch quote)
      (read-quoted-cell reader sb sep quote)
      (loop [ch first-ch]
        (condp == ch
          sep :sep
          lf  :eol
          cr (let [next-ch (.read reader)]
               (when (not= next-ch lf)
                 (.unread reader next-ch))
               :eol)
          eof :eof
          (do (.append sb (char ch))
              (recur (.read reader))))))))

(defn- read-record [reader sep quote]
  (loop [record (transient [])]
    (let [cell (StringBuilder.)
          sentinel (read-cell reader cell sep quote)]
      (if (= sentinel :sep)
        (recur (conj! record (str cell)))
        [(persistent! (conj! record (str cell))) sentinel]))))

(defprotocol Read-CSV-From
  (read-csv-from [input sep quote]))

(extend-protocol Read-CSV-From
  String
  (read-csv-from [s sep quote]
    (read-csv-from (PushbackReader. (StringReader. s)) sep quote))
  
  Reader
  (read-csv-from [reader sep quote]
    (read-csv-from (PushbackReader. reader) sep quote))
  
  PushbackReader
  (read-csv-from [reader sep quote]
    (lazy-seq
     (let [[record sentinel] (read-record reader sep quote)]
       (case sentinel
	 :eol (cons record (read-csv-from reader sep quote))
	 :eof (when-not (= record [""])
		(cons record nil))))))

  IReduceInit ;; can't deal with newlines in quoted cells down this path :(
  (read-csv-from [lines sep quote]
    (let [qstr (str (char quote))
          sep-str (str (char sep))]
      (eduction ;; allow the caller to reduce/transduce however he wants
        (map (fn [^String line]
               (if (.contains line qstr) ;; contains quoted cell
                 (-> line
                     StringReader.
                     PushbackReader.
                     (read-record sep quote)
                     first)
                 (vec (.split line sep-str)))))
        lines)))
  )

(defn read-csv
  "Reads CSV-data from input (String, java.io.Reader or something reducible)
   into a sequence of vectors. For non-reducible inputs the sequence will be
   lazily computed, whereas for reducible ones it will be transducer-friendly
   (returns an `eduction`).

   ATTENTION: newlines in quoted cells of reducible inputs are NOT supported!

   Valid options are
     :separator (default \\,)
     :quote (default \\\")"
  [input & options]
  (let [{:keys [separator quote]
         :or {separator \,
              quote \"}} options]
    (read-csv-from input (int separator) (int quote))))


;; Writing

(defn- write-cell [^Writer writer obj sep quote quote?]
  (let [string (str obj)
	must-quote (quote? string)]
    (when must-quote (.write writer (int quote)))
    (.write writer (if must-quote
		     (str/escape string
				 {quote (str quote quote)})
		     string))
    (when must-quote (.write writer (int quote)))))

(defn- write-record [^Writer writer record sep quote quote?]
  (loop [record record]
    (when-first [cell record]
      (write-cell writer cell sep quote quote?)
      (when-let [more (next record)]
	(.write writer (int sep))
	(recur more)))))

(defn- write-csv*
  [^Writer writer records sep quote quote? ^String newline]
  (loop [records records]
    (when-first [record records]
      (write-record writer record sep quote quote?)
      (.write writer newline)
      (recur (next records)))))

(defn write-csv
  "Writes data to writer in CSV-format.

   Valid options are
     :separator (Default \\,)
     :quote (Default \\\")
     :quote? (A predicate function which determines if a string should be quoted. Defaults to quoting only when necessary.)
     :newline (:lf (default) or :cr+lf)"
  [writer data & options]
  (let [opts (apply hash-map options)
        separator (or (:separator opts) \,)
        quote (or (:quote opts) \")
        quote? (or (:quote? opts) #(some #{separator quote \return \newline} %))
        newline (or (:newline opts) :lf)]
    (write-csv* writer
		data
		separator
		quote
                quote?
		({:lf "\n" :cr+lf "\r\n"} newline))))

(defn lines-reducible
  "A reducible alternative to `line-seq`."
  [^BufferedReader rdr]
  (reify IReduceInit
    (reduce [this f init]
      (with-open [r rdr]
        (loop [state init]
          (if (reduced? state)
            @state
            (if-let [line (.readLine r)]
              (recur (f state line))
              state)))))))

(defn- row->map
  [header row]
  (zipmap header row))

(defn rows->maps
  "Assumes first row is the header.
   To be used against lazy-seqs."
  ([csv-data]
   (rows->maps csv-data true))
  ([csv-data kw?]
   (map
     (partial row->map (cond->> (first csv-data)
                                kw? (map keyword)))
     (rest csv-data))))

(defn rows->maps-xform
  "Transducer version of `rows->maps`.
   Assumes no headers in the actual data,
   hence expected as the first argument."
  ([headers]
   (rows->maps-xform headers true))
  ([headers kw?]
   (map
     (partial row->map
              (cond->> headers
                       kw? (map keyword))))))