package com.japisoft.framework.dockable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.UIManager;

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
public class DefaultTitleBar extends JComponent implements
		DockableFrameTitleBar, MouseListener {
	private String title = null;
	private Icon icon = null;
	private int height = 0;
	private int ascent = 0;
	private Color gradientStart;
	private Color gradientStop;
	private Color gradientSelectedStart;
	private Color gradientSelectedStop;
	private Color titleColor;

	public DefaultTitleBar() {
		setFont( new Font( "dialog", Font.PLAIN, 12 ) );
		setLayout( new ButtonLayout() );

		resetColors();

		gradientSelectedStart = UIManager
				.getColor("jdock.innerwindow.gradient.selectedStartColor");
		if (gradientSelectedStart == null)
			gradientSelectedStart = UIManager.getColor("Panel.background");

		gradientSelectedStop = UIManager
				.getColor("jdock.innerwindow.gradient.selectedStopColor");
		if (gradientSelectedStop == null)
			gradientSelectedStop = UIManager.getColor("Panel.background");

		focusMode = true;
	}

	public DefaultTitleBar( String title ) {
		this();
		this.title = title;
	}

	public DefaultTitleBar( String title, Icon icon ) {
		this( title );
		this.icon = icon;
	}

	private void resetColors() {
		gradientStart = UIManager.getColor( "jdock.innerwindow.gradient.startColor" );
		if (gradientStart == null)
			gradientStart = new Color(128, 128, 128);

		gradientStop = UIManager.getColor( "jdock.innerwindow.gradient.stopColor" );
		
		if (gradientStop == null)
			gradientStop = UIManager.getColor( "Panel.background" );

		titleColor = UIManager.getColor( "jdock.innerwindow.titleColor" );
		if (titleColor == null)
			titleColor = Color.BLACK;	
	}

	public void addNotify() {
		super.addNotify();
		addMouseListener(this);
	}

	public void removeNotify() {
		super.removeNotify();
		removeMouseListener(this);
	}

	private boolean focusMode = false;
	private boolean maximizedState = false;

	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			if ( !( getParent() instanceof Windowable ) )
				return;
			Windowable iw = (Windowable) getParent();
			boolean maximizedState = iw.isMaximized();
			if (getParent().getParent() instanceof JDock.InnerPanel) {
				JDock.InnerPanel panel = (JDock.InnerPanel) getParent()
						.getParent();
				panel.getJDock().maximizedRestoredInnerWindow(iw.getId());
			}			
		}
		
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		if ( !( getParent() instanceof BasicInnerWindow ) )
			return;
		BasicInnerWindow parent = (BasicInnerWindow) getParent();
		boolean ok = false;
		if (parent.getView() instanceof JScrollPane) {
			JScrollPane sp = (JScrollPane) parent.getView();
			if (sp.getViewport().getView() != null) {
				sp.getViewport().getView().requestFocus();
				ok = true;
			}
		}
		if (!ok)
			parent.getView().requestFocus();
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void focusMode( boolean focused ) {
		if (focusMode == focused)
			return;
			
		focusMode = focused;
		repaint();
		if (focusMode) {

			// Send event
			if ( getParent() instanceof Windowable ) {
				Windowable biw = ( Windowable )getParent();
				biw.fireDockEvent( biw.getId(), JDockEvent.INNERWINDOW_SELECTED );
			}

			if (DockManager.LAST_FOCUS != null
					&& DockManager.LAST_FOCUS != this) {
				DockManager.LAST_FOCUS.focusMode(false);
			}
			DockManager.LAST_FOCUS = this;
		}
	}

	public void setTitle(String title) {
		this.title = title;
		repaint();
	}

	public String getTitle() {
		return title;
	}

	public void setIcon( Icon icon ) {
		this.icon = icon;
		repaint();
	}

	public Icon getIcon() {
		return icon;
	}

	/** Reset the color for the background header. Use a <code>null</code>
	 * value for restoring the initial value */
	public void setBackground( Color color ) {
		if ( color == null ) {
			resetColors();
		} else {	
			if ( gradientStop == gradientSelectedStart )
				gradientStop = color;
			gradientStart = color;
		}
		repaint();
	}
	
	/** Reset the color for the foreground header. Use a <code>null</code>
	 * value for restoring the initial value */
	public void setForeground( Color color ) {
		if ( color == null )
			resetColors();
		else 
			titleColor = color;
		repaint();
	}

	public void addSeparator() {
		JLabel btn = null;
		add( btn = new JLabel( new SeparatorImage() ) );
		btn.setPreferredSize( new Dimension( 8, 16 ) );
	}

	public void addButton(JButton button) {
		add(button);
		button.setPreferredSize( new Dimension( 16, 16 ) );
	}

	public void addButton(JButton button, boolean undecorated ) {
		if ( undecorated ) {
			button.setBorderPainted( false );
			button.setBorder( null );
		}
		add( button );
	}

	public void removeAllButtons() {
		removeAll();
	}

	public void prepare() {
		invalidate();
		validate();
		repaint();
	}

	public Dimension getPreferredSize() {
		Dimension dim = super.getPreferredSize();
		if (height == 0) {
			FontMetrics fm = getFontMetrics(getFont());
			height = fm.getHeight() + 6;
			ascent = fm.getDescent();
		}
		if (dim == null)
			dim = new Dimension(1, height);
		else
			dim.height = height;
		return dim;
	}

	public void paintComponent( Graphics gc ) {
		super.paintComponent(gc);
		Graphics2D gc2 = (Graphics2D) gc;

		gc2.setRenderingHint( 
			RenderingHints.KEY_TEXT_ANTIALIASING, 
			RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB 
		);		
		
		Color start = gradientStart;
		Color stop = gradientStop;

		if (focusMode) {
			start = gradientSelectedStart;
			stop = gradientSelectedStop;
		}

		GradientPaint gradient = new GradientPaint(0, 0, start, getWidth() / 2,
				getHeight(), stop);
		gc2.setPaint(gradient);
		gc2.fillRect(0, 0, getWidth(), getHeight());
		gc2.setColor(titleColor);

		if ( icon != null ) {
			icon.paintIcon( this, gc2, 2, ( getHeight() - icon.getIconHeight() ) / 2 );
		}

		if (title != null ) {
			gc2.drawString(title, icon == null ? 10 : icon.getIconWidth() + 5, getHeight() / 2 + ascent + 2);
		}

		// gc2.drawRect(1, 1, getWidth() - 2, getHeight() - 3);
	}

	public JComponent getView() {
		return this;
	}

	/////////////////////////////////////////////////////////////

	class SeparatorImage implements Icon {
		public int getIconHeight() {
			return 16;
		}

		public int getIconWidth() {
			return 16;
		}

		public void paintIcon( Component c, Graphics g, int x, int y ) {
			g.setColor( Color.white );
			g.drawLine( 2, 2, 2, 14 );
			g.setColor( Color.gray );
			g.drawLine( 3, 2, 3, 14 );
		}
	}

}
