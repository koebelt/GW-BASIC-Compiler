# create a build directory

mkdir -p build

# change to the build directory

cd build

# run cmake to configure the build environment

cmake ..

# build the project

make

# if a file (./build.sh -f <file>) is specified, run the program

if [ "$1" = "-f" ]; then
    ./GWBasicCompiler ../$2
fi
