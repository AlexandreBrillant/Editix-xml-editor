// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2026 Alexandre Brillant
//
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
// AI Training Restriction :
// This source code is provided for human use only.
// Using this code to train, fine-tune, or develop AI models,
// machine learning systems, or similar technologies is
// STRICTLY PROHIBITED. Violations will terminate all rights
// under the applicable license.

package com.japisoft.editix.action.file;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;

import com.japisoft.editix.action.file.PinManager.FileInfo;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.framework.descriptor.InterfaceBuilder;
import com.japisoft.framework.descriptor.helpers.MenuBuilderDelegate;
import com.japisoft.xflows.task.ui.MenuBuilder;

public class PinFilesMenuBuilder implements MenuBuilderDelegate {

	@Override	
	public void build( JMenu menu ) {
		menu.removeAll();
		try {
			for ( int i = 0; i < PinManager.Instance().getItemCount(); i++ ) {
				menu.add( new PinFileAction( PinManager.Instance().getItem( i ) ) );
			}
			menu.setEnabled( PinManager.Instance().getItemCount() > 0 );			
		} catch ( Throwable e ) {
		}		
	}

	class PinFileAction extends AbstractAction {
		private FileInfo fi = null;
		public PinFileAction( FileInfo fi ) {
			putValue( Action.NAME, fi.location );
			this.fi = fi;
		}
		public void actionPerformed( ActionEvent e ) {
			String location = ( String )getValue( Action.NAME );
			File f = new File( location );
			if ( !f.exists() ) {
				EditixFactory.buildAndShowErrorDialog( "Can't load " + location );
				PinManager.Instance().removeFile( location );
			} else {	
				Map<String,String> properties = fi.properties;
				String flatProperties = "";
				Set<String> keys = properties.keySet();
				for ( String k : keys ) {
					if ( !"".equals( flatProperties ) )
						flatProperties += ";";
					flatProperties += k + "=" + properties.get( k );
				}
				OpenAction.openFile( fi.type, false, location, null, flatProperties );
			}
		}
	}
	
}
