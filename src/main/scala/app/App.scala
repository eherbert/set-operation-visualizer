package app

import scala.collection.mutable.Buffer

import javafx.scene.input.MouseEvent
import scalafx.Includes.observableList2ObservableBuffer
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.canvas.Canvas
import scalafx.scene.layout.BorderPane
import scalafx.scene.shape.Circle
import scalafx.beans.property.ObjectProperty
import scalafx.animation.AnimationTimer

object App extends JFXApp {
  val sceneWidth = 1200
  val sceneHeight = 800

  var prevSelectedRegion: scalafx.scene.shape.Shape = new Circle()

  val canvas = new Canvas(sceneWidth, sceneHeight)
  canvas.onMouseClicked = (e: MouseEvent) => {
    MySet.unselected()
    MySet.sets.foreach(_.unselected())
  }

  val border = new BorderPane
  border.center = canvas

  stage = new PrimaryStage {
    title = "Set Operation Visualizer"
    resizable = false
    scene = new Scene(sceneWidth, sceneHeight) {
      content = Buffer(canvas, MySet.selectedRegion, MySet().shape, MySet(200, 200).shape, MySet(300, 300).shape)
      
      MySet.watcher.onChange((source, oldValue, newValue) => {
        content -= prevSelectedRegion
        content.insert(1, MySet.selectedRegion)
        prevSelectedRegion = MySet.selectedRegion
      })

      var lastTime = 0L
      val timer: AnimationTimer = AnimationTimer(t => {
        if (lastTime > 0) {
          val delta = (t - lastTime) / 1e9
          
        }
        lastTime = t
      })
      timer.start()
    }
  }
}
