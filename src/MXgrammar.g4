grammar MXgrammar;
import LexerRule;

program
    :   defUnit*
    ;

defUnit
    :   classDef
    |   funcDef
    |   varDef ';'
    |   ';'
    ;

classDef
    :   CLASS ID '{' (varDef ';'| funcDef | constructDef)* '}' ';'         //varDef must be only declaration
    ;

funcDef:
    (type | VOID) ID '(' (formalPara (',' formalPara)*)? ')' block
    ;

varDef
    :   type varDefOne (',' varDefOne)*
    ;


constructDef
    :   ID '(' (formalPara (',' formalPara)*)? ')' block
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


//formalParas       //return a critical node
//    :   formalPara (',' formalPara)*
//    ;

formalPara
    :   type ID     //the only exclusive since ID hasn't interpreted as expr yet.
    ;

varDefOne
    :   ID ('=' expr)?
    ;

// For Block
block
    : '{' statement* '}'
    ;

statement
    :   block                                               #block_st
    |   varDef ';'                                          #varDef_st
    |   IF '(' cond=expr ')' then_st=statement
        (ELSE else_st=statement)?                           #if_st
    |   WHILE '(' cond=expr ')' statement                   #while_st
    |   FOR '(' for_init? ';'
        cond=expr? ';'
        for_update? ')'
        statement                                           #for_st
    |   RETURN expr? ';'                                    #return_st
    |   BREAK ';'                                           #break_st
    |   CONTINUE ';'                                        #continue_st
    |   ';'                                                 #empty_st
    |   expr ';'                                            #expr_st
    ;

for_init    //package into a block finally
    :   varDef                                               #for_init_withDef//VarDefnode->StatementNode->BlockNode
    |   expr (',' expr)*                                     #for_init_withoutDef//many ExprNode->many StatementNode->BlockNode
    ;

for_update
    :   expr (',' expr)*
    ;

expr
    :   THIS                                                #this_expr
    // lvalue but not assignable
    |   constant                                            #const_expr
    |   ID                                                  #id_expr
    // 1.func_name 2.variable   (not the same typically
    |   expr op=('++' | '--')                               #postfix_expr
    |   newType                                             #new_expr
    // a direct expr or a function name(cannot decide yet)
    |   expr '.' ID                                         #member_expr
    // check the whether the type of expr has a member named ID(it may be implemented as a func_name)
    |   func_name=expr '(' exprs? ')'                       #funcCall_expr
    // the former expr must be a func_name(ID or expr.ID or NEW new Semantic.ASTtype)
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




