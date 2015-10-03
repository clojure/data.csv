clojure.data.csv
========================================

CSV reader/writer to/from Clojure data structures.

Follows the [RFC4180](http://tools.ietf.org/html/rfc4180) specification but is more relaxed.



Releases and Dependency Information
========================================

Latest stable release: 0.1.3

* [All Released Versions](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.clojure%22%20AND%20a%3A%22data.csv%22)

* [Development Snapshot Versions](https://oss.sonatype.org/index.html#nexus-search;gav~org.clojure~data.csv~~~)

[Leiningen](https://github.com/technomancy/leiningen) dependency information:

```clj
[org.clojure/data.csv "0.1.3"]
```

[Maven](http://maven.apache.org/) dependency information:

```xml
<dependency>
  <groupId>org.clojure</groupId>
  <artifactId>data.csv</artifactId>
  <version>0.1.3</version>
</dependency>
```



Example Usage
========================================

```clj
(require '[clojure.data.csv :as csv]
         '[clojure.java.io :as io])

(with-open [in-file (io/reader "in-file.csv")]
  (doall
    (csv/read-csv in-file)))

(with-open [out-file (io/writer "out-file.csv")]
  (csv/write-csv out-file
                 [["abc" "def"]
                  ["ghi" "jkl"]]))
```

Refer to the [API documentation](http://clojure.github.com/data.csv/)
for additional information.


Developer Information
========================================

* [GitHub project](https://github.com/clojure/data.csv)

* [Bug Tracker](http://dev.clojure.org/jira/browse/DCSV)

* [Continuous Integration](http://build.clojure.org/job/data.csv/)

* [Compatibility Test Matrix](http://build.clojure.org/job/data.csv-test-matrix/)



Change Log
====================

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

Copyright (c) Jonas Enlund, 2012. All rights reserved.  The use and
distribution terms for this software are covered by the Eclipse Public
License 1.0 (http://opensource.org/licenses/eclipse-1.0.php) which can
be found in the file epl-v10.html at the root of this distribution.
By using this software in any fashion, you are agreeing to be bound by
the terms of this license.  You must not remove this notice, or any
other, from this software.
