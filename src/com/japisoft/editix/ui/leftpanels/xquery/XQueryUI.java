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

package com.japisoft.editix.ui.leftpanels.xquery;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.Configuration;
import net.sf.saxon.query.DynamicQueryContext;
import net.sf.saxon.query.StaticQueryContext;
import net.sf.saxon.query.XQueryExpression;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XQueryCompiler;
import net.sf.saxon.s9api.XQueryEvaluator;
import net.sf.saxon.s9api.XQueryExecutable;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.trans.XPathException;

import com.japisoft.editix.editor.xquery.XQueryEditor;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.windows.EditixFrame;
import com.japisoft.framework.descriptor.ActionModel;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.xml.validator.DefaultValidator;
import com.japisoft.xmlpad.xml.validator.Validator;

/**
*
* @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
*/
public class XQueryUI extends javax.swing.JPanel implements ActionListener {
   
   /** Creates new form XQueryDialog */
   public XQueryUI( boolean cb1, boolean cb2 ) {
       initComponents();
       cbOpenEditor.setSelected( cb2 );
       cbXMLOutput.setSelected( cb1 );
       
       Icon i = com.japisoft.framework.ui.Toolkit.getIconFromClasspath("images/gear_run.png");
       btnRun.setIcon( i );
       
		getActionMap().put( "run", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				btnRun.doClick();
			}
		});
		
		getInputMap( JPanel.WHEN_IN_FOCUSED_WINDOW ).put( 
				KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, KeyEvent.CTRL_DOWN_MASK), "run" );

   }
   
   public void addNotify() {
	   super.addNotify();
	   btnRun.addActionListener( this );
	   btnCopy.addActionListener( this );
   }

   public void removeNotify() {
	   super.removeNotify();
	   btnRun.removeActionListener( this );
	   btnCopy.removeActionListener( this );
   }

   public void actionPerformed( ActionEvent e ) {
	   if ( e.getSource() == btnRun ) {
		   
		   if ( EditixFrame.THIS.getSelectedContainer() == null )
			   EditixFactory.buildAndShowErrorDialog(
						"No selected document. Open a document" );

			xqueryEditor.getEditor().setCaretColor( Color.black );		

			try {
							
				// Parse the document as a DOM one
				DefaultValidator validator = new DefaultValidator( true, true );
				if ( validator.notifyAction( EditixFrame.THIS.getSelectedContainer(), false ) == Validator.ERROR ) {
					EditixFactory.buildAndShowErrorDialog( "Can't parse the current document, please fix it before evaluating" );
					return;
				}

				Processor processor = new Processor(false);
				XQueryCompiler xqueryCompiler = processor.newXQueryCompiler();
				XQueryExecutable xqueryExec = xqueryCompiler.compile( xqueryEditor.getText()  );
				XQueryEvaluator evaluator = xqueryExec.load();
				
				DocumentBuilder docBuilder = processor.newDocumentBuilder();
				XdmNode contextNode = docBuilder.build( new DOMSource( validator.getDocument() ) );

				evaluator.setContextItem( contextNode );				
				
				StringWriter writer = new StringWriter(); 				
				Serializer serializer = processor.newSerializer( writer );
				
				if ( cbXMLOutput.isSelected() ) {
				    serializer.setOutputProperty(Serializer.Property.METHOD, "xml");
				    serializer.setOutputProperty(Serializer.Property.INDENT, "yes");
				} else {
				    serializer.setOutputProperty(Serializer.Property.METHOD, "text");
				}

				evaluator.run( serializer );
				xqueryResult.setText( writer.toString() );

/*				
				
				Configuration config = new Configuration();
				StaticQueryContext staticContext = 
				        new StaticQueryContext( config );
				XQueryExpression exp = 
				        staticContext.compileQuery( xqueryEditor.getText() );
				
				DynamicQueryContext dynamicContext = 
			        new DynamicQueryContext( config );
				
				dynamicContext.setContextNode(
						staticContext.buildDocument( 
								new DOMSource( validator.getDocument() )
						) 
				);

				StringWriter buffer = new StringWriter();
				StreamResult result = new StreamResult( buffer );			

				Properties props = new Properties();
				
				if ( cbXMLOutput.isSelected() ) {
					props.setProperty( OutputKeys.METHOD, "xml");
					props.setProperty( OutputKeys.INDENT, "yes");
				} else {
					props.setProperty( OutputKeys.METHOD, "text");
				}

				exp.run( 
						dynamicContext, 
						result, 
						props );

				xqueryResult.setText( buffer.toString() );
				
*/				

				jTabbedPane1.setSelectedIndex( 1 );
				
				if ( cbOpenEditor.isSelected() ) {
					IXMLPanel panel = null;
					String type = "XML";
					panel = EditixFactory.buildNewContainer( type, (String)null );					
					XMLContainer container = panel.getMainContainer();
					container.setText( writer.toString() );
					EditixFrame.THIS.addContainer( panel );
				}

			/*
			} catch( XPathException exc ) {
				SourceLocator locator = exc.getLocator();
				EditixFactory.buildAndShowErrorDialog( "Wrong expression : " + exc.getMessageAndLocation() );
				if ( locator != null && locator.getLineNumber() != -1 ) {
					int line = locator.getLineNumber();
					try {
						int offset = xqueryEditor.getDocument().getDefaultRootElement().getElement( line - 1 ).getStartOffset();
						offset += ( locator.getColumnNumber() - 1 );
						xqueryEditor.getEditor().setCaretPosition( offset );
						xqueryEditor.getEditor().setCaretColor( Color.red );
						xqueryEditor.requestFocus();
					} catch( Exception npe ) {}
				}
			}
			
			*/
				
			} catch( SaxonApiException exc ) {

				EditixFactory.buildAndShowErrorDialog( "Wrong expression : " + exc.getMessage() );
				int line = exc.getLineNumber();
				try {
					int offset = xqueryEditor.getDocument().getDefaultRootElement().getElement( line - 1 ).getStartOffset();
					offset += 0;
					xqueryEditor.getEditor().setCaretPosition( offset );
					xqueryEditor.getEditor().setCaretColor( Color.red );
					xqueryEditor.requestFocus();
				} catch( Exception npe ) {}
				
				
			}

	   } else
	   if ( e.getSource() == btnCopy ) {
		   
		   String content = null;
		   
		   if ( jTabbedPane1.getSelectedIndex() == 0 ) {

			   content = xqueryEditor.getText();
			   
		   } else {

			   content = xqueryResult.getText();
			   
		   }

		   if ( content != null )
			Toolkit
			.getDefaultToolkit()
			.getSystemClipboard()
			.setContents(
				new StringSelection(
					content )
					, null );

	   }
   }

   /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
   // <editor-fold defaultstate="collapsed" desc=" Generated Code ">
   private void initComponents() {
       btnRun = new javax.swing.JButton();
       btnCopy = new javax.swing.JButton();
       jTabbedPane1 = new javax.swing.JTabbedPane();
       pnlXQuery = new javax.swing.JPanel();
       xqueryEditor = new XQueryEditor() ;
       pnlResult = new javax.swing.JPanel();
       spResult = new javax.swing.JScrollPane();
       xqueryResult = new javax.swing.JTextArea();
       cbXMLOutput = new javax.swing.JCheckBox();
       cbOpenEditor = new javax.swing.JCheckBox( "Open a new editor");
       
       btnRun.setText("Run");

       btnCopy.setText("Copy");


       org.jdesktop.layout.GroupLayout pnlXQueryLayout = new org.jdesktop.layout.GroupLayout(pnlXQuery);
       pnlXQuery.setLayout(pnlXQueryLayout);
       pnlXQueryLayout.setHorizontalGroup(
           pnlXQueryLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
           .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlXQueryLayout.createSequentialGroup()
               .addContainerGap()
               .add(xqueryEditor.getView(), org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 363, Short.MAX_VALUE)
               .addContainerGap())
       );
       pnlXQueryLayout.setVerticalGroup(
           pnlXQueryLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
           .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlXQueryLayout.createSequentialGroup()
               .addContainerGap()
               .add(xqueryEditor.getView(), org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE)
               .addContainerGap())
       );
       jTabbedPane1.addTab("XQuery", pnlXQuery);

       xqueryResult.setColumns(20);
       xqueryResult.setRows(5);
       spResult.setViewportView(xqueryResult);

       org.jdesktop.layout.GroupLayout pnlResultLayout = new org.jdesktop.layout.GroupLayout(pnlResult);
       pnlResult.setLayout(pnlResultLayout);
       pnlResultLayout.setHorizontalGroup(
           pnlResultLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
           .add(pnlResultLayout.createSequentialGroup()
               .addContainerGap()
               .add(spResult, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 363, Short.MAX_VALUE)
               .addContainerGap())
       );
       pnlResultLayout.setVerticalGroup(
           pnlResultLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
           .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlResultLayout.createSequentialGroup()
               .addContainerGap()
               .add(spResult, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE)
               .addContainerGap())
       );
       jTabbedPane1.addTab("Result", pnlResult);

       cbXMLOutput.setText("XML output");
       cbXMLOutput.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
       cbXMLOutput.setMargin(new java.awt.Insets(0, 0, 0, 0));

       org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
       this.setLayout(layout);
       layout.setHorizontalGroup(
           layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
           .add(layout.createSequentialGroup()
               .addContainerGap()
               .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                   .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                   .add(layout.createSequentialGroup()
                       .add(btnRun)
                       .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                       .add(btnCopy))
                   .add(cbXMLOutput).add(cbOpenEditor))
               .addContainerGap())
       );
       layout.setVerticalGroup(
           layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
           .add(layout.createSequentialGroup()
               .addContainerGap()
               .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                   .add(btnRun)
                   .add(btnCopy, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
               .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
               .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
               .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
               .add(cbXMLOutput)
               .addContainerGap()
               .add(cbOpenEditor))
       );
   }// </editor-fold>

   // Variables declaration - do not modify
   private javax.swing.JButton btnCopy;
   private javax.swing.JButton btnRun;
   javax.swing.JCheckBox cbOpenEditor;
   javax.swing.JCheckBox cbXMLOutput;
   private XQueryEditor xqueryEditor;
   private javax.swing.JTabbedPane jTabbedPane1;
   private javax.swing.JPanel pnlResult;
   private javax.swing.JPanel pnlXQuery;
   private javax.swing.JScrollPane spResult;
   private javax.swing.JTextArea xqueryResult;
   // End of variables declaration

}
