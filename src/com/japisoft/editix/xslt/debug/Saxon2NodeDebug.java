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
import java.util.Collections;
import java.util.List;

import net.sf.saxon.expr.LetExpression;
import net.sf.saxon.expr.LocalBinding;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.expr.instruct.ApplyTemplates;
import net.sf.saxon.expr.instruct.FixedElement;
import net.sf.saxon.expr.instruct.ForEach;
import net.sf.saxon.expr.instruct.GlobalParam;
import net.sf.saxon.expr.instruct.GlobalVariable;
import net.sf.saxon.expr.instruct.Instruction;
import net.sf.saxon.expr.instruct.LocalParam;
import net.sf.saxon.expr.instruct.TemplateRule;
import net.sf.saxon.expr.instruct.ValueOf;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XPathExecutable;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmEmptySequence;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmValue;
import net.sf.saxon.trace.Traceable;

public class Saxon2NodeDebug implements NodeDebug {
	private Traceable traceable;
	private XPathContext xc;
	private VariablesContext cv;
	private String systemUri;	
	private Processor processor;
	
	public Saxon2NodeDebug(
			Traceable traceable,
			XPathContext xc,
			VariablesContext cv
	) {
		this.traceable = traceable;
		this.xc = xc;
		this.cv = cv;
		this.systemUri = traceable.getLocation().getSystemId();
		this.processor = new Processor( false );	
	}
	
	@Override
	public String getSystemUri() {
		return systemUri;
	}
	
	@Override
	public String getLocalName() {
		if ( traceable == null )
			return null;

		if ( traceable instanceof ForEach ) {
			ForEach fe = ( ForEach)traceable;
			return "for-each [" + fe.getSelectExpression() + "]";
		}
		
		if ( traceable instanceof ApplyTemplates ) {
			ApplyTemplates at = (ApplyTemplates)traceable;
			return "apply-template [" + at.getSelectExpression() + "]";
		}
		
		if ( traceable instanceof ValueOf ) {
			ValueOf vo = ( ValueOf )traceable;
			return "value-of [" + vo.getSelect() + "]";
		}
		
		if ( traceable instanceof TemplateRule ) {
			TemplateRule tr = ( TemplateRule )traceable;
			return "template [" + tr.getMatchPattern().toShortString() + "]";
		}
		
		if ( traceable instanceof FixedElement ) {
			FixedElement fe = ( FixedElement )traceable;
			return fe.getFixedElementName().getDisplayName();
		}
		
		if ( traceable instanceof LetExpression ) {
			LetExpression le = ( LetExpression )traceable;
			if ( le instanceof LocalBinding ) {
				StructuredQName var = ( ( LocalBinding )le ).getVariableQName();
				return "variable [" + var.getDisplayName() + "]";
			}
				
		}
		
		return traceable.getClass().getSimpleName();
	}
	
	@Override
	public String getVariableName() {
		return getLocalName();
	}
	
	@Override
	public boolean isVariable() {
	    return traceable instanceof GlobalVariable
	            || traceable instanceof GlobalParam
	            || traceable instanceof LocalParam;
	}
	
	@Override
	public String getPrefix() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNamespace() {
		if ( isVariable() )
			return "http://www.w3.org/1999/XSL/Transform";
		if ( traceable == null )
			return null;
		StructuredQName qName = traceable.getObjectName();
		if ( qName != null )
			return qName.getNamespaceUri().toString();
		return null;
	}
	
	@Override
	public int getLineNumber() {
		if ( traceable == null )
			return -1;
		if ( traceable.getLocation() != null )
			return traceable.getLocation().getLineNumber();
		return -1;
	}
	
	@Override
	public String getAttributeValue(String attName) {
		if ( traceable == null )
			return null; 
		if ( traceable instanceof Instruction ) {
			Instruction intruction = ( Instruction )traceable;
			Object value = intruction.getProperty(attName);
			if ( value != null )
				return value.toString();
		}
		return null;
	}
	
	@Override
	public Object getValue() {
		if ( xc == null || xc.getStackFrame() == null )
			return null;
		
		Sequence[] values = xc.getStackFrame().getStackFrameValues();
		if ( values == null || values.length == 0 )
			return null;
		List<String> result = new ArrayList<>();
		for ( Sequence seq : values ) {
			if ( seq != null )
				result.add( seq.toString() );
		}
		return result.isEmpty() ? null : result;
	}
	
	@Override
	public Object evalXPath(String xpath) throws Exception {
		if ( xc == null )
			return XdmEmptySequence.getInstance();
		Item current = xc.getContextItem();
		if ( !( current instanceof NodeInfo ) )
			return XdmEmptySequence.getInstance();
		
		XPathCompiler compiler = processor.newXPathCompiler();
		XPathExecutable executable = compiler.compile( xpath );
		XPathSelector selector = executable.load();

		XdmItem xdmItem = XdmValue.wrap( current ).itemAt( 0 );		
		selector.setContextItem( xdmItem );
		return selector.evaluate();	
	}
	
	@Override
	public List<Variable> getXPathContext() {
		if ( xc == null ) return Collections.emptyList();

		List<Variable> variables = new ArrayList<Variable>(); 
		
		Item current = xc.getContextItem();
		if ( current instanceof NodeInfo ) {
			/*
			NodeInfo cursor = (NodeInfo)current;
			while ( cursor != null ) {
				Variable v = new VariableImpl( cursor );
				variables.add( v );
				cursor = cursor.getParent();
			}
			*/
			variables.add( new VariableImpl( (NodeInfo)current ) );
		}
		
		return variables;
		
	}
	
   @Override
    public List<Variable> getVariables() {
	   return cv != null ? cv.getVariables() : Collections.emptyList();
    }

    @Override
    public List<Variable> getParameters() {
    	return cv != null ? cv.getParameters() : Collections.emptyList();
    }	

}


/*

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
*/
