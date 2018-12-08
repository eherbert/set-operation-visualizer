package app


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
import scalafx.scene.text.Font
import scalafx.scene.canvas.Canvas
import scala.collection.mutable.Buffer

object App extends JFXApp {
  val sceneWidth = 1200
  val sceneHeight = 800

  /*
   *
   * GUI DESIGN
   *
   */

  val messageLabel = new Label("")
  val messageLabelBox = new HBox(0, messageLabel)
  val newSetButton = new Button("New Set")
  val controlsMenu = new HBox(25, newSetButton)
  val bottomBox = new VBox(0, messageLabelBox, controlsMenu)
  var setMenuShape = MySet.createMenuShape(new Circle())
  val nameField = new TextField()
  val setMenu = new VBox(25, setMenuShape.delegate, nameField)
  val deleteSetButton = new Button("Delete Set")
  val setMenuBorderPane = new BorderPane
  val borderPane = new BorderPane

  val padding = Insets(25)
  val background = new Background(Array(new BackgroundFill(Color.LightGrey, null, null)))
  val border = new javafx.scene.layout.Border(new javafx.scene.layout.BorderStroke(
    Color.BLACK,
    BorderStrokeStyle.Solid,
    CornerRadii.Empty,
    BorderWidths.Default))

  messageLabel.text = "Hello world!"
  messageLabel.font = new Font(14)
  messageLabel.wrapText = true

  messageLabelBox.padding = padding
  messageLabelBox.border = border
  messageLabelBox.background = background

  def errorMessage(str: String) {
    messageLabel.textFill = Color.Red
    messageLabel.text = str
  }

  def actionMessage(str: String) {
    messageLabel.textFill = Color.Black
    messageLabel.text = str
  }

  controlsMenu.background = background
  controlsMenu.padding = padding

  nameField.promptText = "set name"
  nameField.onAction = (e: ActionEvent) => {
    val str = nameField.text().trim
    if (str.filterNot(_.isLetter).length > 0) { errorMessage("Set names can only contain letters.") }
    else if (str.filterNot(_.isUpper).length > 0) { errorMessage("Set names can only contain upper letters.") }
    else if (str.length > 10) { errorMessage("Set names can be no longer than 10 characters.") }
    else if (MySet.sets.map(_.name) == str) { errorMessage("Set name already in use.") }
    else if (str.length == 0) { errorMessage("Set name must be at least one character.") }
    else {
      MySet.focusedSetIndex match {
        case Some(i) => {
          val oldName = MySet.sets(i).name
          MySet.sets(i).name = str
          actionMessage("Renamed set " + oldName + " to " + str + ".")
        }
        case None => errorMessage("No set selected.")
      }
    }
  }

  setMenu.padding = padding

  def setupSetMenu(shape: scalafx.scene.shape.Shape, name: String) {
    setMenuShape = MySet.createMenuShape(shape)
    setMenu.children(0) = setMenuShape
    nameField.text = name
  }

  setMenu.padding = Insets(25)
  setupSetMenu(MySetWhole.createEmpty().shape, "")

  deleteSetButton.maxWidth = Double.MaxValue
  deleteSetButton.onAction = (e: ActionEvent) => {
    MySet.focusedSetIndex match {
      case Some(i) => {
        actionMessage("Deleted set " + MySet.sets(i).name + ".")
        MySet.deregisterMySet(MySet.sets(i))
        MySet.focusedSetIndex = None
      }
      case None => errorMessage("No set selected.")
    }
  }

  setMenuBorderPane.prefWidth = 100
  setMenuBorderPane.top = setMenu
  setMenuBorderPane.bottom = deleteSetButton
  setMenuBorderPane.background = background
  setMenuBorderPane.border = border

  borderPane.bottom = bottomBox
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
          case Some(pos) => {
            MySet.sets(pos).focused()
            setupSetMenu(MySet.sets(pos).shape, MySet.sets(pos).name)
            actionMessage("Selected set " + MySet.sets(pos).name + ".")
          }
          case None => {
            setupSetMenu(MySetWhole.createEmpty().shape, "")
            actionMessage("")
          }
        }
        oldValue match {
          case Some(pos) => MySet.sets(pos).unfocused()
          case None =>
        }
      })

      newSetButton.onAction = (e: ActionEvent) => {
        val set = MySetWhole()
        MySet.registerMySet(set)
        actionMessage("Created set " + set.name + ".")
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