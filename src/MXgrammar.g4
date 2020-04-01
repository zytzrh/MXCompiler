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
    |   FOR '(' for_init? ';'
        cond=expr? ';'
        for_update? ')'
        statement                                           #for_state
    |   RETURN expr? ';'                                    #return_state
    |   BREAK ';'                                           #break_state
    |   CONTINUE ';'                                        #continue_state
    |   ';'                                                 #empty_state
    |   expr ';'                                            #expr_state
    ;

for_init    //package into a block finally
    :   type varDefOne (',' varDefOne)*     //varDefnode->blocknode
    |   expr (',' expr)*    //must be '=' exprnode->bloknode
    ;

for_update
    :   expr (',' expr)*
    ;

expr
    :   THIS                                                #this_expr
    |   constant                                            #const_expr
    |   ID                                                  #id_expr
    // 1.func_name 2.variable
    |   expr op=('++' | '--')                               #postfix_expr
    |   newType                                             #new_expr
    // a direct expr or a function name
    |   expr '.' ID                                         #member_expr
    // check the whether the type of expr has a member named ID(it may be implemented as a func_name)
    |   func_name=expr '(' exprs? ')'                       #funcCall_expr
    // the former expr must be a func_name(ID or expr.ID or NEW new Type)
    |   array_name=expr '[' index=expr ']'                  #subscript_expr
    // check expr must be array type
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
    // check type
    |   <assoc=right> lhs=expr op='=' rhs=expr              #assign_expr
    // must right associative check type
    |   '(' expr ')'                                        #sub_expr
    ;

exprs
    :   expr (',' expr)*
    ;

newType
    :   NEW nonArray     ('[' expr ']')*('[' ']')+('[' expr ']')+   #wrong_newType
    |   NEW nonArray     ('[' expr ']')+('[' ']')*                  #array_newType
    //  do not call constructor yet?
//    |   nonArray     '(' ')'                                    #class_creator
    |   NEW nonArray                                                #normal_newType
    ;

constant
    :   Bool_constant
    |   Int_constant
    |   String_constant
    |   NULL
    ;


Int_constant: '0' | [1-9][0-9]*;
Bool_constant: TRUE | FALSE;
String_constant: '"' (ESC|.)*? '"';
fragment
ESC: '\\"' | '\\n' | '\\\\';    //Only used by other lexical rules




