import sbt._
import de.element34.sbteclipsify.Eclipsify

class RetColProject(info: ProjectInfo) extends DefaultProject(info)
                                       with Eclipsify{
//  ----------------------------------------------------------------

  override def managedStyle = ManagedStyle.Maven

  val credentials = new java.io.File(Path.userHome / ".ivy2" / "ivy-element.ppk" toString)
  lazy val publishTo = Resolver.sftp("Element d.o.o. Maven2 Repository", "element.hr", "/ivy") as ("ivy", credentials, "sbtivym2")

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
        <name>Element d.o.o. Maven2 Repository</name>
        <url>http://element.hr/m2</url>
      </repository>
    </repositories>

//  ################################################################

  val scalatest = "org.scalatest" % "scalatest" % "1.3" % "test"
}