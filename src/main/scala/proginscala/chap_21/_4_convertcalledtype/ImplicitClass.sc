case class Rectangle(width: Int, height: Int)

implicit class RectangleMaker(width: Int) {
  def x(height: Int) = Rectangle(width, height)

  // 자동으로 생성된 변환 함수
  implicit def RectangleMaker(width: Int) = new RectangleMaker(width)
}

val myRectangle = 3 x 4

