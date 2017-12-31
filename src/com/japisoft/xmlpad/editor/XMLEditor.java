package com.japisoft.xmlpad.editor;

import com.japisoft.framework.collection.FastVector;
import com.japisoft.framework.job.FastJob;
import com.japisoft.framework.job.Job;
import com.japisoft.framework.job.SwingEventSynchro;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.look.LookManager;
import com.japisoft.xmlpad.tree.TreeListeners;

import com.japisoft.xmlpad.Debug;
import com.japisoft.xmlpad.SharedProperties;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.action.ActionModel;
import com.japisoft.xmlpad.action.Properties;
import com.japisoft.xmlpad.editor.renderer.BasicLineRenderer;
import com.japisoft.xmlpad.editor.renderer.LineRenderer;
import com.japisoft.xmlpad.editor.renderer.PlainLineRenderer;
import com.japisoft.xmlpad.editor.renderer.SimpleLineRenderer;
import com.japisoft.xmlpad.error.ErrorListener;
import com.japisoft.xmlpad.error.ErrorSelectionListener;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.awt.im.InputContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.undo.*;

import javax.swing.text.*;
import javax.swing.event.*;
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
public class XMLEditor extends JEditorPane implements
		CaretListener,
		FocusListener, 
		ErrorListener,
		ErrorSelectionListener,
		KeyListener,
		MouseMotionListener,
		MouseListener,
		ViewPainterListener {

	private Color errorHighlightColor = Color.red;
	private Color selectionHighlightColor = new Color( 180, 180, 220 );
	private Color xpathHighlightColor = new Color( 0, 100, 0 );

	Object selectionHighlight;
	Object errorHighlightTag;

	private Keymap map;
	private UndoManager um;

	// The default editor kit for this text component
	private Document document;
	private EditorKit EDITOR_KIT;
	private boolean disableCaretListeners = false;
	private EditorUndoableListener editorUndoableListener = new EditorUndoableListener();

	public XMLEditor( EditorContext context ) {
		super();
		this.editorContext = context;
		setBorder( null );
		// setCursor( Cursor.getPredefinedCursor( Cursor.TEXT_CURSOR ) );
		resetDefaultLook();
		initUI(); // After for the UIManager
		initKeymap();
		um = new UndoManager();
	}

	private void initUI() {
		String p = "xmlpad.editor.";
		Font f = UIManager.getFont(p + "font");
		if ( f != null )
			setFont( f );
		Color c = UIManager.getColor( p + "dtdElementColor" );
		if (c != null)
			setColorForDTDElement(c);
		c = UIManager.getColor(p + "dtdNotationColor");
		if (c != null)
			setColorForDTDNotation(c);
		c = UIManager.getColor(p + "dtdAttributeColor");
		if (c != null)
			setColorForDTDAttribute(c);
		c = UIManager.getColor(p + "dtdEntityColor");
		if (c != null)
			setColorForDTDEntity(c);
		c = UIManager.getColor(p + "tagBorderLineColor");
		if (c != null)
			setColorForTagBorderLine(c);
		c = UIManager.getColor(p + "cdataColor");
		if (c != null)
			setColorForCDATA(c);
		c = UIManager.getColor(p + "entityColor");
		if (c != null)
			setColorForEntity(c);
		c = UIManager.getColor(p + "commentColor");
		if (c != null)
			setColorForComment(c);
		c = UIManager.getColor(p + "declarationColor");
		if (c != null)
			setColorForDeclaration(c);
		c = UIManager.getColor(p + "docTypeColor");
		if (c != null)
			setColorForDocType(c);
		c = UIManager.getColor(p + "literalColor");
		if (c != null)
			setColorForLiteral(c);
		c = UIManager.getColor(p + "tagColor");
		if (c != null)
			setColorForTag(c);
		c = UIManager.getColor(p + "invalidColor");
		if (c != null)
			setColorForInvalid(c);
		c = UIManager.getColor(p + "textColor");
		if (c != null)
			setColorForText(c);
		c = UIManager.getColor(p + "attributeColor");
		if (c != null)
			setColorForAttribute(c);
		c = UIManager.getColor(p + "attributeSeparatorColor");
		if (c != null)
			setColorForAttributeSeparator(c);
		c = UIManager.getColor(p + "selectionHighlightColor");
		if (c != null)
			setSelectionHighlightColor(c);
		c = UIManager.getColor(p + "backgroundColor");
		if (c != null)
			setBackground(c);
		c = UIManager.getColor(p + "tagBackground");
		if (c != null)
			setColorForTagBackground(c);
		c = UIManager.getColor(p + "declarationBackground");
		if (c != null)
			setColorForDeclarationBackground(c);
		c = UIManager.getColor(p + "entityBackground");
		if (c != null)
			setColorForEntityBackground(c);
		c = UIManager.getColor(p + "commentBackground");
		if (c != null)
			setColorForCommentBackground(c);
		c = UIManager.getColor(p + "docTypeBackground");
		if (c != null)
			setColorForDocTypeBackground(c);
		c = UIManager.getColor(p + "cdataBackground");
		if (c != null)
			setColorForCDATABackground(c);		
		colorCurrentLine = UIManager.getColor( p + "currentLineColor" );
		if ( colorCurrentLine == null )
			colorCurrentLine = new Color( 230, 250, 230 );		
	}

	public void dispose() {
		Debug.debug("Dispose XMLEditor " + this);
		setTransferHandler( null );
		removeFocusListener( this );
		removeCaretListener( this );
		setViewPainterListener( null );
		getDocument().removeUndoableEditListener( editorUndoableListener );
		setUndoManager( null );
		this.editorContext = null;
		removeMouseListener(popupMouseAdapter);
		removeMouseMotionListener( this );
		removeMouseListener( this );
		removeKeyListener( this );
		releaseKeyMap();
	}

	private PopupMouseAdapter popupMouseAdapter;

	/** This method will call the <code>init</code> method */
	public void addNotify() {
		super.addNotify();
		initOnce();		
		if ( enableHighlightCurrentLine )
			highlightCurrentLine();		
	}
	
	/**
	 * Override of the standard JTextComponent.select Move caret to the start of
	 * the selection instead of the end */
	public void select(int selectionStart, int selectionEnd) {
		// argument adjustment done by java.awt.TextComponent
		int docLength = getDocument().getLength();

		if (selectionStart < 0) {
			selectionStart = 0;
		}
		if (selectionStart > docLength) {
			selectionStart = docLength;
		}
		if (selectionEnd > docLength) {
			selectionEnd = docLength;
		}
		if (selectionEnd < selectionStart) {
			selectionEnd = selectionStart;
		}

		setCaretPosition(selectionEnd); // this is the
		// change !
		moveCaretPosition(selectionStart); // this is the
	}

	private boolean initOnce = false;

	/**
	 * Connect listeners to inner parts. Called by the <code>addNotify</code>
	 * method */
	protected void initOnce() {
		if ( initOnce )
			return;
		initOnce = true;
		addCaretListener( this );
		getDocument().addUndoableEditListener( editorUndoableListener );
		addFocusListener(this);
		if ( getXMLContainer() != null ) {
			setUndoManager(
				getXMLContainer().createUndoManager()
			);
		}
		addMouseListener( popupMouseAdapter = new PopupMouseAdapter() );
		initKeyMap();		
		if (getEditorKit() instanceof XMLEditorKit) {
			((XMLEditorKit) getEditorKit()).setSyntaxColor(getXMLContainer()
					.getDocumentInfo().hasSyntaxColor());
			((XMLEditorKit) getEditorKit()).setDTDMode(getXMLContainer()
					.getDocumentInfo().isDtdMode());
		}
		
		if ( getDocument() instanceof XMLPadDocument )
			((XMLPadDocument) getDocument()).setAutoCloseTag(getXMLContainer()
				.getDocumentInfo().hasAutoClosing());
		
		if ( getDocument() instanceof XMLPadDocument )
			((XMLPadDocument) getDocument()).setAutoCloseTag(getXMLContainer()
				.hasAutoQuoteClosing());
		
		setTransferHandler( new CustomTransferHandler() );
		addMouseMotionListener( this );
		addMouseListener( this );
		addKeyListener( this );
	}

	private void initKeyMap() {
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0),
				new CustomTabAction());

		getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_TAB,
						KeyEvent.SHIFT_DOWN_MASK), new CustomUntabAction());
		
		ContentAssistantAction cas = new ContentAssistantAction();
		
		getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,
						Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() ), cas );

		getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,
						Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | KeyEvent.SHIFT_DOWN_MASK ), cas );
		
		getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,
						KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK),
				new EntityAssistantAction());
		getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,
						KeyEvent.CTRL_DOWN_MASK | KeyEvent.ALT_DOWN_MASK),
				new SystemAssistantAction());

		// Moving
		
		getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,
					KeyEvent.CTRL_DOWN_MASK), new SelectNodeDownAction());
		getInputMap()
				.put(
					KeyStroke.getKeyStroke(KeyEvent.VK_UP,
							KeyEvent.CTRL_DOWN_MASK),
					new SelectNodeUpAction());

		getInputMap().put(
			KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,
					KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK ),
			new SelectNextChildNodeAction());		
		getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_UP,
					KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK ), 
					new SelectPreviousChildNodeAction());

		getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN,
					KeyEvent.CTRL_DOWN_MASK), 
					new SelectFirstChildNodeAction());
		getInputMap().put(
					KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP,
							KeyEvent.CTRL_DOWN_MASK  ),
					new SelectParentNodeAction());

	}

	private void releaseKeyMap() {
		getInputMap().remove(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0));
		getInputMap().remove(
				KeyStroke.getKeyStroke(KeyEvent.VK_TAB,
						KeyEvent.SHIFT_DOWN_MASK));
		getInputMap().remove(
				KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,
						KeyEvent.CTRL_DOWN_MASK));
		getInputMap().remove(
				KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,
						KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
		getInputMap().remove(
				KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,
						KeyEvent.CTRL_DOWN_MASK | KeyEvent.ALT_DOWN_MASK));
		
		// Moving
		
		getInputMap().remove(
				KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,
						KeyEvent.CTRL_DOWN_MASK ));
		getInputMap().remove(
				KeyStroke.getKeyStroke(KeyEvent.VK_UP, 
						KeyEvent.CTRL_DOWN_MASK ));
		getInputMap().remove(
				KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,
						KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
		getInputMap().remove(
				KeyStroke.getKeyStroke(KeyEvent.VK_UP, KeyEvent.CTRL_DOWN_MASK
						| KeyEvent.SHIFT_DOWN_MASK));
		getInputMap().remove(
				KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN,
						KeyEvent.CTRL_DOWN_MASK ));
		getInputMap().remove(
				KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 
						KeyEvent.CTRL_DOWN_MASK ));		
		
	}

	/** This method will call the dispose method */
	public void removeNotify() {
		super.removeNotify();
		if (container != null) {
			if (container.isAutoDisposeMode()) {
				dispose();
			}
		}
	}

	/** @return the current document location from the caret position */
	public FPNode getCurrentNodeLocation() {
		return lastStructureLocation;
	}

	private XMLContainer container;

	/** Reset the container for this editor */
	public void setXMLContainer(XMLContainer container) {
		this.container = container;
	}

	/** @return the container for this editor */
	public XMLContainer getXMLContainer() {
		return container;
	}

	public XMLPadDocument getXMLDocument() {
		return (XMLPadDocument) getDocument();
	}

	public Document getDocument() {		
		if (document == null) {
			// document = new XMLDocument(this);
			// this.setEditorKit(getEditorKit());
			document = getEditorKit().createDefaultDocument();
			if ( document instanceof XMLPadDocument ) {
				( ( XMLPadDocument )document ).setXMLEditor( this ); 
			}
		}
		return document;
	}

	public void resetDocument(XMLPadDocument doc) {
		// JPF
		if (document != null && document instanceof XMLPadDocument) {
			((XMLPadDocument) document).setEditor(null);
		}
		// JPF
		document = doc;
		super.setDocument(doc);
	}

	private boolean autoResetAction = true;

	/**
	 * By default to <code>true</code>. This method will enable to reset the
	 * </code> ActionModel</code> each time the current editor take the focus.
	 * Thus all <code>XMLAction</code> will work with the good one.
	 */
	public void setAutoResetAction(boolean reset) {
		this.autoResetAction = reset;
	}

	/**
	 * @return <code>true</code> if a <code>focusGained</code> resets the
	 *         ActionModel
	 */
	public boolean isAutoResetAction() {
		return autoResetAction;
	}

	private boolean focusView = false;

	/**
	 * Each time the editor take the focus. The <code>actionModel</code> wil be
	 * used to active all action on the current editor. User can change this
	 * behavior calling <code>setAutoResetAction</code>
	 */
	public void focusGained(FocusEvent e) {
		setToolTipText( null );
		unhighlightLine();
		if (autoResetAction)
			ActionModel.resetActionState(this, getXMLContainer());
		getXMLContainer().resetEditor(this);
		
		if ( getXMLContainer().isAutoFocus() ) {
			getXMLContainer().focus();
		}
		
		if (delayedStructuredDamaged) {
			delayedStructuredDamaged = false;
			((XMLPadDocument) getDocument()).structureDamaged();
		}
		if (UIManager.getBorder("xmlpad.editor.focusBorder") != null)
			setBorder(UIManager.getBorder("xmlpad.editor.focusBorder"));
	}

	private boolean delayedStructuredDamaged = false;

	/**
	 * Here a way to delay a text/tree synchronization for the next focus gained
	 */
	void setDelayedStructureDamaged(boolean delay) {
		this.delayedStructuredDamaged = delay;
	}

	/** For the moment, nothing is done */
	public void focusLost(FocusEvent e) {
		focusView = false;
		if (UIManager.getBorder("xmlpad.editor.focusBorder") != null)
			setBorder(null);
	}

	// Real time tree synchronisation
	private boolean realTimeStructureChanged = true;

	/**
	 * Notify in real time any tree structure change. This is needed for real
	 * time tree synchronization
	 */
	public void setEnabledRealTimeStructureChanged(boolean support) {
		this.realTimeStructureChanged = support;
		if ( getDocument() instanceof XMLPadDocument ) {
			((XMLPadDocument) getDocument()).enableStructureDamagedSupport(support);
		}
	}

	/**
	 * Notify in real time any tree structure change. This is needed for real
	 * time tree synchronization
	 */
	public boolean isEnabledRealTimeStructureChanged() {
		return realTimeStructureChanged;
	}

	/**
	 * Insert the closing tag while the user inserts the opening tag :
	 * <code>true</code> by default
	 */
	public void setAutoCloseTag(boolean autoClose) {
		((XMLPadDocument) getDocument()).setAutoCloseTag(autoClose);
	}

	/**
	 * Does the closing tag is inserted automatically :<code>true</code> by
	 * default
	 */
	public boolean isAutoCloseTag() {
		return ((XMLPadDocument) getDocument()).isAutoCloseTag();
	}

	boolean documentModified = false;

	/** Only for internal usage. User shouldn't call it or override it */
	public void resetDocumentState(boolean modified) {
		this.documentModified = modified;
	}

	/**
	 * @return <code>true</code> if the current document has been modified once
	 */
	public boolean isDocumentModified() {
		return documentModified;
	}

	/**
	 * Notify that the structure has changed. This method should only appear in
	 * particular case like load a new document ...
	 */
	public void notifyStructureChanged() {
		if ( getDocument() instanceof XMLPadDocument )
			((XMLPadDocument) getDocument()).structureDamaged();
	}

	// Called by the XMLDocument
	void notifyDocumentChanged() {
		if (!documentModified) {
			documentModified = true;
			getXMLContainer().notifyDocumentVersion(false);
		}
	}

	private EditorContext editorContext;

	public void initErrorProcessing() {
	}

	private int minErrorLineFound = -1;

	public void notifyError(Object context, boolean localError,
			String sourceLocation, int line, int col, int offset,
			String message, boolean onTheFly) {
		if (localError) {
			if (!onTheFly && minErrorLineFound == -1 && line > 0)
				minErrorLineFound = line;
			else if (onTheFly && SharedProperties.HIGHLIGHT_ERROR_ONTHEFLY) {
				highlightErrorLine(line);
				repaint();
			}
		}
	}

	public void notifyNoError(boolean onTheFly) {
		if (errorHighlightTag == null)
			return;
		getHighlighter().removeHighlight(errorHighlightTag);
		errorHighlightTag = null;
	}

	private boolean showFirstLineError = false;
	
	public void setEnabledFirstErrorProcessing( boolean value ) {
		showFirstLineError = value;
	}

	public void stopErrorProcessing() {
		/*
		if ( showFirstLineError ) {
			if (minErrorLineFound != -1) {
				setLineNumber( minErrorLineFound );
			}
			repaint();
			minErrorLineFound = -1;
		}
		*/
		
		repaint();
	}

	public void selectFirstError() {
		if (minErrorLineFound != -1) {
			setLineNumber( minErrorLineFound );
		}
		repaint();
		minErrorLineFound = -1;
	}
	
	public void errorSelected(String source, int line, String message) {
		highlightErrorLine(line, true);
	}

	/** Reset the default color of the editor */
	public void resetDefaultLook() {
		// The default editor kit for this text component
		LookManager.install(container, this);
		if (errorLineRenderer == null)
			errorLineRenderer = BasicLineRenderer.getSharedInstance();
		if (selectionLineRenderer == null)
			selectionLineRenderer = BasicLineRenderer.getSharedInstance();
		if (xpathLineRenderer == null)
			xpathLineRenderer = SimpleLineRenderer.getSharedInstance();
	}

	public EditorKit getEditorKit() {
		if ( EDITOR_KIT == null )
			EDITOR_KIT = new XMLEditorKit( this );
		return EDITOR_KIT;
	}
	
	private ViewPainterListener viewListener;
	
	public void setViewPainterListener( ViewPainterListener listener ) {
		this.viewListener = listener;
	}

	// ViewPainterListener
	public void paintElement( int x, int y ) {
		if ( this.viewListener != null ) {
			this.viewListener.paintElement( x, y );
		}
	}

	// ViewPainterListener	
	public void reset( int y ) {
		if ( this.viewListener != null ) {
			this.viewListener.reset( y );
		}		
	}

	private Map<Integer,Integer> closedElementStatus = null;
	private List<Point> closedElementRange = null;

	public boolean isClosedElement( int row ) {
		if ( closedElementStatus == null )
			return false;
		if ( closedElementStatus.containsKey( row ) )
			return true;
		return false;
	}

	/** For Closed Elements */
	public boolean isInvisibleRow( int row ) {
		if ( closedElementRange == null ) {
			return false;
		}
		for ( int i = 0; i < closedElementRange.size(); i++ ) {
			Point p = closedElementRange.get( i );
			if ( row > p.x && row <= p.y )
				return true;
		}
		return false;
	}

	public void setClosedElement( int row, boolean status ) {
		if ( closedElementStatus == null ) {
			closedElementStatus = new HashMap<Integer, Integer>();
		}
		if ( closedElementRange == null ) {
			closedElementRange = Collections.synchronizedList( new ArrayList<Point>() );
		}
		if ( status ) {	
			FPNode node = getXMLDocument().getGreatestNodeAt( row );			
			int endoffset = node.getStoppingOffset();
			int endLine = getDocument().getDefaultRootElement().getElementIndex( endoffset );
			closedElementStatus.put( row, endLine );
			closedElementRange.add( new Point( row, endLine ) );
		} else {
			closedElementStatus.remove( row );
			for ( Point p : closedElementRange ) {
				if ( p.x == row ) {
					closedElementRange.remove( p );
					break;
				}
			}
		}
	}

	public void checkClosedElement( int insertedRow, int rowNumber, boolean added ) {
		if ( closedElementStatus != null ) {
			Integer[] values = closedElementStatus.keySet().toArray( new Integer[] {} );
			for ( int v : values ) {
				int lastRow = closedElementStatus.get( v );
				if ( added ) {
					if ( v >= insertedRow ) {
						closedElementStatus.remove( v );

						for ( Point p : closedElementRange ) {
							if ( p.x == v ) {
								closedElementRange.remove( p );
							}
						}

						if ( v != insertedRow ) {
							closedElementStatus.put( 
								v + rowNumber,
								( lastRow + rowNumber )
							);
							closedElementRange.add( 
								new Point( 
									( v + rowNumber ), 
									( lastRow + rowNumber ) ) 
							);
						}
					}
				} else {
					if ( v >= insertedRow  ) {
						closedElementStatus.remove( v );
						
						for ( Point p : closedElementRange ) {
							if ( p.x == v ) {
								closedElementRange.remove( p );
							}
						}
						
						if ( v != insertedRow ) {
							closedElementStatus.put( v - rowNumber, ( lastRow - rowNumber ) );
							closedElementRange.add( 
								new Point( 
									( v - rowNumber ), 
									( lastRow - rowNumber ) ) 
							);							
						}
					}
				}
			}
		}	
	}

	public void setDisplaySpace( boolean displaySpace ) {
		if ( EDITOR_KIT instanceof XMLEditorKit ) {
			( ( XMLEditorKit )EDITOR_KIT ).setDisplaySpace( displaySpace );
			repaint();
		}
	}
	
	public boolean isDisplaySpace() {
		if ( EDITOR_KIT instanceof XMLEditorKit ) {
			return ( ( XMLEditorKit )EDITOR_KIT ).isDisplaySpace();
		} else
			return false;
	}
	
	// Initiate the keymap action
	public void initKeymap() {
		Keymap parent = getKeymap();
		map = addKeymap("XMLEditor", parent);
		setKeymap(map);
		map.setDefaultAction(new DefaultTextAction(map.getDefaultAction()));
	}

	// Add a new action to the current map
	public void setAction(KeyStroke key, Action a) {
		map.addActionForKeyStroke(key, a);
	}

	// ///////////////////// COLOR /////////////////////////////

	private Color colorTagUnderline = null;
	private Color colorEntity = null;
	private Color colorComment = null;
	private Color colorCommentStart = null;
	private Color colorDeclaration = null;
	private Color colorDeclarationStart = null;
	private Color colorDeclarationEnd = null;
	private Color colorDocType = null;
	private Color colorDocTypeStart = null;
	private Color colorDocTypeEnd = null;
	private Color colorLiteral = null;
	private Color colorTag = null;
	private Color colorInvalid = null;
	private Color colorText = null;
	private Color colorAttribute = null;
	private Color colorTagDelimiter = null;
	private Color colorTagBackground = null;
	private Color colorDeclarationBackground = null;
	private Color colorEntityBackground = null;
	private Color colorCommentBackground = null;
	private Color colorDocTypeBackground = null;
	private Color colorCDATABackground = null;
	private Color colorAttributeSeparator = null;
	private Color colorTagEnd = null;
	private Color colorNameSpace = null;
	private Color colorCDATA = null;
	private Color colorCDATAStart = null;
	private Color colorCDATAEnd = null;
	private Color colorTagBorderLine = null;
	private Color colorLineSelection = null;
	private Color colorLineError = null;
	private Color colorDTDElement = null;
	private Color colorDTDAttribute = null;
	private Color colorDTDEntity = null;
	private Color colorCurrentLine = null;

	private Color colorOpenCloseTip = null;
	private Color colorOpenCloseTipBackground = null;
	
	// ///////////////////// COLOR ///////////////////////////

	public void setColorOpenCloseTip( Color c ) {
		this.colorOpenCloseTip = c;
	}

	public Color getColorOpenCloseTip() {
		if ( this.colorOpenCloseTip == null )
			return Color.GRAY;
		return this.colorOpenCloseTip;
	}
	
	public void setColorOpenCloseTipBackground( Color c ) {
		this.colorOpenCloseTipBackground = c;
	}

	public Color getColorOpenCloseTipBackground() {
		return this.colorOpenCloseTipBackground;
	}
	
	public void setColorForCurrentLine( Color c ) {
		colorCurrentLine = c;
	}

	/**
	 * Choose a particular color for this attribute. Use a <code>null</code>
	 * color for removing it
	 */
	public void setColorForAttribute(String attribute, Color c) {
		if (container != null)
			container.getDocumentColorAccessibility().setColorForAttribute(
					attribute, c);
	}

	/** @return a custom color for this attribute */
	public Color getColorForAttribute(String attribute) {
		if (container != null) {
			return container.getDocumentColorAccessibility()
					.getColorForAttribute(attribute);
		} else
			return null;
	}

	/**
	 * @return <code>true</code> if a custom color is available for this
	 *         attribute
	 */
	public boolean hasColorForAttribute(String attribute) {
		if (container != null) {
			return container.getDocumentColorAccessibility()
					.hasColorForAttribute(attribute);
		} else
			return false;
	}

	/**
	 * Choose a particular color for this tag. Use a <code>null</code> color for
	 * removing it
	 */
	public void setColorForTag(String tag, Color c) {
		if (container != null)
			container.getDocumentColorAccessibility().setColorForTag(tag, c);
	}

	/** @return a custom color for this tag */
	public Color getColorForTag(String tag) {
		if (container != null) {
			return container.getDocumentColorAccessibility()
					.getColorForTag(tag);
		} else
			return null;
	}

	/** @return <code>true</code> if a custom color is available for this tag */
	public boolean hasColorForTag(String tag) {
		if (container != null)
			return container.getDocumentColorAccessibility()
					.hasColorForTag(tag);
		else
			return false;
	}

	/**
	 * Choose a particular color for this namespace prefix. Use a
	 * <code>null</code> color for removing it
	 */
	public void setColorForPrefix(String prefix, Color c) {
		if (container != null)
			container.getDocumentColorAccessibility().setColorForPrefix(prefix,
					c);
	}

	/** @return a custom color for this prefix */
	public Color getColorForPrefix(String prefix) {
		if (container != null) {
			return container.getDocumentColorAccessibility().getColorForPrefix(
					prefix);
		} else
			return null;
	}

	/** @return <code>true</code> if a custom color exist for this prefix */
	public boolean hasColorForPrefix(String prefix) {
		if (container != null) {
			return container.getDocumentColorAccessibility().hasColorForPrefix(
					prefix);
		} else
			return false;
	}

	/**
	 * Choose a particular background color for this namespace prefix. Use a
	 * <code>null</code> color for removing it
	 */
	public void setBackgroundColorForPrefix(String prefix, Color c) {
		if (container != null)
			container.getDocumentColorAccessibility()
					.setBackgroundColorForPrefix(prefix, c);
	}

	/** @return a custom background color for this prefix */
	public Color getBackgroundColorForPrefix(String prefix) {
		if (container != null) {
			return container.getDocumentColorAccessibility()
					.getBackgroundColorForPrefix(prefix);
		} else
			return null;
	}

	/**
	 * @return <code>true</code> if a custom background color exist for this
	 *         prefix
	 */
	public boolean hasBackgroundColorForPrefix(String prefix) {
		if (container != null) {
			return container.getDocumentColorAccessibility()
					.hasBackgroundColorForPrefix(prefix);
		} else
			return false;
	}

	/** Colorize a line selection */
	public void setColorForLineSelection(Color color) {
		this.colorLineSelection = color;
	}

	/** Colorize a line error */
	public void setColorForLineError(Color color) {
		this.colorLineError = color;
	}

	/** Colorize !ELEMENT DTD definition */
	public void setColorForDTDElement(Color color) {
		this.colorDTDElement = color;
	}

	/** @return ELEMENT DTD color */
	public Color getColorForDTDElement() {
		return colorDTDElement;
	}

	/** Colorize !ATTRIBUTE DTD definition */
	public void setColorForDTDAttribute(Color color) {
		this.colorDTDAttribute = color;
	}

	/** @return ATTRIBUTE DTD color */
	public Color getColorForDTDAttribute() {
		return colorDTDAttribute;
	}

	/** Colorize !ENTITY DTD definition */
	public void setColorForDTDEntity(Color color) {
		this.colorDTDEntity = color;
	}

	/** @return ENTITY DTD color */
	public Color getColorForDTDEntity() {
		return colorDTDEntity;
	}

	private Color colorDTDNotation;

	public void setColorForDTDNotation(Color color) {
		this.colorDTDNotation = color;
	}

	public Color getColorForDTDNotation() {
		return colorDTDNotation;
	}

	/** Colorize the border line for tag delimiter */
	public void setColorForTagBorderLine(Color color) {
		this.colorTagBorderLine = color;
	}

	/** @return the color for the border line */
	public Color getColorForTagBorderLine() {
		return colorTagBorderLine;
	}

	/** Colorize CDATA section */
	public void setColorForCDATA(Color color) {
		this.colorCDATA = color;
	}

	/** Color for &lt;![CDATA[[ */
	public void setColorForCDATAStart(Color color) {
		this.colorCDATAStart = color;
	}

	/** Color for &lt;![CDATA[[ */
	public Color getColorForCDATAStart() {
		return colorCDATAStart;
	}

	/** Color for ]]&gt; */
	public void setColorForCDATAEnd(Color color) {
		this.colorCDATAEnd = color;
	}

	/** Color for ]]&gt; */
	public Color getColorForCDATAEnd() {
		return colorCDATAEnd;
	}

	/** Colorize CDATA section background */
	public void setColorForCDATABackground(Color color) {
		this.colorCDATABackground = color;
	}

	/** Colorize underline for current selection */
	public void setColorForTagUnderline(Color color) {
		this.colorTagUnderline = color;
	}

	/** Colorize entity &ENTITY; */
	public void setColorForEntity(Color color) {
		this.colorEntity = color;
	}

	/** Colorize XML comment &lt;!-- ... --&gt; */
	public void setColorForComment(Color color) {
		this.colorComment = color;
	}

	/** Color for &lt;!-- */
	public void setColorForCommentStart(Color color) {
		this.colorCommentStart = color;
	}

	/** Color for &lt;!-- */
	public Color getColorForCommentStart() {
		return colorCommentStart;
	}

	/** Color for --!&gt; */
	public void setColorForCommentEnd(Color color) {
		this.colorCommentStart = color;
	}

	/** Color for --!&gt; */
	public Color getColorForCommentEnd() {
		return colorCommentStart;
	}

	/** Color for &lt;? */
	public void setColorForDeclarationStart(Color color) {
		this.colorDeclarationStart = color;
	}

	/** Color for &lt;? */

	public Color getColorForDeclarationStart() {
		return colorDeclarationStart;
	}

	/** Color for ?&gt; */
	public void setColorForDeclarationEnd(Color color) {
		this.colorDeclarationEnd = color;
	}

	/** Color for ?&gt; */
	public Color getColorForDeclarationEnd() {
		return colorDeclarationEnd;
	}

	/** Colorize XML declaration &lt;? ... ?&gt; */
	public void setColorForDeclaration(Color color) {
		this.colorDeclaration = color;
	}

	/** Colorize DocType declaration &lt;!DOCTYPE ... &gt; */
	public void setColorForDocType(Color color) {
		this.colorDocType = color;
	}

	/** Colorize &lt;!DOCTYPE */
	public void setColorForDocTypeStart(Color color) {
		this.colorDocTypeStart = color;
	}

	/** Colorize &lt;!DOCTYPE */
	public Color getColorForDocTypeStart() {
		return colorDocTypeStart;
	}

	/** Colorize &gt; */
	public void setColorForDocTypeEnd(Color color) {
		this.colorDocTypeEnd = color;
	}

	/** Colorize &&gt; */
	public Color getColorForDocTypeEnd() {
		return colorDocTypeEnd;
	}

	/** Colorize DocType declaration background &lt;!DOCTYPE ... &gt; */
	public void setColorForDocTypeBackground(Color color) {
		this.colorDocTypeBackground = color;
	}

	/** Colorize literal "..." */
	public void setColorForLiteral(Color color) {
		this.colorLiteral = color;
	}

	/** Colorize tag &lt;Tag... */
	public void setColorForTag(Color color) {
		this.colorTag = color;
	}

	/** Colorize error */
	public void setColorForInvalid(Color color) {
		this.colorInvalid = color;
	}

	/** Colorize text */
	public void setColorForText(Color color) {
		this.colorText = color;
	}

	/** Colorize attribute &lt;tag ATTRIBUTE="..." ... */
	public void setColorForAttribute(Color color) {
		this.colorAttribute = color;
	}

	/** Colorize tag delimiter &lt; or &gt; */
	public void setColorForTagDelimiter(Color color) {
		this.colorTagDelimiter = color;
	}

	/** Colorize a tag background */
	public void setColorForTagBackground(Color color) {
		this.colorTagBackground = color;
	}

	/** Colorize a declaration background */
	public void setColorForDeclarationBackground(Color color) {
		this.colorDeclarationBackground = color;
	}

	/** Colorize an entity background */
	public void setColorForEntityBackground(Color color) {
		this.colorEntityBackground = color;
	}

	/** Colorize a comment background */
	public void setColorForCommentBackground(Color color) {
		this.colorCommentBackground = color;
	}

	/** Colorize '=' */
	public void setColorForAttributeSeparator(Color color) {
		this.colorAttributeSeparator = color;
	}

	/** Colorize '/' */
	public void setColorForTagEnd(Color color) {
		this.colorTagEnd = color;
	}

	/** Colorize NameSpace */
	public void setColorForNameSpace(Color color) {
		this.colorNameSpace = color;
	}

	/** Color for a line error */
	public Color getColorForLineError() {
		return colorLineError;
	}

	/** Color for a line selection */
	public Color getColorForLineSelection() {
		return colorLineSelection;
	}

	/** Color for CDATA section */
	public Color getColorForCDATA() {
		return colorCDATA;
	}

	/** Colofr for CDATA Background */
	public Color getColorForCDATABackground() {
		return colorCDATABackground;
	}

	/** Color for tag underline */
	public Color getColorForTagUnderline() {
		return colorTagUnderline;
	}

	/** Colorize NameSpace */
	public Color getColorForNameSpace() {
		return colorNameSpace;
	}

	/** Colorize entity &ENTITY; */
	public Color getColorForEntity() {
		return colorEntity;
	}

	/** Colorize XML comment &lt;!-- ... --&gt; */
	public Color getColorForComment() {
		return this.colorComment;
	}

	/** Colorize XML declaration &lt;? ... ?&gt; */
	public Color getColorForDeclaration() {
		return this.colorDeclaration;
	}

	/** Colorize DocType declaration &lt;!DOCTYPE ... &gt; */
	public Color getColorForDocType() {
		return this.colorDocType;
	}

	/** Colorize DocType declaration &lt;!DOCTYPE ... &gt; */
	public Color getColorForDocTypeBackground() {
		return this.colorDocTypeBackground;
	}

	/** Colorize literal "..." */
	public Color getColorForLiteral() {
		return this.colorLiteral;
	}

	/** Colorize tag &lt;Tag... */
	public Color getColorForTag() {
		return this.colorTag;
	}

	/** Colorize error */
	public Color getColorForInvalid() {
		return this.colorInvalid;
	}

	/** Colorize text */
	public Color getColorForText() {
		return this.colorText;
	}

	/** Colorize attribute &lt;tag ATTRIBUTE="..." ... */
	public Color getColorForAttribute() {
		return this.colorAttribute;
	}

	/** Colorize tag delimiter &lt; or &gt; */
	public Color getColorForTagDelimiter() {
		return this.colorTagDelimiter;
	}

	/** @return the color for the tag background */
	public Color getColorForTagBackground() {
		return this.colorTagBackground;
	}

	/** @return the color for a tag backgrond */
	public Color getColorForDeclarationBackground() {
		return this.colorDeclarationBackground;
	}

	/** @return the color for an entity background */
	public Color getColorForEntityBackground() {
		return this.colorEntityBackground;
	}

	/** @return the color for a comment bakground */
	public Color getColorCommentBackground() {
		return this.colorCommentBackground;
	}

	/** Colorize '=' */
	public Color getColorForAttributeSeparator() {
		return this.colorAttributeSeparator;
	}

	/** Colorize '/' */
	public Color getColorForTagEnd() {
		return this.colorTagEnd;
	}

	// //////////////////////////

	/** Reset the undo manager */
	public void setUndoManager(UndoManager undo) {
		this.um = undo;
	}

	/** @return the current undo manager */
	public UndoManager getUndoManager() {
		return um;
	}

	/** Undo last action */
	public void undo() {
		try {
			um.undo();
		} catch (CannotUndoException exc) {
			ActionModel.setEnabledAction(ActionModel.UNDO_ACTION, false);
		}
	}

	/** Redo last action */
	public void redo() {
		try {
			um.redo();
		} catch (CannotRedoException exc) {
			ActionModel.setEnabledAction(ActionModel.REDO_ACTION, false);
		}
	}

	private XMLEntityResolver resolver = new XMLEntityResolver();

	/**
	 * Update the current entity resolver, it is useful for traducing char to
	 * entity while reading and the contrary when getting the current text
	 */
	public void setEntityResolver(XMLEntityResolver resolver) {
		this.resolver = resolver;
	}

	/** @return the current entity resolver */
	public XMLEntityResolver getEntityResolver() {
		return resolver;
	}

	private String resolveText(String text) {
		if (text == null)
			return text;
		if (resolver == null)
			return text;

		StringBuffer sb = new StringBuffer();
		char[] ch = text.toCharArray();
		String tmp = null;
		boolean isText = true;
		boolean armTag = false;

		for (int i = 0; i < ch.length; i++) {
			if (ch[i] == '<') {
				// In tag
				if (!armTag) {

					armTag = true;
				} else {
					// Need a resolution
				}
			}
		}
		return text;
	}

	/**
	 * <b>Note : You must use setText on the XMLContainer rather than on this
	 * class </b> Load the text from the inputstream. The stream is closed after
	 * reading
	 */
	public void setText(java.io.InputStream input) throws java.io.IOException {
		StringBuffer sb = new StringBuffer();
		int c;
		while ((c = input.read()) != -1) {
			sb.append((char) c);
		}
		input.close();
		setText(sb.toString());
	}

	/**
	 * <b>Note : You must use setText on the XMLContainer rather than on this
	 * class </b>
	 */
	public void setText(String newText) {
		try {
			if (um != null)
				um.discardAllEdits();
			else
				um = container.createUndoManager();
			getDocument().remove(0, document.getLength());
			getDocument().insertString(0, newText, null);
		} catch (BadLocationException ex) {
		}
		lastStructureLocation = null;
	}

	/** @return a text starting from */
	public String getText(int from) {
		try {
			return document.getText(from, document.getLength() - from);
		} catch (BadLocationException ex) {
		}
		return null;
	}

	/** @return the current text */
	public String getText() {
		return getText(0);
	}

	/** reset the caret to the current line number */
	public void setLineNumber(int num) {
		try {
			Element root = document.getDefaultRootElement();
			Element line = root.getElement(num - 1);
			setCaretPosition(line.getStartOffset());
		} catch (Exception ex) {
		}
	}

	/** Insert a string at the current caret location */
	public void insertText(String text) {
		try {
			int loc = 0;
			loc = getCaretPosition();
			document.insertString(loc, text, null);
		} catch (BadLocationException ex) {
		}
	}

	/**
	 * @return the current editor kit
	 */
	public EditorKit createDefaultEditorKit() {
		return EDITOR_KIT;
	}

	/** Remove the highlight of the current line */
	public void unhighlightLine() {
		if (selectionHighlight != null)
			getHighlighter().removeHighlight(selectionHighlight);
		repaint();
		selectionHighlight = null;
	}

	/** @return the start of the line for the caret position */
	public int getLineStart(int caret) {
		try {
			Document doc = getDocument();
			Element map = doc.getDefaultRootElement();
			int line = map.getElementIndex(caret);
			Element lineElement = map.getElement(line);
			int start = lineElement.getStartOffset();
			return start;
		} catch (Throwable th) {
			th.printStackTrace();
			return -1;
		}
	}

	/** @return the end of the line for the caret position */
	public int getLineEnd(int caret) {
		try {
			Document doc = getDocument();
			Element map = doc.getDefaultRootElement();
			int line = map.getElementIndex(caret);
			Element lineElement = map.getElement(line);
			int end = lineElement.getEndOffset();
			return end;
		} catch (Throwable th) {
			return -1;
		}
	}

	/** Highlight the line with an invokeLater */
	public void asynchronousHighlightLine(int line) {
		final int l = line;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				highlightLine(l);
			}
		});
	}

	/** Highlight the line with the "selectionColor" property */
	public void highlightLine(int line) {
		
		if ( selectionHighlight != null ) {
			unhighlightLine();
		}
		
		// Error line
		try {
			if (selectionHighlight == null)
				selectionHighlight = getHighlighter().addHighlight(0, 0,
						new SelectionLineHighlighter());
		} catch (BadLocationException ex) {
			return;
		}

		try {
			Document doc = getDocument();
			Element map = doc.getDefaultRootElement();
			Element lineElement = map.getElement(Math.max(0, line - 1));
			if (lineElement == null) {
				System.err.println("Can't find element : " + (line - 1) + "?");
				return;
			}
			int start = lineElement.getStartOffset();
			int end = lineElement.getEndOffset();
			getHighlighter().changeHighlight(selectionHighlight, start, end);
			Rectangle v = modelToView(start);
			if ( v == null )	// No visible !
				return;
			v.height = getVisibleRect().height;
			v.y -= v.height / 2;
			scrollRectToVisible(v);
		} catch (BadLocationException bl) {
		}

		// Due to unknown refresh error
		repaint();
	}

	/** Remove error line */
	public void removeHighlightedErrorLine() {
		if (errorHighlightTag != null)
			getHighlighter().removeHighlight(errorHighlightTag);
		errorHighlightTag = null;
	}

	/** Remove all highlighted lines */
	public void removeHighlightedLines() {
		getHighlighter().removeAllHighlights();
	}

	/** Highlight the following list of node : SimpleNode */
	public void highlightNodes(List list) {
		getHighlighter().removeAllHighlights();
		XPathLineHighlighter highlighter = new XPathLineHighlighter();

		for (int i = 0; i < list.size(); i++) {
			FPNode node = (FPNode) list.get(i);
			int line = node.getStartingLine();
			if (line >= 1) {
				Element map = getDocument().getDefaultRootElement();
				Element lineElement = map.getElement(line - 1);
				int start = lineElement.getStartOffset();
				int stop = lineElement.getEndOffset();
				try {
					getHighlighter().addHighlight(start, stop, highlighter);
				} catch (BadLocationException th) {

				}
			}
		}
	}

	public void highlightNode(FPNode node) {
		int line = node.getStartingLine();
		if (line >= 1) {
			highlightNode(line);
		}
	}

	public void highlightNode(int line) {
		getHighlighter().removeAllHighlights();
		XPathLineHighlighter highlighter = new XPathLineHighlighter();
		Element map = getDocument().getDefaultRootElement();
		Element lineElement = map.getElement(line - 1);
		if (lineElement != null) {
			int start = lineElement.getStartOffset();
			int stop = lineElement.getEndOffset();
			try {
				getHighlighter().addHighlight(start, stop, highlighter);
			} catch (BadLocationException th) {
			}
		}
	}
	
	public void setCaretForLine( int line ) {
		Element map = getDocument().getDefaultRootElement();
		Element lineElement = map.getElement(line);
		if (lineElement != null) {
			int start = lineElement.getStartOffset();
			setCaretPosition( start );
		}		
	}
	
	private Object expressionHighlighter = null;

	public void highlightExpression( int offset ) {
		try {
			removeHighlightExpression();
			ExpressionHighlighter highlighter = new ExpressionHighlighter();
			expressionHighlighter = getHighlighter().addHighlight(
					offset, offset + 1, 
					highlighter);
		} catch ( BadLocationException th ) {
		}
	}

	public void removeHighlightExpression() {
		if ( expressionHighlighter != null ) {
			getHighlighter().removeHighlight( 
				expressionHighlighter );
			expressionHighlighter = null;
		}
	}

	/** Show the error line without auto scrolling */
	public void highlightErrorLine(int line) {
		highlightErrorLine(line, false);
	}
	
	/**
	 * Show the error line
	 * 
	 * @param lineStart
	 *            error line
	 * @param autoScroll
	 *            if <code>true</code> it will scroll to the good line
	 */
	public void highlightErrorLine(int lineStart, boolean autoScroll) {

		if (lineStart > 0) {

			// Error line
			/*
			 * try { if (errorHighlightTag == null) { try {
			 * getDocument().notify(); } catch (Throwable th) { }
			 * errorHighlightTag = getHighlighter().addHighlight(0, 0, new
			 * ErrorLineHighlighter()); } } catch (BadLocationException ex) { }
			 */

			try {
				Document doc = getDocument();
				Element map = doc.getDefaultRootElement();
				if (lineStart - 1 < 0)
					lineStart = 1;
				Element lineElement = map.getElement(lineStart - 1);
				if (lineElement == null) {
					System.err.println("Can't find element : "
							+ (lineStart - 1) + "?");
					return;
				}

				int start = lineElement.getStartOffset();
				int end = lineElement.getEndOffset();

				// Remove the previous one
				if (errorHighlightTag != null) {
					getHighlighter().removeHighlight(errorHighlightTag);
				}

				// Doesn't work with the JDK 6 ??
				// getHighlighter().changeHighlight(errorHighlightTag, start,
				// end);

				errorHighlightTag = getHighlighter().addHighlight(start, end,
						new ErrorLineHighlighter());

				if (autoScroll) {
					Rectangle v = modelToView(start);
					v.height = getVisibleRect().height;
					v.y -= v.height / 2;
					scrollRectToVisible(v);
				}

				repaint();

			} catch (BadLocationException bl) {
			}

		}
	}

	private boolean xmllocation = true;

	/**
	 * Enabled the current tree location for a caret change. This feature can
	 * reduce the velocity. So it should be disabled for heavy XML document
	 */
	public void setEnabledTreeLocationForCaret(boolean rt) {
		this.xmllocation = rt;
		if (!rt)
			if (editorContext != null)
				editorContext.notifyLocation(null);
	}

	/**
	 * Is Enabled the current tree location for a caret change ?. True by
	 * default
	 */
	public boolean isEnabledTreeLocationForCaret() {
		return xmllocation;
	}

	protected void updateSelectionState() {
		boolean hasSelection = getSelectionStart() < getSelectionEnd()
				&& getSelectionStart() >= 0;

		ActionModel.activeActionForSelection(hasSelection);
	}

	FPNode lastStructureLocation;

	private boolean enabledXPathLocation = true;

	/**
	 * Enabled XPath location each time the cursor change. By default
	 * <code>true</code>
	 */
	public void setEnabledXPathLocation(boolean enabled) {
		this.enabledXPathLocation = enabled;
	}

	/**
	 * @return <code>true</code> if the xpath location is available on caret
	 *         changes
	 */
	public boolean isEnabledXPathLocation() {
		return enabledXPathLocation;
	}

	/**
	 * Notify to listener the current document location from the caret position.
	 * Note that this method shouldn't be called because this is done for caret
	 * changes and this is asynchronous. For instant action call rather
	 * <code>reforceCurrentLocation</code>
	 */
	public void notifyCurrentLocation() {
		if (getXMLContainer().getTreeListeners() == null
				|| !enabledXPathLocation
				|| getXMLContainer().getErrorManager().hasLastError())
			return;

		if (editorContext != null
				&& !getXMLContainer().getTreeListeners().isLocationLocked()) {
			// JobManager.addJob(LOCATION_JOB);
			// Now real time
			if (LOCATION_JOB.preRun())
				LOCATION_JOB.run();
		}
	}

	public void notifyCaretLocation() {
		int caret = getCaretPosition();
		Element e = getDocument().getDefaultRootElement();
		int line = e.getElementIndex(caret);
		int col = caret - e.getElement(line).getStartOffset();
		editorContext.notifyCaretLocation(col + 1, line + 1);
	}

	/**
	 * Synchronous action for refreshing the current document location from the
	 * caret position
	 * 
	 * @param waitTreeSynchro
	 *            if <code>true</code>, the current thread will wait for a good
	 *            text/tree synchro
	 */
	public void synchronousCurrentLocation(boolean waitTreeSynchro) {
		if (getXMLContainer() == null)
			return;
		if (getXMLContainer().getTreeListeners() == null)
			return;

		if (getXMLContainer().hasErrorMessage()) {
			waitTreeSynchro = false;
		}

		if (waitTreeSynchro
				&& getXMLContainer().getTreeListeners().isLocationLocked()) {
			// JobManager.COMMON_MANAGER.waitForNoJob();
		}
		if (editorContext != null
				&& !getXMLContainer().getTreeListeners().isLocationLocked()) {
			if (!getXMLContainer().getTreeListeners().isLocationLocked()) {
				FPNode node = getXMLDocument().getXMLPath(getCaretPosition());
				lastStructureLocation = node;
				if (editorContext != null) {
					editorContext.notifyLocation(node);
				}
				// Avoid bad refresh for underline
				XMLEditor.this.repaint();
			}
		}
	}
	
	private boolean enableHighlightCurrentLine = true;
	
	public void setEnableHighlightCurrentLine( boolean enableHighlightCurrentLine ) {
		this.enableHighlightCurrentLine = enableHighlightCurrentLine;
	}
	
	/** Structure computing */
	public void caretUpdate(CaretEvent e) {
		notifyCaretLocation();
		
		if ( enableHighlightCurrentLine )
			highlightCurrentLine();
		
		if (disableCaretListeners || delayedDisableCaretListeners) {
			delayedDisableCaretListeners = false;
			return;
		}
		updateSelectionState();
		if (xmllocation && enabledXPathLocation) {
			notifyCurrentLocation();
		}
		ExpressionHighlighterManager.highlight( this );
	}
	
	private Object currentLineHighlightTag = null;
	private CurrentLineHightlighter currentLineHighlight = null;
	
	private void highlightCurrentLine() {
		
		int location = 
			getCaretPosition();
		int index = 
			getDocument().getDefaultRootElement().getElementIndex( location );
		Element e = 
			getDocument().getDefaultRootElement().getElement( index );

		if ( currentLineHighlightTag != null )
			getHighlighter().removeHighlight( currentLineHighlightTag );

		if ( currentLineHighlight == null )
			currentLineHighlight = 
				new CurrentLineHightlighter();

		try {
			currentLineHighlightTag = getHighlighter().addHighlight(
					e.getStartOffset(),
					e.getEndOffset(),
					currentLineHighlight
			);
		} catch (BadLocationException e1) {
		}		

	}

	private boolean delayedDisableCaretListeners = false;

	/**
	 * Reset the caret location without updating the current tree location
	 * 
	 * @return <code>true</code> when the operation is correct
	 */
	public boolean setCaretPositionWithoutNotification(int caret) {
		delayedDisableCaretListeners = true;
		try {
			setCaretPosition(caret);
		} catch (IllegalArgumentException exc) {
			return false;
		}
		return true;
	}

	public void mouseDragged( MouseEvent e ) {}

	private boolean infoTooltip = true;
	
	public void setInfoToolTip( boolean infoTooltip ) {
		this.infoTooltip = infoTooltip; 
	}

	private int tooltipDelay = 0;
	
	public void setSpeedToolTip( String text ) {
		if ( tooltipDelay == 0 ) {
			tooltipDelay = ToolTipManager.sharedInstance().getInitialDelay();
		}
		if ( text == null ) {
			setToolTipText( null );			
			ToolTipManager.sharedInstance().setInitialDelay( tooltipDelay );
		} else {
			ToolTipManager.sharedInstance().setInitialDelay( 10 );
			setToolTipText( text );
		}
	}

	public void keyPressed(KeyEvent e) {
		if ( getXMLContainer() != null ) {
			getXMLContainer().editorKeyPressed( e );
		}
	}

	public void keyReleased(KeyEvent e) {
		if ( getXMLContainer() != null ) {
			getXMLContainer().editorKeyReleased( e );
		}
	}

	public void keyTyped(KeyEvent e) {
	}
	
	public void mouseClicked(MouseEvent e) {
		if ( getXMLContainer() != null )
			getXMLContainer().editorMouseClicked(e);
	}
	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {
	}
	public void mousePressed(MouseEvent e) {
	}
	public void mouseReleased(MouseEvent e) {
	}

	public void mouseMoved( MouseEvent e ) {
		
		if ( getXMLContainer() != null ) {
			getXMLContainer().editorMouseMoved( e );
		}

		if ( infoTooltip ) {
			setSpeedToolTip( null );
			if ( e.isControlDown() ) {			
				int offset = viewToModel( e.getPoint() );			
				try {
					FPNode node = 
						getXMLDocument().getXMLPath( offset );
					if ( node != null ) {
						setSpeedToolTip( 
							node.getXPathLocation() 
						);
					} else
						setSpeedToolTip( null );
				} catch( RuntimeException re ) {
					setToolTipText( null );
				}
			}
			if ( e.isAltDown() ) {
				try {
					int offset = viewToModel( e.getPoint() );
					FPNode node = 
						getXMLDocument().getXMLPath( offset );
					if ( node != null ) {
						if ( node.isText() )
							setToolTipText( node.getContent() );
						else {
							StringBuffer sb = 
								new StringBuffer( "<html><body>" );
							if ( node.getViewAttributeCount() > 0 ) {
								for ( int i = 0; i <
									node.getViewAttributeCount(); i++ ) {
									String name = node.getViewAttributeAt( i );
									sb.append( "<b>" );
									sb.append( name );
									sb.append( "</b> : <i>" );
									sb.append( node.getAttribute( name ) );
									sb.append( "</i>" );
									sb.append( "<br>" );
									
								}
							} else
								sb.append( "No attributes" );
							sb.append( "</body></html>" );
							setSpeedToolTip( sb.toString() );
						}
					}
					else
						setSpeedToolTip( null );
				} catch( RuntimeException re ) {
					setSpeedToolTip( null );
				}			
			}
		}
	}

	////////////////////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////
	// /// Inner classes
	////////////////////////////////////////////////////////////////////////////
	// ///////////////////////
	////////////////////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////

	class DefaultTextAction extends TextAction implements ActionListener {
		public DefaultTextAction(Action ta) {
			super("default");
			this.ta = (TextAction) ta;
		}

		public void actionPerformed(ActionEvent evt) {
			ta.actionPerformed(evt);
		}

		private TextAction ta;
	}

	// Undo / Redo
	class EditorUndoableListener implements UndoableEditListener {
		public void undoableEditHappened(UndoableEditEvent evt) {
			um.addEdit(evt.getEdit());
		}
	}

	private LineRenderer errorLineRenderer;

	/**
	 * Reset the renderer for error line. This invokation should be done inside
	 * the Look step
	 */
	public void setErrorLineRenderer(LineRenderer renderer) {
		this.errorLineRenderer = renderer;
	}

	private LineRenderer xpathLineRenderer;

	public void setXPathLineRenderer(LineRenderer renderer) {
		this.xpathLineRenderer = renderer;
	}

	private Color highlightExpressionColor = Color.LIGHT_GRAY;
	
	public void setHighlightExpressionColor( Color c ) {
		highlightExpressionColor = c;
	}
	
	private LineRenderer currentLineRenderer;
	
	public void setCurrentLineRenderer( LineRenderer renderer ) {
		this.currentLineRenderer = renderer;
	}

	class ExpressionHighlighter implements Highlighter.HighlightPainter {
		
		public void paint(Graphics g, int p0, int p1, Shape bounds,
				JTextComponent c) {
			try {
				Rectangle r = 
					modelToView( p0 );
				Rectangle r2 = 
					modelToView( p1 );
				g.setColor( highlightExpressionColor );
				g.fillRect( r.x, r.y, r2.x - r.x, r.height );
			} catch (BadLocationException e) {

			}

		}

	}

	class XPathLineHighlighter implements Highlighter.HighlightPainter {
		public void paint(Graphics g, int p0, int p1, Shape bounds,
				JTextComponent textComponent) {

			FontMetrics metrics = g.getFontMetrics();
			Document doc = getDocument();
			int lineNo = doc.getDefaultRootElement().getElementIndex(p0);

			Rectangle rect = (Rectangle) bounds;
			int height = metrics.getHeight();
			int x = rect.x - 1;
			int y = (rect.y + height * lineNo) - 1;
			int width = rect.width + 2;

			Point p = getTextBeginEnd(lineNo);
			if (p != null) {
				try {
					Rectangle rx = modelToView(p.x);
					Rectangle ry = modelToView(p.y - 2);
					x = rx.x;
					width = ry.x - rx.x;
				} catch (BadLocationException exc) {
				}
			}
			xpathLineRenderer.renderer(g, xpathHighlightColor, x, y, width,
					height);
		}
	}
	
	class CurrentLineHightlighter implements Highlighter.HighlightPainter {

		public void paint(
				Graphics g, 
				int p0, 
				int p1, 
				Shape bounds,
				JTextComponent textComponent ) {

			FontMetrics metrics = g.getFontMetrics();
			Document doc = getDocument();
			int lineNo = doc.getDefaultRootElement().getElementIndex(
					p0
			);

			Rectangle rect = (Rectangle) bounds;
			int height = metrics.getHeight();
			int y = rect.y + height * lineNo;
			int x = 0;
			int width = getWidth();

			if ( currentLineRenderer == null ) {
				currentLineRenderer = 
					PlainLineRenderer.getSharedInstance();
			}
			
			currentLineRenderer.renderer(
					g, 
					colorCurrentLine, 
					x, 
					y, 
					width,
					height
			);
		}
		
	}

	class ErrorLineHighlighter implements Highlighter.HighlightPainter {
		public void paint(Graphics g, int p0, int p1, Shape bounds,
				JTextComponent textComponent) {

			FontMetrics metrics = g.getFontMetrics();
			Document doc = getDocument();
			int lineNo = doc.getDefaultRootElement().getElementIndex(p0);

			Rectangle rect = (Rectangle) bounds;
			int height = metrics.getHeight();
			int x = rect.x;
			int y = rect.y + height * lineNo;
			int width = rect.width;
			Point p = getTextBeginEnd(lineNo);
			if (p != null) {
				try {
					Rectangle rx = modelToView(p.x);
					Rectangle ry = modelToView(p.y);
					x = rx.x;
					width = ry.x - rx.x;
				} catch (BadLocationException exc) {
				}
			}

			errorLineRenderer.renderer(g, errorHighlightColor, x, y, width,
					height);
		}
	}

	private LineRenderer selectionLineRenderer;

	/**
	 * Reset the renderer for error line. This invokation should be done inside
	 * the Look step
	 */
	public void setSelectionLineRenderer(LineRenderer renderer) {
		this.selectionLineRenderer = renderer;
	}

	// @return begin end of text eliminating white space
	private Point getTextBeginEnd(int line) {
		Document doc = getDocument();
		Element e = doc.getDefaultRootElement().getElement(line);
		int start = e.getStartOffset();
		int stop = e.getEndOffset();
		try {
			String txt = doc.getText(start, stop - start + 1);
			for (int i = 0; i < txt.length(); i++) {
				if (Character.isWhitespace(txt.charAt(i)))
					continue;
				else {
					int j;
					for (j = txt.length() - 1; j > i; j--) {
						if (!Character.isWhitespace(txt.charAt(j))) {
							int i1 = start + i;
							int i2 = start + j;
							if (i2 - i1 < 2)
								return null;
							return new Point(i1, i2 + 1);
						}
					}
				}
			}
		} catch (BadLocationException exc) {
			return null;
		}
		return null;
	}

	// Show current node

	class SelectionLineHighlighter implements Highlighter.HighlightPainter {
		public void paint(Graphics g, int p0, int p1, Shape bounds,
				JTextComponent textComponent) {

			FontMetrics metrics = g.getFontMetrics();
			Document doc = getDocument();
			int lineNo = doc.getDefaultRootElement().getElementIndex(p0);
			Point p = getTextBeginEnd(lineNo);

			Rectangle rect = (Rectangle) bounds;
			int height = metrics.getHeight();
			int x = rect.x;
			int y = rect.y + height * lineNo;
			int width = rect.width;

			if (p != null) {
				try {
					Rectangle rx = modelToView(p.x);
					Rectangle ry = modelToView(p.y);

					if (ry.y != rx.y) {
						// ??
						if (p.y - 2 > p.x)
							ry = modelToView(p.y - 2);
					}

					x = rx.x;

					width = ry.x - rx.x;
				} catch (BadLocationException exc) {
				}
			}

			selectionLineRenderer.renderer(g, selectionHighlightColor, x, y,
					width, height);
		}
	}

	class PopupMouseAdapter extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}

		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				if (getXMLContainer() != null) {
					getXMLContainer().showPopup(XMLEditor.this, e.getX(),
							e.getY());
				}
			}
		}
	}

	// //////////////////////////////// Key Action

	class SelectParentNodeAction extends AbstractAction {
	
		public void actionPerformed( ActionEvent e ) {		
		
			FPNode currentOne = getCurrentNodeLocation();
			if ( currentOne != null ) {
				
				// Don't get a text node				
				if ( currentOne.isText() ) {
					currentOne = currentOne.getFPParent();
					if ( currentOne == null )
						return;
				}
	
				FPNode parent = currentOne.getFPParent();
				if ( parent != null ) {								
					highlightNode(parent);
					int delta = 1;
					if (parent.isText())
						delta = 0;
					setCaretPosition(parent.getStartingOffset() + delta);
					repaint();				
				}
				
			}
		
		}
		
	}

	class SelectFirstChildNodeAction extends AbstractAction {
		
		public void actionPerformed( ActionEvent e ) {		
		
			FPNode currentOne = getCurrentNodeLocation();
			if ( currentOne != null ) {
				
				// Don't get a text node				
				if ( currentOne.isText() ) {
					currentOne = currentOne.getFPParent();
					if ( currentOne == null )
						return;
				}
	
				if ( currentOne.childCount() > 0 ) {

					FPNode child = currentOne.childAt( 0 );
					if ( child != null ) {								
						highlightNode(child);
						int delta = 1;
						if (child.isText())
							delta = 0;
						setCaretPosition(child.getStartingOffset() + delta);
						repaint();				
					}
				
				}
				
			}
		
		}
		
	}
		
	class SelectPreviousChildNodeAction extends AbstractAction {

		public void actionPerformed( ActionEvent e ) {

			FPNode currentOne = getCurrentNodeLocation();
			if ( currentOne != null ) {
				
				// Don't get a text node				
				if ( currentOne.isText() ) {
					currentOne = currentOne.getFPParent();
					if ( currentOne == null )
						return;
				}

				FPNode parent = currentOne.getFPParent();
				if ( parent != null ) {
					int previous = -1;
					for ( int i = 0; i < parent.childCount(); i++ ) {
						if ( parent.childAt( i ) == currentOne ) {
							previous = i - 1;
							break;
						}
					}
					if ( previous > -1 ) {

						FPNode previousNode = parent.childAt( previous );
						
						highlightNode(previousNode);

						int delta = 1;
						if (previousNode.isText())
							delta = 0;

						setCaretPosition(previousNode.getStartingOffset() + delta);
						repaint();
						
					}
				}
			}			
			
		}

	}

	class SelectNextChildNodeAction extends AbstractAction {
		
		public void actionPerformed(ActionEvent e) {

			FPNode currentOne = getCurrentNodeLocation();
			if ( currentOne != null ) {

				// Don't get a text node
				if ( currentOne.isText() ) {
					currentOne = currentOne.getFPParent();
					if ( currentOne == null )
						return;
				}
				
				FPNode parent = currentOne.getFPParent();
				if ( parent != null ) {
					int next = -1;
					for ( int i = 0; i < parent.childCount(); i++ ) {
						if ( parent.childAt( i ) == currentOne ) {
							next = i + 1;
							break;
						}
					}
					if ( next < parent.childCount() && next > -1 ) {

						FPNode nextNode = parent.childAt( next );
						
						highlightNode(nextNode);

						int delta = 1;
						if (nextNode.isText())
							delta = 0;

						setCaretPosition(nextNode.getStartingOffset() + delta);
						repaint();
						
					}
				}
			}			
			
		}

	}

	/** Highlight the next node */
	class SelectNodeUpAction extends AbstractAction {

		public void actionPerformed(ActionEvent e) {

			FPNode currentOne = getCurrentNodeLocation();
			if (currentOne != null) {

				if (currentOne.isText())
					currentOne.getFPParent();

				FastVector v = currentOne.getDocument().getFlatNodes();
				int i = v.indexOf(currentOne);
				if (i > 0) {

					FPNode previousNode = (FPNode) v.get(i - 1);
					highlightNode(previousNode);

					int delta = 1;
					if (previousNode.isText())
						delta = 0;

					setCaretPosition(previousNode.getStartingOffset() + delta);
					repaint();

				}

			}

		}

	}

	/** Highlight the previous node */
	class SelectNodeDownAction extends AbstractAction {

		public void actionPerformed(ActionEvent e) {

			FPNode currentOne = getCurrentNodeLocation();
			if (currentOne != null) {

				FastVector v = currentOne.getDocument().getFlatNodes();
				// v.dump();

				int i = v.indexOf(currentOne);
				if (i < (v.size() - 1)) {

					FPNode nextNode = (FPNode) v.get(i + 1);

					highlightNode(nextNode);

					int delta = 1;
					if (nextNode.isText())
						delta = 0;

					setCaretPosition(nextNode.getStartingOffset() + delta);
					repaint();

				}

			}

		}

	}

	// Invoke the content assistant
	class ContentAssistantAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {

			getXMLDocument().manageCompletion(true, getCaretPosition(), null);

			/*
			 * 
			 * if (getXMLDocument().isInsideTag(getCaretPosition(), false,
			 * true)) {
			 * 
			 * int[] attValueDelimiter = getXMLDocument()
			 * .getAttributeValueLocation(getCaretPosition());
			 * 
			 * if (attValueDelimiter != null) { getXMLDocument()
			 * .manageCompletion( false, attValueDelimiter[0], new String( new
			 * char[] { (char) attValueDelimiter[2] })); } else {
			 * getXMLDocument().manageCompletion(true, getCaretPosition(), " ");
			 * } } else getXMLDocument() .manageCompletion(true,
			 * getCaretPosition(), "<");
			 */

		}
	}

	class EntityAssistantAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			getXMLDocument().manageCompletion(true, getCaretPosition(), "&");
		}
	}

	class SystemAssistantAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			getXMLDocument().manageCompletion(true, getCaretPosition(), "<!");
		}
	}

	// Indentation on a text selection

	class CustomTabAction extends TextAction {

		public CustomTabAction() {
			super(DefaultEditorKit.insertTabAction);
			ActionMap map = getActionMap();
		}

		public void actionPerformed(ActionEvent e) {
			// JPF : handling of caret at first position of next line

			int selectionEnd = getSelectionEnd();
			Element root = document.getDefaultRootElement();
			int lineStop = root.getElementIndex(selectionEnd);
			Element lastLine = root.getElement(lineStop);
			if (selectionEnd == lastLine.getStartOffset()) {
				selectionEnd--;
				lineStop--;
			}

			Character chrIndent = (Character) ActionModel.getProperty(
					ActionModel.FORMAT_ACTION, Properties.INDENT_CHAR_PROPERTY,
					new Character(' '));
			Integer indentSize = (Integer) ActionModel.getProperty(
					ActionModel.FORMAT_ACTION, Properties.INDENT_SIZE_PROPERTY,
					new Integer(4));

			int indent = indentSize.intValue();
			StringBuffer b = new StringBuffer();
			for (int i = 0; i < indent; i++)
				b.append(chrIndent.charValue());
			String indentString = b.toString();

			if (selectionEnd > getSelectionStart()) {
				int lineStart = document.getDefaultRootElement()
						.getElementIndex(getSelectionStart());

				int init = Math.min(lineStart, lineStop);
				int stop = Math.max(lineStart, lineStop);
				getXMLDocument().enableStructureDamagedSupport(false);

				for (int i = init; i <= stop; i++) {
					int lineOffset = document.getDefaultRootElement()
							.getElement(i).getStartOffset();
					try {
						if (i == stop)
							getXMLDocument().enableStructureDamagedSupport(true);
						getXMLDocument().insertString(lineOffset, indentString, null);
					} catch (BadLocationException exc) {
						getXMLDocument().enableStructureDamagedSupport(true);
					}
				}
			} else {
				try {
					// A priori pas juste en tabulation ?
					// document.insertString(getCaretPosition(), " ", null);
					document.insertString(getCaretPosition(), indentString,
							null);
				} catch (BadLocationException exc) {
					getXMLDocument().enableStructureDamagedSupport(true);
				}
			}
			// defaultAction.actionPerformed(e);
		}
	}

	// Unindent a text selection
	class CustomUntabAction extends TextAction {
		public CustomUntabAction() {
			super("delete-tab");
		}

		public void actionPerformed(ActionEvent e) {
			int lineStart;

			// JPF : handling of caret at first position of next line
			int selectionEnd = getSelectionEnd();
			Element root = document.getDefaultRootElement();
			int lineStop = root.getElementIndex(selectionEnd);
			Element lastLine = root.getElement(lineStop);
			if (selectionEnd == lastLine.getStartOffset()) {
				selectionEnd--;
				lineStop--;
			}

			if (selectionEnd > getSelectionStart()) {
				lineStart = document.getDefaultRootElement().getElementIndex(
						getSelectionStart());
			} else {
				lineStart = document.getDefaultRootElement().getElementIndex(
						getCaretPosition());
				lineStop = lineStart;
			}

			int init = Math.min(lineStart, lineStop);
			int stop = Math.max(lineStart, lineStop);

			Character chrIndent = (Character) ActionModel.getProperty(
					ActionModel.FORMAT_ACTION, Properties.INDENT_CHAR_PROPERTY,
					new Character(' '));
			Integer indentSize = (Integer) ActionModel.getProperty(
					ActionModel.FORMAT_ACTION, Properties.INDENT_SIZE_PROPERTY,
					new Integer(4));

			int indent = indentSize.intValue();

			getXMLDocument().enableStructureDamagedSupport(false);
			for (int i = init; i <= stop; i++) {
				if (i == stop)
					getXMLDocument().enableStructureDamagedSupport(true);
				int lineOffset = document.getDefaultRootElement().getElement(i)
						.getStartOffset();

				int lineEnd = document.getDefaultRootElement().getElement(i)
						.getEndOffset();

				try {
					// !! no handling of tab
					int count = 0;
					for (int c = lineOffset; c < lineEnd && count < indent
							&& getText(c, 1).charAt(0) == chrIndent.charValue(); c++, count++) {
					}
					document.remove(lineOffset, count);
				} catch (BadLocationException exc) {
					getXMLDocument().enableStructureDamagedSupport(true);
				}
			}
		}
	}

	////////////////////////////////////////////////////////////////////////////
	// //

	/**
	 * @return the highlight color
	 */
	public Color getSelectionHighlightColor() {
		return selectionHighlightColor;
	}

	/**
	 * @param selectionHighlightColor
	 *            Reset the highlight line color
	 */
	public void setSelectionHighlightColor(Color selectionHighlightColor) {
		this.selectionHighlightColor = selectionHighlightColor;
	}

	// //////// JOB FOR Real-time location

	final CurrentLocationJob LOCATION_JOB = new CurrentLocationJob();

	public void forceLocationJob() {
		LOCATION_JOB.preRun();
		LOCATION_JOB.run();
		LOCATION_JOB.dispose();
	}
	
	public static Class _locationJobClass = CurrentLocationJob.class;

	final class CurrentLocationJob implements Job, SwingEventSynchro, FastJob {

		public Object getSource() {
			return this;
		}

		public boolean hasErrors() {
			return false;
		}
		
		public void dispose() {
			computedLocation = null;
		}

		public boolean isAlone() {
			return true;
		}

		public void stopIt() {
		}

		FPNode computedLocation = null;

		public boolean preRun() {
			computedLocation = null;
			if (getXMLContainer() == null)
				return false;
			if (getXMLContainer().getTreeListeners() == null)
				return false;
			if (editorContext != null
					&& !getXMLContainer().getTreeListeners().isLocationLocked()) {
				if (!getXMLContainer().getTreeListeners().isLocationLocked()) {

					try {
						computedLocation = getXMLDocument()
								.getXMLPath(getCaretPosition());
					} catch (RuntimeException e) {
						// ?
					}

				}
			}
			return true;
		}

		public void run() {

			try {
				if (getXMLContainer() == null) {
					return;
				}
				if (getXMLContainer().getTreeListeners() == null)
					return;

				if (editorContext != null
						&& !getXMLContainer().getTreeListeners()
								.isLocationLocked()) {
					if (!getXMLContainer().getTreeListeners()
							.isLocationLocked()) {
						if (editorContext != null) {
							if (lastStructureLocation != computedLocation)
								editorContext.notifyLocation(computedLocation);
						}
						lastStructureLocation = computedLocation;
						// Avoid bad refresh for underline
						XMLEditor.this.repaint();
					}
				}
			} finally {
				computedLocation = null;
			}
		}
	}

	class CustomTransferHandler extends TransferHandler {

		public void exportToClipboard(JComponent comp, Clipboard clipboard,
				int action) throws IllegalStateException {
			if (comp instanceof JTextComponent) {
				JTextComponent text = (JTextComponent) comp;
				int p0 = text.getSelectionStart();
				int p1 = text.getSelectionEnd();
				if (p0 != p1) {
					try {
						Document doc = text.getDocument();
						String srcData = doc.getText(p0, p1 - p0);
						StringSelection contents = new StringSelection(srcData);

						// this may throw an IllegalStateException,
						// but it will be caught and handled in the
						// action that invoked this method
						clipboard.setContents(contents, null);

						if (action == TransferHandler.MOVE) {
							doc.remove(p0, p1 - p0);
						}
					} catch (BadLocationException ble) {
					}
				}
			}
		}

		public boolean importData(JComponent comp, Transferable t) {
			if (comp instanceof JTextComponent) {
				DataFlavor flavor = getFlavor(t.getTransferDataFlavors());

				if (flavor != null) {
					InputContext ic = comp.getInputContext();
					if (ic != null) {
						ic.endComposition();
					}
					try {
						
						Object obj = t.getTransferData(flavor);
						String data = null;
						
						try {
							if ( obj instanceof FPNode ) {
								
								FPNode sn = ( FPNode )obj;
								
								int delta = 0;
								if ( sn.isTag() )
									delta++;
								
								data = getDocument().getText(
										sn.getStartingOffset(),
										sn.getStoppingOffset() - sn.getStartingOffset() + delta );
								
							} else
													
								data = (String) t.getTransferData(flavor);

							((JTextComponent) comp).replaceSelection(data);
						} catch (BadLocationException e) {
							return false;
						}

						return true;
					} catch (UnsupportedFlavorException ufe) {
					} catch (IOException ioe) {
					}
				}
			}
			return false;
		}

		public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
			JTextComponent c = (JTextComponent) comp;
			if (!(c.isEditable() && c.isEnabled())) {
				return false;
			}
			return (getFlavor(transferFlavors) != null);
		}

		public int getSourceActions(JComponent c) {
			return NONE;
		}

		private DataFlavor getFlavor(DataFlavor[] flavors) {
			if (flavors != null) {
				for (int counter = 0; counter < flavors.length; counter++) {
					if ( flavors[ counter ].equals( DataFlavor.stringFlavor )) {
						return flavors[ counter ];
					} else
					if ( flavors[ counter ].equals( TreeListeners.NODE_FLAVOR ) )
						return flavors[ counter ];
				}
			}
			return null;
		}

	}

}
