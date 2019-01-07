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
import scalafx.scene.input.MouseEvent

class MySet(var shape: Shape, var name: String, var elements: ESet) {

  protected var dragLock = false
  protected var stroke = shape.stroke()
  protected var fill = shape.fill()

  protected val nameLabel = new Label(name)
  nameLabel.textFill = Color.Black

  def selected() { shape.stroke = Color.Green }
  def unselected() { shape.stroke = stroke }

  def focused() { shape.stroke = Color.Green }
  def unfocused() { shape.stroke = stroke }

  def content(): Buffer[Node] = Buffer(shape, nameLabel)

  def setName(str: String) {
    name = str
  }

  def setElements(str: String): Boolean = {
    Element.fromString(str) match {
      case Some(es) => {
        elements = es
        true
      }
      case None => false
    }
  }

  private var initTranslateX = shape.translateX()
  private var initTranslateY = shape.translateY()
  private var initDragAnchorX = 0.0
  private var initDragAnchorY = 0.0

  def setDefaultBehavior() {
    shape.onMousePressed = (e: MouseEvent) => {
      dragLock = false
      initTranslateX = shape.translateX()
      initTranslateY = shape.translateY()
      initDragAnchorX = e.sceneX
      initDragAnchorY = e.sceneY
    }

    shape.onMouseDragged = (e: MouseEvent) => {
      shape.translateX = initTranslateX + (e.sceneX - initDragAnchorX)
      shape.translateY = initTranslateY + (e.sceneY - initDragAnchorY)
      dragLock = true
    }

    shape.onMouseReleased = (e: MouseEvent) => {
      if (!dragLock) {
        dragLock = false
      }
    }

    shape.onMouseClicked = (e: MouseEvent) => {
      if (!dragLock) {
        MySet.changeFocusedSet(Some(MySet.sets.indexOf(this)))
      }
    }
  }

  def setUserSelectionBehavior() {
    shape.onMousePressed = (e: MouseEvent) => {}
    shape.onMouseDragged = (e: MouseEvent) => {}
    shape.onMouseReleased = (e: MouseEvent) => {}

    shape.onMouseClicked = (e: MouseEvent) => {
      println(name)
      //println((e.sceneX, e.sceneY))
      //MySet.clipIntersection(new Point2D(e.x, e.y))
      MySet.clipIntersection(new Point2D(e.sceneX, e.sceneY))
    }
  }

}

object MySet {
  import StringUtils._

  val sets = Buffer[MySet]()
  val contentWatcher = ObjectProperty(sets.flatMap(_.content()))

  val clearColor = Color.rgb(0, 0, 0, 0.0)

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
    name
  }

  var focusedSetIndex: Option[Int] = None
  val focusedSetWatcher = ObjectProperty(focusedSetIndex)

  def changeFocusedSet(set: Option[Int]) {
    focusedSetIndex match {
      case Some(i) => sets(i).unfocused()
      case None =>
    }
    focusedSetIndex = set
    focusedSetWatcher() = set
    focusedSetIndex match {
      case Some(i) => sets(i).focused()
      case None =>
    }
  }

  def createMenuShape(shape: scalafx.scene.shape.Shape): scalafx.scene.shape.Shape = {
    val ret = Shape.intersect(shape, shape)
    ret.fill <== shape.fill
    ret.stroke <== shape.stroke
    ret
  }

  var selectionBuilder: Option[MySetOrganic] = None

  def clipIntersection(loc: Point2D) {
    val involved = sets.filter(x => {
      val localLoc = x.shape.parentToLocal(loc)
      x.shape.contains(localLoc)
    })
    val add = involved.foldLeft(involved(0).shape: scalafx.scene.shape.Shape)((r, e) => Shape.intersect(r, e.shape))
    val shape = (sets -- involved).foldLeft(add)((r, e) => Shape.subtract(r, e.shape))
    selectionBuilder = selectionBuilder match {
      case Some(sb) => {
        deregisterMySet(sb)
        val union = Shape.union(sb.shape, shape)
        union.fill = Color.Red
        sb.shape = union
        registerMySet(sb)
        Some(sb)
      }
      case None => {
        val sb = MySetOrganic(shape, "")
        sb.shape.fill = Color.Red
        registerMySet(sb)
        Some(sb)
      }
    }
  }
}