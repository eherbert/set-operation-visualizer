package app

import scalafx.Includes._
import javafx.event.ActionEvent
import scalafx.scene.shape.Shape
import scalafx.scene.shape.Circle
import scalafx.scene.paint.Color
import javafx.scene.input.MouseEvent
import scala.collection.mutable.Buffer
import scalafx.geometry.Point2D
import scalafx.beans.value.ObservableValue
import scalafx.beans.property.ObjectProperty

class MySet private (val shape: Circle) {
  private var prevMouseLoc = Vec2()

  def selected() { shape.stroke = Color.Green }
  def unselected() { shape.stroke = Color.Black }

  shape.onMousePressed = (e: MouseEvent) => {
    prevMouseLoc = Vec2(e.x, e.y)
    selected()
    MySet.unselected()
  }

  shape.onMouseDragged = (e: MouseEvent) => {
    shape.centerX = shape.centerX() + (e.x - prevMouseLoc.x())
    shape.centerY = shape.centerY() + (e.y - prevMouseLoc.y())
    prevMouseLoc = Vec2(e.x, e.y)
  }

  shape.onMouseReleased = (e: MouseEvent) => {
    unselected()
    MySet.unselected()
  }

  shape.onMouseClicked = (e: MouseEvent) => {
    MySet.clipIntersection(new Point2D(e.x, e.y))
  }
}

object MySet {
  val r = 100

  val sets = Buffer[MySet]()
  var selectedRegion: scalafx.scene.shape.Shape = new Circle()
  val watcher = ObjectProperty(selectedRegion.boundsInParent)
  selectedRegion.fill = Color.Red

  def apply(): MySet = {
    val shape = Circle(100, 100, r)
    shape.fill = Color.rgb(0, 0, 0, 0.0)
    shape.stroke = Color.Black
    val set = new MySet(shape)
    sets += set
    set
  }

  def apply(cx: Double, cy: Double): MySet = {
    val shape = Circle(cx, cy, r)
    shape.fill = Color.rgb(0, 0, 0, 0.0)
    shape.stroke = Color.Black
    val set = new MySet(shape)
    sets += set
    set
  }

  def clipIntersection(loc: Point2D) {
    val involved = sets.filter(_.shape.contains(loc))
    val add = involved.foldLeft(involved(0).shape: scalafx.scene.shape.Shape)((r, e) => Shape.intersect(r, e.shape))
    val shape = (sets -- involved).foldLeft(add)((r,e) => Shape.subtract(r, e.shape))
    selectedRegion = shape
    selectedRegion.fill = Color.Red
    watcher() = selectedRegion.boundsInParent
  }

  def unselected() {
    selectedRegion = new Circle()
    watcher() = selectedRegion.boundsInParent
  }
}