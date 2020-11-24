import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.{Http, HttpExt}
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}

import scala.concurrent.Future
import scala.util.{Failure, Success}

object HttpClientSingleRequest {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem(Behaviors.empty, "SingleRequest")
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.executionContext
    implicit val classicSystem = system.classicSystem

    var i = 0
    val ext: HttpExt = Http()
    while (i < 32768) {

      val responseFuture: Future[HttpResponse] = ext.singleRequest(HttpRequest(uri = "http://127.0.0.1:8051/redis/regions?_param={%22clientId%22:%22posts%22,%20%22clientSecret%22:%22dt_FAfQDrefwERw1d3f.2e2Os41fwAfe%22}"))

      responseFuture
        .onComplete {
          case Success(res) =>
          case Failure(a) => println(a); sys.error("something wrong")
        }
      i += 1
    }

  }

}
