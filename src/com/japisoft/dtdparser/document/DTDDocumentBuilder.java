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

package com.japisoft.dtdparser.document;

import com.japisoft.dtdparser.CannotFindElementException;
import com.japisoft.dtdparser.node.*;

/**
 * Build the DTD document
 *
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public interface DTDDocumentBuilder {

    /** @return the root of the DTD document */
    public RootDTDNode getRoot();

    /** Factory for building DTDNode */
    public void setNodeFactory( DTDNodeFactory factory );

    /** Start the DTD definition */
    public void notifyStartDTD();
    /** Stop the DTD definition */
    public void notifyStopDTD();

    /** Found a comment */
    public void notifyComment( String comment );


    // Entity usage
    
    public static final int INTERNAL_ENTITY = 0;
    public static final int SYSTEM_ENTITY = 1;
    public static final int PUBLIC_ENTITY = 2;

    /** Found an entity */
    public void notifyEntity( String entity, boolean parameter, int type, String value );
    /** Found a tag definition */
    public void notifyStartElement( String e );
    /** End of the tag definition */
    public void notifyStopElement();

    /** Item equals element name or #PCDATA */
    public void notifyElementChoiceItem( String item );

    /** Item equals element name or EMPTY or ANY or #PCDATA */
    public void notifyElementIncludeItem( String item );

    /** Notify '(' meet for the element declaration */
    public void notifyStartElementChildren();

    /** Notify operator '+' or '*' or '?' */
    public void notifyOperator( char operator );

    /** Notify ')' meet for the element declaration */
    public void notifyStopElementChildren();

    // Attribute value type
    public static final int ID_ATT_VAL = 0;
    public static final int IDREF_ATT_VAL = 1;
    public static final int ENTITY_ATT_VAL = 2;
    public static final int ENTITIES_ATT_VAL = 3;
    public static final int NMTOKEN_ATT_VAL = 4;
    public static final int NMTOKENS_ATT_VAL = 5;
    public static final int CDATA_ATT_VAL = 6;

    // Attribute usage
    public static final int REQUIRED_ATT = 0;
    public static final int IMPLIED_ATT = 1;
    public static final int FIXED_ATT = 2;

    /**
     * Found an attribute definition
     * @param element Element tag
     * @param id Attribute id
     * @param valueType ID, IDREF, ENTITY, ENTITIES, NMTOKEN, NMTOKENS or CDATA
     * @param enum a <code>String[]</code> value
     * @param attDec REQUIRED, IMPLIED or FIXED
     * @param def a <code>String</code> value or ""
     */
    public void notifyAttribute( 
				String element, 
				String id, 
				int valueType, 
				String[] enume, 
				int attDec, 
				String def ) throws CannotFindElementException;

    /** @return the first element for local DTD */
    public String getFirstElement();

}





