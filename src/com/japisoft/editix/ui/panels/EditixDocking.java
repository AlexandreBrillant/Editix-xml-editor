package com.japisoft.editix.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.UIManager;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.dockable.InnerWindowProperties;
import com.japisoft.framework.dockable.JDock;
import com.japisoft.framework.dockable.JDockEvent;
import com.japisoft.framework.dockable.JDockListener;
import com.japisoft.framework.dockable.action.BasicActionModel;
import com.japisoft.framework.dockable.action.common.CloseAction;
import com.japisoft.multipanes.BasicTitledPane;
import com.japisoft.multipanes.MultiPanes;
import com.japisoft.multipanes.TitledPane;
import com.japisoft.multipanes.TitledPaneEvent;
import com.japisoft.multipanes.TitledPaneListener;
import com.japisoft.multipanes.view.ActionTitledPaneView;

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
public class EditixDocking extends JDock implements JDockListener, TitledPaneListener {
	private MultiPanes mp = null;
	
	public void setPane( String id, String title, Icon i, JComponent component ) {
		if ( mp == null ) {
			
			/** Remove this comment for coloring the arrow */
			UIManager.put(
			"multipanes.arrowTitledPaneView.rightArrowColor", Color.LIGHT_GRAY );
			UIManager.put(
	        "multipanes.arrowTitledPaneView.downArrowColor", Color.BLACK );		
			
			mp = new MultiPanes();

			try {
				mp.setDefaultSelectedTitledPaneFont(
					UIManager.getFont( "Label.font").deriveFont( Font.BOLD ) );
			} catch( Throwable th ) {}

			Action a = new AbstractAction() {
				public void actionPerformed( ActionEvent e ) {
					PanelManager.hideByAction( e.getActionCommand() );
				}
			};

			a.putValue(
					Action.SMALL_ICON,
					new ImageIcon( 
							ClassLoader.getSystemResource( "images/window_delete.png" ) ) ); 

			mp.addTitledPaneListener( this );
			mp.setView( new ActionTitledPaneView( a ) );
			mp.setPreferredSize( new Dimension( 200, 100 ) );
			addInnerWindow( 
					new InnerWindowProperties( 
							"mp",
							"Panels",
							null,
							BasicActionModel.getInstance( new CloseAction() ),
							mp ), BorderLayout.WEST );
		}
		mp.getModel().addTitledPane(
				new BasicTitledPane( id, title, i, title, component ) );
	}

	public boolean hasPane( String id ) {
		return ( mp != null && mp.getModel().getTitledPaneByName( id ) != null ) ||
			( hiddenPane != null && hiddenPane.containsKey( id ) );
	}
	
	public void showPane( String id ) {
		try {
			if ( isInnerWindowHidden( "mp" ) )
				showInnerWindow( "mp" );
			// Restore it
			if ( isHiddenPane( id ) ) {
				TitledPane tp = ( TitledPane )hiddenPane.get( id );
				mp.getModel().addTitledPane( tp );
				hiddenPane.remove( id );
			}
			if ( mp != null ) {
				mp.open( id );
				
				int currentWidth = mp.getWidth();
				if ( mp.getModel().getTitledPaneByName( id ) != null ) {
					Dimension dim = mp.getModel().getTitledPaneByName( id ).getView().getPreferredSize();
					if ( currentWidth < dim.width ) {
						resize( "mp", dim.width - currentWidth, 0 );
					}
				}

			}
			PanelManager.saveShownState( id );
			
			
		} catch (RuntimeException e) {
			ApplicationModel.debug( e );
		}
	}

	private boolean restoringState = false;
	
	protected void setUIReady(boolean added) {
		if ( added ) {
			restoringState = true;
			PanelManager.restoreState();
			addJDockListener( this );
			restoringState = false;
		}
	}

	public boolean isHiddenPanes() {
		return isInnerWindowHidden( "mp" );
	}

	public void hidePane( String id ) {
		if ( hiddenPane == null )
			hiddenPane = new HashMap();
		// Store it for reshowing it
		if ( mp == null )
			return;
		TitledPane tp = mp.getModel().getTitledPaneByName( id );
		if ( tp == null )
			return;	// ?
		hiddenPane.put( id, tp );
		mp.getModel().removeTitledPane( tp );		
		if ( mp.getModel().getTitledPaneCount() == 0 )
			hidePanes();
		else {
			// Show to open another one
			mp.open( mp.getModel().getTitledPaneAt( 0 ).getName() );
		}
	}

	public boolean isHiddenPane( String id ) {
		return ( hiddenPane != null ) && 
					hiddenPane.containsKey( id );
	}

	private HashMap hiddenPane;
	
	public void hidePanes() {
		hideInnerWindow( "mp" );
	}

	public void jdockAction(JDockEvent event) {
		if ( event.getType() == JDockEvent.INNERWINDOW_HIDDEN ) {
			if ( "mp".equals( event.getId() ) ) {
				PanelManager.saveState( true );
				PanelManager.saveShownState( null );
			}					
		}
	}

	public void closed(TitledPaneEvent event) {		
	}
	public void opened(TitledPaneEvent event) {
		PanelManager.saveShownState( event.getSelectedTitledPane().getName() );
	}

	
}
