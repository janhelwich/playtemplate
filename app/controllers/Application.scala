package controllers

import play.api._
import libs.ws.WS
import play.api.mvc._
import com.codahale.jerkson._
import collection.immutable.ListMap
import scala.Predef._

trait FacebookAuth extends Controller{
  val facebookClientId:String
  val facebookClientSecret:String
  val uriToRedirectFromFacebook:String
  val scopes:String

  lazy val facebookConnectClientUrl = "https://www.facebook.com/dialog/oauth?client_id="+facebookClientId+"&redirect_uri="+uriToRedirectFromFacebook+"&scope="+scopes+"&state=JANSTEST_ARBITRARY_BUT_UNIQUE_STRING_TEMP_TILL_GENERATED_AND_CHECKED"
  lazy val facebookOauthTokenRequestUrl = "https://graph.facebook.com/oauth/access_token?client_id="+facebookClientId+"&redirect_uri="+uriToRedirectFromFacebook+"&client_secret="+facebookClientSecret+"&code="
  lazy val facebookOauthTokenParseRegex = "access_token=(.*)\\&expires=".r
  lazy val facebookGraphUrl = "https://graph.facebook.com/me?access_token="

  //TODO scope params

  def callFBforOauthTokenImmediatly(codeFromFB: String): String = {
    val fbOathResult = WS.url(facebookOauthTokenRequestUrl + codeFromFB).get.value.get.body
    val matchOfFBAuth = facebookOauthTokenParseRegex.findFirstMatchIn(fbOathResult)
    matchOfFBAuth.get.subgroups(0)
  }

  case class FacebookAuthResult(val token:String){
    lazy val graphResult = {
      val basicInfo = WS.url(facebookGraphUrl + token).get.value.get.body
      Json.parse[Map[String, String]](basicInfo)
    }
  }

  def onSuccess(callback: FacebookAuthResult  => Result): FacebookAuthFailImpl = {
    new FacebookAuthFailImpl(callback)
  }

  class FacebookAuthFailImpl(val callback: FacebookAuthResult  => Result){
    def onFail(onFail: => Result): Action[AnyContent] = Action { implicit request =>
      if(request.queryString.contains("code")){
        val codeFromFB = request.queryString("code").head
        val oauthToken = callFBforOauthTokenImmediatly(codeFromFB)
        callback(new FacebookAuthResult(oauthToken))
      } else {
        onFail
      }
    }
  }

}

object Application extends Controller with FacebookAuth {
  val uriToRedirectFromFacebook = "http://janstest.de:9000/facebookauth"
  val facebookClientSecret = "43f277d0887bf5bb1691d7326e0f2053"
  val facebookClientId = "263228610445079"
  val scopes = "email,user_activities"

  def index() = Action { implicit request =>
    if(session.get("user").isEmpty){
      Redirect(facebookConnectClientUrl)
    } else {
      Ok(render("landing.scaml", ('facebookUrl, facebookConnectClientUrl), ('name, session.get("user").get))).withNewSession
    }
  }

  def facebookauth = onSuccess{ result =>
    println("YEAAAAHHH " + result.token)
    Redirect("http://janstest.de:9000").withSession(("user", result.graphResult("name")))
  } onFail (
    Unauthorized("Please grant access to our app via facebook")
  )

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