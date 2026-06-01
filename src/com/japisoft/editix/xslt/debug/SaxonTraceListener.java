// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2025 Alexandre Brillant
// 
// For non-commercial usage :
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// See the GNU General Public License for more details: https://www.gnu.org/licenses/gpl-3.0
// 
// For commercial use or integration into proprietary software :
// A commercial license is required. Visit https://www.editix.com for details.

package com.japisoft.editix.xslt.debug;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.icl.saxon.Context;
import com.icl.saxon.NodeHandler;
import com.icl.saxon.om.NodeInfo;
import com.japisoft.editix.ui.xslt.debug.DebugContext;
import com.japisoft.editix.ui.xslt.debug.DebugElement;
import com.japisoft.editix.ui.xslt.debug.DebugVariable;
import com.japisoft.xmlpad.IXMLPanel;

import net.sf.saxon.Controller;
import net.sf.saxon.expr.StackFrame;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.expr.instruct.Block;
import net.sf.saxon.expr.instruct.DocumentInstr;
import net.sf.saxon.expr.instruct.ParameterSet;
import net.sf.saxon.expr.instruct.SlotManager;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.NamePool;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.trace.Traceable;
import net.sf.saxon.lib.TraceListener;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public class SaxonTraceListener extends CommonTraceListener 
		implements TraceListener, 
						com.icl.saxon.trace.TraceListener,
							VariablesContext {

	public static String NAMESPACE_XSLT = "http://www.w3.org/1999/XSL/Transform";
	
	public SaxonTraceListener( IXMLPanel panel ) {
		super( panel );
	}

	public void open() {}	
	public void close() {}
	public void startCurrentItem(Item currentItem) {}
	public void endCurrentItem(Item currentItem) {}

	private NamePool pool;	
	private ArrayList stack = null;
	private ArrayList storedVariables = null;

	//////////////

	public static String getPrettyLocalName( NodeDebug ni ) {
		if ( NAMESPACE_XSLT.equals( ni.getNamespace() ) ) {
			
			String res = ni.getLocalName();

			if ( "template".equals( ni.getLocalName() ) ) {
				
				if ( ni.getAttributeValue( "match" ) != null ) {
					res += " [" + ni.getAttributeValue( "match" ) + "]"; 
				} else
				if ( ni.getAttributeValue( "name" ) != null ) {
					res += " [" + ni.getAttributeValue( "name" ) + "]";
				}

			} else
			if ( "value-of".equals( ni.getLocalName() ) ) {

				res += " [" + ni.getAttributeValue( "select" ) + "]";
				
			} else
			if ( "if".equals( ni.getLocalName() ) ) {
				
				res += " [" + ni.getAttributeValue( "test" ) + "]";

			} else
			if ( "variable".equals( ni.getLocalName() ) ) {
				
				res += " [" + ni.getAttributeValue( "name" ) + "]";
				
			} else
			if ( "param".equals( ni.getLocalName() ) ) {
				
				res += " [" + ni.getAttributeValue( "name" ) + "]";

			} else
			if ( "for-each".equals( ni.getLocalName() ) ) {
				
				res += " [" + ni.getAttributeValue( "select" ) + "]";
				
			} else
			if ( "apply-templates".equals( ni.getLocalName() ) ) {
				
				if ( ni.getAttributeValue( "select" ) != null )
					res += " [" + ni.getAttributeValue( "select" ) + "]";
				
			} else
			if ( "call-template".equals( ni.getLocalName() ) ) {
				
				res += " [" + ni.getAttributeValue( "name" ) + "]";

			} else
			if ( "sequence".equals( ni.getLocalName() ) ) {
				
				res += " [" + ni.getAttributeValue( "select" ) + "]";
				
			}

			return res;

		} else
			return ni.getLocalName();
	}
	
	public void open(Controller arg0) {
	}
	
	public void setOutputDestination(PrintStream arg0) {
	}
	
	// Generic one
	public void enter( NodeDebug ni ) {
		if  ( noWait || 
				ni.getLocalName() == null )
			return;
		
		if ( stack == null )
			stack = new ArrayList();
		
		stack.add(
				new DebugElement(
						getPrettyLocalName( ni ),
						ni.getLineNumber(),
						!NAMESPACE_XSLT.equals( ni.getNamespace() ),
						ni.getSystemUri()
						) );		
		
		
		boolean mustWait = continueMode;

		if ( bmContext.matchLine(
				ni.getSystemUri(),
				( ni.getLineNumber() - 1 ), 
				panel.getMainContainer() ) ) {			
			mustWait = true;
		}
		
		if ( mustWait || 
				continueMode ) {

			// XPATH CONTEXT

			ArrayList<DebugElement> xpathContext = null;
			List<Variable> l = ni.getXPathContext();
			if ( l != null ) {

				xpathContext = new ArrayList<DebugElement>();

				for ( int i = 0; i < l.size(); i++ ) {

					if ( l.get( i ) instanceof XPathContextNode ) {
						XPathContextNode xcn = ( XPathContextNode )l.get( i );
						
						xpathContext.add(
	
								new DebugElement( 
										xcn.getName(),
										xcn.getLineNumber(),
										!xcn.currentOne(),
										ni.getSystemUri()
								)			
						);
						
					} else {

						Variable v = l.get( i );
						DebugElement de = new DebugElement( 
								v.getName(),
								v.getLine(),
								false,
								null );
						xpathContext.add( de );
						
					}
				}
			}

			// VARIABLES

			List<DebugVariable> variables = null;
			l = ni.getVariables();

			if ( l != null ) {
				
				variables = new ArrayList<DebugVariable>();
				for ( int i = 0; i < l.size(); i++ ) {
					
					Variable vc = ( Variable )l.get( i );
					variables.add(
						new DebugVariable( 
								vc.getName(),
								vc.getType(),
								vc.getValue(),
								vc.getLine()
						) 
					);

				}

			}
			
			// PARAMETERS
			
			List<DebugVariable> parameters = null;
			l = ni.getParameters();

			if ( l != null ) {
				
				parameters = new ArrayList<DebugVariable>();
				for ( int i = 0; i < l.size(); i++ ) {

					Variable vc = ( Variable )l.get( i );
					parameters.add(
						new DebugVariable( 
								vc.getName(),
								vc.getType(),
								vc.getValue(),
								vc.getLine()
						) 
					);

				}

			}
			
			panel.setProperty( "xslt.debug", 
					new DebugContext(
							ni.getSystemUri(),
							stack, 
							xpathContext, 
							variables,
							parameters ) );

			// Show the current line

			unhighLight( true );
			
			showCurrentLine( 
				ni.getSystemUri(), 
				ni.getLineNumber() - 1 
			);

			refreshResult();			
			waitForDebug();			
		}		
	}

	// Saxon v2.0

	
	@Override
	public void enter(Traceable instruction, Map<String, Object> properties, XPathContext context) {
		
		if ( instruction instanceof DocumentInstr )
			return;
		if ( instruction instanceof Block )
			return;
		
		Saxon2NodeDebug nodeDebug = new Saxon2NodeDebug( 
				instruction, context, this ); 

		ParameterSet ps = context.getLocalParameters();		
		parameters = null;

		StructuredQName[] names = ps.getParameterNames();
		
		int i = 1;
		for ( StructuredQName name : names ) {
			int index = ps.getIndex(name);
			if ( index > -1 ) {
				if ( parameters == null )
					parameters = new ArrayList<Variable>();
				Object obj = ps.getValue( index );
				parameters.add( 0, new VariableImpl( "Param " + i, null, obj, -1 ) );
				i++;
			}
			
		}
		
		StackFrame frame = context.getStackFrame();
		Sequence[] values = frame.getStackFrameValues();
		SlotManager map = frame.getStackFrameMap();
		variables = new ArrayList<Variable>();
		String type = "expr";
		
		if ( values != null && map != null ) {
			for ( i = 0; i < values.length; i++ ) {
				StructuredQName varName = map.getVariableMap().get( i );
				if ( varName != null ) {
					Sequence value = values[ i ];
					if ( value != null ) {
						Item item;
						StringBuilder sb = new StringBuilder();
						try {
							SequenceIterator iter = value.iterate();
							while ( ( item = iter.next() ) != null ) {
								if ( sb.length() > 0 )
									sb.append( ", " );
								if ( item instanceof NodeInfo ) {
									NodeInfo ni = ( NodeInfo )item;
									type = "node";
									sb.append( ni.getDisplayName() );
								} else
									sb.append( item.getUnicodeStringValue() );
							}
						} catch( Throwable th ) {
							
						}
						variables.add( new VariableImpl( varName.getDisplayName(), type, sb.toString(), -1 ) );
					}
				}
			}
		}

		enter( nodeDebug );
	}	
	
	@Override
    public void leave(Traceable instruction) {
		
    }

	// ------------------------------------------------------------
	
	// Saxon v1.0

	public void enter(NodeInfo arg0, Context arg1) {		
		enter( new Saxon1NodeDebug( arg0, arg1 ) );
	}

	public void leave(NodeInfo arg0, Context arg1) {
	}

	public void enterSource(NodeHandler arg0, Context arg1) {}
	

	public void leaveSource(NodeHandler arg0, Context arg1) {}
	public void toplevel(NodeInfo arg0) {}

	// ------------------------------------------------------------
	
	// Context variables
	
	private List<Variable> parameters,variables;
	
	public List<Variable> getParameters() {
		return parameters;
	}

	public List<Variable> getVariables() {
		return variables;
	}
	
}

