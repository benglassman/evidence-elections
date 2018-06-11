package services

import java.security.MessageDigest
import java.util.UUID
import java.util.concurrent.TimeUnit

import model.User
import org.apache.commons.codec.binary.Base64
import org.mindrot.jbcrypt.BCrypt
import play.api.mvc.{Cookie, RequestHeader}
import play.api.cache.SyncCacheApi
import scalikejdbc._

import scala.concurrent.duration.Duration

class AuthService(cacheApi: SyncCacheApi) {
  val mda = MessageDigest.getInstance("SHA-512")
  val cookieHeader = "X-Auth-Token"

  def login(user_name: String, password: String): Option[Cookie] = {
    for {
      user <- checkUser(user_name, password)
      cookie <- Some(createCookie(user))
    } yield {
      cookie
    }
  }

  private def checkUser(user_name: String, password: String): Option[User] = {
    DB.readOnly { implicit session =>
      val maybeUser = sql"select * from user where user_name = $user_name". map(User.fromRS).single().apply()
      maybeUser.flatMap { user =>
        if (BCrypt.checkpw(password, user.password)) {
          Some(user)
        } else None
      }
    }
  }

  private def createCookie(user: User): Cookie = {
    val randomPart = UUID.randomUUID().toString.toUpperCase
    val userPart = user.id.toString.toUpperCase
    val key = s"$randomPart|$userPart"
    val token = Base64.encodeBase64String(mda.digest(key.getBytes))
    val duration = Duration.create(10, TimeUnit.HOURS)
    cacheApi.set(token, user, duration)
    Cookie(cookieHeader, token, maxAge = Some(duration.toSeconds.toInt))
  }

  def checkCookie(header: RequestHeader): Option[User] = {
    for {
      cookie <- header.cookies.get(cookieHeader)
      user <- cacheApi.get[User](cookie.value)
    } yield {
      user
    }
  }

}
