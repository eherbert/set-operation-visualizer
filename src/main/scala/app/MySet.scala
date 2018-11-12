package app

import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color

class MySet private (
  name: String,
  var x: Double,
  var y: Double,
  var r: Double) {
  
  var strokeColor = Color.BLACK

  def += (a: Double, b:Double) {
    x += a
    y += b
  }
  
  def surrounds(a: Double, b: Double): Boolean = {
    val c = math.sqrt(math.pow(a - (x+r), 2) + math.pow(b - (y+r), 2))
    c < r
  }

  def selected() {
    strokeColor = Color.GREEN
  }
  
  def deselected() {
    strokeColor = Color.BLACK
  }
  
  def render(gc: GraphicsContext) {
    gc.stroke = strokeColor
    gc.strokeOval(x, y, r*2, r*2)
  }
}

object MySet {
  var nextName = "A"

  def apply(x: Double, y: Double, r:Double) = {
    val set = new MySet(nextName, x, y, r)
    set
  }
}