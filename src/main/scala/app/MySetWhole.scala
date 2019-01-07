package app

import scalafx.Includes._
import scalafx.scene.shape.Circle
import scalafx.scene.paint.Color
import scalafx.scene.input.MouseEvent
import scalafx.scene.control.Label
import scalafx.geometry.Pos
import scala.collection.mutable.Buffer

class MySetWhole private (circle_ : Circle, name_ : String, elements_ : ESet)
  extends MySet(circle_, name_, elements_) {

}

object MySetWhole {
  val r = 50

  def apply(): MySet = {
    val shape = Circle(100, 100, r)
    shape.fill = MySet.clearColor
    shape.stroke = Color.Black
    val name = MySet.getName()
    val elements = Element.empty()
    val set = new MySetWhole(shape, name, elements)
    set.setDefaultBehavior()
    set
  }

  def createEmpty(): MySet = {
    val shape = Circle(-1, -1, r)
    shape.fill = MySet.clearColor
    shape.stroke = MySet.clearColor
    val name = ""
    val elements = Element.empty()
    val set = new MySetWhole(shape, name, elements)
    set.setDefaultBehavior()
    set
  }
}