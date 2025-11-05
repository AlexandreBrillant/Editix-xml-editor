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

import net.sf.saxon.om.DocumentInfo;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.value.AtomicValue;
import net.sf.saxon.value.Closure;

public class VariableImpl 
		implements Variable {

	private String name;
	private String type;
	private Object value;
	private int line;

	public VariableImpl(
			String name,
			String type,
			Object value,
			int line ) {
		this.name = name;
		this.type = type;
		this.value = value;
		this.line = line;
	}
	
	private NodeDebug node = null;
	
	public VariableImpl(
		NodeDebug node
	) {
		this.node = node;		
	}

	public String getName() {
		if ( node != null )
			return node.getVariableName();
		return name;
	}

	public String getType() {
		if ( node != null ) {
			Object value = node.getValue();
			return prettyType( value );
		}
		if ( type == null )
			return prettyType( value );
		return type;
	}

	public Object getValue() {
		if ( node != null )
			return prettyValue( node.getValue() );
		return prettyValue( value );
	}

	public int getLine() {
		return line;
	}

	private String prettyType( Object value ) {
		if ( value instanceof AtomicValue ) {
			AtomicValue av = ( AtomicValue )value;
			return av.getPrimitiveType().getDisplayName();
		} else
			return "node";
	}
	
	private String prettyValue( Object value ) {
		if( value instanceof Closure ) {
			try {
				Closure c = ( Closure )value;
				SequenceIterator si = c.iterate();
				int cpt = 0;
				while ( ( si.next() != null ) ) {
					cpt++;
				}
				return cpt + " item(s)";
			} catch( net.sf.saxon.trans.XPathException xe ) {
				return "Can't evaluate";
			}
		} else
		if ( value instanceof DocumentInfo ) {
			return "Document";
		}			
		return value.toString();
	}
	
}

