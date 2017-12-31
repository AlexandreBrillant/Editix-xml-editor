package com.japisoft.editix.editor.xsd.view.designer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.TransferHandler;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.japisoft.editix.editor.xsd.toolkit.SchemaHelper;

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
public abstract class XSDAbstractComponentImpl extends JComponent 
	implements
		MouseListener, 
			XSDComponent, 
				MouseMotionListener {

	private static final int PADDING_HEIGHT = 10;

	protected Element e;

	protected boolean selected = false;
	protected boolean opened = false;
	protected boolean paintElementName = true;
	protected boolean paintName = true;

	private static final Stroke OPT_ELE_BORDER = 
		new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
	        BasicStroke.JOIN_MITER, 2.0f, new float[] { 2.0f }, 0.0f);

	private static final Stroke DEFAULT_STROKE = new BasicStroke(1);

	static final ImageIcon CLOSE_ICON = new ImageIcon(
			XSDAbstractComponentImpl.class.getResource("box_closed.png"));

	public static final ImageIcon OPEN_ICON = new ImageIcon(
			XSDAbstractComponentImpl.class.getResource("box.png"));

	public XSDAbstractComponentImpl() {	
		setTransferHandler( new DragDropImpl() );
	}
	
	public void setElement(Element e) {
		this.e = e;
		setBackground( ColorFactory.getBackgroundColor( getLocalName() ) );
	}

	public Element getElement() {
		return e;
	}

	public void dispose() {
		this.e.setUserData("ui", null, null);
		this.e = null;
		this.listener = null;
		for (int i = 0; i < getComponentCount(); i++) {
			XSDComponent c = (XSDComponent) getComponent(i);
			c.dispose();
		}
	}

	public JComponent getView() {
		return this;
	}

	public void setSelected(boolean state) {
		this.selected = state;
		repaint();
	}

	public void setOpened(boolean state) {
		this.opened = state;
		e.setUserData( 
				"opened", 
				new Boolean( state ), 
				null );
		repaint();
	}

	public boolean isOpened() {
		if ( e.getUserData( "opened" ) != null ) {
			return ( Boolean )e.getUserData( "opened" );
		}
		return opened;
	}

	private XSDComponentListener listener;

	public void setComponentListener(XSDComponentListener listener) {
		this.listener = listener;
	}

	protected boolean hasOpenIcon = true;

	public void mouseClicked(MouseEvent e) {
		
		Rectangle r = new Rectangle( 
				0, 
				0, 
				getWidth() - ( hasOpenIcon ? OPEN_ICON.getIconWidth() : 0 ), 
				getHeight() );
		if ( r.contains( e.getX(), e.getY() ) ) {

			if ( e.getClickCount() > 1 ) {
				listener.goInto( this );
			} else
				listener.select( this );

		} else {
			r = new Rectangle( 	
					getWidth() - OPEN_ICON.getIconWidth(),
					0,
					OPEN_ICON.getIconWidth(),
					getHeight() );
			if ( r.contains( e.getX(), e.getY() ) ) {
				listener.openClose( this, true );
			}
		}
	}

	public void addNotify() {
		super.addNotify();
		addMouseListener(this);
		addMouseMotionListener( this );
	}

	public void removeNotify() {
		super.removeNotify();
		removeMouseListener(this);
		removeMouseMotionListener( this );
	}

	public void mousePressed(MouseEvent e) {
		mayBePopup( e );
	}

	private void mayBePopup( MouseEvent e ) {
		if ( e.isPopupTrigger() ) {
			Rectangle r = new Rectangle( 0, 0, getWidth() - OPEN_ICON.getIconWidth(), getHeight() );
			if ( r.contains( e.getX(), e.getY() ) ) {
				listener.popup( this, e.getX(), e.getY() );
			}			
		}
	}
	
	public void mouseReleased(MouseEvent e) {
		mayBePopup( e );
	}

	public void mouseEntered(MouseEvent e) {

		setToolTipText( 
				getToolTip( this.e, "Right click for adding an element<br>Double click for going into the definition" ) );

	}
	
	public void mouseDragged(MouseEvent e) {
		currentDragComponent = this;			
		getTransferHandler().exportAsDrag( this, e, TransferHandler.MOVE );
	}

	public void mouseMoved(MouseEvent e) {
	}

	public static String getToolTip( Element e, String message ) {

		StringBuffer s = 
			new StringBuffer( "<html><body>" );
		s.append( "<b>" ).append( e.getLocalName() ).append( "</b>" );
		
		String name = null;
		
		if (e.hasAttribute("name"))
			name = e.getAttribute("name");
		else if (e.hasAttribute("ref"))
			name = e.getAttribute("ref");

		if ( name != null ) {
			s.append( "<i> " ).append( name );

			if ( e.hasAttribute( "type" ) ) {
				s.append( " (" + e.getAttribute( "type" ) + ")" );
			}
			s.append( "</i>" );
		}

		String doc = SchemaHelper.getDocumentation( e );
		if ( doc != null ) {
			s.append( "<div style='width:400px;color:green'>" );
			s.append( doc );
			s.append( "</div>" );
		}

		if ( message != null ) {
			s.append( "<div style='color:gray'>" );
			s.append( message );
			s.append( "</div>" );
		}

		s.append( "</body></html>" );
		return s.toString();

	}

	public void mouseExited(MouseEvent e) {}

	protected Stroke borderStyle;

	private static Font typeFont = new Font( "arial", Font.ITALIC, 10 );
	private static Font nameFont = new Font( "arial", Font.BOLD, 12 );
	private static Color SELECTED_COLOR = ColorFactory.getSelectedBackgroundColor( null );

	private Shape rCache = null;

	protected boolean isOptional() {
		return "0".equals( e.getAttribute( "minOccurs" ) ) ||
			"optional".equals( e.getAttribute( "use" ) );		
	}
	
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		g2.setRenderingHint( 
			RenderingHints.KEY_TEXT_ANTIALIASING, 
			RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB 
		);
		
		int openWidth = OPEN_ICON.getIconWidth();
		int openHeight = OPEN_ICON.getIconHeight();

		if ( !hasOpenIcon ) {
			openWidth = 0;
			openHeight = 0;
		}
				
		int y = 1;
		int x = 1;
		
		if ( rCache == null || 
				rCache.getBounds().width != ( getWidth() - openWidth - 2 ) || 
					rCache.getBounds().height != ( getHeight() - 2 ) ) {
			rCache = createBorderShape();
		}		
		
		if (selected) {
			g.setColor( SELECTED_COLOR );
		} else {
			g.setColor( getBackground() );
		}

		Paint p = g2.getPaint();
		g2.setPaint( new GradientPaint(0,0,g2.getColor(),getWidth(),getHeight(),Color.WHITE,false) );
		g2.fill( rCache );		
		g2.setPaint( p );
		
		if ( hasOpenIcon() ) {
			Image oi = CLOSE_ICON.getImage();
			if (opened)
				oi = OPEN_ICON.getImage();
			g.drawImage(oi, getWidth() - OPEN_ICON.getIconWidth(),
					(getHeight() / 2 - OPEN_ICON.getIconHeight() / 2)
							, null);
		}

		if ( selected )
			g2.setColor(Color.BLUE);
		else
			g2.setColor(Color.GRAY);

		if ( borderStyle != null ) {			
			g2.setStroke(borderStyle);
		} else {
			if ( isOptional() )
				g2.setStroke( OPT_ELE_BORDER );			
		}

		g2.draw( rCache );

		paintSpecialContent( g2 );

		x++;
		
		if (borderStyle != null) 
			g2.setStroke(DEFAULT_STROKE);

		if ( paintElementName ) {
			g2.setColor(Color.DARK_GRAY);
			g2.setFont(typeFont);
			g2.drawString(getElementName(), x + 1, y
					+ getFontMetrics(typeFont).getAscent() + 1);
		}

		if ( paintName ) {
			if ( isNameVisible() ) {
				if ( selected )
					g2.setColor( ColorFactory.getSelectedForegroundColor( getElementName() ) );
				else
					g2.setColor( ColorFactory.getForegroundColor( getElementName() ) );
				g2.setColor( Color.BLACK );
				y += ( ( getHeight() - getFontMetrics(typeFont).getHeight() ) / 2 ) +
							getFontMetrics( nameFont ).getAscent()
							- 2; // ??
				g2.setFont( nameFont );
				g2.drawString(getName(), x + 1, y);
			}
		}

		if ( e.hasAttribute( "ref" ) ) {
			g2.setColor( Color.GRAY );
			g2.fillPolygon( 
					new int[] { getWidth() - openWidth - 1, getWidth() - openWidth - 1, getWidth() - 5 - openWidth - 1 }, 
					new int[] { getHeight() - 5 - 1, getHeight() - 1, getHeight() - 1 }, 
					3 );
		}
	}

	protected boolean isNameVisible() { return true; }	
	protected void paintSpecialContent( Graphics2D g ) {}

	private boolean hasOpenIcon() {
		if ( hasOpenIcon && e.hasChildNodes() ) {
			NodeList nl = e.getChildNodes();
			for ( int i = 0; i < nl.getLength(); i++ ) {
				Node n = nl.item( i );
				if ( n.getNodeType() == Node.TEXT_NODE )
					continue;
				if ( n.getNodeType() == Node.COMMENT_NODE )
					continue;
				if ( "simpleType".equals( n.getLocalName() ) )
					return false;
				else
					if ( !"annotation".equals( n.getLocalName() ) )
						return true;
			}
			return false;
		} else
			return false;
	}

	protected Shape createBorderShape() {

		int openWidth = OPEN_ICON.getIconWidth();
		int openHeight = OPEN_ICON.getIconHeight();

		int y = 1;
		int x = 1;

		if ( !hasOpenIcon ) {
			openWidth = 0;
			openHeight = 0;
		}

		return new Rectangle( x, y, getWidth() - openWidth - 2,
				getHeight() - 2 );
	}

	public Rectangle getVisibleBounds() {
		int openWidth = OPEN_ICON.getIconWidth();
		int openHeight = OPEN_ICON.getIconHeight();		
		int x = 1;
		int y = 1;
		return new Rectangle(
				x, 
				y, 
				getWidth() - openWidth - 2,
				getHeight() );
	}	

	/** For complex container */
	public String getElementName() {
		return e.getLocalName();
	}

	public String getLocalName() {
		return e.getLocalName();
	}
	
	public String getName() {
		if (e.hasAttribute( "name" ))
			return e.getAttribute( "name" );
		else if (e.hasAttribute( "ref" ))
			return e.getAttribute( "ref" );
		else
			return "";
	}

	protected Dimension cachedPreferredSize = null;

	public void updateUIContentCache() {
		cachedPreferredSize = null;
	}
	
	public Dimension getPreferredSize() {
		return getNameSize();
	}
	
	protected Dimension getNameSize() {
		if ( cachedPreferredSize != null )
			return cachedPreferredSize;
		int w = 0;
		int h = 0;
		if ( hasOpenIcon )
			h += CLOSE_ICON.getIconHeight();
		if ( hasOpenIcon )
			w += OPEN_ICON.getIconWidth();
		FontMetrics fm1 = getFontMetrics( typeFont );
		h = Math.max( h, fm1.getHeight() );
		String name = getName();
		if (name != null) {
			FontMetrics fm2 = getFontMetrics( nameFont );
			w += Math.max(fm2.stringWidth(name), fm1.stringWidth( e
					.getLocalName()));
		} else
			w += fm1.stringWidth( e.getLocalName() );
		cachedPreferredSize = new Dimension(w + 5, h + PADDING_HEIGHT );
		return cachedPreferredSize;
	}

	public void remove( boolean validate ) {
		// Remove all the children
		NodeList nl = e.getChildNodes();
		if ( nl != null ) {
			for ( int i = 0; i < nl.getLength(); i++ ) {
				if ( nl.item( i ) instanceof Element ) {
					Element child = ( Element )nl.item( i );
					XSDComponent c = ( XSDComponent )child.getUserData( "ui" );
					if ( c != null ) {
						c.remove(validate);
					}
				}
			}
		}
		Container c = getParent();
		c.remove( this );
		c.invalidate();
		if ( validate ) {
			c.validate();
			c.repaint();
		}
		dispose();
	}

	///////////////////////////////////////////////////////////////

	private static XSDAbstractComponentImpl currentDragComponent = null;
	private static XSDAbstractComponentImpl currentDropComponent = null;
	private static boolean acceptedImport = false;

	class AddChildAction extends AbstractAction {
		private Element dragElement = null;
		private Element dropElement = null;

		AddChildAction( Element dragElement, Element dropElement ) {
			putValue( NAME, "Add as a child" );
			this.dragElement = dragElement;
			this.dropElement = dropElement;
		}
		public void actionPerformed(ActionEvent e) {
			listener.moveInto(
					( XSDComponent )dragElement.getUserData( "ui" ),
					( XSDComponent )dropElement.getUserData( "ui" ) );
			this.dragElement = null;
			this.dropElement = null;
		}
	}

	class AddBeforeAction extends AbstractAction {
		private Element dragElement = null;
		private Element dropElement = null;

		AddBeforeAction( Element dragElement, Element dropElement ) {
			putValue( NAME,  "Add before this node" );
			this.dragElement = dragElement;
			this.dropElement = dropElement;
		}
		public void actionPerformed(ActionEvent e) {
			listener.moveBefore(
					( XSDComponent )dragElement.getUserData( "ui" ),
					( XSDComponent )dropElement.getUserData( "ui" ) );			
			this.dragElement = null;
			this.dropElement = null;
		}		
	}

	class AddAfterAction extends AbstractAction {
		private Element dragElement = null;
		private Element dropElement = null;

		AddAfterAction( Element dragElement, Element dropElement ) {
			putValue( NAME, "Add after this node" );
			this.dragElement = dragElement;
			this.dropElement = dropElement;
		}		
		public void actionPerformed(ActionEvent e) {
			listener.moveAfter(
					( XSDComponent )dragElement.getUserData( "ui" ),
					( XSDComponent )dropElement.getUserData( "ui" ) );			
			this.dragElement = null;
			this.dropElement = null;			
		}		
	}

	class DragDropImpl extends TransferHandler {

		public boolean canImport( JComponent comp, DataFlavor[] transferFlavors ) {
			acceptedImport = testImport( comp );
			if ( acceptedImport )
				currentDropComponent = (XSDAbstractComponentImpl)comp;
			return acceptedImport;
		}

		private boolean testImport( JComponent comp ) {
			
			// If comp is a child of the current element, reject

			if ( currentDragComponent != null && 
					comp instanceof XSDAbstractComponentImpl ) {

				XSDAbstractComponentImpl targetComp = ( XSDAbstractComponentImpl )comp;
				Element dragElement = currentDragComponent.getElement();
				Element dropElement = ( targetComp.getElement() );

				if ( dragElement == dropElement )	// For continuing the Drag / Drop
					return true;

				while ( dropElement != null ) {

					if ( dropElement.getParentNode() instanceof Element ) {
						dropElement = ( Element )dropElement.getParentNode();
					} else
						break;

					if ( dropElement == dragElement ) {

						return false;

					}

				}

				dropElement = ( targetComp.getElement() );

				if ( SchemaHelper.canHaveThisChild( dropElement, dragElement ) ) {
					return true;
				} else {
					if ( SchemaHelper.canHaveThisChild( ( Element )dropElement.getParentNode(), dragElement ) ) {
						return true;
					}
				}
				
				return false;

			} else
				return false;

		}

		protected Transferable createTransferable( JComponent c ) {
			
			return new StringSelection( c.getName() );

		}

		protected void exportDone( JComponent source, Transferable data, int action ) {
			super.exportDone( source, data, action );

			if ( acceptedImport ) {
				JPopupMenu popup = new JPopupMenu();
				Element dropElement = currentDropComponent.getElement();
				Element dragElement = currentDragComponent.getElement();
	
				if ( dragElement == dropElement )
					return;

				if ( SchemaHelper.canHaveThisChild( 
						( Element )dropElement.getParentNode(), 
						dragElement ) ) {
					JMenuItem item = new JMenuItem( 
							new AddBeforeAction( dragElement, dropElement ) );
					popup.add( item );
					item = new JMenuItem( 
							new AddAfterAction( dragElement, dropElement ) );
					popup.add( item );
				}

				if ( SchemaHelper.canHaveThisChild( dropElement, dragElement ) ) {
					if ( popup.getComponentCount() > 0 )
						popup.addSeparator();
					
					AddChildAction aca = new AddChildAction( 
							dragElement, 
							dropElement );

					if ( popup.getComponentCount() == 0 ) {
						aca.actionPerformed( null );	// Direct action
					} else {

						JMenuItem item = new JMenuItem( 
								aca );
						popup.add( item );
						
					}
				} 

				if ( popup.getComponentCount() > 0 ) {
					popup.show( currentDropComponent, 0, 0 );
				}
			}
			
			currentDragComponent = null;
			currentDropComponent = null;
			acceptedImport = false;
		}

		public int getSourceActions( JComponent c ) {
			return MOVE;
		}

		public boolean importData( JComponent comp, Transferable t ) {
			return super.importData( comp, t );
		}

	}

}
