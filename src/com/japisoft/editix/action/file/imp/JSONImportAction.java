package com.japisoft.editix.action.file.imp;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import com.japisoft.editix.action.xml.format.FormatAction;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.xml.format.FormatterConfig;
import com.japisoft.xflows.task.SwingActionTaskContext;
import com.japisoft.xflows.task.imp.json.JSonFileRunner;
import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;

import java.awt.event.ActionEvent;
import java.io.File;

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
public class JSONImportAction extends AbstractAction {

	public void actionPerformed( ActionEvent e ) {
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
				} else
					return true;
			}
		});
		if ( fc.showOpenDialog( EditixFrame.THIS ) == 
				JFileChooser.APPROVE_OPTION ) {
			File sourceFile = fc.getSelectedFile();
			JSonFileRunner runner = new JSonFileRunner();
			SwingActionTaskContext satc = new SwingActionTaskContext( sourceFile );
			runner.run( satc );
			if ( !satc.hasErrorFound() ) {
				IXMLPanel panel = EditixFactory.buildNewContainer( "XML", ( String )null );
				XMLContainer container = panel.getMainContainer();
				container.setText( satc.getTaskResult() );
				FormatAction.format(
					container,
					null,
					new FormatterConfig(),
					null
				);
				EditixFrame.THIS.addContainer( panel );
			}
		}
	}

}
