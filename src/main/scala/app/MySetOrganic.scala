package app

import scalafx.Includes._
import scalafx.scene.shape.Shape
import scalafx.scene.paint.Color
import scalafx.scene.input.MouseEvent
import scala.collection.mutable.Buffer

class MySetOrganic private (shape_ : Shape, name_ : String, elements_ : ESet)
  extends MySet(shape_, name_, elements_) {

}

object MySetOrganic {
  def apply(shape: Shape): MySetOrganic = {
    shape.fill = MySet.clearColor
    shape.stroke = Color.Black
    val name = MySet.getName()
    val elements = Element.empty()
    new MySetOrganic(shape, name, elements)
  }

  def apply(shape: Shape, name: String): MySetOrganic = {
    shape.fill = MySet.clearColor
    shape.stroke = Color.Black
    val elements = Element.empty()
    new MySetOrganic(shape, name, elements)
  }
}