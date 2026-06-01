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

import net.sf.saxon.expr.Expression;
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
import net.sf.saxon.om.NodeName;
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

		if ( traceable instanceof FixedElement ) {
			FixedElement fe = ( FixedElement )traceable;
			return fe.getObjectName().getLocalPart();
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
		return null;
	}

	@Override
	public String getNamespace() {
		if ( traceable != null && traceable.getClass().toString().contains( ".instruct." ) )
			return SaxonTraceListener.NAMESPACE_XSLT;
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
	
	public List<Variable> getXPathContext() {
		if ( xc == null ) return Collections.emptyList();
		List<Variable> variables = new ArrayList<Variable>(); 
		Item current = xc.getContextItem();
		if ( current instanceof NodeInfo ) {
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

