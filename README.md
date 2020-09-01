clojure.data.csv
========================================

[*API documentation*](http://clojure.github.com/data.csv/)

CSV reader/writer to/from Clojure data structures.

Follows the [RFC4180](http://tools.ietf.org/html/rfc4180) specification but is more relaxed.



Releases and Dependency Information
========================================

This project follows the version scheme MAJOR.MINOR.PATCH where each component provides some relative indication of the size of the change, but does not follow semantic versioning. In general, all changes endeavor to be non-breaking (by moving to new names rather than by breaking existing names).

Latest stable release: 1.0.0

* [All Released Versions](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.clojure%22%20AND%20a%3A%22data.csv%22)

* [Development Snapshot Versions](https://oss.sonatype.org/index.html#nexus-search;gav~org.clojure~data.csv~~~)

[CLI/`deps.edn`](https://clojure.org/reference/deps_and_cli) dependency information:
```clojure
org.clojure/data.csv {:mvn/version "1.0.0"}
```

[Leiningen](https://github.com/technomancy/leiningen) dependency information:

    [org.clojure/data.csv "1.0.0"]

[Maven](http://maven.apache.org/) dependency information:

    <dependency>
      <groupId>org.clojure</groupId>
      <artifactId>data.csv</artifactId>
      <version>1.0.0</version>
    </dependency>



Example Usage
========================================

```clojure
(require '[clojure.data.csv :as csv]
         '[clojure.java.io :as io])

(with-open [reader (io/reader "in-file.csv")]
  (doall
    (csv/read-csv reader)))

(with-open [writer (io/writer "out-file.csv")]
  (csv/write-csv writer
                 [["abc" "def"]
                  ["ghi" "jkl"]]))
```

Refer to the [API documentation](http://clojure.github.com/data.csv/)
for additional information.

## Working with data.csv

This library is meant to remain small and focus on nothing but correctly parsing
csv files. The following sections describes how to effectively use data.csv as a
building block in larger programs as well as some hints on how to solve common
problems.

### Laziness

When parsing a csv file with data.csv the result is a lazy sequence of vectors
of strings. With some care, laziness makes it possible to process very large csv
files without excessive memory use. Here's an example of a program that copies
one csv file to another but drops the first and last columns:

```clojure
(defn copy-csv [from to]
  (with-open [reader (io/reader from)
              writer (io/writer to)]
    (->> (read-csv reader)
         (map #(rest (butlast %)))
         (write-csv writer))))
```

This function will work even if the csv file is larger than would fit in memory
because all the steps are lazy.

There are a few things to look out for when dealing with lazy
sequences. Espacially with data.csv where the sequence is often created via a
`clojure.java.io/reader` that could already be closed when the lazy sequence is
consumed. For example

```clojure
(defn read-column [filename column-index]
  (with-open [reader (io/reader filename)]
    (let [data (read-csv reader)]
      (map #(nth % column-index) data))))

(defn sum-second-column [filename]
  (->> (read-column filename 1)
       (drop 1) ;; Drop header column
       (map #(Double/parseDouble %))
       (reduce + 0)))
```

This program will throw the exception "`java.io.Exception`: Stream Closed". The
reason is that both `read-csv` and `map` are lazy, so `read-column` will
immeditaly return a sequence without actually reading any bytes from the
file. The reading (and parsing) will happen when data is needed by the calling
code (`reduce` in this case). By the time `reduce` tries to add the first value
`with-open` will already have closed the `io/reader` and the exception is
thrown.

There are two solutions to this problem:

1. Move the opening/closing of the reader up the callstack to the point where
   the content is consumed:

```clojure
(defn read-column [reader column-index]
  (let [data (read-csv reader)]
    (map #(nth % column-index) data)))

(defn sum-second-column [filename]
  (with-open [reader (io/reader filename)]
    (->> (read-column reader 1)
         (drop 1)
         (map #(Double/parseDouble %))
         (reduce + 0))))
```

2. Don't return a lazy sequence

```clojure
(defn read-column [filename column-index]
  (with-open [reader (io/reader filename)]
    (let [data (read-csv reader)]
      ;; mapv is not lazy, so the csv data will be consumed at this point
      (mapv #(nth % column-index) data))))

(defn sum-second-column [filename]
  (->> (read-column filename 1)
       (drop 1)
       (map #(Double/parseDouble %))
       (reduce + 0)))
```

Which approach to choose depends on the application. If the csv file isn't huge
the second approach will often work well. If you want to be careful not to read
the csv file into memory the first approach is preferable.

### Parsing into maps

Data.csv parses lines of a csv file into a vector of strings. This is often not
the desired output where you might want the result to be a sequence of maps
instead, such as

```text
foo,bar,baz
A,1,x
B,2,y
C,3,z
```

```clojure
({:foo "A"
  :bar "2"
  :baz "x"}
 {:foo "B"
  :bar "2"
  :baz "y"}
 {:foo "C"
  :bar "3"
  :baz "z"})
```

One fairly elegant way to achieve this is the expression

```clojure
(defn csv-data->maps [csv-data]
  (map zipmap
       (->> (first csv-data) ;; First row is the header
            (map keyword) ;; Drop if you want string keys instead
            repeat)
	  (rest csv-data)))

(csv-data->maps (read-csv reader))
```

This function is lazy so all the options described in the previous section is
still valid.  Now that the data is in a nice format it's easy to do any desired
post-processing:

```clojure
(->> (read-csv reader)
     csv-data->maps
     (map (fn [csv-record]
            (update csv-record :bar #(Long/parseLong %)))))

({:foo "A"
  :bar 1
  :baz "x"}
 {:foo "B"
  :bar 2
  :baz "y"}
 {:foo "C"
  :bar 3
  :baz "z"})
```

### Byte Order Mark

A [byte order mark (BOM)](https://en.wikipedia.org/wiki/Byte_order_mark) is a
byte sequence that appears as the first couple of bytes in some CSV files (and
other text files). Data.csv will not automatically remove these extra bytes so
they can accidentally be interpreted as part of the first cells characters. If
you want to avoid this you can either try to manually detect it by looking at
the first byte(s) and calling `(.skip reader 1)` before you pass the reader to
read-csv.

Another option is to create the reader in such a way that the BOM will be
automatically removed. One way to achieve this is to use
[`org.apache.commons.io.input/BOMInputStream`](https://commons.apache.org/proper/commons-io/javadocs/api-release/org/apache/commons/io/input/BOMInputStream.html):

```clojure
(with-open [reader (-> "data.csv"
                       io/input-stream
                       BOMInputStream.
                       io/reader)]
  (doall (read-csv reader)))
```



Developer Information
========================================

* [GitHub project](https://github.com/clojure/data.csv)

* [Bug Tracker](http://dev.clojure.org/jira/browse/DCSV)

* [Continuous Integration](http://build.clojure.org/job/data.csv/)

* [Compatibility Test Matrix](http://build.clojure.org/job/data.csv-test-matrix/)



Change Log
====================

* Release 1.0.0 on 2020-02-18

* Release 0.1.4 on 2017-04-05
  * [DCSV-16](https://dev.clojure.org/jira/browse/DCSV-16) Resolve some reflection warnings

* Release 0.1.3 on 2015-08-10
  * [DCSV-4](http://dev.clojure.org/jira/browse/DCSV-4) Allow carriage
    return by itself as a record separator

* Release 0.1.2 on 2012-02-24
  * Fixed keyword params for `write-csv`

* Release 0.1.1 on 2012-02-14
  * Added quote? keyword param to write-csv
  * Code cleanup

* Release 0.1.0 on 2011-08-26
  * Initial release.



Copyright and License
========================================

Copyright (c) Jonas Enlund, Rich Hickey, and contributors, 2012-2020. 
All rights reserved.  The use and
distribution terms for this software are covered by the Eclipse Public
License 1.0 (http://opensource.org/licenses/eclipse-1.0.php) which can
be found in the file epl-v10.html at the root of this distribution.
By using this software in any fashion, you are agreeing to be bound by
the terms of this license.  You must not remove this notice, or any
other, from this software.
