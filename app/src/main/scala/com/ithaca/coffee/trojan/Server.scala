package com.ithaca.coffee.trojan

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.PredefinedToEntityMarshallers
import akka.http.scaladsl.model.MediaTypes._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.{ActorFlowMaterializer, FlowMaterializer}

import scala.io.Source
import scala.util.{Failure, Success, Try}

object Server extends App with Service {
  override implicit val system = ActorSystem()
  override implicit val materializer = ActorFlowMaterializer()

  Http().bindAndHandle(routes, "0.0.0.0", 8080)
}

trait Marshallers {
  implicit val marshaller = PredefinedToEntityMarshallers.stringMarshaller(`text/html`)
}

trait Rejections {
  implicit val rejections = RejectionHandler
    .newBuilder()
    .handle {
      case FileNotFound(file) => complete {
        HttpResponse(NotFound, entity = s"File with path $file not found on server")
      }
    }.result()
}

case class FileNotFound(file: String) extends Rejection

trait Service extends Marshallers with Rejections {
  implicit val system: ActorSystem
  implicit val materializer: FlowMaterializer

  val routes = {
    logRequest("trojan-gate") {
      pathPrefix("app") {
        (get & path(Segments)) { file =>
          val path = file mkString "/"
          Try {
            Source.fromFile(s"app/src/main/web/$path").mkString
          } match {
            case Success(content) => complete { content }
            case Failure(_) => reject { FileNotFound(path) }
          }
        }
      }
    }
  }

}
