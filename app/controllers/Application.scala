package controllers

import play.api._
import play.api.mvc._
import models.User
import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import javax.imageio.ImageIO
import java.net.URL
import com.sun.image.codec.jpeg.JPEGCodec
import java.awt.geom.AffineTransform
import java.awt.RenderingHints
import java.awt.image.AffineTransformOp

object Application extends Controller {
  
  def index = Action {
//    Ok(views.html.index("Your new application is ready."))
    User.create("jan")
    Ok(render("landing.scaml"))
  }

  def map = Action {
    Ok( render("map.scaml"))
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