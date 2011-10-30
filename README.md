# data.csv

`data.csv` is a Clojure library for reading and writing comma
separated value (csv) files. It is licensed under the [Eclipse open
source license](http://www.opensource.org/licenses/eclipse-1.0.php).
The library has been tested on Clojure versions 1.2, 1.2.1 and
1.3. API documentation is available at
[http://clojure.github.com/data.csv](http://clojure.github.com/data.csv).
Please use [JIRA](http://dev.clojure.org/jira/browse/DCSV) for bug
reports instead of the GitHub bug tracker. If you want to contribute
code to the project make sure you have signed the [Contributer
Agreement](http://clojure.org/contributing).

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

