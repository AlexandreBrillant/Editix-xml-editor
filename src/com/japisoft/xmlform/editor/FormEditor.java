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

package com.japisoft.xmlform.editor;

import java.io.File;
import java.io.StringReader;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.InputSource;

import com.japisoft.xmlform.Toolkit;
import com.japisoft.xmlform.designer.Factory;
import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.XMLDocumentInfo;
import com.japisoft.xmlpad.action.xml.FormatAction;
import com.japisoft.xmlpad.bookmark.BookmarkContext;
import com.japisoft.xmlpad.tree.parser.Parser;

public class FormEditor extends JTabbedPane 
		implements IXMLPanel, ChangeListener {

	private XMLContainer container = null;	
	private Factory factory = null;
	private EditorComponent ec = null;

	public FormEditor( Factory factory ) {
		super( JTabbedPane.BOTTOM );
		this.factory = factory;
		container = 
			factory.buildNewContainer();
		addTab( "Source Editor", 
				new ImageIcon( getClass().getResource( 
					"document_edit.png" ) ),
				container.getView() );
		addTab( "Visual Editor",
				new ImageIcon( getClass().getResource( 
					"flash.png" ) ),
				( ec = new EditorComponent() ) );
	}
	
	public Object print() {
		return this;
	}
	
	boolean addNotifyOk = false;
	
	public void addNotify() {
		addNotifyOk = true;
		super.addNotify();
		addChangeListener( this );
		// Force by defaut the visual mode

		String content = container.getText();
		if  ( content.indexOf( "/>" ) > -1 ) {
			setSelectedIndex( 1 );
		}
	}

	public String getCurrentDocumentLocation() {
		return container.getCurrentDocumentLocation();
	}
	
	public void removeNotify() {
		super.removeNotify();
		removeChangeListener( this );
		addNotifyOk = false;
	}	
	
	public void dispose() {
		container.dispose();
	}
	
	public void copy() {
	}
	public void cut() {
	}
	public void paste() {
	}

	public Action getAction(String actionId) {
		return null;
	}

	public XMLContainer getMainContainer() {
		return container;
	}

	public XMLContainer getSubContainerAt(int index) {
		return null;
	}

	public void selectSubContainer(IXMLPanel panel) {
	}	
	
	public int getSubContainerCount() {
		return 0;
	}
	
	public XMLContainer getSelectedContainer() {
		return null;
	}
	
	public BookmarkContext getBookmarkContext() {
		return null;
	}	
	
	public Iterator getProperties() {
		return container.getProperties();
	}

	public Object getProperty(String name, Object def) {
		return container.getProperty( name, def );
	}

	public Object getProperty(String name) {
		return container.getProperty( name );
	}
	public IXMLPanel getPanelParent() {
		return null;
	}
	public Parser createNewParser( boolean lightweightMode ) {
		return null;
	}
	public XMLContainer getSubContainer(String type) {
		return container.getSubContainer( type );
	}

	public JComponent getView() {
		return this;
	}

	public void prepareToSave() {
		setSelectedIndex( 0 );	
	}

	public void postLoad() {
		if ( addNotifyOk )
			removeChangeListener( this );
		setSelectedIndex( 1 );
		SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						resetVisualData();						
					}
				} );
		if ( addNotifyOk )
			addChangeListener( this );
	}
	
	private void resetVisualData() {
		String content = container.getText();
		try {
			DocumentBuilder builder = 
				DocumentBuilderFactory.newInstance().newDocumentBuilder();

			boolean newDocument = false;
			
			if ( content.indexOf( "/>" ) == -1 && content.indexOf( "</" ) == -1 ) {
				content = content + "<tmp/>";
				newDocument = true;
			}
			
			Document doc = builder.parse( 
					new InputSource( 
						new StringReader( 
								content ) ) 
			);
			if ( !newDocument ) {
				ec.loadDocument(
					container.getCurrentDocumentLocation(), 
					doc
				);
			} else {
				NodeList nl = doc.getChildNodes();
				boolean found = false;
				for ( int i = 0; i < nl.getLength(); i++ ) {
					Node n = nl.item( i );
					if ( n instanceof ProcessingInstruction ) {
						ProcessingInstruction pi = 
							( ProcessingInstruction )n;
						if ( "xmlform".equalsIgnoreCase( pi.getTarget() ) ) {
							found = true;
							String formPath = pi.getData();
							formPath = Toolkit.trimQuote( formPath );
							
							File f = new File( formPath );
							if ( f.exists() ) {
								ec.newDocument( formPath );
							} else {
								// Try relative one
								if ( container.getCurrentDocumentLocation() != null ) {
									f = new File( 
										new File( container.getCurrentDocumentLocation() ).getParentFile(),
										formPath );
									if ( f.exists() )
										ec.newDocument( f.toString() );
									else
										ec.newDocument( formPath );	// May be URL
								} else {
									ec.newDocument( formPath ); // May be URL
								}
							}
							break;
						}
					}
				}
				if ( !found ) {
					setSelectedIndex( 
						0 
					);
					factory.buildAndShowErrorDialog( "Can't find the xmlform processing instruction ?" );
				}
			}
		} catch( Exception e ) {
			//e.printStackTrace()
			factory.buildAndShowErrorDialog( 
				"Error found in the XML source => " + e.getMessage() + "]" 
			);
			setSelectedIndex( 
				0 
			);
		}
	}

	public boolean reload() {
		return false;
	}

	public void setAutoDisposeMode(boolean disposeMode) {
		container.setAutoDisposeMode( disposeMode );
	}

	public void setDocumentInfo(XMLDocumentInfo info) {
		container.setDocumentInfo( info );		
	}

	public void setProperty(String name, Object content) {
		container.setProperty( name, content );
	}

	public void stateChanged(ChangeEvent e) {
		if ( getSelectedIndex() == 1 ) {

			resetVisualData();

		} else {
			
			try {
				
				Document result = 
					ec.getDocument();
				
				// Format the result
				FormatAction fa = 
					( FormatAction )com.japisoft.xmlpad.action.ActionModel.getActionByName( 
							com.japisoft.xmlpad.action.ActionModel.FORMAT_ACTION 
					);
				XMLContainer oldContainer = 
					fa.getXMLContainer();
				fa.setXMLContainer( container );
				fa.formatAction( result );
				fa.setXMLContainer( oldContainer );

			} catch( Exception exc ) {
				factory.buildAndShowErrorDialog( 
					"Can't generate the source [" + exc.getMessage() + "]" 
				);									
			}

		}
	}
	
	
}
