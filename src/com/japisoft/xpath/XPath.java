// (c) ALEXANDRE BRILLANT : http://www.japisoft.com
// All this work is confidential, you have rights to
// change and evolve it for your products but you
// have no rights to sell it, propose concurrent works.
// Morever any changes to bugs or evolutions should
// be send to JAPISOFT that needs to maintain a
// valid version and has all rights on the product.

package com.japisoft.xpath;

import java.io.*;

import com.japisoft.framework.xml.parser.document.Document;
import com.japisoft.xpath.node.AbstractNode;
import java.util.Hashtable;
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
public class XPath {
	
	private static final String NO_EXPRESSION_FOUND = "No expression found";
	private static final String NO_KIT_FOUND = "No kit found";
	private XPathKit kit;

	/** Here we pass a kit specific to the XML tree format like DOM,
	JDOM, fastParser lightweight node */
	public XPath(XPathKit kit) {
		super();
		this.kit = kit;
		if (kit == null) {
			throw new RuntimeException( "An XPathKit is required" );
		}
	}

//@@
	static {
		System.out.println( "JXP 1.3.9 - 30 Day Evaluation Version\nhttp://www.japisoft.com" );
	}
//@@	

	/** This constructor searches the Kit using the system property
	 * com.japisoft.jxp.kit. An Runtime exception is thrown is the
	 * property is not defined or if the kit provided is not found
	 */
	public XPath() {
		String loc = System.getProperty("com.japisoft.jxp.kit");
		if (loc == null)
			throw new RuntimeException("Can't find the property value com.japisoft.jxp.kit");
		try {
			this.kit = (XPathKit) (Class.forName(loc)).newInstance();
		} catch (Exception ex) {
			throw new RuntimeException("Can't load the class " + loc, ex);
		}
	}

	private Object refNode;
	private boolean navCache = true;

	/** Reset the cache for the navigator part. By default to true. Note that this method could
	 * create more memory usage. If you use very large document it may be better to disable it. If
	 * you alter your document after parsing, you Must disable it for avoiding bad XPath result. */
	public void setNavigatorCache( boolean cache ) {
		this.navCache = cache;
	}

	/** @return if the navigator cache is available. By default to true */
	public boolean hasNavigatorCache() {
		return navCache;
	}

	/** Reset the reference node for resolution. It may be the root node. This
	 * element is required before evaluate the current expression */
	public void setReferenceNode(Object node) {
		if ( node != refNode ) {
			this.refNode = kit.getBetterReferenceNode( node );
			if ( navCache )
				innerCache = new Hashtable();
		}
	}

	private String expr;
	private AbstractNode exprNode;

	/** Reset the XPath expression. This method should be called once for each XPath expression
	 * because a pre-parsing is made. If the expression is invalid an exception XPathException is thrown.
	 * @param expr an XPath expression */
	public void setXPathExpression(String expr) throws XPathException {
		if ( this.expr != null && 
				this.expr.equals( expr ) )	// We provide the same expression
			return;

		this.expr = expr;
		XPathParser p = new XPathParser( new StringReader( expr ) );
		XPathResolver r = null;
		if ( "true".equals( 
			System.getProperty( "jxp.tree.debug" ) ) ) {
			r = new DebugXPathResolver();
		} else
			r = new TreeXPathResolver();

		p.setXPathResolver(r);
		try {
			p.yyparse();
		} catch (Throwable th) {
			if ( "true".equals( System.getProperty( "jxp.debug" ) ) )
				th.printStackTrace();
			throw new XPathException(
				"Error with " + expr + " :" + th.getMessage(),
				p.getCurrentPos());
		}
		this.exprNode = r.getRootParsedNode();
		if (exprNode == null) {
			throw new XPathException( "Inner error null exprNode", 0 );
		}
	}

	/** Resolve the XPath expression for the reference node. If no XPath expression is passed or
	 * no reference node is known a RuntimeException is thrown. This function
	 * always returns a NodeSet even if the result is not a NodeSet, in this last
	 * case this is always an empty NodeSet */
	public NodeSet resolve() throws XPathException {
		if (exprNode == null) {
			throw new RuntimeException( NO_EXPRESSION_FOUND );
		}
		Object res = resolveAny();
		if (res instanceof NodeSet)
			return (NodeSet) res;
		return new NodeSet();
	}

	/** Resolve the XPath expression, user has to cast correctly the result as
	 * NodeSet,
	 * String,
	 * Double
	 * Boolean
	 */
	public Object resolveAny() throws XPathException {
		if (exprNode == null) {
			throw new XPathException( NO_EXPRESSION_FOUND, 0 );
		}
		try {
			XPathContext context = new XPathContext(refNode, kit);
			context.setCache( navCache );
			context.setNavigatorCacheContent( innerCache );
			context.setNamespaceDeclarationModel(htNamespace);
			context.setVariableModel(htVariable);
			return exprNode.eval(context);
		} catch (Throwable th) {
			if ("true".equals(System.getProperty("jxp.debug")))
				th.printStackTrace();
			throw new XPathException(th.getMessage(), 0);
		}
	}
	
	private Hashtable htNamespace;

	/** Add a namespace declaration
	 * If this prefix apperars in the XPath expression then the tied uri
	 * if used for discovering node from the uri namespace
	 */
	public void addNamespaceDeclaration(String prefix, String uri) {
		if (htNamespace == null) {
			htNamespace = new Hashtable();
		}
		htNamespace.put(prefix, uri);
	}

	/** Remove a namespace declaration for this prefix */
	public void removeNamespaceDeclaration(String prefix) {
		if (htNamespace != null) {
			htNamespace.remove(prefix);
		}
	}

	private Hashtable htVariable;

	private void storeVariable(String name, Object value) {
		if (htVariable == null) {
			htVariable = new Hashtable();
		}
		htVariable.put(name, value);
	}

	/** Set a variable value from the XPath expression */
	public void addVariable(String name, int value) {
		storeVariable(name, new Double(value));
	}

	/** Set a variable value from the XPath expression */
	public void addVariable(String name, boolean value) {
		storeVariable(name, new Boolean(value));
	}

	/** Set a variable value from the XPath expression */
	public void addVariable(String name, double value) {
		storeVariable(name, new Double(value));
	}

	/** Set a variable value from the XPath expression */
	public void addVariable(String name, String value) {
		storeVariable(name, value);
	}

	/** Set a variable value from the XPath expression */
	public void addVariable(String name, NodeSet value) {
		storeVariable(name, value);
	}

	/** Remove the variable binding for this name, if an XPath expression
	 * contains this name, an error will be thrown
	 */
	public void removeVariable(String name) {
		if (htVariable != null) {
			htVariable.remove(name);
		}
	}

	/** Set a feature support for the current kit. A RuntimeException
	 * should be thrown by the kit that doesn't support such feature */
	public void setFeature(String feature, boolean enable) {
		if (kit == null)
			throw new RuntimeException(NO_KIT_FOUND);
		kit.setFeature(feature, enable);
	}

	/** @return true if the feature is supported by the current kit */
	public boolean isFeatureSupported(String feature) {
		if (kit == null)
			throw new RuntimeException(NO_KIT_FOUND);
		return kit.isFeatureSupported(feature);
	}

	/** Return a list of supported features by the current kit */
	public String[] getSupportedFeatures() {
		if (kit == null)
			throw new RuntimeException(NO_KIT_FOUND);
		return kit.getSupportedFeatures();
	}

	//////////////////////////////////////////////////

	private Hashtable innerCache = null; 

	public static void main(String[] args) throws Throwable {
		XPath xp = new XPath( new com.japisoft.xpath.kit.FastParserKit() );

		com.japisoft.framework.xml.parser.FPParser p = new com.japisoft.framework.xml.parser.FPParser();			
		Document doc = p.parse(new FileInputStream( "/home/japisoft/japisoft/japisoft/japisoft/soft/jxp/samples/shone/test.xml" ) );
		xp.setReferenceNode(
			doc.getRoot() );
		xp.setXPathExpression(
			"count(/form_data/users/user)" );
		Object o = xp.resolveAny();
		System.out.println( "RESULT = " + o );
	}

}

// XPath ends here
