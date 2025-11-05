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

package com.japisoft.editix.action.dtdschema.generator.transformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.japisoft.editix.action.dtdschema.generator.MetaAttribute;
import com.japisoft.editix.action.dtdschema.generator.MetaNode;
import com.japisoft.editix.action.dtdschema.generator.MetaObject;
import com.japisoft.editix.action.dtdschema.generator.Transformer;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor) */
public class DTDTransformer extends AbstractTransformer {

	public String getType() {
		return "DTD";
	}
	
	public boolean hasVersion() { return false; }

	protected void generateMetaNode( MetaNode node, StringBuffer sb ) {
		sb.append( System.getProperty( "line.separator" ) );
		sb.append( "<!ELEMENT ").append( node.getName() ).append( "  " );

		if ( node.getChildren().size() == 0 ) {
			if ( node.acceptText() )
				sb.append( "( #PCDATA )" );
			else
				sb.append( "EMPTY" );
		}

		for ( int i = 0; i < node.getChildren().size(); i++ ) {
			if ( i == 0 ) {
				sb.append( "( " );
				if ( node.acceptText() )
					sb.append( "#PCDATA | " );
			}

			boolean choiceMode = false;
			
			if ( i > 0 ) {
				if ( sequenceMode ) {
					sb.append( " , " );
				} else {
					sb.append( " | " );
					choiceMode = true;
				}
			}

			MetaNode child = ( MetaNode )( node.getChildren().get( i ) );
			sb.append( child.getName() );

			if ( !choiceMode ) {
			
				if ( node.hasMultipleOccurence( child ) ) {
					
					if ( node.canBeMissing( child ) ) 
					
						sb.append( "*" );
					
					else
						
						sb.append( "+" );
					
				} else
				if ( node.canBeMissing( child ) ) {
					
					sb.append( "?" );
					
				}
				
			}

			if ( i == node.getChildren().size() - 1 ) {
				sb.append( " )" );
			}
			
			if ( choiceMode ) {
				sb.append( "*" );
			}
			
		}

		sb.append( ">" ).append( System.getProperty( "line.separator" ) );
		if ( node.hasAttributes() ) {
			sb.append( "<!ATTLIST " ).append( node.getName() ).append( System.getProperty( "line.separator" ) );
			for ( int i = 0; i < node.getAttributes().size(); i++ ) {
				MetaAttribute ma = ( MetaAttribute )node.getAttributes().get( i );

				String finalType = translateType( ma.getType() );

				sb.append( ma.getName() ).append( " " ).append( finalType ).append( " ").append(
						ma.isAlways() ? "#REQUIRED" : "#IMPLIED" ).append( System.getProperty( "line.separator" ) );
			}
			sb.append( ">" ).append( System.getProperty( "line.separator" ) );
		}
	}
	
	private String translateType( String type ) {
		String finalType = "CDATA";
		
		if ( MetaObject.ID_TYPE.equals( type ) )
			finalType = "ID";
		else
		if ( MetaObject.IDREF_TYPE.equals( type ) )
			finalType = "IDREF";

		return finalType;
	}
	
}

