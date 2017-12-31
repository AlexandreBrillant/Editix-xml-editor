package com.japisoft.xmlpad.action;

import com.japisoft.xmlpad.*;
import com.japisoft.xmlpad.editor.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.swing.Action;
import javax.swing.Icon;
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
public class ActionModel implements ActionSet {

	private static Vector model = new Vector();

	/////////////////////////

	/** File group */
	public static String FILE_GROUP = "file";

	/** Edit group */
	public static String EDIT_GROUP = "edit";

	/** Search group */
	public static String SEARCH_GROUP = "search";

	/** XML group */
	public static String XML_GROUP = "xml";

	/** Other group */
	public static String OTHER_GROUP = "other";

	/** Tree group */
	public static String TREE_GROUP = "tree";

	/** Toolkit group */
	public static String TOOLKIT_GROUP = "toolkit";

	static {
		Enumeration enume = XMLPadProperties.getProperties();
		Hashtable htGroup = new Hashtable();

		while (enume.hasMoreElements()) {
			String mKey = (String) enume.nextElement();

			if (mKey.startsWith("action.")) {
				int i = mKey.indexOf(".", 7);
				if (i > -1) {
					String mGroup = mKey.substring(7, i);
					String order = mKey.substring(i + 1);
					try {
						int ii = Integer.parseInt(order) - 1;
						ActionGroup a = (ActionGroup) htGroup.get(mGroup);
						if (a == null) {
							a = new ActionGroup(mGroup);
							htGroup.put(mGroup, a);
						}
						String mClass = XMLPadProperties.getProperty(mKey, "");
						try {
							Class mActionCl = Class.forName(mClass);
							int j = ii;
							while (a.size() - 1 < j) {
								a.add(null);
							}
							a.setElementAt(mActionCl.newInstance(), ii);
						} catch (Throwable th) {
							th.printStackTrace();
						}
					} catch (NumberFormatException exc) {
					}
				}
			}
		}

		String order = XMLPadProperties.getProperty("groupOrder",
				"file,edit,search,xml");
		StringTokenizer st = new StringTokenizer(order, ",");
		while (st.hasMoreTokens()) {
			String grp = st.nextToken();
			ActionGroup a = (ActionGroup) htGroup.get(grp);
			if (a != null)
				addGroup(a);
		}

		if (model.size() == 0) {
			try {

				// Add Default Action
				addActionForGroup(FILE_GROUP, NEW_ACTION);
				addActionForGroup(FILE_GROUP, LOAD_ACTION);
				addActionForGroup(FILE_GROUP, SAVE_ACTION);
				addActionForGroup(FILE_GROUP, SAVEAS_ACTION);
				addActionForGroup(FILE_GROUP, INSERT_ACTION);

				addActionForGroup(EDIT_GROUP, UNDO_ACTION);
				addActionForGroup(EDIT_GROUP, REDO_ACTION);
				addActionForGroup(EDIT_GROUP, CUT_ACTION);
				addActionForGroup(EDIT_GROUP, COPY_ACTION);
				addActionForGroup(EDIT_GROUP, PASTE_ACTION);
				//				addActionForGroup(EDIT_GROUP,FAST_COMMENT_ACTION);

				addActionForGroup(SEARCH_GROUP, SEARCH_ACTION);

				addActionForGroup(XML_GROUP, PARSE_ACTION);
				addActionForGroup(XML_GROUP, FORMAT_ACTION);
				addActionForGroup(XML_GROUP, COMMENT_ACTION);

				addActionForGroup(OTHER_GROUP, SPLIT_ACTION);
				addActionForGroup(OTHER_GROUP, SPLIT_ACTION_HOR);

				addActionForGroup(ActionModel.TREE_GROUP,
						TREE_SELECTNODE_ACTION);
				addActionForGroup(ActionModel.TREE_GROUP,
						TREE_COMMENTNODE_ACTION);

				addActionForGroup(ActionModel.TREE_GROUP, TREE_CUTNODE_ACTION);
				addActionForGroup(ActionModel.TREE_GROUP, TREE_COPYNODE_ACTION);
				addActionForGroup(ActionModel.TREE_GROUP, TREE_PREVIOUS_ACTION);
				addActionForGroup(ActionModel.TREE_GROUP, TREE_NEXT_ACTION);
				addActionForGroup(ActionModel.TREE_GROUP,
						TREE_ADDHISTORY_ACTION);
				addActionForGroup(ActionModel.TREE_GROUP,
						TREE_CLEANHISTORY_ACTION);
				addActionForGroup(ActionModel.TREE_GROUP, TREE_EDITNODE_ACTION);

			} catch (ClassNotFoundException exc) {
				exc.printStackTrace();
			}
		}
	}

	/** Build the toolBar by adding all available action (in a toolbarable state) */
	public static void buildToolBar(JToolBar toolBar) {
		Enumeration group = ActionModel.getGroups();
		while (group.hasMoreElements()) {
			ActionGroup ag = (ActionGroup) group.nextElement();
			boolean included = false;
			for (int i = 0; i < ag.size(); i++) {
				Action a = (Action) ag.get(i);
				if (!(a instanceof XMLAction && !((XMLAction) a)
						.isToolbarable())) {
					toolBar.add(a);
					included = true;
				}
			}
			if (group.hasMoreElements() && included)
				toolBar.addSeparator();
		}
	}

	/** Build the toolBar by adding all available action (in a toolbarable state) */
	public static void buildPopupMenu(JPopupMenu popupMenu) {
		Enumeration group = ActionModel.getGroups();
		while (group.hasMoreElements()) {
			ActionGroup ag = (ActionGroup) group.nextElement();
			boolean included = false;
			for (int i = 0; i < ag.size(); i++) {
				Action a = (Action) ag.get(i);
				if (!(a instanceof XMLAction && !((XMLAction) a).isPopable())) {
					popupMenu.add(a);
					included = true;
				}
			}
			if (group.hasMoreElements() && included)
				popupMenu.addSeparator();
		}
	}

	static boolean enabledAutoResetActionState = true;

	/**
	 * Decide or not to reset the action model with the last XMLContainer
	 * getting the focus. By default <code>true</code>
	 */
	public static void setEnabledAutoResetActionState(boolean enabled) {
		enabledAutoResetActionState = enabled;
	}

	/** Replace an action icon by this one */
	public static void setIconForAction(String name, javax.swing.Icon icon) {
		Action a = getActionByName(name);
		if (a != null) {
			a.putValue(Action.SMALL_ICON, icon);
		}
	}

	/**
	 * @deprecated Use only the version with the XMLContainer as parameter Reset
	 *             the current Action state for this editor and this container,
	 *             thus action will work on the good editor.
	 * @param editor
	 * @param container
	 */
	public static void resetActionState(XMLEditor editor, XMLContainer container) {
		if (!enabledAutoResetActionState)
			return;
		for (int i = 0; i < model.size(); i++) {
			((ActionGroup) model.get(i)).resetActionState(editor, container);
		}
	}

	/**
	 * Reset the current Action state for this editor and this container
	 * 
	 * @param container
	 */
	public static void resetActionState(XMLContainer container) {
		if (!enabledAutoResetActionState)
			return;
		for (int i = 0; i < model.size(); i++) {
			if (container != null)
				( ( ActionGroup )model.get(i)).resetActionState(
						container.getEditor(), 
						container );
			else
				( ( ActionGroup )model.get(i)).resetActionState(
						null, 
						null);
		}
	}

	/** Insert a new group of actions */
	public static void addGroup(ActionGroup group) {
		model.add(group);
	}

	/** @return <code>true</code> if this group name is known */
	public static boolean hasGroup(String name) {
		for (int i = 0; i < model.size(); i++) {
			if (model.get(i) instanceof ActionGroup) {
				ActionGroup ag = (ActionGroup) model.get(i);
				if (name.equals(ag.getName()))
					return true;
			}
		}
		return false;
	}

	/** Remove a group of action */
	public static void removeGroup(ActionGroup group) {
		model.remove(group);
	}

	/**
	 * @return an actions group for a name. <code>null</code> is returned for
	 *         unknown group
	 */
	public static ActionGroup getGroupByName(String groupName) {
		for (int i = 0; i < model.size(); i++) {
			ActionGroup ag = (ActionGroup) model.get(i);
			if (ag.getName().equals(groupName))
				return ag;
		}
		return null;
	}

	/**
	 * Add this action 'a' for the followed groupName. If the groupName is not
	 * known then the action a is added to a new ActionGroup getting the
	 * groupName.
	 * 
	 * @param groupName
	 *            existing group name
	 * @param a
	 *            XML action
	 */
	public static void addActionForGroup(String groupName, XMLAction a) {
		for (int i = 0; i < model.size(); i++) {
			ActionGroup ag = (ActionGroup) model.get(i);
			if (groupName.equals(ag.getName())) {
				ag.addAction(a);
				return;
			}
		}
		addGroup(new ActionGroup(groupName));
		addActionForGroup(groupName, a);
	}

	/**
	 * Add a new action for this groupName. If the action is not found a
	 * ClassNotFoundException will be thrown
	 */
	public static void addActionForGroup(String groupName, String actionClass)
			throws ClassNotFoundException {
		try {
			addActionForGroup(groupName, (XMLAction) Class.forName(actionClass)
					.newInstance());
		} catch (Throwable th) {
			th.printStackTrace();
			throw new ClassNotFoundException("Can't load " + actionClass);
		}
	}

	/**
	 * Remove this action 'a' from the following groupName. If no action group
	 * is found then then nothing is done.
	 * 
	 * @param groupName
	 * @param a
	 */
	public static void removeActionForGroup(String groupName, XMLAction a) {
		for (int i = 0; i < model.size(); i++) {
			ActionGroup ag = (ActionGroup) model.get(i);
			if (groupName.equals(ag.getName())) {
				ag.removeAction(a);
				return;
			}
		}
	}

	/** Remove an action by its name */
	public static void removeActionByName(String name) {
		for (int i = 0; i < model.size(); i++) {
			if (model.get(i) instanceof ActionGroup) {
				ActionGroup ag = (ActionGroup) model.get(i);
				ag.removeAction(name);
			}
		}
	}
	
	/** @return current action by name */
	public static Action getActionByName(String name) {
		for (int i = 0; i < model.size(); i++) {
			ActionGroup mAg = (ActionGroup) model.get(i);
			Action a = mAg.getActionByName(name);
			if (a != null)
				return a;
		}
		return null;
	}

	/** Replace an action matching the name by the newAction */
	public static void replaceActionByName(String name, Action newAction) {
		for (int i = 0; i < model.size(); i++) {
			ActionGroup mAg = (ActionGroup) model.get(i);
			Action a = mAg.getActionByName(name);
			if (a != null) {
				int idx = mAg.indexOf(a);
				mAg.set(idx, newAction);
				break;
			}
		}
	}

	/** Enabled/Disabled an action by its name */
	public static void setEnabledAction(String name, boolean enabled) {
		Action a = getActionByName(name);
		if (a != null)
			a.setEnabled(enabled);
	}

	/**
	 * @return the current action state. If the action is not know it will
	 *         return false
	 */
	public static boolean isEnabledAction(String name) {
		Action a = getActionByName(name);
		if (a == null)
			return false;
		return a.isEnabled();
	}

	/**
	 * Active an action by its name
	 * 
	 * @return <code>true</code> is the action is possible else false if
	 *         something is wrong
	 */
	public static boolean activeActionByName(String name) {
		Action a = getActionByName(name);
		if (a != null) {
			if (a instanceof XMLAction) {
				return ((XMLAction) a).notifyAction();
			} else {
				a.actionPerformed(null);
			}
		}
		return false;
	}

	/**
	 * Active an action changing the current container and editor this these
	 * ones. The previous XMLContainer and XMLEditor for each action is
	 * maintained at the end of the processing.
	 * 
	 * @return <code>true</code> is the action is possible else false if
	 *         something is wrong
	 */
	public static boolean activeActionByName(String name,
			XMLContainer container, XMLEditor editor) {
		Action a = getActionByName(name);
		if (a instanceof XMLAction) {
			boolean res = false;
			XMLAction xa = (XMLAction) a;
			XMLContainer oldContainer = xa.getXMLContainer();
			XMLEditor oldEditor = xa.getXMLEditor();
			try {
				xa.setXMLContainer(container);
				xa.setXMLEditor(editor);
				res = xa.notifyAction();
			} finally {
				xa.setXMLContainer(oldContainer);
				xa.setXMLEditor(oldEditor);
			}
			return res;
		} else if (a != null) {
			a.actionPerformed(null);
			return true;
		} else
			return false;
	}

	/**
	 * Enabled/Disabled all action implementing the ActicateOnSelection
	 * interface
	 */
	public static void activeActionForSelection(boolean selection) {
		for (int i = 0; i < model.size(); i++) {
			ActionGroup ag = (ActionGroup) model.get(i);
			for (int j = 0; j < ag.size(); j++) {
				Action a = (Action) ag.get(j);
				if (a instanceof ActivateOnSelection) {
					a.setEnabled(selection);
				}
			}
		}
	}

	/** @return available groups */
	public static Enumeration getGroups() {
		return model.elements();
	}

	/** Set a feature for this action name */
	public static void setFeature(String actionName, String featureName,
			boolean enabled) {
		Action a = getActionByName(actionName);
		if (a instanceof XMLAction)
			((XMLAction) a).setFeature(featureName, enabled);
	}

	/** Check a feature for this action name */
	public static boolean hasFeature(String actionName, String featureName) {
		Action a = getActionByName(actionName);
		if (a == null)
			return false;
		if (a instanceof XMLAction) {
			return ((XMLAction) a).hasFeature(featureName);
		} else
			return false;
	}

	/** Set this param for the action related to the actionName */
	public static void setParam(String actionName, Object param) {
		Action a = getActionByName(actionName);
		if (a instanceof XMLAction) {
			((XMLAction) a).setParam(param);
		}
	}

	/** @return the current param for this action or <code>null</code> */
	public static Object getParam(String actionName) {
		Action a = getActionByName(actionName);
		if (a instanceof XMLAction)
			return ((XMLAction) a).getParam();
		return null;
	}

	/**
	 * Reset a property value by calling setProperty on the action known by
	 * actionName. If the action is not found, this method will has no effect
	 * 
	 * @param actionName
	 *            XMLAction name
	 * @param propertyName
	 *            Property name
	 * @param value
	 *            Property value
	 */
	public static void setProperty(String actionName, String propertyName,
			Object value) {
		Action a = getActionByName(actionName);

		if (a instanceof XMLAction)
			((XMLAction) a).setProperty(propertyName, value);
	}

	/**
	 * @return a property value by calling getProperty on the action known by
	 *         the actionName. If the actionName is invalid a <code>null</code>
	 *         value will be returned
	 */
	public static Object getProperty(String actionName, String propertyName,
			Object defaultValue) {
		Action a = getActionByName(actionName);
		if (a instanceof XMLAction)
			return ((XMLAction) a).getProperty(propertyName, defaultValue);
		return defaultValue;
	}

	/** Reset the default icon for this actionName by this one */
	public static void setIcon(String actionName, Icon newIcon) {
		Action a = getActionByName(actionName);
		if (a != null)
			a.putValue(Action.SMALL_ICON, newIcon);
	}

//@@	
	static int CHECKED = 0;

	static void testDay() {
		Date d = new Date();
		File home1 = new File(System.getProperty("user.home"));
		if (!home1.exists()) {
			CHECKED = 45;
			return;
		}

		Date lastDate = null;
		int delta = 0;

		new File(home1, ".xp12").delete();
		new File(home1, ".xp12b").delete();		
		new File(home1, ".xp11").delete();
		new File(home1, ".xp11b").delete();		
		new File(home1, ".xp9").delete();
		new File(home1, ".xp9b").delete();		
		new File(home1, ".xp2").delete();
		new File(home1, ".xp2b").delete();
		new File(home1, ".xp3").delete();
		new File(home1, ".xp3b").delete();
		new File(home1, ".xp4").delete();
		new File(home1, ".xp5").delete();
		new File(home1, ".xp5b").delete();
		new File(home1, ".xp6").delete();
		new File(home1, ".xp6b").delete();
		new File(home1, ".xp7").delete();
		new File(home1, ".xp7b").delete();
		
		try {
			// Read previous file
			ObjectInputStream obj = new ObjectInputStream(new FileInputStream(
					new File(home1, ".xp13")));
			try {
				lastDate = (Date) obj.readObject();
			} finally {
				obj.close();
			}
		} catch (Throwable th) {
		}

		if (lastDate == null) { // Store it
			try {
				ObjectOutputStream obj = new ObjectOutputStream(
						new FileOutputStream(new File(home1, ".xp13" ) ) );
				lastDate = d;
				obj.writeObject(lastDate);
			} catch (Throwable th) {
				CHECKED = 45;
				return;
			}
		}

		CHECKED = (int) (d.getTime() - lastDate.getTime())
				/ (1000 * 60 * 60 * 24);
		if (CHECKED < 0)
			CHECKED = 45;

		File f2 = new File(home1, ".xp13b");

		if (!f2.exists()) {
			try {
				FileOutputStream output = new FileOutputStream(f2);
				output.write(CHECKED);
				output.close();
			} catch (Throwable th) {
			}
		}

		try {
			FileInputStream lastO = new FileInputStream(f2);
			int lastCHECKED = lastO.read();
			lastO.close();
			if (lastCHECKED < CHECKED) {
				FileOutputStream output = new FileOutputStream(f2);
				output.write(CHECKED);
				output.close();
			} else
				CHECKED = lastCHECKED;
		} catch (Throwable th) {
		}
	}

/*	
	static {

		try {
			testDay();
		} catch (Throwable th) {
			// Applet
		}

		if (CHECKED >= 45) {
			char[] _ = new char[43];
			_[0] = 69;
			_[1] = 110;
			_[2] = 100;
			_[3] = 32;
			_[4] = 111;
			_[5] = 102;
			_[6] = 32;
			_[7] = 101;
			_[8] = 118;
			_[9] = 97;
			_[10] = 108;
			_[11] = 117;
			_[12] = 97;
			_[13] = 116;
			_[14] = 105;
			_[15] = 111;
			_[16] = 110;
			_[17] = 32;
			_[18] = 58;
			_[19] = 32;
			_[20] = 104;
			_[21] = 116;
			_[22] = 116;
			_[23] = 112;
			_[24] = 58;
			_[25] = 47;
			_[26] = 47;
			_[27] = 119;
			_[28] = 119;
			_[29] = 119;
			_[30] = 46;
			_[31] = 106;
			_[32] = 97;
			_[33] = 112;
			_[34] = 105;
			_[35] = 115;
			_[36] = 111;
			_[37] = 102;
			_[38] = 116;
			_[39] = 46;
			_[40] = 99;
			_[41] = 111;
			_[42] = 109;

			System.out.println( new String( _ ) );
			try {
				System.exit( 0 );
			} catch (Throwable th) {
				try {
					Thread.sleep(Long.MAX_VALUE);
				} catch (Throwable th2) {
				}
			}
		}
	}
*/
//@@
}
