import sbt._

class TwineProject(info: ProjectInfo) extends DefaultProject(info) with extract.BasicSelfExtractingProject
{
  val lag_net = "lag.net repository" at "http://www.lag.net/repo"
  val snapshots = "scala-tools snapshots" at "http://scala-tools.org/repo-snapshots/"
  
  val configgy = "net.lag" % "configgy" % "1.3" % "provided->default"
  val dispatch = "net.databinder" %% "dispatch-twitter" % "{{dispatch.version}}"
  // rm below when published ivy.xml is correct
  val dispatch_oauth = "net.databinder" %% "dispatch-oauth" % "{{dispatch.version}}"
  val dispatch_json = "net.databinder" %% "dispatch-json" % "{{dispatch.version}}"
  val dispatch_http = "net.databinder" %% "dispatch-http" % "{{dispatch.version}}"
  
  override def installActions = update.name :: script.name :: readme.name :: Nil
	
  // will use proguard to make one runnable jar later, for now a crazy long classpath will do
  lazy val script = task {
    import Process._
    import java.io.File
    val twine = (info.projectPath / "twine").asFile
    FileUtilities.write(twine,
      "java -cp %s %s \"$@\"" format (
        (Path.makeString(runClasspath.get) :: mainDependencies.scalaJars.get.toList).mkString(File.pathSeparator),
        getMainClass(false).get
      ), log) orElse {
      ("chmod a+x " + twine) ! log
      None
    }
  } dependsOn compile
  
  lazy val readme = task {
    val rf = path("README").asFile
    print("Printing %s ==>\n\n" format rf)
    FileUtilities.readStream(rf, log) { stm =>
      io.Source.fromInputStream(stm).getLines.foreach(print); None
    }
  }
}
