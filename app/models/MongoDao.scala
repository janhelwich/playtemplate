package models

import play.api.Play
import java.net.URI
import com.mongodb.casbah.MongoConnection
import com.novus.salat.dao.SalatDAO
import com.mongodb.casbah.Imports._
import scala.Some

trait MongoDao[E] {
  val dbname:String = "default"
  val db = {
    Play.configuration.getString("mongo.uri") match {
      case Some(uriTxt) => {
        val uri = new URI(uriTxt)
        val db = MongoConnection(uri.getHost, uri.getPort).getDB(uri.getPath.replace("/", ""))
        val user_pwd = uri.getUserInfo.split(":")
        if (!db.authenticate(user_pwd(0), user_pwd(1))) throw new Exception("No authorization in mongoDb")
        else db
      }
      case _ => MongoConnection()(dbname)
    }
  }
  val dao = new SalatDAO[E, ObjectId](collection = db.getCollection("pics").asScala) {}
}