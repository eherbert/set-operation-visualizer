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
import scalafx.scene.control.TextArea
import scalafx.scene.control.Tooltip

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
  val newSetButtonTooltip = new Tooltip()
  val newSetButton = new Button("New Set")
  val selectNewSetButtonTooltip = new Tooltip()
  val selectNewSetButton = new Button("Select New Set")
  val controlsMenu = new HBox(25, newSetButton, selectNewSetButton)
  val bottomBox = new VBox(0, messageLabelBox, controlsMenu)
  var setMenuShape = MySet.createMenuShape(new Circle())
  val nameFieldTooltip = new Tooltip()
  val nameField = new TextField()
  val membersFieldTooltip = new Tooltip()
  val membersField = new TextField()
  val setMenu = new VBox(25, setMenuShape.delegate, nameField, membersField)
  val confirmSelectionButton = new Button("Confirm Selection")
  val deleteSelectionButton = new Button("Delete Selection")
  val deleteSetButtonTooltip = new Tooltip()
  val deleteSetButton = new Button("Delete Set")
  val buttonMenu = new VBox(0, confirmSelectionButton, deleteSelectionButton, deleteSetButton)
  val setMenuBorderPane = new BorderPane
  val borderPane = new BorderPane

  val padding = Insets(25)
  val background = new Background(Array(new BackgroundFill(Color.LightGrey, null, null)))
  val border = new javafx.scene.layout.Border(new javafx.scene.layout.BorderStroke(
    Color.BLACK,
    BorderStrokeStyle.Solid,
    CornerRadii.Empty,
    BorderWidths.Default))

  messageLabel.text = "Hello world! Hover over buttons or fields to view tooltips."
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

  newSetButtonTooltip.text = "Add a new set to the work area."

  newSetButton.tooltip = newSetButtonTooltip
  newSetButton.onAction = (e: ActionEvent) => {
    val set = MySetWhole()
    MySet.registerMySet(set)
    actionMessage("Created set " + set.name + ".")
  }

  selectNewSetButtonTooltip.text = "Add a new set by selecting in the work area."

  selectNewSetButton.tooltip = selectNewSetButtonTooltip
  selectNewSetButton.onAction = (e: ActionEvent) => {
    MySet.sets.foreach(_.setUserSelectionBehavior())
    controlsMenu.disable = true
    deleteSetButton.disable = true
    confirmSelectionButton.disable = false
    deleteSelectionButton.disable = false
  }

  controlsMenu.background = background
  controlsMenu.padding = padding

  nameFieldTooltip.text = "Names can contain only upper letters.\nNames must be between one and 10 characters in length.\nNames should not be in use in the work area."

  nameField.promptText = "Set name."
  nameField.tooltip = nameFieldTooltip
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
          MySet.sets(i).setName(str)
          actionMessage("Renamed set " + oldName + " to " + str + ".")
        }
        case None => errorMessage("No set selected.")
      }
    }
  }

  membersFieldTooltip.text = "Valid set formats include {1,2,3,4}, {{1,2},{3,4}}, and {(1,2),(3,4)}.\nSets cannot contain more than 100 members.\nSets can only be one additional nested set level or one pair level deep."

  membersField.promptText = "Set members."
  membersField.tooltip = membersFieldTooltip
  //membersArea.wrapText = true

  def setupSetMenu(shape: scalafx.scene.shape.Shape, name: String) {
    setMenuShape = MySet.createMenuShape(shape)
    setMenu.children(0) = setMenuShape
    nameField.text = name
  }

  setMenu.padding = padding
  setMenu.minWidth = 150
  setupSetMenu(MySetWhole.createEmpty().shape, "")

  confirmSelectionButton.maxWidth = Double.MaxValue
  confirmSelectionButton.disable = true
  confirmSelectionButton.onAction = (e: ActionEvent) => {
    controlsMenu.disable = false
    deleteSetButton.disable = false
    confirmSelectionButton.disable = true
    deleteSelectionButton.disable = true
    MySet.selectionBuilder match {
      case Some(sb) => {
        MySet.deregisterMySet(sb)
        val finalSet = MySetOrganic(sb.shape)
        MySet.registerMySet(finalSet)
        MySet.selectionBuilder = None
      }
      case None =>
    }
    MySet.sets.foreach(_.setDefaultBehavior())
  }

  deleteSelectionButton.maxWidth = Double.MaxValue
  deleteSelectionButton.disable = true
  deleteSelectionButton.onAction = (e: ActionEvent) => {
    controlsMenu.disable = false
    deleteSetButton.disable = false
    confirmSelectionButton.disable = true
    deleteSelectionButton.disable = true
    MySet.selectionBuilder match {
      case Some(sb) => {
        MySet.deregisterMySet(sb)
        MySet.selectionBuilder = None
      }
      case None =>
    }
    MySet.sets.foreach(_.setDefaultBehavior())
  }

  deleteSetButtonTooltip.text = "Delete a set from the work area."

  deleteSetButton.maxWidth = Double.MaxValue
  deleteSetButton.tooltip = deleteSetButtonTooltip
  deleteSetButton.onAction = (e: ActionEvent) => {
    MySet.focusedSetIndex match {
      case Some(i) => {
        actionMessage("Deleted set " + MySet.sets(i).name + ".")
        // have to change focused set before deletion to avoid out of bounds error
        MySet.changeFocusedSet(None)
        MySet.deregisterMySet(MySet.sets(i))
      }
      case None => errorMessage("No set selected.")
    }
  }

  setMenuBorderPane.prefWidth = 100
  setMenuBorderPane.top = setMenu
  setMenuBorderPane.bottom = buttonMenu
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
          // if new value is valid
          case Some(pos) => {
            setupSetMenu(MySet.sets(pos).shape, MySet.sets(pos).name)
            actionMessage("Selected set " + MySet.sets(pos).name + ".")
          }
          // if new value is not valid
          case None => {
            setupSetMenu(MySetWhole.createEmpty().shape, "")
          }
        }
      })
    }
  }
}