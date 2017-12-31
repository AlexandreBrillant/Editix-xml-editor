package com.japisoft.editix.document.xslt;

import java.util.List;

import javax.swing.text.BadLocationException;

import com.japisoft.framework.xml.grammar.GrammarAttribute;
import com.japisoft.framework.xml.grammar.GrammarElement;
import com.japisoft.framework.xml.grammar.xsd.XSDGrammar;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.editor.XMLPadDocument;
import com.japisoft.xmlpad.helper.handler.AbstractHelperHandler;
import com.japisoft.xmlpad.helper.model.AttDescriptor;
import com.japisoft.xmlpad.helper.model.TagDescriptor;

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
public class SchemaHandler extends AbstractHelperHandler {

	public SchemaHandler() {
		super();
		// System.out.println( "aa" );
	}

	@Override
	protected String getActivatorSequence() {
		return null;
	}

	@Override
	public String getTitle() {
		return "DTD/Schema helper";
	}

	private XSDGrammar g = null;
	private String gHref = null;
	
	private void loadGrammar( String href ) {
		if ( g != null ) {
			// The same grammar, don't reload
			if ( href.equalsIgnoreCase( gHref ) )
				return;
		}

		try {
			
			g = new XSDGrammar( href );

		} catch( Exception exc ) {
		}

		gHref = href;
	}

	@Override
	public boolean haveDescriptors(
			FPNode currentNode,
			XMLPadDocument document, 
			boolean insertBefore, 
			int offset,
			String activatorString ) {
		
		if ( !( "<".equals( activatorString ) || activatorString == null ) ) {
			return false;
		}

		String mark = "<?schema ";
		try {
			String header = ( document.getText( 0, Math.max( 1, document.nextTag( 0 ) ) ) );
			int i = header.indexOf( mark );
			
			boolean found = false;
			
			if ( i > -1 ) {
				int j = header.indexOf( "?>", i + 1 );
				if ( j > -1 ) {
					String piParameter = header.substring( i + mark.length(), j );
					piParameter = piParameter.trim();
					i = piParameter.indexOf( "'" );
					if ( i == -1 ) {
						i = piParameter.indexOf( "\"" );
					}
					if ( i > -1 ) {
						j = piParameter.indexOf( "'", i + 1 );
						if ( j == -1 ) {
							j = piParameter.indexOf( "\"", i + 1 );
						}
						if ( j > -1 ) {
							String schemaLocation = piParameter.substring( i + 1, j );
							loadGrammar( schemaLocation );
							found = true;
						}
					}
				}
			}
			
			if ( !found ) {
				// remove grammar reference
				g = null;
				gHref = null;
			}

		} catch( BadLocationException exc ) {
			
		}

		return ( g != null );
	}

	@Override
	protected void installDescriptors(FPNode currentNode,
			XMLPadDocument document, int offset, String activatorString) {	
		if ( g != null ) {
			
			if ( document.isInsideTag( offset ) ) {
			
				// Only attributes
				String name = currentNode.getContent();
				if ( name != null ) {
					
					GrammarElement e = g.getGlobalElement( name );
					if ( e != null ) {
						List<GrammarAttribute> l = e.getAttributes( false );
						if ( l != null ) {
							for ( GrammarAttribute ga : l ) {
								String dv = ga.getDefaultValue() == null ? "" : ga.getDefaultValue().getValue();
								AttDescriptor ad = new AttDescriptor( ga.getName(), dv );
								addDescriptor( ad );
							}
						}
					}

				}
				
			} else {
			
				List<GrammarElement> l = g.getGlobalElements();
				if ( l != null ) {
					for ( GrammarElement ge : l ) {					
						TagDescriptor td = new TagDescriptor( ge.getName(), false );
						
						String inferRes = ge.infer();
						td.setRawContent( inferRes );
	
						addDescriptor( td );
					}
				}
				
			}
		}
	}

	@Override
	public void dispose() {
		g = null;
		gHref = null;
		super.dispose();
	}

}
