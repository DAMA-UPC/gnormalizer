# GNORMALIZER [![Build Status](https://travis-ci.org/DAMA-UPC/gnormalizer.svg?branch=master)](https://travis-ci.org/DAMA-UPC/gnormalizer) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/dab05d9551dc46c0a33a68ae94fa7765)](https://www.codacy.com/app/DAMA-UPC/gnormalizer?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=DAMA-UPC/gnormalizer&amp;utm_campaign=Badge_Grade) [![Codacy Badge](https://api.codacy.com/project/badge/Coverage/dab05d9551dc46c0a33a68ae94fa7765)](https://www.codacy.com/app/DAMA-UPC/gnormalizer?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=DAMA-UPC/gnormalizer&amp;utm_campaign=Badge_Coverage)

## How to use

### Step 1: Add dependency to Gnormalizer

If using the SBT build tool:

```sbtshell
libraryDependencies ++= "edu.upc" %% "gnormalizer" % 0.4 % "test"
```

### Step 2: Use it:

```scala
import gnormalizer.Gnormalizer
import gnormalizer.GraphFormat

Gnormalizer
  .inputFile("./input/connections.graph", GraphFormat.EdgeList)
  .outputFile("./output/connections.graph", GraphFormat.EdgeList)
  .execute(bucketSize = 3000) // If not specified, the default bucket size is '4500'.
```
