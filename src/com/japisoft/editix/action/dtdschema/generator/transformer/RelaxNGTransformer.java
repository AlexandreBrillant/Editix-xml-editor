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

package com.japisoft.editix.action.dtdschema.generator.transformer;

import java.util.ArrayList;
import java.util.Vector;

import com.japisoft.editix.action.dtdschema.generator.MetaAttribute;
import com.japisoft.editix.action.dtdschema.generator.MetaNode;
import com.japisoft.editix.action.dtdschema.generator.MetaObject;
import com.japisoft.editix.action.dtdschema.generator.Transformer;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class RelaxNGTransformer extends AbstractTransformer {
	
	@Override
	protected void initTransform(MetaNode root, StringBuffer sb) {
		sb.append( "<grammar xmlns=\"http://relaxng.org/ns/structure/1.0\" datatypeLibrary=\"http://www.w3.org/2001/XMLSchema-datatypes\">\n" );
		sb.append( "\t<start>\n" );
		sb.append( "\t\t<ref name=\"" ).append( root.getName() ).append( ".element\"/>\n" );		
		sb.append( "\t</start>\n" );
	}
	
	@Override
	protected void closeTransform(MetaNode root, StringBuffer sb) {
		sb.append( "</grammar>\n" );
	}
	
	@Override
	protected void generateMetaNode( MetaNode element, StringBuffer sb ) {
		sb.append( "\n\t<define name=\"" ).append( element.getName() ).append( ".element\">\n" );
		sb.append( "\t\t<element name=\"" ).append( element.getName() ).append( "\">\n" );
		
		if ( element.acceptText() ) {
			sb.append( "\t\t\t<text/>\n" );
		} else
			if ( element.getChildren().size() == 0 ) {
				sb.append( "\t\t\t<empty/>\n" );
			}

		// Add attributes

		if ( element.hasAttributes() ) {
			Vector atts = element.getAttributes();
			for ( int i = 0; i < atts.size(); i++ ) {
				MetaAttribute att = ( MetaAttribute )atts.get( i );
				if ( !att.isAlways() )
					sb.append( "\t\t\t<optional>\n" );

					sb.append( "\t\t\t\t<attribute name=\"" ).append( att.getName() ).append( "\">\n" );
					if ( !att.getType().equals( MetaObject.TEXT_TYPE ) )
						sb.append( "\t\t\t\t\t<data type=\"" ).append( SchemaTransformer.translateType( att.getType() ) ).append( "\"/>\n" );
					else
						sb.append( "\t\t\t\t<text/>\n" );
					sb.append( "\t\t\t\t</attribute>\n" );
				
				if ( !att.isAlways() )
					sb.append( "\t\t\t</optional>\n" );
				
			}
		}
		
		if ( element.getChildren().size() > 0 ) {
			sb.append( "\t\t\t<zeroOrMore>\n" );
			
			if ( sequenceMode ) {
				sb.append( "\t\t\t\t<group>\n" );
			} else {
				sb.append( "\t\t\t\t<choice>\n" );
			}

			for ( int i = 0; i < element.getChildren().size(); i++ ) {
				MetaNode node = ( MetaNode )element.getChildren().get( i );
				sb.append( "\t\t\t\t\t<ref name=\"" ).append( node.getName() ).append( ".element\"/>\n" );
			}

			if ( sequenceMode ) {
				sb.append( "\t\t\t\t</group>\n" );
			} else {
				sb.append( "\t\t\t\t</choice>\n" );
			}
			sb.append( "\t\t\t</zeroOrMore>\n" );
		}

		sb.append( "\t\t</element>\n" );
		sb.append( "\t</define>\n" );
	}

	public String getType() {
		return "RNG";
	}

	public boolean hasVersion() {
		return true;
	}

}
