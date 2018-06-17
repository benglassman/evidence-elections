package model

import scalikejdbc.WrappedResultSet

case class Candidate(id: BigInt,name: String, party: String)

object Candidate {
  def fromRS(rs: WrappedResultSet): Candidate = {
    Candidate(rs.bigInt("candidateid"),rs.string("name"), rs.string("party"))
  }
//  def fromID(id: BigInt): WrappedResultSet = {
//
//  }
}