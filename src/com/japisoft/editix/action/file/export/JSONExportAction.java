package com.japisoft.editix.action.file.export;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.editix.ui.SelectableEncoding;
import com.japisoft.framework.toolkit.FileToolkit;
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
public class JSONExportAction extends AbstractAction {

	public void actionPerformed( ActionEvent e ) {

		IXMLPanel container = 
			EditixFrame.THIS.getSelectedContainer();

		if ( container == null || container.getMainContainer() == null ) {
			EditixFactory.buildAndShowWarningDialog( "No selected XML document ?" );
			return;
		}

		JFileChooser fc = EditixFactory.buildFileChooser( new FileFilter() {			
			@Override
			public String getDescription() {
				return "JSON file (*.json, *.jso)";
			}
			@Override
			public boolean accept( File f ) {
				if ( f.isFile() ) {
					String tmp = f.getName().toLowerCase();
					return tmp.endsWith( ".json" ) || tmp.endsWith( ".jso" );
				}
				return true;
			}
		});

		try {
			if ( fc.showSaveDialog( EditixFrame.THIS ) == JOptionPane.OK_OPTION ) {
				String xmlContent = container.getMainContainer().getText();
				JSONObject jo = XML.toJSONObject( xmlContent );
				String content = jo.toString(1);
				
				String encoding = null;
				if ( fc instanceof SelectableEncoding ) {
					encoding = ( ( SelectableEncoding )fc ).getSelectedEncoding();
					if ( "AUTOMATIC".equalsIgnoreCase( encoding ) ) {
						encoding = null;
					}
				}

				FileToolkit.writeFile(
					fc.getSelectedFile(),
					content,
					encoding
				);
				
				EditixFactory.buildAndShowInformationDialog( "Exported" );
			}
		} catch( JSONException ex ) {
			EditixFactory.buildAndShowErrorDialog( ex.getMessage() );
		} catch( IOException ex ) {
			EditixFactory.buildAndShowErrorDialog( ex.getMessage() );
		}

	}

}
