package app

import scala.collection.mutable.Buffer

import scalafx.Includes._
import scalafx.animation.AnimationTimer
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.event.ActionEvent
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.layout.Background
import scalafx.scene.layout.BackgroundFill
import scalafx.scene.layout.BorderPane
import scalafx.scene.layout.BorderStrokeStyle
import scalafx.scene.layout.BorderWidths
import scalafx.scene.layout.CornerRadii
import scalafx.scene.layout.HBox
import scalafx.scene.layout.VBox
import scalafx.scene.paint.Color
import scalafx.scene.input.MouseEvent
import scalafx.scene.shape.Shape
import scalafx.scene.control.TextField
import scalafx.geometry.Point2D
import scalafx.scene.control.Label
import scalafx.scene.shape.Circle

object App extends JFXApp {
  val sceneWidth = 1200
  val sceneHeight = 800

  MySet.registerMySet(MySetWhole())
  MySet.focusedSetIndex = Some(0)

  /*
   *
   * GUI DESIGN
   *
   */

  val newSetButton = new Button("New Set")
  val controlsMenu = new HBox(25, newSetButton)
  var setMenuShape = MySet.createMenuShape(new Circle())
  val nameField = new TextField()
  val errorLabel = new Label("")
  val setMenu = new VBox(25, setMenuShape.delegate, nameField, errorLabel)
  val deleteSetButton = new Button("Delete Set")
  val setMenuBorderPane = new BorderPane
  val borderPane = new BorderPane

  controlsMenu.background = new Background(Array(new BackgroundFill(Color.LightGrey, null, null)))
  controlsMenu.padding = Insets(25)
  controlsMenu.border = new javafx.scene.layout.Border(new javafx.scene.layout.BorderStroke(
    Color.BLACK,
    BorderStrokeStyle.Solid,
    CornerRadii.Empty,
    BorderWidths.Default))

  nameField.text = MySet.sets(0).name
  nameField.onAction = (e: ActionEvent) => {
    val str = nameField.text().trim
    if (str.filterNot(_.isLetter).length > 0) {
      errorLabel.text = "Set names can only contain letters."
    } else if (str.filterNot(_.isUpper).length > 0) {
      errorLabel.text = "Set names can only contain upper letters."
    } else if (str.length > 10) {
      errorLabel.text = "Set names can be no longer than 10 characters."
    } else if (MySet.sets.map(_.name) == str) {
      errorLabel.text = "Set name already in use."
    } else {
      MySet.focusedSetIndex match {
        case Some(i) => MySet.sets(i).name = str
        case None => errorLabel.text = "No set selected."
      }
    }
  }

  errorLabel.textFill = Color.Red
  errorLabel.wrapText = true

  setMenu.padding = Insets(25)

  def setupSetMenu(shape: scalafx.scene.shape.Shape, name: String) {
    setMenuShape = MySet.createMenuShape(shape)
    setMenu.children(0) = setMenuShape
    nameField.text = name
  }

  setMenu.padding = Insets(25)
  setupSetMenu(new Circle(), "")

  deleteSetButton.maxWidth = Double.MaxValue
  deleteSetButton.onAction = (e: ActionEvent) => {
    MySet.focusedSetIndex match {
      case Some(i) => {
        MySet.deregisterMySet(MySet.sets(i))
        MySet.focusedSetIndex = None
      }
      case None => errorLabel.text = "No set selected."
    }
  }

  setMenuBorderPane.prefWidth = 100
  setMenuBorderPane.top = setMenu
  setMenuBorderPane.bottom = deleteSetButton
  setMenuBorderPane.background = new Background(Array(new BackgroundFill(Color.LightGrey, null, null)))
  setMenuBorderPane.border = new javafx.scene.layout.Border(new javafx.scene.layout.BorderStroke(
    Color.BLACK,
    BorderStrokeStyle.Solid,
    CornerRadii.Empty,
    BorderWidths.Default))

  borderPane.bottom = controlsMenu
  borderPane.right = setMenuBorderPane

  /*
   *
   * APP
   *
   */

  stage = new PrimaryStage {
    title = "Set Operation Visualizer"
    resizable = false
    scene = new Scene(sceneWidth, sceneHeight) {
      root = borderPane
      MySet.sets.flatMap(_.content()).foreach(node => content += node)

      MySet.contentWatcher.onChange((source, oldValue, newValue) => {
        (oldValue -- newValue).foreach(node => content -= node)
        (newValue -- oldValue).foreach(node => content += node)
      })

      MySet.focusedSetWatcher.onChange((source, oldValue, newValue) => {
        newValue match {
          case Some(pos) => setupSetMenu(MySet.sets(pos).shape, MySet.sets(pos).name)
          case None => setupSetMenu(new Circle(), "")
        }
      })

      newSetButton.onAction = (e: ActionEvent) => {
        val set = MySetWhole()
        MySet.registerMySet(set)
      }

      /*
      var lastTime = 0L
      val timer: AnimationTimer = AnimationTimer(t => {
        if (lastTime > 0) {
          val delta = (t - lastTime) / 1e9

        }
        lastTime = t
      })
      timer.start()
      *
      */
    }
  }
}