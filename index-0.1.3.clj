{:namespaces
 ({:source-url
   "https://github.com/clojure/data.csv/blob/b70b33d56c239972f3e1c53c3c4f1b786909e93f/src/main/clojure/clojure/data/csv.clj",
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
   "https://github.com/clojure/data.csv/blob/b70b33d56c239972f3e1c53c3c4f1b786909e93f/src/main/clojure/clojure/data/csv.clj#L83",
   :raw-source-url
   "https://github.com/clojure/data.csv/raw/b70b33d56c239972f3e1c53c3c4f1b786909e93f/src/main/clojure/clojure/data/csv.clj",
   :wiki-url
   "http://clojure.github.com/data.csv//clojure.data.csv-api.html#clojure.data.csv/read-csv",
   :doc
   "Reads CSV-data from input (String or java.io.Reader) into a lazy\nsequence of vectors.\n\n Valid options are\n   :separator (default \\,)\n   :quote (default \\\")",
   :var-type "function",
   :line 83,
   :file "src/main/clojure/clojure/data/csv.clj"}
  {:arglists ([writer data & options]),
   :name "write-csv",
   :namespace "clojure.data.csv",
   :source-url
   "https://github.com/clojure/data.csv/blob/b70b33d56c239972f3e1c53c3c4f1b786909e93f/src/main/clojure/clojure/data/csv.clj#L123",
   :raw-source-url
   "https://github.com/clojure/data.csv/raw/b70b33d56c239972f3e1c53c3c4f1b786909e93f/src/main/clojure/clojure/data/csv.clj",
   :wiki-url
   "http://clojure.github.com/data.csv//clojure.data.csv-api.html#clojure.data.csv/write-csv",
   :doc
   "Writes data to writer in CSV-format.\n\nValid options are\n  :separator (Default \\,)\n  :quote (Default \\\")\n  :quote? (A predicate function which determines if a string should be quoted. Defaults to quoting only when necessary.)\n  :newline (:lf (default) or :cr+lf)",
   :var-type "function",
   :line 123,
   :file "src/main/clojure/clojure/data/csv.clj"})}
