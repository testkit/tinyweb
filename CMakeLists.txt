cmake_minimum_required (VERSION 2.8)
project (tinyweb)

#version number
set (tinyweb_VERSION_MAJOR 1)
set (tinyweb_VERSION_MINOR 0)

#build arch option
option(i386 "i386" OFF)
option(x86_64 "x86_64" OFF)

find_package(Threads REQUIRED)

add_executable(tinyweb websocket.c tinyweb.c mongoose.c)

target_link_libraries(tinyweb "${CMAKE_THREAD_LIBS_INIT}")
target_link_libraries(tinyweb ${CMAKE_DL_LIBS})

add_executable(cgi-getfield cgi-getfield.c)

add_executable(cgi-getcookie cgi-getcookie.c)  


IF(i386)
    set_target_properties(tinyweb PROPERTIES COMPILE_FLAGS "-m32" LINK_FLAGS "-m32")
    set_target_properties(cgi-getfield PROPERTIES COMPILE_FLAGS "-m32" LINK_FLAGS "-m32")
    set_target_properties(cgi-getcookie PROPERTIES COMPILE_FLAGS "-m32" LINK_FLAGS "-m32")
ENDIF()
IF(x86_64)
    set_target_properties(tinyweb PROPERTIES COMPILE_FLAGS "-m64" LINK_FLAGS "-m64")
    set_target_properties(cgi-getfield PROPERTIES COMPILE_FLAGS "-m64" LINK_FLAGS "-m64")
    set_target_properties(cgi-getcookie PROPERTIES COMPILE_FLAGS "-m64" LINK_FLAGS "-m64")
ENDIF()
