# this script is called when the judge is building your compiler.
# no argument will be passed in.
set -e
cd "$(dirname "$0")"
mkdir -p bin
find ./src -name *.java | javac -d bin -classpath "/ulib/java/antlr-4.8-complete.jar" @/dev/stdin
#find ./src -name *.java | javac -d bin -classpath "/mnt/d/Code/Compiler/MXCompiler/Antlr/antlr-4.8-complete.jar" @/dev/stdin