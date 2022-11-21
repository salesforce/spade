package com.salesforce.mce.spade.aws

package object util {

  implicit class SeqAsOption[T](seq: Seq[T]) {
    def asOption(): Option[Seq[T]] = if (seq.isEmpty) None else Option(seq)
  }

}
