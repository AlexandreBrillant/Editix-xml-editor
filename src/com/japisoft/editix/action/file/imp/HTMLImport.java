package com.japisoft.editix.action.file.imp;

import java.awt.event.ActionEvent;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import org.w3c.tidy.Tidy;
import com.japisoft.editix.toolkit.Toolkit;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.ui.toolkit.BrowserCaller;
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
public class HTMLImport extends AbstractAction {

	public void actionPerformed(ActionEvent e) {		
		//£££
		JFileChooser chooser = EditixFactory
				.buildFileChooserForDocumentType("XHTML");
		if (chooser.showOpenDialog(EditixFrame.THIS) == JFileChooser.APPROVE_OPTION) {
			try {
				byte[] data = convertHTMLInputStream( 
						new FileInputStream( chooser.getSelectedFile() ) );
				if ( data != null ) {
					IXMLPanel panel = EditixFactory.buildNewContainer("XHTML", (String)null);
					XMLContainer container = panel.getMainContainer();
					container.setText(Toolkit.getEncodedString(data).getContent());
					EditixFrame.THIS.addContainer(panel);
				}
			} catch (Throwable exc) {
				ApplicationModel.debug( exc );
				EditixFactory.buildAndShowErrorDialog( "Can't import : " + exc.getMessage() );				
			}
		}
		//££
	}

	public static byte[] convertHTMLInputStream( InputStream input ) {
		Tidy tidy = new Tidy();
		tidy.setXmlOut(true);
		tidy.setNumEntities(true);
		tidy.setQuoteNbsp(true);
		tidy.setQuiet(true);
		tidy.setErrout( new PrintWriter( new StringWriter() ) );		
		try {
			InputStream in = new BufferedInputStream( input );
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			try {
				tidy.parse( in, output );
			} finally {
				in.close();
				output.close();
			}
			byte[] data = output.toByteArray();
			return data;
		} catch (Exception exc) {
			ApplicationModel.debug( exc );
			EditixFactory.buildAndShowErrorDialog( "Can't import : " + exc.getMessage() );
			return null;
		}
	}

}
