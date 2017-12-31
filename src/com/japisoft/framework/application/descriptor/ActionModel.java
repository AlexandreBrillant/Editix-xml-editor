package com.japisoft.framework.application.descriptor;

import java.awt.event.ActionEvent;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.Icon;

import com.japisoft.framework.actions.SynchronizableAction;
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
public class ActionModel {
	private static Hashtable htAction = null;

	/** Default name for the open action */
	public final static String OPEN = "open";

	/** Default name for the close all action */
	public final static String CLOSE_ALL = "closeAll";

	/** Default name for saving a project */
	public final static String SAVE_PROJECT = "prjSave";

	/** Default name for saving a project in a directory */
	public final static String SAVEAS_PROJECT = "prjSaveAs";

	/** Default name for saving a file in a directory */
	public final static String SAVEAS = "saveAs";

	/** Default name for saving a file */
	public final static String SAVE = "save";

	/** Default name for saving all the file */
	public final static String SAVE_ALL = "saveAll";

	public static boolean LAST_ACTION_STATE = false;

	/** Store an action in the model. The id is the name of the action */
	public static void storeAction(String id, Action a) {
		if (htAction == null)
			htAction = new Hashtable();
		htAction.put(id, a);
	}

	/** Restore an action from its name (id) */
	public static Action restoreAction(String id) {
		if (htAction == null)
			return null;
		return ( Action ) htAction.get(id);
	}

	/** Check if this action name is known */
	public static boolean hasAction(String id) {
		if (htAction == null)
			return false;
		return htAction.containsKey(id);
	}

	/** Enabled/Disabled the following action by its name (id) */
	public static void setEnabled(String id, boolean state) {
		Action a = restoreAction(id);
		if (a == null) {
			throw new RuntimeException("Unknown action " + id);
		} else
			a.setEnabled(state);
	}

	/**
	 * Check the Enabled/Disabled state for the following action by its name
	 * (id)
	 */
	public static boolean isEnabled(String id) {
		Action a = restoreAction(id);
		if (a == null) {
			throw new RuntimeException("Unknown action " + id);
		}
		return a.isEnabled();
	}

	/** Run an action with the following name and event */
	public static void activeActionById(String id, ActionEvent e) {
		if (htAction != null && htAction.containsKey(id)) {
			Action a = (Action) htAction.get(id);
			LAST_ACTION_STATE = false;
			a.actionPerformed(e);
		}
	}

	/**
	 * Run an action with the following name and event and a param available
	 * from the "param" key (getValue)
	 */
	public static void activeActionById(String id, ActionEvent e, String param) {
		LAST_ACTION_STATE = true;
		if (htAction != null && htAction.containsKey(id)) {
			Action a = (Action) htAction.get(id);
			a.putValue("param", param);
			try {
				a.actionPerformed(e);
			} finally {
				a.putValue("param", null);
			}
		}
	}

	/**
	 * Run an action with the following name and event and two params available
	 * from the "param" and "param2" keys (getValue)
	 */
	public static void activeActionById(
			String id, 
			ActionEvent e,
			String param1, 
			String param2 ) {
		LAST_ACTION_STATE = true;
		if (htAction != null && htAction.containsKey(id)) {
			Action a = (Action) htAction.get(id);
			a.putValue("param", param1);
			if (param2 != null)
				a.putValue("param2", param2);
			a.putValue( "param3", null );
			try {
				a.actionPerformed(e);
			} finally {
				a.putValue("param", null);
			}
		}
	}

	/**
	 * Run an action with the following name and event and two params available
	 * from the "param" and "param2" keys (getValue)
	 */
	public static void activeActionById(String id, ActionEvent e,
			String param1, String param2, String param3) {
		LAST_ACTION_STATE = true;
		if ( htAction != null && 
				htAction.containsKey(id)) {
			Action a = (Action) htAction.get(id);
			a.putValue("param", param1);
			if (param2 != null)
				a.putValue("param2", param2);
			if (param3 != null)
				a.putValue("param3", param3);
			try {
				a.actionPerformed(e);
			} finally {
				a.putValue("param", null);
			}
		}
	}

	public static Icon getIconActionById( String id ) {
		Action a = restoreAction( id );
		if ( a == null )
			return null;
		return (Icon)a.getValue( Action.SMALL_ICON );
	}
	
	public static Iterator getActionsName() {
		return htAction.keySet().iterator();
	}
	
	/** Notify action implementing the SynchronizableAction that they must 
	 * change their inner state
	 */
	public static void synchronizeState( Object context ) {
		Iterator it = getActionsName();
		while ( it.hasNext() ) {
			String action = ( String )it.next();
			Action a = ( Action )htAction.get( action );
			if ( a instanceof SynchronizableAction ) {
				( ( SynchronizableAction )a ).synchronizeState( context );
			}
		}
	}

}
