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

// XMLAction ends here
