import net.liftweb.json._
import net.liftweb.json.Serialization.write
import net.liftweb.json.JsonDSL._

/**
  * Created by dnezh on 3/29/16.
  */
object LiftJsonTest extends App {
  val p = Person("Dmytro Nezhynskyi", Address("Kyiv", "Ukraine"), Measures(12345, List(1.1,2.2,3.3,3.1415)))

  implicit val formats = DefaultFormats
  val json = write(p)
  println(pretty(render(json)))
}

case class Person(name: String, address: Address, measures: Measures)
case class Address(city: String, country: String)
case class Measures(channel: Long, data: List[Double])