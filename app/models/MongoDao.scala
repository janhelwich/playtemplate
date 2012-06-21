package models

import play.api.Play
import java.net.URI
import com.mongodb.casbah.MongoConnection
import com.mongodb.casbah.Imports._
import play.api.Play.current
import scala.Some
import com.novus.salat.global._


trait MongoDao {
  val dbname:String = "default"
  lazy val db = {
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

  implicit val ctxToAvoidImportOfSalatGlobal: com.novus.salat.Context = ctx

  def collection(name:String) = db.getCollection(name).asScala
}