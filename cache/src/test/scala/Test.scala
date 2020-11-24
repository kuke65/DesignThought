import akka.http.scaladsl.model.{HttpMethods, HttpRequest}

object Test {

  def main(args: Array[String]): Unit = {
    val request = HttpRequest(HttpMethods.GET, "http://127.0.0.1:8051/redis/regions?_param={\"clientId\":\"posts\", \"clientSecret\":\"dt_FAfQDrefwERw1d3f.2e2Os41fwAfe\"}")

  }

}
