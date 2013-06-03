#!/bin/sh
exec scala -cp lib/commons-io-2.4.jar $0 $@
!#

import java.io.File
import org.apache.commons.io.FileUtils
import scala.collection.mutable.ArrayBuffer

import org.yit._

object App {
  def main(args: Array[String]): Unit = {
    val name = args(0)
    val path = new File("../gen/%s/assets/www/".format(name.trim)).getPath + "/"

    println("Starting to generate html files at " + path)
    // Read content structure
    var contentTreeStructure: String = FileUtils.readFileToString(new File("../data/content/content_structure.txt"))
    // Read the HTML template
    var htmlTemplate: String = FileUtils.readFileToString(new File("../data/content/page_template.html"))
    println("Loaded files")

    var pgBuilder: PageBuilder = new PageBuilder
    var htmlGen: HtmlGen = new HtmlGen

    var trees: ArrayBuffer[Tree] = pgBuilder.buildTree(contentTreeStructure);


    val appName = pgBuilder.getAppName(contentTreeStructure)

    println("Generating html trees [" + trees.length + "]")

    println("Generating index file")

    htmlGen.createIndexHtmlFile(path, appName, htmlTemplate, trees)

    println("Generating sub tree files")

    htmlGen.generateHtmlContent(path, htmlTemplate, trees)
  }
}
