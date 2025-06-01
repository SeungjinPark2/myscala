package example

import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

object CircleSmokeTest extends App {
  val person = Person("Alice", 30)

  // JSON 문자열로 변환
  val jsonString: String = person.asJson.noSpaces
  println(jsonString)
  // 출력: {"name":"Alice","age":30}

  val inputJson = """{"name": "Bob", "age": 25}"""

  val decoded = decode[Person](inputJson) // Either[Error, Person]

  decoded match {
    case Right(p) => println(s"Parsed person: $p")
    case Left(error) => println(s"Failed to parse: $error")
  }
}
