# this one is important
SET(CMAKE_SYSTEM_NAME Linux)

# specify the cross compiler
SET(CMAKE_C_COMPILER   "/usr/local/ostroxt-x86_64/sysroots/x86_64-ostroxtsdk-linux/usr/bin/x86_64-ostro-linux/x86_64-ostro-linux-clang")
SET(CMAKE_CXX_COMPILER "/usr/local/ostroxt-x86_64/sysroots/x86_64-ostroxtsdk-linux/usr/bin/x86_64-ostro-linux/x86_64-ostro-linux-clang++")

# where is the target environment
SET(CMAKE_FIND_ROOT_PATH  /usr/local/ostroxt-x86_64/sysroots/corei7-64-ostro-linux)

# search for programs in the build host directories
SET(CMAKE_FIND_ROOT_PATH_MODE_PROGRAM NEVER)
# for libraries and headers in the target directories
SET(CMAKE_FIND_ROOT_PATH_MODE_LIBRARY ONLY)
SET(CMAKE_FIND_ROOT_PATH_MODE_INCLUDE ONLY)

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -m64 -march=corei7 -mtune=corei7 -mfpmath=sse -msse4.2 -mlittle-endian -lpthread")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -m64 -march=corei7 -mtune=corei7 -mfpmath=sse -msse4.2 -mlittle-endian -lpthread")
