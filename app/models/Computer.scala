package models

import java.util.{Date}

import com.novus.salat._
import com.novus.salat.global._
import com.novus.salat.dao._
import com.novus.salat.annotations._
import com.mongodb.casbah.Imports._
import models.SalatImports._

case class Computer(
  @Key("_id") id: ObjectId= new ObjectId, 
  name: String, 
  introduced: Option[Date], 
  discontinued: Option[Date], 
  companyId: Option[ObjectId])

object Computer {
  def fromForm(id:ObjectId, name:String, introduced:Option[Date], discontinued:Option[Date], company: Option[String]) :Computer ={
    Computer(new ObjectId(), name, introduced, discontinued, company.map(c => new ObjectId(c))) 
  }

  def toForm(computer: Computer) = {
    Some((computer.id, computer.name, computer.introduced, computer.discontinued, computer.companyId.map(c => c.toString)))
  }
}


object ComputerDAO extends SalatDAO[Computer, ObjectId](collection = mongoCollection("computers")) {


  def list(filter: String = ""): List[(Computer, Option[Company])] = {
    val where = if(filter == "")  MongoDBObject.empty else MongoDBObject( "name" -> (""".*""").r)
    val computers = find(where).toSeq

    computers.map{ computer => 
      val company = computer.companyId.flatMap( id => CompanyDAO.find(id))
      (computer, company)
    }
  }

}
