package com.japisoft.editix.action.xsl;

import java.awt.event.ActionEvent;
import java.io.File;
import java.net.MalformedURLException;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;

import com.japisoft.editix.toolkit.Toolkit;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.editor.XMLPadDocument;

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
public class AssignXSLTAction extends AbstractAction {

	public void actionPerformed(ActionEvent e) {
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container == null )
			return;

		JFileChooser chooser = new JFileChooser();

		chooser.setFileFilter(new FileFilter() {
			public boolean accept(File f) {
				return f.isDirectory() || f.toString().toLowerCase().endsWith( ".xsl" );
			}
			public String getDescription() {
				return "XSLT (*.xsl)";
			}
		});

		if ( chooser.showOpenDialog( EditixFrame.THIS )
			== JFileChooser.APPROVE_OPTION ) {
			XMLPadDocument doc = container.getXMLDocument();
			int next = Math.max( 0, doc.nextTag( 0 ) - 1 );
			try {
				
				String href = null;
				if ( container.getCurrentDocumentLocation() != null ) {
					href = com.japisoft.framework.app.toolkit.Toolkit.getRelativePath(
							chooser.getSelectedFile(),
							new File( container.getCurrentDocumentLocation() ) );
				} else 
					href = Toolkit.toURL( chooser.getSelectedFile() ); 
				
				doc.insertString( next, "<?xml-stylesheet type=\"text/xsl\" href=\"" + href + "\"?>", null );
			} catch( BadLocationException exc ) {
			} catch( MalformedURLException exc ) {
			}
		}
	}

}
