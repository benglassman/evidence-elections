package model
import scalikejdbc.WrappedResultSet

case class Race(id: BigInt, raceType: String, state: String, Candidate1: Candidate, Candidate2: Candidate)

object Race {
  def fromRS(rs: WrappedResultSet): Race = {
    Race(rs.bigInt("raceid"),rs.string("raceType"),rs.string("statename"),
      Candidate(rs.bigInt("candidate1id"), rs.string("c1name"),rs.string("c1party")),
      Candidate(rs.bigInt("candidate2id"), rs.string("c2name"),rs.string("c2party")))
  }
}

