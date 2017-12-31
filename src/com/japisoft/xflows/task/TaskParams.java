package com.japisoft.xflows.task;

import java.util.HashMap;
import java.util.Iterator;

import com.japisoft.framework.xml.parser.node.FPNode;

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
public class TaskParams {

	public static final int XSLTPARAMS = 1; 

	private HashMap map = null;
	
	public void setParam( String name, String value ) {
		if ( map == null )
			map = new HashMap();
		if ( value != null )
			map.put( name, new ParamValue( 0, value ) );
		else
			map.remove( name );
	}

	public void setParam( String name, String value, int type ) {
		if ( map == null )
			map = new HashMap();
		if ( value != null )
			map.put( name, new ParamValue( type, value ) );
		else
			map.remove( name );
	}
		
	public String getParamValue( String name ) {
		if ( map == null )
			return null;
		if ( map.containsKey( name ) )
			return ( ( ParamValue )map.get( name ) ).value;
		return null;
	}

	public String getParamValue( String name, String defaultValue ) {
		if ( map == null )
			return defaultValue;
		if ( map.containsKey( name ) )
			return ( ( ParamValue )map.get( name ) ).value;
		return defaultValue;
	}
		
	public boolean getParamValueBoolean( String name ) {
		String v = getParamValue( name );
		return "true".equals( v );
	}

	public int getParamValueInteger( String name ) {
		String v = getParamValue( name );
		if ( v == null )
			return 0;
		try {
			return Integer.parseInt( v );
		} catch( NumberFormatException exc ) {
			return 0;
		}
	}

	public boolean hasParamValue( String name ) {
		if ( map == null )
			return false;
		ParamValue pv = ( ParamValue )map.get( name );
		return pv != null && !"".equals( pv.value );
	}

	public int getParamType( String name ) {
		if ( map == null )
			return 0;
		return ( ( ParamValue )map.get( name ) ).type;
	}

	public Iterator getParams() {
		if ( map == null )
			return new Iterator() {
				public boolean hasNext() {
					return false;
				}
				public Object next() {
					return null;
				}
				public void remove() {
				}
			};
		else
			return map.keySet().iterator();
	}

	public String toString() {
		return "" + map;
	}

	public FPNode toXML() {
		if ( map == null )
			return null;

		FPNode params = new FPNode( FPNode.TAG_NODE, "params" );
		
		for ( Iterator it = getParams(); it.hasNext(); ) {
			String key = ( String )it.next();
			String value = getParamValue( key );
			int type = getParamType( key );
			FPNode paramNode = new FPNode( FPNode.TAG_NODE, "param" );
			paramNode.setAttribute( "key", key );
			paramNode.setAttribute( "value", value );
			paramNode.setAttribute( "type", "" + type );
			params.appendChild( paramNode );
		}
		
		return params;
	}

	public void updateFromXML( FPNode params ) {
		map = new HashMap();
		for ( int i = 0; i < params.childCount(); i++ ) {
			FPNode node = params.childAt( i );
			String name = node.getAttribute( "key", "?" );
			String value = node.getAttribute( "value", "?" );
			int type = Integer.parseInt( node.getAttribute( "type", "0" ) );
			setParam( name, value, type );
		}
	}

	public TaskParams cloneParams() {
		TaskParams params = new TaskParams();
		if ( map != null )
			params.map = ( HashMap )map.clone();
		return params;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////

	class ParamValue {
		public int type;
		public String value;

		public ParamValue( int type, String value ) {
			this.type = type;
			this.value = value;
		}
		
		public String toString() {
			return type + "," + value;
		}
	}

}
