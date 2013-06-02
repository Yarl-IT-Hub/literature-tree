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


    val buildXml =
      """
        |<?xml version="1.0" encoding="UTF-8"?>
        |<project name="%s" default="help">
        |
        |    <!-- The local.properties file is created and updated by the 'android' tool.
        |         It contains the path to the SDK. It should *NOT* be checked into
        |         Version Control Systems. -->
        |    <property file="local.properties" />
        |
        |    <!-- The ant.properties file can be created by you. It is only edited by the
        |         'android' tool to add properties to it.
        |         This is the place to change some Ant specific build properties.
        |         Here are some properties you may want to change/update:
        |
        |         source.dir
        |             The name of the source directory. Default is 'src'.
        |         out.dir
        |             The name of the output directory. Default is 'bin'.
        |
        |         For other overridable properties, look at the beginning of the rules
        |         files in the SDK, at tools/ant/build.xml
        |
        |         Properties related to the SDK location or the project target should
        |         be updated using the 'android' tool with the 'update' action.
        |
        |         This file is an integral part of the build system for your
        |         application and should be checked into Version Control Systems.
        |
        |         -->
        |    <property file="ant.properties" />
        |
        |    <!-- if sdk.dir was not set from one of the property file, then
        |         get it from the ANDROID_HOME env var.
        |         This must be done before we load project.properties since
        |         the proguard config can use sdk.dir -->
        |    <property environment="env" />
        |    <condition property="sdk.dir" value="${env.ANDROID_HOME}">
        |        <isset property="env.ANDROID_HOME" />
        |    </condition>
        |
        |    <!-- The project.properties file is created and updated by the 'android'
        |         tool, as well as ADT.
        |
        |         This contains project specific properties such as project target, and library
        |         dependencies. Lower level build properties are stored in ant.properties
        |         (or in .classpath for Eclipse projects).
        |
        |         This file is an integral part of the build system for your
        |         application and should be checked into Version Control Systems. -->
        |    <loadproperties srcFile="project.properties" />
        |
        |    <!-- quick check on sdk.dir -->
        |    <fail
        |            message="sdk.dir is missing. Make sure to generate local.properties using 'android update project' or to inject it through the ANDROID_HOME environment variable."
        |            unless="sdk.dir"
        |    />
        |
        |    <!--
        |        Import per project custom build rules if present at the root of the project.
        |        This is the place to put custom intermediary targets such as:
        |            -pre-build
        |            -pre-compile
        |            -post-compile (This is typically used for code obfuscation.
        |                           Compiled code location: ${out.classes.absolute.dir}
        |                           If this is not done in place, override ${out.dex.input.absolute.dir})
        |            -post-package
        |            -post-build
        |            -pre-clean
        |    -->
        |    <import file="custom_rules.xml" optional="true" />
        |
        |    <!-- Import the actual build file.
        |
        |         To customize existing targets, there are two options:
        |         - Customize only one target:
        |             - copy/paste the target into this file, *before* the
        |               <import> task.
        |             - customize it to your needs.
        |         - Customize the whole content of build.xml
        |             - copy/paste the content of the rules files (minus the top node)
        |               into this file, replacing the <import> task.
        |             - customize to your needs.
        |
        |         ***********************
        |         ****** IMPORTANT ******
        |         ***********************
        |         In all cases you must update the value of version-tag below to read 'custom' instead of an integer,
        |         in order to avoid having your file be overridden by tools such as "android update project"
        |    -->
        |    <!-- version-tag: 1 -->
        |    <import file="${sdk.dir}/tools/ant/build.xml" />
        |
        |</project>
      """.stripMargin.format(name.trim).trim




    println("Creating build file..")

    FileUtils.write(new File(path + "build.xml"), buildXml)

  }
}
