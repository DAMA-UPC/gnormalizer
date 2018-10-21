# GNORMALIZER [![Build Status](https://travis-ci.org/DAMA-UPC/gnormalizer.svg?branch=master)](https://travis-ci.org/DAMA-UPC/gnormalizer) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/dab05d9551dc46c0a33a68ae94fa7765)](https://www.codacy.com/app/DAMA-UPC/gnormalizer?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=DAMA-UPC/gnormalizer&amp;utm_campaign=Badge_Grade) [![Codacy Badge](https://api.codacy.com/project/badge/Coverage/dab05d9551dc46c0a33a68ae94fa7765)](https://www.codacy.com/app/DAMA-UPC/gnormalizer?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=DAMA-UPC/gnormalizer&amp;utm_campaign=Badge_Coverage)

## How to use

### Step 1: Add dependency to Gnormalizer

If using the SBT build tool:

```sbtshell
libraryDependencies ++= "dama-upc" %% "gnormalizer" % 0.5.0
```

If using Gradle:

```gradle
compile 'dama-upc:gnormalizer:0.5.0'
```

If using Maven:

```xml
<dependency>
  <groupId>dama-upc</groupId>
  <artifactId>gnormalizer</artifactId>
  <version>0.5.0</version>
  <type>pom</type>
</dependency>
```

### Step 2: Use it:

```scala
import gnormalizer._

Gnormalizer
  .builder()
  .inputFile("./input/connections.graph", EdgeList)
  .outputFile("./output/connections.graph", EdgeList)
  .execute(
    startDeserializationAtLine = Some(2), // If not specified, the deserialization will be from the beginning of the file.
    bucketSize = Some(3000) // If not specified, the default bucket size is '4500'.
  )
```
