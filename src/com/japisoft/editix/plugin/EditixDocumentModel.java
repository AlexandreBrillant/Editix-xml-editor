package com.japisoft.editix.plugin;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.xmlpad.IXMLPanel;

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
public class EditixDocumentModel {

	EditixDocumentModel() {
	}
	
	/** 
	 * @return The number of opened documents
	 */
	public int getDocumentCount() {
		return EditixFrame.THIS.getXMLContainerCount();
	}

	/**
	 * @param index A number between 0 and the number of opened documents - 1
	 * @return An access to the document at this index */
	public EditixDocument getDocument( int index ) {
		if ( index >= getDocumentCount() || index < 0 ) {
			throw new RuntimeException( "Invalid index, must be between [0-" + ( getDocumentCount() - 1 ) );
		}
		return new EditixDocument( index );
	}

	/**
	 * @return The current document or <code>null</code> if there's no current document
	 */
	public EditixDocument getCurrentDocument() {
		if ( EditixFrame.THIS.getCurrentXMLContainerIndex() == -1 )
			return null;
		return new EditixDocument( EditixFrame.THIS.getCurrentXMLContainerIndex() );
	}

	/**
	 * Create and add a new document
	 * @param type the document type XML, DTD, XSD, CSS...
	 * @return a new document
	 */
	public EditixDocument newDocument( String type ) {
		IXMLPanel c = EditixFactory.buildNewContainer( type, ( String )null );
		EditixFrame.THIS.addContainer( c );
		return new EditixDocument( getDocumentCount() - 1 );
	}
	
}
