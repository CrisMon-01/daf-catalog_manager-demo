import com.typesafe.sbt.packager.docker.Cmd
import de.heikoseeberger.sbtheader.license.Apache2_0
import de.zalando.play.generator.sbt.ApiFirstPlayScalaCodeGenerator.autoImport.playScalaAutogenerateTests
import play.sbt.routes.RoutesKeys.routesGenerator
import sbt.Keys.resolvers
import uk.gov.hmrc.gitstamp.GitStampPlugin._

organization in ThisBuild := "it.gov.***REMOVED***"

name := "***REMOVED***-catalog-manager"

val isStaging = System.getProperty("STAGING") != null

val playVersion = "2.5.14"

Seq(gitStampSettings: _*)

version in ThisBuild := sys.***REMOVED***.getOrElse("CATALOG_MANAGER_VERSION", "1.0.4-SNAPSHOT")


lazy val client = (project in file("client")).
  settings(Seq(
    name := "***REMOVED***-catalog-manager-client",
    swaggerGenerateClient := true,
    swaggerClientCodeGenClass := new it.gov.***REMOVED***.swaggergenerators.DafClientGenerator,
    swaggerCodeGenPackage := "it.gov.***REMOVED***.catalogmanager",
    swaggerSourcesDir := file(s"${baseDirectory.value}/../conf"),
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play-json" % playVersion,
      "com.typesafe.play" %% "play-ws" %  playVersion
    )
  )).enablePlugins(SwaggerCodegenPlugin)

lazy val root = (project in file(".")).enablePlugins(PlayScala, ApiFirstCore, ApiFirstPlayScalaCodeGenerator, ApiFirstSwaggerParser)
  .dependsOn(client)
  .aggregate(client)

scalaVersion in ThisBuild := "2.11.8"


libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  filters,
  "org.webjars" % "swagger-ui" % "3.0.7",
  specs2 % Test,
  "org.scalacheck" %% "scalacheck" % "1.13.5" % Test,
 // "org.specs2" %% "specs2-scalacheck" % "3.8.9" % Test,
  "me.jeffmay" %% "play-json-tests" % "1.5.0" % Test,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0" % Test,
  "org.mongodb" %% "casbah" % "3.1.1", //,
  "net.caoticode.dirwatcher" %% "dir-watcher" % "0.1.0",
  "it.gov.***REMOVED***" %% "common" % Versions.***REMOVED***CommonVersion,
  "me.lessis" %% "base64" % "0.2.0",
  "ch.lightshed" %% "courier" % "0.1.4",
  "com.sksamuel.avro4s" %% "avro4s-core" % "1.1.3",
  "com.sksamuel.avro4s" %% "avro4s-json" % "1.1.3",
  "com.sksamuel.avro4s" %% "avro4s-generator" % "1.1.3"


  //"com.github.cb372" %% "scalacache-guava" % "0.9.4"
  //"com.unboundid" % "unboundid-ldapsdk" % "4.0.0"
  //"it.***REMOVED***" %% "ingestion-module" % "0.1.0" exclude("org.apache.avro", "avro")
)

// add nexus repo from security manager
//

libraryDependencies ++= Seq(
  "io.prometheus" % "simpleclient" % "0.1.0",
  "io.prometheus" % "simpleclient_hotspot" % "0.1.0",
  "io.prometheus" % "simpleclient_common" % "0.1.0"
)

resolvers ++= Seq(
  Resolver.mavenLocal,
  //"zalando-bintray" at "***REMOVED***://dl.bintray.com/zalando/maven",
  "scalaz-bintray" at "***REMOVED***://dl.bintray.com/scalaz/releases",
  "jeffmay" at "***REMOVED***://dl.bintray.com/jeffmay/maven",
  Resolver.url("sbt-plugins", url("***REMOVED***://dl.bintray.com/gruggiero/sbt-plugins"))(Resolver.ivyStylePatterns),
  "lightshed-maven" at "***REMOVED***://dl.bintray.com/content/lightshed/maven"
)

resolvers ++= { if(isStaging) Seq("***REMOVED*** repo" at "***REMOVED***://nexus.***REMOVED***.test:8081/repository/maven-public/")
                else Seq("***REMOVED*** repo" at "***REMOVED***://nexus.***REMOVED***.***REMOVED***.it:8081/repository/maven-public/")}

import com.typesafe.sbt.packager.MappingsHelper._
mappings in Universal ++= directory(baseDirectory.value / "data")

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
apiFirstParsers := Seq(ApiFirstSwaggerParser.swaggerSpec2Ast.value).flatten
playScalaAutogenerateTests := false
playScalaCustomTemplateLocation := Some(baseDirectory.value / "templates")

headers := Map(
  "sbt" -> Apache2_0("2017", "TEAM PER LA TRASFORMAZIONE DIGITALE"),
  "scala" -> Apache2_0("2017", "TEAM PER LA TRASFORMAZIONE DIGITALE"),
  "conf" -> Apache2_0("2017", "TEAM PER LA TRASFORMAZIONE DIGITALE", "#"),
  "properties" -> Apache2_0("2017", "TEAM PER LA TRASFORMAZIONE DIGITALE", "#"),
  "yaml" -> Apache2_0("2017", "TEAM PER LA TRASFORMAZIONE DIGITALE", "#")

)

dockerBaseImage := "anapsix/alpine-java:8_jdk_unlimited"
dockerCommands := dockerCommands.value.flatMap {
  case cmd@Cmd("FROM", _) => List(cmd,
    Cmd("RUN", "apk update && apk add bash krb5-libs krb5"),
    Cmd("RUN", "ln -sf /etc/krb5.conf /opt/jdk/jre/lib/security/krb5.conf")
  )
  case other => List(other)
}

dockerExposedPorts := Seq(9000)

dockerEntrypoint := {Seq(s"bin/${name.value}", "-Dconfig.file=conf/production.conf")}

dockerRepository := { if(isStaging)Option("nexus.***REMOVED***.test") else Option("nexus.***REMOVED***.***REMOVED***.it") }


publishTo in ThisBuild := {
  val nexus = if(isStaging) "***REMOVED***://nexus.***REMOVED***.test:8081/repository/"
              else "***REMOVED***://nexus.***REMOVED***.***REMOVED***.it:8081/repository/"

  if (isSnapshot.value)
    Some("snapshots" at nexus + "maven-snapshots/")
  else
    Some("releases"  at nexus + "maven-releases/")
}

credentials += {if(isStaging) Credentials(Path.userHome / ".ivy2" / ".credentialsTest") else Credentials(Path.userHome / ".ivy2" / ".credentials")}

javaOptions in Test += "-Dconfig.resource=" + System.getProperty("config.resource", "localConfigs/integration.conf")


// Wart Remover Plugin Configuration
//wartremoverErrors ++= Warts.allBut(Wart.Nothing, Wart.PublicInference, Wart.Any, Wart.Equals)
//wartremoverExcluded ++= getRecursiveListOfFiles(baseDirectory.value / "target" / "scala-2.11" / "routes").toSeq
