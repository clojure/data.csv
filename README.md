# data.csv

`data.csv` is a Clojure library for reading and writing comma
separated value (csv) files. It is licensed under the [Eclipse open
source license](http://www.opensource.org/licenses/eclipse-1.0.php).
The library has been tested on Clojure versions 1.2, 1.2.1 and
1.3.

## Installation

### Using maven

Add 

    <dependency>
      <groupId>org.clojure</groupId>
      <artifactId>data.csv</artifactId>
      <version>0.1.0</version>
    </dependency>

to your `pom.xml` file.

### Using leiningen

Add `[org.clojure/data.csv "0.1.0"]` as a dependency to your `project.clj`.

## Usage

    (require '[clojure.data.csv :as csv]
             '[clojure.java.io :as io])

    (with-open [in-file (io/reader "in-file.csv")]
      (doall
        (csv/read-csv in-file)))

    (with-open [out-file (io/writer "out-file.csv")]
      (csv/write-csv out-file
                     [["abc" "def"]
                      ["ghi" "jkl"]]))

## Features

### Reading

`data.csv` supports [RFC 4180](http://tools.ietf.org/html/rfc4180).
Additionally, it is possible to choose separator and quote
characters. Reading is *fast* and *lazy*. See `(doc read-csv)` for
available options.

### Writing

See `(doc write-csv)` for available options.
