cmake_minimum_required(VERSION 3.4)
project(heliosgame C)

find_package(JNI REQUIRED)
include_directories(${JNI_INCLUDE_DIRS})

file(GLOB helios_game_sources src/main/c/*.c src/main/c/*.h)
add_library(heliosgame ${helios_game_sources})
target_link_libraries(heliosgame ${JNI_LIBRARIES})