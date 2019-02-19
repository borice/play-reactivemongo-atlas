package controllers

import javax.inject._
import play.api.libs.json.{JsObject, Json}
import play.api.mvc._
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.DefaultDB
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents,
                               reactiveMongoApi: ReactiveMongoApi)
                              (implicit ec: ExecutionContext) extends AbstractController(cc) {

  def db: Future[DefaultDB] = reactiveMongoApi.database
  def helloColl: Future[JSONCollection] = db.map(_.collection[JSONCollection]("hello"))

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action.async { implicit request: Request[AnyContent] =>
    helloColl.flatMap(_.find(Json.obj(), Option.empty[JsObject]).one[JsObject]).map {
      case Some(obj) =>
        val message = (obj \ "message").as[String]
        Ok(views.html.index(message))

      case None => NotFound
    }
  }
}
