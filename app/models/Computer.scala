package models

import java.util.{Date}
import play.api.Play.current
import com.novus.salat._
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._

case class Computer(
  id: ObjectId = new ObjectId, 
  name :String, 
  introduced: Option[Date] = None, 
  discontinued: Option[Date] = None, 
  @Key("company_id") companyId: Option[ObjectId] = None
)

object Computer extends ModelCompanion[Computer, ObjectId] {
  val collection = mongoCollection("computers")
  val dao = new SalatDAO[Computer, ObjectId](collection = collection) {}

  def list(filter: String = ""): List[(Computer, Option[Company])] = {
    val where = if(filter == "") MongoDBObject.empty else MongoDBObject("name" ->(""".*"""+ filter +""".*""").r)
    find(where).map(withCompany).toList
   }

  def withCompany(computer:Computer) = {
    val company = computer.companyId.flatMap(id => Company.findOneByID(id))
   (computer, company)
  }
}

// vim: set ts=2 sw=2 et:
