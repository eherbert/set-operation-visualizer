package app

import scalafx.Includes._
import scalafx.scene.paint.Color
import scalafx.scene.shape.Shape
import scala.collection.mutable.Buffer
import scalafx.geometry.Point2D
import scalafx.scene.Node
import scalafx.beans.property.ObjectProperty
import scalafx.scene.control.Label
import scalafx.geometry.Pos

class MySet(val shape: Shape, var name: String) {

  protected var prevMouseLoc = Vec2()
  protected var dragLock = false
  protected var members = Set[String]()
  protected var stroke = shape.stroke()
  protected var fill = shape.fill()

  protected val nameLabel = new Label(name)
  nameLabel.textFill = Color.Black

  def selected() { shape.stroke = Color.Green }
  def unselected() { shape.stroke = stroke }

  def focused() { shape.stroke = Color.Green }
  def unfocused() { shape.stroke = stroke }

  def content(): Buffer[Node] = Buffer(shape, nameLabel)
}

object MySet {
  import StringUtils._

  val sets = Buffer[MySet]()
  val contentWatcher = ObjectProperty(sets.flatMap(_.content()))

  def registerMySet(set: MySet) {
    sets += set
    contentWatcher() = sets.flatMap(_.content())
  }
  def deregisterMySet(set: MySet) {
    sets -= set
    contentWatcher() = sets.flatMap(_.content())
  }

  var nextName = "A"

  def getName(): String = {
    while (sets.map(_.name).contains(nextName)) {
      nextName = nextName.increment
    }
    val name = nextName
    nextName = nextName.increment
    println(name)
    name
  }

  var focusedSetIndex: Option[Int] = None
  val focusedSetWatcher = ObjectProperty(focusedSetIndex)

  def createMenuShape(shape: scalafx.scene.shape.Shape): scalafx.scene.shape.Shape = {
    val ret = Shape.intersect(shape, shape)
    ret.fill <== shape.fill
    ret.stroke <== shape.stroke
    ret
  }

}

/*
class MySet private (val shape: Circle) {
  private var prevMouseLoc = Vec2()
  private var dragLock = false

  def selected() { shape.stroke = Color.Green }
  def unselected() { shape.stroke = Color.Black }

  shape.onMousePressed = (e: MouseEvent) => {
    prevMouseLoc = Vec2(e.x, e.y)
    selected()
    MySet.unselected()
    dragLock = false
  }

  shape.onMouseDragged = (e: MouseEvent) => {
    shape.centerX = shape.centerX() + (e.x - prevMouseLoc.x())
    shape.centerY = shape.centerY() + (e.y - prevMouseLoc.y())
    prevMouseLoc = Vec2(e.x, e.y)
    dragLock = true
  }

  shape.onMouseReleased = (e: MouseEvent) => {
    if(dragLock) {
      unselected()
      MySet.unselected()
    } else dragLock = false
  }

  shape.onMouseClicked = (e: MouseEvent) => {
    if(!dragLock) {
      unselected()
      MySet.clipIntersection(new Point2D(e.x, e.y))
    }
  }
}

object MySet {
  val r = 100

  val sets = Buffer[MySet]()
  var selectedRegion: scalafx.scene.shape.Shape = new Circle()
  val watcher = ObjectProperty(selectedRegion.boundsInParent)
  selectedRegion.fill = Color.Red
  
  var ctrlClicked = false

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
    if(ctrlClicked) selectedRegion = Shape.union(selectedRegion, shape)
    else selectedRegion = shape
    selectedRegion.fill = Color.Red
    watcher() = selectedRegion.boundsInParent
  }

  def unselected() {
    selectedRegion = new Circle()
    watcher() = selectedRegion.boundsInParent
  }
}
*/