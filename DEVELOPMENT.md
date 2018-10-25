### Developing Gnormalizer

#### Run the test

For running all the project Unit tests:

```sh
sbt test
```

For running the failed tests from the latest build:

```sh
sbt testQuick
```

#### Check for library updates:

For checking if there is any dependency update:

```sh
sbt dependencyUpdates
```

### Publishing

#### STEP 1: Log into the Bintray account

In the project root, using SBT set the Bintray account login credentials:

```
sbt bintrayChangeCredentials
```

#### STEP 2: Publish the project to Bintray

On the project root:

```
sbt publish
```
