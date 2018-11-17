package app

import scala.collection.mutable.Buffer

import javafx.event.ActionEvent
import scalafx.Includes.eventClosureWrapperWithParam
import scalafx.Includes.jfxMouseEvent2sfx
import scalafx.animation.AnimationTimer
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.canvas.Canvas
import scalafx.scene.control.Button
import scalafx.scene.control.TextArea
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.BorderPane
import scalafx.scene.layout.HBox
import scalafx.scene.paint.Color
import scalafx.scene.text.Text

object App extends JFXApp {
  val sceneWidth = 1200
  val sceneHeight = 800

  val sets = Buffer[MySet]()
  var selected = Buffer[MySet]()

  val newSetName = new TextArea()
  newSetName.promptText = "Set Name"
  newSetName.maxWidth = 125
  newSetName.maxHeight = 25

  val newSetButton = new Button("New Set")
  newSetButton.prefHeight = newSetName.height()

  val newSetMessage = new Text()
  newSetButton.onAction = (e: ActionEvent) => {
    val name = newSetName.text.getValue.trim()
    if (name.length == 0) newSetMessage.text = "Set name cannot be empty."
    else if (name.length > 11) newSetMessage.text = "Set name cannot be longer than 10 characters."
    else if (name.filterNot(x => x.isLetter || x.isDigit).length > 0) newSetMessage.text = "Set name cannot contain non alpha-numeric characters."
    else if (sets.map(_.name).contains(name)) newSetMessage.text = "Set name cannot already be in use."
    else if (sets.length == 10) newSetMessage.text = "Only 10 sets allowed for simultaneous use."
    else {
      newSetMessage.text = "Set " + name + " created."
      sets += MySet(name, 100, 100, 100)
    }
  }

  val topMenu = new HBox(10, newSetName, newSetButton, newSetMessage)

  var prevMouseLoc = Vec2()
  val canvas = new Canvas(sceneWidth, sceneHeight)
  val gc = canvas.graphicsContext2D
  canvas.onMousePressed = (e: MouseEvent) => {
    val mouseLoc = Vec2(e.x,e.y)
    val viable = sets.filter(_.surrounds(mouseLoc)) -- selected
    if (viable.length > 0) {
      selected = Buffer(viable(0))
      selected.foreach(_.selected())
    }
    prevMouseLoc = mouseLoc
  }
  canvas.onMouseDragged = (e: MouseEvent) => {
    val mouseLoc = Vec2(e.x,e.y)
    selected.foreach(_.loc += mouseLoc - prevMouseLoc)
    prevMouseLoc = mouseLoc
  }
  canvas.onMouseReleased = (e: MouseEvent) => {
    selected.foreach(_.deselected())
    selected.clear()
  }
  canvas.onMouseClicked = (e:MouseEvent) => {
    val mouseLoc = Vec2(e.x,e.y)
    val viable = sets.filter(_.surrounds(mouseLoc)) -- selected
    viable.map(_.surroundingPoints(viable))
  }

  val border = new BorderPane
  border.center = canvas
  border.top = topMenu

  stage = new PrimaryStage {
    title = "Set Operation Visualizer"
    resizable = false
    scene = new Scene(sceneWidth, sceneHeight) {
      root = border

      var lastTime = 0L
      val timer: AnimationTimer = AnimationTimer(t => {
        if (lastTime > 0) {
          val delta = (t - lastTime) / 1e9

          gc.clearRect(0, 0, canvas.width(), canvas.height())
          gc.fill = Color.LightGray
          gc.fillRect(0, 0, canvas.width(), canvas.height())
          sets.foreach(_.render(gc))
        }
        lastTime = t
      })
      timer.start()
    }
  }
}
