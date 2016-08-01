import de.heikoseeberger.sbtheader.HeaderKey._
import de.heikoseeberger.sbtheader.license.Apache2_0
import com.typesafe.sbt.pgp.PgpKeys.publishSigned
import com.typesafe.sbt.SbtGhPages.GhPagesKeys._
//import com.typesafe.sbt.site.util.SiteHelpers._
import com.typesafe.sbt.SbtGit.GitKeys._

import scala.xml.transform.{RewriteRule, RuleTransformer}

name := "united-streams"

version := "1.0"

scalaVersion := "2.11.8"

organization := "com.github.mboogerd"

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-unchecked",
  "-Xfatal-warnings",
  //  "-Xlint",
  "-Yinline-warnings",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  //  "-Ywarn-value-discard",
  "-Xfuture",
  "-Ywarn-unused-import"
  //  "-Ylog-classpath"
)

javacOptions ++= Seq("-Xlint:deprecation", "-Xlint:unchecked", "-Xlink:-warn-missing-interpolator", "-g:vars")

cancelable in Global := true

def javaVersion(version: String): Seq[Def.Setting[Task[Seq[String]]]] = Seq(
  javacOptions ++= Seq("-source", version, "-target", version),
  scalacOptions += s"-target:jvm-$version"
)

def licenceSettings = Seq(
  licenses +=("Apache-2.0", url("http://opensource.org/licenses/apache2.0.php")),
  headers := Map(
    "scala" -> Apache2_0("2015", "Merlijn Boogerd"),
    "conf" -> Apache2_0("2015", "Merlijn Boogerd", "#")
  )
)

lazy val Benchmark = config("bench") extend Test

def commonSettings = Seq(
  organization := "org.mboogerd",
  scalaVersion := "2.11.8",
  resolvers += Resolver.jcenterRepo,
  libraryDependencies ++= Seq(
    "com.iheart" %% "ficus" % "1.2.6",
    "org.typelevel" %% "cats" % "0.6.1",
    "org.scalatest" %% "scalatest" % "3.0.0-RC4" % "test",
    "org.scalacheck" %% "scalacheck" % "1.13.2" % "test",
    "com.storm-enroute" %% "scalameter" % "0.7" % "test",
    "org.typelevel" %% "discipline" % "0.5" % "test"
  ),
  testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework"),
  parallelExecution in Benchmark := false,
  parallelExecution in Test := false
)

lazy val core = project.in(file("core"))
  .settings(moduleName := "core")
  .settings(javaVersion("1.8"))
  .settings(commonSettings)
  .settings(licenceSettings)
  .settings{
    val akkaVersion = "2.4.8"
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % "test"
    )
  }
  .enablePlugins(AutomateHeaderPlugin)
  .configs(Benchmark)
  .settings(inConfig(Benchmark)(Defaults.testSettings): _*)

lazy val docSettings = Seq(
  autoAPIMappings := true,
  ghpagesNoJekyll := false,
  fork in tut := true,
  git.remoteRepo := "git@github.com:mboogerd/united-streams.git",
  preprocessIncludeFilter := "*.md" | "*.markdown",
  includeFilter in makeSite := "*.html" | "*.css" | "*.png" | "*.jpg" | "*.gif" | "*.js" | "*.swf" | "*.yml" | "*.md",
  mappings in makeSite <++= tut
)

lazy val docs = project
  .settings(moduleName := "docs")
  .settings(commonSettings)
  .settings(unidocSettings)
  .settings(tutSettings)
  .settings(tutScalacOptions ~= (_.filterNot(Set("-Ywarn-unused-import", "-Ywarn-dead-code"))))
  .settings(docSettings)
  .enablePlugins(JekyllPlugin)
  .settings(ghpages.settings)
  .dependsOn(core)