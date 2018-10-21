import gnormalizer.api.{AcceptedGraphFormats, GnormalizerApiBuilder}
import gnormalizer.normalizers.GraphNormalizer

package object gnormalizer extends AcceptedGraphFormats {

  /**
    * Object containing the entry-points of the Gnormalizer APIs.
    */
  object Gnormalizer {

    /**
    * Contains the list of accepted graph formats in Gnormalizer.
      */
    object GraphFormats extends AcceptedGraphFormats

    /**
      * @return an instance of the builder API [[GnormalizerApiBuilder]].
      */
   def builder(): GnormalizerApiBuilder = new GnormalizerApiBuilder(new GraphNormalizer())
 }
}
