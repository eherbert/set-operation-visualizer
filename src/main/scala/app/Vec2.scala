package app

class Vec2 private (private var _x: Double, private var _y: Double) {
  def x(): Double = _x
  def y(): Double = _y

  def +=(other: Vec2) {
    _x += other._x
    _y += other._y
  }

  def -(other: Vec2): Vec2 = new Vec2(_x - other._x, _y - other._y)

  def distance(other: Vec2): Double = math.sqrt(math.pow(_x - other._x, 2) + math.pow(_y - other._y, 2))
  
  override def toString():String = "Vec2 (" + x + ", " + y + ")"
}

object Vec2 {
  def apply(): Vec2 = new Vec2(-1.0, -1.0)
  def apply(x: Double, y: Double): Vec2 = new Vec2(x, y)
}