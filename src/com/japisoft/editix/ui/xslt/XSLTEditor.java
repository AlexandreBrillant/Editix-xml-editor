package com.japisoft.editix.ui.xslt;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.TransferHandler;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;

import com.japisoft.editix.ui.xslt.debug.DebugContext;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.app.toolkit.Toolkit;
import com.japisoft.framework.collection.FastVector;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.dockable.InnerWindowProperties;
import com.japisoft.framework.dockable.JDock;
import com.japisoft.framework.dockable.JDockEvent;
import com.japisoft.framework.dockable.JDockListener;
import com.japisoft.framework.dockable.action.ActionModel;
import com.japisoft.framework.dockable.action.BasicActionModel;
import com.japisoft.framework.dockable.action.common.MaxMinAction;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.ui.table.FeatureTable;
import com.japisoft.framework.xml.XMLToolkit;
import com.japisoft.framework.xml.parser.document.Document;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.Debug;
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
public class XSLTEditor extends JDock implements 
	IXMLPanel,
	ActionListener,
	JDockListener,
	LineSelectionListener,
	XSLTConsoleMode {

	private XSLTFiles containers = null;
	private XMLDataSourcePanel xmlData = null;
	private ResultPanel resPanel = null;
	private Factory factory;

	public static String DEF_XSLTFile = null;
	public static String DEF_DATAFile = null;
	public static String DEF_RESULTFile = null;
	public static String DEBUG_PROPERTY = "xslt.debug";
	public static String DEBUG_CURRENT_LINE = "xslt.debug.line";
	public static String PROFILER_PROPERTY = "xslt.profiler";
	public static String ENCODING = "encoding";
	public static String LOADRES_CMD = "reload.res";
	public static String MAXIMIZED_CMD = "maximized.editor";	
	private static final String XSLT_EDITOR_MAXIMIZED_PREFERENCE = "editing-maximized";

	private XSLTEditorListener listener;

	public XSLTEditor( 
			XSLTEditorListener listener,
			Factory factory, 
			boolean debug ) {
		super();
		this.listener = listener;
		this.factory = factory;
		this.debugMode = debug;
		initUI( listener );
	}
	
	public Action getAction( String actionId ) {
		return null;
	}

	public void copy() {
	}

	public void cut() {
	}

	public void paste() {
	}
		
	public void setEnabledConsole( boolean enabled ) {
		resPanel.setEnabledConsole( enabled );
	}

	public void setMessage( String txt ) {
		resPanel.setMessage( txt );
	}
	
	@Override
	public Object print() {
		return containers.getSelectedContainer();
	}
	
	public IXMLPanel getPanelParent() {
		return null;
	}
	
	public XMLDataSourcePanel getSourcePanel() {
		return xmlData;
	}
	
	public Parser createNewParser() {
		return null;
	}
	
	public void setDocumentInfo( XMLDocumentInfo info ) {
		containers.getMainContainer().setDocumentInfo( info );
		if ( "XSLT3".equals( 
				info.getType() ) )
			setInnerWindowTitleForId(
				"xslt", "Document Type : [XSLT version 3.0]" 
			);
		else
		if ( "XSLT2".equals( 
				info.getType() ) )
			setInnerWindowTitleForId(
				"xslt", "Document Type : [XSLT version 2.0]" 
			);
		else
			setInnerWindowTitleForId(
				"xslt", "Document Type : [XSLT version 1.0]" 
			);
	}

	protected void setUIReady( boolean added ) {
		if ( added ) {			
			for ( int i = 0; i < containers.getXMLContainerCount(); i++ ) {
				containers.getXMLContainer( i ).getEditor().setTransferHandler(
					new DropSimpleNodeTransferHandler( containers.getXMLContainer( i ).getEditor().getTransferHandler() ) 
				);
			}
		}

		if ( added )
			addJDockListener( this );
		else
			removeJDockListener( this );

		try {
			if ( Preferences.getPreference(
					"xslt",
					XSLT_EDITOR_MAXIMIZED_PREFERENCE,
					false
				) ) {
				maximizeInnerWindow( "xslt" );
			}
		} catch( RuntimeException e ) {
			// For external usage
		}
	}

	// For the debugger
	public void showSourceLine(int line) {
		xmlData.showSourceLine( line );
	}
	
	public void showXSLTLine( String uriSource, int line ) {
		containers.showXSLTLine( uriSource, line );
	}

	public BookmarkContext getBookmarkContext() {
		XSLTBookmarkContext newModel = 
			new XSLTBookmarkContext();
		for ( int i = 0; i < containers.getXMLContainerCount(); i++ ) {
			newModel.merge(
				containers.getXMLContainer( i ).getBookmarkContext(), 
				containers.getXMLContainer( i )
			);
		}
		return newModel;
	}

	class CustomInnerPanel extends InnerPanel implements IXMLPanel, XSLTConsoleMode {

		public void dispose() {}

		public void setAutoDisposeMode(boolean disposeMode) {
			XSLTEditor.this.setAutoDisposeMode(disposeMode);
		}

		@Override
		public void setEnabledConsole(boolean enabled) {
			XSLTEditor.this.setEnabledConsole( enabled );
		}
		
		@Override
		public void setMessage(String msg) {
			XSLTEditor.this.setMessage( msg );
		}
		
		public XMLContainer getMainContainer() {
			return containers.getMainContainer();
		}
		
		public Parser createNewParser() {
			return null;
		}
		
		public IXMLPanel getPanelParent() {
			return XSLTEditor.this;
		}

		public XMLContainer getSubContainerAt(int index) {
			return XSLTEditor.this.getSubContainerAt( index );
		}
		public int getSubContainerCount() {
			return XSLTEditor.this.getSubContainerCount();
		}
		public void selectSubContainer(IXMLPanel panel) {
			XSLTEditor.this.selectSubContainer( panel );			
		}
		
		public BookmarkContext getBookmarkContext() {
			return XSLTEditor.this.getBookmarkContext();
		}

		public XMLContainer getSelectedContainer() {
			return containers.getSelectedContainer();
		}
		
		public void copy() {
			containers.getSelectedContainer().copy();
		}
		
		public void cut() {
			containers.getSelectedContainer().cut();
		}
		
		public void paste() {
			containers.getSelectedContainer().paste();
		}
				
		@Override
		public Object print() {
			return XSLTEditor.this.print();
		}
		
		public void setDocumentInfo(XMLDocumentInfo info) {
			containers.getMainContainer().setDocumentInfo( info );
		}		
		
		public Action getAction(String actionId) {
			return null;
		}

		public Iterator getProperties() {
			return XSLTEditor.this.getProperties();
		}

		public Object getProperty(String name, Object def) {
			return XSLTEditor.this.getProperty(name, def);
		}

		public Object getProperty(String name) {
			return XSLTEditor.this.getProperty(name);
		}

		public XMLContainer getSubContainer(String type) {
			return XSLTEditor.this.getSubContainer(type);
		}

		public void prepareToSave() {
			// Switch to the last modified editor
			// Only if the current one is null or not modified
			if ( containers.getSelectedContainer() == null ||
					!containers.getSelectedContainer().isModified() ) {
				for ( int i = 0; i < containers.getXMLContainerCount(); i++ ) {
					XMLContainer tmp = 
						containers.getXMLContainer( i );
					if ( tmp.isModified() ) {
						containers.setSelectedComponent( tmp.getView() );
						break;
					}
				}
			}
			containers.resetVisualState( 
				containers.getSelectedContainer() );
		}	

		public void postLoad() {
		}

		public boolean reload() {
			return XSLTEditor.this.reload();
		}		

		public JComponent getView() {
			return this;
		}

		public void setProperty(String name, Object content) {
			if (LOADRES_CMD.equals(name)) {
				loadResultFile();
			} else
				XSLTEditor.this.setProperty(name, content);
			if ( MAXIMIZED_CMD.equals( name ) ) {
				if ( "true".equals( content ) )
					maximizeInnerWindow( "xslt" );
				else
					restoreInnerWindow( "xslt" );
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
				.getPreference( "defaultXSLTPath" ) );
	}

	public XMLContainer getMainContainer() {
		return containers.getMainContainer();
	}
	
	public XMLContainer getSelectedContainer() {
		return containers.getSelectedContainer();
	}

	public XMLContainer getSubContainer(String type) {
		return xmlData.xmlContainer;
	}

	public XMLContainer getSubContainerAt(int index) {
		return containers.getXMLContainer( index );
	}
	
	public void selectSubContainer( IXMLPanel panel ) {
		for ( int i = 0; i < getSubContainerCount(); i++ ) {
			XMLContainer container = getSubContainerAt( i );
			if ( container == panel ) {
				containers.setSelectedComponent( container.getView() );
			}
		}
	}

	public int getSubContainerCount() {
		return containers.getXMLContainerCount();
	}

	public String getXSLTFile() {
		return containers.getMainContainer().getDocumentInfo().getCurrentDocumentLocation();
	}

	public String getDataFile() {
		return xmlData.file.getText();
	}
	
	public void prepareToSave() {
	}	
	
	public void postLoad() {
	}
	
	public boolean reload() {
		return containers.getMainContainer().reload();
	}	

	private String fileEncoding = null;

	public void setProperty( String name, Object content ) {
		if ( "system-end".equals( name ) ) {
			if ( mustLoadDataFile != null )
				loadDataFile( mustLoadDataFile );
		}
		if ( content == null ) {
			// Remove it
			containers.getMainContainer().setProperty( name, null );
			return;
		}
		containers.getMainContainer().setProperty( name, content );
		if ( DEBUG_PROPERTY.equals( name ) ) {
			if ( isMaximizedInnerWindow( "xslt" ) ) {
				restoreInnerWindow( "xslt" );
			}
			resPanel.debugContainer.updateDebugContext( ( DebugContext )content );
			resPanel.tpResultDebug.setSelectedIndex( 1 );
		} else 
		if ( DEBUG_CURRENT_LINE.equals( name ) ) {
			containers.showCurrentLine( 
				( String )( ( Object[] )content )[ 0 ], ( ( Integer )( ( Object[] )content )[ 1 ] ).intValue() 
			);
		} else
		if ( PROFILER_PROPERTY.equals( name ) ) {
			resPanel.profilerContainer.updateProfilerContext( ( ArrayList )content );
			resPanel.tpResultDebug.setSelectedIndex( 2 );
		} else
		if ( "xslt.data.file".equals( name ) ) {
			if ( mustLoadDataFile != null )
				mustLoadDataFile = content.toString();
			else
				loadDataFile( content.toString() );
		} else if ( ENCODING.equals( name ) )
			fileEncoding = ( String )content;
		else if ( MAXIMIZED_CMD.equals( name ) ) {
				if ( "true".equals( content ) )
					maximizeInnerWindow( "xslt" );
		}
	}

	/** @return a property values */
	public Object getProperty(String name) {
		if ( MAXIMIZED_CMD.equals( name ) ) {
			return "" + isMaximizedInnerWindow( "xslt");
		}
		return containers.getMainContainer().getProperty(name);
	}

	public Object getProperty(String name, Object def) {
		return containers.getMainContainer().getProperty(name, def);
	}

	public Iterator getProperties() {
		if (xmlData != null & xmlData.file != null && xmlData.file.getText() != null && xmlData.file.getText().length() > 0)
			containers.getMainContainer().setProperty("xslt.data.file", xmlData.file.getText());
		return containers.getMainContainer().getProperties();
	}

	public void loadResultFile() {
		String result = (String) containers.getMainContainer().getProperty("xslt.result.file");
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
			containers.getMainContainer().setProperty( "xslt.data.file", fileName );

			if ( preference != null )
				preference.setPreference( "defaultXSLTPath", new File( fileName )
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
		setProperty("xslt.data.file", fileName );
	}

	public void dispose() {
		super.dispose();
		containers.dispose();
		xmlData.dispose();
		resPanel.dispose();
	}
	
	/** @return list of match value */
	List<String> getTemplatesForMatch() {
		ArrayList<String> r = new ArrayList<String>();
		XMLContainer xslContainer = getMainContainer();
		FPNode sn = xslContainer.getRootNode();
		if ( sn != null ) {
			for ( int i = 0; i < sn.childCount(); i++ ) {
				FPNode c = sn.childAt( i );
				if ( c.matchContent( "template" ) ) {
					String match = c.getAttribute( "match" );
					if ( match != null )
						r.add( match );
				}
			}
		}
		return r;
	}

	public void setAutoDisposeMode(boolean disposeMode) {
		containers.getMainContainer().setAutoDisposeMode(disposeMode);
		xmlData.setAutoDisposeMode(disposeMode);
	}

	private void initUI( XSLTEditorListener listener ) {
		this.setLayout( new GridBagLayout() );

		// North

		containers = new XSLTFiles( this, factory, listener );

		this.addInnerWindow(new InnerWindowProperties(
				"xslt", "XSLT Document", containers ),
				new GridBagConstraints(
						0, 0, 2, 2,	1.0, 2.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets( 1, 1, 0, 2 ), 0, 0 ) 
		);

		// South West

		xmlData = new XMLDataSourcePanel(factory);
		xmlData.file.setActionListener(this);
		xmlData.setPreferredSize(new Dimension(300, 200));
		
		BasicActionModel bam = new BasicActionModel(
				new Action[] {
					new TemplateWizardAction(),
					ActionModel.SEPARATOR,
					new EditDataSourceAction(),
					new RefreshAction(),
					ActionModel.SEPARATOR,
					new MaxMinAction()
				} 
		);
	
		this.addInnerWindow(new InnerWindowProperties("data",
				"XML Data source", null, bam, xmlData), new GridBagConstraints(
						0, 2, 1, 1,	1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 1, 1, 0), 0, 0)
		);

		// South Right

		BasicActionModel bam2 = new BasicActionModel(
			new Action[] {
				new EditResultSourceAction(),
				ActionModel.SEPARATOR,
				new CleanResultSourceAction(),
				ActionModel.SEPARATOR,
				new MaxMinAction()				
			} 
		);

		addInnerWindow(new InnerWindowProperties(
				"result",
				"XSLT Result Preview",
				null,
				bam2,
				resPanel = new ResultPanel(factory, debugMode, this)
				), new GridBagConstraints(
								1, 2, 1, 1,	1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
								new Insets(0, 0, 1, 2), 0, 0)
		);

	}

	//////////////////////////////////////////////////////

	public void jdockAction( JDockEvent event ) {
		if ( "xslt".equals( event.getId() ) ) {
			if ( event.getType() == JDockEvent.INNERWINDOW_MAXIMIZED ) {
				if ( containers != null )
					containers.getMainContainer().setProperty(
						MAXIMIZED_CMD, "true" );
			} else
			if ( event.getType() == JDockEvent.INNERWINDOW_RESTORED ) {
				if ( containers != null ) {
					containers.getMainContainer().setProperty(
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
		if ( containers == null )
			return false;
		return "true".equals( 
					containers.getMainContainer().getProperty( MAXIMIZED_CMD ) 
		);
	}

	/////////////////////////////////////////////////////

	private java.util.List listOfXPathResult = null;
	private int xpathCursor = -1;

	// ////////////////////////////////////////////////////

	static class DropSimpleNodeTransferHandler extends TransferHandler {

		private TransferHandler source = null;

		public DropSimpleNodeTransferHandler(TransferHandler handler) {
			this.source = handler;
		}

		public void exportAsDrag(JComponent comp, InputEvent e, int action) {
			source.exportAsDrag(comp, e, action);
		}

		public void exportToClipboard(JComponent comp, Clipboard clip,
				int action) {
			source.exportToClipboard(comp, clip, action);
		}

		public int getSourceActions(JComponent c) {
			return source.getSourceActions(c);
		}

		public Icon getVisualRepresentation(Transferable t) {
			return source.getVisualRepresentation(t);
		}

		public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
			if ( transferFlavors.length > 0 ) {
				if ( transferFlavors[0].getRepresentationClass() == FPNode.class ) {
					return true;
				}
			}
			return source.canImport(comp, transferFlavors);
		}

		public boolean importData(JComponent comp, Transferable t) {
			try {
				Object o = 
					t.getTransferData( t.getTransferDataFlavors()[ 0 ] );

				if ( o instanceof FPNode ) {

					FPNode node = ( FPNode ) o;

					XMLEditor editor = ( XMLEditor ) comp;
					int p = editor.getCaretPosition();

					FPNode selectedNode = 
						editor.getXMLContainer().getCurrentElementNode();
					if ( selectedNode == null ) {
						selectedNode = 
							editor.getXMLContainer().getRootNode();
					}

					if ( selectedNode == null ) // Empty document
						return false;
					String prefix = 
						selectedNode.getDocument().getRoot().getNameSpacePrefix();
					if (prefix == null)
						prefix = "";
					else
						prefix += ":";
					
					// Compute indentation
					int loc = selectedNode.getStartingOffset();
					int index = editor.getDocument().getDefaultRootElement().getElementIndex( loc );
					Element ee = editor.getDocument().getDefaultRootElement().getElement( index );
					int beginning = ee.getStartOffset();
					String tab;
					try {
						tab = editor.getDocument().getText( beginning, loc - beginning );
					} catch (BadLocationException e1) {
						tab = "";
					}
					tab += "\t";
					StringBuffer sb = new StringBuffer( "\n" ).append( tab );

					String matching = null;
					if (node.isText())
						matching = "text()";
					else
						matching = node.getNodeContent();

					if ( selectedNode.matchContent( "stylesheet" ) ) {
						sb.append("<");
						sb.append(prefix);
						sb.append("template match=\"").append(matching).append(
								"\">\n");
						if ( !node.isLeaf() ) {
							sb.append( tab );
							sb.append( tab );
							sb.append( "<" ).append( prefix ).append( "apply-templates/>\n" );
						}
						sb.append( tab );
						sb.append("</").append(prefix).append("template")
								.append(">\n");
						editor.insertText( sb.toString() );
						return true;
					} else {
						XMLEditor editorTmp = ( XMLEditor ) comp;
						try {
							
							// Compute the best matching
							FPNode refNode = selectedNode;
							String currentContext = null;
							while ( refNode != null ) {
								if ( refNode.hasAttribute( "match" ) ) {
									currentContext = refNode.getAttribute( "match" );
									break;
								} else
									if ( refNode.hasAttribute( "select" ) ) {
										currentContext = refNode.getAttribute( "select" );
										break;
									}
								refNode = refNode.getFPParent();								
							}

							if ( currentContext != null ) {
								// Extract the left expression
								int i = currentContext.lastIndexOf( "/" );
								if ( i > -1 ) {
									currentContext = currentContext.substring( i + 1 );
								}
								// Remove predicate
								int j = currentContext.indexOf( "[" );
								if ( j > -1 ) {
									currentContext = currentContext.substring( 0, j );
								}
								String requiredXPath = node.getXPathLocation();
								// Remove location predicate
								requiredXPath = requiredXPath.replaceAll( "\\[\\d+\\]", "" );
								int k = requiredXPath.indexOf( "/" + currentContext + "/" );
								if ( k > -1 ) {
									// Cut it for relative path
									matching = requiredXPath.substring( k + 2 + currentContext.length() );
								} else {
									matching = requiredXPath;
								}
							}

							Rectangle r = editor.modelToView( p );
							JPopupMenu pm = new JPopupMenu();
							pm.add( 
								new InsertForEachAction( editorTmp, tab, prefix, matching ) 
							);
							pm.add( 
								new InsertValueOfAction( editorTmp, tab, prefix, matching ) 
							);
							
							for ( int i = 0; i < node.getViewAttributeCount(); i++ ) {
								pm.add( 
									new InsertValueOfAction( editorTmp,tab, prefix, "@" + node.getViewAttributeAt( i ) ) 
								);	
							}

							pm.show( comp, r.x, r.y );
							return true;
						} catch (BadLocationException e) {
						}
					}
				}

			} catch (UnsupportedFlavorException e) {
			} catch (IOException e) {
			} catch (RuntimeException r) {
				Debug.debug( r );
			}
			return source.importData(comp, t);
		}
	}

	static class InsertForEachAction extends AbstractAction {
		private String prefix,matching,tab;
		private XMLEditor editorTmp;
		public InsertForEachAction( XMLEditor editorTmp, String tab, String prefix, String matching ) {
			putValue( Action.NAME, "Insert a for-each block" );
			this.editorTmp = editorTmp;
			this.tab = tab;
			this.prefix = prefix;
			this.matching = matching;
		}
		public void actionPerformed(ActionEvent e) {
			StringBuffer sb = new StringBuffer("\n");
			sb.append( tab );
			sb.append("<");
			sb.append(prefix);
			sb.append("for-each select=\"").append(matching)
					.append("\">\n");
			sb.append( tab );
			sb.append( "</").append(prefix)
					.append("for-each>\n");
			editorTmp.insertText( sb.toString() );
			editorTmp = null;
		}
	}

	static class InsertValueOfAction extends AbstractAction {
		private String prefix,matching,tab;
		private XMLEditor editorTmp;
		public InsertValueOfAction( XMLEditor editorTmp, String tab, String prefix, String matching ) {
			putValue( Action.NAME, "Insert a value-of for '" + matching + "'" );
			this.tab = tab;
			this.prefix = prefix;
			this.matching = matching;
			this.editorTmp = editorTmp;
		}
		public void actionPerformed(ActionEvent e) {
			StringBuffer sb = new StringBuffer("\n");
			sb.append( tab );
			sb.append("<");
			sb.append(prefix);
			sb.append("value-of select=\"").append(matching)
					.append("\"/>\n");
			editorTmp.insertText( sb.toString() );
			editorTmp = null;
		}
	}
	
	class TemplateWizardAction extends AbstractAction {
		public TemplateWizardAction() {
			Icon i = new ImageIcon( getClass().getResource( "magic_wand.png" ) );
			putValue(
				Action.SMALL_ICON,
				i
			);
			putValue( Action.SHORT_DESCRIPTION, "Template wizard" );
		}
		public void actionPerformed(ActionEvent e) {
			FPNode sn = xmlData.getRootNode();
			if ( sn == null ) {
				factory.buildAndShowErrorDialog( "Can't use the wizard" );
			} else {
				Document d = sn.getDocument();
				FastVector v = d.getFlatNodes();
				if ( v == null ) {
					factory.buildAndShowInformationDialog( "Can't use the wizard" );
				} else {
					HashSet<String> elements = new HashSet<String>();
					for ( int i = 0; i < v.size(); i++ ) {
						FPNode c = ( FPNode )v.get( i );
						if ( c.isTag() )
							elements.add( c.getContent() );
					}
					if ( elements.size() == 0 ) {
						factory.buildAndShowInformationDialog( "Can't find elements" );
					} else {
						List<String> l = new ArrayList<String>(elements);
						Collections.sort( l );
						
						// Remove existing template
						List<String> m = getTemplatesForMatch();
						
						for ( String mm : m )
							l.remove( mm );
						
						if ( l.size() == 0 ) {
							factory.buildAndShowInformationDialog( "No template found" );
						} else {
						
							FeatureTable ft = new FeatureTable(l, "Template match" );
							JScrollPane sp = new JScrollPane( ft );
							sp.setPreferredSize( new Dimension( 300, 300 ) );
							if ( DialogManager.showDialog( 
									ApplicationModel.MAIN_FRAME, 
									"Templates", 
									"Template Wizard", 
									"Choose elements for generating new templates (inserted at the current caret location)", 
									null, 
									sp ) == DialogManager.OK_ID ) {
								List<String> features = ft.getSelectedFeatures();
								
								String prefix = "xsl";
								StringBuffer res = new StringBuffer();
								
								for ( String f : features ) {
									res.append( "\n" );
									res.append( "\n" );
									res.append( "<" );
									if ( prefix != null ) {
										res.append( prefix );
										res.append( ":" );
									}
									res.append( "template match=\"" ).append( f ).append( "\"" );
									res.append( ">" );
									res.append( "\n" );
									res.append( "</" );
									if ( prefix != null ) {
										res.append( prefix );
										res.append( ":" );
									}
									res.append( "template" );
									res.append( ">" );
								}
								
								XMLContainer xc = getMainContainer();
								int offset = xc.getCaretPosition();
								
								xc.getXMLDocument().insertStringWithoutHelper(offset, res.toString(), null );
								
							}
						}
					}
				}
			}
		}
	}

	class CleanResultSourceAction extends AbstractAction {
		public CleanResultSourceAction() {
			Icon i = 
				new ImageIcon( getClass().getResource( "document_new.png" ) );
			putValue(
				Action.SMALL_ICON,
				i
			);
			putValue( Action.SHORT_DESCRIPTION, "Delete the result document" );
		}
		public void actionPerformed(ActionEvent e) {
			if ( getProperty( "xslt.result.file" ) != null ) {
				File f = new File( 
					( String )getProperty( "xslt.result.file" ) 
				);
				if ( f.exists() )
					f.delete();
			}
			resPanel.clean();
		}
	}

	class EditDataSourceAction extends AbstractAction {
		public EditDataSourceAction() {
			Icon i = 
				new ImageIcon( getClass().getResource( "document_edit.png" ) );
			putValue(
				Action.SMALL_ICON,
				i
			);
			putValue( Action.SHORT_DESCRIPTION, "Edit the data source" );
		}		
		public void actionPerformed(ActionEvent e) {
			listener.editDocument( xmlData.getCurrentFilePath() );
		}
	}

	class EditResultSourceAction extends AbstractAction {
		public EditResultSourceAction() {
			Icon i = 
				new ImageIcon( getClass().getResource( "document_edit.png" ) );
			putValue(
				Action.SMALL_ICON,
				i
			);
			putValue( Action.SHORT_DESCRIPTION, "Edit the result" );
		}		
		public void actionPerformed(ActionEvent e) {
			listener.editDocument( ( String )getProperty( "xslt.result.file" ) );
		}		
	}
	
	class RefreshAction extends AbstractAction {

		public RefreshAction() {
			Icon i = 
				new ImageIcon( getClass().getResource( "refresh.png" ) );
			putValue( 
				Action.SMALL_ICON, 
				i 
			);
			putValue( Action.SHORT_DESCRIPTION, "Reload the document" );
		}

		public void actionPerformed(ActionEvent e) {		
			if ( getProperty( "xslt.data.file" ) != null ) {
				mustRefresh = true;
				setProperty(
						"xslt.data.file",
						getProperty( "xslt.data.file" ) );
				mustRefresh = false;
			}
		}
	}

	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.setBounds(10, 10, 500, 500);
		f.getContentPane().add(
				new XSLTEditor( null, new SingleFactoryImpl(), false ).getView() );
		f.setVisible(true);
	}

}
