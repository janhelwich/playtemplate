// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository 
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("play" % "sbt-plugin" % "2.0.1")

libraryDependencies <+= sbtVersion(v => "com.mojolly.scalate" %% "xsbt-scalate-generator" % (v + "-0.1.6"))