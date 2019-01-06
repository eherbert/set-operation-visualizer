package app

import scala.collection.mutable.Buffer

class Element

class SUnit(x: String) extends Element {
  override def toString(): String = {
    "SUnit (" + x + ")"
  }
}
class SPair(x: String, y: String) extends Element {
  override def toString(): String = {
    "SPair (" + x + ", " + y + ")"
  }
}
class SSet(xs: Buffer[Element]) extends Element {
  override def toString(): String = {
    "SSet {" + xs.map(_.toString()).mkString(", ") + "}"
  }
}

object Element {
  def empty(): Buffer[Element] = {
    Buffer[Element]()
  }

  def fromString(str: String): Option[Buffer[Element]] = {
    try {
      Some(str.trim
        .drop(1)
        .dropRight(1)
        .trim
        .split(",")
        .map(x => new SUnit(x.trim))
        .toBuffer)
    } catch  {
      case _ : Throwable => None
    }
  }
}