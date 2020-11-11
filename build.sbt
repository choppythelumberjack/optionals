import ReleaseTransformations._
import sbt.util.FileInfo
import sbtrelease.ReleasePlugin

lazy val root = (project in file("."))
  .settings(
    name := "optionals",
    libraryDependencies ++= Seq(
      "com.novocode" % "junit-interface" % "0.11" % "test"
    ),
    crossPaths := false,
    projectDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.2" % Test,
      "junit" % "junit" % "4.11" % Test,
      "com.novocode" % "junit-interface" % "0.11" % Test
    )
  )
  .settings(fmppSettings: _*)
  .settings(releaseSettings: _*)

lazy val fmppTask = Def.task {
  val s = streams.value
  val output = sourceManaged.value
  val fmppSrc = resourceDirectory.value
  val inFiles = (fmppSrc ** "*.ftl").get.toSet
  val cachedFun = FileFunction.cached(s.cacheDirectory / "fmpp", outStyle = FilesInfo.exists, inStyle = FileInfo.lastModified) { (in: Set[File]) =>
    IO.delete((output ** "*.java").get)
    val args = "--expert" :: "-q" :: "-S" :: fmppSrc.getPath :: "-O" :: output.getPath ::
      "--replace-extensions=ftl, java" :: "-M" :: "execute(**/*.ftl), ignore(**/*)" :: Nil
      /*toError*/
    (runner in fmpp).value.run("fmpp.tools.CommandLine", (fullClasspath in fmppConfig).value.files, args, s.log)
      .failed foreach (sys error _.getMessage)
    (output ** "*.java").get.toSet
  }
  cachedFun(inFiles).toSeq
}

/* FMPP Task */
lazy val fmpp = TaskKey[Seq[File]]("fmpp")
lazy val fmppConfig = {
  val Fmpp = config("fmpp") // Fmpp needs to be capital or build fails??
  Fmpp.hide
}
lazy val fmppSettings = inConfig(Compile)(Seq(sourceGenerators += fmpp.taskValue, fmpp := fmppTask.value)) ++ Seq(
  libraryDependencies ++= Seq(
    ("net.sourceforge.fmpp" % "fmpp" % "0.9.15" % fmppConfig.name).intransitive,
    "org.freemarker" % "freemarker" % "2.3.23" % fmppConfig.name,
    "oro" % "oro" % "2.0.8" % fmppConfig.name,
    "org.beanshell" % "bsh" % "2.0b5" % fmppConfig.name,
    "xml-resolver" % "xml-resolver" % "1.2" % fmppConfig.name
  ),
  ivyConfigurations += fmppConfig,
  fullClasspath in fmppConfig := update.map { _ select configurationFilter(fmppConfig.name) map Attributed.blank }.value,
  mappings in (Compile, packageSrc) ++= {
    val fmppSrc = (sourceDirectory in Compile).value / "java"
    val inFiles = fmppSrc ** "*.ftl"
    ((managedSources in Compile).value.pair(Path.relativeTo((sourceManaged in Compile).value) | Path.flat)) ++ // Add generated sources to sources JAR
      (inFiles pair (Path.relativeTo(fmppSrc) | Path.flat)) // Add *.fm files to sources JAR
  }
)

lazy val releaseSettings = ReleasePlugin.extraReleaseCommands ++ Seq(
  credentials += Credentials(Path.userHome / ".sbt" / "sonatype_credentials"),
  javacOptions in (Compile, compile) ++= Seq("-source", "1.8", "-target", "1.8", "-g:lines"),
  crossPaths := false, // drop off Scala suffix from artifact names.
  autoScalaLibrary := false, // exclude scala-library from dependencies
  licenses := Seq("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0.txt")),
  releaseCrossBuild := true,
  organization := "com.github.choppythelumberjack",
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  publishMavenStyle := true,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },
  releaseProcess := {
    Seq[ReleaseStep](
          checkSnapshotDependencies,
          inquireVersions,
          runClean,
          setReleaseVersion,
          commitReleaseVersion,
          tagRelease,
          publishArtifacts,
          setNextVersion,
          commitNextVersion,
          releaseStepCommand("sonatypeReleaseAll"),
          pushChanges
    )
  },
  pomExtra := (
      <url>https://github.com/choppythelumberjack</url>
      <scm>
        <connection>scm:git:git@github.com:choppythelumberjack/optionals.git</connection>
        <developerConnection>scm:git:git@github.com:choppythelumberjack/optionals.git</developerConnection>
        <url>https://github.com/choppythelumberjack/optionals</url>
      </scm>
      <developers>
        <developer>
          <id>choppythelumberjack</id>
          <name>Choppy The Lumberjack</name>
          <url>https://github.com/choppythelumberjack</url>
        </developer>
      </developers>)
)
