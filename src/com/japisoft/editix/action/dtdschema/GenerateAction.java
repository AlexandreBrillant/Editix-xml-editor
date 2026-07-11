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

package com.japisoft.editix.action.dtdschema;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import com.japisoft.editix.action.dtdschema.generator.MetaNode;
import com.japisoft.editix.action.dtdschema.generator.SchemaGenerator;
import com.japisoft.editix.action.dtdschema.generator.Transformer;
import com.japisoft.editix.document.DocumentModel;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.framework.application.descriptor.ActionModel;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.ui.toolkit.BrowserCaller;
import com.japisoft.framework.xml.parser.ParseException;
import com.japisoft.framework.xml.parser.document.Document;
import com.japisoft.framework.xml.parser.node.FPNode;

import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.XMLDocumentInfo;
import com.japisoft.xmlpad.editor.XMLTemplate;
import com.japisoft.xmlpad.tree.parser.InnerXMLParser;

/**
 * Generic action for producing a schema
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 */
public abstract class GenerateAction extends AbstractAction {

	private Transformer transformer;

	protected boolean formatResult = false;
	
	public GenerateAction( Transformer transformer ) {
		this.transformer = transformer;
	}

	public void actionPerformed( ActionEvent e ) {

		try {
		
			// Get the current root
	
			XMLContainer container = EditixFrame.THIS.getSelectedContainer();
			
			// Must switch to the inner parser for having text nodes
			
			InnerXMLParser parser = new InnerXMLParser();
			Document docTmp = parser.parseContent( container.getText() );
			
	//		FPNode root = ( FPNode )container.getTree().getModel().getRoot();
			
			FPNode root = (FPNode)docTmp.getRoot();
			
			
			if ( root == null ) {
				JOptionPane.showMessageDialog( container.getView(), "Can't generate a schema for this document " );
				return;
			}
	
			MetaNode metaRoot = SchemaGenerator.getMetaModel( root );
			MetaModelUpdatePanel mmup = null;
	
			if ( DialogManager.showDialog(
					EditixFrame.THIS,
					"Meta model",
					"Your document content",
					"Check if EditiX has found the best attribute type by selecting each node and change it if needed, then press OK",
					null,
					mmup = new MetaModelUpdatePanel( metaRoot ) ) == 
						DialogManager.OK_ID ) {
			
				transformer.setSequenceMode( mmup.hasDefaultSequence() );
				
				String content = SchemaGenerator.generate( metaRoot, transformer );
		
				XMLDocumentInfo doc = DocumentModel.getDocumentForType( transformer.getType() );
				IXMLPanel panel = EditixFactory.getPanelForDocument( doc );
				XMLContainer newContainer = panel.getMainContainer();
		
				newContainer.setAutoNewDocument( false );
				newContainer.setDocumentInfo( doc );
		
				XMLTemplate template = new XMLTemplate();
				template.setRawContent(
						( transformer.hasVersion() ? "<?xml version=\"1.0\" " + "encoding=\"${default-encoding}\"?>\n" : "" ) + 
							content );
		
				newContainer.setText( template.toString( doc ) );
				
				EditixFrame.THIS.addContainer( panel );
				
				if ( formatResult )
					ActionModel.activeActionById( "format", null );
			}
			
		} catch( ParseException p ) {
			
			EditixFactory.buildAndShowErrorDialog( "Wrong XML document, fix it before generating" );
			
		}

	}

}
