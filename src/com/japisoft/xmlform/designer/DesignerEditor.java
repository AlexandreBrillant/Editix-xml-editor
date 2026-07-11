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

package com.japisoft.xmlform.designer;

import java.awt.event.ActionEvent;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.XMLDocumentInfo;
import com.japisoft.xmlpad.action.ActionModel;
import com.japisoft.xmlpad.action.xml.FormatAction;
import com.japisoft.xmlpad.bookmark.BookmarkContext;
import com.japisoft.xmlpad.tree.parser.Parser;
import com.japisoft.xmlpad.xml.validator.DefaultValidator;
import com.japisoft.xmlpad.xml.validator.Validator;

/** For EditiX */
public class DesignerEditor extends JTabbedPane 
		implements IXMLPanel, ChangeListener {

	private XMLContainer container = null;	
	private Factory factory = null;
	private DesignerComponent ve = null;

	public DesignerEditor( Factory factory ) {
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
				( ve = new DesignerComponent() ) );
	}

	boolean addNotifyOk = false;
	
	public Object print() {
		return this;
	}
	
	public String getCurrentDocumentLocation() {
		return container.getCurrentDocumentLocation();
	}
	
	public void addNotify() {
		addNotifyOk = true;
		super.addNotify();
		addChangeListener( this );
		// Force by defaut the visual mode
		setSelectedIndex( 1 );
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
		ve.cut();
	}
	
	public void paste() {
	}
		
	public Action getAction(String actionId) {
		if ( "cut".equals( actionId ) ) {
			if ( getSelectedIndex() == 1 ) {
				return new AbstractAction() {
					public void actionPerformed( ActionEvent e ) {
						ve.cut();
					}
				};
			}
		}
		return null;
	}

	public IXMLPanel getPanelParent() {
		return null;
	}
	public Parser createNewParser( boolean lightweight ) {
		return null;
	}
	
	public XMLContainer getMainContainer() {
		return container;
	}
	
	public XMLContainer getSelectedContainer() {
		return null;
	}		

	public XMLContainer getSubContainerAt(int index) {
		return null;
	}

	public void selectSubContainer(IXMLPanel panel) {
	}	
	
	public int getSubContainerCount() {
		return 0;
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

	public XMLContainer getSubContainer(String type) {
		return container.getSubContainer( type );
	}

	public BookmarkContext getBookmarkContext() {
		return null;
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
						resetVisualData( false );						
					}
				} );
		if ( addNotifyOk )
			addChangeListener( this );
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

	private void resetVisualData( boolean checkSimpleNode ) {
		
		if ( container.getRootNode() != null || !checkSimpleNode ) {
			
			// Build DOM tree
			DefaultValidator dv = 
				new DefaultValidator( true );
			if ( dv.validate( container, false ) == Validator.ERROR ) {
				factory.buildAndShowErrorDialog( "Error found inside the source\nPlease fix it before using the visual editor" );
				setSelectedIndex( 0 );
			} else {
				
				Document d = dv.getDocument();
				if ( d.getDocumentElement() == null ) {
					
					Element xfe = d.createElement( "xf" );
					xfe.setAttribute( "appVersion", "1.0" );
					d.appendChild( xfe );

				}
				try {
										
					ve.load( 
						container.getCurrentDocumentLocation(), 
						d );
				} catch( Exception exc ) {
					
					exc.printStackTrace();
										
					//factory.buildAndShowErrorDialog( "Error found inside the source\nPlease fix it before using the visual editor" );
					//setSelectedIndex( 0 );					
				}
			}
			
		}
		
	}
	
	public void stateChanged(ChangeEvent e) {
		if ( getSelectedIndex() == 1 ) {

			resetVisualData( true );

		} else {

			try {
				Document result = 
					DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
				ve.save( result );
				// Format the result
				FormatAction fa = ( FormatAction )ActionModel.getActionByName( 
				ActionModel.FORMAT_ACTION );
				XMLContainer oldContainer = fa.getXMLContainer();
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
