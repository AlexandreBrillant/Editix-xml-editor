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

package com.japisoft.xmlpad.action;

import com.japisoft.xmlpad.SharedProperties;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.editor.XMLEditor;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import java.util.Hashtable;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

/**
 * Abstract class for XML action.
 * <p>
 * The XMLAction class is shared between serveral <code>XMLContainers</code>. Each time an
 * XMLContainers take the focus, all XMLAction are resetted for taking into account the
 * current XMLContainer. This is possible with the <code>setXMLContainer</code> method. You can force
 * the usage of an XMLContainer calling <code>ActionModel.resetActionState</code>
 * </p> 
 * 
 * <p>This action takes an XMLContainer and XMLEditor context for working. You can invoke
 * an action by calling <code>notifyAction</code>. The icon is found from the current classpath 
 * searching for an "Action" class name an "Action16.gif" or "Action24.gif" depending on the IMAGE_SIZE value. 
 * Otherwise, user can have its own icon location by overriding the <code>getDefaultIcon</code> method.</p>
 * <p>
 * If you don't wish to use .gif image, please call <code>XMLAction.IMAGE_EXT = EXT</code>
 * </p>
 * <p>
 * If your action works only on the real time tree, you must implement the <code>TreeAction</code> interface or inherit
 * from the <code>com.japisoft.xmlpad.tree.action.AbstractTreeAction</code>
 * </p>
 * <p>
 * You can change or traduce default label and tooltip using Properties file (from PropertyResourceBundle spec). 
 * This property file is found using the getName() value. So if you have a com.MyAction class, the property file
 * will be found in the com/MyAction.properties path from the classpath. Use a LABEL key and a TOOLTIP key... like :
 * <pre><code>
 * LABEL=myActionName
 * TOOLTIP=This is ...
 * MNEMONIC=A
 * ACCELERATOR=ctrl A
 * ICON=com/japisoft/xmlpad/action/edit/CopyAction16.gif
 * GROUP=Edit
 * </code></pre>
 * </p>
 * <p>
 * To override a default action descriptor. Create a file from the default action name and inserts it before in your classpath the
 * xmlpad.jar. For instance if you want to change the label of the NewAction, create a 
 * com/japisoft/xmlpad/action/new/NewAction.properties with a LABEL key or change the default one inside the 
 * xmlpad.jar
 * </p>
 * <p>
 * When adding an action in an existing group, you can override the getActionGroup method to match the good group or
 * called setActionReferenceGroup to use the same group than the action name argument :
 * <pre> 
 * <code>
 * class MyAction extends XMLAction {
 *		public MyAction() {
 *			super();
 *		}
 *		public String getPopupGroup() { return "Edit"; }
 * }
 * 
 * or
 * 
 * class MyAction extends XMLAction {
 * 	public MyAction() {
 * 		setActionReferenceGroup( ActionModel.CUT_ACTION );
 * 	}
 * }
 * </code>
 * </pre>
 * </p>
 * <p>
 * <pre>
 *  UIManager properties : 
 * - xmlpad.action.[FULL ACTION CLASS NAME].mnemonic (a string)
 * - xmlpad.action.[FULL ACTION CLASS NAME].accelerator(a string)
 * - xmlpad.action.[FULL ACTION CLASS NAME].label
 * - xmlpad.action.[FULL ACTION CLASS NAME].tooltip
 * - xmlpad.action.[FULL ACTION CLASS NAME].icon
 * </pre>
 * </p>
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.7 */
public abstract class XMLAction
	extends AbstractAction
	implements Features, Properties {
	/** Default image size */
	public static int IMAGE_SIZE = 16;
	/** Default image file extension */
	public static String IMAGE_EXT = "gif";
	/** Use a resource bundle for tabel/tooltip/shortcut/icon. By default true. If you want to alter it, use the <code>SharedProperties</code> */
	public static boolean RESOURCE_BUNDLE = SharedProperties.LOCALIZED_MESSAGE;
	/** Use default method for building UI part. By default <code>true</code> */
	public static boolean AUTO_UI = true;

	/** Status returned by the notifyAction */
	public static boolean VALID_ACTION = true;
	/** Status returned by the notifyAction */
	public static boolean INVALID_ACTION = false;

	/** XMLAction group reference name */
	protected String groupName;
	protected XMLEditor editor;

	public XMLAction() {
		super();
		installUIContent();
	}

	/** Reset all reference to XMLEditor and XMLContainer. This method should only
	 * be used when this action has no more usage
	 */
	public void dispose() {
		editor = null;
		container = null;
	}

	private String actionReference;

	/** ActionReference is a way to use the same icon than another known action
	 *  @param actionReference another action class name */
	public XMLAction(String actionReference) {
		super();
		setActionReferenceIcon(actionReference);
		//setActionReferenceGroup( actionReference );
	}

	/** Override the default icon using the icon of another action. Usage sample : 
	 *  setActionReferenceIcon( ActionModel.FORMAT_ACTION ); will use the same
	 * icon than the format action
	 * @param actionReference Action name
	 */
	public void setActionReferenceIcon(String actionReference) {
		this.actionReference = actionReference;
		installUIContent();
	}

	/** Override the default group name using the group name used by this actionReference */
	public void setActionReferenceGroup(String actionReference) {
		try {
			ResourceBundle r = ResourceBundle.getBundle(actionReference);
			groupName = r.getString("GROUP");
		} catch (Throwable th) {
		}
	}

	private boolean popable = true;

	/** Decide to include this action in a popup menu */
	public void setPopable(boolean popable) {
		this.popable = popable;
	}

	/** @return <code>true</code> if this action can be integrated in a popup menu. By default <code>true</code> */
	public boolean isPopable() {
		return popable;
	}

	private boolean toolbarable = true;

	/** Decide to include this action in the main toolbar. By default <code>true</code> */
	public void setToolbarable(boolean inToolbar) {
		this.toolbarable = inToolbar;
	}

	/** @return <code>true</code> if the toolbar can be include in the main toolbar */
	public boolean isToolbarable() {
		return toolbarable;
	}

	private ResourceBundle resource = null;

	/** @return a label from the resource bundle or the default one */	
	protected String getLabel( String name, String def ) {
		if ( resource == null )		
			return def;
		try {
			return resource.getString( name );			
		} catch( MissingResourceException exc ) {
		}
		return def;
	}

	/** Reset the default icon and the default text */
	protected void installUIContent() {
		if ( !AUTO_UI )
			return;			

		Icon icon = getDefaultIcon();

		String label = getDefaultLabel();
		String tooltip = getDefaultTooltip();
		KeyStroke _accelerator = getDefaultAccelerator();
		String accelerator = null;
		if (_accelerator != null) {
			putValue(Action.ACCELERATOR_KEY, _accelerator);
		}
		char _mnemonic = getDefaultMnemonic();
		String mnemonic = null;
		if (_mnemonic > 0)
			mnemonic = "" + _mnemonic;

		try {
			if (RESOURCE_BUNDLE) {
				resource = ResourceBundle.getBundle(getName());
				if (resource != null) {
					try {
						label = resource.getString("LABEL");
					} catch (MissingResourceException ex) {
					}
					try {
						tooltip = resource.getString("TOOLTIP");
					} catch (MissingResourceException ex) {
					}
					try {
						mnemonic = resource.getString("MNEMONIC");
					} catch (MissingResourceException ex) {
					}
					try {
						accelerator = resource.getString("ACCELERATOR");
					} catch (MissingResourceException ex) {
					}
					try {
						groupName = resource.getString("GROUP");
					} catch (MissingResourceException ex) {
					}
					try {
						icon =
							new ImageIcon(
								ClassLoader.getSystemClassLoader().getResource(
									resource.getString("ICON")));
					} catch (Throwable th) {
					}
				}
			}
		} catch (Throwable th) {
			//	if ( "true".equals( System.getProperty( "xmlpad.debug" ) ) )
			//		th.printStackTrace();
		}

		String prefix = "xmlpad.action." + getClass().getName() + ".";

		// Check from the UIManager
		if ( UIManager.getString( prefix + "mnemonic" ) != null ) {
			mnemonic = UIManager.getString( prefix + "mnemonic" );
		}

		if ( UIManager.getString( prefix + "accelerator" ) != null )
			accelerator = UIManager.getString( prefix + "accelerator" );

		if ( UIManager.getString( prefix + "label" ) != null ) 
			label = UIManager.getString( prefix + "label" );

		if ( UIManager.getString( prefix + "tooltip" ) != null ) 
			tooltip = UIManager.getString( prefix + "tooltip" );

		if ( UIManager.getIcon( prefix + "icon" ) != null )
			icon = UIManager.getIcon( prefix + "icon" );

		// Apply it

		if (mnemonic != null)
			putValue(Action.MNEMONIC_KEY, new Integer(mnemonic.charAt(0)));

		if (accelerator != null) {
			
			if ( accelerator.length() > 1 && 
					accelerator.startsWith( "*" ) ) {
				char last = accelerator.charAt( accelerator.length() - 1 );
				boolean shift = accelerator.indexOf( "shift " ) > 0;
				putValue(
						Action.ACCELERATOR_KEY,
						KeyStroke.getKeyStroke( 
								last, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | ( shift ? KeyEvent.SHIFT_MASK : 0 ) ) );
			}

			putValue(
				Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke( accelerator ) );
		}

		if (label != null)
			putValue(Action.NAME, label);

		if (tooltip != null)
			putValue(Action.SHORT_DESCRIPTION, tooltip);

		if (icon != null)
			putValue(Action.SMALL_ICON, icon);
		else {
			if (EMPTY_ICON == null)
				EMPTY_ICON = new EmptyIcon();
			putValue(Action.SMALL_ICON, EMPTY_ICON);
		}
	}

	/** @return the default icon for this action. By default the location is defined from the
	 *  current class name. For sample com.japisoft.xmlpad.action.edit.CutAction will have an
	 *  image stored at com/japisoft/xmlpad/action/edit/CutAction16.gif
	 */
	protected Icon getDefaultIcon() {
		String path = getClass().getName();
		if (actionReference != null)
			path = actionReference;
		path = path.replace('.', '/');
		path = path + IMAGE_SIZE + "." + IMAGE_EXT;
		try {
			return new ImageIcon(getClass().getClassLoader().getResource(path));
		} catch (Throwable th) {
		}
		return null;
	}

	/** @return the default action text. It uses the current name and remove if it exists the "Action" part. It is only
	 * used if no DefaultAction is available. */
	protected String getDefaultLabel() {
		String name = getName();
		int i = name.lastIndexOf('.');
		if (i > -1)
			name = name.substring(i + 1);
		if (name.endsWith("Action"))
			name = name.substring(0, name.length() - 6);
		return name;
	}

	/** @return the default tooltip for this action */
	protected String getDefaultTooltip() {
		return null;
	}

	/** @return the default mnemonic for this action */
	protected char getDefaultMnemonic() {
		return 0;
	}

	/** @return the default accelarator for this action */
	protected KeyStroke getDefaultAccelerator() {
		return null;
	}

	/** With this group you can create sub-menu inside the popup. By default it returns <code>null</code> */
	public String getPopupGroup() {
		return groupName;
	}

	/** Set the current XMLEditor for action */
	public void setXMLEditor(XMLEditor editor) {
		this.editor = editor;
		if (editor != null)
			notifyXMLEditor();
	}

	/** @return the current used editor */
	public XMLEditor getXMLEditor() {
		return editor;
	}

	/** Notify that an XMLEditor has been provided for futur action */
	protected void notifyXMLEditor() {
	}

	protected XMLContainer container;

	/** Set the current XMLEditor for action */
	public void setXMLContainer(XMLContainer container) {
		this.container = container;
		if (container != null)
			notifyXMLContainer();
		//if ( container == null )
		//	new RuntimeException().printStackTrace();
	}

	/** @return the current used XML container component */
	public XMLContainer getXMLContainer() {
		return container;
	}

	/** Notify that an XMLContainer has been provided for futur action */
	protected void notifyXMLContainer() {
	}

	/** @return <code>true</code> for editor focus at the end of the action. By default <code>true</code>  */
	protected boolean autoRequestFocus() { return true; }

	/** Don't override it, this method will call the <code>notifyAction</code> if an editor is available */
	public void actionPerformed(ActionEvent e) {
		if (getXMLEditor() != null) {
			if ( delegate != null ) {
				delegate.putValue( "XMLCONTAINER", container );
				delegate.actionPerformed( e );
				delegate.putValue( "XMLCONTAINER", null );
			}
			else {
				notifyAction();
				if ( autoRequestFocus() )
					getXMLContainer().requestFocus();
			}
		}
	}

	private Action delegate;

	/** Set a delegate for receiving the action event. By calling this method, the current action wll not
	 * be performed. Note that before calling actionPerformed a value for the key XMLCONTAINER will
	 * contain the current XMLContainer component.
	 */
	public void setActionDelegate( Action listener ) {
		this.delegate = listener;
	}

	/** Notify action must be done. It returns INVALID_ACTION if the
	 * action is not accessible and VALID_ACTION if this is correct.
	 * @return VALID_ACTION if the action is possible and else INVALID_ACTION
	 */
	public abstract boolean notifyAction();

	/** Return the name of the action. By default the class name is returned */
	public String getName() {
		return getClass().getName();
	}

	protected Object param = null;

	/** Set an optionnal parameter. Useful inside the <code>notifyAction</code> */
	public void setParam(Object param) {
		this.param = param;
	}

	/** @return the current parameter */
	public Object getParam() {
		return param;
	}

	private Hashtable htFeature;

	/** Set a feature for the action */
	public void setFeature(String feature, boolean enabled) {
		if (htFeature == null)
			htFeature = new Hashtable();
		htFeature.put(feature, new Boolean(enabled));
	}

	/** @return true if this feature is available */
	public boolean hasFeature(String feature) {
		if (htFeature == null)
			return false;
		if (htFeature.containsKey(feature))
			return ((Boolean) htFeature.get(feature)).booleanValue();
		return false;
	}

	/** @return a property value for this action. If the current <code>XMLAction</code> has not overrided it a <code>null</code> value will be returned */
	public Object getProperty(String propertyName, Object defaultValue) {
		return defaultValue;
	}

	/** Reset a property value for this action. Note this is the reponsability of the
	 * XMLAction to store it
	 * @param propertyName Property name
	 * @param value Property value
	 */
	public void setProperty(String propertyName, Object value) {
	}

	static EmptyIcon EMPTY_ICON = null;

	/** Empty icon for alignment on popup */
	public static class EmptyIcon extends Object implements Icon {
		private final int height;
		private final int width;

		public EmptyIcon() {
			height = IMAGE_SIZE;
			width = IMAGE_SIZE;
		}

		public EmptyIcon(Dimension size) {
			this.height = size.height;
			this.width = size.width;
		}

		public EmptyIcon(int height, int width) {
			this.height = height;
			this.width = width;
		}

		public int getIconHeight() {
			return height;
		}

		public int getIconWidth() {
			return width;
		}

		public void paintIcon(Component c, Graphics g, int x, int y) {
		}
	}

}

