package app

import scalafx.Includes._
import javafx.event.EventHandler
import javafx.event.ActionEvent
import scalafx.scene.input.MouseEvent
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.control.Button
import scalafx.scene.Scene
import scalafx.scene.layout.BorderPane
import scalafx.scene.canvas.Canvas
import scalafx.scene.paint.Color

import scala.collection.mutable.Buffer
import scalafx.animation.AnimationTimer
import scalafx.scene.input.KeyCode
import scalafx.scene.input.KeyEvent
import scalafx.scene.control.TextArea
import scalafx.scene.layout.HBox
import scalafx.scene.control.TextField
import scalafx.scene.text.Text

object Main extends JFXApp {
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
    if(name.length == 0) newSetMessage.text = "Set name cannot be empty."
    else if(name.length > 11) newSetMessage.text = "Set name cannot be longer than 10 characters."
    else if(name.filterNot(x => x.isLetter || x.isDigit).length > 0) newSetMessage.text = "Set name cannot contain non alpha-numeric characters."
    else if(sets.map(_.name).contains(name)) newSetMessage.text = "Set name cannot already be in use."
    else if(sets.length == 10) newSetMessage.text = "Only 10 sets allowed for simultaneous use."
    else {
      newSetMessage.text = "Set " + name + " created."
      sets += MySet(name, 100, 100, 100)
    }
  }
  val topMenu = new HBox(10,newSetName,newSetButton,newSetMessage)

  val canvas = new Canvas(sceneWidth, sceneHeight)
  val gc = canvas.graphicsContext2D

  var mouseX = 0.0
  var mouseY = 0.0
  var prevMouseX = 0.0
  var prevMouseY = 0.0
  var deltaX = 0.0
  var deltaY = 0.0

  var mousePressed = false
  var mouseDragged = false
  var mouseReleased = false

  canvas.onMousePressed = (e: MouseEvent) => {
    mousePressed = true
    mouseDragged = false
    mouseReleased = false
    prevMouseX = mouseX
    prevMouseY = mouseY
    mouseX = e.x
    mouseY = e.y
    deltaX = mouseX - prevMouseX
    deltaY = mouseY - prevMouseY
  }
  canvas.onMouseDragged = (e: MouseEvent) => {
    mousePressed = false
    mouseDragged = true
    mouseReleased = false
    prevMouseX = mouseX
    prevMouseY = mouseY
    mouseX = e.x
    mouseY = e.y
    deltaX = mouseX - prevMouseX
    deltaY = mouseY - prevMouseY
  }
  canvas.onMouseReleased = (e: MouseEvent) => {
    mousePressed = false
    mouseDragged = false
    mouseReleased = true
    prevMouseX = mouseX
    prevMouseY = mouseY
    mouseX = e.x
    mouseY = e.y
    deltaX = mouseX - prevMouseX
    deltaY = mouseY - prevMouseY
  }

  def setMovementManagement(delta:Double) {
    val viable = sets.filter(_.surrounds(mouseX, mouseY)) -- selected
    if(viable.length > 0 && mousePressed) {
      selected = Buffer(viable(0))
      selected.foreach(_.selected())
    }
    else if(mouseDragged) selected.foreach(_ += (deltaX*delta*80, deltaY*delta*80))
    else if(mouseReleased) {
      selected.foreach(_.deselected())
      selected.clear()
    }
  }

  stage = new PrimaryStage {
    title = "Set Operation Visualizer"
    resizable = false
    scene = new Scene(sceneWidth, sceneHeight) {
      val border = new BorderPane
      border.center = canvas
      border.top = topMenu

      root = border

      var lastTime = 0L
      val timer: AnimationTimer = AnimationTimer(t => {
        if (lastTime > 0) {
          val delta = (t - lastTime) / 1e9

          gc.clearRect(0, 0, canvas.width(), canvas.height())
          sets.foreach(_.render(gc))

          setMovementManagement(delta)
        }
        lastTime = t
      })
      timer.start()
    }
  }
}
