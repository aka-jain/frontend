package controllers.commercial

import com.gu.contentapi.client.model.v1.ContentType.{Article, Video}
import common.commercial.hosted._
import common.{Edition, ExecutionContexts, Logging}
import model.Cached.RevalidatableResult
import model.commercial.Lookup
import model.{Cached, NoCache}
import play.api.mvc._
import views.html.hosted.{guardianHostedArticle, guardianHostedArticle2, guardianHostedGallery, guardianHostedVideo}

import scala.concurrent.Future

class HostedContentController extends Controller with ExecutionContexts with Logging {

  private def renderPage(hostedPage: Future[Option[HostedPage]])
    (implicit request: Request[AnyContent]): Future[Result] =
    hostedPage map {
      case Some(page: HostedVideoPage) => Cached(60)(RevalidatableResult.Ok(guardianHostedVideo(page)))
      case Some(page: HostedGalleryPage) => Cached(60)(RevalidatableResult.Ok(guardianHostedGallery(page)))
      case Some(page: HostedArticlePage) => Cached(60)(RevalidatableResult.Ok(guardianHostedArticle(page)))
      case Some(page: HostedArticlePage2) => Cached(60)(RevalidatableResult.Ok(guardianHostedArticle2(page)))
      case _ => NoCache(NotFound)
    }

  def renderLegacyHostedPage(campaignName: String, pageName: String) = Action.async { implicit request =>
    renderPage(Future.successful(hardcoded.LegacyHostedPages.fromCampaignAndPageName(campaignName, pageName)))
  }

  def renderHostedPage(campaignName: String, pageName: String) = Action.async { implicit request =>
    val itemId = s"advertiser-content/$campaignName/$pageName"
    val capiResponse = Lookup.content(itemId, Edition(request)) { content =>
      if (content.isHosted) {
        content.`type` match {
          case Video => HostedVideoPage.fromContent(content)
          case Article => HostedArticlePage2.fromContent(content)
          case _ =>
            log.error(s"Failed to render unsupported hosted type: ${content.`type`}: ${content.id}")
            None
        }
      } else {
        log.error(s"Failed to render non-hosted content: ${content.id}")
        None
      }
    }
    val page = capiResponse fallbackTo {
      Future.successful(hardcoded.HostedPages.fromCampaignAndPageName(campaignName, pageName))
    }
    renderPage(page)
  }
}
