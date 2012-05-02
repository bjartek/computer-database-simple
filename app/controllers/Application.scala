package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import com.mongodb.casbah.Imports._

import views._
import models._

/**
 * Manage a database of computers
 */
object Application extends Controller { 
  
  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.Application.list(""))
  
  /**
   * Describe the computer form (used in both edit and create screens).
   */ 
  val computerForm = Form(
    mapping(
      "id" -> ignored(new ObjectId()),
      "name" -> nonEmptyText,
      "introduced" -> optional(date("yyyy-MM-dd")),
      "discontinued" -> optional(date("yyyy-MM-dd")),
      "company" -> optional(text)
    )(Computer.fromForm)(Computer.toForm)
  )
  
  // -- Actions

  /**
   * Handle default path requests, redirect to computers list
   */  
  def index = Action { Home }
  
  /**
   * Display the paginated list of computers.
   *
   * @param filter Filter applied on computer names
   */
  def list(filter: String) = Action { implicit request =>
    Ok(html.list(ComputerDAO.list(filter), filter))
  }
  
  /**
   * Display the 'edit form' of a existing Computer.
   *
   * @param id Id of the computer to edit
   */
  def edit(id: String) = Action {
    ComputerDAO.findOneByID(new ObjectId(id)).map { computer =>
      Ok(html.editForm(id, computerForm.fill(computer)))
    }.getOrElse(NotFound)
  }
  
  /**
   * Handle the 'edit form' submission 
   *
   * @param id Id of the computer to edit
   */
  def update(id: String) = Action { implicit request =>
    computerForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.editForm(id, formWithErrors)),
      computer => {
        ComputerDAO.save(computer.copy(id = new ObjectId(id)))
        Home.flashing("success" -> "Computer %s has been updated".format(computer.name))
      }
    )
  }
  
  /**
   * Display the 'new computer form'.
   */
  def create = Action {
    Ok(html.createForm(computerForm))
  }
  
  /**
   * Handle the 'new computer form' submission.
   */
  def save = Action { implicit request =>
    computerForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.createForm(formWithErrors)),
      computer => {
        ComputerDAO.insert(computer)
        Home.flashing("success" -> "Computer %s has been created".format(computer.name))
      }
    )
  }
  
  /**
   * Handle computer deletion.
   */
  def delete(id: String) = Action {
    ComputerDAO.remove(MongoDBObject("_id" -> new ObjectId(id)))
    Home.flashing("success" -> "Computer has been deleted")
  }

}
            
