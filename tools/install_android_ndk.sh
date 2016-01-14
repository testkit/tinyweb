#!/usr/bin/env sh

wget http://dl.google.com/android/ndk/android-ndk-r10e-linux-x86_64.bin

chmod a+x android-ndk-r10e-linux-x86_64.bin
 
./android-ndk-r10e-linux-x86_64.bin >/dev/null 2>&1

mv android-ndk-r10e ../android-ndk 
