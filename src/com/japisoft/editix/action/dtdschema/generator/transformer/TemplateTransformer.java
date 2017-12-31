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
