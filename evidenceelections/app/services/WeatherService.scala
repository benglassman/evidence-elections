package services

import play.libs.ws.WSClient
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class WeatherService(ws: WSClient) {
  def getTemperature(lat: Double, lon: Double): Future[Double] = {
    val temperature = Future(98.7)
    temperature
  }
}
