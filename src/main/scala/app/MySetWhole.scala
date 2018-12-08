package app

import scalafx.Includes._
import scalafx.scene.shape.Circle
import scalafx.scene.paint.Color
import scalafx.scene.input.MouseEvent
import scalafx.scene.control.Label
import scalafx.geometry.Pos

class MySetWhole private (circle_ : Circle, name_ : String)
  extends MySet(circle_, name_) {

  shape.onMousePressed = (e: MouseEvent) => {
    prevMouseLoc = Vec2(e.x, e.y)
    //selected()
    dragLock = false
  }

  shape.onMouseDragged = (e: MouseEvent) => {
    circle_.centerX = circle_.centerX() + (e.x - prevMouseLoc.x())
    circle_.centerY = circle_.centerY() + (e.y - prevMouseLoc.y())
    nameLabel.layoutX = circle_.centerX() - nameLabel.width() / 2
    nameLabel.layoutY = circle_.centerY() - nameLabel.height() / 2
    prevMouseLoc = Vec2(e.x, e.y)
    dragLock = true
  }

  shape.onMouseReleased = (e: MouseEvent) => {
    if (dragLock) {
      //unselected()
    } else {
      dragLock = false
    }
  }

  shape.onMouseClicked = (e: MouseEvent) => {
    if (!dragLock) {
      //unselected()
      val i = MySet.sets.indexOf(this)
      MySet.focusedSetIndex = Some(i)
      MySet.focusedSetWatcher() = Some(i)
    }
  }

}

object MySetWhole {
  val r = 50

  def apply(): MySet = {
    val shape = Circle(100, 100, r)
    shape.fill = Color.rgb(0, 0, 0, 0.0)
    shape.stroke = Color.Black
    val name = MySet.getName()
    new MySetWhole(shape, name)
  }

  def createEmpty(): MySet = {
    val shape = Circle(-1, -1, r)
    shape.fill = Color.rgb(0, 0, 0, 0.0)
    shape.stroke = Color.rgb(0, 0, 0, 0.0)
    val name = MySet.getName()
    new MySetWhole(shape, name)
  }
}