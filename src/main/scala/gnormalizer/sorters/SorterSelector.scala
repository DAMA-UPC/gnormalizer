package gnormalizer.sorters

/**
  * Object containing methods using for choosing a [[Sorter]] between
  * the [[DiskSorter]] and the [[InMemorySorter]].
  */
object SorterSelector {

  @inline private[this] val bytesInKilobyte: Int = 1024
  @inline private[this] val bytesInMegabyte: Long = bytesInKilobyte * 1024L
  @inline private[this] val bytesInGigabyte: Long = bytesInMegabyte * 1024L

  /**
    * Threshold used used for making the decision between using
    * the [[InMemorySorter]] or the [[DiskSorter]].
    */
  val thresholdToDiskSorterInKilobytes: Long = bytesInGigabyte // 1 Gigabyte

  /**
    * Obtains the [[Sorter]] needed to sort an input file. It can be
    * either a [[DiskSorter]] or as [[InMemorySorter]], choosing the [[InMemorySorter]]
    * if the whole graph fits entirely into memory.
    *
    * @param fileSizeInBytes from the file that will be sorted by the output [[Sorter]].
    * @return the [[Sorter]] that will be used to sort the input graph.
    */
  def sorterFromFile(fileSizeInBytes: Long): Sorter =
    if (fileSizeInBytes < bytesInGigabyte) {
      new InMemorySorter()
    } else {
      new DiskSorter()
    }
}
