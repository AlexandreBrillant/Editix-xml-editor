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

package com.japisoft.framework.xml.parser.walker;

import com.japisoft.framework.xml.parser.node.*;

/**
 * Match tag with the good name
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public class NodeNameCriteria extends AbstractCriteria {
    private String name;
    
    /** Search for the good 'name' */
    public NodeNameCriteria( String name ) {
    	super();   
		this.name = name;
		if ( name == null )
		    throw new RuntimeException( "Invalid criteria name" );
    }
    
    public boolean isValid( FPNode node ) {    	
    	if ( "*".equals( name ) )
    		return node.isTag();
    	
    	if ( node.isTag() ) {
    		
    		if ( ignoreCase ) {
    			
    			if ( containsMode ) {
    				
    				return node.getContent().toLowerCase().contains( name.toLowerCase() );
    				
    			} else {
    				
    				return name.equalsIgnoreCase( node.getContent() );
    				
    			}
    			
    		} else {
    			
    			if ( containsMode ) {
    	
    				return node.getContent().contains( name );
    				
    			} else
    				
    				return name.equals( node.getContent() );
    			
    		}
    		
    	} else
    		
    		return false;
    	
    }

}

