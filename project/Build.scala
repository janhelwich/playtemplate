import sbt._
import Keys._
import PlayProject._
import com.mojolly.scalate.ScalatePlugin._

object ApplicationBuild extends Build {

    val appName         = "template"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "org.fusesource.scalate" % "scalate-core" % "1.5.3",
      "com.mongodb.casbah" %% "casbah" % "2.1.5-1",
      "com.novus" %% "salat-core" % "0.0.8-SNAPSHOT"
  )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      scalateTemplateDirectory in Compile <<= (baseDirectory) { _ / "app/views" }
    )
}
