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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.saxon.expr.LetExpression;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.expr.instruct.TraceExpression;

import net.sf.saxon.om.Item;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.trace.InstructionInfo;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.xpath.XPathEvaluator;

public class Saxon2NodeDebug implements NodeDebug {

	private TraceExpression ii;
	private XPathContext xc;
	private String systemUri;
	private VariablesContext cv;
	
	public Saxon2NodeDebug(
			TraceExpression ii,
			XPathContext xc,
			VariablesContext cv
	) {
		systemUri = 
			ii.getSystemId();
		this.ii = ii;
		this.xc = xc;
		this.cv = cv;
	}

	public String getSystemUri() {	
		return systemUri;
	}	
	
	public String getLocalName() {
		TraceExpression te = ( TraceExpression )ii;
		InstructionInfo info = te.getInstructionInfo();
		StructuredQName qName = info.getObjectName();
		String name = (String)info.getProperty("name");
		if ( name != null )
			return name;
		if ( qName != null ) {
			if ( isVariable() ) {
				getValue();				
				return "xsl:variable [" + qName.getDisplayName() + "]";
			}
			return qName.getDisplayName();
		}
		Iterator props = info.getProperties();
		String addOn = "";
		while ( props.hasNext() ) {
			String prop = ( String )props.next();
			Object val = info.getProperty( prop );
			if ( "match".equals( prop ) ) {
				addOn += " [" + val + "]";
			} else
			if ( "name".equals( prop ) ) {
				addOn += " [" + val + "]";
			}
		}
		return net.sf.saxon.om.StandardNames.getDisplayName( 
			te.getConstructType() 
		) + addOn;			
	}

	public String getVariableName() {
		TraceExpression te = ( TraceExpression )ii;
		InstructionInfo info = te.getInstructionInfo();
		StructuredQName qName = info.getObjectName();
		return qName.getDisplayName();
	}
	
	public boolean isVariable() {
		return ii.getConstructType() == 2013;
	}
	
	public String getPrefix() {
		return null;
	}
	
	public String getNamespace() {
		TraceExpression te = ( TraceExpression )ii;
		InstructionInfo info = te.getInstructionInfo();		
		String name = (String)info.getProperty("name");
		if ( name == null ) {
			StructuredQName qName = info.getObjectName();
			if ( qName != null ) {
				name = qName.getDisplayName();
			}
		}
		if ( name == null || isVariable() ) {
			return "http://www.w3.org/1999/XSL/Transform";			
		}
		return null;
	}	

	public int getLineNumber() {		
		return ii.getLineNumber();
	}

	public String getAttributeValue(String attName) {
		return null;
	}
	
	public Object getValue() {	
		TraceExpression te = ( TraceExpression )ii;
		InstructionInfo info = te.getInstructionInfo();
		Iterator props = info.getProperties();
		while ( props.hasNext() ) {
			String prop = ( String )props.next();
			Object val = info.getProperty( prop );
			
			if ( val instanceof LetExpression ) {
				LetExpression le = ( LetExpression )val;
				try {
					Object value = le.eval(xc);
					return value;
				} catch( XPathException exc ) {
					return null;
				}
			}
		}
		return null;
	}

	public Object evalXPath(String xpath) throws Exception {
		SequenceIterator it = xc.getCurrentIterator();
		Item current = it.current();
		XPathEvaluator xp = new XPathEvaluator();
		return xp.evaluate( xpath, current );
	}

	public ArrayList getXPathContext() {

		ArrayList res = null;
		
		SequenceIterator it = xc.getCurrentIterator();
		try {
			Item current = it.current();
			it = it.getAnother();
			Item i;			
			do {
				i = it.next();
				if ( i != null ) {
					if ( i instanceof NodeInfo ) {
						NodeInfo ni = ( NodeInfo )i;
						if ( res == null )
							res = new ArrayList();						
						res.add(							
							new XPathNodeContextImpl(
									ni.getNodeKind() == 3 ? "text()" : ni.getDisplayName(),
									ni.getLineNumber(),
									i.equals( current ) )								
						);
					}
				}

			} while ( i != null );			
		} catch (XPathException e) {
		}

		return res;
	}	

	public List<Variable> getVariables() {
		return cv.getVariables();
	}

	public List<Variable> getParameters() {
		return cv.getParameters();
	}

}

