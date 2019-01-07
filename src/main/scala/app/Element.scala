package app

import scala.collection.mutable.Buffer

class Element

class EUnit(x: String) extends Element {
  override def toString(): String = {
    x.toString()
  }

  def canEqual(a: Any) = a.isInstanceOf[EUnit]
  override def equals(that: Any): Boolean = {
    that match {
      case that: EUnit => that.canEqual(this) && this.hashCode == that.hashCode
      case _ => false
    }
  }
  override def hashCode: Int = {
    val prime = 31
    var result = 1
    result = prime * result + (if (x == null) 0 else x.hashCode)
    return result
  }
}
class EPair(x: String, y: String) extends Element {
  override def toString(): String = {
    "(" + x + ", " + y + ")"
  }

  def canEqual(a: Any) = a.isInstanceOf[EPair]
  override def equals(that: Any): Boolean = {
    that match {
      case that: EPair => that.canEqual(this) && this.hashCode == that.hashCode
      case _ => false
    }
  }
  override def hashCode: Int = {
    val prime = 31
    var result = 1
    result = prime * result + (if (x == null) 0 else x.hashCode) + (if (y == null) 0 else y.hashCode)
    return result
  }
}
class ESet(xs: Set[Element]) extends Element {
  override def toString(): String = {
    "{" + xs.map(_.toString()).mkString(", ") + "}"
  }

  def canEqual(a: Any) = a.isInstanceOf[ESet]
  override def equals(that: Any): Boolean = {
    that match {
      case that: ESet => that.canEqual(this) && this.hashCode == that.hashCode
      case _ => false
    }
  }
  override def hashCode: Int = {
    val prime = 31
    var result = 1
    result = prime * result + xs.map(_.hashCode()).sum
    return result
  }
}

object Element {
  def empty(): ESet = {
    new ESet(Set[Element]())
  }

  def fromString(str: String): Option[ESet] = {
    try {
      Some(new ESet(str.trim
        .drop(1)
        .dropRight(1)
        .trim
        .split(",")
        .map(x => new EUnit(x.trim))
        .toSet))
    } catch {
      case _: Throwable => None
    }
  }
}