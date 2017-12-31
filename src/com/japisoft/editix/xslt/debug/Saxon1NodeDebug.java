package com.japisoft.editix.xslt.debug;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.icl.saxon.Binding;
import com.icl.saxon.Context;
import com.icl.saxon.expr.NodeSetValue;
import com.icl.saxon.expr.TextFragmentValue;
import com.icl.saxon.expr.Value;
import com.icl.saxon.expr.XPathException;
import com.icl.saxon.om.NodeEnumeration;
import com.icl.saxon.om.NodeInfo;
import com.icl.saxon.style.StyleElement;
import com.icl.saxon.style.XSLParam;
import com.icl.saxon.style.XSLVariable;

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
public class Saxon1NodeDebug implements NodeDebug {

	private NodeInfo ni;
	private Context ctx;
	private String systemUri;

	public Saxon1NodeDebug( NodeInfo ni, Context ctx ) {
		this.ni = ni;
		this.ctx = ctx;
		systemUri = ni.getSystemId();
	}
	
	public String getSystemUri() {
		return systemUri;
	}

	public String getLocalName() {
		if ( ni.getNodeType() == NodeInfo.TEXT )
			return "text()";
		return ni.getLocalName();
	}

	public String getPrefix() {
		return ni.getPrefix();
	}

	public int getLineNumber() {
		return ni.getLineNumber();
	}

	public String getAttributeValue(String attName) {
		return ni.getAttributeValue( "", attName );
	}
	
	public String getNamespace() {
		return ni.getURI();
	}	

	public ArrayList getXPathContext() {

		NodeInfo ni = ctx.getContextNodeInfo();
		if ( ni != null ) {

			ArrayList al = new ArrayList();
			
			String name = ni.getDisplayName();
			if ( ni.getNodeType() == 3 )
				name = "text()";
			else
			if ( ni.getNodeType() == NodeInfo.ROOT )
				name = "document()";
			
			al.add( new XPathNodeContextImpl(
					name,
					ni.getLineNumber(),
					true ) );
			
			return al;
		}
		
		return null;
	}	

	public Object evalXPath(String xpath) throws Exception {
		return null;
	}	
	
	public List<Variable> getVariables() {
		
		ArrayList<Variable> bindings = new ArrayList<Variable>();
		if ( ni instanceof Node ) {
			scanVariablesParameters( ( Node )ni, bindings, true );
		}

		if ( bindings.size() > 0 ) {
			Collections.reverse( bindings );
			return bindings;
		}

		return null;
	}	

	public  List<Variable> getParameters() {

		ArrayList<Variable> bindings = new ArrayList<Variable>();
		if ( ni instanceof Node ) {
			scanVariablesParameters( ( Node )ni, bindings, false );
		}

		if ( bindings.size() > 0 ) {
			Collections.reverse( bindings );
			return bindings;
		}
				
		return null;
	}	
	
	private void scanVariablesParameters( Node from, ArrayList r, boolean variableMode ) {

		// Get the parent
		if ( from.getParentNode() instanceof Element ) {

			Node n = from;
			
			while ( n != null ) {
				
				if ( n instanceof Binding ) {
					
					Binding b = ( Binding )n;
					
					if ( ( variableMode && ( b instanceof XSLVariable ) ) || 
							( !variableMode && ( b instanceof XSLParam ) ) ) {
					
						String name = b.getVariableName();
						Value v = ctx.getBindery().getValue( b );
						
						if ( v != null ) {

							String typeName = "?";
							switch( v.getDataType() ) {
							case Value.ANY :
								typeName = "ANY";
								break;
							case Value.BOOLEAN :
								typeName = "BOOLEAN";
								break;
							case Value.NODESET :
								typeName = "NODESET";
								break;
							case Value.NUMBER :
								typeName = "NUMBER";
								break;
							case Value.OBJECT :
								typeName = "OBJECT";
								break;
							case Value.STRING :
								typeName = "STRING";
								break;
							}
							
							String resultValue = null;
							
							if ( v instanceof NodeSetValue && ( !( v instanceof TextFragmentValue ) )  ) {
								
								NodeSetValue nsv = ( NodeSetValue )v;
								try {
									 // Avoid to corrupt the variable state
									 NodeEnumeration enumeration = nsv.enumerate( ctx, false);
									 int count = 0;
									 while ( enumeration.hasMoreElements() ) {
										 enumeration.nextElement();
										 count++;
									 }
	
									resultValue = count + " nodes";
								} catch (XPathException e) {
									resultValue = "[CAN'T EVALUATE]";								
								}
	
							} else {
								try {
									resultValue = v.asString();
								} catch (XPathException e) {
									resultValue = "[CAN'T EVALUATE]";
								}
							}
	
							if ( v instanceof TextFragmentValue ) {
								typeName = "STRING";
							}
							
							r.add( 
									new VariableImpl( 
											name, 
											typeName, 
											resultValue, 
											( ( StyleElement )n ).getLineNumber() ) 
							);

						}
						
					}
					
				}

				if ( n.getPreviousSibling() == null ) {
					
					if ( n.getParentNode() instanceof Element ) {
						
						n = n.getParentNode();

					} else 
						break;	// No root element
					
				} else {
					
					n = n.getPreviousSibling();
					
				}
				
			}
			
		}
	}

	public boolean isVariable() {
		return "variable".equals( ni.getLocalName() );
	}
	
	public String getVariableName() {
		return getLocalName();
	}
	
	public Object getValue() {
		return null;
	}
	
}
