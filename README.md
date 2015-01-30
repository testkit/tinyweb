Tinyweb

This file is used for building tinyweb both Android and Tizen.

Android:
      if you want to build tinyweb for android, please download busybox-1.22.1, which we based on this version. And please download An  droid-ndk-r10d-linux-x86_64.bin file.
      
      wget http://dl.google.com/android/ndk/android-ndk-r9d-linux-x86_64.tar.bz2
      export PATH=$HOME/android-ndk-r10d/toolchain/platform-version/prebuild/linux-platform/bin:$PATH
      
      then you can modify some .config file to fit your needs.
      $ cd tinyweb/android/native/jni/ && /path/to/android-ndk-<version>/ndk-build
      -	Copy tinyweb/android/native/libs/ to folder tinyweb/android/assets/system/libs/
      -	Import project tinyweb to Android Developer Tool by location tinyweb /android
     
      make busybox to static file, copy busybox file to android/assets/system/libs/<platform>
      -	Export the android project to APK and install APK to android device

Tizen:
     ### build ARM 
     -  $ /path/to/tizen-sdk/tools/native-make clean 
     -  $ /path/to/tizen-sdk/tools/native-make -a armel -t GCC-4 5 
     -  $ mkdir arm  
     -  $ cp tinyweb arm/ 
     -  $ cp cgi-getcookie arm/ 
     -  $ cp cgi-getfield arm/ 
     -  $ cp server pem arm/ 
     -  $ cp * so arm/ 
     ### build IA 
     -  $ /path/to/tizen-sdk/tools/native-make clean      
     -  $ /path/to/tizen-sdk/tools/native-make -a i386 -t GCC-4 5 
     -  $ mkdir ia 
     -  $ cp tinyweb ia/ 
     -  $ cp cgi-getcookie ia/ 
     -  $ cp cgi-getfield ia/ 
     -  $ cp server pem ia/ 
     -  $ cp * so ia/ 
