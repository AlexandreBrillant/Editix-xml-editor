package com.japisoft.editix.ui.xslt.action;

import java.awt.event.ActionEvent;
import java.io.FileReader;
import java.io.FileWriter;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.japisoft.framework.dialog.BasicOKCancelDialogComponent;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.ui.text.FileTextField;
import com.japisoft.editix.ui.xslt.XSLTEditor;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.action.ActionModel;

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
public class RunAction extends AbstractAction {

	private JFrame owner;
	private XSLTEditor container;

	public RunAction(JFrame owner, XSLTEditor container) {
		this.owner = owner;
		this.container = container;
		putValue(
			Action.SMALL_ICON,
			new ImageIcon( getClass().getResource( "Play16.gif" ) ) );
		putValue(
			Action.NAME,
			"Run XSLT" );
	}

	public void actionPerformed(ActionEvent e) {
		
		container.setEnabledConsole( true );
		try {
		
			String xsltFile = container.getXSLTFile();
			String dataFile = container.getDataFile();
	
			String previousFile = ( dialog != null ? dialog.getFile() : null );
			if ( previousFile == null )
				previousFile = XSLTEditor.DEF_RESULTFile;
	
			dialog = new ResultDialog( previousFile );
			dialog.setVisible( true );
	
			if ( dialog.getLastAction() == DialogManager.OK_ID ) {
	
				String resultFile = dialog.getFile();
	
				if ( xsltFile == null || xsltFile.length() == 0 ) {
					JOptionPane.showMessageDialog( container.getView(), "Save your XSLT File before transforming", "Error", JOptionPane.ERROR_MESSAGE );
					return;
				}
	
				if ( dataFile == null || dataFile.length() == 0 ) {
					JOptionPane.showMessageDialog( container.getView(), "No Data File", "Error", JOptionPane.ERROR_MESSAGE );
					return;
				}
	
				if ( resultFile == null || resultFile.length() == 0 ) {
					JOptionPane.showMessageDialog( container.getView(), "No Result File", "Error", JOptionPane.ERROR_MESSAGE );
					return;
				}
	
				ActionModel.activeActionByName( ActionModel.SAVE_ACTION );
	
				try {
	
					error( null );
	
					javax.xml.transform.TransformerFactory tFactory =
						javax.xml.transform.TransformerFactory.newInstance();
	
					Transformer transformer = tFactory.newTransformer( new StreamSource( new FileReader( xsltFile ) ) );
	
					transformer.transform(
						new javax.xml.transform.stream.StreamSource( new FileReader( dataFile ) ),
						new StreamResult(new FileWriter( dialog.getFile() ) ) );
	
					container.setProperty( "xslt.result.file", dialog.getFile() );
					container.loadResultFile();
	
					XSLTEditor.DEF_DATAFile = dataFile;
					XSLTEditor.DEF_RESULTFile = dialog.getFile();
					XSLTEditor.DEF_XSLTFile = xsltFile;
	
				} catch (TransformerException ex) {
					if ("true".equals(System.getProperty("editix.debug")))
						ex.printStackTrace();
					String message = ex.getMessage();
					Throwable th = ex;
					while (true) {
						if (th.getCause() == null)
							break;
						th = th.getCause();
					}
					message = th.getMessage();
	
					XMLContainer cont = container.getMainContainer();
	
					if (ex.getLocator() != null) {
						cont.getErrorManager().notifyError(
								message,
								ex.getLocator().getLineNumber() );
					}
					else
						error( message );
	
				} catch (Throwable th) {
					if ("true".equals(System.getProperty("editix.debug")))
						th.printStackTrace();
	
					error( th.getMessage() );
				}
			}
			
		} finally {
			container.setEnabledConsole( false );
		}
	}

	private void error( String message ) {
		container.getMainContainer().getErrorManager().notifyError( message, 0 );
	}

	private ResultDialog dialog = null;

	class ResultDialog extends BasicOKCancelDialogComponent {
		FileTextField tft = null;

		public ResultDialog(String previous) {
			super(
				owner,
				"XSLT Result file",
				"XSLT Result file",
				"Choose a file for the transformation result", 
				null
			);
			tft = new FileTextField("Result", null, (String)null);
			if (previous != null)
				tft.setText(previous);
			setUI(tft);
			setSize( 300, 200 );
		}

		public String getFile() {
			return tft.getText();
		}
	}

}
