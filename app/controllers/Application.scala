package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {
  
  def index = Action {
//    Ok(views.html.index("Your new application is ready."))
    Ok(Scalate("landing.scaml").render())
  }

  def map = Action {
    Ok( Scalate("map.scaml").render())
  }

}