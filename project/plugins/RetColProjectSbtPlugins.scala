import sbt._

class RetColSbtPlugins(info: ProjectInfo) extends PluginDefinition(info) {
  val eclipse = "de.element34" % "sbt-eclipsify" % "0.7.0"
}
