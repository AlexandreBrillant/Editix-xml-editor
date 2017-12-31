package com.japisoft.editix.action.dtdschema.generator.transformer;

import java.util.ArrayList;
import java.util.Vector;

import com.japisoft.editix.action.dtdschema.generator.MetaAttribute;
import com.japisoft.editix.action.dtdschema.generator.MetaNode;
import com.japisoft.editix.action.dtdschema.generator.MetaObject;
import com.japisoft.editix.action.dtdschema.generator.Transformer;

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
