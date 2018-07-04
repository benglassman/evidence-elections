package model

import scalikejdbc.WrappedResultSet

case class Candidate(id: Long, name: String, party: String)

object Candidate {
  def fromRS(rs: WrappedResultSet): Candidate = {
    Candidate(rs.long("candidateid"), rs.string("name"), rs.string("party"))
  }
//  def fromID(id: BigInt): WrappedResultSet = {
//
//  }
}