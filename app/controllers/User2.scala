package controllers

import javax.inject.Inject
import play.api.cache._
import play.api.libs.json._
import play.api.mvc.{AbstractController, Action, _}


class User2 @Inject() (components: ControllerComponents, cache: SyncCacheApi) extends AbstractController(components) {
  def AuthenticatedAction(f: Request[AnyContent] => Result): Action[AnyContent] = {
    Action { request =>
      (request.session.get("idToken").flatMap { idToken =>
        cache.get[JsValue](idToken + "profile")
      } map { profile =>
        f(request)
      }).orElse {
        Some(Redirect(routes.Application.index()))
      }.get
    }
  }

  def index = AuthenticatedAction { request =>
    val idToken = request.session.get("idToken").get
    val profile = cache.get[JsValue](idToken + "profile").get
    Ok(views.html.user(profile))
  }
}