package mirari.wished

import play.api.mvc.{SimpleResult, Request, ActionBuilder}
import scala.concurrent.Future

/**
 * @author alari
 * @since 2/20/14
 */
object WishedAction extends ActionBuilder[Request] {
  protected def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[SimpleResult]): Future[SimpleResult] =
    Unwished wrap block(request)
}
