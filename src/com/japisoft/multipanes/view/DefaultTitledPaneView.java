package com.japisoft.multipanes.view;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
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
public class DefaultTitledPaneView implements TitledPaneView {

	private MultiPanes panes;

	public void init( MultiPanes panes ) {
		this.panes = panes;
	}
	
	public JComponent buildPanelHeader( TitledPane pane ) {
		return updateButtonForUIManager( panes, pane, new JButton( new ButtonAction( pane ) ), false );
	}

	/** Update the view due to opening / closing new state
	 * @param pane The panel
	 * @param openedState the opening / closing status */
	public void updateView( JComponent headerView, TitledPane pane, boolean openedState ) {
		for  (int i = 0; i < panes.getComponentCount(); i++ ) {
			Component c = panes.getComponent( i );
			if ( c instanceof JButton ) {
				JButton b = ( JButton )c;
				if ( b.getAction() instanceof ButtonAction ) {
					ButtonAction ba = ( ButtonAction )b.getAction();
					if ( ba.pane == pane ) {
						updateButtonForUIManager( panes, pane, b, openedState );
						break;
					}
				}
			}
		}
	}

	static JButton updateButtonForUIManager( MultiPanes panes, TitledPane pane, JButton button, boolean selected ) {

		if ( selected ) {
			boolean background = false;
			boolean foreground = false;

			if ( panes.getDefaultSelectedTitledPaneBackground() != null ) {
				button.setBackground( panes.getDefaultSelectedTitledPaneBackground() );
				background = true;
			}

			if ( panes.getDefaultSelectedTitledPaneFont() != null ) {
				if ( panes.getDefaultTitledPaneFont() == null )
					panes.setDefaultTitledPaneFont(
							button.getFont() );
				button.setFont( panes.getDefaultSelectedTitledPaneFont() );				
			}

			if ( panes.getDefaultSelectedTitledPaneForeground() != null ) {
				button.setForeground( panes.getDefaultSelectedTitledPaneForeground() );
				foreground = true;
			}
				
			if ( !background && pane.getBackground() != null ) {
				button.setBackground( pane.getBackground() );
				background = true;
			}
			if ( !foreground && pane.getForeground() != null ) {
				button.setForeground( pane.getForeground() );
				foreground = true;
			}
			
			if ( !background && panes.getDefaultTitledPaneBackground() != null )
				button.setBackground( panes.getDefaultTitledPaneBackground() );

			if ( !foreground && panes.getDefaultTitledPaneForeground() != null )
				button.setForeground( panes.getDefaultTitledPaneForeground() );
						
		} else {

			boolean background = false;
			boolean foreground = false;
			
			if ( panes.getDefaultTitledPaneFont() != null ) {
				button.setFont(
						panes.getDefaultTitledPaneFont() );
			}
			
			if ( pane.getBackground() != null ) {
				button.setBackground( pane.getBackground() );
				background = true;
			}

			if ( pane.getForeground() != null ) {
				button.setForeground( pane.getForeground() );
				background = true;
			}
			
			if ( !background && panes.getDefaultTitledPaneBackground() != null ) {
				button.setBackground( panes.getDefaultTitledPaneBackground() );
				background = true;
			}

			if ( !foreground && panes.getDefaultTitledPaneForeground() != null ) {
				button.setForeground( panes.getDefaultTitledPaneForeground() );
				foreground = true;
			}

			if ( !background ) {
				button.setBackground( UIManager.getColor( "Button.background" ) );
			}
			
			if ( !foreground ) {
				button.setForeground( UIManager.getColor( "Button.foreground" ) );
			}

		}

		return button;
	}
	
	class ButtonAction extends AbstractAction implements PropertyChangeListener {

		private TitledPane pane;

		public ButtonAction( TitledPane pane ) {
			this.pane = pane;
			pane.addPropertyChangeListener( this );
			setEnabled( pane.isEnabled() );
		}

		public Object getValue(String key) {
			if ( Action.NAME == key )
				return pane.getTitle();
			if ( Action.SMALL_ICON == key )
				return pane.getIcon();
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

}
