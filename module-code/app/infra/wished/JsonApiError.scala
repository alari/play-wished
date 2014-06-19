package infra.wished

import play.api.libs.json._
import play.api.mvc.{Results, Result}

/**
 * @author alari
 * @since 6/9/14
 */
case class JsonApiError(status: Int, code: String, summary: String, service: String, data: JsValue = JsNull) extends Throwable {
  override def toString = s"$status($service/$code): $summary / $data"
}

object JsonApiError {
  implicit val f: Format[JsonApiError] = (__ \ "failure").format(Json.writes[JsonApiError])
  implicit def toResp(err: JsonApiError): Result = Results.Status(err.status)(Json.toJson(err))
  implicit def toJson(err: JsonApiError): JsValue = Json.toJson(err)
}