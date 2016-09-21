package other

import java.io.File

import com.typesafe.config.{Config, ConfigFactory, ConfigObject}

import scala.collection.JavaConverters._
import scala.collection.mutable

//TODO: Do me
object ApplicationConfig {

  val ComponentsKey = "logiagent.components"
  val BasePathKey = "basepath"
  val ApiVersionKey = "version"
  val AuditPathKey = "logiagent.log-directory-path"

  private val configuration: Config = ConfigFactory.parseFile(new File("./conf/application.conf")).resolve()

  def config(): Config = {
    configuration
  }

  def getString(key: String): String = {
    configuration.getString(key)
  }

  def getComponentDirectories: mutable.Map[String, String] = {
    val configObject: ConfigObject = configuration.getObject(ComponentsKey)
    val keySet = configObject.keySet().asScala

    val componentDirectories: mutable.Map[String, String] = scala.collection.mutable.Map()



    val auditPath: String = configuration.getString(AuditPathKey)


    for (key <- keySet) {
      val basePath = configuration.getString(s"$ComponentsKey.$key.$BasePathKey")
      val apiVersion = configuration.getString(s"$ComponentsKey.$key.$ApiVersionKey")


      componentDirectories.put(basePath, s"$basePath$apiVersion$auditPath")
    }

    componentDirectories
  }
}
