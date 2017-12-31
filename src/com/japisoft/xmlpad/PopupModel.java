package com.japisoft.xmlpad;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.Action;

import com.japisoft.xmlpad.action.ActionGroup;
import com.japisoft.xmlpad.action.ActionModel;
import com.japisoft.xmlpad.action.TreeAction;
import com.japisoft.xmlpad.action.XMLAction;

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
public class PopupModel {
	private final Object SEPARATOR = null;
	private Vector content;
	private XMLContainer container;

	public PopupModel( XMLContainer container ) {
		super();
		content = new Vector();
		this.container = container;
	}

	/** Only when the container is disposed. User shouldn't call it */
	public void dispose() {
		container = null;
	}

	private Vector vListeners = null;

	/** Add a new listener for the popup update */
	public void addPopupModelListener( PopupModelListener listener ) {
		if (vListeners == null)
			vListeners = new Vector();
		vListeners.add(listener);
	}

	/** Remove a listener for the toolbar update */
	public void removePopupModelListener( PopupModelListener listener) {
		if (vListeners != null)
			vListeners.remove(listener);
	}

	private boolean cEnableChange = false;

	/** Enable "real time" notification */
	public void setEnabledListener(boolean enable) {
		this.cEnableChange = enable;
		if (enable)
			fireNotification();
	}

	// Change notification
	private void fireNotification() {
		for (int i = 0; i < vListeners.size(); i++) {
			PopupModelListener listener =
				(PopupModelListener) vListeners.get(i);
			listener.updateActions(content);
		}
	}

	/** @return the number of action */
	public int size() {
		return content.size();
	}

	/** Add a new action. User can add new XMLAction here. In the last case
	 * a notifyContainer will be called with the current XMLContainer */
	public void addAction(Action a) {
		content.add(a);
		if ( a instanceof XMLAction ) {
			( ( XMLAction )a ).setXMLContainer( container );
			( ( XMLAction )a ).setXMLEditor( container.getEditor() );
		}
		if (cEnableChange)
			fireNotification();
	}

	/** Remove an action or XMLAction. I the last case, the current XMLContainer and
	 * XMLEditor will be disposed calling <code>dispose</code> on the XMLAction
	 * @param a Action to be removed
	 * @param definitly Call the <code>dispose</code> method on XMLAction if <code>true</code>
	 *  */
	public void removeAction(Action a, boolean definitly ) {
		content.remove(a);		
		if ( definitly ) {
				if ( a instanceof XMLAction ) {
					( ( XMLAction )a ).dispose();
				}
		} 
		if (cEnableChange)
			fireNotification();
	}

	public void removeAction(Action a ) {
		removeAction( a, false );
	}

	/** Insert an action for the location */
	public void insertActionAt(Action a, int location) {
		content.insertElementAt(a, location);
		if (cEnableChange)
			fireNotification();
	}

	/** @return an action for the location */
	public Action getActionAt(int location) {
		return (Action) content.elementAt(location);
	}

	/** @return true if an action is available for the location */
	public boolean isAction(int location) {
		return !isSeparator(location);
	}

	/** Add a separator */
	public void addSeparator() {
		content.add(SEPARATOR);
		if (cEnableChange)
			fireNotification();
	}

	/** Insert a separator at the location */
	public void insertSeparatorAt(int location) {
		content.insertElementAt(SEPARATOR, location);
		if (cEnableChange)
			fireNotification();
	}

	/** Remove a separator at the location */
	public void removeSeparator(int location) {
		if (content.get(location) == SEPARATOR)
			content.removeElementAt(location);
		if (cEnableChange)
			fireNotification();
	}

	/** @return true if a separator is available for the location */
	public boolean isSeparator(int location) {
		return (content.get(location) == SEPARATOR);
	}

	/** Reset the popup model to include all action (Popable) from the
	 * <code>ActionModel</code> */
	public static void resetPopupModel( PopupModel model ) {
		Enumeration group = ActionModel.getGroups();
		while ( group.hasMoreElements() ) {
			ActionGroup ag = ( ActionGroup ) group.nextElement();
			boolean included = false;
			for (int i = 0; i < ag.size(); i++) {
				Action a = ( Action ) ag.get( i );
				if ( !( ( a instanceof XMLAction ) && !( ( XMLAction )a ).isPopable() ) ) {
					model.addAction(a);
					included = true;
				}
			}
			if ( group.hasMoreElements() && included )
				model.addSeparator();
		}
	}

	/** Reset this popup model to include all tree popable action from the
	 * <code>ActionModel</code> tree group. */
	public static void resetTreePopupModel( PopupModel model ) {
		ActionGroup group = ActionModel.getGroupByName( ActionModel.TREE_GROUP );
		if ( group == null )
			return;
		for (int i = 0; i < group.size(); i++) {
			if ( group.get( i ) instanceof TreeAction ) {
				TreeAction a = ( TreeAction ) group.get( i );
				if ( a.isTreePopable() )
					model.addAction( (XMLAction)a );
			}
		}
	}

}
