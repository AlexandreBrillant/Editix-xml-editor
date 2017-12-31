package com.japisoft.editix.ui.locationbar;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;

import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.editix.ui.EditixNodeLocationListener;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.XMLContainer;

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
public class EditixNodeLocationBar extends JComponent 
		implements 
			MouseListener, 
			MouseMotionListener {

	private List<FPNode> hierarchy = null;
	private static Color LIGHT = ( Color )EditixApplicationModel.getSharedProperty( "table.background.even.color");
	private static Color LIGHT2 = ( Color )EditixApplicationModel.getSharedProperty( "table.background.even.dark.color" );
	private static Color DARK = Color.BLACK;

	static {
		Color c = UIManager.getColor( "editix.nodelocation.light" );
		if ( c != null ) {
			LIGHT = c;
		}
		c = UIManager.getColor( "editix.nodelocation.light2" );
		if ( c != null ) {
			LIGHT2 = c;
		}
		c = UIManager.getColor( "editix.nodelocation.dark" );
		if ( c != null ) {
			DARK = c;
		}
	}

	private EditixNodeLocationListener listener;
	
	public String getType() {
		if ( listener instanceof XMLContainer ) {
			return ( ( XMLContainer )listener ).getDocumentInfo().getType();
		}
		return "";
	}

	public EditixNodeLocationBar( EditixNodeLocationListener listener ) {
		setPreferredSize( 
			new Dimension( 0, 16 ) 
		);
		this.listener = listener;
		setBorder( null ); 		
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

	public void setCurrentNode( FPNode node ) {
		if ( hierarchy == null ) {
			if ( node != null )
				hierarchy = 
					new ArrayList<FPNode>();
		} else {
			hierarchy.removeAll( hierarchy );
		}
		while ( node != null ) {
			hierarchy.add( 0, node );
			node = node.getFPParent();
		}
		repaint();
	}

	private FontMetrics fm = null;
	
	private int currentSelection = -1;
	
	private void drawHierarchy( Graphics g ) {
		if ( ( hierarchy != null ) && 
				( hierarchy.size() > 0 ) ) {

			int maxWidth = ( getWidth() / hierarchy.size() );

			int currentX = 0;
			int i = 0;
			boolean selection = false;
 
			for ( int j = 0; j < hierarchy.size(); j++ ) {
				
				FPNode node = hierarchy.get( j );
				
				if ( node.isText() )
					break;
				
				String name = LabelFactory.getInstance().getLabel( getType() , node );

				int rectWidth = 0;
				
				if ( name != null ) {
					rectWidth =
						fm.stringWidth( name );					
					rectWidth += 5;
				}

				int rx = currentX + 1;
				int ry = 1;
				int rw = rectWidth - 1;
				int rh = ( getHeight() - 2 );

				selection = false;
				
				if ( ( currentMouseX >= rx && currentMouseX <= ( rx + rw ) ) && 
						( currentMouseY >= ry && currentMouseY <= ( ry + rh ) ) ) {
					if ( currentSelection != i ) {
						setToolTipText( 
							node.getXPathLocation() );
					}
					currentSelection = i; 
					g.setColor( Color.GRAY );
					g.drawRoundRect( rx, ry, rw, rh, 4, 4 );
					selection = true;
				}

				if ( j == hierarchy.size() - 1 ) {
					g.setColor( 
							LIGHT2 );				
				} else {
					g.setColor( 
							LIGHT );				
				}

				if ( !selection ) {
					g.setColor( Color.WHITE );
					g.drawRoundRect( rx, ry, rw, rh, 4, 4 );
				}
				
				g.setColor( LabelFactory.getInstance().getColor( getType() , node ) );				

				g.fillRoundRect(
					rx,
					ry,
					rw,
					rh,
					4,
					4
				);

				Graphics2D g2 = ( Graphics2D )g;

				g2.setRenderingHint( 
						RenderingHints.KEY_TEXT_ANTIALIASING, 
						RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB 
				);				
				
				if ( name != null ) {	
					int y = ( getHeight() - fm.getDescent() );
					g.setColor( Color.BLACK );
					g.drawString( 
						name,
						( currentX + 2 ),
						y
					);
					currentX += 
						( rectWidth );
					
					i++;
				}
			}
		}
		
	}
	
	@Override
	protected void paintComponent( Graphics g ) {
		super.paintComponent( g );		
		try {
			if ( fm == null ) {
				fm = 
					getFontMetrics( 
						getFont() 
					);
			}
			drawHierarchy( g );
		} catch( Throwable th ) {}
		
	}

	public void dispose() {
		hierarchy = null;
		listener = null;
	}

	public void mouseDragged(MouseEvent e) {
	}
	
	private int currentMouseX = -1, currentMouseY = -1;

	public void mouseMoved(MouseEvent e) {
		if ( ( hierarchy != null ) && 
				( hierarchy.size() > 0 ) ) {
			currentMouseX = 
				e.getX();
			currentMouseY = 
				e.getY();
			repaint();
		} else {
			currentMouseX = -1;
			currentMouseY = -1;
			currentSelection = -1;			
		}
	}

	public void mouseClicked(MouseEvent e) {
		if ( currentSelection >= 0 )
			listener.gotoNode( 
				hierarchy.get( currentSelection ) );
	}
	
	private FPNode getCurrentSelection() {
		if ( currentSelection >= 0 )
			return hierarchy.get( currentSelection );
		return null;
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
		currentMouseX = -1;
		currentMouseY = -1;
		currentSelection = -1;
	}

	private void showPopup( MouseEvent e ) {
		if ( e.isPopupTrigger() ) {
			CustomPopupMenu m = new CustomPopupMenu();
			m.show( e );
		}
	}
	
	public void mousePressed(MouseEvent e) {
		showPopup( e );
	}

	public void mouseReleased(MouseEvent e) {
		showPopup( e );		
	}

	class CustomPopupMenu extends JPopupMenu implements ActionListener {

		private FPNode currentNode = null;
		
		public void show( MouseEvent e ) {
			if  ( e.isPopupTrigger() ) {
				JMenuItem item = new JMenuItem( "Go to" );
				item.setActionCommand( "goto" );
				item.addActionListener( this );
				add( item );
				addSeparator();
				item = new JMenuItem( "Select" );
				item.addActionListener( this );
				item.setActionCommand( "select" );
				add( item );
				item = new JMenuItem( "Copy" );
				item.setActionCommand( "copy" );
				item.addActionListener( this );
				add( item );
				item = new JMenuItem( "Cut" );
				item.setActionCommand( "cut" );
				item.addActionListener( this );
				add( item );
				currentNode = getCurrentSelection();
				show( 
					EditixNodeLocationBar.this, 
					e.getX(), 
					e.getY() 
				);
			}					
		}

		@Override
		public void hide() {
			super.hide();
			currentNode = null;
		}

		public void actionPerformed( ActionEvent e ) {
			String cmd = e.getActionCommand();
			if ( "goto".equals( cmd ) ) {
				listener.gotoNode( currentNode );
			} else
			if ( "select".equals( cmd ) ) {
				listener.selectNode( currentNode );
			} else
			if ( "copy".equals( cmd ) ) {
				listener.copyNode( currentNode );
			} else
			if ( "cut".equals( cmd ) ) {
				listener.cutNode( currentNode );
			}
			currentNode = null;
		}
		
	}
	
}
