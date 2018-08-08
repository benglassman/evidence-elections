package model
import scalikejdbc._
import play.api.libs.json.Json

case class Race(id: Long, raceType: String, state: String, Candidate1: Candidate, Candidate2: Candidate)

object Race {
  implicit val writes = Json.writes[Race]
  def fromRS(rs: WrappedResultSet): Race = {
    Race(rs.long("raceid"), rs.string("raceType"), rs.string("statename"),
      Candidate(rs.long("candidate1id"), rs.string("c1name"), rs.string("c1party")),
      Candidate(rs.long("candidate2id"), rs.string("c2name"), rs.string("c2party")))
  }
  def racesList: List[Race] = DB.readOnly { implicit session =>
    val race = sql"""
                      select race.raceid, race.raceType, state.statename, race.candidate1id, c1.name as c1name, c1.party as c1party, race.candidate2id, c2.name as c2name, c2.party as c2party
                      from elections.race
                      left join elections.state
                      on race.state = state.stateid
                      left join elections.candidates c1
                      on race.candidate1id = c1.candidateid
                      left join elections.candidates c2
                      on race.candidate2id=c2.candidateid
                      order by race.raceid asc
                      limit 10
        """
      .map(Race.fromRS).list().apply()
    race
  }
}

