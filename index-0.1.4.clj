{:namespaces
 ({:doc "Reading and writing comma separated values.",
   :author "Jonas Enlund",
   :name "clojure.data.csv",
   :wiki-url "http://clojure.github.io/data.csv/index.html",
   :source-url
   "https://github.com/clojure/data.csv/blob/2a4b8a365b1a3dabe4c9b5455c2b33523fcef559/src/main/clojure/clojure/data/csv.clj"}),
 :vars
 ({:raw-source-url
   "https://github.com/clojure/data.csv/raw/2a4b8a365b1a3dabe4c9b5455c2b33523fcef559/src/main/clojure/clojure/data/csv.clj",
   :name "read-csv",
   :file "src/main/clojure/clojure/data/csv.clj",
   :source-url
   "https://github.com/clojure/data.csv/blob/2a4b8a365b1a3dabe4c9b5455c2b33523fcef559/src/main/clojure/clojure/data/csv.clj#L87",
   :line 87,
   :var-type "function",
   :arglists ([input & options]),
   :doc
   "Reads CSV-data from input (String or java.io.Reader) into a lazy\nsequence of vectors.\n\n Valid options are\n   :separator (default \\,)\n   :quote (default \\\")",
   :namespace "clojure.data.csv",
   :wiki-url
   "http://clojure.github.io/data.csv//index.html#clojure.data.csv/read-csv"}
  {:raw-source-url
   "https://github.com/clojure/data.csv/raw/2a4b8a365b1a3dabe4c9b5455c2b33523fcef559/src/main/clojure/clojure/data/csv.clj",
   :name "write-csv",
   :file "src/main/clojure/clojure/data/csv.clj",
   :source-url
   "https://github.com/clojure/data.csv/blob/2a4b8a365b1a3dabe4c9b5455c2b33523fcef559/src/main/clojure/clojure/data/csv.clj#L127",
   :line 127,
   :var-type "function",
   :arglists ([writer data & options]),
   :doc
   "Writes data to writer in CSV-format.\n\nValid options are\n  :separator (Default \\,)\n  :quote (Default \\\")\n  :quote? (A predicate function which determines if a string should be quoted. Defaults to quoting only when necessary.)\n  :newline (:lf (default) or :cr+lf)",
   :namespace "clojure.data.csv",
   :wiki-url
   "http://clojure.github.io/data.csv//index.html#clojure.data.csv/write-csv"})}
