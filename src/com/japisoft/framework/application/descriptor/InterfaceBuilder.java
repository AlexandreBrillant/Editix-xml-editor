package com.japisoft.framework.application.descriptor;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;

import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.swing.Action;
import javax.swing.Icon;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.application.descriptor.helpers.ActionBuilder;
import com.japisoft.framework.application.descriptor.helpers.InterfaceBuilderFactory;
import com.japisoft.framework.application.descriptor.helpers.MenuBuilderDelegate;
import com.japisoft.framework.application.descriptor.helpers.Savable;
import com.japisoft.framework.internationalization.Traductor;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.xml.parser.ParseException;
import com.japisoft.framework.xml.parser.FPParser;
import com.japisoft.framework.xml.parser.document.Document;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.parser.walker.AndCriteria;
import com.japisoft.framework.xml.parser.walker.AttributeCriteria;
import com.japisoft.framework.xml.parser.walker.NodeNameCriteria;
import com.japisoft.framework.xml.parser.walker.OrCriteria;
import com.japisoft.framework.xml.parser.walker.TreeWalker;
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
public class InterfaceBuilder implements Savable {

	public final static String MENU_RECENT_PROJECT = "prjOpenRecent";
	public final static String MENU_RECENT_FILE = "openr";
	private URL documentURL;
	private ActionBuilder builder = null;

	/**
	 * @param input An URL for loading the descriptor
	 * @param builder Update the default ActionBuilder by this one. This is useful when you
	 * need a custom behavior for building each action object
	 * @throws InterfaceBuilderException If the XML descriptor is a wrong one */
	public InterfaceBuilder( URL input, ActionBuilder builder, String defaultDescriptorURI ) throws InterfaceBuilderException {
		this.documentURL = input;
		this.builder = builder;
		try {
			InputStream stream = input.openStream();
			try {
				buildUI( stream, defaultDescriptorURI );
			} finally {
				stream.close();
			}
			ApplicationModel.INTERFACE_BUILDER = this;
		} catch ( IOException exc ) {
			throw new InterfaceBuilderException("Problem reading " + input, exc);
		}
	}

	public URL getDocumentURL() {
		return this.documentURL;
	}

	/**
	 * @param input An URL for loading the descriptor
	 * @throws InterfaceBuilderException If the XML descriptor is a wrong one */	
	public InterfaceBuilder( URL input, String defaultDescriptorURI ) throws InterfaceBuilderException {
		this( input, null, defaultDescriptorURI );
	}

	public InterfaceBuilder() {}
	
	/** Build the user interface from this XML file descriptor */
	public void buildUI( InputStream input, String defaultURI ) throws InterfaceBuilderException {
		FPParser p = new FPParser();
		try {
			buildUI( p.parse( input ) );
		} catch (ParseException e) {
			ApplicationModel.debug( e );
			throw new InterfaceBuilderException( "Error while parsing " + ApplicationModel.USERINTERFACE_FILE + " at line " + e.getLine() + " : " + e.getMessage() );
		}
	}

	private FPNode lastRoot = null;

	private void buildUI( Document doc ) throws InterfaceBuilderException {
		FPNode root = ( FPNode ) doc.getRoot();
		this.lastRoot = root;
		buildUI( root );
	}

	private Icon appIcon = null;

	/** @return the main application icon */
	public Icon getAppIcon() {
		return appIcon;
	}

	private String name = null;
	
	public String getApplicationName() {
		return name;
	}
	
	private void buildUI( FPNode root ) throws InterfaceBuilderException {
		try {
			appIcon = com.japisoft.framework.app.toolkit.Toolkit.getImageIcon( root
					.getAttribute( "icon" ) );
		} catch ( Throwable th ) {}

		name = root.getAttribute( "name" );
		
		TreeWalker tw = new TreeWalker( root );
		
		Enumeration enumeration = tw.getNodeByCriteria(new NodeNameCriteria( "model" ), false);
		while (enumeration.hasMoreElements()) {
			buildModel((FPNode) enumeration.nextElement());
		}
		enumeration = tw.getNodeByCriteria(
				new NodeNameCriteria( "menuBar" ), false);
		if (enumeration.hasMoreElements()) {
			buildMenuBar((FPNode) enumeration.nextElement());
		}
		enumeration = tw.getNodeByCriteria(new NodeNameCriteria( "toolBar" ), false);

		while (enumeration.hasMoreElements()) {
			buildToolBar((FPNode) enumeration.nextElement());
		}
		
		enumeration = tw.getNodeByCriteria(new NodeNameCriteria( "popup" ), false);
		while (enumeration.hasMoreElements()) {
			buildPopup((FPNode) enumeration.nextElement());
		}

		rootProperties = new Properties();
		for ( int i = 0; i < root.getViewAttributeCount(); i++ ) {
			String att = root.getViewAttributeAt( i );
			String val = root.getViewAttribute( att );
			rootProperties.put( att, val );
		}
	}

	private Properties rootProperties;
	
	/** @return a root properti */
	public String getRootProperty( String name ) {
		if ( rootProperties != null )
			return rootProperties.getProperty( name );
		return null;
	}
	
	// Build the menuBar
	private void buildMenuBar(FPNode node) throws InterfaceBuilderException {
		menuBar = new JMenuBar();
		node.setApplicationObject(menuBar);
		TreeWalker tw = new TreeWalker(node);
		Enumeration enu = tw.getNodeByCriteria(new NodeNameCriteria("menu"),
				false);
		while (enu.hasMoreElements()) {
			FPNode _ = (FPNode) enu.nextElement();
			buildMenu(menuBar, _);
		}
	}

	private JMenuBar menuBar = null;

	/** @return the current menuBar */
	public JMenuBar getMenuBar() {
		return menuBar;
	}

	// Build the menu
	private void buildMenu(JComponent menuBar, FPNode node)
			throws InterfaceBuilderException {
		JMenu menu = new JMenu();
		node.setApplicationObject(menu);
		menu.setEnabled(node.childCount() > 1);

		String id = node.getAttribute("id", "");
		
		menu.setName( id );
		FPNode ui = (FPNode) node.childAt(0);

		String label = ui.getAttribute("label", "" );
		
		label = Traductor.traduce( id + ".label", label );

		String mnemonic = ui.getAttribute("mnemonic");
		
		mnemonic = Traductor.traduce( id + ".mnemonic", mnemonic );

		String icon = ui.getAttribute("icon");
		if (icon != null)
			menu.setIcon(com.japisoft.framework.app.toolkit.Toolkit
					.getImageIcon(icon));
		else if (!(menuBar instanceof JMenuBar))
			menu.setIcon(com.japisoft.framework.app.toolkit.Toolkit
					.getDefaultImage());

		menu.setText(label);
		if (mnemonic != null && mnemonic.length() > 0)
			menu.setMnemonic(mnemonic.charAt(0));

		// Build menu content

		if ( !"true".equals( node.getAttribute( "dynamic" ) ) ) {
			TreeWalker tw = new TreeWalker(node);

			Enumeration enu = tw.getNodeByCriteria(new OrCriteria(
					new OrCriteria(new NodeNameCriteria("itemRef"),
							new NodeNameCriteria("item")), new OrCriteria(
							new NodeNameCriteria("menu"), new NodeNameCriteria(
									"separator"))), false);

			while (enu.hasMoreElements()) {
				FPNode _ = (FPNode) enu.nextElement();
				if ( _.matchContent( "item" ) )
					buildMenuItem(menu, _);
				else if (	
					_.matchContent( "menu" ) )
					buildMenu(menu, _);
				else if ( _.matchContent( "separator" ) )
					menu.addSeparator();
				else if ( _.matchContent( "itemRef" ) ) {
					String ref = _.getAttribute( "ref" );
					if ( ref != null ) {
						if ( ActionModel.hasAction( ref ) )
							menu.add( ActionModel.restoreAction( ref ) );
						else
							throw new InterfaceBuilderException( "Can't find action reference " + ref );
					}
				}
			}
		}

		if ( !"false".equals( node.getAttribute( "enabled" ) ) )
			menuBar.add(menu);

		if ( node.hasAttribute( "builder" ) ) {
			String builderCl = node.getAttribute( "builder" );
			try {
				MenuBuilderDelegate builder = (
						MenuBuilderDelegate ) Class.forName( builderCl ).newInstance();
				builder.build( menu );
				if ( builderLst == null )
					builderLst = new ArrayList();
				builderLst.add( builder );
			} catch ( Throwable th ) {
				throw new InterfaceBuilderException( 
						"Can't use the menu builder " + builderCl, th );
			}
		}

	}
	
	private ArrayList builderLst = null;
	
	public int getBuilderCount() {
		if ( builderLst == null )
			return 0;
		return builderLst.size();
	}
	
	public MenuBuilderDelegate getBuilder( int index ) {
		return ( MenuBuilderDelegate )builderLst.get( index );
	}

	public void save() throws Exception {
		for ( int i = 0; i < getBuilderCount(); i++ ) {
			MenuBuilderDelegate mbd = getBuilder( i );
			if ( mbd instanceof Savable ) {
				Savable ms = ( Savable )mbd;
				ms.save();
			}
		}
	}
	
	private HashMap htGroupAction = null;

	private void storeActionByGroup(String refs, Action a) {
		StringTokenizer st = new StringTokenizer( refs, ";" );
		while (st.hasMoreTokens()) {
			String tkn = st.nextToken();
			if (htGroupAction == null)
				htGroupAction = new HashMap();
			ArrayList l = ( ArrayList ) htGroupAction.get( tkn );
			if (l == null)
				htGroupAction.put( tkn, l = new ArrayList() );
			l.add(a);
		}
	}

	/** Enabled/Disabled a set of action from this group name */
	public void setEnabledActionForGroup(
			String groupName, 
			boolean enabled ) {
		if ( htGroupAction == null )
			throw new RuntimeException( "No group found" );
		ArrayList l = ( ArrayList ) htGroupAction.get( groupName );
		if ( l != null )
			for (int i = 0; i < l.size(); i++) {
				Action a = ( Action )l.get( i );
				a.setEnabled( enabled );
			}	
	}

	/** Enabled/Disabled an action from its id */
	public void setEnabledActionForId( String id, boolean enabled ) {
		ActionModel.setEnabled( id, enabled );
	}

	/** Check the enabled/disabled state for its id */
	public boolean isEnabledActionForId( String id ) {
		return ActionModel.isEnabled( id );
	}

	/** Enabled/Disabled all group */
	public void setEnabledActionForAllGroup( boolean enabled ) {
		Set set = htGroupAction.keySet();
		Iterator it = set.iterator();
		while ( it.hasNext() ) {
			String ref = ( String )it.next();
			if ( !"*".equals( ref ) )
				setEnabledActionForGroup( ref, enabled );
		}
	}

	private Hashtable htActions = null;

	/** @return an action from this id name */
	public Action getActionById( String id ) {
		if ( htActions == null )
			return null;
		return ( Action )htActions.get( id );
	}

	private Action buildItem( FPNode item ) throws InterfaceBuilderException {
		FPNode ui = null;
		String label = null;
		Integer mnemonic = null;
		String shortcut = null;
		String icon = null;
		String icon2 = null;
		String help = null;
		String action = null;
		String param = null;
		String param2 = null;
		String param3 = null;
		String param4 = null;
		String type = null;
		boolean selected = false;
		boolean enabled = true;

		String pid = 
			item.getAttribute( "id" );		

		for ( int i = 0; i < item.childCount(); i++ ) {
			FPNode child = item.childAt( i );
			if ( child.matchContent( "ui" ) ) {
				ui = child;
				label = 
					ui.getAttribute("label", "");

				label = 
					Traductor.traduce( pid + ".label", label );

				String tmp = ui.getAttribute("mnemonic");
				
				tmp = 
					Traductor.traduce( pid + ".mnemonic", tmp );

				if ( tmp != null && tmp.length() > 0 )
					mnemonic = new Integer( tmp.charAt( 0 ) );
				shortcut = ui.getAttribute("shortcut");
				icon = ui.getAttribute( "icon" );
				icon2 = ui.getAttribute( "icon2" );
				help = ui.getAttribute("help");
				
				help = 
					Traductor.traduce( pid + ".help", help );

				action = ui.getAttribute("action");
				param = ui.getAttribute( "param" );
				param2 = ui.getAttribute( "param2" );			
				param3 = ui.getAttribute( "param3" );
				param4 = ui.getAttribute( "param4" );
				type = ui.getAttribute( "type" );
				
				selected = "true".equals( ui.getAttribute( "selected" ) );
				

				if ( ui.hasAttribute( "enabled" ) ) {
					enabled = "true".equals(ui.getAttribute( "enabled" ) );
				} else {
					if ( ui.getFPParent().hasAttribute( "enabled" ) ) {
						enabled = "true".equals( ui.getFPParent().getAttribute( "enabled" ) );
					}
				}
				break;
			}
		}

		boolean mustEnabledToFalse = false;

		if ( action == null ) {
			// Search for an action node
			for ( int i = 0; i < item.childCount(); i++ ) {
				FPNode child = item.childAt( i );
				if ( child.matchContent( "action" ) ) {
					action = child.getAttribute( "class" );
					
					if ( action == null && child.hasAttribute( "libraries" ) ) {
						if ( child.getAttribute( "libraries" ).toLowerCase().endsWith( ".js" ) ) {
							action = "JavaScript";
						}
					}
					
					if ( action == null )
						throw new InterfaceBuilderException( "Action defined but no class attribute at line " + child.getStartingLine() );

					type = child.getAttribute( "type" );
					selected = "true".equals( child.getAttribute( "selected" ) );
					
					if ( child.getAttribute( "selected" ) != null &&
							child.getAttribute( "selected" ).contains( "/" ) ) {
						String[] _tmp = child.getAttribute( "selected" ).split( "/" );
						selected = Preferences.getPreference( _tmp[ 0 ], _tmp[ 1 ], false ); 
					}

					if ( child.hasAttribute( "enabled" ) ) 
						mustEnabledToFalse = "false".equals( child.getAttribute( "enabled" ) );
					break;
				}
			}
		}

		try {

			if ( action == null )
				throw new InterfaceBuilderException( "Cannot find an action on the node at line " + item.getStartingLine() );

			// Try to find an alias for the action class
			if ( hasActionClassName( action ) )
				action = getActionClassName( action );

			ActionBuilder currentBuilder = builder;
			if ( currentBuilder == null )
				currentBuilder = InterfaceBuilderFactory.getActionBuilder();

			if ( currentBuilder == null )
				throw new InterfaceBuilderException( "Odd problem : No ActionBuilder Found ??" );
			
			Action a = currentBuilder.buildAction(
					item, action);
			
			if ( a == null )
				throw new InterfaceBuilderException( 
					"Cannot build the action " + action + " / name = " + label + " / line = " + item.getStartingLine() 
				);
			
			if ( pid != null ) {
				if ( htActions == null )
					htActions = new Hashtable();			
				htActions.put( pid, a );
			}

			String group = null;
			
			if ( ui != null ) {
				group = ui.getAttribute( "group" );
			}

			if ( group == null ) 
				group = item.getAttribute( "group" );
			
			if ( group != null )
				storeActionByGroup( group, a );

			a.putValue( Action.NAME, label );

			if (mnemonic != null ) {
				a.putValue( Action.MNEMONIC_KEY, mnemonic );
			}

			if ( ApplicationModel.MACOSX_MODE ) {
				String tmp = ui.getAttribute( "macShortcut" );
				if (tmp != null)
					shortcut = tmp;
			}

			if ( shortcut != null ) {

				if (shortcut.length() == 1)
					shortcut = Preferences.getPreference("shortcut", pid,
							shortcut.charAt(0))
							+ "";

				KeyStroke ks = null;

				if (shortcut.length() > 1)
					ks = KeyStroke.getKeyStroke(shortcut);
				else if (shortcut.length() == 1) {
					
					char c = shortcut.charAt(0);
					int modifier = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
					if ( c == '{' ) {
						c = KeyEvent.VK_LEFT;
						modifier = 
							KeyEvent.ALT_MASK;
					}
					else
					if ( c == '}' ) {
						c = KeyEvent.VK_RIGHT;
						modifier = 
							KeyEvent.ALT_MASK;
					}

					ks = KeyStroke.getKeyStroke( 
							c, 
							modifier 
					);
				}

				a.putValue(Action.ACCELERATOR_KEY, ks);
			}

			if (icon != null) {
				a.putValue(Action.SMALL_ICON,
						com.japisoft.framework.app.toolkit.Toolkit
								.getImageIcon(icon));
			} else
				a.putValue(Action.SMALL_ICON,
						com.japisoft.framework.app.toolkit.Toolkit
								.getDefaultImage());
			
			if ( icon2 != null ) {
				a.putValue(Action.SMALL_ICON + "2",
						com.japisoft.framework.app.toolkit.Toolkit
								.getImageIcon(icon2));				
			}

			if ( help != null ) {
				a.putValue(Action.SHORT_DESCRIPTION, help);
			}
			if ( param != null )
				a.putValue( "param", param );
			if ( param2 != null )
				a.putValue( "param2", param2 );
			if ( param3 != null )
				a.putValue( "param3", param3 );
			if ( param4 != null )
				a.putValue( "param4", param4 );

			if ( type != null ) {
				a.putValue( "type", type );
			}
			
			if ( selected )
				a.putValue( "selected", "true" );
			
			item.setApplicationObject( a );
			a.setEnabled( enabled );
			
			if ( mustEnabledToFalse ) {
				a.setEnabled( false );
			}
			
			a.putValue( "id", item.getAttribute( "id", "?" ) );
			
			return a;

		} catch ( Throwable th ) {
			if (th instanceof InterfaceBuilderException)
				throw (InterfaceBuilderException) th;
			else {
				throw new InterfaceBuilderException("Cannot build action "
						+ action, th);
			}
		}

	}
	
	// For a front end action (replacing an menu action)

	private HashMap pushActions = null;
	
	public void pushAction( Action a, String id ) {
		if ( a == null )
			return;
		Action oldAction = 
			getActionById( id );
		if ( oldAction != null && actionIdToUI != null ) {
			JComponent ui = ( JComponent )actionIdToUI.get( id );
			if ( ui != null ) {
				//a.putValue( "ui", ui );
				if ( pushActions == null )
					pushActions = new HashMap();
				Stack s = ( Stack )pushActions.get( id );
				if ( s == null ) {
					s = new Stack();
					pushActions.put( id, s );
				}
				if ( ui instanceof AbstractButton ) {
					// Store the old one
					if ( s.isEmpty() || 
							( s.peek() != oldAction ) )
						s.push( oldAction );					
					((AbstractButton)ui).setAction( a );
					
					// Copy UI keys : name, icon, description
					if ( a.getValue( Action.NAME ) == null ) {
						a.putValue( Action.NAME, oldAction.getValue( Action.NAME ) );
						a.putValue( Action.SHORT_DESCRIPTION, oldAction.getValue( Action.SHORT_DESCRIPTION ) );						
						a.putValue( Action.SMALL_ICON, oldAction.getValue( Action.SMALL_ICON ) );
					}
					
					ActionModel.storeAction( id, a );
				}
			}
		}
	}
	
	public void popAction( String id ) {
		// Restore the last context action
		if ( pushActions != null ) {
			Stack s = ( Stack )pushActions.get( id );
			if ( s != null && 
					!s.isEmpty() ) {
				Action a = ( Action )s.pop();
				JComponent ui = ( JComponent )actionIdToUI.get( id );
				if ( ui instanceof AbstractButton ) {
					((AbstractButton)ui).setAction( a );
					ActionModel.storeAction( id, a );
				}
			}
		}
	}

	private HashMap actionIdToUI = null;
	
	// Build a menu item : A Leaf
	private void buildMenuItem(JMenu menu, FPNode node)
			throws InterfaceBuilderException {
		//if ("false".equals(node.getAttribute("enabled")))
		//	return;

		Action a = buildItem( node );

		JMenuItem item = null;
		if ( "checkbox".equals( a.getValue( "type" ) ) ) {
			item = new JCheckBoxMenuItem( a );
			
			if ( "true".equals( a.getValue( "selected" ) ) ) {
				item.setSelected( true );
				a.actionPerformed( new ActionEvent( item, 0, null ) );
			}
			else
				item.setSelected( false );
		}
		else
			item = new JMenuItem();
		String id = node.getAttribute("id", "");
		item.setName(id);
		
		item.setAction(a);		
		if ( actionIdToUI == null ) {
			actionIdToUI = new HashMap();
		}
		actionIdToUI.put( id, item );
		ActionModel.storeAction(id, a);
		menu.add(item);

		node.childAt( 0 ).setApplicationObject(item);
	}

	private Hashtable htToolBarsByGroup = null;

	/** @return a toolbar for this group */
	public JToolBar getToolBarByGroup( String group ) {
		if (htToolBarsByGroup == null)
			return null;
		return ( JToolBar )htToolBarsByGroup.get( group );
	}

	private Hashtable htToolBarsByID = null;
	
	/** @return a toolbar from this name / id */
	public JToolBar getToolBarById( String id ) {
		if ( htToolBarsByID == null )
			return null;
		return ( JToolBar )htToolBarsByID.get( id );
	}
	
	/** Enabled/Disabled a toolbar content for this id */
	public void setEnabledToolBarContentById( String id, boolean enabled ) {
		JToolBar tb = getToolBarById( id );
		if ( tb == null )
			throw new RuntimeException( "Unkonwn toolbar " + id );
		else {
			for ( int i = 0; i < tb.getComponentCount(); i++ ) {
				if ( tb.getComponent( i )
						instanceof AbstractButton ) {
					AbstractButton ab = ( AbstractButton )tb.getComponent( i );
					ab.getAction().setEnabled( enabled );
				}
			}
		}
	}

	private void buildToolBar(FPNode node) throws InterfaceBuilderException {
		if ("false".equals(node.getAttribute("enabled")))
			return;
		String id = node.getAttribute("id", "?");

		JToolBar tb = new JToolBar();
		tb.setName( id );
		tb.setFloatable( !"false".equals( node.getAttribute( "floatable" ) ) );
		
		boolean buttonBorder = !"false".equals( node.getAttribute( "border" ) );
		
		node.setApplicationObject(tb);
		TreeWalker tw = new TreeWalker(node);
		Enumeration enu = tw.getNodeByCriteria(new OrCriteria(
				new NodeNameCriteria("item"), new OrCriteria(
						new NodeNameCriteria("separator"),
						new NodeNameCriteria("itemRef"))), false);
		while (enu.hasMoreElements()) {
			FPNode _ = (FPNode) enu.nextElement();
			if ("item".equals(_.getNodeContent())) {
				String _id = _.getAttribute("id", "?");
				Action a = buildItem( _ );
				ActionModel.storeAction(_id, a);
				JButton btn = tb.add(a);
				if ( !buttonBorder )
					btn.setBorderPainted( false );
			} else if ("itemRef".equals(_.getNodeContent())) {
				String ref = _.getAttribute("ref", "?");
				if (ActionModel.hasAction(ref)) {
					JButton btn = tb.add(ActionModel.restoreAction(ref));
					if ( !buttonBorder )
						btn.setBorderPainted( false );
				} else
					if ( !"?".equals( ref ) )
						throw new InterfaceBuilderException( "Can't find the action reference " + ref );
			} else if ("separator".equals(_.getNodeContent()))
				tb.addSeparator();
		}

		if ( htToolBarsByGroup == null )
			htToolBarsByGroup = new Hashtable();
		if ( htToolBarsByID == null )
			htToolBarsByID = new Hashtable();
		
		htToolBarsByID.put( id, tb );
		
		String target = node.getAttribute("group", "*");
		StringTokenizer st = new StringTokenizer(target, ";");
		while (st.hasMoreTokens()) {
			htToolBarsByGroup.put(st.nextToken(), tb);
		}
	}

	/** @return <code>true</code> if a popup if available for this name/id */
	public boolean hasPopup(String id) {
		if (htPopups == null)
			return false;
		return htPopups.containsKey(id);
	}

	private Hashtable htPopups = null;

	/** @return a popup menu for this name/id */
	public JPopupMenu getPopup(String id) {
		if (htPopups == null)
			return null;
		return (JPopupMenu) htPopups.get(id);
	}

	private Hashtable htActionClassAlias = null;

	/** This is a way to not used a full class name in the file descriptor but
	 * rather an alias. The real classname is then resolved here. This is a way to
	 * protect the final class name or to have more flexibility to choose a class
	 * when starting
	 * @param alias The action class alias
	 * @param className The final action class name
	 */
	public void setActionClassAlias( String alias, String className ) {
		if ( htActionClassAlias == null )
			htActionClassAlias = new Hashtable();
		htActionClassAlias.put( alias, className );
	}

	/** @return the real classname used from this action alias */
	public String getActionClassName( String alias ) {
		if ( htActionClassAlias != null )
			return ( String )htActionClassAlias.get( alias );
		return null;
	}
	
	/** @return <code>true</code> if a classname is bound to this alias name */
	public boolean hasActionClassName( String alias ) {
		if ( htActionClassAlias != null )
			return htActionClassAlias.containsKey( alias );
		return false;
	}

	private void buildPopup(FPNode node) throws InterfaceBuilderException {
		if ("false".equals(node.getAttribute("enabled")))
			return;
		String id = node.getAttribute("id", "?");
		JPopupMenu popup = new JPopupMenu();
		node.setApplicationObject(popup);

		TreeWalker tw = new TreeWalker(node);
		Enumeration enu = tw.getNodeByCriteria(new OrCriteria(
				new NodeNameCriteria("item"), new OrCriteria(
						new NodeNameCriteria("separator"), new OrCriteria(
								new NodeNameCriteria("menu"),
								new NodeNameCriteria("itemRef")))), false);
		while (enu.hasMoreElements()) {
			FPNode _ = (FPNode) enu.nextElement();
			if ("item".equals(_.getNodeContent())) {
				String _id = _.getAttribute("id", "?");
				Action a = buildItem( _ );
				ActionModel.storeAction(_id, a);
				popup.add(a);
			} else if ("itemRef".equals(_.getNodeContent())) {
				String ref = _.getAttribute("ref", "?");
				if (ActionModel.hasAction(ref))
					popup.add(ActionModel.restoreAction(ref));
				else
					if ( !"?".equals( ref ) )
						throw new InterfaceBuilderException( "Can't find the action reference [" + ref + "]" );
			} else if ("separator".equals(_.getNodeContent()))
				popup.addSeparator();
			else if ("menu".equals(_.getNodeContent()))
				buildMenu(popup, _);
		}

		if (htPopups == null)
			htPopups = new Hashtable();

		htPopups.put(id, popup);
	}

	private Hashtable htModels;

	private void buildModel(FPNode node) throws InterfaceBuilderException {
		ArrayList model = new ArrayList();
		if (htModels == null)
			htModels = new Hashtable();

		String id = node.getAttribute("id", "?");
		ArrayList al = new ArrayList();
		node.setApplicationObject(al);
		htModels.put(id, al);

		TreeWalker tw = new TreeWalker(node);
		Enumeration enu = tw.getNodeByCriteria(new OrCriteria(
				new NodeNameCriteria("item"), new OrCriteria(
						new NodeNameCriteria("separator"), new OrCriteria(
								new NodeNameCriteria("menu"),
								new NodeNameCriteria("itemRef")))), false);
		while (enu.hasMoreElements()) {
			FPNode _ = (FPNode) enu.nextElement();
			if ( _.matchContent( "item" ) ) {
				String _id = _.getAttribute("id", "?");
				Action a = buildItem( _ );
				ActionModel.storeAction(_id, a);
				al.add(a);
			} else if ("itemRef".equals(_.getNodeContent())) {
				String ref = _.getAttribute("ref", "?");
				if (ActionModel.hasAction(ref))
					al.add(ActionModel.restoreAction(ref));
			}
		}
	}

	/** @return a set of action for this name / id */
	public ArrayList getModel(String id) {
		if (htModels != null)
			return (ArrayList) htModels.get(id);
		return null;
	}

	/** @return a specific menu for this name / id */
	public JMenu getMenu(String id) {
		FPNode node = getMenuNode(id);
		if (node != null)
			return (JMenu) node.getApplicationObject();
		return null;
	}

	/** @return the XML node managing this menu name / id */
	public FPNode getMenuNode(String id) {
		TreeWalker tw = new TreeWalker(lastRoot);
		Enumeration enu = tw.getNodeByCriteria(new AndCriteria(
				new NodeNameCriteria("menu"), new AttributeCriteria("id", id)),
				true);
		if (enu.hasMoreElements())
			return (FPNode) enu.nextElement();
		return null;
	}

	/** Enabled/Disabled a complete menu for this name/id */
	public void setEnabledMenu(String id, boolean enabled) {
		FPNode n = getMenuNode(id);
		if (n != null)
			((JMenu) n.getApplicationObject()).setEnabled(enabled);
	}

	///////////// MODIF /////////////

	public boolean treeModified = false;

	/** @return <code>true</code> if the XML document has been modified */
	public boolean isApplicationDocumentModified() {
		return treeModified;
	}

	/** Force a document modification */
	public void setApplicationDocumentModified() {
		treeModified = true;
	}

	/** Save the current application document. It will work only if
	 * you have a document URL 
	 * @throws InterfaceBuilderException If the operation is not possible
	 * */
	public void saveApplicationDocument() throws InterfaceBuilderException {
		if ( documentURL == null )
			throw new InterfaceBuilderException( "No Document URL, cannot save this document" );
		if ("file".equals(documentURL.getProtocol())) {
			String fileName = documentURL.toExternalForm();
			fileName = fileName.replaceAll("%20", " ");
			fileName = fileName.substring(5);
			try {
				FPNode node = getMenuNode( "plugin" );
				if (node != null) {
					while ( node.childCount() > 1 ) {
						node.removeChildNodeAt( 1 );
					}
				}
				lastRoot.getDocument().write(
						new FileOutputStream( fileName ) );
			} catch ( Throwable th ) {
				throw new InterfaceBuilderException( "Can't store " + fileName, th );
			}
		} else
			throw new InterfaceBuilderException( "Unknown protocol for saving this document" );
	}

	/** Remove the following children item matching this param */
	public void removeMenuItemForParam( String menuId, String param ) {
		FPNode n = getMenuNode( menuId );
		if ( n != null ) {
			JMenu menu = ( JMenu )n.getApplicationObject();
			
			// Remove similar node
			for ( int i = 0; i < n.childCount(); i++ ) {
				FPNode _ = n.childAt( i );

				if ( !"item".equals( _.getContent() ) )
					continue;

				FPNode __ = _.childAt( 0 );

				if ( __.getAttribute( "param" ) != null
						&& __.getAttribute( "param" ).equals( param ) ) {
					treeModified = true;					
					if ( __.getApplicationObject() != null ) {
						menu.remove( i - 1 );
					}
					n.removeChildNode( _ );
					break;
				}
			}
		}
	}

	/** Remove all the menu content */
	public void cleanMenuItems( String menuId ) {
		FPNode n = getMenuNode(menuId);
		if ( n != null ) {
			FPNode sn = n.childAt( 0 );
			n.removeChildrenNodes();
			n.appendChild( sn );
			JMenu menu = (JMenu) n.getApplicationObject();
			menu.removeAll();
			if ( "true".equals( n.getAttribute( "dynamic" ) ) )
				menu.setEnabled( false );
			setApplicationDocumentModified();
		}
	}

	/** Add a menu item for this menu id. For dynamic usage */
	public void insertMenuItemAtFirst(String menuId, Action a, int limit) {
		FPNode n = getMenuNode(menuId);
		if (n != null) {
			JMenu menu = (JMenu) n.getApplicationObject();
			treeModified = true;

			if (n.childCount() > limit) {
				FPNode n1 = n.childAt(n.childCount() - 1);
				FPNode n2 = n.childAt(0);

				n.removeChildNodeAt(n.childCount() - 1);

				menu.remove(menu.getItemCount() - 1);
			}
			
			// Remove similar node
			for (int i = 0; i < n.childCount(); i++) {
				FPNode _ = n.childAt(i);

				if (!"item".equals(_.getContent()))
					continue;

				FPNode __ = _.childAt(0);

				if (__.getAttribute("param") != null
						&& __.getAttribute("param").equals(a.getValue("param"))) {
					if (__.getApplicationObject() != null) {
						menu.remove( ( JMenuItem )__.getApplicationObject() );
					}
					n.removeChildNode(_);
					break;
				}
			}

			int i = 0;
			while ( i < menu.getItemCount() ) {

				JMenuItem item = menu.getItem( i );
				Action aa = item.getAction();
				if ( aa.getValue( "param" ) != null && 
						aa.getValue( "param" ).equals( 
								a.getValue( "param" ) ) ) {
					menu.remove( i );
				} else
					i++;

			}
			
			if (a.getValue("iconPath") != null) {
				try {
					a.putValue(Action.SMALL_ICON,
							new ImageIcon(
								Thread.currentThread().getContextClassLoader().getResource(
									(String) a
											.getValue("iconPath"))));
				} catch (Throwable th) {
					System.err.println("Can't load " + a.getValue("iconPath"));
				}
			}

			JMenuItem item = new JMenuItem( a );
			menu.insert( item, 0 );

			if ( !menu.isEnabled() )
				menu.setEnabled(true);

			FPNode tmpNode = null;

			n.insertChildNode(1, tmpNode = createNodeFromAction("*", a));
			tmpNode.childAt(0).setApplicationObject(item);
		}
	}

	public FPNode createModel( 
			String id, 
			ArrayList al ) {
		
		FPNode n = new FPNode( FPNode.TAG_NODE, "model" );
		n.setAttribute( "id", id );
		for ( int i = 0; i < al.size(); i++ ) {
			Action a = ( Action )al.get( i );
			FPNode n2 = 
				createNodeFromAction( 
						id + "-" + i, 
						a );
			n.appendChild( n2 );
		}
		return n;

	}

	private FPNode createNodeFromAction(String id, Action a) {
		FPNode n = new FPNode(FPNode.TAG_NODE, "item");
		n.setAttribute("id", id);

		FPNode ui = new FPNode(FPNode.TAG_NODE, "ui");
		ui.setApplicationObject(a);
		ui.setAttribute("action", a.getClass().getName());
		ui.setAttribute("label", (String) a.getValue(Action.NAME));
		
		if (a.getValue("param") != null)
			ui.setAttribute("param", (String) a.getValue("param"));
		if (a.getValue("param2") != null)
			ui.setAttribute("param2", (String) a.getValue("param2"));
		if (a.getValue("param3") != null)
			ui.setAttribute("param3", (String) a.getValue("param3"));
		if (a.getValue("param4") != null)
			ui.setAttribute("param4", (String) a.getValue("param4"));		
		if (a.getValue("iconPath") != null)
			ui.setAttribute("icon", (String) a.getValue("iconPath"));
		if ( a.getValue( "type" ) != null )
			ui.setAttribute( "type", ( String )a.getValue( "type" ) );
		if ( a.getValue( "selected" ) != null )
			ui.setAttribute( "selected", ( String )a.getValue( "selected" ) );
		n.addNode( ui );
		return n;
	}

}
