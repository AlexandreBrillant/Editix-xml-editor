package com.japisoft.framework.dockable;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToolBar;
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
public class BasicStatusBar extends JToolBar {

	public BasicStatusBar() {
		super();
		setFloatable( false );
	}

	public void prepare( JDock dock ) {
		prepare( dock, false );
	}
	
	public void prepare( JDock dock, boolean reset ) {
		if ( reset )
			removeAll();
		JComponent c = dock.getDockingView();
		ArrayList known = new ArrayList();
		
		for ( int i = 0; i < c.getComponentCount(); i++ ) {
			Component c2 = c.getComponent( i );
			Windowable w = dock.componentToWindowable( c2 );
			if ( w != null ) {
				addWindowable( dock, w );
				known.add( w.getId() );
			}
		}

		// Add for hidden windows
		
		if ( dock.hasHiddenWindows() ) {
			Enumeration enume = 
				dock.hiddenWindows();
			while ( enume.hasMoreElements() ) {
				Windowable w = ( Windowable )enume.nextElement();
				addWindowable( dock, w );
				//known.add( w.getId() );
			}
		}
		
		for ( int i = 0; i < getComponentCount(); i++ ) {
			JButton btn = ( JButton )getComponent( i );
			HideShowAction a = ( HideShowAction )btn.getAction();
			resetButtonUI( btn, !known.contains( a.id ) );
		}
	}

	void addWindowable( JDock dock, Windowable w ) {
		
		// Search for this windowable

		JButton btn = getButtonById( w.getId() );

		if ( btn == null ) {
			HideShowAction action = 
				new HideShowAction( w.getId(), dock );
			if ( w.getIcon() != null )
				action.putValue( Action.SMALL_ICON, w.getIcon() );
			if ( w.getTitle() != null )
				action.putValue( Action.SHORT_DESCRIPTION, w.getTitle() );
			action.putValue( Action.NAME, w.getId() );
	
			btn = ( JButton )add( action );
			
			if ( dock.isUIReady() ) {
				dock.getView().invalidate();
				dock.getView().validate();
				invalidate();
				validate();
				repaint();
			}
		}
	}

	void resetButtonUI( JButton button, boolean hidden ) {
		
		if ( button != null ) {
			if ( hidden ) {
				button.setBackground( Color.gray );
			}
			else
				button.setBackground( UIManager.getColor( "button.background" ) );
		}
	}

	public void dispose() {
		removeAll();
	}
	
	private JButton getButtonById( String id ) {
		for ( int i = 0; i < getComponentCount(); i++ ) {
			JButton btn = ( JButton )getComponent( i );
			HideShowAction a = ( HideShowAction )btn.getAction();
			if ( id.equals( a.id ) )
				return btn;
		}
		return null;
	}

	class HideShowAction extends AbstractAction {
		
		private String id;
		private JDock dock;

		public HideShowAction( String id, JDock dock ) {
			this.id = id;
			this.dock = dock;
		}

		public void actionPerformed( ActionEvent e ) {
			if ( dock.isInnerWindowHidden( id ) ) {
				dock.showInnerWindow( id );				
			} else {
				dock.hideInnerWindow( id );			
			}
		}
	}

}
