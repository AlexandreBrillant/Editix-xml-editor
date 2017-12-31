package com.japisoft.multipanes.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.UIManager;

import com.japisoft.multipanes.MultiPanes;
import com.japisoft.multipanes.TitledPane;
import com.japisoft.multipanes.TitledPaneView;

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
public class ActionTitledPaneView implements TitledPaneView {
	
	private MultiPanes mp;
	private Action action;
	
	/** This action should contains an icon, each time it will be pressed the
	 * actionPerformed of this action will be called and the actionComment will contain
	 * the TitledPane name
	 */
	public ActionTitledPaneView( Action action ) {
		this.action = action;
	}

	public void init( MultiPanes mp ) {
		this.mp = mp;
	}	
	
	public JComponent buildPanelHeader( TitledPane pane ) {
		return new InnerComponent( pane );
	}

	public void updateView( 
			JComponent headerView, 
			TitledPane pane, 
			boolean openedState ) {
		if ( headerView != null ) {
			( ( InnerComponent )headerView ).update( pane );
			if ( openedState ) {
				if ( mp.getDefaultSelectedTitledPaneFont() != null )
					headerView.setFont( mp.getDefaultSelectedTitledPaneFont() );
			} else {
					headerView.setFont( UIManager.getFont( "Label.font" ) );
				
			}
		}
	}

	class InnerComponent extends JComponent implements MouseListener {

		private InnerComponent( TitledPane pane ) {
			update( pane );
		}

		private void update( TitledPane pane ) {
			this.id = pane.getName();
			this.label = pane.getTitle();
			this.mainIcon = pane.getIcon();
			this.labelWidth = getFontMetrics( 
					UIManager.getFont( "Label.font") ).stringWidth( label );
			this.setBackground( pane.getBackground() );
			this.setForeground( pane.getForeground() );
			setToolTipText( pane.getToolTip() );
		}

		protected void paintComponent( Graphics g ) {
			super.paintComponent( g );
			
			Graphics2D g2 = (Graphics2D) g;

			g2.setRenderingHint( 
				RenderingHints.KEY_TEXT_ANTIALIASING, 
				RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB 
			);
			
			Color tmp = g.getColor();
			g.setColor( Color.GRAY );
			g.draw3DRect( 1, 1, getWidth() - 2, getHeight() - 2, true );
			g.setColor( tmp );
			if ( label != null )
				g.drawString( label, ( getWidth() - labelWidth ) / 2, ( ( getHeight() - getFontMetrics( getFont() ).getHeight() ) / 2 ) + getFontMetrics( getFont() ).getAscent() );
			if ( mainIcon != null ) {
				mainIcon.paintIcon( this, g, 10, ( getHeight() - mainIcon.getIconHeight() ) / 2 );
			}
			if ( action != null ) {
				Icon i = ( Icon )action.getValue( Action.SMALL_ICON );
				if ( i != null )
					i.paintIcon( this, g, getWidth() - 10 - i.getIconWidth(), ( getHeight() - i.getIconHeight() ) / 2 );
			}
		}

		public Dimension getPreferredSize() {
			return new Dimension( 1, 30 );
		}

		private Icon mainIcon;
		private int labelWidth;
		private String label;
		private String id;
		
		public void addNotify() {
			super.addNotify();
			addMouseListener( this );
		}
		
		public void removeNotify() {
			super.removeNotify();
			removeMouseListener( this );
		}		
		
		public void mouseClicked(MouseEvent e) {
			// Check for the action location
			Icon i = ( Icon )action.getValue( Action.SMALL_ICON );
			if ( i != null ) {
				Rectangle r = 
					new Rectangle( 
							getWidth() - 10 - i.getIconWidth(), 
							( getHeight() - i.getIconHeight() ) / 2,
							i.getIconWidth(),
							i.getIconHeight() );
				if ( r.contains(
						e.getX(),
						e.getY() ) ) {
					action.actionPerformed(
							new ActionEvent( 
									this, 
									0,
									id ) );
					return;
				}
			}			
			mp.open( id );
		}
		public void mouseEntered(MouseEvent e) {
		}
		public void mouseExited(MouseEvent e) {
		}
		public void mousePressed(MouseEvent e) {
		}
		public void mouseReleased(MouseEvent e) {
		}		
	}

}
