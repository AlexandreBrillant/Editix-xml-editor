package com.japisoft.editix.ui.panels;

import java.util.Iterator;
import java.util.StringTokenizer;

import javax.swing.Action;

import com.japisoft.editix.action.panels.PanelAction;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.editix.ui.PanelStateListener;
import com.japisoft.editix.ui.PanelStateManager;
import com.japisoft.framework.application.descriptor.ActionModel;
import com.japisoft.framework.preferences.Preferences;
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
public class PanelManager {

	public static void showByAction( String actionName ) {
		Panel p = getPanelByAction( actionName );
		if ( p != null )
			p.showPanel();
	}

	public static void hideByAction( String actionName ) {
		Panel p = getPanelByAction( actionName );
		if ( p != null )
			p.hidePanel();		
	}

	public static void initByAction( String actionName ) {
		Panel p = getPanelByAction( actionName );
		if ( p != null )
			p.init();		
	}

	// Save state for the 'init' method
	public static void saveState( boolean resetState ) {
		Iterator it = ActionModel.getActionsName();
		StringBuffer state = new StringBuffer();
		while ( it.hasNext() ) {
			String actionName = ( String )it.next();
			Action a = ActionModel.restoreAction( actionName );
			if ( ( a instanceof PanelAction ) && 
					( ( PanelAction ) a ).isPrepared() ) {
				( ( PanelAction )a ).preparePanel().stop();
				if ( state.length() > 0 )
					state.append( ";" );
				state.append(
						actionName ).append( 
							"=" ).append( 
								( ( PanelAction )a ).preparePanel().isShown() );
				if ( resetState )
					( ( ( PanelAction )a ).preparePanel() ).setState( false );
			}
		}
		Preferences.setPreference( 
				Preferences.SYSTEM_GP,
				"panelmanager.state",
				state.toString() );
		if ( shownPanel != null )
			Preferences.setPreference( 
				Preferences.SYSTEM_GP,
				"panelmanager.shownpanel",
				shownPanel );		
	}

	private static String shownPanel;
	
	public static void saveShownState( String id ) {
		shownPanel = id;
	}

	public static void restoreState() {
		String oldState = Preferences.getPreference( 
				Preferences.SYSTEM_GP,
				"panelmanager.state",
				( String )null );
		if ( oldState != null ) {
			StringTokenizer st = new StringTokenizer(
					oldState, ";", false );
			while ( st.hasMoreTokens() ) {
				String t = st.nextToken();
				int i = t.indexOf( "=" );
				String actionName = t.substring( 0, i );
				boolean state = "true".equals( t.substring( i + 1 ) );
				if ( state )
					showByAction( actionName );
			}
		}
		shownPanel = Preferences.getPreference( 
				Preferences.SYSTEM_GP,
				"panelmanager.shownpanel",
				(String)null );		
		if ( shownPanel != null ) {
			EditixFrame.THIS.dockingSpace.showPane( shownPanel );
		}
	}

	public static Panel getPanelByAction( String actionName ) {
		Action a = ActionModel.restoreAction( actionName );
		if ( a instanceof PanelAction ) {
			return ( ( PanelAction )a ).preparePanel();
		}
		return null;
	}

	public static Panel getShownPanelByAction( String actionName ) {
		Action a = ActionModel.restoreAction( actionName );
		if ( a instanceof PanelAction ) {
			PanelAction pa = ( PanelAction )a;
			if ( pa.isPrepared() ) {
				return pa.preparePanel();
			}
		}
		return null;
	}

	private static void resetCurrentXMLContainerForPanels( XMLContainer container ) {
		Iterator it = ActionModel.getActionsName();
		while ( it.hasNext() ) {
			String actionName = ( String )it.next();
			Action a = ActionModel.restoreAction( actionName );
			if ( ( a instanceof PanelAction ) && 
					( ( PanelAction ) a ).isPrepared() ) {
				( ( ( PanelAction )a ).preparePanel() ).setCurrentXMLContainer( 
						container );
			}
		}
	}

	static {
		PanelStateManager.addPanelStateListener( new RawPanelStateListener() );
	}

	static class RawPanelStateListener implements PanelStateListener {
		public void newPath(String previousPath, String newPath) {
		}
		public void setCurrentXMLContainer(XMLContainer container) {
			resetCurrentXMLContainerForPanels( container );
		}
		public void close(XMLContainer container) {
		}
	}

}
