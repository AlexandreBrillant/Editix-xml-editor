package com.japisoft.framework.xml.parser;

import org.xml.sax.*;

import com.japisoft.framework.collection.FastVector;

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
public class AttributesSax2Impl implements Attributes {
    public AttributesSax2Impl() {
	super();   
    }

    private FastVector vURI = new FastVector();
    private FastVector vLocalName = new FastVector();
    private FastVector vType = new FastVector();
    private FastVector vQName = new FastVector();
    private FastVector vValue = new FastVector();

    /** Add a new attribute */
    public void addAttribute( String prefix, String localName, String uri, String type, String value ) {
	vURI.add( uri );
	vLocalName.add( localName );
	vType.add( type );
	if ( prefix != null ) {
	    vQName.add( prefix + ":" + localName );
	} else
	    vQName.add( localName );
	vValue.add( value );
    }

    public int getLength () {
	return vURI.size();
    }

    public String getURI ( int index ) { 
	return (String)vURI.get( index );
    }

    public String getLocalName (int index) {
	return (String)vLocalName.get( index );
    }

    public String getQName (int index) {
	return (String)vQName.get( index );
    }

    public String getType (int index) {
	return (String)vType.get( index );
    }

    public String getValue (int index) {
	return (String)vValue.get( index );
    }

    public int getIndex (String uri, String localName) {
	for ( int i = 0; i < getLength(); i++ ) {
	    if ( uri.equals( vURI.get( i ) ) && 
		 localName.equals( vLocalName.get( i ) ) )
		return i;
	}
	return -1;
    }

    public int getIndex (String qName) {
	return vQName.indexOf( qName );
    }

    public String getType (String uri, String localName) {
	int i = getIndex( uri, localName );
	if ( i > -1 )
	    return (String)vType.get( i );
	return null;
    }

    public String getType (String qName) {
	int i = getIndex( qName );
	if ( i > -1 )
	    return (String)vType.get( i );
	return null;
    }

    public String getValue (String uri, String localName) {
	int i = getIndex( uri, localName );
	if ( i > -1 )
	    return (String)vValue.get( i );
	return null;
    }

    public String getValue (String qName) {
	int i = getIndex( qName );
	if ( i > -1 )
	    return (String)vValue.get( i );
	return null;
    }

    public String toString() {
	StringBuffer sb = new StringBuffer();
	sb.append( "[" );
	for ( int i = 0; i < getLength(); i++ ) {
	    if ( i > 0 )
		sb.append( "," );
	    sb.append( "{" ).append( "uri=" ).append( getURI( i ) ).append( ",local=" ).append( getLocalName( i ) );
	    sb.append( ",qname=" ).append( getQName( i ) ).append( ",type=" ).append( getType( i ) );
	    sb.append( ",value=" ).append( getValue( i ) ).append( "}" );
	}
	sb.append( "]" );
	return sb.toString();
    }

}

// AttributesImpl ends here
