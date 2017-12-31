package com.japisoft.editix.ui.container.xpath;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;

import com.japisoft.editix.ui.container.SerializeStateObject;

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
public class XPathEditorModel implements SerializeStateObject {

	private String xpath;

	public String getXPath() {
		return xpath;
	}

	public void setXPath( String xpath ) {
		this.xpath = xpath;
	}

	public void restoreState(String serialize) {
		String[] tmp = serialize.split( "£" );
		xpath = tmp[ 0 ];
		columns = new ArrayList<XPathColumn>();
		for ( int i = 1; i < tmp.length; i++ ) {
			XPathColumn xpc = new XPathColumn();
			xpc.restoreState( tmp[ i ] );
			columns.add( xpc );
		}
	}

	public String serializeState() {
		StringBuffer sb = new StringBuffer( this.xpath );
		for ( int i = 0; i < getColumnCount(); i++ ) {
			sb.append( "£" );
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
		private String xpath;

		XPathColumn( String name, String xpath ) {
			this.name = name;
			this.xpath = xpath;
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
			return xpath;
		}
		public void setXpath(String xpath) {
			this.xpath = xpath;
		}
		private XPathExpression xpe = null;
		public XPathExpression getXPathExpression( XPath xpath ) throws Exception {
			if ( xpe == null ) {
				xpe = xpath.compile( this.xpath );
			}
			return xpe;
		}
	}

}
