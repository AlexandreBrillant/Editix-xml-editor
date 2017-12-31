package com.japisoft.editix.editor.xquery;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import com.japisoft.editix.ui.xslt.Factory;
import com.japisoft.framework.ui.text.FileTextField;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.LocationEvent;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.look.LookManager;

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
public class XMLDataSourcePanel extends JPanel 
		implements TreeSelectionListener {

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
	JTabbedPane tp = new JTabbedPane( JTabbedPane.BOTTOM );
	
	private void initUI() {
		
		file = new FileTextField( null, null, new String[] { "xml" }, factory.getPathBuilder() );
		
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
						3, 3, 3, 3), 0, 0));		
	}

	public void valueChanged( TreeSelectionEvent e ) {
		FPNode n = ( FPNode )e.getPath().getLastPathComponent();
		xmlContainer.notifyLocationListener( new LocationEvent( xmlContainer, n ) );
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
	}

	public void removeNotify() {
		super.removeNotify();
		tree.removeTreeSelectionListener( this );
	}

	// From the debugger part
	public void showSourceLine(int line) {
		tp.setSelectedIndex( 1 );
		xmlContainer.getEditor().highlightLine( line );
	}
}
