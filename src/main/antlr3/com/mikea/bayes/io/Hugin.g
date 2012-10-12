grammar Hugin;

options {output=AST;}
tokens { DOMAIN; NET; ATTR_LIST; ATTR; NODE; INT_LIST; STR; STR_LIST; FLOAT_LIST; FLOAT_MATRIX; POTENTIAL; FROM; TO; }


@parser::header {
    package com.mikea.bayes.io;
}

@lexer::header {
    package com.mikea.bayes.io;
}


@members {
    private ErrorReporter errorReporter = null;
    public void setErrorReporter(ErrorReporter errorReporter) {
        this.errorReporter = errorReporter;
    }
    @Override
    public void emitErrorMessage(String msg) {
        errorReporter.reportError(msg);
    }
}

@lexer::members {
    private ErrorReporter errorReporter = null;
    public void setErrorReporter(ErrorReporter errorReporter) {
        this.errorReporter = errorReporter;
    }
    @Override
    public void emitErrorMessage(String msg) {
        errorReporter.reportError(msg);
    }
}


domainDefinition: domainHeader domainElement* EOF
    -> ^(DOMAIN domainHeader domainElement*);

domainHeader: 'net' '{' attributeList '}'
    -> ^(NET attributeList);

attributeList: attribute*
    -> ^(ATTR_LIST attribute*);
attribute: attributeName '=' attributeValue ';'
    -> ^(ATTR attributeName attributeValue);

attributeName: ID;
attributeValue: integerList | stringLiteral | stringList | floatList | floatMatrix;

integerList: '(' INTEGER+ ')'
    -> ^(INT_LIST INTEGER+);
floatMatrix: '(' floatList+ ')'
    -> ^(FLOAT_MATRIX floatList+);
floatList: '(' FLOAT+ ')'
    -> ^(FLOAT_LIST FLOAT+);
stringList: '(' stringLiteral+ ')'
    -> ^(STR_LIST stringLiteral+);

domainElement: basicNode | potential;

basicNode: 'node' ID '{' attributeList '}'
    -> ^(NODE ID attributeList);

potential: 'potential' '(' ID '|' (ID)* ')' '{' attributeList '}'
    -> ^(POTENTIAL ^(TO ID) ^(FROM ID*) attributeList);


stringLiteral: '"'! (
        // EscapeSequence |
        ~('\\'|'"')
        )* '"'!;


ID  :   ('a'..'z'|'A'..'Z'|'_')+ ;
INTEGER: ('0'..'9')+;
FLOAT: ('0'..'9')+ '.' ('0'..'9')* | '.' ('0'..'9')+ ;
WHITESPACE : ( '\t' | ' ' | '\r' | '\n'| '\u000C' )+    { $channel = HIDDEN; } ;