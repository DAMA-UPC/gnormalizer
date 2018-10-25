# GNORMALIZER [![Build Status](https://travis-ci.org/DAMA-UPC/gnormalizer.svg?branch=master)](https://travis-ci.org/DAMA-UPC/gnormalizer) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/dab05d9551dc46c0a33a68ae94fa7765)](https://www.codacy.com/app/DAMA-UPC/gnormalizer?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=DAMA-UPC/gnormalizer&amp;utm_campaign=Badge_Grade) [![Codacy Badge](https://api.codacy.com/project/badge/Coverage/dab05d9551dc46c0a33a68ae94fa7765)](https://www.codacy.com/app/DAMA-UPC/gnormalizer?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=DAMA-UPC/gnormalizer&amp;utm_campaign=Badge_Coverage)

## How to use the library

### Step 1: Add dependency:

https://bintray.com/dama-upc/Babel-Platform/babel

#### If using the SBT tool:

Add the following snippet to the `build.sbt` file:

```sbtshell
resolvers += "maven" at "https://dl.bintray.com/dama-upc/Babel-Platform"
libraryDependencies += "dama-upc" %% "gnormalizer" % "0.7.0"
```

### Step 2: Use it:

For using Gnormalizer, we can use its builder API directly for specifying the
input graph and its format. Then, we need to specify the output graph location
and format and call the `execute()` method. The following code snippet shows an
example usage of the library:

```scala
import gnormalizer._

Gnormalizer
  .builder()
  .inputFile("./input/connections.graph", EdgeList)
  .outputFile("./output/connections.graph", EdgeList)
  .execute()
```

If we want a verbose mode, printing the graph as soon as its been normalized
or we to overload the default `startDeserializationAtLine` and/or `bucketSize`
default configurations, we can call an overloaded `execute`
method as in the following code snippet:

```scala
import gnormalizer._

Gnormalizer
  .builder()
  .inputFile("./input/connections.graph", EdgeList)
  .outputFile("./output/connections.graph", EdgeList)
  .execute(
    startDeserializationAtLine = Some(2), // Default -> 1
    bucketSize = Some(3000), // Default -> 4500
    verboseLog = true // Default -> false
  )
```
