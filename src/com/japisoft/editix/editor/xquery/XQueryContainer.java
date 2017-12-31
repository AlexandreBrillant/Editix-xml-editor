package com.japisoft.editix.editor.xquery;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.File;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;

import com.japisoft.editix.ui.xslt.Factory;
import com.japisoft.editix.ui.xslt.LineSelectionListener;
import com.japisoft.editix.ui.xslt.Preference;
import com.japisoft.editix.ui.xslt.SingleFactoryImpl;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.app.toolkit.Toolkit;
import com.japisoft.framework.dockable.InnerWindowProperties;
import com.japisoft.framework.dockable.JDock;
import com.japisoft.framework.dockable.JDockEvent;
import com.japisoft.framework.dockable.JDockListener;
import com.japisoft.framework.dockable.action.ActionModel;
import com.japisoft.framework.dockable.action.BasicActionModel;
import com.japisoft.framework.dockable.action.common.MaxMinAction;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.xml.XMLToolkit;
import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.XMLDocumentInfo;
import com.japisoft.xmlpad.bookmark.BookmarkContext;
import com.japisoft.xmlpad.editor.XMLEditor;
import com.japisoft.xmlpad.tree.parser.Parser;

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
public class XQueryContainer extends JDock implements 
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
	public static String PROFILER_PROPERTY = "xslt.profiler";
	public static String ENCODING = "encoding";
	public static String LOADRES_CMD = "reload.res";
	public static String MAXIMIZED_CMD = "maximized.editor";	
	private static final String XQUERY_EDITOR_MAXIMIZED_PREFERENCE = "editing-maximized";

	private Action parseAction = null;
	
	public XQueryContainer( 
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
	
	public Parser createNewParser() {
		return null;
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
					"xquery",
					XQUERY_EDITOR_MAXIMIZED_PREFERENCE,
					false
				) ) {
				maximizeInnerWindow( "xquery" );
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

		public Parser createNewParser() {
			return null;
		}		
		
		public IXMLPanel getPanelParent() {
			return XQueryContainer.this;
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
			return XQueryContainer.this.print();
		}
		
		public void setAutoDisposeMode( boolean disposeMode ) {
			XQueryContainer.this.setAutoDisposeMode( disposeMode );
		}
		
		public Action getAction( String actionId ) {
			return XQueryContainer.this.getAction( actionId );
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
			return XQueryContainer.this.getProperties();
		}

		public Object getProperty(String name, Object def) {
			return XQueryContainer.this.getProperty(name, def);
		}

		public Object getProperty(String name) {
			return XQueryContainer.this.getProperty(name);
		}

		public XMLContainer getSubContainer(String type) {
			return XQueryContainer.this.getSubContainer(type);
		}

		public void prepareToSave() {
		}	
		
		public void postLoad() {
		}		

		public boolean reload() {
			return XQueryContainer.this.reload();
		}		

		public JComponent getView() {
			return this;
		}

		public void setProperty(String name, Object content) {
			if (LOADRES_CMD.equals(name)) {
				loadResultFile();
			} else
				XQueryContainer.this.setProperty(name, content);
			if ( MAXIMIZED_CMD.equals( name ) ) {
				if ( "true".equals( content ) )
					maximizeInnerWindow( "xquery" );
				else
					restoreInnerWindow( "xquery" );
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
//			resPanel.debugContainer.updateDebugContext((DebugContext) content);
			resPanel.tpResultDebug.setSelectedIndex(1);
		} else 
		if ( PROFILER_PROPERTY.equals( name ) ) {
//			resPanel.profilerContainer.updateProfilerContext( (ArrayList)content );
			resPanel.tpResultDebug.setSelectedIndex( 2 );
		} else
		if ("xquery.data.file".equals(name)) {
			if ( mustLoadDataFile != null )
				mustLoadDataFile = content.toString();
			else
				loadDataFile( content.toString() );
		} else if (ENCODING.equals(name))
			fileEncoding = ( String )content; 
		
		mainContainer.setProperty( name, content );
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
				mainContainer.setProperty("xquery.data.file", xmlData.file.getText());
		return mainContainer.getProperties();
	}

	public void loadResultFile() {
		String result = (String) mainContainer.getProperty("xquery.result.file");
		resPanel.loadResultFile(result, fileEncoding);
	}

	private String mustLoadDataFile = null;
	private boolean mustRefresh = false;
	
	public void loadDataFile( String fileName ) {
		// Don't reload the samefile
		if (!mustRefresh && 
				fileName.equals(xmlData.xmlContainer.getCurrentDocumentLocation()))
			return;

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
			mainContainer.setProperty( "xquery.data.file", fileName );

			if ( preference != null )
				preference.setPreference( "defaultXqueryPath", new File( fileName )
						.getParent() );

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
		setProperty("xquery.data.file", fileName );
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
		this.setLayout( new GridBagLayout() );

		// North

		mainContainer = new XQueryEditor();

		this.addInnerWindow(new InnerWindowProperties(
				"xquery", "XQuery Document", mainContainer.getView() ), 
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
				"XQuery Result Preview", resPanel = new ResultPanel(factory,
						debugMode, this)), new GridBagConstraints(
								1, 2, 1, 1,	1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
								new Insets(0, 0, 1, 2), 0, 0));
		
	}

	//////////////////////////////////////////////////////

	public void jdockAction( JDockEvent event ) {
		if ( "xquery".equals( event.getId() ) ) {
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

			Icon i = new ImageIcon( getClass().getResource( "refresh.png" ) );
			putValue( Action.SMALL_ICON, i );
			
		}

		public void actionPerformed(ActionEvent e) {
			
			if ( getProperty( "xquery.data.file" ) != null ) {
				mustRefresh = true;
				setProperty(
						"xquery.data.file",
						getProperty( "xquery.data.file" ) );
				mustRefresh = false;
			}
		}
	}

	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.setBounds(10, 10, 500, 500);
		f.getContentPane().add(
				new XQueryContainer( new SingleFactoryImpl(), false, null ).getView() );
		f.setVisible(true);
	}
	
}
