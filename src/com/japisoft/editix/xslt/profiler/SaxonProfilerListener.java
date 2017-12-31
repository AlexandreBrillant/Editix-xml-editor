package com.japisoft.editix.xslt.profiler;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.expr.instruct.TraceExpression;
import net.sf.saxon.om.Item;
import net.sf.saxon.trace.InstructionInfo;

import com.icl.saxon.Context;
import com.icl.saxon.NodeHandler;
import com.icl.saxon.om.NodeInfo;
import com.icl.saxon.trace.TraceListener;
import com.japisoft.editix.ui.xslt.XSLTEditor;
import com.japisoft.editix.ui.xslt.profiler.ProfilerElement;
import com.japisoft.editix.xslt.debug.NodeDebug;
import com.japisoft.editix.xslt.debug.Saxon1NodeDebug;
import com.japisoft.editix.xslt.debug.Saxon2NodeDebug;
import com.japisoft.editix.xslt.debug.SaxonTraceListener;
import com.japisoft.editix.xslt.debug.Variable;
import com.japisoft.editix.xslt.debug.VariablesContext;
import com.japisoft.xmlpad.IXMLPanel;

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
public class SaxonProfilerListener implements TraceListener,
		net.sf.saxon.lib.TraceListener, VariablesContext {

	private IXMLPanel container;
	public SaxonProfilerListener( IXMLPanel container ) {
		this.container = container;
	}

	private static String NAMESPACE_XSLT = "http://www.w3.org/1999/XSL/Transform";

	// SAXON 1

	public void open() {}
	public void close() {}
	public void toplevel(NodeInfo arg0) {}
	public void enterSource(NodeHandler arg0, Context arg1) {}
	public void leaveSource(NodeHandler arg0, Context arg1) {}

	private HashMap mapResult = null;
	
	public void enter(NodeInfo arg0, Context arg1) {
		enter( arg0, new Saxon1NodeDebug( arg0, arg1 ) );
	}

	private void enter( Object arg0, NodeDebug nd ) {
		// Only for XSLT element
		if ( NAMESPACE_XSLT.equals( nd.getNamespace() ) ) {
			
			if ( mapResult == null )
				mapResult = new HashMap();
			ProfilerElement pe = ( ProfilerElement )mapResult.get( arg0 );
			if ( pe == null ) {
				pe = new ProfilerElement();
				pe.name = SaxonTraceListener.getPrettyLocalName( nd );
				pe.iteration = 1;
				pe.startingTime = System.currentTimeMillis();
				pe.line = nd.getLineNumber();
				mapResult.put( arg0, pe );
			} else
				pe.iteration++;
		}
	}

	public void leave(NodeInfo arg0, Context arg1) {
		leaveSure( arg0 );
	}

	public void leaveSure( Object arg0 ) {
		if ( mapResult != null ) {
			ProfilerElement pe = ( ProfilerElement )mapResult.get( arg0 );
			if ( pe != null ) {
				pe.totalTime += ( System.currentTimeMillis() - pe.startingTime );
			}
		}
	}

	// SAXON 2

	public void open(net.sf.saxon.Controller arg0) {

	};
	public void setOutputDestination(PrintStream arg0) {
	}

	public void enter(InstructionInfo arg0, XPathContext arg1) {
		if ( arg0 instanceof TraceExpression )
			enter( arg0, new Saxon2NodeDebug( ( TraceExpression )arg0, arg1, this ) );	
	}

	public void leave(InstructionInfo arg0) {
		if ( arg0 != null )
			leaveSure( arg0 );
	}

	public void stopResult() {
		if ( mapResult != null ) {
			Collection col = mapResult.values();
			ArrayList l = new ArrayList( col );
			Collections.sort( l, new Comparator() {
				public int compare(Object arg0, Object arg1) {
					ProfilerElement a = ( ProfilerElement )arg0;
					ProfilerElement b = ( ProfilerElement )arg1;
					return (int)( b.totalTime - a.totalTime );
				}
			});
			container.setProperty( XSLTEditor.PROFILER_PROPERTY, l );
		}
	
		this.container = null;
	}


	public void startCurrentItem(Item arg0) {}

	public void endCurrentItem(Item arg0) {}
	

	public List<Variable> getParameters() {
		return null;
	}
	public List<Variable> getVariables() {
		return null;
	}

}
