import sbt._
import de.element34.sbteclipsify.Eclipsify

class RetColProject(info: ProjectInfo) extends DefaultProject(info)
                                       with Eclipsify{
//  ----------------------------------------------------------------

  override def managedStyle = ManagedStyle.Maven
  lazy val publishTo = Resolver.sftp("Element d.o.o. Ivy Repository", "element.hr", "/ivy")

  override def packageSrcJar = defaultJarPath("-sources.jar")
  val sourceArtifact = Artifact.sources(artifactID)

  override def packageDocsJar = defaultJarPath("-javadoc.jar")
  val docsArtifact = Artifact.javadoc(artifactID)

  override def packageToPublishActions = super.packageToPublishActions ++ Seq(packageDocs, packageSrc)
  override def pomIncludeRepository(repo: MavenRepository) = false

  override def pomExtra =
    <repositories>
      <repository>
        <id>ElementMaven2Repository</id>
        <name>Element d.o.o. Ivy Repository</name>
        <url>http://http://element.hr/ivy</url>
      </repository>
    </repositories>

//  ################################################################

  val scalatest = "org.scalatest" % "scalatest" % "1.3" % "test"
}