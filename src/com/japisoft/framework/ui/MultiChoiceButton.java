package com.japisoft.framework.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

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
public class MultiChoiceButton extends JButton implements ActionListener {

	public MultiChoiceButton( Action a ) {
		super( a );
		setBorderPainted( false );
	}
	
	public void addNotify() {
		super.addNotify();
		addActionListener( this );
	}
	
	public void removeNotify() {
		super.removeNotify();
		removeActionListener( this );
	}

	private Insets finalInsets = null;
	
	public Insets getInsets() {
		if ( finalInsets != null )
			return finalInsets;
		Insets insets = super.getInsets();
		insets.right = insets.right + ICON.getIconWidth();
		return ( finalInsets = insets );
	}
	
	private void showPopup( JComponent component, Action a ) {
		JPopupMenu popup = new JPopupMenu();
		for ( int i = 1; i <= 20; i++ ) {
			if ( a.getValue( "label" + i ) != null ) {
				String lbl = ( String )a.getValue( "label" + i );
				String cmd = ( String )a.getValue( "cmd" + i );

				JMenuItem item = new JMenuItem( a );
				item.setText( lbl );
				item.setActionCommand( cmd );
				
				if ( a.getValue( "icon" + i ) != null )
					item.setIcon( ( Icon )a.getValue( "icon" + i ) );
				
				popup.add( item );
			} else
				break;
		}
		popup.show( component, 0, component.getHeight() );
	}
	
	public void paintComponent( Graphics gc ) {
		super.paintComponent( gc );
		ICON.paintIcon( this, gc, 0, 0 );
	}
			
	static EmptyIcon ICON = new EmptyIcon();
	
	/** Empty icon for alignment on popup */
	public static class EmptyIcon extends Object implements Icon {
		private final int height;
		private final int width;
		
		public EmptyIcon() {
			height = 10;
			width = 10;
		}

		public int getIconHeight() {
			return height;
		}

		public int getIconWidth() {
			return width;
		}

		private Polygon p;
		
		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.setColor( Color.BLACK );
			if ( p == null ) {
				p = new Polygon(
						new int[] { 
								1, 1 + ( width / 2 ), 1 + width
						},
						new int[] {
								height / 4, ( 3 * height ) / 4, height / 4
						}, 3 );
				p.translate( x + c.getWidth() - width - 4, y + ( c.getHeight() - height ) / 2 );
			} 
			g.fillPolygon( p );
		}
	}
	
	public void actionPerformed( ActionEvent e ) {
		showPopup( this, getAction() );
	}
	
	public static void main( String[] args ) {
		AbstractAction a = new AbstractAction() {
			public void actionPerformed( ActionEvent e ) {}
		};
		a.putValue( Action.NAME, "TEST" );
		
		a.putValue( "label1", "Test 1" );
		a.putValue( "label2", "Test 2" );
		a.putValue( "cmd1", "CMD 1" );
		a.putValue( "cmd2", "CMD 2" );
		
		JFrame f = new JFrame();
		JToolBar t = new JToolBar();
		t.add( new MultiChoiceButton( a ) );
		f.getContentPane().add( t, BorderLayout.NORTH );
		f.setSize( 100, 100 );
		f.setVisible( true );
	}
	
}
