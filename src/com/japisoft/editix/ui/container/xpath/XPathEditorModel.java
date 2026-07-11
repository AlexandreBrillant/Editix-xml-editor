// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2026 Alexandre Brillant
//
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
// AI Training Restriction :
// This source code is provided for human use only.
// Using this code to train, fine-tune, or develop AI models,
// machine learning systems, or similar technologies is
// STRICTLY PROHIBITED. Violations will terminate all rights
// under the applicable license.

package com.japisoft.editix.ui.container.xpath;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;

import com.japisoft.editix.ui.container.SerializeStateObject;

public class XPathEditorModel implements SerializeStateObject {

	private String xpath;

	public String getXPath() {
		return xpath;
	}

	public void setXPath( String xpath ) {
		this.xpath = xpath;
	}

	public void restoreState(String serialize) {
		String[] tmp = serialize.split( "§" );
		xpath = tmp[ 0 ];
		columns = new ArrayList<XPathColumn>();
		for ( int i = 1; i < tmp.length; i++ ) {
			XPathColumn xpc = new XPathColumn();
			xpc.restoreState( tmp[ i ] );
			columns.add( xpc );
		}
	}

	public String serializeState() {
		if ( this.xpath == null )
			return null;
		StringBuffer sb = new StringBuffer( this.xpath );
		for ( int i = 0; i < getColumnCount(); i++ ) {
			sb.append( "§" );
			sb.append( getColumn( i ).serializeState() );
		}
		return sb.toString();
	}

	private List<XPathColumn> columns = null;
	
	public void addColumn( String name, String xpath ) {
		if ( columns == null ) {
			columns = new ArrayList<XPathColumn>();
		}
		columns.add( new XPathColumn( name, xpath ) );
	}
	
	public int getColumnCount() {
		if ( columns == null ) {
			return 0;
		}
		return columns.size();
	}

	public XPathColumn getColumn( int index ) {
		return columns.get( index );
	}

	@Override
	public String toString() {
		return xpath;
	}	
	
	// ---------------------------------------------

	public static class XPathColumn implements SerializeStateObject {
		private String name;
		private String xpathCol;

		XPathColumn( String name, String xpath ) {
			this.name = name;
			this.xpathCol = xpath;
		}

		public XPathColumn() {}

		public void restoreState(String serialize) {
			String[] tmp = serialize.split( "µ" );
			this.setName( tmp[ 0 ] );
			this.setXpath( tmp[ 1 ] );
		}

		public String serializeState() {
			return getName() + "µ" + getXpath();
		}

		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getXpath() {
			return xpathCol;
		}
		public void setXpath(String xpath) {
			this.xpathCol = xpath;
		}
		private XPathExpression xpe = null;
		public XPathExpression getXPathExpression( XPath xpath ) throws Exception {
			if ( xpe == null ) {
				xpe = xpath.compile( this.xpathCol );
			}
			return xpe;
		}
	}

}
