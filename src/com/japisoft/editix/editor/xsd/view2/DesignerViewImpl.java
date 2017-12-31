package com.japisoft.editix.editor.xsd.view2;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import org.w3c.dom.Element;

import com.japisoft.editix.editor.xsd.Factory;
import com.japisoft.editix.editor.xsd.toolkit.SchemaHelper;
import com.japisoft.editix.editor.xsd.view.XSDSelectionListener;
import com.japisoft.editix.editor.xsd.view.View;
import com.japisoft.editix.editor.xsd.view2.node.XSDNode;
import com.japisoft.editix.editor.xsd.view2.node.XSDNodeImpl;
import com.japisoft.editix.editor.xsd.view2.nodeview.XSDNodeView;

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
public class DesignerViewImpl extends JComponent implements View, MouseListener, MouseMotionListener, ActionListener {

	private XSDNode node = null;
	private Factory factory = null;

	private ImageIcon refIcon = null;
	private ImageIcon typeIcon = null;
	private ImageIcon substitutionIcon = null;	

	public DesignerViewImpl( Factory factory  ) {
		super();
		this.factory = factory;		
		setTransferHandler( new NodeDragDropHandler() );

		refIcon = new ImageIcon(
			getClass().getResource( 
				"ref.png" )
		);

		typeIcon = new ImageIcon(
			getClass().getResource( 
				"type.png" )
		);
		
		substitutionIcon = new ImageIcon(
			getClass().getResource( 
				"substitution.png" )				
		);
	}

	@Override
	public void addNotify() {
		super.addNotify();
		addMouseListener( this );
		addMouseMotionListener( this );
	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		removeMouseListener( this );
		removeMouseMotionListener( this );
	}

	private XSDSelectionListener selectionListener;

	public void setCommonSelectionListener( XSDSelectionListener listener ) {
		this.selectionListener = listener;
	}
	
	public void repaintSelection() {
		if ( currentSelection != null ) {
			currentSelection.repaint();
		}
		repaint();
	}
	
	@Override
	public void init( Element schemaNode ) {
		SchemaHelper.unmark( schemaNode );		
		node = new XSDNodeImpl( schemaNode );
		// Select the first by default
		select( node );
		SchemaHelper.dumpMark( schemaNode );
	}
	
	public XSDNode getNode() { return node; }
	
	@Override
	public JComponent getView() {
		return this;
	}
	
	private boolean debugContainer = false;
	
	@Override
	protected void paintComponent( Graphics g ) {
		super.paintComponent( g );
		if ( node != null ) {
			Graphics2D g2d = ( Graphics2D )g;	
			
			maxX = 0;
			maxY = 0;

			Graphics g2 = g.create(0,0,0,0);
			
			int fullHeight = node.getView().getFullHeight( this );
			
			paintNode( 0, fullHeight / 2, node, ( Graphics2D )g2 );

			if ( debugContainer ) {
				g2d.setColor( Color.RED );
				g2d.drawRect( 0, 0, maxX, maxY );
			}
			
			if ( maxY < getParent().getHeight() ) {
				
				maxY = 0;
				
				int deltaY = ( getHeight() - fullHeight ) / 2 + ( ( Integer )node.getData( "view.y" ) / 2 );				
				// paintNode( 0, ( getHeight() - fullHeight ) / 2, node, g2d );
				paintNode( 0, deltaY, node, g2d );
				
			} else {
				
				maxY = 0;
				
				paintNode( 0, fullHeight / 2, node, g2d );
			}			
			
			
			if ( getHeight() != maxY + 10 || ( maxX - 10 ) > getWidth() ) {				
				final int maxY2 = maxY;
				SwingUtilities.invokeLater(
					new Runnable() {
						public void run() {
							setPreferredSize( new Dimension( maxX + 10, maxY2 + 20 ));
							revalidate();							
						}
					} );
			}			
		}
	}

	private void drawOpenCloseIcon( 
			XSDNode node, 
			Graphics2D g2D, int x, int y, int width, int height ) {

		int r = 4;
		g2D.setColor( Color.WHITE );
		g2D.fillRect(
				x + width - r - 1, 
				y + ( height / 2 ) - r, 
				2 * r, 
				2 * r 
		);				
		g2D.setColor( Color.BLACK );
		g2D.drawRect(
			x + width - r - 1, 
			y + ( height / 2 ) - r, 
			2 * r, 
			2 * r 
		);
		g2D.drawLine(
				x + width - r - 1,
				y + ( height / 2 ),
				x + width + r - 1,
				y + ( height / 2 ) );

		if ( node.isOpened() ) {
			g2D.drawLine(
				x + width - 1,
				y + ( height / 2 - r ),
				x + width - 1,
				y + ( height / 2 + r ) );
		}
		
		node.setData( "open.x", x + width - r - 1 );
		node.setData( "open.y", y + ( height / 2 ) - r );
		node.setData( "open.w", 2 * r );
		node.setData( "open.h", 2 * r );		
	}
	
	private int maxX = 0;
	private int maxY = 0;
	
	private int minX = 0;
	private int minY = 0;
	
	public int getMaxX() { return maxX; }
	public int getMaxY() { return maxY; }
	
	public void paintNode( int x, int y, XSDNode node, Graphics2D g2D ) {

		int childrenMargin = 14;
		
		XSDNodeView view = node.getView();
		int width = view.getWidth( this );
		int height =  view.getHeight( this );
		int fullHeight = view.getFullHeight( this );
		
		int maxOccurs = 1;

		Element e = node.getDOM();
		if ( e.hasAttribute( "maxOccurs" ) ) {
			try {
				maxOccurs = Integer.parseInt( e.getAttribute( "maxOccurs" ) );
			} catch( NumberFormatException nfe ) {
				if ( "unbounded".equals( e.getAttribute( "maxOccurs" ) ) ) {
					maxOccurs = Integer.MAX_VALUE;
				}
			}
		}

		if ( maxOccurs > 1 ) {

			Graphics2D g2D2 = ( Graphics2D )g2D.create( x + 3, y + 3, width, height );
			view.paint( g2D2 );
			
			int minOccurs = 1;
			if ( e.hasAttribute( "minOccurs" ) ) {
				try {
					minOccurs = Integer.parseInt( e.getAttribute( "minOccurs" ) );
				} catch( NumberFormatException nfe ) {
				}
			}
			
			g2D.drawString( minOccurs + "..." + ( maxOccurs != Integer.MAX_VALUE ? maxOccurs : "\u221e" ), x + width - 25, y + height + 13 );
			
		}
				
		Graphics2D g2D2 = ( Graphics2D )g2D.create( x, y, width, height );
		view.paint( g2D2 );

		if ( e.hasAttribute( "ref" ) ) {
			refIcon.paintIcon( this, g2D, x + width - ( refIcon.getIconWidth() / 2), y + height - ( refIcon.getIconHeight() / 2 ) );	
		}
		
		if ( e.hasAttribute( "type" ) ) {
			String value = e.getAttribute( "type" );
			if ( !value.startsWith( "xsd:" ) && ! value.startsWith( "xs:" ) ) {
				typeIcon.paintIcon( this, g2D, x + width - ( typeIcon.getIconWidth() / 2), y + height - ( typeIcon.getIconHeight() / 2 ) );				
			}
		}
		
		if ( e.hasAttribute( "substitutionGroup" ) ) {
			substitutionIcon.paintIcon(this, g2D, x + width - ( refIcon.getIconWidth() / 2), y + height - ( refIcon.getIconHeight() / 2 ) );
		}
		
		node.setData( "view.x", x );
		node.setData( "view.y", y );		
		node.setData( "view.w", width );
		node.setData( "view.h", height );

		maxX = Math.max( maxX, x + width );
		maxY = Math.max( maxY, y + height );		

		minX = Math.min( minX, x );
		minY = Math.min( minY, y );		

		if ( node.getChildCount() > 0 || 
				!node.isOpened() ) {
			drawOpenCloseIcon( node, g2D, x, y, width, height );
		}

		int xb = x + width + childrenMargin / 2;
		int yb = y + height / 2;

		if ( node.getChildCount() > 0 )
			g2D.drawLine( x + width, yb, xb, yb );		

		// Paint Children
		
		x += width + childrenMargin;
		y += ( height / 2 );
		y -= ( fullHeight / 2 );
		
		for ( int i = 0;i < node.getChildCount(); i++ ) {
			XSDNode childNode = node.getChildAt( i );
			int fh = childNode.getView().getFullHeight( this );
			y += ( fh - height ) / 2;
			paintNode( x, y, childNode, g2D );
			
			int yb2 = y + childNode.getView().getHeight( this ) / 2; 
			
			g2D.drawLine( 
				xb, 
				yb,
				xb,
				yb2
			);

			g2D.drawLine( 
				xb, 
				yb2,
				xb + childrenMargin / 2,
				yb2
			);
			
			y += childNode.getView().getFullHeight( this ) + childrenMargin;
		}
	}
	
	@Override
	public void dispose() {
	}

	@Override
	public void stopEditing() {
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		getTransferHandler().exportAsDrag( this, e, TransferHandler.MOVE );
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {

		// Avoid conflict with the mouse pressed
		if ( e.isPopupTrigger() )
			return;
		
		boolean mustRepaint = false;

		// Open / Close node
		if ( !( mustRepaint = testClickedOpenClose( node, e.getX(), e.getY() ) ) ) {
			
			// Selection
			
			mustRepaint = testClickedSelection( node, e.getX(), e.getY() );

			if ( e.getClickCount() > 1 ) {
				
				if ( currentSelection != null ) {
				
					if ( currentSelection.getData( "open.x" ) != null ) {
						currentSelection.setOpened( !currentSelection.isOpened() );
						mustRepaint = true;
					}
					
				}
				
			}			
			
		}

		if ( mustRepaint )
			repaint();

	}
		
	private boolean testClickedOpenClose( XSDNode node, int x, int y ) {
		if ( node.getData( "open.x" ) != null ) {
			int x1 = ( Integer )node.getData( "open.x" );
			int y1 = ( Integer )node.getData( "open.y" );
			int x2 = x1 + ( Integer )node.getData( "open.w" );
			int y2 = y1 + ( Integer )node.getData( "open.h" );
			if ( x1 < x && x2 > x && y1 < y && y2 > y ) {
				node.setOpened( !node.isOpened() );
				return true;
			}
		}
		
		boolean openClose = false;
		
		for ( int i = 0;i < node.getChildCount(); i++ ) {
			openClose = openClose || testClickedOpenClose( node.getChildAt( i ), x, y);
		}
		
		return openClose;
	}
	
	private XSDNode currentSelection = null;
	
	private void select( XSDNode node ) {
		node.setSelected( !node.isSelected() );
		if ( node.isSelected() ) {
			if ( selectionListener != null )
				selectionListener.select( node.getDOM() );
			if ( currentSelection != null ) {
				currentSelection.setSelected( false );
			}
			currentSelection = node;
		}		
	}
	
	private boolean testClickedSelection( XSDNode node, int x, int y ) {
		if ( node != currentSelection ) {
			if ( node.getData( "view.x" ) != null ) {
				int x1 = ( Integer )node.getData( "view.x" );
				int y1 = ( Integer )node.getData( "view.y" );
				int x2 = x1 + ( Integer )node.getData( "view.w" );
				int y2 = y1 + ( Integer )node.getData( "view.h" );
				if ( x1 < x && x2 > x && y1 < y && y2 > y ) {
					select( node );
					return true;
				}
			}
		}
		
		boolean selectionFound = false;
		
		for ( int i = 0;i < node.getChildCount(); i++ ) {
			selectionFound = selectionFound || testClickedSelection( node.getChildAt( i ), x, y);
		}
		
		return selectionFound;
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		 
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		maybeShowPopup(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		maybeShowPopup(e);
		dragNode = null;
	}
	
	private void maybeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger() ) {
///        	currentSelection = null;
//        	if ( testClickedSelection( node, e.getX(), e.getY() ) ) {
        		if ( currentSelection != null && currentSelection.isEnabled() ) {
        			// select( currentSelection );
        			ActionPopup ap = new ActionPopup();
        			ap.show( ( JComponent )e.getSource(), e.getX(), e.getY() );
        		}
//        	}
        }
    }	

	public void actionPerformed(ActionEvent e) {
		
		SchemaHelper.unmark( currentSelection.getDOM() );
		
		if ( "delete".equals( e.getActionCommand() ) ) {
			cut();
		} else
		if ( "add".equals( e.getActionCommand() ) ) {
			XSDNode newNode = currentSelection.add( ( ( JMenuItem )e.getSource() ).getText() );
			if ( newNode != null )
				select( newNode );
		} else
		if ( "insert".equals( e.getActionCommand() ) ) {
			XSDNode newNode = currentSelection.insert( ( ( JMenuItem )e.getSource() ).getText() );
			select( newNode );
		} else
		if ( "moveup".equals( e.getActionCommand() ) ) {
			currentSelection.moveUp();		
		} else
		if ( "movedown".equals( e.getActionCommand() ) ) {
			currentSelection.moveDown();
		}

		repaint();
	}

	private Element bufferNode = null;
	
	public void cut() {
		if ( currentSelection != null ) {
			bufferNode = currentSelection.getDOM();
			currentSelection.remove();
			repaint();
		}
	}
	
	public void copy() {
		if ( currentSelection != null ) {
			bufferNode = currentSelection.getDOM();
		}		
	}
	
	public void paste() {
		if ( currentSelection != null ) {
			boolean ok = currentSelection.append( bufferNode );
			if ( !ok ) {
				factory.buildAndShowErrorDialog( "Can't paste this node" );
			} else
				repaint();	
		}
	}

	/////////////////////////////////////////////////////////////////////////////////
	
	class ActionPopup extends JPopupMenu {

		private void fillSubmenu( String action, JMenu parent, Element content ) {
			String[] children = SchemaHelper.getChildrenForElement( content );
			if ( children != null )
				for ( int i = 0; i < children.length; i++ ) {
					if ( i > 0 ) {
						if ( "attribute".equals( children[ i ] ) || 
								"unique".equals( children[ i ] ) )
							parent.addSeparator();
					}

					JMenuItem item3 = new JMenuItem( children[ i ] );
					item3.setActionCommand( action );
					item3.addActionListener( DesignerViewImpl.this );
					parent.add( item3 );
				}
			parent.setEnabled( children != null && children.length > 0 );
		}
		
		ActionPopup() {
			JMenu item1 = new JMenu( "Add child" );
			add( item1 );
			
			fillSubmenu( "add", item1, currentSelection.getDOM() );
			
			JMenu item1bis = new JMenu( "Insert child" );
			add( item1bis );
			
			if ( currentSelection.getParent() != null )
				fillSubmenu( "insert", item1bis, currentSelection.getParent().getDOM());
			
			Element e = currentSelection.getDOM();
			if ( e.hasAttribute( "ref" ) ) {

				Element refFound = SchemaHelper.getAnyByName( 
						e.getOwnerDocument().getDocumentElement(),
						e.getAttribute( "ref" ) );

				if ( refFound != null ) {
				
					JMenuItem item3 = new JMenuItem( "Open Reference" );
					item3.setActionCommand( "openref" );
					item3.addActionListener( DesignerViewImpl.this );
					add( item3 );
					
				}

			}

			if ( e.hasAttribute( "type" ) ) {

				Element type = SchemaHelper.getComplexTypeByName( 
						e.getOwnerDocument().getDocumentElement(),
						e.getAttribute( "type" ) );
				
				if ( type != null ) {
				
					JMenuItem item3 = new JMenuItem( "Open Type" );
					item3.setActionCommand( "opentype" );
					item3.addActionListener( DesignerViewImpl.this );
					add( item3 );
					
				}
								
			}

			JMenuItem item4 = new JMenuItem( "Move up" );
			item4.setActionCommand( "moveup" );
			item4.addActionListener( DesignerViewImpl.this );
			add( item4 );
			if ( SchemaHelper.isFirstElement( currentSelection.getDOM() ) ) {
				item4.setEnabled( false );
			}
			
			JMenuItem item5 = new JMenuItem( "Move down" );
			item5.setActionCommand( "movedown" );
			item5.addActionListener( DesignerViewImpl.this );
			add( item5 );
			if ( SchemaHelper.isLastElement( currentSelection.getDOM() ) ) {
				item5.setEnabled( false );
			}

			JMenuItem item6 = new JMenuItem( "Open All" );
			item6.setActionCommand( "openall" );
			item6.addActionListener( DesignerViewImpl.this );
			add( item6 );
			
			if ( currentSelection.getParent() != null ) {
			
				addSeparator();
			
				JMenuItem item2 = new JMenuItem( "Delete" );
				item2.setActionCommand( "delete" );
				item2.addActionListener( DesignerViewImpl.this );
			
				add( item2 );
				
			}
			
			item1bis.setEnabled( item1bis.getItemCount() > 0 );
		}

	}

	private Element dragNode = null;
	
	class NodeDragDropHandler extends TransferHandler {
		
		protected Transferable createTransferable( JComponent c ) {
			if ( currentSelection == null )
				return null;
			return new TransferableNode( dragNode );
		}

		public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
			
			for ( int i = 0; i < transferFlavors.length; i++ ) {
				if ( transferFlavors[ i ].getRepresentationClass() == Element.class )
					return true;
			}
			return false;
		}

		@Override
		public boolean canImport(TransferSupport info) {
			if ( !info.isDrop() )
				return false;
			DropLocation location = info.getDropLocation();
			Point p = location.getDropPoint();
			if ( testClickedSelection( node, (int)p.getX(), (int)p.getY() ) ) {
				repaint();
				
				boolean ancestor = SchemaHelper.isAncestor( dragNode, currentSelection.getDOM() ); 
				
				// Test if the dragNode is an ancestor of the currentSelection
				if ( ancestor ) {
					return false;
				}
				
			}
			return super.canImport(info);
		}
		
		
		
		public void exportAsDrag(JComponent comp, InputEvent e, int action) {
			if ( e instanceof MouseEvent && dragNode == null ) {
				MouseEvent me = ( MouseEvent )e;
				if ( currentSelection != null ) {
					dragNode = currentSelection.getDOM();
					super.exportAsDrag(comp, e, action);
				}
			}
		}

		public boolean importData(JComponent comp, Transferable t) {
			if ( currentSelection != null ) {
				if ( dragNode != currentSelection.getDOM() ) {
					
					boolean ancestor = SchemaHelper.isAncestor( dragNode, currentSelection.getDOM() );
					if ( ancestor )
						return false;
					
					XSDNode source = ( XSDNode )dragNode.getUserData( "node" );
					if ( currentSelection.append( ( Element )dragNode.cloneNode( true ) ) ) {
						source.remove();
					}
					
					currentSelection.setOpened( true );
					
					repaint();
				}
			}
			dragNode = null;
			return super.importData(comp, t);
		}
		public int getSourceActions(JComponent c) {
			return MOVE;
		}
	}

	public static DataFlavor NODE_FLAVOR = new DataFlavor(
		Element.class,
		"Simple node"
	);

	class TransferableNode implements Transferable {
		DataFlavor flavors[] = { NODE_FLAVOR };

		Element node;

		public TransferableNode(Element node) {
			this.node = node;
		}

		public synchronized DataFlavor[] getTransferDataFlavors() {
			return flavors;
		}

		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return (flavor.getRepresentationClass() == Element.class);
		}

		public synchronized Object getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException, IOException {
			if (isDataFlavorSupported(flavor)) {
				return (Object) node;
			} else {
				throw new UnsupportedFlavorException(flavor);
			}
		}

	}
	
}
