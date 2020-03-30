lexer grammar LexerRule;
// Reserved Word
INT:    'int';
BOOL:   'bool';
STRING: 'string';
NULL:   'null';
VOID:   'void';

TRUE:   'true';
FALSE:  'false';

IF:     'if';
ELSE:   'else';

FOR:    'for';
WHILE:  'while';
BREAK:  'break';
CONTINUE:'continue';

RETURN: 'return';

NEW:    'new';

CLASS:  'class';
THIS:   'this';

ID: [a-zA-Z][a-zA-Z_0-9]*;

// ------ SKIPS ------
WS
    :   [ \t\n\r]+  -> skip
    ;

//Newline
//    :   (   '\r' '\n'?
//        |   '\n'
//        )   -> skip
//    ;

BlockComment // undefined, be cautious
    :   '/*' .*? '*/' -> skip
    ;

LineComment
    :   '//' ~[\r\n]*   -> skip
    ;