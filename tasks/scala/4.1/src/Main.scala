object Main {
  def main(args: Array[String]): Unit = {
    val v1 = Vector3D(1, 2, 3)
    val v2 = Vector3D(4, 5, 6)
    println(s"v1 + v2 = ${v1.addition(v2)}")
    println(s"v1 - v2 = ${v1.substraction(v2)}")
    println(s"v1 * 4 = ${v1.multiplication(4)}")
    println(s"v1 * v2 = ${v1.multiplicationScalar(v2)}")
    println(s"v1 Transpose: ${v1.transpose()}")
  }
}

case class Vector3D(x:Double, y:Double, z:Double){
  def addition(second: Vector3D): Vector3D = Vector3D(this.x + second.x, this.y + second.y, this.z + second.z)

  def substraction(second: Vector3D): Vector3D = Vector3D(this.x - second.x, this.y - second.y, this.z - second.z)

  def multiplication(scalar: Int): Vector3D = Vector3D(this.x * scalar, this.y * scalar, this.z * scalar)

  def multiplicationScalar(second: Vector3D): Vector3D = Vector3D(this.x * second.x, this.y * second.y, this.z * second.z)

  def transpose(): List[Double] = List (x,y,z)

}