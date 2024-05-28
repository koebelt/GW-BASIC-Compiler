# Specify the minimum version of CMake required
cmake_minimum_required(VERSION 3.10)

# Set the project name and version
project(GWBasicCompiler VERSION 1.0)

# Set the C standard
set(CMAKE_C_STANDARD 17)
set(CMAKE_C_STANDARD_REQUIRED True)

# Specify the directories where to find header files
include_directories(include)

# Add an executable with the name MyCompiler
# and specify the source files
add_executable(GWBasicCompiler
    src/main.c
    # src/lexer.c
    # src/parser.c
    # src/semantic.c
    # src/codegen.c
)

# Optionally, you can add compiler options or definitions
# target_compile_options(MyCompiler PRIVATE -Wall -Wextra -pedantic)
# target_compile_definitions(MyCompiler PRIVATE SOME_DEFINITION)

# Optionally, you can set the output directory for binaries
# set_target_properties(MyCompiler PROPERTIES RUNTIME_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/bin)
