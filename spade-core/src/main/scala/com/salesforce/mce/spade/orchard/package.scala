package com.salesforce.mce.spade

import scala.collection.compat.immutable.LazyList
import scala.collection.compat.immutable.LazyList.#::

package object orchard {

  implicit class LazyListHasLazyFold[A](xs: LazyList[A]) {
    def lazyFold[B](z: B)(op: (B, A) => (B, Boolean)): B = xs match {
      case LazyList() =>
        z
      case head #:: tail =>
        val (newZ, cont) = op(z, head)
        if (cont) tail.lazyFold(newZ)(op) else newZ
    }
  }

}
