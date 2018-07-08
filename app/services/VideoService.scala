package services

import com.sun.xml.internal.bind.v2.TODO
import scalikejdbc._

class VideoService {

  import com.google.api.services.youtube.{YouTube, YouTubeRequestInitializer}
  import com.google.api.services.youtube.model.{Video => GVideo}
  import com.google.api.client.json.jackson2.JacksonFactory
  import com.google.api.client.http.javanet.NetHttpTransport
  import com.google.api.client.http.HttpRequest
  import com.google.api.client.http.HttpRequestInitializer
  import scala.collection.JavaConverters._
  import model.Video

  //  init google api access
  val transport = new NetHttpTransport()
  val factory = new JacksonFactory()
  val httpRequestInit = new HttpRequestInitializer {
    override def initialize(re: HttpRequest) = {}
  }

  val service = new YouTube.Builder(transport, factory, httpRequestInit).setApplicationName("evidence-elections")
    .setYouTubeRequestInitializer(new YouTubeRequestInitializer("AIzaSyDD-19LW5F1rpYhqx1-C-N29w2tAcfFCos")).build()

  // get details of a video with id
  def getVideoInfo(id: String): GVideo = {
    val videoResponse = service.videos().list("snippet").setId(id).execute()
    // videoResponse contains list of items. We can access first result using

    val videoList: List[GVideo] = videoResponse.getItems().asScala.toList
    val video = videoList(0)
    video
  }

  def getIdsByRace(raceId: Long): List[String] = {
    val ids: List[String] = DB.readOnly { implicit session =>
      val videos =
        sql"""
                      select *
                      from elections.video
                      where raceId= $raceId
        """
          .map(Video.fromRS).list().apply()
      videos.map(video=>video.youTubeId)
    }
    ids
  }
}
