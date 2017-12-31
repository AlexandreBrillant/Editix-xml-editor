// (c) ALEXANDRE BRILLANT : http://www.japisoft.com
// All this work is confidential, you have rights to
// change and evolve it for your products but you
// have no rights to sell it, propose concurrent works.
// Morever any changes to bugs or evolutions should
// be send to JAPISOFT that needs to maintain a
// valid version and has all rights on the product.


package com.japisoft.xpath;

import com.japisoft.xpath.node.AbstractNode;

/**
This program is available under two licenses : 

1. For non commercial usage : 

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

2. For commercial usage :

You need to get a commercial license for source usage at : 

http://www.editix.com/buy.html

Copyright (c) 2018 Alexandre Brillant - JAPISOFT SARL - http://www.japisoft.com

@author Alexandre Brillant - abrillant@japisoft.com
@author JAPISOFT SARL - http://www.japisoft.com

*/
public class DebugXPathResolver implements XPathResolver {
    public DebugXPathResolver() {
	super();
	System.out.println( "" );
	System.out.println( "DEBUG START" );
    }

    public void startPredicate() {}
    
    public void nextFunction() {
	System.out.println( "next function" );
    }

    public void nextParam() {
	System.out.println( "next param" );
    }
    public void functionName( String qname ) {
	System.out.println( "function name : " + qname );
    }
    public void number( String number ) {
	System.out.println( "number : " + number );
    }
    public void literal( String literal ) {
	System.out.println( "literal : " + literal );
    }
    public void variable( String name ) {
	System.out.println( "variable : " + name );
    }
    public void unaryOperator( int type ) {
	System.out.println( "unary operator : " + type );
    }
    public void binaryOperator( String opExt ) {
	System.out.println( "binary operator : " + opExt );
    }
    public void binaryOperator( int type ) {
	System.out.println( "binary operator : " + type );
    }

    public void nodeType( String nodeType ) {
	System.out.println( "node type : " + nodeType );
    }
    public void processingInstruction( String name, String argument ) {
	System.out.println( "processing instruction : " + name + " " + argument );
    }
    public void nameTest( String nameTest, String namespacePrefix ) {
	System.out.println( "nameTest : " + nameTest );
    }
    public void axis( String axisName ) {
	System.out.println( "axis : " + axisName );
    }
    public void abbreviatedAxis( String axisName ) {
	System.out.println( "abbreviated axis : " + axisName );
    }

    public void nextPredicate() {
	System.out.println( "next predicate" );
    }

    public void nextExpression() {
	System.out.println( "next expression" );
    }

    public void nextLocationPath() {
	System.out.println( "next location path" );
    }

    public void root() {
	System.out.println( "root" );
    }

    public NodeSet getNodeSet() {
	return null;
    }

    public void init( XPathContext context, NodeSet set ) {
	System.out.println( "init" );
    }

    public AbstractNode getRootParsedNode() { return null; }
}

// DebugXPathResolver ends here
