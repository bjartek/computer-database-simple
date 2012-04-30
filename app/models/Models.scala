package models

import java.util.{Date}

import com.novus.salat._
import com.novus.salat.global._
import com.novus.salat.dao._
import com.novus.salat.annotations._
import com.mongodb.casbah.Imports._
import models.SalatImports._

case class Company(@Key("_id") id: ObjectId = new ObjectId, name: String)
case class Computer(@Key("_id") id: ObjectId = new ObjectId, name: String, introduced: Option[Date] = None, discontinued: Option[Date] = None, @Key("company_id") companyId: Option[ObjectId] = None) 


object Computer {
  def fromForm(id:ObjectId, name:String, introduced:Option[Date], discontinued:Option[Date], company: Option[String]) :Computer ={
    Computer(new ObjectId(), name, introduced, discontinued, company.map(c => new ObjectId(c))) 
  }

  def toForm(computer: Computer) = {
    Some((computer.id, computer.name, computer.introduced, computer.discontinued, computer.companyId.map(c => c.toString)))
  }
}

object CompanyDAO extends SalatDAO[Company, ObjectId](collection = mongoCollection("companies")) {

  def options: Seq[(String,String)] = {
    find(MongoDBObject.empty).map(it => (it.id.toString, it.name)).toSeq
  }
}


object ComputerDAO extends SalatDAO[Computer, ObjectId](collection = mongoCollection("computers")) {


  def list(filter: String = ""): Seq[(Computer, Option[Company])] = {

    val where = if(filter == "") MongoDBObject.empty else MongoDBObject("name" ->(""".*"""+filter+""".*""").r)
    val computers = find(where).toSeq

    computers.map{ c => 
      val company = c.companyId.flatMap(id => CompanyDAO.findOneByID(id))
      (c, company)
    }
  }
}


