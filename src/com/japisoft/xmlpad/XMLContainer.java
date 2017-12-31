package com.japisoft.xmlpad;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.job.BasicJob;
import com.japisoft.framework.job.JobManager;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.ui.MultiChoiceButton;
import com.japisoft.framework.xml.SchemaLocator;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.parser.tools.ParserToolkit;
import com.japisoft.xmlpad.action.ActionModel;
import com.japisoft.xmlpad.action.MultipleChoice;
import com.japisoft.xmlpad.action.TreeAction;
import com.japisoft.xmlpad.action.XMLAction;
import com.japisoft.xmlpad.action.file.LoadAction;

import com.japisoft.xmlpad.bookmark.BookmarkContext;
import com.japisoft.xmlpad.editor.*;
import com.japisoft.xmlpad.elementview.ElementView;
import com.japisoft.xmlpad.elementview.ElementViewContext;
import com.japisoft.xmlpad.error.ErrorListener;
import com.japisoft.xmlpad.error.ErrorManager;
import com.japisoft.xmlpad.error.ErrorView;
import com.japisoft.xmlpad.look.LookManager;
import com.japisoft.xmlpad.nodeeditor.Editor;
import com.japisoft.xmlpad.nodeeditor.EditorModel;

import com.japisoft.xmlpad.tree.TreeListeners;
import com.japisoft.xmlpad.tree.TreeState;
import com.japisoft.xmlpad.tree.parser.Parser;
import com.japisoft.xmlpad.tree.parser.XMLParserFactory;
import com.japisoft.xmlpad.tree.renderer.*;
import com.japisoft.xmlpad.helper.HelperManager;
import com.japisoft.xmlpad.helper.SchemaHelperManager;
import com.japisoft.xmlpad.helper.handler.system.ClosingTagHandler;
import com.japisoft.xmlpad.helper.handler.system.DefaultAttributeHandler;
import com.japisoft.xmlpad.helper.handler.system.DefaultEntityHandler;
import com.japisoft.xmlpad.helper.handler.system.DefaultSystemHandler;

import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.print.Printable;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.undo.*;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
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
public class XMLContainer implements IXMLPanel {
	// ACTIONS CLASS

	static {
		JobManager.class.getClass();
		EditorModel.class.getClass();
	}

	public static boolean isMacOSXPlatform() {
		String os = System.getProperty( "os.name" );
		if ( os != null && 
				( os.toLowerCase().indexOf( MACOSX_ID ) > -1 ) )
			return true;
		return false;
	}
	
	private static final String MACOSX_ID = "mac os x";
	
	
	private TreeListeners treeListeners;
	private XMLEditor firstEditor;
	private JToolBar toolbar;
	JToolBar treeToolbar;
	private ToolBarModel toolBarModel;
	private ToolBarModel treeToolBarModel;
	private PopupModel popupModel;
	private PopupModel treePopupModel;
	private ComponentFactory cf;
	private XMLDocumentInfo documentInfo;
	private XMLIntegrity documentIntegrity;
	private boolean realTimeTreeOnTextChange = true;
	private CustomToolBarModelListener toolbarModelListener;
	private CustomToolBarModelListener treeToolBarModelListener;
	private CustomPopupModelListener popupModelListener;
	private CustomTreePopupModelListener treePopupModelListener;
	private XMLEditor currentEditor;
	protected JScrollPane sp1, sp2;

	/**
	 * You will have to call <code>dispose</code> after usage */
	protected XMLContainer(ComponentFactory cf) {
		super();
		this.cf = cf;
		prepareUI();
		ActionModel.resetActionState( this );
	}

	/**
	 * Create a new <code>XMLContainer</code>, all components are created by
	 * the default <code>ComponentFactory</code>. You will have to call
	 * <code>dispose</code> after usage of this XMLContainer for freeing inner
	 * resources. You can avoid to call <code>dispose</code> using the second
	 * constructor
	 */
	public XMLContainer() {
		this( ComponentFactory.getFactory() );		
	}

	/**
	 * Create a new XMLContainer. if autoDisposeMode is false, XMLContainer will
	 * free no resource automatically. Thus you will have to call
	 * <code>dispose</code> to conclude the usage for freeing all inner
	 * references and help the garbage collector to do the best job.
	 * 
	 * <p>
	 * Note that if you add/remove/add/... this container you MUSTN'T use the
	 * <code>true</code> value
	 * </p>
	 */
	public XMLContainer( boolean autoDisposeMode ) {
		this();
		setAutoDisposeMode(autoDisposeMode);
	}
	
	// For Special container like with XSLT
	private IXMLPanel parentPanel;
	public void setParentPanel( IXMLPanel parent ) {
		this.parentPanel = parent;
	}
	public IXMLPanel getParentPanel() {
		return parentPanel;
	}

	private IView view;

	/**
	 * This is called by the view while the <code>addNotify</code> or the
	 * <code>removeNotify</code>.
	 * 
	 * @param uiReady
	 *            <code>true</code> when the final view for the container is
	 *            ready
	 */
	public void setUIReady( boolean uiReady ) {
	}
	
	public Action getAction(String actionId) {
		return null;
	}

	/** @return The view containing the final panel */
	public JComponent getView() {
		return getInnerView().getFinalView();
	}
	
	public IXMLPanel getPanelParent() {
		return null;
	}

	public void copy() {
		getEditor().copy();
	}
	public void cut() {
		getEditor().cut();
	}
	public void paste() {
		getEditor().paste();		
	}

	public Object print() {
		return getEditor();
	}
	
	/** For inner usage only */
	public IView getInnerView() {
		if ( view == null )
			view = ComponentFactory.getFactory().getUIContainer( this );
		return view;
	}

	/** @return the main container. This is itself */
	public XMLContainer getMainContainer() {
		return this;
	}

	public XMLContainer getSelectedContainer() {
		return null;
	}		
	
	/** @return a sub container for this document type. This is itself */
	public XMLContainer getSubContainer(String type) {
		return this;
	}
	
	public XMLContainer getSubContainerAt(int index) {
		return null;
	}

	public int getSubContainerCount() {
		return 0;
	}

	public void selectSubContainer(IXMLPanel panel) {
	}	
	
	public boolean reload() {
		if ( getCurrentDocumentLocation() == null )
			return false;
		SwingUtilities.invokeLater( // Synchronized it
				new Runnable() {
					public void run() {
						try {
							LoadAction.loadInBuffer( XMLContainer.this, getCurrentDocumentLocation() );
						} catch( Throwable th ) {
							// ??
						}
					}
				} );						
		return true;
	}	

	/**
	 * @return <code>true</code> if this container will manage syntax
	 *         completion
	 */
	public boolean hasSyntaxCompletion() {
		return getHelperManager().isEnabled();
	}
	
	/** Enabled/Disabled syntax completion. By default <code>true</code> */
	public void setSyntaxCompletion(boolean syntaxCompletion) {
		getHelperManager().setEnabled( syntaxCompletion );
	}
	
	private boolean backgroundValidation = false;
	
	public boolean hasBackgroundValidation() {
		return backgroundValidation;
	}
	
	public void setBackgroundValidation( boolean value ) {
		this.backgroundValidation = value;
	}

	private ElementView elementView = null;

	void setElementView(ElementView view) {
		this.elementView = view;
	}

	ElementView getElementView() {
		return elementView;
	}

	private XMLTemplate template = null;

	/** Template for the 'new' operation */
	public void setTemplate(XMLTemplate template) {
		this.template = template;
	}

	/**
	 * @return the default template for the 'new' operation. If user has no
	 *         specify a template, a new one is created
	 */
	public XMLTemplate getTemplate() {
		XMLTemplate _ = null;
		if (template == null) {
			_ = new XMLTemplate();
			_
					.setComment(" Your document, created at : "
							+ new java.util.Date());
		} else
			_ = template;

		if (getDefaultDTDLocation() != null) {
			_.setSchema(null);
			_.setDtd(getDefaultDTDLocation());
			_.setRootNode(getDefaultDTDRoot());
		} else {
			if (getDefaultSchemaLocation() != null) {
				_.setSchema(getDefaultSchemaLocation());
				_.setDtd(null);
				_.setRootNode(getDefaultSchemaRoot());
			}
		}
		return _;
	}

	/**
	 * Reset the current documentation info. This objet contains a set of
	 * information like the file filter about editable document. If a template
	 * is found inside this info, it will replace the current XMLTemplate
	 * @param info */
	public void setDocumentInfo( XMLDocumentInfo info ) {
		if ( info == null ) {
			documentInfo = null;
			return;
		}
		this.documentInfo = info.cloneDocument();

		if ( info.getTemplate() != null ) {
			XMLTemplate template = new XMLTemplate();
			template.setRawContent( info.getTemplate() );
			setTemplate( template );
		}

		getSyntaxHelper().setExternalDTDCommentFile( info.getDTDExternalCommentFile() );
		getSyntaxHelper().setDefaultNamespace( info.getDefaultNamespace() );
		
		if ( info.getHelperHandlers() != null )
			getHelperManager().resetHandlers( info.getHelperHandlers(), info.isAssistantAppendMode() );
		else {
			if ( info.hasDefaultAssistant() )
				installDefaultHelperHandlers( getHelperManager() );
		}
	}

	/** @return the current document info */
	public XMLDocumentInfo getDocumentInfo() {
		if (documentInfo == null)
			documentInfo = new XMLDocumentInfo();
		return documentInfo;
	}

	private HelperManager helperManager = null;

	/** @return the current helper manager. This is the container for all the content assistant definition */
	public HelperManager getHelperManager() {
		if ( helperManager == null ) {
			helperManager = new HelperManager( this );
			installDefaultHelperHandlers( helperManager );
		}
		return helperManager;
	}
	
	/** Install the default assistants for the helper */
	protected void installDefaultHelperHandlers( HelperManager manager ) {
		ArrayList handlers = new ArrayList();
		handlers.add( new DefaultSystemHandler() );
		handlers.add( new DefaultEntityHandler() );
		handlers.add( new ClosingTagHandler() );
		handlers.add( new DefaultAttributeHandler() );
		manager.resetHandlers( handlers, false );
	}
	
	/**
	 * User can add/remove dynamically action by acting on this model.
	 * 
	 * @return the model for the default toolBar
	 */
	public ToolBarModel getToolBarModel() {
		if (toolBarModel == null) {
			toolBarModel = new ToolBarModel(this);
			ToolBarModel.resetToolBarModel(toolBarModel);
		}
		return toolBarModel;
	}

	/**
	 * User can add/remove dynamically action by acting on this model.
	 * 
	 * @return the model for the default popup
	 */
	public PopupModel getPopupModel() {
		if (popupModel == null) {
			popupModel = new PopupModel(this);
			PopupModel.resetPopupModel(popupModel);
		}
		return popupModel;
	}

	/** @return the model of available actions on the tree's popup */
	public PopupModel getTreePopupModel() {
		if (treePopupModel == null) {
			treePopupModel = new PopupModel(this);
			PopupModel.resetTreePopupModel(treePopupModel);
		}
		return treePopupModel;
	}

	/** @return the model of available actions on the tree */
	public ToolBarModel getTreeToolBarModel() {
		if (treeToolBarModel == null) {
			treeToolBarModel = new ToolBarModel(this);
			resetDefaultTreeToolBarModel(treeToolBarModel);
		}
		return treeToolBarModel;
	}

	protected void resetDefaultTreeToolBarModel(ToolBarModel model) {
		ToolBarModel.resetTreeToolBarModel(treeToolBarModel);
	}

	private SchemaHelperManager syntaxhelper = null;

	/**
	 * @return the current SyntaxHelper. This helper is used for showing a popup
	 *         for available tags or entities choice
	 */
	public SchemaHelperManager getSyntaxHelper() {
		if (syntaxhelper == null)
			syntaxhelper = new SchemaHelperManager( 
					getErrorManager(), getHelperManager() );
		//syntaxhelper.setErrorListener( getEditor() );
		return syntaxhelper;
	}

	private JScrollPane editorScrollPane;
	private JPanel panelEditor;
	JSplitPane mainSplitPane;
	private JTree tree;

	protected JScrollPane prepareScrollPaneTextEditor( JComponent component ) {
		JScrollPane tmp = 
			new JScrollPane( component );
		if ( sp1 == null ) {
			sp1 = tmp;
		}
		else {
			sp2 = tmp;
		}
		return tmp;
	}

	private void prepareUI() {
		panelEditor = cf.getNewPanel();
		panelEditor.setLayout(new BorderLayout());
		panelEditor.add(editorScrollPane = prepareScrollPaneTextEditor(firstEditor == null ? firstEditor = cf
				.getNewXMLEditor( commonContext ) : firstEditor ), BorderLayout.CENTER);
		firstEditor.setXMLContainer(this);
		boolean treeSupported = true;

		try {
			treeSupported = "true".equals(XMLPadProperties.getProperty("tree",
					"true"));
		} catch (Throwable th) {
			th.printStackTrace();
		}

		if (treeSupported && treeAvailable && treeElementViewAvailable )
			setElementView(cf.getNewElementView(this));

		prepareScrollPane(editorScrollPane);
	}

	private JViewport rowHeader = null;
	
	public void setDisplayRowHeader( boolean display ) {
		if ( !display ) {
			rowHeader = editorScrollPane.getRowHeader();
			editorScrollPane.setRowHeader( null );
		} else
			if ( rowHeader != null ) {
				editorScrollPane.setRowHeader( rowHeader );
			}
	}
	
	private void prepareScrollPane(JScrollPane sp) {
		ViewRowComponent p = new ViewRowComponent( this );
		
		JViewport vp = new JViewport();
		vp.setView(p);
		vp.setBackground(firstEditor.getBackground());
		sp.setRowHeader(vp);

		if ( sp.getViewport().getView() instanceof XMLEditor ) {
			( ( XMLEditor )sp.getViewport().getView() ).setViewPainterListener( p );
		}
		
		if ( listenerSP == null ) {
			listenerSP = new ScrollBarListener();
		}
		
		sp.getVerticalScrollBar().addAdjustmentListener( listenerSP );
		getErrorManager().addErrorListener( p );
	}

	private ScrollBarListener listenerSP = null;

	private void repaintRowComponent() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JViewport vp = ((JScrollPane) ((JViewport) getEditor()
						.getParent()).getParent()).getRowHeader();
				vp.invalidate();
				vp.validate();
			}
		});
	}

	/**
	 * @return the current XML tree view
	 * @deprecated use getUIAccessibility().getTree()
	 */
	public JTree getTree() {
		return tree;
	}

	boolean hasTreeDelegate = false;

	/**
	 * This is a way to share a tree which is outside the XMLContainer. This is
	 * only useful if the <code>setTreeAvailable( false )</code> method is
	 * called.
	 * @deprecated use getUIAccessibility().setTreeDelegate */
	public void setTreeDelegate(JTree treeDelegate) {
		tree = treeDelegate;
		hasTreeDelegate = true;
	}

	/** For inner usage only */
	public EditorContext getEditorContext() {
		return commonContext;
	}

	private ArrayList navigation = null;
	private int indexNavigation = -1;
	private int navigationLimit = 10;

	/** Set the navigation limit. By default to 10 */
	public void setNavigationHistoryLimit(int limit) {
		this.navigationLimit = limit;
	}

	/** @return the navigation history limit */
	public int getNavigationHistoryLimit() {
		return navigationLimit;
	}

	/**
	 * Store a new navigation XPath value. This action will updat the navigation
	 * cursor to the new one */
	public void addNavigationHistoryPath(String path) {
		if (navigation == null)
			navigation = new ArrayList();
		navigation.add(path);
		if (navigation.size() > navigationLimit)
			navigation.remove(0);
		indexNavigation = navigation.size();
		updateNavigationHistoryState();
	}

	/**
	 * Remove this XPath navigation value. This action will update the
	 * navigation cursor to the last one
	 */
	public void removeNavigationHistoryPath(String path) {
		if (navigation != null) {
			navigation.remove(path);
			indexNavigation = navigation.size();
			updateNavigationHistoryState();
		}
	}

	/** Update the previous, next action status from the current context */
	public void updateNavigationHistoryState() {
		if (navigation == null || navigation.size() == 0) {
			ActionModel.setEnabledAction(ActionModel.TREE_NEXT_ACTION, false);
			ActionModel.setEnabledAction(ActionModel.TREE_PREVIOUS_ACTION,
					false);
			ActionModel.setEnabledAction(ActionModel.TREE_CLEANHISTORY_ACTION,
					false);
		} else {
			ActionModel.setEnabledAction(ActionModel.TREE_PREVIOUS_ACTION,
					indexNavigation > 0);
			ActionModel.setEnabledAction(ActionModel.TREE_NEXT_ACTION,
					indexNavigation < navigation.size() - 1);
			ActionModel.setEnabledAction(ActionModel.TREE_ADDHISTORY_ACTION,
					true);
			ActionModel.setEnabledAction(ActionModel.TREE_CLEANHISTORY_ACTION,
					true);
		}
	}

	/** Reset the navigation path */
	public void cleanNavigationHistoryPath() {
		navigation = null;
		indexNavigation = -1;
		updateNavigationHistoryState();
	}

	/**
	 * Change the navigation history cursor and return the previous navigation
	 * path. It will return <code>null</code> if the previous path is not
	 * available
	 */
	public String previousNavigationHistoryPath() {
		if (navigation == null)
			return null;
		if (indexNavigation > 0) {
			indexNavigation--;
			updateNavigationHistoryState();
			return (String) navigation.get(indexNavigation);
		}
		return null;
	}

	/**
	 * Change the navigation history cursor and return the next navigation path.
	 * If will return <code>null</code> if the next path is not available
	 */
	public String nextNavigationHistoryPath() {
		if (navigation == null)
			return null;
		if (indexNavigation < navigation.size() - 1) {
			indexNavigation++;
			updateNavigationHistoryState();
			return (String) navigation.get(indexNavigation);
		}
		return null;
	}

	/**
	 * Search inside the current document a DTD and parses it for syntax helper.
	 * It will return true if the DTD is found
	 */
	public boolean searchAndParseDTD() {
		if ( getEditor().getDocument() instanceof XMLPadDocument ) 
			return ((XMLPadDocument) getEditor().getDocument()).parseDTD();
		return false;
	}

	/** Search and parse a schema from the current document */
	public boolean searchAndParseSchema() {
		if ( getEditor().getDocument() instanceof XMLPadDocument ) 
			return ((XMLPadDocument) getEditor().getDocument()).parseSchema();
		return false;
	}

	public void setFocusView(boolean focusView) {
		getInnerView().setFocusView(focusView);
	}

	public void requestFocus() {
		getView().requestFocus();
	}

	BookmarkContext bookmarks;

	/** Set a bookmarkContext. This is required to mark a set of line */
	public void setBookmarkContext(BookmarkContext context) {
		this.bookmarks = context;
	}

	/** @return the current bookmarkContext. By default <code>null</code> */
	public BookmarkContext getBookmarkContext() {
		return bookmarks;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////
	////////// SCHEMA
	/////////////////////////////////////////////////////////////////////////////////////////////////////

	private String currentDTD;
	private String currentDTDRoot;

	/**
	 * @return the current DTD location
	 */
	String getCurrentDTD() {
		return currentDTD;
	}

	/**
	 * @return the current DTD root
	 */
	String getCurrentDTDRoot() {
		return currentDTDRoot;
	}

	public boolean hasSchema() {
		return currentDTD != null || currentSchema != null;
	}
	
	/**
	 * Reset the default document DTD location
	 * 
	 * @param dtdRoot
	 *            The DTD Root element
	 * @param dtd
	 *            The DTD path or URL */
	void setDefaultDTD( String dtdRoot, String dtd ) {
		getDocumentInfo().setDefaultDTD( dtdRoot, dtd );
		setDTD( null, null, -1 );
	}

	/**
	 * Reset the default document DTD location
	 * 
	 * @param dtdRoot
	 *            The DTD Root element
	 * @param dtd
	 *            The DTD URL for content
	 */
	void setDefaultDTD( String dtdRoot, URL dtd ) {
		getDocumentInfo().setDefaultDTD( dtdRoot, dtd );
		try {
			getSyntaxHelper().setExternalDTDCommentFile( getDocumentInfo().getDTDExternalCommentFile() );
			getSyntaxHelper().setDTDContent(dtdRoot, new SchemaLocator( dtd ) );
		} catch (Throwable th) {
		}
	}

	private String currentSchema;
	private String currentSchemaRoot;

	/**
	 * @return the current schema location
	 */
	String getCurrentSchema() {
		return currentSchema;
	}

	/**
	 * @return the current schema root
	 */
	String getCurrentSchemaRoot() {
		return currentSchemaRoot;
	}

	/**
	 * Reset the current schema location
	 * 
	 * @param root
	 *            Schema root tag
	 * @param schemaLocation
	 *            Schema URL or current document relative location
	 * @param schemaDeclaration this is the location of the declaration in the document, can be -1
	 */
	void setSchema(String schemaRoot, String[] namespace, String[] schemaLocation, int schemaDeclaration ) {
		if (schemaLocation == null) {
			schemaRoot = getDocumentInfo().getDefaultSchemaRoot();
			if ( getDocumentInfo().getDefaultSchemaLocation() != null )
				schemaLocation = new String[] { getDocumentInfo().getDefaultSchemaLocation() };
		}
		
		this.currentSchemaRoot = schemaRoot;
		if ( schemaLocation != null && schemaLocation.length > 0 )
			this.currentSchema = schemaLocation[ 0 ];
		else
			this.currentSchema = null;

		getSyntaxHelper().setSchemaLocation(
				getCurrentDocumentLocation(),
				namespace,
				schemaLocation,
				currentSchemaRoot, schemaDeclaration,
				(EntityResolver)getProperty( "entityresolver" ) );

		// Reforce DTD for default value
		if ((currentSchema == null)
				&& (getDocumentInfo().getDefaultDTDLocation() != null)) {
			setDTD((String)null, (String)null, -1 );
		} else if (currentSchema == null && relaxNGValidationLocation != null) {
			getSyntaxHelper().setRelaxNGLocation(relaxNGValidationLocation);
		}
	}
	
	void setDTD( String dtdRoot, SchemaLocator locator ) throws Exception {
		getSyntaxHelper().setExternalDTDCommentFile( getDocumentInfo().getDTDExternalCommentFile() );
		getSyntaxHelper().setDTDContent( dtdRoot, locator );
	}

	/**
	 * Reset the current dtd root and dtd location by this one. This is done
	 * automatically for a new document and for parsing action. If you want
	 * default dtd, call rather the <code>setDefaultDTD</code> method */
	void setDTD(String dtdRoot, String dtdLocation, int dtdDeclarationLine ) {
				
		if (dtdLocation == null) {
			// Use the default one
			dtdRoot = getDocumentInfo().getDefaultDTDRoot();
			dtdLocation = getDocumentInfo().getDefaultDTDLocation();
					
			if ( dtdRoot != null ) {
				if (dtdLocation == null) {
					URL u = getDocumentInfo().getDefaultDTDURL();
					if (u != null) {
						try {
							SchemaLocator locator = new SchemaLocator( u.openStream() ); 
							locator.schemaDeclarationLine = dtdDeclarationLine;
							getSyntaxHelper().setExternalDTDCommentFile( getDocumentInfo().getDTDExternalCommentFile() );
							getSyntaxHelper().setDTDContent( 
									dtdRoot, 
									locator 
							);
							return;
						} catch ( Exception e ) {
						}
					}
				}
			}
		}

		this.currentDTD = dtdLocation;
		this.currentDTDRoot = dtdRoot;

		if ( currentDTD != null ) {
			// Check from the resolver for another location
			// This is useful when using an XML Catalog
			if ( SharedProperties.DEFAULT_ENTITY_RESOLVER != null ) {
				try {
					InputSource source = SharedProperties.DEFAULT_ENTITY_RESOLVER.resolveEntity( null, currentDTD );
					if ( source != null && !( source instanceof DTDMapperUsage ) ) {
							SchemaLocator locator = null;
							if ( source.getCharacterStream() != null )
								locator = new SchemaLocator( source.getCharacterStream() );
							else
							if ( source.getByteStream() != null )
								locator = new SchemaLocator( source.getByteStream() );
							else
							if ( source.getSystemId() != null )
								locator = new SchemaLocator( source.getSystemId() );
							
							if ( locator != null ) {
								getSyntaxHelper().setExternalDTDCommentFile( getDocumentInfo().getDTDExternalCommentFile() );
								getSyntaxHelper().setDTDContent( currentDTDRoot, locator );
								return;
							}
					}
				} catch( Exception exc ) {}
			}
		}
		
		getSyntaxHelper().setDTDLocation( getCurrentDocumentLocation(),
				currentDTD, currentDTDRoot, dtdDeclarationLine );

		if ( currentDTD == null && relaxNGValidationLocation != null )
			getSyntaxHelper().setRelaxNGLocation(relaxNGValidationLocation);
	}

	/**
	 * @return the current DTD root element
	 */
	String getDefaultDTDRoot() {
		return getDocumentInfo().getDefaultDTDRoot();
	}

	/**
	 * @return the current DTDLocation built with the current document location
	 */
	String getDefaultDTDLocation() {
		return getDocumentInfo().getDefaultDTDLocation();
	}

	/**
	 * @param resolve
	 *            if <code>true</code> the location is built using the current
	 *            document location
	 * @return the current DTD location. This DTD location can take into account
	 *         relative document location
	 */
	String getDTDLocation(boolean resolve) {
		if (getCurrentDocumentLocation() != null) {
			if (getCurrentDocumentLocation().indexOf("://") == -1) {
				if (currentDTD != null) {
					File f = new File(new File(getCurrentDocumentLocation())
							.getParentFile(), currentDTD);
					if (f.exists())
						return f.toString();
				}
			}
		}
		return currentDTD;
	}

	/**
	 * @return the default schema file/url location
	 */
	String getDefaultSchemaLocation() {
		return getDocumentInfo().getDefaultSchemaLocation();
	}

	/**
	 * @return the default schema root tag
	 */
	String getDefaultSchemaRoot() {
		return getDocumentInfo().getDefaultSchemaRoot();
	}

	/**
	 * Reset the default schema root tag and document location (url or file
	 * path)
	 */
	void setDefaultSchema(String root, String location) {
		getDocumentInfo().setDefaultSchema(root, location);
		getSyntaxHelper().setSchemaLocation(getCurrentDocumentLocation(),
				new String[] { null }, new String[] { location }, root,-1,null);
	}

	private SchemaLocator relaxNGValidationLocation = null;

	void setRelaxNGValidationLocation(SchemaLocator location) {
		if (location == null) {
			getSyntaxHelper().setRelaxNGLocation(
					relaxNGValidationLocation = null);
		} else {
			getSyntaxHelper().setRelaxNGLocation(
					relaxNGValidationLocation = location);
		}
	}

	/**
	 * @return the current RelaxNG document location
	 */
	SchemaLocator getRelaxNGValidationLocation() {
		return relaxNGValidationLocation;
	}

	/**
	 * @param resolve
	 *            if <code>true</code> the location is built using the current
	 *            document location
	 * @return the current Schema location. This schema location can take into
	 *         account relative document location
	 */
	String getSchemaLocation(boolean resolve) {
		if (getCurrentDocumentLocation() != null) {
			if (getCurrentDocumentLocation().indexOf("://") == -1) {
				if (currentSchema != null) {
					File f = new File(new File(getCurrentDocumentLocation())
							.getParentFile(), currentSchema);
					if (f.exists())
						return f.toString();
				}
			}
		}
		return currentSchema;
	}

	//////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Reset the current document location. This is useful for reading/writing a
	 * document
	 */
	public void setCurrentDocumentLocation(String location) {
		getDocumentInfo().setCurrentDocumentLocation(location);
	}

	/** @return the current document location, this is a physical path */
	public String getCurrentDocumentLocation() {
		return getDocumentInfo().getCurrentDocumentLocation();
	}

	/** @return the current document location as an URL */
	public URL getCurrentDocumentLocationURL() {
		String currentPath = getCurrentDocumentLocation();
		try {
			if ( currentPath != null && 
					currentPath.indexOf( "://" ) == -1 ) {
				File f = new File( currentPath );
				return f.toURL();
			} else
				if ( currentPath != null )
					return new URL( currentPath );
		} catch (MalformedURLException e) {
		}
		return null;
	}

	/** More information about the current document location */
	public Object getCurrentDocumentLocationArg() {
		return getDocumentInfo().getCurrentDocumentLocationArg();
	}

	/** Reset an optional argument for the document location */
	public void setCurrentDocumentLocationArg(Object arg) {
		getDocumentInfo().setCurrentDocumentLocationArg(arg);
	}

	private boolean previousModifiedState = true;

	/** Update this container state if the document has been changed */
	public void setModifiedState( boolean state ) {
		if (state == previousModifiedState)
			return;
		previousModifiedState = state;

		firstEditor.resetDocumentState(state);
		if (secondEditor != null)
			secondEditor.resetDocumentState(state);

		ActionModel.setEnabledAction(ActionModel.SAVE_ACTION, state);
		ActionModel.setEnabledAction(ActionModel.SAVEAS_ACTION, state);
		
		if ( !state ) // Fore listeners
			notifyDocumentVersion( true );
	}
	
	public boolean isModified() {
		return previousModifiedState;
	}

	private JPopupMenu popup = null;

	/** @return the current menu popup */
	public JPopupMenu getCurrentPopup() {
		return popup;
	}

	private JPopupMenu treePopup = null;

	/**
	 * @return the current tree menu popup
	 * @deprecated use getUIAccessibility().getCurrentTreePopup()
	 */
	public JPopupMenu getCurrentTreePopup() {
		return treePopup;
	}

	/**
	 * @return the current toolbar
	 * @deprecated use getUIAccessibility().getToolBar()
	 */
	public JToolBar getToolBar() {
		return toolbar;
	}

	/**
	 * @return the current editor component. Editor can changed due to splitting
	 *         state.
	 * @deprecated use getUIAccessibility().getEditor()
	 */
	public XMLEditor getEditor() {
		if ( currentEditor != null )
			return currentEditor;
		if ( firstEditor == null ) {
			firstEditor = cf
			.getNewXMLEditor( commonContext );			
		}
		return firstEditor;
	}

	/** @return the current caret position. -1 is returned if there's no editor */
	public int getCaretPosition() {
		return getEditor().getCaretPosition();
	}
	
	public void setCaretPosition( int caret ) {
		getEditor().setCaretPosition( caret );
	}
	
	/** @return the current caret row. -1 is returned if this is impossible to know */
	public int getCaretRow() {
		int caret = getCaretPosition();
		if ( caret == -1 )
			return -1;
		return getDocument().getDefaultRootElement().getElementIndex( caret );
	}

	private boolean autoFocus = true;

	/**
	 * If true when the setText method is called the current editor gets the
	 * focus. By default to <code>true</code>
	 * 
	 * @param autoFocus
	 *            Get the focus for the setText
	 */
	public void setAutoFocus(boolean autoFocus) {
		this.autoFocus = autoFocus;
	}

	/**
	 * @return <code>true</code> if the current editor gets the focus while
	 *         calling setText
	 */
	public boolean isAutoFocus() {
		return autoFocus;
	}

	private boolean setTextCalled = false;

	/**
	 * Reset the XML content. It is adviced not to call this same method on the
	 * <code>XMLEditor</code>.
	 * 
	 * @deprecated use getAccessibility().setText() */	
	public void setText( String text ) {		
		if ( Preferences.getPreference( "xmlconfig", "carriageReturnMode", ApplicationModel.isMacOSXPlatform() ) ) {
			text = text.replace( "\r", "" );
		}
		setText( text, true );
	}

	/**
	 * Insert text at the current location
	 * @param text */
	public void insertText( String text ) {
		try {
			getEditor().getDocument().insertString( getEditor().getCaretPosition(), text, null );
		} catch( BadLocationException ble ) {
		}
	}

	/**
	 * Reset the XML content. It is adviced not to call this same method on the
	 * <code>XMLEditor</code>.
	 * @param text New text
	 * @param newDocumentFlag specify if this a complete new document
	 * @deprecated use getAccessibility().setText() */
	public void setText(String text, boolean newDocumentFlag ) {
		if (getElementView() != null)
			getElementView().updateView(null);
		this.setTextCalled = true;
		boolean tagIntegrity = getDocumentIntegrity().isProtectTag();
		if (tagIntegrity)
			getDocumentIntegrity().setProtectTag(false);

		if (firstEditor == null) {
			throw new RuntimeException(
					"Illegal XMLContainer usage, please check if the autoDispose mode is disabled ?");
		}

		// Force a notification for no error
		getErrorManager().notifyNoError( true );
		getErrorManager().notifyNoError( false );
		
		removeErrorPanel( false );		
		removeErrorPanel( true );
		
		firstEditor.resetDocumentState(true);
		firstEditor.removeHighlightedLines();
		rootNode = null;

		// Unknown problem ?
		if (firstEditor.getParent() != null
				&& firstEditor.getParent().getParent() != null) {

			try {
				ViewRowComponent vc = (ViewRowComponent) ((JScrollPane) firstEditor
						.getParent().getParent()).getRowHeader().getView();
				vc.setCurrentNode(null);
				vc.repaint();
			} catch( NullPointerException exc ) {}
		}

		boolean stateDocumentEditableMode = isEditableDocumentMode();
		setEditableDocumentMode(true);

		firstEditor.setText( text );
		setEditableDocumentMode( stateDocumentEditableMode );

		if (tagIntegrity)
			getDocumentIntegrity().setProtectTag(true);
		firstEditor.getUndoManager().discardAllEdits();
		if (secondEditor != null)
			secondEditor.getUndoManager().discardAllEdits();
		ActionModel.setEnabledAction(ActionModel.UNDO_ACTION, false);
		if (autoFocus)
			getEditor().requestFocus();

		try {
			if ( getDocumentInfo().isSelectFirstTagAfterReading() ) {
				int firstLocation = ((XMLPadDocument) firstEditor.getDocument()).nextTag(0);
				if (firstLocation >= 0)
					firstEditor.setCaretPosition(firstLocation);
			} else
				firstEditor.setCaretPositionWithoutNotification( 0 );
		} catch (IllegalArgumentException e) {
			// We can ignore it
		}

		if ( !getDocumentInfo().isTEXT() ) {	// Ignore Parsing for pure Text
			boolean ok = false;
			if ( getDocumentInfo().getDefaultDTDLocation() == null )
				ok = searchAndParseDTD();
			if (!ok) {
				if ( getDocumentInfo().getDefaultSchemaLocation() == null )
					ok = searchAndParseSchema();
			}
		}
		notifyDocumentVersion( newDocumentFlag );
		setModifiedState( false );
		cleanNavigationHistoryPath();
	}
	
	/**
	 * @return the XM content
	 * @deprecated use getAccessibility().getText() */
	public String getText() {
		return firstEditor.getText();
	}

	/** @return the current XML document */
	public Document getDocument() {
		if (firstEditor == null)
			return null;
		return firstEditor.getDocument();
	}
	
	public XMLPadDocument getXMLDocument() {
		return (XMLPadDocument)getDocument();
	}
	
	/** @return <code>true</code> if the current editor has a text selection */
	public boolean hasTextSelection() {
		return getEditor().getSelectionEnd() > getEditor().getSelectionStart();
	}

	private boolean editable = true;

	/** The document is only readable for <code>true</code> */
	public void setEditable( boolean editable ) {
		this.editable = editable;
		firstEditor.setEditable( editable );
		if (secondEditor != null)
			secondEditor.setEditable( editable );

		ActionModel.setEnabledAction(ActionModel.COMMENT_ACTION, editable);
		ActionModel.setEnabledAction(ActionModel.CUT_ACTION, editable);
		ActionModel.setEnabledAction(ActionModel.PASTE_ACTION, editable);
		ActionModel.setEnabledAction(ActionModel.NEW_ACTION, editable);
		ActionModel.setEnabledAction(ActionModel.FORMAT_ACTION, editable);
		ActionModel.setEnabledAction(ActionModel.LOAD_ACTION, editable);
	}

	private boolean editableDocumentMode = true;

	/**
	 * This mode will let the cursor, but user will not be abable to update the
	 * document if the parameter is <code>false</code>
	 */
	public void setEditableDocumentMode( boolean editable ) {
		this.editableDocumentMode = editable;
	}

	/** @return <code>true</code> by default */
	public boolean isEditableDocumentMode() {
		return editableDocumentMode;
	}

	/** By default return true */
	public boolean isEditable() {
		return editable;
	}

	int lastTreeDividerLocation = 40;

	/**
	 * Show or hide the location tree. This method has no effect if no tree is
	 * available =><code>XMLPadProperties.setProperty("tree", "false" )</code>
	 * 
	 * @param treeVisible
	 *            Show or hide the current location tree
	 * @deprecated use getUIAccessibility().setTreeVisible
	 */
	public void setTreeVisible(boolean treeVisible) {
		if (tree != null) {
			if (treeVisible)
				mainSplitPane.setDividerLocation(lastTreeDividerLocation);
			else {
				lastTreeDividerLocation = mainSplitPane.getDividerLocation();
				mainSplitPane.setDividerLocation(0);
			}
		}
	}

	/**
	 * @return <code>true</code> if there's a current location tree and the
	 *         splitpane bar is not closed
	 * @deprecated use getUIAccessibility().isTreeVisible()
	 */
	public boolean isTreeVisible() {
		if (tree == null)
			return false;
		return mainSplitPane.getDividerLocation() > 1;
	}

	private boolean treeDragDrop = true;
	
	public boolean hasTreeDragDrop() {
		return treeDragDrop;
	}
	
	public void setTreeDragDrop( boolean treeDragDrop ) {
		this.treeDragDrop = treeDragDrop;
	}

	private boolean treeAvailable = true;

	/**
	 * Decide to have a left tree with the XML document content or not. By
	 * default <code>true</code>
	 * 
	 * @deprecated use getUIAccessibility().setTreeAvailable() */
	public void setTreeAvailable(boolean treeAvailable) {
		this.treeAvailable = treeAvailable;
	}

	/**
	 * @return <code>true</code> if the tree is available
	 * @deprecated getUIAccessibility().isTreeAvailable() */
	public boolean isTreeAvailable() {
		return treeAvailable;
	}

	/**
	 * Activate/Deactivate the toolbar for the tree
	 * @deprecated
	 * @param treeToolBarAvailable */
	public void setTreeToolBarAvailable( boolean treeToolBarAvailable ) {
		this.treeToolBarAvailable = treeToolBarAvailable;
	}

	/**
	 * @deprecated
	 * @return <code>true</code> if a tree toolbar is available */
	public boolean isTreeToolBarAvailable() {
		return this.treeToolBarAvailable;
	}

	private boolean treeElementViewAvailable = true;

	/**
	 * Activate the tree element view
	 * @deprecated
	 * @param treeElementViewAvailable
	 */
	public void setTreeElementViewAvailable( boolean treeElementViewAvailable ) {
		this.treeElementViewAvailable = treeElementViewAvailable;
	}
	
	/**
	 * @deprecated
	 * @return <code>true</code> if the element view is available */
	public boolean isTreeElementViewAvailable() {
		return this.treeElementViewAvailable;
	}

	private boolean toolBarVisible = true;
	
	/**
	 * Show or hide the default toolbar. This method has no effect if the
	 * default toolbar is not available. Note that this method is only for
	 * dynamic usage, it has no effect before the visibility of the
	 * <code>XMLContainer</code>. If you wish no toolbar, you must call
	 * <code>setToolBarAvailable( false )</code>
	 * 
	 * @deprecated getUIAccessibility().setToolBarVisible */
	public void setToolBarVisible(boolean toolbarVisible) {
		if (!toolBarAvailable)
			return;
		this.toolBarVisible = toolbarVisible;
		if (toolbar != null) {
			if (!toolbarVisible)
				getView().remove(toolbar);
			else
				getView().add(toolbar, BorderLayout.NORTH);
		}
		getView().invalidate();
		getView().validate();
	}

	/**
	 * @return the toolbar state
	 * @deprecated getUIAccessibility().isToolBarVisible */
	public boolean isToolBarVisible() {
		if (!toolBarAvailable)
			return false;
		return toolBarVisible;
	}

	boolean treeToolBarAvailable = true;

	private boolean toolBarAvailable = true;

	/**
	 * Create a default toolbar. By default <code>true</code>. If user has an
	 * external toolbar, this property must be set to <code>false</code>. It
	 * is possible to control the visibility by calling
	 * <code>setToolBarVisible( ... )</code>.
	 * 
	 * @deprecated getUIAccessibility().setToolBarAvailable
	 */
	public void setToolBarAvailable(boolean toolBarAvailable) {
		this.toolBarAvailable = toolBarAvailable;
	}

	/**
	 * @return <code>true</code> if a default toolbar is available.
	 * @deprecated getUIAccessibility().setToolBarAvailable
	 */
	public boolean isToolBarAvailable() {
		return toolBarAvailable;
	}

	private boolean popupAvailable = true;

	/**
	 * Create a default popup. By default <code>true</code>
	 * 
	 * @deprecated use getUIAccessibility().setPopupAvailable */
	public void setPopupAvailable(boolean popupAvailable) {
		this.popupAvailable = popupAvailable;
	}

	/**
	 * @return <code>true</code> if a default popup is available
	 * @deprecated use getUIAccessibility().isPopupAvailable */
	public boolean isPopupAvailable() {
		return popupAvailable;
	}

	private boolean treePopupAvailable = true;

	/**
	 * Reset the tree popup. By default to <code>true</code>. This code has
	 * no effect if no tree is used.
	 * 
	 * @deprecated use getUIAccessibility().setTreePopupAvailable */
	public void setTreePopupAvailable(boolean treePopupAvailable) {
		this.treePopupAvailable = treePopupAvailable;
	}
	
	/**
	 * @return <code>true</code> if a tree exists and if the tree popup is
	 *         available
	 * @deprecated use getUIAccessibility().isTreePopupAvailable
	 */
	public boolean isTreePopupAvailable() {
		if (tree == null)
			return false;
		return treePopupAvailable;
	}

	/** @return the default toolBar using the <code>ComponentFactory</code> */
	protected JToolBar createToolBar() {
		JToolBar tb = cf.getNewToolBar();
		return tb;
	}

	/** @return the default popup using the <code>ComponentFactory</code> */
	protected JPopupMenu createPopupMenu() {
		return cf.getNewPopupMenu();
	}

	/**
	 * Update the toolbarModel adding separator. This method is called by the
	 * <code>XMLContainer</code> constructor. It uses the
	 * <code>ActionModel</code> for adding action. For note : It will invoke
	 * the current <code>NEW_ACTION</code> for initializing the document
	 * content. Note that if no default toolBar is available, this method will
	 * have a limited scope */
	public void resetDefaultToolBarActions() {
		ActionModel.resetActionState(this);
		if (!toolBarAvailable)
			return;
		ToolBarModel.resetToolBarModel(toolBarModel);
	}

	/** Enable the tree selection for each text caret change. By default to true */
	public void setEnabledTreeLocationForCaret(boolean rt) {
		firstEditor.setEnabledTreeLocationForCaret(rt);
	}

	/**
	 * Synchronize the tree each time the text has significatif change. By
	 * default to <code>true</code> */
	public void setEnabledRealTimeStructureChanged(boolean support) {
		firstEditor.setEnabledRealTimeStructureChanged(support);
	}

	boolean addNotifyDone = false;

	private double initialDividerLocation = 0.2;

	private double elementViewDividerLocation = 0.5;

	/** @return the location for the tree divider in percent */
	protected double getInitialDividerLocation() {
		return initialDividerLocation;
	}

	/** @return the location for the element view / tree divider in percent */
	protected double getElementViewInitialDividerLocation() {
		return elementViewDividerLocation;
	}

	/**
	 * Reset the initial divider location between tree and text. This value is
	 * between 0 to 1
	 * 
	 * @param location
	 *            Location between 0 and 1 / 0.5 means at the middle
	 */
	void setInitialTreeTextDividerLocation(double location) {
		this.initialDividerLocation = location;
	}

	/**
	 * Reset the initial divider location between the tree and the element view.
	 * This value is between 0 to 1
	 * 
	 * @param location
	 *            Location between 0 and 1 / 0.5 means at the middle
	 */
	void setElementViewDividerLocation(double location) {
		this.elementViewDividerLocation = location;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private UIStateListener uiListener = null;

	/** Here a listener for knowing the user interface state : ready or not */
	public void setUIStateListener(UIStateListener listener) {
		this.uiListener = listener;
	}

	// Initialized state */
	private boolean initialized = false;

	JSplitPane elementSplitPane = null;

	/**
	 * Prepare the UI elements of the container. This method is used once from
	 * the <code>addNotify</code> method of the main panel ( can be the
	 * CustomView class ) */
	protected void initOnce(JComponent view) {
		if ( initialized )
			return;

		initialized = true;
		Debug.debug("INIT");

		initModels();

		VisibleStateAction v = new VisibleStateAction();
		view.addComponentListener(v);
		view.addHierarchyListener(v);

		initUI(view);

		if (getDocumentInfo().isRealTimeTree()) {
			treeListeners = new TreeListeners(this);			
		}

		if (tree != null) {
			if ( treeListeners != null )
				treeListeners.init();
		}

		if (!setTextCalled && autoNewDocument)
			ActionModel.activeActionByName(ActionModel.NEW_ACTION);

		// Synchronized the tree
		if (setTextCalled) {
			Debug.debug( "synchronized tree" );
			if ( getTreeListeners() != null )
			getTreeListeners().notifyStructureChanged();
		} else
			Debug.debug( "non synchronized tree" );

		if ( elementView != null ) {
			elementView.init( new BasicElementViewContext() );
		}

		// Disable undo/redo
		ActionModel.setEnabledAction(ActionModel.UNDO_ACTION, false);
		ActionModel.setEnabledAction(ActionModel.REDO_ACTION, false);

		if (uiListener != null)
			uiListener.ready();
		
		initOnceErrorBinding();
	}

	/** Called once for connecting all the UI parts that must manage the parsing errors */
	protected void initOnceErrorBinding() {
		if ( treeListeners != null && 
				tree != null ) {
			getErrorManager().addErrorListener( 
					treeListeners.getTreeErrorListener() );
		}
		if ( elementView != null )
			getErrorManager().addErrorListener( elementView );
		getErrorManager().addErrorListener( getErrorView() );		
		getErrorManager().addErrorListener( new ParsingErrorListener() );
		getErrorManager().addErrorListener( getSyntaxHelper() );
		getErrorManager().addErrorListener( getEditor() );
	}
	
	/** Prepare the layout of the components */
	protected void initUI(JComponent view) {
		view.setLayout(new BorderLayout());

		if ((getDocumentInfo().isTreeAvailable() && treeAvailable)
				&& !hasTreeDelegate) {
			tree = cf.getNewTree();

			tree.setScrollsOnExpand(true);
			LookManager.install(this,tree);
			mainSplitPane = cf.getNewHorizontalSplitPane();
			mainSplitPane.setDividerLocation((int) 0);
			
			JComponent leftComponent = null;
			JComponent sp = null;

			if (!treeToolBarAvailable)
				sp = new JScrollPane(tree);
			else {
				sp = new JPanel();
				sp.setLayout(new BorderLayout());
				if ( treeToolbar != null ) {
					sp.add(treeToolbar, BorderLayout.NORTH);
					treeToolbar.setFloatable(false);
				}
				sp.add(new JScrollPane(tree), BorderLayout.CENTER);
				
				if ( treeToolBarModel != null )
					treeToolBarModel.setEnabledListener(true);
			}

			sp.setPreferredSize(new Dimension(100, 50));
			
			if (elementView == null) {
				leftComponent = sp;
				mainSplitPane.setDividerLocation( 0.4 );
			} else {
				JSplitPane sp2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
				// sp2.setOneTouchExpandable(true);
				sp2.setDividerLocation((int) 0);

				elementSplitPane = sp2;
				sp2.setTopComponent(sp);

				if (elementView.autoScroll())
					sp2.setBottomComponent(elementView.getView());
				else
					sp2.setBottomComponent(new JScrollPane(elementView
							.getView()));

				leftComponent = sp2;
			}

			mainSplitPane.setLeftComponent(leftComponent);
			mainSplitPane.setRightComponent(panelEditor);

			view.add(mainSplitPane, BorderLayout.CENTER);

			if (treeListeners != null)
				treeListeners.notifyStructureChanged();

		} else {
			view.add(panelEditor, BorderLayout.CENTER);
		}

		if (toolBarAvailable) {
			view.add(toolbar = createToolBar(), BorderLayout.NORTH);
			toolBarModel
					.addToolBarModelListener(toolbarModelListener = new CustomToolBarModelListener(
							toolbar));
			toolBarModel.setEnabledListener(true);
		}

	}
	
	/** Prepare various models for toolbars or popups */
	protected void initModels() {
		if (toolBarAvailable) {
			toolBarModel = getToolBarModel();
		}

		if (popupAvailable && !useCustomPopupMenu()) {
			popupModel = getPopupModel();
		}

		if (treePopupAvailable)
			treePopupModel = getTreePopupModel();

		if (treeToolBarAvailable && !hasTreeDelegate) {			
			treeToolbar = cf.getNewTreeToolBar();
			if ( treeToolbar != null ) {
				getTreeToolBarModel().addToolBarModelListener(
					treeToolBarModelListener = new CustomToolBarModelListener(
							treeToolbar ));
			}
		}
		
		if (popupAvailable) {
			popup = createPopupMenu();
			if (!useCustomPopupMenu()) {
				popupModel
						.addPopupModelListener(popupModelListener = new CustomPopupModelListener());
				popupModel.setEnabledListener(true);
			}
		} else if (useCustomPopupMenu()) {
			popup = getCurrentPopup();
		}

		if (treePopupAvailable) {
			treePopup = createPopupMenu();
			treePopupModel
					.addPopupModelListener(treePopupModelListener = new CustomTreePopupModelListener());
			treePopupModel.setEnabledListener(true);
		}
	}

	private boolean autoNewDocument = true;

	/**
	 * Decide to initialize the XMLContainer calling the new action. By default
	 * <code>true</code>
	 */
	public void setAutoNewDocument(boolean autoNew) {
		this.autoNewDocument = autoNew;
	}

	/**
	 * @return <code>true</code> if you don't want to use the XMLPAd popup
	 *         menu but your own. By default
	 *         <code>false. Note that you must override too the
	 * getCurrentPopupMenu and disable the default popup</code>
	 */
	protected boolean useCustomPopupMenu() {
		return false;
	}

	/**
	 * Remove all listeners/component connection. This is called by the
	 * <code>removeNotify</code> method if XMLContainer works with the
	 * <code>disposeMode</code>
	 */
	public void dispose() {		
		JobManager.addJob(new DisposeJob());
	}

	class DisposeJob extends BasicJob {

		public void dispose() {
		}
		public boolean isAlone() {
			return false;
		}

		public void stopIt() {
		}

		public Object getSource() {
			return this;
		}

		public void run() {
			if ( helperManager != null ) {
				helperManager.dispose();
			}

			if (syntaxhelper != null) {
				syntaxhelper.dispose();
			}
			
			syntaxhelper = null;
			helperManager = null;
			
			if (tree != null) {
				if ( tree.getCellRenderer() instanceof FastTreeRenderer ) {
					( ( FastTreeRenderer )tree.getCellRenderer() ).dispose();
				}
				treeListeners.dispose();
			}

			if (toolbar != null) {
				// swing bug, it seems that one listener is not removed in
				// AbstractButton in JToolBar
				for (int i = toolbar.getComponentCount() - 1; i >= 0; i--) {
					Component button = toolbar.getComponent(i);
					if (button instanceof AbstractButton) {
						((AbstractButton) button).setAction(null);
					}
				}
				toolbar.removeAll();
			}
			if (popup != null && !useCustomPopupMenu()) {
				// swing bug, it seems that one listener is not removed in
				// AbstractButton in JPopup
				for (int i = popup.getComponentCount() - 1; i >= 0; i--) {
					Component button = popup.getComponent(i);
					if (button instanceof AbstractButton) {
						((AbstractButton) button).setAction(null);
					}
				}
				popup.removeAll();
			}

			if (toolbarModelListener != null)
				toolBarModel.removeToolBarModelListener(toolbarModelListener);
			if (treeToolBarModelListener != null)
				treeToolBarModel
						.removeToolBarModelListener(treeToolBarModelListener);
			if (popupModelListener != null)
				popupModel.removePopupModelListener(popupModelListener);
			if (treePopupModelListener != null)
				treePopupModel.removePopupModelListener(treePopupModelListener);

			if (firstEditor != null) {
				JScrollPane sp = (JScrollPane) firstEditor.getParent().getParent();
				((ViewRowComponent)sp.getRowHeader().getView()).dispose();
				sp.getVerticalScrollBar().removeAdjustmentListener(listenerSP);
				((XMLPadDocument) firstEditor.getDocument()).setEditor(null);
				firstEditor.dispose();
				firstEditor.setXMLContainer(null);
				firstEditor = null;
			}

			if (secondEditor != null) {
				JScrollPane sp = (JScrollPane) secondEditor.getParent().getParent();
				((ViewRowComponent)sp.getRowHeader().getView()).dispose();
				sp.getVerticalScrollBar().removeAdjustmentListener(listenerSP);
				secondEditor.dispose();
				secondEditor.setXMLContainer(null);
				secondEditor = null;
			}
 
			currentEditor = null;
			
			if (toolBarModel != null)
				toolBarModel.dispose();

			if (popupModel != null)
				popupModel.dispose();

			if (treePopupModel != null)
				treePopupModel.dispose();

			if (elementView != null)
				elementView.dispose();

			if ( disposeAction )
				ActionModel.resetActionState( null );
			rootNode = null;
			listenerSP = null;
			if (uiListener != null)
				uiListener.dispose();

			if (view != null)
				view.dispose();

			if ( errorManager != null ) {
				errorManager.dispose();
				errorManager = null;
			}
			
			if ( errorView != null ) {
				errorView.dispose();
				errorView = null;
			}

			disposeAccessiblities();
			
			treeState = null;
			htProperties = null;			

			Debug.debug("DISPOSE XMLContainer");
		}
	}

	boolean disposeMode = false;

	/**
	 * Set JXMLPad in a special mode for freeing internal resource. By default
	 * to <code>false</code>
	 */
	public void setAutoDisposeMode(boolean disposeMode) {
		this.disposeMode = disposeMode;
	}

	boolean disposeAction = true;
	
	/** Decide to dispose the ActionModel when disposing the XMLContainer. By default <code>true</code> */
	public void setDisposeAction( boolean disposeAction ) {
		this.disposeAction = disposeAction;
	}

	/**
	 * @return <code>true</code> if JXMLPad frees itself its inner resource
	 * for the garbage collector. By default to <code>true</code> */
	public boolean isAutoDisposeMode() {
		return disposeMode;
	}
	
	private ErrorManager errorManager = null;

	/** @return the objet managing all the parsing errors */
	public ErrorManager getErrorManager() {
		if ( errorManager == null )
			errorManager = new ErrorManager();
		return errorManager;
	}
	
	private ErrorView errorView = null;
	private boolean showErrorPanel = false;

	/** Override the default error view */
	void setErrorView( ErrorView errorView ) {
		if ( this.errorView != null )
			getErrorManager().removeErrorListener( this.errorView );
		this.errorView = errorView;
		if ( errorView != null )
			getErrorManager().addErrorListener( errorView );
	}

	ErrorView getErrorView() {
		if ( errorView == null ) {
			errorView = ComponentFactory.getFactory().getDefaultErrorView();
		}
		return errorView; 
	}
	
	/**
	 * Show an error panel for each parsing error with the following message. If
	 * user needs to have a custom behavior, it is suggested to use a
	 * <code>DocumentStateListener</code> and disable the error panel by
	 * calling <code>setErrorPanelAvailable( false )</code> */
	protected void showErrorPanel() {
		
		if ( !errorPanelEnabled ||
				getErrorManager().hasLastErrorOnTheFly() && 
				!getErrorView().isShownForOnTheFly() ) {
			return;
		}

		removeLocationPanel();
		showErrorPanel = true;

		// Plug it 
		getErrorView().addErrorSelectionListener( getUIAccessibility().getEditor() );
		
		panelEditor.add( getErrorView().getView(), BorderLayout.SOUTH );
		panelEditor.invalidate();
		panelEditor.validate();
		panelEditor.repaint();
		
		getErrorView().initOnceAdded();	

		getEditor().setCaretColor( Color.red );
	}

	/**
	 * Remove the error panel. If the ErrorPanelAvailable property has been set
	 * to <code>false</code>. This code has no effect */
	protected void removeErrorPanel( boolean onTheFly ) {
		if ( !errorPanelEnabled )
			return;
		if ( showErrorPanel ) {
			if ( !errorView.isShownForOnTheFly() && onTheFly ) {
				// Can't remove for this case
				return;
			}
			getErrorView().removeErrorSelectionListener( getUIAccessibility().getEditor() );
			panelEditor.remove(errorView.getView());
			showErrorPanel = false;
			if (showLocationPanel)
				showLocationPanel();
			panelEditor.invalidate();
			panelEditor.validate();
			panelEditor.repaint();
			getEditor().setCaretColor(Color.black);
		}
	}

	private boolean showLocationPanel = true;

	private LocationPanel cLocationPanel = null;

	private void showLocationPanel() {
		if (showErrorPanel)
			return;
		if (cLocationPanel == null)
			cLocationPanel = new LocationPanel();
		panelEditor.add(cLocationPanel, BorderLayout.SOUTH);
		panelEditor.invalidate();
		panelEditor.validate();
		panelEditor.repaint();
		showLocationPanel = true;
	}

	private void removeLocationPanel() {
		if (cLocationPanel == null)
			return;
		showLocationPanel = false;
		panelEditor.remove(cLocationPanel);
		panelEditor.repaint();
	}

	/**
	 * Edit the current node with the EditorModel API. This API part is for
	 * custom node editor usage
	 * 
	 * @return <code>false</code> if the editing is not allowed here
	 */
	public boolean editNode() {
		return editNode( getCurrentElementNode() );
	}

	/**
	 * Edit the provided node with the EditorModel API. This API part is for
	 * custom node editor usage.
	 * 
	 * @return <code>false</code> if the editing is not allowed here
	 */
	public boolean editNode(FPNode currentNode) {

		if (currentNode == null)
			return false;

		FPNode n = currentNode;

		if (!EditorModel.accept(n))
			return false;

		int start = n.getStartingOffset();
		int stop = n.getStoppingOffset();
		if (n.isTag())
			stop++;

		Editor edit = EditorModel.getEditorForNode(n);
		if (edit == null)
			return false;

		try {

			com.japisoft.xmlpad.nodeeditor.EditorContext context = new com.japisoft.xmlpad.nodeeditor.EditorContext(
					this, n, this.getEditor().getText(start, stop - start));

			edit.edit(context);

			if (context.getResult() != null) {
				getEditor().select(start, stop);
				getEditor().replaceSelection(context.getResult());
			}

		} catch (BadLocationException exc) {
			return false;
		}

		return true;
	}

	private boolean statusBarEnabled = true;

	/**
	 * Choose to show a minimal statusbar with the current location. By default
	 * to <code>true</code>. If you use external status bar and a
	 * LocationListener, you should disable it.
	 */
	public void setStatusBarAvailable(boolean statusBar) {
		this.statusBarEnabled = statusBar;
	}

	/**
	 * @return <code>true</code> if a status bar is shown with the current
	 *         document location
	 */
	public boolean isStatusBarAvailable() {
		return statusBarEnabled;
	}

	private boolean errorPanelEnabled = true;

	/**
	 * Choose to show a minimal panel for each parsing error with a comment
	 * line. By default to <code>true</code>. If you have external panel for
	 * that using a DocumentStateListener, you should disable it
	 */
	public void setErrorPanelAvailable(boolean errorPanel) {
		this.errorPanelEnabled = errorPanel;
	}

	/** @return <code>true</code> if an error panel is shown for parsing error */
	public boolean isErrorPanelAvailable() {
		return errorPanelEnabled;
	}

	/**
	 * Provides the last parsed root node. This is for internal usage only, so
	 * don't override it or call it.
	 */
	public void setLastNodeParsed(FPNode node) {
		this.rootNode = node;
	}

	private FPNode rootNode;


	/** @return the last document location for the caret. It can be a text node or an element */
	public FPNode getCurrentNode() {
		return getEditor().getCurrentNodeLocation();
	}

	/** @return this is similar to the getCurrentNode, except it cannot return a text node */
	public FPNode getCurrentElementNode() {
		FPNode currentOne = getCurrentNode();
		if( currentOne == null )
			return null;
		if ( currentOne.isText() )
			return currentOne.getFPParent();
		return currentOne;
	}

	/** @return the current document root node */
	public FPNode getRootNode() {
		if (treeListeners == null)
			return null;
		return rootNode;
	}
	
	/** Default parser for the outline view and error on the fly */
	public Parser createNewParser() {
		return XMLParserFactory.getInstance().newParser();		
	}

	/** Update the UI elements like the location statusbar for this node */
	public void updateNodeLocation(FPNode content) {		

		if (!showErrorPanel && statusBarEnabled) {
			if (content != null) {
				showLocationPanel();
				cLocationPanel.setLocation(content);
			} else {
				if (cLocationPanel != null) {
					panelEditor.remove(cLocationPanel);
				}
				getView().invalidate();
				getView().validate();
				showLocationPanel = false;
			}
		}

		if ( treeListeners != null )
			treeListeners.resetTreeLocation( content );
		
		if ( 
				firstEditor != null && 
				!hasErrorMessage() ) {
			ViewRowComponent vc = ( ViewRowComponent ) ( ( JScrollPane )firstEditor
					.getParent().getParent() ).getRowHeader().getView();

			vc.setCurrentNode(content);
			vc.repaint();
		}
		
		if (elementView != null) {
			elementView.updateView( content );
		}
	}

	/** Show the tree location in the tree and in a minimal status bar */
	protected void showLocation( FPNode content ) {
		if ( getCurrentNode() == content )
			return;
		updateNodeLocation( content );
	}

	/** @return the manager for real time tree location */
	public TreeListeners getTreeListeners() {
		return treeListeners;
	}

	/** Enable the view of the tree location in the status bar */
	public void setEnabledTreeLocation(boolean location) {
		firstEditor.setEnabledTreeLocationForCaret(location);
	}

	/** Is Enabled the view of the tree location in the status bar ? */
	public boolean isEnabledTreeLocation() {
		return firstEditor.isEnabledTreeLocationForCaret();
	}

	/**
	 * Particular case for using the good editor with focus. User shouldn't use
	 * it
	 */
	public void resetEditor(XMLEditor editor) {
		this.currentEditor = editor;
	}

	private JSplitPane cSP = null;

	private XMLEditor secondEditor = null;

	private JScrollPane secondEditorScrollPane = null;

	TreeState treeState = null;

	private boolean hasFocus = true;

	/**
	 * This is a method called by the inner editor for saving the current tree
	 * state. This is useful when sharing a tree between several XML containers,
	 * it gives information to the shared tree that this is the current XML
	 * container.
	 */
	public void focus() {
		if (hasTreeDelegate && treeState != null) {
			if (treeListeners != null) {
				treeListeners.setState(treeState);
			}
		}
		hasFocus = true;
	}

	/**
	 * This is a method called by the inner editor for restoring the current
	 * tree state. This is only useful when sharing a tree between several XML
	 * containers. The user shouldn't override this method.
	 */
	public void unfocus() {
		if (hasTreeDelegate) {
			treeState = treeListeners.getState();
		}
		hasFocus = false;
	}

	/**
	 * Notify that the current container has the focus when managing one tree
	 * for several XML Container
	 */
	public boolean hasFocus() {
		return hasFocus;
	}

	private boolean split = false;

	/**
	 * if <code>true</code>, it will split the current editors in two one
	 * else it will unsplit the two current editors in only one
	 */
	public void setSplit(boolean split, boolean vertical) {
		if (split) {
			split(vertical);
		} else
			unSplit();
	}

	/** @return true if the current editor is splitted with two ones */
	public boolean isSplit() {
		return split;
	}

	public void split(boolean vertical) {
		splitInner(vertical);
	}

	/** Split the current editor in two ones or unsplit it */
	void splitInner(boolean vertical) {
		if (split) {
			unSplit();
		} else {
			
			boolean stateDispose = disposeMode;
			disposeMode = false;
			panelEditor.remove(editorScrollPane);
			disposeMode = stateDispose;
			
			if (vertical)
				cSP = cf.getNewVerticalSplitPane();
			else
				cSP = cf.getNewHorizontalSplitPane();

			cSP.setTopComponent(editorScrollPane);
			cSP.setBottomComponent(secondEditorScrollPane = prepareScrollPaneTextEditor(
					secondEditor = cf.getNewXMLEditor( commonContext )));

			prepareScrollPane(secondEditorScrollPane);

			secondEditor.setXMLContainer(this);
			panelEditor.add(cSP, BorderLayout.CENTER);
			cSP.setDividerLocation((int) (panelEditor.getHeight() / 2));
			
			split = true;
			panelEditor.invalidate();
			panelEditor.validate();
			getView().repaint();
			
			secondEditor.setAutoResetAction(firstEditor.isAutoResetAction());
			secondEditor.resetDocument((XMLPadDocument) firstEditor.getDocument());
			secondEditor.setEditable(isEditable());
		}
	}

	private EditorContext commonContext = new XMLEditorContext();

	/**
	 * Here a way to reset all <code>XMLAction</code> from the current
	 * XMLEditor focus. By default to <code>true</code>
	 */
	public void setAutoResetAction(boolean autoResetAction) {
		firstEditor.setAutoResetAction(autoResetAction);
		if (secondEditor != null)
			secondEditor.setAutoResetAction(autoResetAction);
	}

	/** Show only one editor */
	public void unSplit() {
		if (isSplit()) {
			boolean tmp = isAutoDisposeMode();
			setAutoDisposeMode( false );
			
			//JPF
			secondEditor.dispose();
			//JPF
			panelEditor.remove( cSP );
			cSP.remove( editorScrollPane );
			
			panelEditor.add( editorScrollPane, BorderLayout.CENTER );
			panelEditor.invalidate();
			panelEditor.validate();
			getView().repaint();
			cSP = null;
			secondEditorScrollPane = null;
			secondEditor = null;
			currentEditor = null;
			ActionModel.resetActionState( this );
			split = false;
			setAutoDisposeMode( tmp );
		}
	}

	private Map htProperties;

	/** Reset the inner properties */
	public void resetProperties(Map map) {
		this.htProperties = map;
	}

	/**
	 * Store a property inside this container. It can be useful for action that
	 * must store their state between several editors
	 */
	public void setProperty(String name, Object content) {
		if (htProperties == null)
			htProperties = new HashMap();
		if (content != null)
			htProperties.put(name, content);
		else
			htProperties.remove(name);
	}

	/** @return a property values */
	public Object getProperty(String name) {
		if (htProperties != null)
			return htProperties.get(name);
		return null;
	}
	
	/** Check if a property is available */
	public boolean hasProperty( String name ) {
		return htProperties != null && htProperties.containsKey( name );
	}

	public Iterator getProperties() {
		if (htProperties == null)
			return null;
		return htProperties.keySet().iterator();
	}
	
	public Map getPropertiesMap() {
		return htProperties;
	}

	/** @return a property value or the default one */
	public Object getProperty(String name, Object def) {
		Object obj = getProperty(name);
		if (obj == null)
			return def;
		return obj;
	}

	private void visibleAction(Component c) {

		if (c == getView()) {
			visibleAction();
		}

		if (elementSplitPane != null && mainSplitPane != null) {

			if (elementSplitPane.getDividerLocation() <= 0) {
				elementSplitPane
						.setDividerLocation(getElementViewInitialDividerLocation());
			} else
				elementSplitPane
						.setDividerLocation(getElementViewInitialDividerLocation());

			mainSplitPane.setDividerLocation((int) ((double) getView()
					.getWidth() * getInitialDividerLocation()));

			if (getUIAccessibility().isTreeToolBarAvailable()) {
				if (getInitialDividerLocation() > 0) {
					if (mainSplitPane.getDividerLocation() < getUIAccessibility()
							.getTreeToolBar().getPreferredSize().width) {
						mainSplitPane.setDividerLocation(getUIAccessibility()
								.getTreeToolBar().getPreferredSize().width);
					}
				}
			}

			Debug.debug("Visible Action : " + mainSplitPane.getDividerLocation());
		}
	}

	/** Action when the component is shown at screen */
	protected void visibleAction() {
		Debug.debug( "VISIBLE" );
	}

	/////////////////////////////////////////////////////////////////////
	
	Accessibility access = null;

	/** @return an implementation for interacting easily with XMLContainer */
	public Accessibility getAccessibility() {
		if (access == null)
			access = new XMLContainerAccessibility( this );
		return access;
	}

	SchemaAccessibility schemaAccess = null;

	/** @return an implementation for interacting easily for schemas */
	public SchemaAccessibility getSchemaAccessibility() {
		if (schemaAccess == null)
			schemaAccess = new BasicSchemaAccessibility( this );
		return schemaAccess;
	}

	UIAccessibility uiAccess = null;

	/** @return an implementation for interacting easily with inner components */
	public UIAccessibility getUIAccessibility() {
		if (uiAccess == null)
			uiAccess = new BasicUIAccessibility( this );
		return uiAccess;
	}

	DocumentColorAccessibility documentColorAccess = null;

	/**@return an implementation for updating the color for one document like a particular tag color... */
	public DocumentColorAccessibility getDocumentColorAccessibility() {
		if ( documentColorAccess == null )
			documentColorAccess = new BasicDocumentColorAccessibility( this );
		return documentColorAccess;
	}

	private void disposeAccessiblities() {
		if ( access != null )
			access.dispose();
		if ( uiAccess != null )
			uiAccess.dispose();
		if ( documentColorAccess != null )
			documentColorAccess.dispose();
		if ( schemaAccess != null )
			schemaAccess.dispose();
	}

	////////////////////////////////////////// COLORS //////////////////////////////////

	/** Define a color for this tagName. Use a <code>null</code> color for removing it */
	void setColorForTag( String tagName, Color c ) {
		getDocumentInfo().setColorForTag( tagName, c );
	}

	/** @return a custom color for this tagName */ 
	Color getColorForTag( String tagName ) {
		return getDocumentInfo().getColorForTag( tagName );
	}

	/** @return <code>true</code> if a custom color exists for this tagName */
	boolean hasColorForTag( String tagName ) {
		return getDocumentInfo().hasColorForTag( tagName );
	}
	
	/** Choose a particular color for an attribute. Use the color <code>null</code> for removing it */
	void setColorForAttribute( String attributeName, Color c ) {
		getDocumentInfo().setColorForAttribute( attributeName, c );
	}

	/** @return the user custom color for this attribute */
	Color getColorForAttribute( String attributeName ) {
		return getDocumentInfo().getColorForAttribute( attributeName );
	}

	/** @return <code>true</code> if this attribute has a custom color */ 
	boolean hasColorForAttribute( String attributeName ) {
		return getDocumentInfo().hasColorForAttribute( attributeName );
	}

	/** Choose a particular color for a tag prefix. Use the color <code>null</code> for removing it */
	void setColorForPrefix( String prefixName, Color c ) {
		getDocumentInfo().setColorForPrefix( prefixName, c );
	}

	/** @return a custom color for this prefix name */
	Color getColorForPrefix( String prefixName ) {
		return getDocumentInfo().getColorForPrefix( prefixName );
	}

	/** @return <code>true</code> if a color exist for this prefixName */
	boolean hasColorForPrefix( String prefixName ) {
		return getDocumentInfo().hasColorForPrefix( prefixName );
	}

	/** Choose a particular background color for a tag prefix. Use the color <code>null</code> for removing it */
	void setBackgroundColorForPrefix( String prefixName, Color c ) {
		getDocumentInfo().setBackgroundColorForPrefix( prefixName, c );
	}

	/** @return a custom background color for this prefix name */
	Color getBackgroundColorForPrefix( String prefixName ) {
		return getDocumentInfo().getBackgroundColorForPrefix( prefixName );
	}

	/** @return <code>true</code> if a background color exist for this prefixName */
	boolean hasBackgroundColorForPrefix( String prefixName ) {
		return getDocumentInfo().hasBackgroundColorForPrefix( prefixName );
	}

	///////////////////////////////////////////////////////////////////////////////////////

	class VisibleStateAction extends ComponentAdapter implements
			HierarchyListener {

		public void hierarchyChanged(HierarchyEvent e) {
			visibleAction(e.getComponent());
			getView().removeHierarchyListener(this);
		}

		public void componentHidden(ComponentEvent e) {
			super.componentHidden(e);
		}

		public void componentMoved(ComponentEvent e) {
			super.componentMoved(e);
		}

		public void componentResized(ComponentEvent e) {
			super.componentResized(e);
			visibleAction(e.getComponent());
			e.getComponent().removeComponentListener(this);
		}
	}

	class XMLEditorContext implements EditorContext {
		public void notifyLocation(FPNode location) {
			showLocation(location);
			if (locationListener != null)
				notifyLocationListener(
				new LocationEvent(this, location));
		}
		public void notifyCaretLocation(int x, int y) {
			notifyCaretListener(x, y);
		}
	}

	/** Panel for Location notification */
	class LocationPanel extends JPanel {
		private JLabel lbl = new JLabel();

		public LocationPanel() {
			super();
			setLayout(new BorderLayout());
			add(lbl);
			lbl.setForeground(Color.black);
			lbl.setFont(new Font(null, 0, 12));
		}

		/** Reset the tree location */
		public void setLocation(FPNode s) {
			lbl.setText(s.getXPathLocation());
		}
	}


	/** Listener for updating the popup */
	class CustomPopupModelListener implements PopupModelListener {
		protected JPopupMenu getPopupMenu() {
			return getCurrentPopup();
		}

		protected boolean isPopable(XMLAction a) {
			if (a instanceof TreeAction)
				return ((TreeAction) a).isTreePopable();
			return a.isPopable();
		}

		public void updateActions(java.util.Vector v) {
			getPopupMenu().removeAll();
			Hashtable htGroup = null;
			for (int i = 0; i < v.size(); i++) {
				Action action = (Action) v.get(i);
				if (action == null) {
					if (i < v.size() - 1)
						popup.addSeparator();
				} else {
					String text = (action instanceof XMLAction) ? ((String) action
							.getValue("ACTION.NAME"))
							: null;

					boolean ok = true;
					String group = null;
					JPopupMenu pm = getPopupMenu();
					JMenu menu = null;
					if (action instanceof XMLAction) {
						ok = isPopable((XMLAction) action);
						group = ((XMLAction) action).getPopupGroup();
						if (group != null) {
							if (htGroup == null)
								htGroup = new Hashtable();
							if (!htGroup.containsKey(group)) {
								JMenu _;
								htGroup.put(group, _ = new JMenu(group));
								_.setIcon(new XMLAction.EmptyIcon());
								pm.add(_);
							}
							menu = (JMenu) htGroup.get(group);
						}
					}

					if (ok) {
						JMenuItem m = null;

						if (menu != null)
							m = (JMenuItem) menu.add(action);
						else
							m = (JMenuItem) pm.add(action);

						if (text != null)
							m.setText(text);
					}
				}
			}
		}
	}

	/** Listener for updating the tree popup */
	class CustomTreePopupModelListener extends CustomPopupModelListener {
		protected JPopupMenu getPopupMenu() {
			return getCurrentTreePopup();
		}
	}

	/** Listener for updating the toolbar */
	class CustomToolBarModelListener implements ToolBarModelListener {

		private JToolBar toolbar;

		public CustomToolBarModelListener(JToolBar toolbar) {
			this.toolbar = toolbar;
		}

		public void updateActions(java.util.Vector v) {
			// Rebuild the toolbar
			toolbar.removeAll();

			for (int i = 0; i < v.size(); i++) {
				Action action = (Action) v.get(i);

				if (action == null) {
					toolbar.addSeparator();
				} else {
					if (action instanceof MultipleChoice) {
						toolbar.add(new MultiChoiceButton(action));
					} else {
						JButton btn = toolbar.add(action);
						btn.setBorderPainted( false );
					}
				}
			}
		}
	}

	/** Update undo/redo button state */
	public void refreshUndoRedoState() {
		ActionModel.setEnabledAction(ActionModel.UNDO_ACTION, getEditor()
				.getUndoManager().canUndo());
		ActionModel.setEnabledAction(ActionModel.REDO_ACTION, getEditor()
				.getUndoManager().canRedo());
	}

	/** New UndoRedoManager */
	class CustomUndoRedoManager extends UndoManager {

		public void undo() {
			try {
				super.undo();
			} catch (Throwable th) {
			}

			ActionModel.setEnabledAction(ActionModel.UNDO_ACTION, canUndo());
			ActionModel.setEnabledAction(ActionModel.REDO_ACTION, canRedo());
		}

		public void redo() {
			try {
				super.redo();
			} catch (Throwable th) {
			}
			ActionModel.setEnabledAction(ActionModel.UNDO_ACTION, canUndo());
			ActionModel.setEnabledAction(ActionModel.REDO_ACTION, canRedo());
		}

		public boolean addEdit(UndoableEdit anEdit) {
			boolean b = super.addEdit(anEdit);
			ActionModel.setEnabledAction(ActionModel.UNDO_ACTION, canUndo());
			ActionModel.setEnabledAction(ActionModel.REDO_ACTION, canRedo());
			return b;
		}
	}

	/** For inner usage only */
	public UndoManager createUndoManager() { return new CustomUndoRedoManager(); }
	
	/**
	 * Show a popup. This is invoked by <code>XMLEditor</code> so theorically
	 * you needn't to call it directly
	 */
	public void showPopup(Component c, int x, int y) {
		if ( popup != null )
			popup.show( c, x, y );
	}

	/**
	 * @return the current XML integrity manager
	 */
	public XMLIntegrity getDocumentIntegrity() {
		if (documentIntegrity == null)
			documentIntegrity = new XMLIntegrity();
		return documentIntegrity;
	}

	/**
	 * This objet contains data for avoiding to corrupt the current document.
	 * 
	 * @param integrity
	 *            XML integrity manager
	 */
	public void setDocumentIntegrity(XMLIntegrity integrity) {
		documentIntegrity = integrity;
	}

	/**
	 * @return <code>true</code> if the tree is updated for each text change
	 *         rather than on return key
	 */
	public boolean isRealTimeTreeOnTextChange() {
		return realTimeTreeOnTextChange;
	}

	/**
	 * If you use the <code>false</code> value the tree will only be updated
	 * for each return key. This is better for medium or heavy XML document. By
	 * default to <code>false</code>
	 * 
	 * @param realTimeTreeOnTextChange
	 *            Tree is updated for any text change if true
	 */
	public void setRealTimeTreeOnTextChange(boolean realTimeTreeOnTextChange) {
		this.realTimeTreeOnTextChange = realTimeTreeOnTextChange;
	}

	static ResourceBundle bundle = null;

	static boolean hasBundleError = false;

	/** For inner usage, it returns a localized message for this key */
	public static String getLocalizedMessage(String key, String defaultValue) {
		if (hasBundleError)
			return defaultValue;
		try {
			return bundle.getString(key);
		} catch (MissingResourceException exc) {
			return defaultValue;
		}
	}

	static {
		prepareBundle();
	}

	/** For inner usage, it returns a bundle for the default language */
	static ResourceBundle prepareBundle() {
		if (bundle == null) {
			if (SharedProperties.LOCALIZED_MESSAGE) {
				try {
					bundle = ResourceBundle.getBundle(XMLContainer.class
							.getName());
				} catch (MissingResourceException exc) {
				}
			}
			hasBundleError = (bundle == null);
			if (!hasBundleError)
				ParserToolkit.translateMessages(bundle);
		}

		return bundle;
	}

	private CaretListener caretListener;

	/** Add a listener for giving information about the current caret location */
	public void setCaretListener(CaretListener listener) {
		this.caretListener = listener;
	}

	/** Remove a listener */
	public void unsetCaretListener() {
		this.caretListener = null;
	}

	private LocationListener locationListener;

	/** Set a listener for giving information about the current document location */
	public void setLocationListener(LocationListener listener) {
		this.locationListener = listener;
	}

	/** Remove a listener */
	public void unsetLocationListener() {
		locationListener = null;
	}

	/**
	 * Notify to all LocationListener that the current document location has
	 * changed. Note that you shouldn't override this method because this is
	 * used internally.
	 */
	public void notifyLocationListener(LocationEvent event) {
		if (locationListener != null)
			locationListener.locationChanged(event);
	}

	/**
	 * Notify to all CaretListener that the current caret location has changed.
	 * Note that you shouldn't override this method because this is used
	 * internally.
	 */
	public void notifyCaretListener(int col, int line) {
		if (caretListener != null)
			caretListener.caretLocation(col, line);
	}

	/** Add listener for document parsing state : error or not */
	public void addDocumentStateListener(DocumentStateListener listener) {
		getInnerView().addDocumentStateListener(listener);
	}

	/** Remove a listener */
	public void removeDocumentStateListener(DocumentStateListener listener) {
		getInnerView().removeDocumentStateListener(listener);
	}

	/** @return <code>true</code> if the last parsing step has error */
	public boolean hasErrorMessage() {
		return getErrorManager().hasLastError();
	}
	
	/**
	 * Notify that a new document has been inserted or the current one has been
	 * altered once. This method will notify all
	 * <code>DocumentStateListener</code> element.
	 */
	public void notifyDocumentVersion(boolean newOne) {

		Object[] listeners = getInnerView().getDocumentStateListeners();

		for (int i = listeners.length - 1; i >= 0; i--) {
			if (newOne)
				((DocumentStateListener) listeners[i]).newDocument( this );
			else
				((DocumentStateListener) listeners[i]).documentModified( this );
		}
	}

	private boolean autoCloseQuote = true;
	
	public boolean hasAutoQuoteClosing() {
		return autoCloseQuote;
	}
	
	public void setAutoQuoteClosing( boolean auto ) {
		autoCloseQuote = auto;
	}
	
	/** A way to receive the current editor mouse move event. Do nothing by default, must be overrided */
	public void editorMouseClicked(MouseEvent e) {}
	public void editorMouseMoved(MouseEvent e) {}	
	public void editorKeyPressed(KeyEvent e) {}
	public void editorKeyReleased(KeyEvent e) {}	

	public void prepareToSave() {}	
	public void postLoad() {}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	class ParsingErrorListener implements ErrorListener {

		public void initErrorProcessing() {}

		public void notifyError(
				Object context,
				boolean localError, 
				String sourceLocation,
				int line, 
				int col, 
				int offset, 
				String message, 
				boolean onTheFly ) {
		}

		public void notifyNoError(boolean onTheFly) {
			removeErrorPanel( onTheFly );
		}

		public void stopErrorProcessing() {
			if ( hasErrorMessage() )
				showErrorPanel();
		}
	}

	class ScrollBarListener implements AdjustmentListener {
		public void adjustmentValueChanged(AdjustmentEvent e) {
			//	repaintRowComponent();

			JViewport vp = ((JScrollPane) ((JViewport) getEditor().getParent())
					.getParent()).getRowHeader();
			if ( vp != null )
				vp.repaint(); // repaint is enough

		}
	}

	class BasicElementViewContext implements ElementViewContext {
		public Point update(String startPart, String endPart, int startOffset,
				int stopOffset) {
			return getXMLDocument().updateElement(startPart, endPart, startOffset,
					stopOffset);
		}

		public boolean isEditable() {
			return XMLContainer.this.isEditable()
					&& JobManager.COMMON_MANAGER.isTerminated("parsing");
		}
	}

	
	

}
