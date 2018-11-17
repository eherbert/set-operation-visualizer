package app

import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color
import scala.collection.mutable.Buffer

class MySet private (
  val name: String,
  val loc: Vec2,
  val radius: Double) {

  var strokeColor = Color.BLACK
  var fillColor = Color.BLACK

  private def cloc(): Vec2 = Vec2(loc.x() + radius, loc.y() + radius)

  def surrounds(other: Vec2): Boolean = cloc.distance(other) < radius

  // finds closest uninterrupted circle segment to selected point, returns points along that segment
  def surroundingPoints(others: Buffer[MySet]): Buffer[Vec2] = {
    def findNextBorderSegment(points: Buffer[Vec2]): (Buffer[Vec2], Buffer[Vec2]) = {
      val containers = others.filter(_.surrounds(points(0)))
      val segment = points.filter(p => others.filter(_.surrounds(p)) == containers)
      val remaining = points -- segment
      (segment, remaining)
    }

    var points = (0.0 until 2.0 by 0.01).toSeq.map(a => {
      val t = a * math.Pi
      val v = Vec2(math.cos(t) * radius + loc.x(), math.sin(t) * radius + loc.y())
      println(v)
      v
    }).toBuffer
    val segments = Buffer[Buffer[Vec2]]()
    while (!points.isEmpty) {
      val (s, r) = findNextBorderSegment(points)
      segments += s
      points = r
    }
    
    println(segments.length)
    
    /*
    segments.foldLeft(segments(0))(s => {
      val avg = (s.map(_._1).sum / s.length, s.map(_._2).sum / s.length)
    })
    * 
    */

    Buffer[Vec2]()
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
    gc.strokeOval(loc.x(), loc.y(), radius * 2, radius * 2)
    gc.fill = fillColor
    gc.fillText(name, loc.x(), loc.y())
  }
  
  override def toString():String = "MySet " + name + ": " + loc
}

object MySet {

  def apply(name: String, x: Double, y: Double, r: Double) = {
    new MySet(name, Vec2(x, y), r)
  }
}