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

package com.japisoft.editix.editor.jsx;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;

import com.japisoft.editix.ui.xslt.Factory;
import com.japisoft.editix.ui.xslt.LineSelectionListener;
import com.japisoft.editix.ui.xslt.Preference;
import com.japisoft.editix.ui.xslt.SingleFactoryImpl;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.dockable.InnerWindowProperties;
import com.japisoft.framework.dockable.JDock;
import com.japisoft.framework.dockable.JDockEvent;
import com.japisoft.framework.dockable.JDockListener;
import com.japisoft.framework.dockable.action.ActionModel;
import com.japisoft.framework.dockable.action.BasicActionModel;
import com.japisoft.framework.dockable.action.common.MaxMinAction;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.toolkit.Toolkit;
import com.japisoft.framework.xml.XMLToolkit;
import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.XMLDocumentInfo;
import com.japisoft.xmlpad.bookmark.BookmarkContext;
import com.japisoft.xmlpad.editor.XMLEditor;
import com.japisoft.xmlpad.tree.parser.Parser;

/**
 * JavaScript XML container
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public class JSXContainer extends JDock implements 
	IXMLPanel, 
	ActionListener, 
	JDockListener,
	LineSelectionListener {

	private XMLContainer mainContainer = null;
	private XMLDataSourcePanel xmlData = null;
	private ResultPanel resPanel = null;
	private Factory factory;
	
	public static String DEF_XSLTFile = null;
	public static String DEF_DATAFile = null;
	public static String DEF_RESULTFile = null;
	public static String DEBUG_PROPERTY = "xslt.debug";

	public static String ENCODING = "encoding";
	public static String LOADRES_CMD = "reload.res";
	public static String MAXIMIZED_CMD = "maximized.editor";	
	private static final String XQUERY_EDITOR_MAXIMIZED_PREFERENCE = "editing-maximized";

	public static final String DATAFILE_KEY = "jsx.data.file";
	
	private Action parseAction = null;
	
	public JSXContainer( 
			Factory factory, 
			boolean debug,
			Action parseAction ) {
		super();
		this.parseAction = parseAction;
		this.factory = factory;
		this.debugMode = debug;
		initUI();
	}

	public IXMLPanel getPanelParent() {
		return null;
	}
	
	public Parser createNewParser( boolean lightweightMode ) {
		return null;
	}
	
	@Override
	public String getCurrentDocumentLocation() {
		return mainContainer.getCurrentDocumentLocation();
	}	
	
	public void setDocumentInfo(XMLDocumentInfo info) {
		mainContainer.setDocumentInfo( info );
	}	
	
	public Action getAction(String actionId) {
		if ( "parse".equals( actionId ) )
			return parseAction;
		return null;
	}
	
	public void copy() {
		mainContainer.copy();
	}

	public void cut() {
		mainContainer.cut();
	}

	public void paste() {
		mainContainer.paste();
	}
	
	@Override
	public Object print() {
		return mainContainer;
	}
	
	protected void setUIReady( boolean added ) {		
		if ( added )
			addJDockListener( this );
		else
			removeJDockListener( this );

		try {
			if ( Preferences.getPreference(
					"jsx",
					XQUERY_EDITOR_MAXIMIZED_PREFERENCE,
					false
				) ) {
				maximizeInnerWindow( "jsx" );
			}
		} catch( RuntimeException e ) {
			// For external usage
		}
	}

	// For the debugger
	public void showSourceLine(int line) {
		xmlData.showSourceLine( line );
	}
	public void showXSLTLine(String uriSource, int line) {
		mainContainer.getEditor().highlightLine( line );		
	}
	public void showXSLTLine(int line) {
		mainContainer.getEditor().highlightLine( line );
	}
	
	class CustomInnerPanel extends InnerPanel implements IXMLPanel {

		public void dispose() {}

		public Parser createNewParser( boolean lightweightMode ) {
			return null;
		}	
		
		@Override
		public String getCurrentDocumentLocation() {
			return mainContainer.getCurrentDocumentLocation();
		}		
		
		public IXMLPanel getPanelParent() {
			return JSXContainer.this;
		}
		
		public void copy() {
			mainContainer.copy();
		}

		public void cut() {
			mainContainer.cut();			
		}

		public void paste() {
			mainContainer.paste();			
		}
		
		@Override
		public Object print() {
			return JSXContainer.this.print();
		}
		
		public void setAutoDisposeMode( boolean disposeMode ) {
			JSXContainer.this.setAutoDisposeMode( disposeMode );
		}
		
		public Action getAction( String actionId ) {
			return JSXContainer.this.getAction( actionId );
		}
		
		public XMLContainer getMainContainer() {
			return mainContainer;
		}
		
		public XMLContainer getSelectedContainer() {
			return null;
		}

		public XMLContainer getSubContainerAt(int index) {
			return null;
		}
		
		public int getSubContainerCount() {
			return 0;
		}
		
		public void selectSubContainer(IXMLPanel panel) {
		}		
		
		public BookmarkContext getBookmarkContext() {
			return null;
		}
		
		public void setDocumentInfo(XMLDocumentInfo info) {
			mainContainer.setDocumentInfo( info );
		}

		public Iterator getProperties() {
			return JSXContainer.this.getProperties();
		}

		public Object getProperty(String name, Object def) {
			return JSXContainer.this.getProperty(name, def);
		}

		public Object getProperty(String name) {
			return JSXContainer.this.getProperty(name);
		}

		public XMLContainer getSubContainer(String type) {
			return JSXContainer.this.getSubContainer(type);
		}

		public void prepareToSave() {
		}	
		
		public void postLoad() {
		}		

		public boolean reload() {
			return JSXContainer.this.reload();
		}		

		public JComponent getView() {
			return this;
		}

		public void setProperty(String name, Object content) {
			JSXContainer.this.setProperty(name, content);
			if ( MAXIMIZED_CMD.equals( name ) ) {
				if ( "true".equals( content ) )
					maximizeInnerWindow( "jsx" );
				else
					restoreInnerWindow( "jsx" );
			}
		}
	}

	protected InnerPanel createInnerView() {
		return new CustomInnerPanel();
	}
	
	private boolean debugMode;
	private Preference preference;
	
	public void setPreference( Preference preference ) {
		this.preference = preference;
		xmlData.file.setCurrentDirectory( preference
				.getPreference( "defaultXQueryPath" ) );
	}
	
	public XMLContainer getMainContainer() {
		return mainContainer;
	}
	
	public XMLContainer getSelectedContainer() {
		return null;
	}		
	
	public XMLContainer getSubContainerAt(int index) {
		return null;
	}

	public int getSubContainerCount() {
		return 0;
	}

	public void selectSubContainer(IXMLPanel panel) {
	}	

	public XMLContainer getSubContainer(String type) {
		return xmlData.xmlContainer;
	}

	public String getXSLTFile() {
		return mainContainer.getDocumentInfo().getCurrentDocumentLocation();
	}

	public String getDataFile() {
		return xmlData.file.getText();
	}
	
	public void prepareToSave() {
	}	
	
	public void postLoad() {
	}	
	
	public boolean reload() {
		return mainContainer.reload();
	}	

	private String fileEncoding = null;

	public void setProperty(String name, Object content) {
		if ( "system-end".equals( name ) ) {
			if ( mustLoadDataFile != null )
				loadDataFile( mustLoadDataFile );
		}
		if ( content == null ) {
			// Remove it
			mainContainer.setProperty( name, null );
			return;
		}
		mainContainer.setProperty(name, content);
				
		if (DEBUG_PROPERTY.equals(name)) {
			resPanel.tpResultDebug.setSelectedIndex(1);
		} else 
		if (DATAFILE_KEY.equals(name)) {
			if ( mustLoadDataFile != null )
				mustLoadDataFile = content.toString();
			else
				loadDataFile( content.toString() );
		} else if (ENCODING.equals(name))
			fileEncoding = ( String )content; 

	}

	/** @return a property values */
	public Object getProperty(String name) {
		if ( MAXIMIZED_CMD.equals( name ) ) {
			return "" + isMaximizedInnerWindow( "xquery");
		}
		return mainContainer.getProperty(name);
	}

	public Object getProperty(String name, Object def) {
		return mainContainer.getProperty(name, def);
	}

	public BookmarkContext getBookmarkContext() {
		return null;
	}	
	
	public Iterator getProperties() {
		if ( xmlData.file != null )
			if (xmlData.file.getText().length() > 0)
				mainContainer.setProperty(DATAFILE_KEY, xmlData.file.getText());
		return mainContainer.getProperties();
	}

	private String mustLoadDataFile = null;
	private boolean mustRefresh = false;
	
	public void loadDataFile( String fileName ) {
		
		if ( isMaximized() ) {
			mustLoadDataFile = fileName;
			return;
		}
		
		xmlData.file.setText( fileName );

		try {

			ApplicationModel.debug( "Load DataFile " + fileName );

			String data = XMLToolkit.getContentFromURI(
					fileName,
					Toolkit.getCurrentFileEncoding() ).getContent();
			xmlData.xmlContainer.getDocumentInfo().setCurrentDocumentLocation(
					fileName);
			xmlData.xmlContainer.setText( data );

			// mainContainer.setProperty( "jsx.data.file", fileName );

		} catch ( Throwable th ) {
			xmlData.xmlContainer.setText(
					"Can't load " + fileName + ":\n"
					+ th.getMessage() );
			xmlData.tp.setSelectedIndex( 1 );
		}

	}

	// For a new Data source
	public void actionPerformed(ActionEvent e) {
		String fileName = xmlData.file.getText();
		setProperty(DATAFILE_KEY, fileName );
	}

	public void dispose() {
		super.dispose();
		mainContainer.dispose();
		xmlData.dispose();
		resPanel.dispose();
		editorTmp = null;
	}

	public void setAutoDisposeMode(boolean disposeMode) {
		mainContainer.setAutoDisposeMode(disposeMode);
		xmlData.setAutoDisposeMode(disposeMode);
	}

	private void initUI() {
		this.setLayout( new BorderLayout() );
		
		
		mainContainer = new JSXEditor();
		this.addInnerWindow(
				new InnerWindowProperties(
						"jsx", "JavaScript Document", mainContainer.getView() ),
				BorderLayout.CENTER );
		
		xmlData = new XMLDataSourcePanel(factory);
		xmlData.file.setActionListener(this);
		xmlData.setPreferredSize(new Dimension(300, 200));
		
		BasicActionModel bam = new BasicActionModel(
				new Action[] {
					new RefreshAction(),
					ActionModel.SEPARATOR,
					new MaxMinAction()
				} );
	
		this.addInnerWindow(new InnerWindowProperties("data",
				"XML Data source", null, bam, xmlData), BorderLayout.SOUTH );
		
		
/*
		
		this.setLayout( new GridBagLayout() );

		// North

		mainContainer = new JSXEditor( );

		this.addInnerWindow(new InnerWindowProperties(
				"jsx", "JavaScript Document", mainContainer.getView() ), 
				new GridBagConstraints( 
						0, 0, 2, 2,	1.0, 2.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets( 1, 1, 0, 2 ), 0, 0 ) );

		// South West

		xmlData = new XMLDataSourcePanel(factory);
		xmlData.file.setActionListener(this);
		xmlData.setPreferredSize(new Dimension(300, 200));
		
		BasicActionModel bam = new BasicActionModel(
				new Action[] {
					new RefreshAction(),
					ActionModel.SEPARATOR,
					new MaxMinAction()
				} );
	
		this.addInnerWindow(new InnerWindowProperties("data",
				"XML Data source", null, bam, xmlData), new GridBagConstraints(
						0, 2, 1, 1,	1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 1, 1, 0), 0, 0));

		// South Right

		addInnerWindow(new InnerWindowProperties("result",
				"XML Result Preview", resPanel = new ResultPanel(factory,
						debugMode, this)), new GridBagConstraints(
								1, 2, 1, 1,	1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
								new Insets(0, 0, 1, 2), 0, 0));
	
*/		
	}
	
	//////////////////////////////////////////////////////

	public void jdockAction( JDockEvent event ) {
		if ( "jsx".equals( event.getId() ) ) {
			if ( event.getType() == JDockEvent.INNERWINDOW_MAXIMIZED ) {
				if ( mainContainer != null )
					mainContainer.setProperty(
							MAXIMIZED_CMD, "true" );
			} else
			if ( event.getType() == JDockEvent.INNERWINDOW_RESTORED ) {
				if ( mainContainer != null ) {
					mainContainer.setProperty(
							MAXIMIZED_CMD, "false" );
				}
				if ( mustLoadDataFile != null ) {
					loadDataFile( mustLoadDataFile );
					mustLoadDataFile = null;
				}
			}
		}
	}

	boolean isMaximized() {
		if ( mainContainer == null )
			return false;
		return "true".equals( mainContainer.getProperty( MAXIMIZED_CMD ) );
	}

	/////////////////////////////////////////////////////

	private java.util.List listOfXPathResult = null;
	private int xpathCursor = -1;
	private XMLEditor editorTmp = null;	// Temporary for drag'n drop action
	
	// ////////////////////////////////////////////////////

	class RefreshAction extends AbstractAction {
		
		public RefreshAction() {

			Icon i = Toolkit.getImageIcon( "images/refresh.png" );
			putValue( Action.SMALL_ICON, i );
			
		}

		public void actionPerformed(ActionEvent e) {
			
			if ( getProperty( DATAFILE_KEY ) != null ) {
				mustRefresh = true;
				setProperty(
						DATAFILE_KEY,
						getProperty( DATAFILE_KEY ) );
				mustRefresh = false;
			}
		}
	}

	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.setBounds(10, 10, 500, 500);
		f.getContentPane().add(
				new JSXContainer( new SingleFactoryImpl(), false, null ).getView() );
		f.setVisible(true);
	}
	
}
