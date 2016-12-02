package sorters

import org.specs2.mutable.Specification

/**
  * Test cases for @see [[SorterSelector]]
  */
class SorterSelectorSpec extends Specification {

  "When the file size is lower than the threshold, return a InMemorySorter " in {
    val fileSizeLowerThreshold: Long = SorterSelector.thresholdToDiskSorterInKilobytes - 1
    SorterSelector.sorterFromFile(fileSizeLowerThreshold) must beAnInstanceOf[InMemorySorter]
  }
  "When the file size is equal to the threshold, return a InMemorySorter " in {
    val fileSizeEqualToThreshold: Long = SorterSelector.thresholdToDiskSorterInKilobytes
    SorterSelector.sorterFromFile(fileSizeEqualToThreshold) must beAnInstanceOf[DiskSorter]
  }
  "When the file size is greater than the threshold, return a DiskSorter " in {
    val fileSizeGreaterThreshold: Long = SorterSelector.thresholdToDiskSorterInKilobytes + 1
    SorterSelector.sorterFromFile(fileSizeGreaterThreshold) must beAnInstanceOf[DiskSorter]
  }
}
