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

package com.japisoft.xmlpad.editor;

import java.util.Hashtable;

/**
 * Resolver for XML entity.
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
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


