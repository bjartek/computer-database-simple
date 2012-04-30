package models

import java.util.{Date}

import com.novus.salat._
import com.novus.salat.global._
import com.novus.salat.dao._
import com.novus.salat.annotations._
import com.mongodb.casbah.Imports._
import models.SalatImports._

case class Computer(@Key("_id") id: ObjectId= new ObjectId, name: String, introduced: Option[Date], discontinued: Option[Date], companyId: Option[Long])

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
