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

package com.japisoft.editix.action.help;

import java.awt.event.ActionEvent;
import java.io.ByteArrayOutputStream;

import javax.swing.AbstractAction;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.japisoft.editix.action.xsl.XSLTAction;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.dialog.BasicOKDialogComponent;
import com.japisoft.framework.job.BasicJob;
import com.japisoft.framework.job.Job;
import com.japisoft.framework.job.JobManager;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public class UIHelpAction extends AbstractAction {

	DocumentDialog dialog;

	public void actionPerformed(ActionEvent e) {
			dialog = new DocumentDialog( "Please wait while generating documentation..." );
			JobManager.addJob( new DocGeneratorJob() );
			dialog.setVisible( true );
			dialog.dispose();
			dialog = null;
	}

	class DocGeneratorJob extends BasicJob {

		public Object getSource() {
			return UIHelpAction.this;
		}

		public boolean isAlone() {
			return true;
		}
		
		public void dispose() {}
		
		public void run() {
			TransformerFactory tf = 
				XSLTAction.getTransformerFactoryV1( false );
			try {
				Transformer transformer = tf.newTransformer(
					new StreamSource( ClassLoader.getSystemResourceAsStream( "doc.xsl" ) ) );
			
				ByteArrayOutputStream output = new ByteArrayOutputStream();

				transformer.transform( 
					new StreamSource( ClassLoader.getSystemResourceAsStream( "editix.xml" ) ),
					new StreamResult( output ) );
					
				dialog.setContent( output.toString() );
			} catch( Throwable th ) {
				dialog.setContent( "Error : " + th.getMessage() );
			}
		}

		public void stopIt() {
		}
	}

	class DocumentDialog extends BasicOKDialogComponent  {
		JEditorPane ep = null;
		
		public DocumentDialog( String htmlContent ) {
			super( ApplicationModel.MAIN_FRAME, 
					"Documentation", 
					"Documentation", 
					"EditiX menu content",
					null );
			setUI( new JScrollPane( ep = new JEditorPane( "text/html", htmlContent ) ) );
			ep.setEditable( false );
			ep.setCaretPosition( 0 );
			//pack();
			setSize( 600, 600 );
		}
		
		public void setContent( String html ) {
			ep.setText( html );
			ep.setCaretPosition( 0 );
		}
	}

}
