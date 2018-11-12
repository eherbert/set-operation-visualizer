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

object Main extends JFXApp {
  val sceneWidth = 1200
  val sceneHeight = 800

  val sets = Buffer[MySet]()
  var selected = Buffer[MySet]()

  val newSetButton = new Button("New Set")
  newSetButton.onAction = (e: ActionEvent) => {
    sets += MySet(100, 100, 100)
  }

  val canvas = new Canvas(sceneWidth, sceneHeight)
  val gc = canvas.graphicsContext2D

  var prevMouseX = 0.0
  var prevMouseY = 0.0
  var ctrlPressed = false

  stage = new PrimaryStage {
    title = "Set Operation Visualizer"
    resizable = false
    scene = new Scene(sceneWidth, sceneHeight) {
      val border = new BorderPane
      border.left = newSetButton
      border.center = canvas

      root = border

      var lastTime = 0L
      val timer: AnimationTimer = AnimationTimer(t => {
        if (lastTime > 0) {
          val delta = (t - lastTime) / 1e9

          gc.clearRect(0, 0, canvas.width(), canvas.height())
          sets.foreach(_.render(gc))

          onKeyPressed = (e: KeyEvent) => {
            e.code match {
              case KeyCode.CONTROL => ctrlPressed = true
              case _ =>
            }
          }

          onKeyReleased = (e: KeyEvent) => {
            e.code match {
              case KeyCode.CONTROL => ctrlPressed = false
              case _ =>
            }
          }

          canvas.onMousePressed = (e: MouseEvent) => {
            val viable = sets.filter(_.surrounds(e.x, e.y)) -- selected
            if (viable.length > 0) {
              if (ctrlPressed) selected += viable(0)
              else selected = Buffer(viable(0))
            }
            selected.foreach(_.selected())
            prevMouseX = e.x
            prevMouseY = e.y
          }
          canvas.onMouseDragged = (e: MouseEvent) => {
            selected.foreach(_ += (e.x - prevMouseX, e.y - prevMouseY))
            prevMouseX = e.x
            prevMouseY = e.y
          }
          canvas.onMouseReleased = (e: MouseEvent) => {
            if (!ctrlPressed) {
              selected.foreach(_.deselected())
              selected.clear()
            }
          }
        }
        lastTime = t
      })
      timer.start()
    }
  }
}
