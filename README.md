Droid@Screen
============

Droid@Screen is a stand-alone Java program that show the device screen of an Android phone
at a computer. It's typically used for demo and training.

Prerequisites
-------------

* Java 8
* Android SDK, installed and configured
* Environment variable ANDROID_HOME (or ANDROID_SDK_HOME) pointing the the Android SDK installation directory
* USB driver for the target Android device installed

Execution
---------

Grab the application JAR file (droidAtScreen-*nnn*.jar) and double-click it or
run it at the command-line with

    $ java -jar droidAtScreen-1.2.jar

The application will search for the ADB (Android Debug Bridge) executable using the environment variables mentioned above.
If it cannot find the executable, it will prompt you for the full path.

Plug-in your Android device using the USB cable and a device frame will shortly pop-up.



