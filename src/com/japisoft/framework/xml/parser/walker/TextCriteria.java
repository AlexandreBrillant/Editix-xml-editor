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

package com.japisoft.framework.xml.parser.walker;

import com.japisoft.framework.xml.parser.node.*;

/**
 * Match text or sub-text
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public class TextCriteria extends AbstractCriteria {
    private String text;

    /** Match all Text node */
    public TextCriteria() {
    	super();
    }

    /** Match text nodes with the following sub-text */
    public TextCriteria( String text ) {
    	super();   
    	this.text = text;
    }
    
    public boolean isValid( FPNode node ) {
    	
    	if ( node.isText() ) {
    		
    		if ( text == null )
    			return true;
    		
    		String content = node.getContent();
    		
    		if ( content == null )
    			return false;
    		
    		if ( ignoreCase ) {

    			if ( containsMode ) {
    				
    				return content.toLowerCase().contains( text.toLowerCase() );
    				
    			} else {
    				
    				return content.equalsIgnoreCase( text );
    				
    			}
    			
    		} else {
    			
    			if ( containsMode ) {

    				return content.contains( text );
    				
    			} else {
    				
    				return text.equals( content );
    				
    			}
    			
    		}
    		
    	} else
    		
    		return false;

    }

}


