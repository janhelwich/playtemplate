package controllers

import auth.FacebookAuth
import play.api._
import mvc._
import scalate.render
import useragent.UserAgentOf

object Application extends Controller with FacebookAuth {
  val uriToRedirectFromFacebook = "http://janstest.de:9000/facebookauth"
  val facebookClientSecret = "43f277d0887bf5bb1691d7326e0f2053"
  val facebookClientId = "263228610445079"
  val scopes = "email,user_activities"

  def index() = Action { implicit request =>
    Ok(render("landing.scaml", ('facebookUrl -> facebookConnectClientUrl), ('name -> session.get("username").getOrElse(""))))
  }

  def facebookauth = authenticate{ result =>
    result match {
      case success: Success => {
        Redirect("http://janstest.de:9000").withSession(("username", success.fbGraphProperty("name")))
      }
      case fail: Fail => Unauthorized("Please grant access to our app via facebook")
    }
  }

  def map = Action {
    Ok(render("map.scaml"))
  }

  def mobile = Action { implicit request =>
    if (UserAgentOf(request).isMobile)
      Ok(render("mobile.scaml"))
    else
      Ok(render("landing.scaml"))
  }

}