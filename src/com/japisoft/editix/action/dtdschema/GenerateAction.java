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
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.p3.Manager;
import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.XMLDocumentInfo;
import com.japisoft.xmlpad.editor.XMLTemplate;

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
public abstract class GenerateAction extends AbstractAction {

	private Transformer transformer;

	protected boolean formatResult = false;
	
	public GenerateAction( Transformer transformer ) {
		this.transformer = transformer;
	}

	public void actionPerformed( ActionEvent e ) {

		//£££
		// Get the current root

		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		FPNode root = ( FPNode )container.getTree().getModel().getRoot();
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
		//££
	}

}
