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

    val stringXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n    <string name=\"app_name\">%s</string>\n</resources>".format(name.trim)

    FileUtils.write(new File(path + "/res/values/" + "strings.xml"), stringXml)


    val projectResDir = new File("../data/res/")

    println("Copying layout and image resources form [" + projectResDir.toPath + "]")
    FileUtils.copyDirectory(projectResDir, new File(path + "res"))


    val projectFile =
      """
        |<?xml version="1.0" encoding="UTF-8"?>
        |<projectDescription>
        |	<name>%s</name>
        |	<comment></comment>
        |	<projects>
        |	</projects>
        |	<buildSpec>
        |		<buildCommand>
        |			<name>com.android.ide.eclipse.adt.ResourceManagerBuilder</name>
        |			<arguments>
        |			</arguments>
        |		</buildCommand>
        |		<buildCommand>
        |			<name>com.android.ide.eclipse.adt.PreCompilerBuilder</name>
        |			<arguments>
        |			</arguments>
        |		</buildCommand>
        |		<buildCommand>
        |			<name>org.eclipse.jdt.core.javabuilder</name>
        |			<arguments>
        |			</arguments>
        |		</buildCommand>
        |		<buildCommand>
        |			<name>com.android.ide.eclipse.adt.ApkBuilder</name>
        |			<arguments>
        |			</arguments>
        |		</buildCommand>
        |	</buildSpec>
        |	<natures>
        |		<nature>com.android.ide.eclipse.adt.AndroidNature</nature>
        |		<nature>org.eclipse.jdt.core.javanature</nature>
        |	</natures>
        |</projectDescription>
      """.stripMargin.format(name.trim).trim

    FileUtils.write(new File(path + ".project.xml"), projectFile)


    val manifest =
      """
        |<?xml version="1.0" encoding="utf-8"?>
        |<manifest xmlns:android="http://schemas.android.com/apk/res/android" android:windowSoftInputMode="adjustPan"
        |      package="org.yit.lit" android:versionName="1.0" android:versionCode="1" android:hardwareAccelerated="true">
        |    <supports-screens
        |        android:largeScreens="true"
        |        android:normalScreens="true"
        |        android:smallScreens="true"
        |        android:xlargeScreens="true"
        |        android:resizeable="true"
        |        android:anyDensity="true"
        |        />
        |
        |    <uses-permission android:name="android.permission.CAMERA" />
        |    <uses-permission android:name="android.permission.VIBRATE" />
        |    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
        |    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
        |    <uses-permission android:name="android.permission.ACCESS_LOCATION_EXTRA_COMMANDS" />
        |    <uses-permission android:name="android.permission.INTERNET" />
        |    <uses-permission android:name="android.permission.RECEIVE_SMS" />
        |    <uses-permission android:name="android.permission.RECORD_AUDIO" />
        |    <uses-permission android:name="android.permission.RECORD_VIDEO"/>
        |    <uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
        |    <uses-permission android:name="android.permission.READ_CONTACTS" />
        |    <uses-permission android:name="android.permission.WRITE_CONTACTS" />
        |    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
        |    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
        |    <uses-permission android:name="android.permission.GET_ACCOUNTS" />
        |    <uses-permission android:name="android.permission.BROADCAST_STICKY" />
        |
        |
        |    <application android:icon="@drawable/icon" android:label="@string/app_name"
        |        android:hardwareAccelerated="true"
        |        android:debuggable="true">
        |        <activity android:name="%s" android:label="@string/app_name"
        |                android:theme="@android:style/Theme.Black.NoTitleBar"
        |                android:configChanges="orientation|keyboardHidden|keyboard|screenSize|locale">
        |            <intent-filter>
        |                <action android:name="android.intent.action.MAIN" />
        |                <category android:name="android.intent.category.LAUNCHER" />
        |            </intent-filter>
        |        </activity>
        |    </application>
        |
        |    <uses-sdk android:minSdkVersion="7" android:targetSdkVersion="17"/>
        |</manifest>
      """.stripMargin.format(name.trim).trim


    println("Creating manifest file..")

    FileUtils.write(new File(path + "AndroidManifest.xml"), manifest)

  }
}
