import sbt._
import Keys._
import PlayProject._
import com.mojolly.scalate.ScalatePlugin._

object ApplicationBuild extends Build {

    val appName         = "trader"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "org.fusesource.scalate" % "scalate-core" % "1.5.3"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      scalateTemplateDirectory in Compile <<= (baseDirectory) { _ / "app/views" }
    )
}
