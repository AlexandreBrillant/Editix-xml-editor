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

/*
 * Created on Feb 22, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.japisoft.editix.action.dtdschema.generator.transformer;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.japisoft.editix.action.dtdschema.generator.MetaAttribute;
import com.japisoft.editix.action.dtdschema.generator.MetaNode;
import com.japisoft.editix.action.dtdschema.generator.Transformer;
import com.japisoft.xmlpad.action.ActionModel;
import com.japisoft.xmlpad.action.Properties;

/**
 * Create a minimal template
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 */
public class TemplateTransformer extends AbstractTransformer {

	public String transform(MetaNode root, ArrayList nodeCollection ) {
		StringBuffer sb = new StringBuffer();
		transform( 0, root, nodeCollection, sb );
		return sb.toString();
	}

	public boolean hasVersion() { return true; }

	void transform( 
			int deep, 
			MetaNode node, 
			ArrayList nodeCollection,  
			StringBuffer sb ) {
		if ( node.getChildren().size() > 0 ) {
			addRootHeader( deep, node, sb, false );
			
			Character indentChr = (Character) ActionModel.getProperty( 
					ActionModel.FORMAT_ACTION,
					Properties.INDENT_CHAR_PROPERTY, 
					new Character( ' ' ) );
			
			List<MetaNode> l = node.getChildren();
			for ( int i = 0; i < l.size(); i++ ) {
				transform( deep + 1, ( MetaNode )l.get( i  ), nodeCollection, sb );	
			}
			addRootFooter( deep, node, sb );
		} else {
			if ( node.acceptText() ) {
				addRootHeader( deep, node, sb, false );
				addRootFooter( deep, node, sb );
			} else
			addRootHeader( deep, node, sb, true );
		}
	}

	private void indent( int number, StringBuffer sb ) {
		Character indentChr = (Character) ActionModel.getProperty( 
			ActionModel.FORMAT_ACTION,
			Properties.INDENT_CHAR_PROPERTY, 
			new Character( ' ' ) );

		Integer indentNb = ( Integer )ActionModel.getProperty(
			ActionModel.FORMAT_ACTION,
			Properties.INDENT_SIZE_PROPERTY,
			new Integer( 1 ) );
			
		for ( int i = 0; i < number * indentNb.intValue(); i++ )
			sb.append( indentChr );
	}

	void addRootHeader( int indent, MetaNode root, StringBuffer sb, boolean close ) {
		sb.append( System.getProperty( "line.separator" ) );
		indent( indent, sb );
		sb.append( "<" ).append( root.getName() );
		if ( root.hasAttributes() ) {
			sb.append( " " );

			Vector list = root.getAttributes();
			for ( int i = 0; i < list.size(); i++ ) {
				if ( i > 0 )
					sb.append( " " );
				sb.append( ( (MetaAttribute)list.get( i ) ).getName() ).append( "=").append( "\"\"" );
			}
			if ( close )
				sb.append( "/>" );
			else
				sb.append( ">" );
		} else
			if ( close )
				sb.append( "/>" );
			else
				sb.append( ">" );
	}

	void addRootFooter( int indent, MetaNode root, StringBuffer sb ) {
		sb.append( System.getProperty( "line.separator" ) );
		indent( indent, sb );
		sb.append( "</" ).append( root.getName() ).append( ">" );
	}

	public String getType() {
		return "XML";
	}

}

