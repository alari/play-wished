package infra.wished

import play.api.mvc.{Result, RequestHeader, Results}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.{Future, Promise}


/**
 * @author alari (name.alari@gmail.com)
 * @since 03.11.13 21:02
 */
object Unwished {
  private def recover(p: Promise[Result])(implicit rh: RequestHeader): PartialFunction[Throwable, Unit] = {
    case e: JsonApiError =>
      play.api.Logger.debug(s"(${rh.uri}) ApiError: $e")
      p.success(e)

    case e: akka.pattern.AskTimeoutException =>
      play.api.Logger.error(s"(${rh.uri}) Akka ask ? timed out", e)
      p success Results.RequestTimeout

    case e: Throwable =>
      play.api.Logger.error(s"(${rh.uri}) Exception during request", e)
      p success Results.InternalServerError
  }

  def wrap(f: => Future[Result])(implicit rh: RequestHeader): Future[Result] = {
    val p = Promise[Result]()

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
}