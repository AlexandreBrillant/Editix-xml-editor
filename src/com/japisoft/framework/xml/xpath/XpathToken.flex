package com.japisoft.xpath;

%%

%class XPathToken

%byaccj
%unicode
%pack

%{
  private XPathParser yyparser;

  public int getCurrentPos() { return yy_currentPos; }

  public XPathToken(java.io.Reader r, XPathParser yyparser) {
    this(r);
    this.yyparser = yyparser;
  }
%}

LITERAL = (\"[^\"]*\") | (\'[^']*\')

OR = "or"
AND = "and"

PI = "processing-instruction"
NODE_TYPE = "node"
TEXT_TYPE = "text\(\)"

AXIS_NAME = "ancestor" | "ancestor-or-self" | "attribute" | "child" | "descendant" | "descendant-or-self" | "following" | "following-sibling" | "namespace" | "parent" | "preceding" | "preceding-sibling" | "self"
NUMBER = [0-9]+ (\. [0-9]+)?

NAME = [^ \n\r\t0-9\[\]\:/,\"'\(\)=<>!+*/$@-]([^ \n\r\t\[\]\:/,\"'\(\)=<>!+*/$@])*

// NAME = [a-zA-Zñ]([a-zA-Z0-9\-\_ñ])*
AXIS_SEP = "::"
SUPE = ">="
INFE = "<="
DIFF = "!="
DP = ".."
DS = "//"

%%

{DP} {
       yyparser.yylval = new XPathParserVal( yytext() );
       return XPathParser.DP;
}

{DS} {
       yyparser.yylval = new XPathParserVal( yytext() );
       return XPathParser.DS;
}

"[" |
"]" |
"." |
"/" |
"@" |
"*" |
"(" |
")" |
">" |
"<" |
"=" |
"+" |
"-" |
"/" |
"|" |
"(" |
")" |
"," |
":" |
"$"
{ return (int) yycharat(0); }

{LITERAL} { 
       yyparser.yylval = new XPathParserVal( yytext() );
       return XPathParser.LITERAL; 
}

{INFE} {
       yyparser.yylval = new XPathParserVal( yytext() );
       return XPathParser.INFE;
}

{SUPE} {
       yyparser.yylval = new XPathParserVal( yytext() );
       return XPathParser.SUPE;
}

{DIFF} {
       yyparser.yylval = new XPathParserVal( yytext() );
       return XPathParser.DIFF;
}

{AND} {
       yyparser.yylval = new XPathParserVal( yytext() );
       return XPathParser.AND;
}

{OR} {
       yyparser.yylval = new XPathParserVal( yytext() );
       return XPathParser.OR;
}

{PI} {
       yyparser.yylval = new XPathParserVal( yytext() );
       return XPathParser.PI;
}

{TEXT_TYPE} {
       yyparser.yylval = new XPathParserVal( yytext() );
       return XPathParser.TEXT_TYPE;
}

{NODE_TYPE} {
       yyparser.yylval = new XPathParserVal( yytext() );
       return XPathParser.NODE_TYPE;
}


{AXIS_NAME} {
       yyparser.yylval = new XPathParserVal( yytext() );
       return XPathParser.AXIS_NAME;
}

{AXIS_SEP} {
       yyparser.yylval = new XPathParserVal( yytext() );
       return XPathParser.AXIS_SEP;
}

{NUMBER} {
       yyparser.yylval = new XPathParserVal( yytext() );
       return XPathParser.NUMBER;
}

{NAME} {
     yyparser.yylval = new XPathParserVal( yytext() );
     return XPathParser.NAME;
}

/* whitespace */
[ \t\n\r]+ { }

