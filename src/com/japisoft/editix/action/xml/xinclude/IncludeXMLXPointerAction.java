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

package com.japisoft.editix.action.xml.xinclude;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;

import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.ui.toolkit.FileManager;
import com.japisoft.framework.xml.XMLFileData;
import com.japisoft.framework.xml.XMLToolkit;
import com.japisoft.framework.xml.parser.document.Document;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.tree.parser.InnerXMLParser;

public class IncludeXMLXPointerAction extends AbstractAction {

	protected String getFileType() { return "xml"; }
	protected String getFileDescription() { return "XML File"; }
	protected String getParse() { return null; }

	@Override
	public void actionPerformed(ActionEvent e) {
		
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container == null ) {
			EditixFactory.buildAndShowErrorDialog( "No document" );
			return;
		}
		File f = FileManager.getSelectedFile( true, getFileType(), getFileDescription(), container.getCurrentDocumentLocation() );
		if ( f != null ) {
			String path = null;
			if ( container.getCurrentDocumentLocation() != null )
				path = com.japisoft.framework.app.toolkit.Toolkit.getRelativePath( f, new File( container.getCurrentDocumentLocation() ) );
			else
				path = f.toString();

			path = path.replace( '\\', '/' );
			FPNode root = container.getRootNode();
			String prefix = "xi";
			boolean nsFound = false;
			
			if ( root != null ) {
				Iterator<String> prefixes = root.getNameSpaceDeclaration();
				while ( prefixes != null && prefixes.hasNext() ) {
					String p = prefixes.next();
					String ns = root.getNameSpaceDeclarationURI( p );
					if ( XMLToolkit.NS_XINCLUDE.matches( ns ) ) {
						prefix = p;
						nsFound = true;
					}
				}
			}
			
			StringBuffer sb = new StringBuffer();
			sb.append( "<" ).append( prefix ).append( ":include" );
			if ( !nsFound )
				sb.append( " xmlns:" ).append( prefix ).append( "=\"" ).append( XMLToolkit.NS_XINCLUDE ).append( "\"" );
			sb.append( " href=\"" ).append( path ).append( "\"" );
			
			String p = getParse();
			if ( p != null )
				sb.append( " parse=\"" ).append( p ).append( "\"" );

			try {
				SelectXPointerPanel spp = new SelectXPointerPanel( f );
				if ( 
					DialogManager.showDialog( 
							ApplicationModel.MAIN_FRAME, 
							"Select a node", 
							"XPointer selection", 
							"Select a tree node for selecting it", 
							null, 
							spp, 
							new Dimension( 200, 400 ) ) == DialogManager.OK_ID ) {

					sb.append( " xpointer=\"" + spp.getXPointer() + "\"/>" );
					container.insertText( sb.toString() );
					
				}
			} catch( Throwable th ) {
				EditixFactory.buildAndShowErrorDialog( "Can't parse : " + th.getMessage() );
				return;
			}
		}
			
	}
	
	class SelectXPointerPanel extends JPanel implements TreeSelectionListener {
		
		private JComboBox<String> cb = null;
		private JTree tree = null;
		
		public SelectXPointerPanel( File f ) throws Throwable {
			setLayout( new BorderLayout() );
			XMLFileData d = XMLToolkit.getContentFromInputStream( new FileInputStream( f ), null );
			InnerXMLParser p = new InnerXMLParser();
			Document doc = p.parseContent( d.getContent() );
			
			
			tree = new JTree( new DefaultTreeModel( (TreeNode) doc.getRoot() ) );
			cb = new JComboBox<String>();
			cb.setEditable( true );
			
			add( new JScrollPane( tree ), BorderLayout.CENTER );
			add( cb, BorderLayout.SOUTH );
		}
		
		public String getXPointer() {
			return (String)cb.getSelectedItem();
		}

		public void addNotify() {
			super.addNotify();
			tree.addTreeSelectionListener( this );
		};
		
		public void removeNotify() {
			super.removeNotify();
			tree.removeTreeSelectionListener( this );
		};
		
		@Override
		public void valueChanged( TreeSelectionEvent e ) {
			FPNode node = ( FPNode )tree.getSelectionPath().getLastPathComponent();
			cb.setSelectedItem( node.getXPointerElement() );
		}
		
	}
	
}
