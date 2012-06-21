package controllers

import play.api._
import play.api.mvc._
import models.User

object Application extends Controller {
  
  def index = Action {
//    Ok(views.html.index("Your new application is ready."))
    User.create("jan")
    Ok(render("landing.scaml"))
  }

  def map = Action {
    Ok( render("map.scaml"))
  }

  def mobile = Action {
    Ok( render("mobile.scaml"))
  }

}