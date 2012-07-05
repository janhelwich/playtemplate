package controllers

import play.api._
import libs.ws.WS
import play.api.mvc._
import models.User
import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import javax.imageio.ImageIO
import java.net.URL
import com.sun.image.codec.jpeg.JPEGCodec
import java.awt.geom.AffineTransform
import java.awt.RenderingHints
import java.awt.image.AffineTransformOp
import actors.threadpool.locks.ReentrantReadWriteLock.Sync
import com.codahale.jerkson._

object Application extends Controller {
  val facebookConnectClientUrl = "https://www.facebook.com/dialog/oauth?client_id=263228610445079&redirect_uri=http://janstest.de:9000/facebookauth&scope=email&state=JANSTEST_ARBITRARY_BUT_UNIQUE_STRING_TEMP_TILL_GENERATED_AND_CHECKED"
  val facebookOauthTokenRequestUrl = "https://graph.facebook.com/oauth/access_token?client_id=263228610445079&redirect_uri=http://janstest.de:9000/facebookauth&client_secret=43f277d0887bf5bb1691d7326e0f2053&code="
  val facebookOathTokenParseRegex = "access_token=(.*)\\&expires=".r
  val facebookGraphUrl = "https://graph.facebook.com/me?access_token="

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

  def facebookauth = Action { implicit request =>
    if(request.queryString.contains("code")){
      val code = request.queryString("code").head
      val fbOathResult = WS.url(facebookOauthTokenRequestUrl + code).get().value.get.body
      val matchOfFBAuth = facebookOathTokenParseRegex.findFirstMatchIn(fbOathResult)
      val oauthToken = matchOfFBAuth.get.subgroups(0)
      println(oauthToken)
      val basicInfo = WS.url(facebookGraphUrl + oauthToken).get().value.get.body
      val result = Json.parse[Map[String, String]](basicInfo)
      println(result)
      Redirect("http://janstest.de:9000").withSession(("user", result("name")))
    } else {
      Unauthorized("Please grant access to our app")
    }
  }

  def mobile = Action { implicit request =>
    if (UserAgentOf(request).isMobile)
      Ok(render("mobile.scaml"))
    else
      Ok(render("landing.scaml"))
  }

  def newImage = Action { implicit request =>
    val baos = new ByteArrayOutputStream()
    val another = new ByteArrayOutputStream()
    ImageIO.write(ImageIO.read(new URL("theurl")), "jpg", another)

    val decoder = JPEGCodec.createJPEGDecoder(new ByteArrayInputStream(another.toByteArray))
    val srcImg = decoder.decodeAsBufferedImage()

    val af = AffineTransform.getScaleInstance(1.0, 1.4)
    val rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

    val transform = new AffineTransformOp(af,rh)

    val destImg = transform.createCompatibleDestImage(srcImg, srcImg.getColorModel())
    transform.filter(srcImg, destImg)

    val encoder = JPEGCodec.createJPEGEncoder(baos, JPEGCodec.getDefaultJPEGEncodeParam(destImg))

    encoder.encode(destImg)

    Ok(baos.toByteArray).as("image/jpeg")
  }
}