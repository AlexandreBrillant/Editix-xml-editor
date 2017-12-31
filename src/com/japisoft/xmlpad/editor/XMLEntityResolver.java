package com.japisoft.xmlpad.editor;

import java.util.Hashtable;

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
public class XMLEntityResolver extends Hashtable {

    public static String LT_ENTITY = "lt";
    public static String GT_ENTITY = "gt";
    public static String QUOTE_ENTITY = "quote";
    public static String AMP_ENTITY = "amp";

    public XMLEntityResolver() {
	super();
	setEnableDefaultXMLEntities( true );
    }

    /** Supports for standard XML Entities : '<,>,",&' */
    public void setEnableDefaultXMLEntities( boolean enable ) {
	if ( enable ) {
	    storeEntityValue( '<', LT_ENTITY );
	    storeEntityValue( '>', GT_ENTITY );
	    storeEntityValue( '"', QUOTE_ENTITY );
	    storeEntityValue( '&', AMP_ENTITY );
	} else {
	    removeEntityValue( '<' );
	    removeEntityValue( '>' );
	    removeEntityValue( '"' );
	    removeEntityValue( '&' );
	}
    }

    /** Store the tied entity for the character value */
    public void storeEntityValue( char value, String entityName ) {
	put( new TemporaryCharacter( value ), entityName );
	storeEntity( entityName, value );
    }

    public void removeEntityValue( char value ) {
	TemporaryCharacter tc = new TemporaryCharacter( value );
	String entity = ( String )get( tc );
	if ( entity != null ) {
	    remove( tc );
	    removeEntity( entity );
	}
    }

    private TemporaryCharacter tc = new TemporaryCharacter();

    /** @return an entity for the char value */
    public String getEntityValue( char value ) {
	tc.setValue( value );
	return ( String )get( tc );
    }

    /** @return <code>true</code> if value is known for an entity */
    public boolean hasEntityValue( char value ) {
	return ( getEntityValue( value ) != null );
    }

    private Hashtable htEntityValue = null;

    /** Store an entity value */
    public void storeEntity( String name, char value ) {
	if ( htEntityValue == null )
	    htEntityValue = new Hashtable();
	htEntityValue.put( name, new TemporaryCharacter( value ) );
    }

    /** Remove an entity value */
    public void removeEntity( String name ) {
	if ( htEntityValue == null )
	    return;
	htEntityValue.remove( name );
    }

    /** @return an entity value for the entity name */
    public char getEntityResolution( String entityName ) throws UnknownEntity {
	if ( htEntityValue == null )
	    throw new UnknownEntity();
	TemporaryCharacter tc = ( TemporaryCharacter )htEntityValue.get( entityName );
	if ( tc == null )
	    throw new UnknownEntity();
	return tc.getValue();
    }

    ////////////////////////////////////////////////////////////////////////////////////

    // Better than character 
    public class TemporaryCharacter {
	private char value;

	public TemporaryCharacter( char value ) {
	    setValue( value );
	}

	public void setValue( char value ) {
	    this.value = value;
	}

	public char getValue() { 
	    return value;
	}

	public TemporaryCharacter() {
	    super();
	}

	public boolean equals( Object obj ) {
	    if ( obj instanceof TemporaryCharacter ) {
		return ( ( ( TemporaryCharacter )obj ).hashCode() == hashCode() );
	    } else
		return super.equals( obj );
	}

	public int hashCode() {
	    return ( int )value;
	}
    }
}

// XMLEntityResolver ends here
