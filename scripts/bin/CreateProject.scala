#!/bin/sh
SCRIPT="$(cd "${0%/*}" 2>/dev/null; echo "$PWD"/"${0##*/}")"
DIR=`dirname "${SCRIPT}"}`
exec scala -cp lib/commons-io-2.4.jar $0 $1
::!#

import java.io.File
import org.apache.commons.io._

object App {
  def main(args: Array[String]): Unit = {
    val name = args(0)
    val resDir = "../resources/Sample/"

    val desDir = new File("../gen/" + name)

    println("Creating directory for project [" + name + "] at [" + desDir.toPath + "]")
    desDir.mkdir()

    println("Copying files for project [" + name + "] ")

    FileUtils.copyDirectory(new File(resDir), desDir)

    val path = desDir.toPath.toString + "/"

    val clazz =
      """
        |package org.yit.lit;
        |
        |import android.os.Bundle;
        |import org.apache.cordova.*;
        |
        |public class %s extends DroidGap
        |{
        |    @Override
        |    public void onCreate(Bundle savedInstanceState)
        |    {
        |        super.onCreate(savedInstanceState);
        |        super.setIntegerProperty("splashscreen", R.drawable.splash);
        |        super.loadUrl(Config.getStartUrl(), 10000);
        |    }
        |}
      """.stripMargin.format(name)

    println("Writing main class [" + name + "] ")

    FileUtils.write(new File(path + "src/org/yit/lit/" + name + ".java"), clazz)



    val stringXml =
      """
        |<?xml version="1.0" encoding="utf-8"?>
        |<resources>
        |    <string name="app_name">%s</string>
        |</resources>
      """.stripMargin.format(name)


    FileUtils.write(new File(path + "/res/values/" + "strings.xml"), stringXml)



    println("Copying layout and image resources")


    FileUtils.copyDirectory(new File(path + "res"), "../data/res/"))



  }
}
