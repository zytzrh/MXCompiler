set -e
cd "$(dirname "$0")"
export CCHK="java -classpath /ulib/java/antlr-4.8-complete.jar:./bin Main codegen"
#export CCHK="java -classpath /mnt/d/Code/Compiler/MXCompiler/Antlr/antlr-4.8-complete.jar:./bin Main codegen"
cat > MXcode.mx
$CCHK
#cat < IRout.txt