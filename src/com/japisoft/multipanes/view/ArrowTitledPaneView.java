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

package com.japisoft.multipanes.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.UIManager;

import javax.swing.JButton;
import javax.swing.JComponent;

import com.japisoft.multipanes.MultiPanes;
import com.japisoft.multipanes.TitledPane;
import com.japisoft.multipanes.TitledPaneView;

/**
 * Here a view showing right arrow and down arrow. 
 * UIManager properties : 
 * <pre>
 * - multipanes.arrowTitledPaneView.rightArrowColor
 * - multipanes.arrowTitledPaneView.downArrowColor
 * - multipanes.arrowTitledPaneView.rightArrowIcon
 * - multipanes.arrowTitledPaneView.downArrowIcon
 * </pre>
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public class ArrowTitledPaneView implements TitledPaneView {
	
	private MultiPanes panes;
	
	public void init( MultiPanes panes ) {
		this.panes = panes;
		
		if ( UIManager.getColor(
			"multipanes.arrowTitledPaneView.rightArrowColor" ) == null )
			UIManager.put(
					"multipanes.arrowTitledPaneView.rightArrowColor", Color.black );

		if ( UIManager.getColor(
			"multipanes.arrowTitledPaneView.downArrowColor" ) == null )
			UIManager.put(
					"multipanes.arrowTitledPaneView.downArrowColor", Color.gray );
		
		if ( UIManager.getIcon( "multipanes.arrowTitledPaneView.rightArrowIcon" ) == null ) {
			UIManager.put( "multipanes.arrowTitledPaneView.rightArrowIcon", new ArrowRight() );
		}

		if ( UIManager.getIcon( "multipanes.arrowTitledPaneView.downArrowIcon" ) == null ) {
			UIManager.put( "multipanes.arrowTitledPaneView.downArrowIcon", new ArrowDown() );
		}				
	}
	
	public JComponent buildPanelHeader( TitledPane pane ) {
		return DefaultTitledPaneView.updateButtonForUIManager( panes, pane, new JButton( new ButtonAction( pane ) ), false );
	}

	public void updateView( JComponent headerView, TitledPane pane, boolean openedState ) {		
		for ( int i = 0; i < panes.getComponentCount(); i++ ) {
			if ( panes.getComponent( i ) instanceof JButton ) {
				JButton btn = ( JButton )panes.getComponent( i );
				if ( btn.getAction() instanceof ButtonAction ) {
					ButtonAction ba = ( ButtonAction )btn.getAction();
					if ( ba.pane == pane ) {
						ba.openedState = openedState;
						ba.putValue( Action.SMALL_ICON, ba.getValue( Action.SMALL_ICON ) );
						DefaultTitledPaneView.updateButtonForUIManager( panes, pane, btn, openedState );
						break;
					}
				}
			}
		}
	}

	class ButtonAction extends AbstractAction implements PropertyChangeListener {

		private TitledPane pane;

		public ButtonAction( TitledPane pane ) {
			this.pane = pane;
			pane.addPropertyChangeListener( this );
			setEnabled( pane.isEnabled() );
			openedState = ( pane == panes.getOpenedTitledPane() );
		}

		boolean openedState = false;
		
		public Object getValue(String key) {
			if ( Action.NAME == key )
				return pane.getTitle();
			if ( Action.SMALL_ICON == key )
				return openedState ? UIManager.getIcon( "multipanes.arrowTitledPaneView.downArrowIcon" ) : 
					UIManager.getIcon( "multipanes.arrowTitledPaneView.rightArrowIcon" );
			if ( Action.SHORT_DESCRIPTION == key )
				return pane.getToolTip();
			return null;
		}

		public void actionPerformed(ActionEvent e) {
			if ( panes.getOpenedTitledPane() == pane ) {
				panes.close( pane );
			}
			else {
				panes.open( pane );
			}
		}

		public void propertyChange(PropertyChangeEvent evt) {
			if ( "enabled".equals( evt.getPropertyName() ) )
				setEnabled( ( ( Boolean )evt.getNewValue() ).booleanValue() );
			else
				putValue( evt.getPropertyName(), evt.getNewValue() );
		}

	}

	static class ArrowRight implements Icon {	
		public int getIconHeight() {
			return 20;
		}
		public int getIconWidth() {
			return 20;
		}
		
		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.setColor( UIManager.getColor(
			"multipanes.arrowTitledPaneView.rightArrowColor" ) );
			g.fillPolygon(
				new int[] { 4, 16, 4 },
				new int[] { 8, 14, 20 }, 3 );
		}
	}
	
	static class ArrowDown implements Icon {
		
		public int getIconHeight() {
			return 20;
		}
		
		public int getIconWidth() {
			return 20;
		}
		
		public void paintIcon( Component c, Graphics g, int x, int y ) {
			g.setColor( UIManager.getColor(
			"multipanes.arrowTitledPaneView.downArrowColor" ) );
			g.fillPolygon(
				new int[] { 4, 16, 10 },
				new int[] { 8, 8, 20 }, 3 );
		}
	}
	
}

