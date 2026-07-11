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

package com.japisoft.editix.ui.xslt;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import com.japisoft.editix.ui.pathbuilder.XMLPathBuilder2;
import com.japisoft.framework.toolkit.FileToolkit;
import com.japisoft.framework.ui.text.FileTextField;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.LocationEvent;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.look.LookManager;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public class XMLDataSourcePanel extends JPanel 
		implements TreeSelectionListener, ActionListener {

	JLabel lblPath = new JLabel();
	FileTextField file = null;
	XMLContainer xmlContainer = null;
	GridBagLayout gridBagLayout1 = new GridBagLayout();
	Factory factory = null;

	public XMLDataSourcePanel( Factory factory ) {
		this.factory = factory;
		initUI();
	}

	JTree tree = new JTree();
	JTabbedPane tp = new JTabbedPane( 
			JTabbedPane.BOTTOM 
	);
	
	public String getCurrentFilePath() {
		return file.getText();
	}
	
	public FPNode getRootNode() {
		return xmlContainer.getRootNode();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String ext = FileToolkit.fileExt( file.getText() );
		if ( ext.startsWith( "jso" ) ) {
			tp.setSelectedIndex( 1 );
		}
	}
	
	private void initUI() {
		
		file = new FileTextField(
			null,
			null,
			new String[] { "xml", "jso", "json" }, new XMLPathBuilder2()
		);
		
		lblPath.setText( "Path :" );
		this.setLayout( gridBagLayout1 );
		file.setText( "" );		
		xmlContainer = factory.buildNewContainer( null ).getMainContainer();
		xmlContainer.setAutoResetAction( false );
		xmlContainer.setPopupAvailable( false );
		xmlContainer.setTreeAvailable( false );
		xmlContainer.setEditableDocumentMode( false );
		xmlContainer.getUIAccessibility().setEnableDragNDropForRoot( true );
		xmlContainer.getDocumentInfo().setTreeAvailable( false );
		xmlContainer.setAutoNewDocument( false );
		
		LookManager.getCurrentLook().install( xmlContainer, tree );
		
		xmlContainer.setTreeDelegate( tree );
		
		tp.addTab( "Tree", new JScrollPane( tree ) );
		tp.addTab( "Text", xmlContainer.getView() );
		tp.setSelectedIndex( 0 );

		this.add(lblPath, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4,
						6, 0, 9), 0, 0));
		this.add(file, new GridBagConstraints(1, 0, 5, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(4, 0, 0, 5), 0, 0));
		this.add(tp, new GridBagConstraints(0, 2, 6, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						3, 3, 3, 3), 0, 0)
		);		
	}

	public void valueChanged( TreeSelectionEvent e ) {
		FPNode n = ( FPNode )e.getPath().getLastPathComponent();
		xmlContainer.notifyLocationListener( 
			new LocationEvent( xmlContainer, n ) 
		);
	}

	public void dispose() {
		xmlContainer.dispose();
	}

	public void setAutoDisposeMode( boolean disposeMode ) {
		xmlContainer.setAutoDisposeMode( disposeMode );
	}

	public void addNotify() {
		super.addNotify();
		tree.addTreeSelectionListener( this );
		file.addActionListener( this );
	}
	
	public void removeNotify() {
		super.removeNotify();
		tree.removeTreeSelectionListener( this );
		file.removeActionListener( null );
	}

	// From the debugger part
	public void showSourceLine(int line) {
		tp.setSelectedIndex( 1 );
		xmlContainer.getEditor().highlightLine( line );
	}
}
