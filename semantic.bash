set -e
cd "$(dirname "$0")"
cat > basic-2.mx
export CCHK="java -classpath /ulib/java/antlr-4.8-complete.jar:./bin Main"
#export CCHK="java -classpath /mnt/d/Code/Compiler/MXCompiler/Antlr/antlr-4.8-complete.jar:./bin main"
$CCHK