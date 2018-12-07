package app

import scalafx.Includes._
import scalafx.scene.shape.Shape
import scalafx.scene.paint.Color
import scalafx.scene.input.MouseEvent

class MySetOrganic private (shape_ : Shape, name_ : String) extends MySet(shape_, name_) {
  shape.onMousePressed = (e: MouseEvent) => {
    selected()
  }
  
  shape.onMouseReleased = (e: MouseEvent) => {
    unselected()
  }
}

object MySetOrganic {
  def apply(shape: Shape): MySetOrganic = {
    shape.fill = Color.Grey
    val name = MySet.getName()
    new MySetOrganic(shape, name)
  }
}