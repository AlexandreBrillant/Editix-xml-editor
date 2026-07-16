%{
  import java.io.*;
%}

%token <sval> LITERAL
%token <sval> AXIS_NAME
%token <sval> PI
%token <sval> NODE_TYPE 
%token <sval> TEXT_TYPE 
%token <sval> NUMBER
%token <sval> NAME
%token <sval> AXIS_SEP
%token <sval> OR
%token <sval> AND
%token <sval> SUPE
%token <sval> INFE
%token <sval> DIFF
%token <sval> DS
%token <sval> DP

%nonassoc '|'
%left DIFF INFE SUPE '='
%nonassoc '$' ':' '[' ']' '/' '.' '*' '@' '(' ')' AXIS_SEP
%nonassoc DS DP
%left '+' '-' 
%left NEG

%%

input: locationPath {}
| expr { resolver.nextExpression(); }
;

locationPath: relativeLocationPath {}
| absoluteLocationPath {}
;

absoluteLocationPath : '/' { resolver.root(); }
| '/' relativeLocationPath { resolver.root();  }
| abbreviatedAbsoluteLocationPath { }
;

relativeLocationPath : step {} // resolver.nextLocationPath(); }
| relativeLocationPath '/' step {} { resolver.nextLocationPath(); }
| abbreviatedRelativeLocationPath { }
;

abbreviatedRelativeLocationPath : relativeLocationPath DS step { resolver.nextLocationPath(); resolver.abbreviatedAxis( XPathResolver.ABBREVIATED_DESCENDANT );  }
;

abbreviatedAbsoluteLocationPath : DS relativeLocationPath { resolver.nextLocationPath(); resolver.abbreviatedAxis( XPathResolver.ABBREVIATED_DESCENDANT ); }
;

step : axisSpecifier nodeTest predicates {}
| nodeTest predicates {}
| abbreviatedStep {}
;
 
axisSpecifier : AXIS_NAME AXIS_SEP { resolver.axis( $1 ); }
| abbreviatedAxisSpecifier {}
;

abbreviatedAxisSpecifier : '@' { resolver.abbreviatedAxis( XPathResolver.ABBREVIATED_ATTRIBUTE ); }

abbreviatedStep : '.' { resolver.abbreviatedAxis( XPathResolver.ABBREVIATED_SELF ); resolver.nameTest( null, null ); resolver.nextLocationPath(); }
| DP { resolver.abbreviatedAxis( XPathResolver.ABBREVIATED_ANCESTOR ); resolver.nameTest( null, null ); }
;

predicates: {}
| predicate predicates {}
;

nodeTest: nameTest {}
| TEXT_TYPE { resolver.nodeType( $1 ); }
| NODE_TYPE '(' ')' { resolver.nodeType( $1 ); }
| PI '(' LITERAL ')' { resolver.processingInstruction( $1, $3 ); }
| PI '(' ')' { resolver.processingInstruction( $1, null ); }
;

nameTest: '*' { resolver.nameTest( XPathResolver.ABBREVIATED_NAMETEST, null ); }
| NAME { resolver.nameTest( $1, null ); }
| NAME ':' NAME { resolver.nameTest( $3, $1 ); }
| NAME ':' '*' { resolver.nameTest( "*", $1 ); }
;

predicate: '[' predicateExpr ']' { resolver.nextPredicate(); }
;

predicateExpr : expr {}
;

expr : unionExpr {}
;

unionExpr : orExpr {}
| unionExpr '|' orExpr { resolver.binaryOperator( resolver.UNION ); }

orExpr : andExpr {}
| orExpr OR andExpr { resolver.binaryOperator( resolver.OR ); }
;

andExpr : equalityExpr { }
| andExpr AND equalityExpr { resolver.binaryOperator( resolver.AND ); }
;

equalityExpr : relationalExpr {}
| equalityExpr '=' relationalExpr { resolver.binaryOperator( resolver.EQUAL ); }
| equalityExpr DIFF relationalExpr { resolver.binaryOperator( resolver.NOT_EQUAL ); }
;

relationalExpr : additiveExpr { resolver.nextExpression(); }
| relationalExpr '<' additiveExpr { resolver.binaryOperator( resolver.INF ); }
| relationalExpr '>' additiveExpr { resolver.binaryOperator( resolver.SUP ); }
| relationalExpr INFE additiveExpr { resolver.binaryOperator( resolver.INFE ); }
| relationalExpr SUPE additiveExpr { resolver.binaryOperator( resolver.SUPE ); }

additiveExpr : multiplicativeExpr {}
| additiveExpr '+' multiplicativeExpr { resolver.binaryOperator( resolver.ADD ); }
| additiveExpr '-' multiplicativeExpr { resolver.binaryOperator( resolver.MINUS ); }

multiplicativeExpr : unaryExpr {}
| multiplicativeExpr '*' unaryExpr { resolver.binaryOperator( resolver.STAR ); } 
| multiplicativeExpr NAME unaryExpr { resolver.binaryOperator( $2 ); }
;

unaryExpr : pathExpr {}
| '-' %prec NEG unaryExpr { resolver.unaryOperator( resolver.MINUS ); }
;

pathExpr : filterExpr {}
| locationPath {}
| filterExpr DS relativeLocationPath {}
| filterExpr '/' relativeLocationPath {}
;

filterExpr : primaryExpr {}
| filterExpr predicate {}
;

primaryExpr : '$' NAME { resolver.variable( $2 ); }
| '(' expr ')' {}
| LITERAL { resolver.literal( $1.substring( 1, $1.length() - 1 ) ); }
| NUMBER { resolver.number( $1 ); }
| functionCall {}
;

functionCall : functionName '(' args ')' { resolver.nextFunction(); }
|functionName '(' ')' { resolver.nextFunction(); }
;

functionName : NAME { resolver.functionName( $1 ); }
;

args : expr { resolver.nextParam(); }
|expr ',' args { resolver.nextParam(); }
;

%%

  private XPathToken lexer;
  private XPathResolver resolver;

  public void setXPathResolver( XPathResolver resolver ) {
      this.resolver = resolver;
  }

  public int getCurrentPos() { return lexer.getCurrentPos(); }

  private int yylex () {
    int yyl_return = -1;
    try {
      yylval = new XPathParserVal(0);
      yyl_return = lexer.yylex();
    }
    catch (IOException e) {
      throw new RuntimeException( e.getMessage() );
    }
    return yyl_return;
  }

  public void yyerror (String error) {
    throw new RuntimeException( error );
  }

  public XPathParser(Reader r) {
    lexer = new XPathToken(r, this);
  }










