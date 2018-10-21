package gnormalizer.api

import babel.graph.GraphFormat
import better.files._
import gnormalizer.normalizers.GraphNormalizer

/**
  * Gnormalizer Builder API.
  */
class GnormalizerApiBuilder(normalizer: GraphNormalizer) {

  /**
    * Adds the input file path and its type to the builder.
    *
    * @param inputFilePath with the location of the graph we want to transform.
    * @param inputGraphType of the graph we want to parse.
    * @return a [[GnormalizerBuilderWithInput]] instance with the input parameters as input.
    */
  def inputFile(inputFilePath: String, inputGraphType: GraphFormat): GnormalizerBuilderWithInput =
    GnormalizerBuilderWithInput(inputFilePath, inputGraphType)

  /**
    * Builder instance of the Gnormalizer API returned to the user when he/she has already provided the input.
    *
    * @param inputPath with the location of the graph we want to transform.
    * @param inputGraphType of the graph we want to parse.
    */
  case class GnormalizerBuilderWithInput private (inputPath: String,
                                                  inputGraphType: GraphFormat) {

    /**
      * Adds the output file path and its type to the builder.
      *
      * NOTE: The output file will be removed prior the transformation!
      *
      * @param outputPath with the location of the output graph.
      * @param outputGraphType with the type of the output graph.
      * @return a [[GnormalizerBuilderWithInputAndOutput]] instance with the input and output graph information.
      */
    def outputFile(outputPath: String,
                   outputGraphType: GraphFormat): GnormalizerBuilderWithInputAndOutput =
      GnormalizerBuilderWithInputAndOutput(inputPath = inputPath,
                                           inputGraphType = inputGraphType,
                                           outputPath = outputPath,
                                           outputGraphType = outputGraphType)
  }

  /**
    * Builder instance with all the input/output graph information we need for deserializing the output graph.
    *
    * NOTE: The output file will be removed prior the transformation!
    *
    * @param inputPath with the location of the graph we want to transform.
    * @param inputGraphType of the graph we want to parse.
    * @param outputPath with the location of the output graph.
    * @param outputGraphType with the type of the output graph.
    */
  case class GnormalizerBuilderWithInputAndOutput private (inputPath: String,
                                                           inputGraphType: GraphFormat,
                                                           outputPath: String,
                                                           outputGraphType: GraphFormat) {

    /**
      * Execute the transformation without overriding any default configuration.
      */
    @SuppressWarnings(Array("org.wartremover.warts.Overloading")) // Required if using the library from Java.
    def execute(): Unit = execute(None, None)

    /**
      * Execute the transformation operation overriding the bucket size or the line the serialization starts.
      *
      * NOTE: The output file will be removed prior the transformation!
      *
      * @param bucketSize that will be used for performing the sorting.
      * @param startDeserializationAtLine with the line of the graph we want to start the serialization.
      */
    @SuppressWarnings(Array("org.wartremover.warts.Overloading")) // Required if using the library from Java.
    def execute(bucketSize: Option[Int] = None,
                startDeserializationAtLine: Option[Long] = None): Unit = {

      val outputFile = outputPath.toFile.delete(true).createFile()

      normalizer
        .obfuscateEdgeListGraph(inputPath, startDeserializationAtLine)
        .unsafeRunSync()
        .map(_.toString)
        .map(_.concat("\n"))
        .foreach(outputFile.append(_)(DefaultCharset))
    }
  }
}
