package model

import scalikejdbc.WrappedResultSet

case class User(id: BigInt, user_name: String, password: String)

object User {
  def fromRS(rs: WrappedResultSet): User = {
    User(rs.bigInt("id"),rs.string("user_name"),rs.string("password"))
  }
}