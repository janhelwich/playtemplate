package controllers

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import javax.imageio.ImageIO
import java.net.URL
import com.sun.image.codec.jpeg.JPEGCodec
import java.awt.geom.AffineTransform
import java.awt.RenderingHints
import java.awt.image.AffineTransformOp
import play.api.mvc._

class HerokuCompatibles extends Controller {

  def herokuCompatibleImageTransformation = Action {
    implicit request =>
      val baos = new ByteArrayOutputStream()
      val another = new ByteArrayOutputStream()
      ImageIO.write(ImageIO.read(new URL("theurl")), "jpg", another)

      val decoder = JPEGCodec.createJPEGDecoder(new ByteArrayInputStream(another.toByteArray))
      val srcImg = decoder.decodeAsBufferedImage()

      val af = AffineTransform.getScaleInstance(1.0, 1.4)
      val rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

      val transform = new AffineTransformOp(af, rh)

      val destImg = transform.createCompatibleDestImage(srcImg, srcImg.getColorModel())
      transform.filter(srcImg, destImg)

      val encoder = JPEGCodec.createJPEGEncoder(baos, JPEGCodec.getDefaultJPEGEncodeParam(destImg))

      encoder.encode(destImg)

      Ok(baos.toByteArray).as("image/jpeg")
  }
}
