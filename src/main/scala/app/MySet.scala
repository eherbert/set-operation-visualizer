package app

import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color

class MySet private (
  val name: String,
  var x: Double,
  var y: Double,
  var r: Double) {
  
  var strokeColor = Color.BLACK
  var fillColor = Color.BLACK

  private def cx():Double = x+r
  private def cy():Double = y+r
  
  def += (a: Double, b:Double) {
    x += a
    y += b
  }
  
  def surrounds(a: Double, b: Double): Boolean = {
    val c = math.sqrt(math.pow(a - cx(), 2) + math.pow(b - cy(), 2))
    c < r
  }

  def selected() {
    strokeColor = Color.GREEN
    fillColor = Color.GREEN
  }
  
  def deselected() {
    strokeColor = Color.BLACK
    fillColor = Color.BLACK
  }
  
  def render(gc: GraphicsContext) {
    gc.stroke = strokeColor
    gc.strokeOval(x, y, r*2, r*2)
    gc.fill = fillColor
    gc.fillText(name, x, y)
  }
}

object MySet {
  
  def apply(name:String, x: Double, y: Double, r:Double) = {
    new MySet(name, x, y, r)
  }
}