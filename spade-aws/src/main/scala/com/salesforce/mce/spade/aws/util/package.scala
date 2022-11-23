package com.salesforce.mce.spade.aws

package object util {

  implicit class SeqHasAsOption[T](seq: Seq[T]) {
    def asOption(): Option[Seq[T]] = if (seq.isEmpty) None else Option(seq)
  }

  implicit class MapHasAsOption[K, V](map: Map[K, V]) {
    def asOption(): Option[Map[K, V]] = if (map.isEmpty) None else Option(map)
  }


}
