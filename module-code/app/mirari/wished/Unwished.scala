package mirari.wished

import play.api.mvc.{Request, ActionBuilder, Results, SimpleResult}
import scala.concurrent._
import ExecutionContext.Implicits.global


/**
 * @author alari (name.alari@gmail.com)
 * @since 03.11.13 21:02
 */
class Unwished[K](val httpStatus: Int, content: => Option[Unwished.Content[K]]) extends Throwable {

  lazy val response: SimpleResult =
    content.map(c => Results.Status(httpStatus)(c.content)(c.writeable)).getOrElse(Results.Status(httpStatus))
}

object Unwished {

  case class Content[C](content: C, writeable: play.api.http.Writeable[C])

  private def recover(p: Promise[SimpleResult]): PartialFunction[Throwable, Unit] = {
    case u: Unwished[_] =>
      play.api.Logger.debug(s"Recover # ${u.httpStatus} $u", u)
      p success u.response
      
    case e: akka.pattern.AskTimeoutException =>
      play.api.Logger.error("Akka ask ? timed out", e)
      p success Results.RequestTimeout
      
    case e: Throwable =>
      play.api.Logger.error("Exception during request", e)
      p success Results.InternalServerError
  }

  def forCode[K](httpStatus: Int, content: K)(implicit writeable: play.api.http.Writeable[K]) = new Unwished(httpStatus, Some(Content(content, writeable)))

  def forCode(httpStatus: Int) = new Unwished[Nothing](httpStatus, None)

  def wrap(f: => Future[SimpleResult]): Future[SimpleResult] = {
    val p = promise[SimpleResult]()

    try {
      val result = f

      result.onSuccess {
        case res =>
          p success res
      }
      result onFailure recover(p)

    } catch recover(p)

    p.future
  }

  def flatRight[T, C](f: Future[Either[Unwished[_], T]])(t: T => Future[Either[Unwished[_], C]]): Future[Either[Unwished[_], C]] = f flatMap {
    case Right(ok) => t(ok)
    case Left(ko) => Future(Left(ko))
  }



  import play.api.http.Status._

  val BadGateway = new Unwished[Nothing](BAD_GATEWAY, None)

  val BadRequest = new Unwished[Nothing](BAD_REQUEST, None)

  val Conflict = new Unwished[Nothing](CONFLICT, None)

  val EntityTooLarge = new Unwished[Nothing](REQUEST_ENTITY_TOO_LARGE, None)

  val ExpectationFailed = new Unwished[Nothing](EXPECTATION_FAILED, None)

  val FailedDependency = new Unwished[Nothing](FAILED_DEPENDENCY, None)

  val Forbidden = new Unwished[Nothing](FORBIDDEN, None)

  val GatewayTimeout = new Unwished[Nothing](GATEWAY_TIMEOUT, None)

  val Gone = new Unwished[Nothing](GONE, None)

  val HttpVersionNotSupported = new Unwished[Nothing](HTTP_VERSION_NOT_SUPPORTED, None)

  val InsufficientStorage = new Unwished[Nothing](INSUFFICIENT_STORAGE, None)

  val InternalServerError = new Unwished[Nothing](INTERNAL_SERVER_ERROR, None)

  val Locked = new Unwished[Nothing](LOCKED, None)

  val MethodNotAllowed = new Unwished[Nothing](METHOD_NOT_ALLOWED, None)

  val NotAcceptable = new Unwished[Nothing](NOT_ACCEPTABLE, None)

  val NotFound = new Unwished[Nothing](NOT_FOUND, None)

  val NotImplemented = new Unwished[Nothing](NOT_IMPLEMENTED, None)

  val PreconditionFailed = new Unwished[Nothing](PRECONDITION_FAILED, None)

  val RequestTimeout = new Unwished[Nothing](REQUEST_TIMEOUT, None)

  val ServiceUnavailable = new Unwished[Nothing](SERVICE_UNAVAILABLE, None)

  val TooManyRequest = new Unwished[Nothing](TOO_MANY_REQUEST, None)

  val Unauthorized = new Unwished[Nothing](UNAUTHORIZED, None)

  val UnprocessableEntity = new Unwished[Nothing](UNPROCESSABLE_ENTITY, None)

  val UnsupportedMediaType = new Unwished[Nothing](UNSUPPORTED_MEDIA_TYPE, None)

  val UriTooLong = new Unwished[Nothing](REQUEST_URI_TOO_LONG, None)

  def BadGateway[C](content: => C)(implicit writeable: play.api.http.Writeable[C]) = new Unwished[C](BAD_GATEWAY, Some(Content(content, writeable)))

  def BadRequest[C](content: => C)(implicit writeable: play.api.http.Writeable[C]) = new Unwished[C](BAD_REQUEST, Some(Content(content, writeable)))

  def Conflict[C](content: => C)(implicit writeable: play.api.http.Writeable[C]) = new Unwished[C](CONFLICT, Some(Content(content, writeable)))

  def EntityTooLarge[C](content: => C)(implicit writeable: play.api.http.Writeable[C]) = new Unwished[C](REQUEST_ENTITY_TOO_LARGE, Some(Content(content, writeable)))

  def ExpectationFailed[C](content: => C)(implicit writeable: play.api.http.Writeable[C]) = new Unwished[C](EXPECTATION_FAILED, Some(Content(content, writeable)))

  def FailedDependency[C](content: => C)(implicit writeable: play.api.http.Writeable[C]) = new Unwished[C](FAILED_DEPENDENCY, Some(Content(content, writeable)))

  def Forbidden[C](content: => C)(implicit writeable: play.api.http.Writeable[C]) = new Unwished[C](FORBIDDEN, Some(Content(content, writeable)))

  def GatewayTimeout[C](content: => C)(implicit writeable: play.api.http.Writeable[C]) = new Unwished[C](GATEWAY_TIMEOUT, Some(Content(content, writeable)))

  def Gone[C](content: => C)(implicit writeable: play.api.http.Writeable[C]) = new Unwished[C](GONE, Some(Content(content, writeable)))

  def HttpVersionNotSupported[C](content: => C)(implicit writeable: play.api.http.Writeable[C]) = new Unwished[C](HTTP_VERSION_NOT_SUPPORTED, Some(Content(content, writeable)))

  def InsufficientStorage[C](content: => C)(implicit writeable: play.api.http.Writeable[C]) = new Unwished[C](INSUFFICIENT_STORAGE, Some(Content(content, writeable)))

  def InternalServerError[C](content: => C)(implicit writeable: play.api.http.Writeable[C]) = new Unwished[C](INTERNAL_SERVER_ERROR, Some(Content(content, writeable)))

  def Locked[C](content: => C)(implicit writeable: play.api.http.Writeable[C]) = new Unwished[C](LOCKED, Some(Content(content, writeable)))

  def MethodNotAllowed[C](content: => C)(implicit writeable: play.api.http.Writeable[C]) = new Unwished[C](METHOD_NOT_ALLOWED, Some(Content(content, writeable)))

  def NotAcceptable[C](content: => C)(implicit writeable: play.api.http.Writeable[C]) = new Unwished[C](NOT_ACCEPTABLE, Some(Content(content, writeable)))

  def NotFound[C](content: => C)(implicit writeable: play.api.http.Writeable[C]) = new Unwished[C](NOT_FOUND, Some(Content(content, writeable)))

  def NotImplemented[C](content: => C)(implicit writeable: play.api.http.Writeable[C]) = new Unwished[C](NOT_IMPLEMENTED, Some(Content(content, writeable)))

  def PreconditionFailed[C](content: => C)(implicit writeable: play.api.http.Writeable[C]) = new Unwished[C](PRECONDITION_FAILED, Some(Content(content, writeable)))

  def RequestTimeout[C](content: => C)(implicit writeable: play.api.http.Writeable[C]) = new Unwished[C](REQUEST_TIMEOUT, Some(Content(content, writeable)))

  def ServiceUnavailable[C](content: => C)(implicit writeable: play.api.http.Writeable[C]) = new Unwished[C](SERVICE_UNAVAILABLE, Some(Content(content, writeable)))

  def TooManyRequest[C](content: => C)(implicit writeable: play.api.http.Writeable[C]) = new Unwished[C](TOO_MANY_REQUEST, Some(Content(content, writeable)))

  def Unauthorized[C](content: => C)(implicit writeable: play.api.http.Writeable[C]) = new Unwished[C](UNAUTHORIZED, Some(Content(content, writeable)))

  def UnprocessableEntity[C](content: => C)(implicit writeable: play.api.http.Writeable[C]) = new Unwished[C](UNPROCESSABLE_ENTITY, Some(Content(content, writeable)))

  def UnsupportedMediaType[C](content: => C)(implicit writeable: play.api.http.Writeable[C]) = new Unwished[C](UNSUPPORTED_MEDIA_TYPE, Some(Content(content, writeable)))

  def UriTooLong[C](content: => C)(implicit writeable: play.api.http.Writeable[C]) = new Unwished[C](REQUEST_URI_TOO_LONG, Some(Content(content, writeable)))

  implicit def toLeft[C, K](un: Unwished[K]): Left[Unwished[K], C] = Left(un)
}