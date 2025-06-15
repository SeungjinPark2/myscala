package crudexample

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.{Directives, Route}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport.*
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

object QuickStartServer {
  private final case class ItemsResponse(result: List[Item])

  private trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
    implicit val itemFormat: RootJsonFormat[Item] = jsonFormat3(Item.apply)
    implicit val itemListFormat: RootJsonFormat[ItemsResponse] = jsonFormat1(ItemsResponse.apply)
  }

  private object Routes extends Directives with JsonSupport {
    // 라우팅 정의
    val routes: Route =
      pathPrefix("items") {
        concat(
          // 전체 조회
          get {
            complete(ItemsResponse(ItemRepository.all()))
          },
          // 생성
          post {
            entity(as[Item]) { item =>
              val created = ItemRepository.create(item.name, item.description)
              complete((StatusCodes.Created, created))
            }
          },
          path(IntNumber) { id =>
            concat(
              // 단일 조회
              get {
                rejectEmptyResponse {
                  complete(ItemRepository.get(id))
                }
              },
              // 수정
              put {
                entity(as[Item]) { item =>
                  ItemRepository.update(id, item.name, item.description) match {
                    case Some(updated) => complete(updated)
                    case None => complete(StatusCodes.NotFound)
                  }
                }
              },
              // 삭제
              delete {
                if (ItemRepository.delete(id)) complete(StatusCodes.NoContent)
                else complete(StatusCodes.NotFound)
              }
            )
          }
        )
      }
  }

  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem[Any] = ActorSystem(Behaviors.empty, "AkkaHttpCrudServer")
    implicit val executionContext: ExecutionContextExecutor = system.executionContext

    val bindingFuture = Http().newServerAt("localhost", 8080).bind(Routes.routes)

    bindingFuture.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        println(s"Server online at http://${address.getHostString}:${address.getPort}/")
      case Failure(ex) =>
        println(s"Failed to bind HTTP server: ${ex.getMessage}")
        system.terminate()
    }
  }
}
