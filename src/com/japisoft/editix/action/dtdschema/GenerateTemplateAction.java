package com.japisoft.editix.action.dtdschema;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import com.japisoft.dtdparser.DTDParser;
import com.japisoft.dtdparser.node.DTDNode;
import com.japisoft.dtdparser.node.ElementDTDNode;
import com.japisoft.dtdparser.node.RootDTDNode;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.XMLContainer;

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
public class GenerateTemplateAction extends AbstractAction {

	public void actionPerformed(ActionEvent arg0) {

		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container.getCurrentDocumentLocation() == null ) {
			EditixFactory.buildAndShowWarningDialog( "Please save your DTD/Schema before generating a new XML document" );
			return;
		}

		String type = container.getDocumentInfo().getType();

		List<LabelWrapper> elements = new ArrayList<LabelWrapper>();

		if ( "DTD".equals( type ) ) {
			
			try {
								
				DTDParser parser = new DTDParser();
				parser.parse( new StringReader( container.getText() ) );
				RootDTDNode root = parser.getDTDElement();				
				for  (int i = 0; i < root.getDTDNodeCount(); i++ ) {
					if ( root.getDTDNodeAt( i ).isElement() ) {
						elements.add( new LabelWrapper( ( ElementDTDNode )root.getDTDNodeAt( i ) ) );
					}
				}

				if ( elements.size() > 0 ) {
				
					LabelWrapper rootElement = (LabelWrapper)JOptionPane.showInputDialog(
							EditixFrame.THIS, "Choose your root element",
							"XML Document root",
							JOptionPane.QUESTION_MESSAGE, 
							null, 
							elements.toArray(), 
							elements.get( 0 )
					);				
					
					if ( rootElement != null ) {
						
						try {
						
							GenerateTemplateFromElementAction.generateXMLDocumentFromDTD( 
									( ( ElementDTDNode )rootElement.source ).getName(), container.getCurrentDocumentLocation() );
							
						} catch( Throwable th ) {
							
							EditixFactory.buildAndShowErrorDialog( "Can't generate your XML document : " + th.getMessage() );
							
						}
	
					}
					
				} else {
					
					EditixFactory.buildAndShowWarningDialog( "Can't find an element definition inside your DTD ?" );
					
				}

			} catch( IOException exc ) {
				EditixFactory.buildAndShowErrorDialog( "Can't parse your DTD, please fix it" );
			}

		} else
		if ( "XSD".equals( type ) ) {

			FPNode root = container.getRootNode();
			if ( root == null ) {
				EditixFactory.buildAndShowErrorDialog( "Can't parse your Schema, please fix it" );
			} else {

				for ( int i = 0; i < root.getChildCount(); i++ ) {
					FPNode node = ( FPNode )root.getChildAt( i );
					if ( node.matchContent( "element" ) ) {
						elements.add( new LabelWrapper( node ) );
					}
				}

				if ( elements.size() > 0 ) {

					LabelWrapper rootElement = (LabelWrapper)JOptionPane.showInputDialog(
							EditixFrame.THIS, "Choose your root element",
							"XML Document root",
							JOptionPane.QUESTION_MESSAGE, 
							null, 
							elements.toArray(), 
							elements.get( 0 )
					);				
					
					if ( rootElement != null ) {
						
						try {

							GenerateTemplateFromElementAction.generateXMLDocumentFromXSD( ( FPNode )rootElement.source, container.getCurrentDocumentLocation() );

						} catch( Throwable th ) {
						
							EditixFactory.buildAndShowErrorDialog( "Can't generate your XML document : " + th.getMessage() );
							
						}

					}					

				} else {
					
					EditixFactory.buildAndShowWarningDialog( "Can't find a global element definition inside your Schema ?" );
					
				}
				
			}

		}

	}

	/* ----------------------------------------------------------------------------- */
	
	class LabelWrapper {
		Object source;	
		
		LabelWrapper( Object source ) {
			this.source = source;
		}
		
		@Override
		public String toString() {
			if ( source instanceof FPNode ) {
				return ( ( FPNode )source ).getAttribute( "name" );
			} else
			if ( source instanceof ElementDTDNode ) {
				return ( ( ElementDTDNode )source ).getName();
			} else
				return super.toString();
		}
	}

}
