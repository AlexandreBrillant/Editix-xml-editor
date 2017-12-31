package com.japisoft.editix.action.xsl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.xml.transform.ErrorListener;

import com.japisoft.framework.application.descriptor.ActionModel;
import com.japisoft.framework.ui.toolkit.BrowserCaller;
import com.japisoft.editix.action.xquery.XQueryAction;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.xslt.XSLTEditor;
import com.japisoft.p3.Manager;
import com.japisoft.xmlpad.IXMLPanel;
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
public class XSLTXQueryLastAction extends AbstractAction {

	public void actionPerformed(ActionEvent e) {
		
		//£££
		
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		IXMLPanel panel = EditixFrame.THIS.getSelectedPanel();

		if ( container == null )
			return;

		if ( EditixFactory.mustSaveDialog( container ) ) {
			return;
		}	
		
		boolean ok = com.japisoft.xmlpad.action.ActionModel.activeActionByName(
			com.japisoft.xmlpad.action.ActionModel.SAVE_ACTION,
			container,
			container.getEditor() );
		
		if ( !ok )
			return;

		ErrorListener xsltAction = ( ErrorListener )ActionModel.restoreAction( "transformWithXSLT" );		
		ErrorListener xqueryAction = ( ErrorListener )ActionModel.restoreAction( "transformWithXQuery" );		
		
		if ( !"true".equals( container.getProperty( "xslt.ok" ) ) && 
				!"true".equals( container.getProperty( "xquery.ok" ) ) ) {
			
			XSLTDialog dialog = EditixFactory.getConfigDialog( 
					"XQR".equals( panel.getMainContainer().getDocumentInfo().getType() )		
			);

			// Show the configure dialog
			if ( "XQR".equals( panel.getMainContainer().getDocumentInfo().getType() ) ) {
				
			} else {
				
				if ( panel.getMainContainer().getDocumentInfo().getType().startsWith( "XSLT" ) ) {
					panel.setProperty( "xslt.xslt.file", container.getCurrentDocumentLocation() );
				}
								
			}
			
			dialog.init( panel );
			dialog.setVisible( true );
			dialog.dispose();
			
			if ( dialog.isOk() ) {
				dialog.store( panel );
			}
		}

		if ( "true".equals( container.getProperty( "xslt.ok" ) ) ) {
			// Repeat the last XSLT config
			XSLTAction.applyTransformation( panel, true, false, false, xsltAction );
		}

		if ( "true".equals( container.getProperty( "xquery.ok" ) ) ) {
			( (XQueryAction)xqueryAction ).transform(
					panel,
					true,
					false,
					false,
					xqueryAction );
		}

		//££
	}

}
