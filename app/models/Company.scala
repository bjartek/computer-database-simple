package models

import play.api.Play.current
import com.novus.salat._
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._

case class Company(
  id: ObjectId = new ObjectId, 
  name: String
)

object Company extends ModelCompanion[Company, ObjectId] {
  val collection = mongoCollection("company")
  val dao = new SalatDAO[Company, ObjectId](collection = collection) {}

  def options: Seq[(String,String)] = {
    find(MongoDBObject.empty).map(c => (c.id.toString, c.name)).toSeq
  }
}

// vim: set ts=2 sw=2 et:
