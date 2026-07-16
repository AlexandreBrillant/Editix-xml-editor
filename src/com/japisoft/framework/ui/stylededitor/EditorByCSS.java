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

package com.japisoft.framework.ui.stylededitor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;

import com.japisoft.framework.ui.stylededitor.action.CaretBeginLineAction;
import com.japisoft.framework.ui.stylededitor.action.CaretDownAction;
import com.japisoft.framework.ui.stylededitor.action.CaretEndAction;
import com.japisoft.framework.ui.stylededitor.action.CaretEndLineAction;
import com.japisoft.framework.ui.stylededitor.action.CaretLeftAction;
import com.japisoft.framework.ui.stylededitor.action.CaretRightAction;
import com.japisoft.framework.ui.stylededitor.action.CaretStartAction;
import com.japisoft.framework.ui.stylededitor.action.CaretUpAction;
import com.japisoft.framework.ui.stylededitor.model.NodeElement;
import com.japisoft.framework.ui.stylededitor.model.XMLDocument;
import com.japisoft.framework.ui.stylededitor.ui.ViewPart;
import com.japisoft.framework.ui.stylededitor.ui.ViewPartContainer;
import com.japisoft.framework.ui.stylededitor.ui.ViewPartCursor;
import com.japisoft.framework.ui.stylededitor.ui.ViewPartDocument;
import com.japisoft.framework.ui.stylededitor.ui.ViewPartSelection;
import com.japisoft.framework.ui.stylededitor.ui.node.ElementView;
import com.japisoft.framework.ui.stylededitor.ui.node.ElementViewFactory;

public class EditorByCSS extends JComponent 
		implements 
			ActionListener,
			DocumentListener, 
			KeyListener, 
			MouseListener,
			MouseMotionListener,
			MouseWheelListener {

	private Document document;
	private int caretPosition;

	public EditorByCSS() {
		super();
		setBackground( Color.WHITE );
		setForeground( Color.BLACK );

		if ( UIManager.getColor( "editor.css.background" ) != null ) {
			setBackground( UIManager.getColor( "editor.css.background" ) );
		}

		if ( UIManager.getColor( "editor.css.foreground" ) != null ) {
			setForeground( UIManager.getColor( "editor.css.foreground" ) );
		}
		
		getActionMap().put( "left", new CaretLeftAction() );
		getActionMap().put( "right", new CaretRightAction() );

		getActionMap().put( "left2", new CaretLeftAction( true ) );
		getActionMap().put( "right2", new CaretRightAction( true ) );
		
		getActionMap().put( "up", new CaretUpAction() );
		getActionMap().put( "down", new CaretDownAction() );
		
		getActionMap().put( "up2", new CaretUpAction( true ) );
		getActionMap().put( "down2", new CaretDownAction( true ) );
		
		getActionMap().put( "begin-line", new CaretBeginLineAction() );
		getActionMap().put( "end-line", new CaretEndLineAction() );
		
		getActionMap().put( "begin-line2", new CaretBeginLineAction( true ) );
		getActionMap().put( "end-line2", new CaretEndLineAction( true ) );

		getActionMap().put( "start", new CaretStartAction() );
		getActionMap().put( "end", new CaretEndAction() );
		getActionMap().put( "end-line", new CaretEndLineAction() );

		getActionMap().put( "copy", new CopyAction() );
		getActionMap().put( "cut", new CutAction() );
		getActionMap().put( "paste", new PasteAction() );
		
		getActionMap().put( "clone", new DuplicateCurrent() );

		getInputMap().put( KeyStroke.getKeyStroke( KeyEvent.VK_LEFT, 0 ), "left" );
		getInputMap().put( KeyStroke.getKeyStroke( KeyEvent.VK_RIGHT, 0 ), "right" );		

		getInputMap().put( KeyStroke.getKeyStroke( KeyEvent.VK_LEFT, KeyEvent.SHIFT_DOWN_MASK ), "left2" );
		getInputMap().put( KeyStroke.getKeyStroke( KeyEvent.VK_RIGHT, KeyEvent.SHIFT_DOWN_MASK ), "right2" );		

		getInputMap().put( KeyStroke.getKeyStroke( KeyEvent.VK_UP, 0 ), "up" );
		getInputMap().put( KeyStroke.getKeyStroke( KeyEvent.VK_DOWN, 0 ), "down" );
		
		getInputMap().put( KeyStroke.getKeyStroke( KeyEvent.VK_UP, KeyEvent.SHIFT_DOWN_MASK ), "up2" );
		getInputMap().put( KeyStroke.getKeyStroke( KeyEvent.VK_DOWN, KeyEvent.SHIFT_DOWN_MASK ), "down2" );

		getInputMap().put( KeyStroke.getKeyStroke( KeyEvent.VK_HOME, KeyEvent.SHIFT_DOWN_MASK ), "begin-line2" );
		getInputMap().put( KeyStroke.getKeyStroke( KeyEvent.VK_END, KeyEvent.SHIFT_DOWN_MASK ), "end-line2" );
		
		getInputMap().put( KeyStroke.getKeyStroke( KeyEvent.VK_HOME, 0 ), "begin-line" );
		getInputMap().put( KeyStroke.getKeyStroke( KeyEvent.VK_END, 0 ), "end-line" );
		
		getInputMap().put( KeyStroke.getKeyStroke( KeyEvent.VK_PAGE_UP, 0 ), "start" );
		getInputMap().put( KeyStroke.getKeyStroke( KeyEvent.VK_PAGE_DOWN, 0 ), "end" );
		
		getInputMap().put( KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() ), "clone" );
		
		getInputMap().put( 
				KeyStroke.getKeyStroke(
					KeyEvent.VK_C, 
					java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() 
				), 
				"copy" 
		);

		getInputMap().put( 
			KeyStroke.getKeyStroke(
				KeyEvent.VK_X, 
				java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() 
			), 
			"cut" 
		);

		getInputMap().put( 
			KeyStroke.getKeyStroke(
				KeyEvent.VK_V,
				java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
			), 
			"paste" 
		);				
	}

	public EditorByCSS( XMLDocument document ) {
		this();
		setFocusable( true );
		setDocument( document );
	}
	
	private Factory factory = null;
	
	public void setFactory( Factory factory ) {
		this.factory = factory;
	}
	
	private LocationListener ll = null;
	
	public void setLocationListener( LocationListener listener ) {
		this.ll = listener;
	}

	private Timer t = null;
	
	@Override
	public void addNotify() {
		super.addNotify();		
		requestFocus();
		// setCaretPosition( 0 );		
		addKeyListener( this );
		addMouseListener( this );
		addMouseMotionListener( this );
		addMouseWheelListener( this );		
		t = new Timer( 2000, this );
		t.setRepeats( true );
		t.start();
	}

	@Override
	public void removeNotify() {
		super.removeNotify();		
		removeKeyListener( this );
		removeMouseListener( this );
		removeMouseMotionListener( this );
		removeMouseWheelListener( this );
		t.stop();
		t = null;
	}

	public void cut() {		
		getActionMap().get( "cut" ).actionPerformed( null );		
	}
	
	public void copy() {
		getActionMap().get( "copy" ).actionPerformed( null );		
	}
	
	public void paste() {
		getActionMap().get( "paste" ).actionPerformed( null );
	}

	public void actionPerformed( ActionEvent e ) {
		repaint();
	}

	public void keyPressed( KeyEvent e ) {		
		if ( e.getKeyCode() == KeyEvent.VK_DELETE || 
				e.getKeyCode() == KeyEvent.VK_BACK_SPACE ) {			
			try {			
				int currentOffset = getCaretPosition();
				if ( e.getKeyCode() == KeyEvent.VK_DELETE ) {
					if ( !hasSelection() )
						getDocument().remove( currentOffset, 1 );
					else {
						cut();
					}
				} else
				if ( e.getKeyCode() == KeyEvent.VK_BACK_SPACE ) {
					if ( currentOffset > 0 ) {
						getDocument().remove( currentOffset - 1, 1 );
						SwingUtilities.invokeLater(
							new Runnable() {
								public void run() {
									getActionMap().get( "left" ).actionPerformed( 
										new ActionEvent( EditorByCSS.this, 0, null ) );
								}
							}
						);
					}
				}
			} catch( BadLocationException exc ) {}
		} else {
			if ( com.japisoft.framework.xml.XMLChar.isValid( e.getKeyCode() ) && !e.isActionKey() && !e.isControlDown() ) {
				char ch = e.getKeyChar();
				try {
					getDocument().insertString( this.caretPosition, Character.toString( ch ), null );
					getActionMap().get( "right" ).actionPerformed( 
							new ActionEvent( 
								this, 
								0,
								null ) 
					);
				} catch( BadLocationException exc ) {
					// ?
				}
			}
		}
	}
	
	public void keyReleased(KeyEvent e) {}

	public void keyTyped(KeyEvent e) {}
	
	private int selectionStart = -1;

	public int getSelectionStart() {
		if ( selectionStart == -1 )
			return getSelectionEnd();
		return selectionStart;
	}

	public int getSelectionEnd() {
		return getCaretPosition();
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		int notches = e.getWheelRotation();
		if ( e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL ) {
			Rectangle r = getVisibleRect();	
			if ( notches > 0 ) {
				r.y += 20;
			} else {
				r.y -= 20;
			}
			scrollRectToVisible( r );
		}
	}

	public void mouseClicked( MouseEvent e ) {
		requestFocus();
	}

	public void mouseEntered( MouseEvent e ) {
	}

	public void mouseExited( MouseEvent e ) {
	}

	private boolean mousePressedFlag = false;
	private boolean mousePopup = false;
	
	public void mousePressed( MouseEvent e ) {
		if ( !e.isPopupTrigger() ) {
			if ( e.getButton() < 2 ) {
				mousePressedFlag = true;
				setCaretPosition( viewToModel( e.getPoint() ) );
				selectionStart = getCaretPosition();
				repaint();
			}
		} else {
			showPopup( e );
			mousePopup = true;
		}
		checkSelection();
	}

	public void mouseReleased( MouseEvent e ) {		
		if ( e.isPopupTrigger() ) {
			showPopup( e );
		} else {
			if ( !mousePopup )
				mousePressedFlag = false;
		}		
		mousePopup = false;
	}

	private JPopupMenu popup, popup2 = null;
	private int lastPopupX, lastPopupY;
	
	private void showPopup( MouseEvent e ) {
		if ( !displayTag() ) {
			if ( popup == null ) {
				popup = new JPopupMenu();
				popup.add( getActionMap().get( "cut" ) );
				popup.add( getActionMap().get( "copy" ) );
				popup.add( getActionMap().get( "paste" ) );
			}

			popup.show( 
				this, 
				lastPopupX = e.getX(), 
				lastPopupY = e.getY() 
			);
		} else {
			if ( popup2 == null ) {
				popup2 = new JPopupMenu();		
				popup2.add( getActionMap().get( "cut" ) );
				popup2.add( getActionMap().get( "copy" ) );
				popup2.add( getActionMap().get( "paste" ) );
				popup2.addSeparator();
				popup2.add( new AppendAction() );
				popup2.add( new AppendTextAction() );
				popup2.addSeparator();
				popup2.add( new InsertBeforeAction() );				
				popup2.add( new InsertAfterAction() );
				popup2.addSeparator();
				popup2.add( new DeleteAction() );
			}
			popup2.show( 
				this, 
				lastPopupX = e.getX(), 
				lastPopupY = e.getY() 
			);			
		}
	}

	public Node getPopupDOMNode() {
		XMLDocument doc = ( XMLDocument )getDocument();
		return doc.getDOMAt( lastPopupX, lastPopupY );
	}
	
	public Node getCurrentNode() {
		int index = getDocument().getDefaultRootElement().getElementIndex( getCaretPosition() );
		Element currentElement = getDocument().getDefaultRootElement().getElement( index );
		if ( currentElement instanceof NodeElement ) {
			return ( ( NodeElement )currentElement ).getNode();
		}
		return null;
	}

	// Useful for managing multiple line
	// Must be used at the end of the CSS usage
	public void prepareDOMDocument() {
		org.w3c.dom.Document doc = ( ( XMLDocument )getDocument() ).getDOMDocument();		
		DocumentTraversal traversal = ( DocumentTraversal )doc;
		TreeWalker tw = traversal.createTreeWalker( 
				doc.getDocumentElement(), 
				NodeFilter.SHOW_ELEMENT, 
				null, 
				false );

		org.w3c.dom.Element e = null;
		
		while ( ( e = ( org.w3c.dom.Element )tw.nextNode() ) != null ) {
			prepareDOMElement( e );
		}
	}
	
	private void prepareDOMElement( org.w3c.dom.Element e ) {
		NodeList nl = e.getChildNodes();
		Text oldText = null;
		ArrayList<Text> toRemove = null;
		for ( int i = 0; i < nl.getLength(); i++ ) {
			if ( nl.item( i ) instanceof Text ) {
				if ( oldText != null ) {
					Text t = ( Text )nl.item( i );
					oldText.setNodeValue( 
							oldText.getNodeValue() + "\n" + t.getNodeValue() 
					);
					if ( toRemove == null )
						toRemove = new ArrayList<Text>();
					toRemove.add( t );
				} else {
					oldText = ( Text )nl.item( i );
				}
			} else
				oldText = null;
		}
		
		if ( toRemove != null ) {
			for ( Text t : toRemove ) {
				t.getParentNode().removeChild( t );
			}
		}
	}

	public void mouseDragged(MouseEvent e) {
		if ( !e.isPopupTrigger() ) {
			if ( mousePressedFlag ) {
				setCaretPosition( viewToModel( e.getPoint() ) );
				checkSelection();
			}
		}
	}

	private boolean displayTag = true;
	
	public boolean displayTag() {
		return displayTag;
	}

	public void setDisplayTag( boolean display ) {
		this.displayTag = display;
		invalidateScrollPaneFlag = true;
		repaint();
	}
	
	public void mouseMoved(MouseEvent e) {
		Node n = ( ( XMLDocument )getDocument() ).getDOMAt( e.getX(), e.getY() );
		if ( n == null )
			setToolTipText( null );
		else
			setToolTipText( n.getNodeName() );
	}

	public void changedUpdate( DocumentEvent e ) {
		repaint();
	}

	public void insertUpdate( DocumentEvent e ) {
		// Update all the element view position
		repaint();
		if ( "\n".equals( e.toString() ) ) {
			SwingUtilities.invokeLater(
					new Runnable() {
						public void run() {
							int currentIndex = document.getDefaultRootElement().getElementIndex( getCaretPosition() + 1 );
							if ( currentIndex > -1 ) {
								Element nextElement = document.getDefaultRootElement().getElement( currentIndex );
								setCaretPosition( nextElement.getStartOffset() );
							}
						}
					}
			);
		}
		invalidateScrollPane();
	}

	public void removeUpdate( DocumentEvent e ) {
		if ( "\n".equals( e.toString() ) )
			setCaretPosition( Math.max( 0, getCaretPosition() - 1 ) );
		repaint();
		invalidateScrollPane();
	}
	
	// ----------------------------------------------------------------
	
	public void setDocument( Document document ) {
		if ( this.document != null ) {
			this.document.removeDocumentListener( this );
		}
		this.document = document;
		document.addDocumentListener( this );
		invalidateScrollPaneFlag = true;
	}

	public Document getDocument() {
		return document;
	}

	private boolean invalidateScrollPaneFlag = false;

	private void invalidateScrollPane() {
		NodeElement root = ( NodeElement )document.getDefaultRootElement();
		ElementView view = ElementViewFactory.getInstance().getView( this, root );
		Dimension d = view.getSize( this, root, getGraphics() );
		setPreferredSize( d );
		revalidate();
		invalidateScrollPaneFlag = false;
	}

	private ViewPart rootView = null;
	
	private ViewPart getRootView() {
		if ( rootView == null ) {
			rootView = new ViewPartContainer();
			( ( ViewPartContainer )rootView ).addView( new ViewPartDocument() );
			( ( ViewPartContainer )rootView ).addView( new ViewPartSelection() );				
			( ( ViewPartContainer )rootView ).addView( new ViewPartCursor() );			
		}
		return rootView;
	}

	protected void paintComponent( Graphics g ) {
		g.setColor( getBackground() );
		g.fillRect( 0, 0, getWidth(), getHeight() );
		g.setColor( getForeground() );
		if ( document == null ) {
			super.paintComponent( g );
		} else {
			getRootView().paint( this, g );
		}
		if ( invalidateScrollPaneFlag ) {
			invalidateScrollPane();
		}
	}

	public void setCaretPosition( int offset ) {
		// ( ( XMLDocument )getDocument() ).dump();
		this.caretPosition = offset;
		Rectangle visiblePart = modelToView( offset );
		if ( visiblePart != null ) {
			visiblePart.x = visiblePart.width;
			scrollRectToVisible( visiblePart );
		}
		repaint();
		if ( !mousePressedFlag ) {
			setSelectionStart( offset );
		}
		checkSelection();
		if ( this.ll != null ) {
			int index = getDocument().getDefaultRootElement().getElementIndex( offset );
			if ( index >= 0 ) {
				Element e = getDocument().getDefaultRootElement().getElement( 
					index
				);
				if ( e != null && e instanceof NodeElement ) {
					ll.setLocation( ( ( NodeElement )e ).getNode() );
				}
			}
		}
	}

	private void checkSelection() {
		boolean selection = hasSelection();
		getActionMap().get( "cut" ).setEnabled( selection );
		getActionMap().get( "copy" ).setEnabled( selection );		
	}

	public void setSelectionMode( boolean enabled ) {
		mousePressedFlag = enabled;
		if ( !enabled ) {
			setSelectionStart( getCaretPosition() );
		}
	}

	public boolean hasSelection() {
		return ( this.caretPosition != selectionStart );
	}
	
	public void setSelectionStart( int offset ) {
		this.selectionStart = offset;
	}

	public int getCaretPosition() {
		return this.caretPosition;
	}

	public Rectangle modelToView( int offset ) {
		Element root = document.getDefaultRootElement();
		int index = root.getElementIndex( offset );
		NodeElement node = ( NodeElement )root.getElement( index );
		ElementView view = ElementViewFactory.getInstance().getView( this, node );
		if ( view == null || node == null )	// ?
			return null;
		return view.getBounds( this, node, offset, getGraphics() );
	}

	public int viewToModel( Point pt ) {
		Element root = document.getDefaultRootElement();
		for ( int i = 0; i < root.getElementCount(); i++ ) {
			Element e = root.getElement( i );
			if ( e instanceof NodeElement ) {
				NodeElement ne = ( NodeElement )e;
				ElementView view = ElementViewFactory.getInstance().getView( this, ne );
				if ( view == null )
					continue;
				if ( view.isInside( this, ne, pt.x, pt.y, getGraphics() ) ) {
					return ne.getStartOffset() + view.getOffset( this, ne, pt.x, pt.y, getGraphics() );					
				}
			}
		}
		return -1;
	}

	// Repaint and update the scrollpane
	private void repaint2() {
		invalidateScrollPaneFlag = true;
		repaint();
	}
	
	// ---------------------------------------------------------------------------------------------------
	
	class DuplicateCurrent extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			Node n = getCurrentNode();
			if ( n != null ) {				
				if ( n instanceof Text ) {
					n = n.getParentNode();
				}
				if ( n instanceof org.w3c.dom.Element ) {
					org.w3c.dom.Element ee = ( org.w3c.dom.Element )n;
					org.w3c.dom.Element pe = ( org.w3c.dom.Element )ee.getParentNode();
					ee = ( org.w3c.dom.Element )ee.cloneNode( true );
					pe.appendChild( ee );
					( ( XMLDocument )getDocument() ).updateContent();
					repaint2();					
				}
			}
		}
	}
	
	
	class InsertBeforeAction extends AbstractAction {
		public InsertBeforeAction() {
			putValue( Action.NAME, "Insert Before..." );
		}
		public void actionPerformed( ActionEvent e ) {			
			Node n = getPopupDOMNode();			
			if ( n != null ) {
				String name = "item";
				if ( factory != null )
					name = factory.getNodeName( n.getNodeName() );				
				Node parent = n.getParentNode();
				parent.insertBefore( n.getOwnerDocument().createElement( name ), n );
				( ( XMLDocument )getDocument() ).updateContent();
				repaint2();
			}
		}		
	}

	class InsertAfterAction extends AbstractAction {
		public InsertAfterAction() {
			putValue( Action.NAME, "Insert After..." );
		}
		public void actionPerformed( ActionEvent e ) {
			Node n = getPopupDOMNode();
			if ( n != null ) {
				String name = "item";
				if ( factory != null )
					name = factory.getNodeName( n.getNodeName() );
				Node parent = n.getParentNode();				
				if ( n.getNextSibling() != null ) {
					parent.insertBefore( n.getOwnerDocument().createElement( name ), n.getNextSibling() );						
				} else {
					parent.appendChild( n.getOwnerDocument().createElement( name ) );
				}
				
				( ( XMLDocument )getDocument() ).updateContent();
				repaint2();
			}
		}		
	}
		
	class AppendAction extends AbstractAction {
		public AppendAction() {
			putValue( Action.NAME, "Append..." );
		}
		public void actionPerformed( ActionEvent e ) {
			String name = "item";
			if ( factory != null )
				name = factory.getNodeName( null );			
			Node n = getPopupDOMNode();
			if ( n != null ) {
				n.appendChild( n.getOwnerDocument().createElement( name ) );
				( ( XMLDocument )getDocument() ).updateContent();
				repaint2();
			}
		}
	}

	class AppendTextAction extends AbstractAction {
		public AppendTextAction() {
			putValue( Action.NAME, "Append a text" );
		}
		public void actionPerformed( ActionEvent e ) {
			Node n = getPopupDOMNode();
			if ( n != null ) {
				n.appendChild( n.getOwnerDocument().createTextNode( "New Content" ) );
				( ( XMLDocument )getDocument() ).updateContent();
				repaint2();
			}
		}
	}

	class DeleteAction extends AbstractAction {
		public DeleteAction() {
			putValue( Action.NAME, "Delete..." );
		}
		public void actionPerformed( ActionEvent e ) {
			Node n = getPopupDOMNode();
			
			if ( n != null ) {
				if ( factory != null ) {
					if ( factory.confirm( "Delete [" + n.getNodeName() + "] ?" ) ) {
						n.getParentNode().removeChild( n );
						( ( XMLDocument )getDocument() ).updateContent();
						repaint2();
					}
				}
			}
		}		
	}
	
	class CutAction extends AbstractAction {
		public CutAction() {
			putValue( Action.NAME, "Cut" );
			putValue( Action.SMALL_ICON, 
				new ImageIcon( getClass().getResource( "cut.png" ) ) 
			);
		}
		public void actionPerformed(ActionEvent e) {
			( getActionMap().get( "copy" ) ).actionPerformed( e );
			int start = Math.min( getSelectionStart(), getSelectionEnd() );
			int end = Math.max( getSelectionStart(), getSelectionEnd() );
			try {
				getDocument().remove( start, end - start );
			} catch( BadLocationException exc ) {
			}
			setSelectionMode( false );
		}
	}

	class CopyAction extends AbstractAction {
		public CopyAction() {
			putValue( Action.NAME, "Copy" );
			putValue( Action.SMALL_ICON, 
				new ImageIcon( getClass().getResource( "copy.png" ) ) 
			);
		}
		public void actionPerformed(ActionEvent e) {
			try {				
				int start = Math.min( getSelectionStart(), getSelectionEnd() );
				int end = Math.max( getSelectionStart(), getSelectionEnd() );
				String content = getDocument().getText( start, end - start );
				StringSelection stringSelection = new StringSelection( content );
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(stringSelection, null );
			} catch( BadLocationException exc ) {}
		}	
	}
	
	class PasteAction extends AbstractAction {
		public PasteAction() {
			putValue( Action.NAME, "Paste" );
			putValue( Action.SMALL_ICON, 
				new ImageIcon( getClass().getResource( "paste.png" ) ) 
			);
		}				
		public void actionPerformed(ActionEvent e) {
			Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
			Transferable t = cb.getContents( null );
			if ( t != null ) {
				if ( t.isDataFlavorSupported( DataFlavor.stringFlavor ) ) {
					try {
						String txt = (String)t.getTransferData(DataFlavor.stringFlavor);
						getDocument().insertString( getCaretPosition(), txt, null );
						setCaretPosition( getCaretPosition() + txt.length() );
					} catch( Exception exc ) {
					}
				}
			}
			setSelectionMode( false );
		}		
	}

	public static void main( String[] args ) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating( true );
		dbf.setIgnoringElementContentWhitespace( true );
		DocumentBuilder db = dbf.newDocumentBuilder();
		
		File f = new File( "c:/travail/soft/japisoft-stylededitor/data/Test.xml" );
		org.w3c.dom.Document doc = db.parse( f );
		XMLDocument d = new XMLDocument( f.toURI().toString(), doc );
		final EditorByCSS evb = new EditorByCSS( d );
		JFrame fr = new JFrame();
		fr.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		fr.setSize( 300, 300 );
		fr.add( new JScrollPane( evb ) );

		fr.setVisible( true );
	}

}
