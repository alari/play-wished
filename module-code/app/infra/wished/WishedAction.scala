package infra.wished

import play.api.mvc.{Result, Request, ActionBuilder}
import scala.concurrent.Future

/**
 * @author alari
 * @since 2/20/14
 */
object WishedAction extends ActionBuilder[Request] {
  def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] =
    Unwished.wrap(block(request))(request)
}
