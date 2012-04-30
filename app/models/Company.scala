package models

import java.util.{Date}

import com.novus.salat._
import com.novus.salat.global._
import com.novus.salat.dao._
import com.novus.salat.annotations._
import com.mongodb.casbah.Imports._
import models.SalatImports._

case class Company(@Key("_id") id: ObjectId= new ObjectId, name: String)

object CompanyDAO extends SalatDAO[Company, ObjectId](collection = mongoCollection("company")) {

  def options: Seq[(String,String)] = { 
    find(MongoDBObject.empty).map(company => (company.id.toString, company.name))
  }

}
