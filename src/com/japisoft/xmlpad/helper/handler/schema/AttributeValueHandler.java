package com.japisoft.xmlpad.helper.handler.schema;

import javax.swing.text.BadLocationException;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.editor.XMLPadDocument;
import com.japisoft.xmlpad.helper.SchemaHelperManager;
import com.japisoft.xmlpad.helper.handler.AbstractHelperHandler;
import com.japisoft.xmlpad.helper.model.AttDescriptor;
import com.japisoft.xmlpad.helper.model.AttValueDescriptor;
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
public class AttributeValueHandler extends AbstractHelperHandler {
	
	private AbstractTagHandler tagHandler;
	
	public AttributeValueHandler( AbstractTagHandler handler ) {
		this.tagHandler = handler;
	}
	
	public void dispose() {
		super.dispose();
		this.tagHandler = null;
	}	

	public String getTitle() {
		return tagHandler.getTitle();
	}

	public int getPriority() {
		return 1;
	}	
	
	protected String getActivatorSequence() {
		return "\"";
	}	

	public String getName() {
		return SchemaHelperManager.SCHEMA_ATTRIBUTE_VALUES;
	}	

	public boolean haveDescriptors(
			FPNode currentNode, 
			XMLPadDocument document, 
			boolean insertBefore, 
			int offset, 
			String activatorString ) {
		
		if ( ( "\"".equals( activatorString ) || 
				( "'".equals( activatorString ) )
				) && 
					document.isInsideTag( 
							offset,
							true,
							true )
		) {
			return true;
		} else
			if ( activatorString == null ) {
				if ( document.isInsideAttributeValue( 
						offset ) )
					return true;
			}
		return false;
	}

	protected void installDescriptors(
			FPNode currentNode,
			XMLPadDocument document, 
			int offset, 
			String activatorString ) {
/*		SimpleNode currentNode = 
			document.getContainer().getCurrentElementNode(); */
		if ( currentNode == null )
			return;	// Wrong state?
		TagDescriptor td = 
			tagHandler.getTag( currentNode );
		if ( td == null )
			return;
		AttDescriptor[] atts = td.getAtts();
		if ( atts != null ) {
			String currentAttribute = document.getForwardAttributeName( 
					document, offset );
			if ( currentAttribute == null )
				return;
			for ( int i = 0; i < atts.length; i++ ) {
				AttDescriptor ad = atts[ i ];
				if ( ad.getName().equals( currentAttribute ) ) {
					String[] enums = ad.getEnumValues();
					if ( enums != null ) {
						for ( int j = 0; j < enums.length; j++ ) {
							AttValueDescriptor 
								avd = new AttValueDescriptor( enums[ j ], 
										activatorString != null ? 
												activatorString.charAt( 0 ) : '"' );
							avd.setAddedPart( activatorString );
							avd.setSource( this );
							addDescriptor( avd );
						}
					}
					break;
				}
			}
		}
	}

	public boolean hasDelegateForInsertingResult() {
		return true;
	}	
	
	public void insertResult(
			XMLPadDocument document, 
			int offset, 
			String result ) {
		int[] attDel = document.getAttributeValueLocation( offset );
		if ( attDel == null )
			document.insertStringWithoutHelper( offset, result, null );
		else {
			try {
				document.replace(
						attDel[ 0 ],
						attDel[ 1 ] - attDel[ 0 ] + 1,
						result,
						null );
			} catch (BadLocationException e) {
			}
		}
	}

}
