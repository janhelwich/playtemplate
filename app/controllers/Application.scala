package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {
  
  def index = Action {
//    Ok(views.html.index("Your new application is ready."))
    Ok(render("landing.scaml"))
  }

  def map = Action {
    Ok( render("map.scaml"))
  }

  def mobile = Action {
    Ok( render("mobile.scaml"))
  }

}