package controllers.auth

import play.api.mvc.{Action, Result, Controller}
import play.api.libs.ws.WS
import com.codahale.jerkson.Json

trait FacebookAuth extends Controller {
  val facebookClientId: String
  val facebookClientSecret: String
  val uriToRedirectFromFacebook: String
  val scopes: String

  lazy val facebookConnectClientUrl = "https://www.facebook.com/dialog/oauth?client_id=" + facebookClientId + "&redirect_uri=" + uriToRedirectFromFacebook + "&scope=" + scopes + "&state=JANSTEST_ARBITRARY_BUT_UNIQUE_STRING_TEMP_TILL_GENERATED_AND_CHECKED"
  lazy val facebookOauthTokenRequestUrl = "https://graph.facebook.com/oauth/access_token?client_id=" + facebookClientId + "&redirect_uri=" + uriToRedirectFromFacebook + "&client_secret=" + facebookClientSecret + "&code="
  lazy val facebookOauthTokenParseRegex = "access_token=(.*)\\&expires=".r
  lazy val facebookGraphUrl = "https://graph.facebook.com/me?access_token="

  def callFBforOauthTokenImmediatly(codeFromFB: String): String = {
    val fbOathResult = WS.url(facebookOauthTokenRequestUrl + codeFromFB).get.value.get.body
    val matchOfFBAuth = facebookOauthTokenParseRegex.findFirstMatchIn(fbOathResult)
    matchOfFBAuth.get.subgroups(0)
  }

  class AuthResult()

  case class Success(val token: String) extends AuthResult() {
    lazy val graphResult = {
      val basicInfo = WS.url(facebookGraphUrl + token).get.value.get.body
      Json.parse[Map[String, String]](basicInfo)
    }
  }

  case class Fail() extends AuthResult()

  def authenticate(callback: AuthResult => Result) = Action {
    implicit request =>
      if (request.queryString.contains("code")) {
        val codeFromFB = request.queryString("code").head
        val oauthToken = callFBforOauthTokenImmediatly(codeFromFB)
        callback(Success(oauthToken))
      } else {
        callback(Fail())
      }
  }

}
