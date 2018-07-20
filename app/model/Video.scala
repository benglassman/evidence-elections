package model
import scalikejdbc.WrappedResultSet

case class Video(id: Long, raceId: Long, youTubeId: String, candidateId: Long)

object Video {
  def fromRS(rs: WrappedResultSet): Video = {
    Video(rs.long("id"), rs.long("raceId"), rs.string("youTubeId"),
      rs.long("candidateId"))
  }
}




