package infra.wished

import play.api.libs.json._

/**
 * @author alari
 * @since 6/9/14
 */
case class JsonApiError(status: Int, code: String, summary: String, service: String, data: JsValue = JsNull) extends Throwable {
  override def toString = s"$status($service/$code): $summary / $data"
}

object JsonApiError {
  implicit val w: Writes[JsonApiError] = (__ \ "failure").write(Json.writes[JsonApiError])
  implicit val r: Reads[JsonApiError] = (__ \ "failure").read(Json.reads[JsonApiError])
}