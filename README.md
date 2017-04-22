# Droid@Screen #


Droid@Screen is a stand-alone Java program that show the device screen of an Android phone
at a computer. It's typically used for demo and training.

## Prerequisites ##

* Java 6
* Android SDK, installed and configured
* Environment variable ANDROID_HOME (or ANDROID_SDK_HOME) pointing the the Android SDK installation directory
* USB driver for the target Android device installed

## Execution ##

Grab the application JAR file (droidAtScreen-*nnn*.jar) and double-click it or
run it at the command-line with

    $ java -jar droidAtScreen-1.2.jar

The application will search for the ADB (Android Debug Bridge) executable using the environment variables mentioned above.
If it cannot find the executable, it will prompt you for the full path.

Plug-in your Android device using the USB cable and a device frame will shortly pop-up.

## Commands ##

### Mouse ###

* Left click - tap on the touchscreen
* Scroll click - click on device menu button
* Right click - tap and hold on touchscreen
* Press left button, drag the mouse, release left button - swipe from point where mouse button was hold to point where it is released

### KeyBoard ###
Keyboard supports anly ASCII characters, only Enaglish can be used to send text to device.

* Input any English text
* Ctrl + V - send to device text that is being copied on computer
* Home key - device home button
* End key - device power button
* Esc key - device back button
* Enter key - enter button from virtual keyaboard
* Delete - deletes text behind insertion point
* Backspace - deletes text in front of insertion point
* Space bar - adds a space in text
* Tab - move to next element fromk layout