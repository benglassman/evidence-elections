package model
import scalikejdbc.WrappedResultSet

case class Race(id: Long, raceType: String, state: String, Candidate1: Candidate, Candidate2: Candidate)

object Race {
  def fromRS(rs: WrappedResultSet): Race = {
    Race(rs.long("raceid"), rs.string("raceType"), rs.string("statename"),
      Candidate(rs.long("candidate1id"), rs.string("c1name"), rs.string("c1party")),
      Candidate(rs.long("candidate2id"), rs.string("c2name"), rs.string("c2party")))
  }
}

