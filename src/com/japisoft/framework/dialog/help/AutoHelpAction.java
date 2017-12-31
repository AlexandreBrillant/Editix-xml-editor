package com.japisoft.framework.dialog.help;

import java.awt.event.ActionEvent;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;

import javax.swing.AbstractAction;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.dialog.BasicOKDialogComponent;
import com.japisoft.framework.job.Job;
import com.japisoft.framework.job.JobManager;

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
public class AutoHelpAction extends AbstractAction {

	DocumentDialog dialog;

	public void actionPerformed(ActionEvent e) {
		dialog = new DocumentDialog( "Please wait while generating documentation..." );
		JobManager.addJob( new DocGeneratorJob() );
		dialog.setVisible( true );
		dialog.dispose();
		dialog = null;
	}

	class DocGeneratorJob implements Job {
		public Object getSource() {
			return AutoHelpAction.this;
		}

		public boolean isAlone() {
			return true;
		}

		public void dispose() {}
		
		public void run() {

			if ( ApplicationModel.USERINTERFACE_FILE == null ) {
				throw new RuntimeException( "No value for the property INTERFACE_BUILDER of the ApplicationModel" );
			}
			
			if ( ApplicationModel.AUTODOC_FILE == null ) {
				throw new RuntimeException( "No stylesheet, set the AUTODOC_FILE property" );
			}

			TransformerFactory tf = 
				TransformerFactory.newInstance();
			try {
				Transformer transformer = tf.newTransformer(
					new StreamSource( 
							ClassLoader.getSystemResourceAsStream( 
									ApplicationModel.AUTODOC_FILE ) ) );

				transformer.setParameter( "appname", ApplicationModel.LONG_APPNAME );
				ByteArrayOutputStream output = new ByteArrayOutputStream();

				transformer.transform( 
					new StreamSource( ClassLoader.getSystemResourceAsStream( 
							ApplicationModel.USERINTERFACE_FILE ) ),
					new StreamResult( output ) );

				dialog.setContent( output.toString() );
				
				if ( ApplicationModel.DEBUG_AUTODOC_FILE != null && ApplicationModel.DEBUG_MODE ) {
					ApplicationModel.debug( "Debugging the auto doc file : " + ApplicationModel.DEBUG_AUTODOC_FILE );
					try {
						FileWriter fw = new FileWriter( ApplicationModel.DEBUG_AUTODOC_FILE );
						fw.write( output.toString() );
						fw.close();
					} catch (RuntimeException e) {
						e.printStackTrace();
					}
				}
				
			} catch( Throwable th ) {
				dialog.setContent( "Error : " + th.getMessage() );
			}
		}

		public void stopIt() {
		}
		
		public boolean hasErrors() {

			return false;
		}
	}

	class DocumentDialog extends BasicOKDialogComponent {
		JEditorPane ep = null;
		
		public DocumentDialog( String htmlContent ) {
			super( 
					ApplicationModel.MAIN_FRAME, 
					"Documentation", 
					"Menu Documentation", 
					ApplicationModel.LONG_APPNAME + " menu content", null );

			setUI( new JScrollPane( ep = new JEditorPane( "text/html", htmlContent ) ) );
			ep.setEditable( false );
			ep.setCaretPosition( 0 );
			pack();
			setSize( 600, 600 );
		}

		public void setContent( String html ) {
			ep.setText( html );
			ep.setCaretPosition( 0 );
		}
	}

}
