package app

import scalafx.Includes._
import scalafx.scene.shape.Shape
import scalafx.scene.paint.Color
import scalafx.scene.input.MouseEvent
import scala.collection.mutable.Buffer

class MySetOrganic private (shape_ : Shape, name_ : String, elements_ : Buffer[Element])
  extends MySet(shape_, name_, elements_) {
  
  var initTranslateX = shape.translateX()
  var initTranslateY = shape.translateY()
  var initDragAnchorX = 0.0
  var initDragAnchorY = 0.0

  final def setDefaultBehavior() {
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
      if (dragLock) {
        //unselected()
      } else {
        dragLock = false
      }
    }

    shape.onMouseClicked = (e: MouseEvent) => {
      if (!dragLock) {
        //unselected()
        MySet.changeFocusedSet(Some(MySet.sets.indexOf(this)))
      }
    }
  }
}

object MySetOrganic {
  def apply(shape: Shape): MySetOrganic = {
    shape.fill = MySet.clearColor
    shape.stroke = Color.Blue
    val name = MySet.getName()
    val elements = Element.empty()
    new MySetOrganic(shape, name, elements)
  }
}