Testkit-stub
============================

Intel Tizen Web Test Utility: tinyweb


###Build binaries for TIZEN target with cross-compiler Tool###

1. Install tizen2.2.1 SDK with all component selected, refer to
   https://developer.tizen.org/downloads/sdk/installing-tizen-sdk

2. Run tizen tool "/path/to/tizen-sdk/tools/native-make" to build arm binary of stub

        cd CommandLineBuild
        /path/to/tizen-sdk/tools/native-make clean
        /path/to/tizen-sdk/tools/native-make -a armel -t GCC4.5

3. Run tizen tool "/path/to/tizen-sdk/tools/native-make" to build x86 binary of stub

        cd CommandLineBuild
        /path/to/tizen-sdk/tools/native-make clean
        /path/to/tizen-sdk/tools/native-make -a i386 -t GCC4.5
