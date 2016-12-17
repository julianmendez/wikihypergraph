# [WikiHyperGraph](https://julianmendez.github.io/wikihypergraph/)

[![Build Status](https://travis-ci.org/julianmendez/wikihypergraph.png?branch=master)](https://travis-ci.org/julianmendez/wikihypergraph)


## Source code

To clone and compile the project:

```
$ git clone https://github.com/julianmendez/wikihypergraph.git
$ cd wikihypergraph
$ mvn clean install
```

To run the project (after compiling it):

```
$ cd wikihypergraph
$ mvn exec:java
```

(this means, `cd` twice after cloning)

To compile the project offline, first download the dependencies:

```
$ mvn dependency:go-offline
```

and once offline, use:

```
$ mvn --offline clean install
```

The version number is updated with:

```
$ mvn versions:set -DnewVersion=NEW_VERSION
```

where *NEW_VERSION* is the new version.


### License

This software is distributed under the [GNU General Public License Version 3](https://www.gnu.org/licenses/gpl-3.0.txt).


### Contact

For more information, please contact @julianmendez .

