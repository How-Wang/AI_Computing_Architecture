// Works with mill 0.10.0-M5
import mill.scalalib._
import coursier.MavenRepository

/**
 * Scala 2.12 module that is source-compatible with 2.11.
 * This is due to Chisel's use of structural types. See
 * https://github.com/freechipsproject/chisel3/issues/606
 */
trait HasXsource211 extends ScalaModule {
  override def scalacOptions = T {
    super.scalacOptions() ++ Seq(
      "-deprecation",
      "-feature",
      "-unchecked",
      "-language:reflectiveCalls",
      "-Xsource:2.11"
    )
  }

  def ScalaVersion = "2.12.15"
}

trait HasChisel3 extends ScalaModule {
  override def ivyDeps = Agg(
    ivy"edu.berkeley.cs::chisel3:3.4.4"
  )
}

trait HasChiselTests extends CrossSbtModule  {
  object test extends Tests {
    override def ivyDeps = Agg(
      ivy"edu.berkeley.cs::chisel-iotesters:1.5.4",
    )
    def testFramework = "org.scalatest.tools.Framework"
  }
}

trait HasMacroParadise extends ScalaModule {
  // Enable macro paradise for @chiselName et al
  val macroPlugins = Agg(
    ivy"org.scalamacros:::paradise:2.1.1",
  )
  def scalacPluginIvyDeps = macroPlugins
  def compileIvyDeps = macroPlugins
}


object chiselModule extends CrossSbtModule 
                    with HasChisel3 
                    with HasChiselTests 
                    with HasXsource211 
                    with HasMacroParadise {
  def crossScalaVersion = "2.12.15"

  def sources = T.sources {
    super.sources() ++ Seq(PathRef(T.workspace/"top_entry"))
  }
}
