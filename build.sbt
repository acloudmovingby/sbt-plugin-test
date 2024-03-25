import org.jetbrains.sbtidea.Keys._

lazy val scalaPluginVersion = "2024.1.6"

lazy val graphit =
  project.in(file("."))
    .enablePlugins(SbtIdeaPlugin)
    .settings(
      version := "0.0.1-SNAPSHOT",
      scalaVersion := "2.13.10",
      ThisBuild / intellijPluginName := "Graphit",
      ThisBuild / intellijBuild      := "231.9011.34",
      ThisBuild / intellijPlatform   := IntelliJPlatform.IdeaCommunity,
      Global    / intellijAttachSources := true,
      Compile / javacOptions ++= "--release" :: "11" :: Nil,
        intellijPlugins := Seq(
            //"com.intellij.java".toPlugin, // according to https://github.com/JetBrains/sbt-idea-plugin, this shouldn't be necessary because scala plugin will transitively call it
//            "com.intellij.properties".toPlugin,
            s"org.intellij.scala".toPlugin // org.intellij.scala
        ),
      libraryDependencies += "com.eclipsesource.minimal-json" % "minimal-json" % "0.9.5" withSources(),
      Compile / unmanagedResourceDirectories += baseDirectory.value / "src" / "main" / "resources",
      Test / unmanagedResourceDirectories    += baseDirectory.value / "testResources"
    )
