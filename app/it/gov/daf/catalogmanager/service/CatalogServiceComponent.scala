package it.gov.***REMOVED***.catalogmanager.service


import catalog_manager.yaml.{Dataset, MetaCatalog, MetadataCat, Success}
import it.gov.***REMOVED***.catalogmanager.repository.catalog.CatalogRepositoryComponent
import play.api.libs.json.JsValue

import scala.concurrent.Future
import play.api.libs.ws._
import play.api.libs.ws.ahc.AhcWSComponents

/**
  * Created by ale on 05/05/17.
  */
trait CatalogServiceComponent {
  this: CatalogRepositoryComponent  =>
  val catalogService: CatalogService

  class CatalogService {


    def listCatalogs(page :Option[Int], limit :Option[Int]) :Seq[MetaCatalog] = {
       catalogRepository.listCatalogs(page, limit)

    }
    def catalog(catalogId :String): Option[MetaCatalog] = {
      catalogRepository.catalog(catalogId)
    }

    def catalogByName(name :String): Option[MetaCatalog] = {
      catalogRepository.catalogByName(name)
    }

    def publicCatalogByName(name :String): Option[MetaCatalog] = {
      catalogRepository.publicCatalogByName(name)
    }

    def createCatalog(metaCatalog: MetaCatalog, callingUserid :MetadataCat, ws :WSClient) :Success = {
      println("Service : " +  callingUserid)
      catalogRepository.createCatalog(metaCatalog, callingUserid, ws)
    }

    def createCatalogExtOpenData(metaCatalog: MetaCatalog, callingUserid :MetadataCat, ws :WSClient) :Success = {
      println("Service : " +  callingUserid)
      catalogRepository.createCatalogExtOpenData(metaCatalog, callingUserid, ws)
    }

    def isPresentOnCatalog(name :String) :Option[Boolean] = {
      catalogRepository.isDatasetOnCatalog(name)
    }

  }
}
