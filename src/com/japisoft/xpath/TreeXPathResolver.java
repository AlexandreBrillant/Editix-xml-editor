// (c) ALEXANDRE BRILLANT : http://www.japisoft.com
// All this work is confidential, you have rights to
// change and evolve it for your products but you
// have no rights to sell it, propose concurrent works.
// Morever any changes to bugs or evolutions should
// be send to JAPISOFT that needs to maintain a
// valid version and has all rights on the product.

package com.japisoft.xpath;

import com.japisoft.xpath.node.*;
import java.util.Stack;

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
public final class TreeXPathResolver implements XPathResolver {

	public TreeXPathResolver() {
		super();
	}

	public void nextFunction() {}
	
	public void nextParam() {
		AbstractNode n = ( AbstractNode ) nodeStack.pop();
		Function f = null;
		for ( int i = nodeStack.size() - 1; i >= 0; i-- ) {
			if ( nodeStack.elementAt( i ) instanceof Function ) {
				f = ( Function ) nodeStack.elementAt( i );
				break;
			}
		}

		if ( f == null ) {
			return;
		}

		f.addNode( n );
	}

	private String lastFunctionName;

	public void functionName(String qName) {
		Function f = new Function();
		f.setName(qName);
		currentNode = f;
		nodeStack.push(currentNode);
	}

	public void number(String number) {
		com.japisoft.xpath.node.Number n = new com.japisoft.xpath.node.Number();
		n.setNumber(number);
		nodeStack.push(n);
	}

	public void literal(String literal) {
		Literal l = new Literal();
		l.setValue(literal);
		nodeStack.push(l);
	}

	public void variable(String name) {
		Variable v = new Variable();
		v.setName(name);
		nodeStack.push(v);
	}

	public void unaryOperator( int type ) {
		Expr exp1 = ( Expr ) nodeStack.pop();

		if ( type == XPathResolver.MINUS ) {
			
			if ( exp1 instanceof com.japisoft.xpath.node.Number ) {
				com.japisoft.xpath.node.Number n = (com.japisoft.xpath.node.Number)exp1;
				n.setNumber( "-" + n.getNumber() );
				nodeStack.push( exp1 );
				return;
			}			
		}

		Operator op = new Operator();
		op.setPredefinedOperator(type);
		op.addNode(exp1);
		nodeStack.push( op );
		currentNode = exp1;
	}

	public void binaryOperator(String opExt) {
		Expr exp1 = ( Expr ) nodeStack.pop();
		Expr exp2 = ( Expr ) nodeStack.pop();
		Operator op = new Operator();
		op.setOperator( opExt );
		op.addNode( exp2 );
		op.addNode( exp1 );
		nodeStack.push( op );
		currentNode = op;
	}

	public void binaryOperator(int type) {
		Expr exp1 = null;
		Expr exp2 = null;

		exp1 = (Expr) nodeStack.pop();
		exp2 = (Expr) nodeStack.pop();

		Operator op = new Operator();
		op.setPredefinedOperator( type );
		op.addNode( exp2 );
		op.addNode( exp1 );
		nodeStack.push( op );

		currentNode = op;
	}

	public void nodeType(String nodeType) {
		nameTest(null, null);
		((Node) currentNode).setType(nodeType);
	}

	public void processingInstruction(String name, String argument) {
		currentNode = new Node();
		nodeType( "processing-instruction" );
		((Node)currentNode).setName( "", null );
	}
	
	private String lastAxisName;
	private AbstractNode currentNode;
	private Stack nodeStack = new Stack();
	private AbstractNode root = null;
	
	public void nameTest(String nameTest, String namespacePrefix) {
		currentNode = new Node();
		((Node) currentNode).setName( nameTest, namespacePrefix );
		if (lastAxisName != null) {
			((Node) currentNode).setAxis( lastAxisName );
			((Node) currentNode).setAttributeMode( attributeMode );
		}
		lastAxisName = null;
		attributeMode = false;
		nodeStack.push(currentNode);
		if (root == null) {
			if ( !( nodeStack.elementAt( 0 ) instanceof Function ) ) {
				root = ( Node ) currentNode;
			}
		}
	}

	boolean attributeMode = false;
	
	public void abbreviatedAxis(String axisName) {
		if (ABBREVIATED_SELF == axisName) {
			this.lastAxisName = "self";
		} else if (ABBREVIATED_ANCESTOR == axisName) {
			this.lastAxisName = "parent";
		} else if (ABBREVIATED_DESCENDANT == axisName) {
			this.lastAxisName = "descendant-or-self";
			Node n = null;
			if ( currentNode instanceof Node ) {
				n = (Node)currentNode;
				if ( n == null )
					n = (Node) nodeStack.peek();
				n.setAxis(lastAxisName);
			}
		} else if (ABBREVIATED_ATTRIBUTE == axisName) {
			this.lastAxisName = "attribute";
			attributeMode = true;
		}
	}

	public void axis(String axisName) {
		this.lastAxisName = axisName;
	}
		
	public void nextPredicate() {
		AbstractNode n = (AbstractNode) nodeStack.pop();
		Predicate p = new Predicate();
		p.addNode(n);
		
		if ( nodeStack.size() > 0 ) {
			currentNode = ( Node ) nodeStack.peek();
			currentNode.addNode( p );
		} else
			nodeStack.push( currentNode );
	}

	public void nextExpression() {
		Expr exp = new Expr();
		if ( nodeStack.size() > 0 )
			currentNode = ( AbstractNode ) nodeStack.pop();
		exp.addNode( currentNode );
		nodeStack.push( exp );
	}

	public void nextLocationPath() {
		// Add the current node to the previous one
		if ((nodeStack.size() == 1)
			|| (nodeStack.size() >= 2
				&& !(nodeStack.elementAt(nodeStack.size() - 2)
					instanceof Node))) {
			currentNode = (AbstractNode) nodeStack.peek();
		} else {
			if  ( nodeStack.isEmpty() )	// For processing - instruction
				return;
			Node preNode = (Node) nodeStack.pop();
			currentNode = (Node) nodeStack.pop();
			currentNode.addNode(preNode);
			if ((nodeStack.size() >= 1)
				&& (nodeStack.elementAt(0) instanceof Function)) {
				nodeStack.push(currentNode);
				currentNode = preNode;
			} else {
				AbstractNode tmpNode = currentNode;
				currentNode = preNode;
				nodeStack.push( tmpNode );
			}
		}
	}

	public void root() {
		if (currentNode == null) {
			root = new Node();
			nodeStack.push(root);
			((Node) root).setFromRoot(true);
		} else {
			AbstractNode nTmp = (Node) currentNode;
			while (nTmp.getParentNode() != null) {
				nTmp = nTmp.getParentNode();
			}
			if ( nTmp instanceof Node )
				( (Node)nTmp ).setFromRoot(true);
		}
	}

	public AbstractNode getRootParsedNode() {
		if ((root != null)
			&& !(nodeStack.size() > 0
				&& nodeStack.elementAt(0) instanceof Expr)) {
			if (root instanceof Node) {
				Node n = (Node) root;
				if (!n.hasAxis())
					n.setAxis(lastAxisName);
			}
			return root;
		} else {
			if ( currentNode.getRootNode() != null )
				return currentNode.getRootNode();
			return currentNode;
		}
	}

}
// TreeXPathResolver ends here
