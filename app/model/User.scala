package model

import scalikejdbc.WrappedResultSet

case class User(id: Long, user_name: String, password: String)

object User {
  def fromRS(rs: WrappedResultSet): User = {
    User(rs.long("id"),rs.string("user_name"),rs.string("password"))
  }
}