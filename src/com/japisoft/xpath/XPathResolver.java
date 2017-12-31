// (c) ALEXANDRE BRILLANT : http://www.japisoft.com
// All this work is confidential, you have rights to
// change and evolve it for your products but you
// have no rights to sell it, propose concurrent works.
// Morever any changes to bugs or evolutions should
// be send to JAPISOFT that needs to maintain a
// valid version and has all rights on the product.


package com.japisoft.xpath;

import com.japisoft.xpath.node.*;
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
public interface XPathResolver {
    public static final String ABBREVIATED_SELF = ".";
    public static final String ABBREVIATED_ANCESTOR = "..";
    public static final String ABBREVIATED_DESCENDANT = "//";
    public static final String ABBREVIATED_ATTRIBUTE = "@";

    public static final String ABBREVIATED_NAMETEST = "*";

    /**At the end of the parsing process, this value contains the result set
       @return the current node set */
    public AbstractNode getRootParsedNode();

    /** Select the root node */
    public void root();

    /** Prepare the current nodeSet */
    public void nextLocationPath();
    
    /** Prepare the current nodeSet */
    public void nextPredicate();

    /** Next expression for the predicate */
    public void nextExpression();

    /** Reset the current axis :
	ancestor, ancestor-or-self, attribute, child, descendant, descendant-or-self, following, following-sibling, namespace  parent, preceding, preceding-sibling, self.
    */
    public void axis( String axisName );

    /** '.', '..', '//' */
    public void abbreviatedAxis( String axisName );

    /** Reset the nameTest after the axis name. Can be '*' */
    public void nameTest( String nameTest, String namespacePrefix );

    /** Reset the processing instruction after the axis name
	@param name Processing instruction
	@param argument Argument for the processing instruction, can be <code>null</code>
    */
    public void processingInstruction( String name, String argument );

    /** Reset the nodeType : comment , text , processing-instruction , node */
    public void nodeType( String nodeType );

    /** or operator */
    public static final int OR = 1;
    /** and operator */
    public static final int AND = 2;
    /** = operator */
    public static final int EQUAL = 3;
    /** != operator */
    public static final int NOT_EQUAL = 4;
    /** < operator */
    public static final int INF = 5;
    /** > operator */
    public static final int SUP = 6;
    /** <= operator */
    public static final int INFE = 7;
    /** >= operator */
    public static final int SUPE = 8;
    /** + operator */
    public static final int ADD = 9;
    /** - operator */
    public static final int MINUS = 10;
    /** * operator */
    public static final int STAR = 11;
    /** div operator */
    public static final int DIV = 12;
    /** mod operator */
    public static final int MOD = 13;
    /** union operator */
    public static final int UNION = 14;

    /** Evaluate the current predicate with binary operator */
    public void binaryOperator( int type );

    /** Evaluate the current predicate with this binary operator
     * @param opExt can be equal to 'div' or 'mod' */
    public void binaryOperator( String opExt );

    /** Evaluate the current predicate expression with an unary operator */
    public void unaryOperator( int type );

    /** Evaluate the current variable */
    public void variable( String name );

    /** Current literal */
    public void literal( String literal );

    /** Current number */
    public void number( String number );

    /** Current function name */
    public void functionName( String qname );

    /** Add a parameter */
    public void nextParam();

    /** Evaluate the current function */
    public void nextFunction();

}

// XPathResolver ends here




