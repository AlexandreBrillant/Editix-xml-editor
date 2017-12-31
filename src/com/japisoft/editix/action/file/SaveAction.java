package com.japisoft.editix.action.file;

import java.awt.event.ActionEvent;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;

import com.japisoft.editix.toolkit.Toolkit;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.editix.ui.EditixStatusBar;
import com.japisoft.editix.ui.panels.universalbrowser.CommonUniversalBrowserPanel;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.application.descriptor.InterfaceBuilder;
import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.action.ActionModel;
import com.japisoft.xmlpad.action.XMLAction;

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
public class SaveAction extends AbstractAction {

	public void actionPerformed( ActionEvent e ) {
		if ( EditixFrame.THIS.isEmpty() )
			return;

		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container == null )
			return;
		save( container );
	}
	
	public boolean save( XMLContainer container ) {
		
		SaveActionDelegate sad = null;
		
		if ( container.hasProperty( "save.delegate" ) ) {
			sad = ( SaveActionDelegate )container.getProperty( "save.delegate" );
		}

		String currentEncoding = ( String )container.getProperty( "encoding" );
		if ( currentEncoding == null )
			currentEncoding = Toolkit.getCurrentFileEncoding(); 
		
		// Reset the file encoding
		ActionModel.setProperty( ActionModel.SAVE_ACTION, 
			com.japisoft.xmlpad.action.file.SaveAction.ENCODING_PROPERTY,
			currentEncoding );

		// Reset the file encoding
		ActionModel.setProperty( ActionModel.SAVEAS_ACTION, 
			com.japisoft.xmlpad.action.file.SaveAction.ENCODING_PROPERTY,
			currentEncoding );

		boolean saveOk = ( sad != null && sad.save( container ) );
		if ( !saveOk && sad == null )
			saveOk = save_action();
		
		if ( saveOk ) {
			com.japisoft.framework.application.descriptor.ActionModel.LAST_ACTION_STATE = true;
			
			EditixFrame.THIS.refreshCurrentTabName();
			EditixStatusBar.ACCESSOR.setDelayedMessage( "File " + container.getCurrentDocumentLocation() + " saved..." );
			
			ApplicationModel.fireApplicationValue( "save", container );

		} else
			com.japisoft.framework.application.descriptor.ActionModel.LAST_ACTION_STATE = false;
		return saveOk;
	}

	static boolean save_action() {
		return save_action( false );
	}

	static boolean save_action( boolean saveAs ) {
		IXMLPanel panel = EditixFrame.THIS.getSelectedPanel();
		if ( panel != null )
			panel.prepareToSave();
		
		if ( !saveAs ) {
			// Check for VFS case
			XMLContainer container = EditixFrame.THIS.getSelectedContainer();
			if ( container != null ) {
				String fileName = container.getDocumentInfo().getCurrentDocumentLocation();
				if ( fileName != null && ( fileName.indexOf( "!/" ) > -1 || fileName.startsWith( "ftp:/" ) ) ) {
					return CommonUniversalBrowserPanel.save( container );
				}
			}
		}

		boolean ok = ActionModel.activeActionByName( 
				saveAs ? ActionModel.SAVEAS_ACTION :
				ActionModel.SAVE_ACTION ) == 
			XMLAction.VALID_ACTION;
		// Save in the menu history
		if ( ok ) {
			XMLContainer container = EditixFrame.THIS.getSelectedContainer();
			if ( container != null ) {
				Action a = new OpenAction();
				String fileName = container.getDocumentInfo().getCurrentDocumentLocation();
				String type = container.getDocumentInfo().getType();
				a.putValue( Action.NAME, fileName );
				a.putValue( "param", fileName );
				if ( container.getDocumentInfo().getDocumentIconPath() != null )
					a.putValue( "iconPath", container.getDocumentInfo().getDocumentIconPath() );
				if ( type != null )
					a.putValue( "param2", type );
				String encoding = ( String )container.getProperty( "encoding" );
				if ( encoding != null )
					a.putValue( "param3", encoding );

				// Get the param / key values
				StringBuffer sb = null;
				Iterator it = container.getProperties();
				if ( it != null ) {
					while ( it.hasNext() ) {
						String property = ( String )it.next();
						Object v = container.getProperty( property );
						if ( v instanceof String ) {
							if ( sb == null )
								sb = new StringBuffer();
							if ( sb.length() > 0 )
								sb.append( ";" );
							sb.append( property ).append( "=" ).append( v );
						}
					}
					if ( sb != null )
						a.putValue( "param4", sb.toString() );
				}
				EditixFrame.THIS.getBuilder().insertMenuItemAtFirst( InterfaceBuilder.MENU_RECENT_FILE, a, 20 );
				ApplicationModel.fireApplicationValue( "document.saveAs", fileName );
			}
		}
		return ok;
	}

}
