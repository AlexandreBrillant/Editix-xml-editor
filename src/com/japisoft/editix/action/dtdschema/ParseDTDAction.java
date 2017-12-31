package com.japisoft.editix.action.dtdschema;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.AbstractAction;

import org.apache.xerces.impl.dtd.XMLDTDLoader;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParseException;

import com.japisoft.editix.action.file.SaveAction;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.application.descriptor.ActionModel;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.action.xml.CheckableAction;

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
public class ParseDTDAction extends AbstractAction implements XMLErrorHandler, CheckableAction {

	private XMLContainer container;
	
	public boolean checkDocument(XMLContainer container, boolean silentMode) {
		
		this.container = container;

		XMLDTDLoader
		loader = new XMLDTDLoader();
		loader.setErrorHandler( this );
		container.getErrorManager().initErrorProcessing();
		try {
			loader.loadGrammar(
					new XMLInputSource(
							null,
							container.getCurrentDocumentLocation(),
							null
					)
			);
			if ( !silentMode )
				EditixFactory.buildAndShowInformationDialog( "DTD Checked : No syntax error" );
			container.getErrorManager().notifyNoError( false );
		} catch (XNIException e1) {
		} catch (IOException e1) {
		}
		container.getErrorManager().stopErrorProcessing();
		boolean error = container.getErrorManager().hasLastError();
		this.container = null;
		
		return !error;
	}

	public void actionPerformed( ActionEvent e ) {
		container = EditixFrame.THIS.getSelectedContainer();
		if ( container == null )
			return;
		if ( container.getCurrentDocumentLocation() == null ) {
			EditixFactory.buildAndShowInformationDialog( "Please save your document before checking" );
			return;
		}
		SaveAction sa = ( SaveAction )ActionModel.restoreAction( "save" );
		sa.save( container );
		checkDocument( container, false );
	}

	public void error(String arg0, String arg1, XMLParseException arg2) throws XNIException {
		container.getErrorManager().notifyError(
				container,
				true,
				container.getCurrentDocumentLocation(),
				arg2.getLineNumber(),
				arg2.getColumnNumber(),
				-1,
				arg2.getMessage(),
				false );		
	}

	public void fatalError(String arg0, String arg1, XMLParseException arg2) throws XNIException {
		container.getErrorManager().notifyError(
				container,
				true,
				container.getCurrentDocumentLocation(),
				arg2.getLineNumber(),
				arg2.getColumnNumber(),
				-1,
				arg2.getMessage(),
				false );		
	}

	public void warning(String arg0, String arg1, XMLParseException arg2) throws XNIException {
	}


}
