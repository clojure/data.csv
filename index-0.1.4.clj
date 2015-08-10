{:namespaces
 ({:source-url
   "https://github.com/clojure/data.csv/blob/2a4b8a365b1a3dabe4c9b5455c2b33523fcef559/src/main/clojure/clojure/data/csv.clj",
   :wiki-url
   "http://clojure.github.com/data.csv/clojure.data.csv-api.html",
   :name "clojure.data.csv",
   :author "Jonas Enlund",
   :doc "Reading and writing comma separated values."}),
 :vars
 ({:arglists ([input & options]),
   :name "read-csv",
   :namespace "clojure.data.csv",
   :source-url
   "https://github.com/clojure/data.csv/blob/2a4b8a365b1a3dabe4c9b5455c2b33523fcef559/src/main/clojure/clojure/data/csv.clj#L87",
   :raw-source-url
   "https://github.com/clojure/data.csv/raw/2a4b8a365b1a3dabe4c9b5455c2b33523fcef559/src/main/clojure/clojure/data/csv.clj",
   :wiki-url
   "http://clojure.github.com/data.csv//clojure.data.csv-api.html#clojure.data.csv/read-csv",
   :doc
   "Reads CSV-data from input (String or java.io.Reader) into a lazy\nsequence of vectors.\n\n Valid options are\n   :separator (default \\,)\n   :quote (default \\\")",
   :var-type "function",
   :line 87,
   :file "src/main/clojure/clojure/data/csv.clj"}
  {:arglists ([writer data & options]),
   :name "write-csv",
   :namespace "clojure.data.csv",
   :source-url
   "https://github.com/clojure/data.csv/blob/2a4b8a365b1a3dabe4c9b5455c2b33523fcef559/src/main/clojure/clojure/data/csv.clj#L127",
   :raw-source-url
   "https://github.com/clojure/data.csv/raw/2a4b8a365b1a3dabe4c9b5455c2b33523fcef559/src/main/clojure/clojure/data/csv.clj",
   :wiki-url
   "http://clojure.github.com/data.csv//clojure.data.csv-api.html#clojure.data.csv/write-csv",
   :doc
   "Writes data to writer in CSV-format.\n\nValid options are\n  :separator (Default \\,)\n  :quote (Default \\\")\n  :quote? (A predicate function which determines if a string should be quoted. Defaults to quoting only when necessary.)\n  :newline (:lf (default) or :cr+lf)",
   :var-type "function",
   :line 127,
   :file "src/main/clojure/clojure/data/csv.clj"})}
