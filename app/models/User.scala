package models

import com.mongodb.casbah.Imports._
import com.novus.salat.dao.SalatDAO

case class User(val name:String, _id: ObjectId = new ObjectId)

object User extends MongoDao{
  override val dbname = "optionaldbname"
  val dao = new SalatDAO[User, ObjectId](collection("users")){}

  def find(x:String) = {
    dao.find(MongoDBObject.empty).toList.head
  }

  def findAll = dao.find(MongoDBObject.empty).toList

  def create(x:String) = {
    val e = User(x)
    dao.save(e)
    e
  }
}
