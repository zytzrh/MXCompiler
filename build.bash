set -e
cd "$(dirname "$0")"
mkdir -p bin
#find ./src -name *.java | javac -d bin -classpath "/Antlr/antlr-4.8-complete.jar" @/dev/stdin
find ./src -name *.java | javac -d bin -classpath "/mnt/d/Code/Compiler/MXCompiler/Antlr/antlr-4.8-complete.jar" @/dev/stdin