package controllers

import play.api._
import libs.ws.WS
import play.api.mvc._
import com.codahale.jerkson._

trait FacebookAuth extends Controller{
  val facebookClientId:String
  val facebookClientSecret:String
  val uriToRedirectFromFacebook:String
  lazy val facebookConnectClientUrl = "https://www.facebook.com/dialog/oauth?client_id="+facebookClientId+"&redirect_uri="+uriToRedirectFromFacebook+"&scope=email&state=JANSTEST_ARBITRARY_BUT_UNIQUE_STRING_TEMP_TILL_GENERATED_AND_CHECKED"
  lazy val facebookOauthTokenRequestUrl = "https://graph.facebook.com/oauth/access_token?client_id="+facebookClientId+"&redirect_uri="+uriToRedirectFromFacebook+"&client_secret="+facebookClientSecret+"&code="
  lazy val facebookOauthTokenParseRegex = "access_token=(.*)\\&expires=".r
  lazy val facebookGraphUrl = "https://graph.facebook.com/me?access_token="

  def facebookauth = Action { implicit request =>
    if(request.queryString.contains("code")){
      val code = request.queryString("code").head
      val fbOathResult = WS.url(facebookOauthTokenRequestUrl + code).get().value.get.body
      val matchOfFBAuth = facebookOauthTokenParseRegex.findFirstMatchIn(fbOathResult)
      val oauthToken = matchOfFBAuth.get.subgroups(0)
      val basicInfo = WS.url(facebookGraphUrl + oauthToken).get().value.get.body
      val result = Json.parse[Map[String, String]](basicInfo)
      Redirect("http://janstest.de:9000").withSession(("user", result("name")))
    } else {
      Unauthorized("Please grant access to our app via facebook")
    }
  }
}

object Application extends Controller with FacebookAuth{
  val uriToRedirectFromFacebook = "http://janstest.de:9000/facebookauth"
  val facebookClientSecret = "43f277d0887bf5bb1691d7326e0f2053"
  val facebookClientId = "263228610445079"

  def index() = Action { implicit request =>
    if(session.get("user").isEmpty){
      Redirect(facebookConnectClientUrl)
    } else {
      Ok(render("landing.scaml", ('facebookUrl, facebookConnectClientUrl), ('name, session.get("user").get))).withNewSession
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