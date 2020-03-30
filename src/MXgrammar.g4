grammar MXgrammar;
import LexerRule;

program
    :   defUnit*
    ;

defUnit
    :   classDef
    |   funcDef
    |   varDef
    ;

classDef
    :   CLASS ID '{' (varDef | funcDef | constructDef)* '}' ';'         //varDef must be only declaration
    ;

funcDef:
    (type | VOID) ID '(' formalParas? ')' block
    ;

varDef
    :   type varDefOne (',' varDefOne)* ';'
    ;

constructDef
    :   ID '(' formalParas? ')' block
    ;

type
    :   type '[' ']'            #arrayType
    |   nonArray                #nonArrayType
    ;


nonArray
    :   BOOL
    |   INT
    |   STRING
    |   ID  //for class
    ;


formalParas       //return a critical node
    :   formalPara (',' formalPara)*
    ;

formalPara
    :   type ID
    ;

varDefOne
    :   ID ('=' expr)?
    ;

// For Block
block
    : '{' statement* '}'
    ;

statement
    :   block                                               #block_stat
    |   varDef                                              #varDef_state
    |   IF '(' cond=expr ')' statement (ELSE statement)?    #if_state
    |   WHILE '(' cond=expr ')' statement                   #while_state
    |   FOR '(' init=expr? ';' cond=expr? ';' update=expr? ')'
        statement                                           #for_state
    |   RETURN expr? ';'                                    #return_state
    |   BREAK ';'                                           #break_state
    |   CONTINUE ';'                                        #continue_state
    |   ';'                                                 #empty_state
    |   expr ';'                                            #expr_state
    ;


expr:   expr op=('++' | '--')                               #postfix_expr
    |   <assoc=right> NEW creator                           #new_expr   //a direct expr or a function name
    |   expr '.' ID                                         #member_expr
    |   func_name=expr '(' exprs? ')'                       #funcCall_expr // the former expr must be a func name including new int
    |   expr '[' index=expr ']'                             #subscript_expr
    |   <assoc=right> op=('++' | '--') expr                 #prefix_expr
    |   <assoc=right> op=( '+' | '-' ) expr                 #prefix_expr
    |   <assoc=right> op=( '!' | '~' ) expr                 #prefix_expr
    |   lhs=expr op=('*' | '/' | '%') rhs=expr              #binary_expr
    |   lhs=expr op=('+' | '-') rhs=expr                    #binary_expr
    |   lhs=expr op=('<<' | '>>') rhs=expr                  #binary_expr
    |   lhs=expr op=('<' | '>' | '<=' | '>=') rhs=expr      #binary_expr
    |   lhs=expr op=('==' | '!=') rhs=expr                  #binary_expr
    |   lhs=expr op='&' rhs=expr                            #binary_expr
    |   lhs=expr op='^' rhs=expr                            #binary_expr
    |   lhs=expr op='|' rhs=expr                            #binary_expr
    |   lhs=expr op='&&' rhs=expr                           #binary_expr
    |   lhs=expr op='||' rhs=expr                           #binary_expr
    |   <assoc=right> lhs=expr op='=' rhs=expr              #binary_expr    //must right associative
    |   '(' expr ')'                                        #sub_expr
    |   THIS                                                #this_expr
    |   constant                                            #const_expr
    |   ID                                                  #id_expr
    ;

exprs
    :   expr (',' expr)*
    ;

creator
    :   nonArray     ('[' expr ']')*('[' ']')+('[' expr ']')+   #wrong_creator
    |   nonArray     ('[' expr ']')+('[' ']')*                  #array_creator
//    |   nonArray     '(' ')'                                    #class_creator
    |   nonArray                                                #naive_creator
    ;

constant
    :   BoolLITERAL
    |   IntegerLITERAL
    |   StringLITERAL
    |   NULL
    ;


BoolLITERAL: TRUE | FALSE;
IntegerLITERAL: '0' | [1-9][0-9]*;
StringLITERAL: '"' (ESC|.)*? '"';
fragment
ESC: '\\"' | '\\n' | '\\\\';    //Only used by other lexical rules




