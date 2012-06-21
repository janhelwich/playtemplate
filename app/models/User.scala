package models

import com.mongodb.casbah.Imports._

case class User(val name:String)

object User extends MongoDao[User]{
  override val dbname = "optionaldbname"

  def find(x:String) = {
    dao.find(User(x)).toList.head
  }

  def findAll = dao.find(MongoDBObject.empty).toList

  def create(x:String) = {
    val e = User(x)
    dao.save(e)
    e
  }
}
