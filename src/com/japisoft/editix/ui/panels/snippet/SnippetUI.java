package com.japisoft.editix.ui.panels.snippet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.application.descriptor.ActionModel;
import com.japisoft.framework.ui.FastLabel;
import com.japisoft.framework.xml.parser.node.FPNode;
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
public class SnippetUI extends javax.swing.JPanel implements
	MouseListener, 
	MouseMotionListener, 
	ActionListener,
	TreeSelectionListener {

private static final String TEXT_TEMPLATE = "=T=";

static final String INSERT_CMD = "insert";	
static final String REN_CMD = "ren";
static final String DEL_CMD = "del";
static final String ADDD_CMD = "addd";
static final String ADDS_CMD = "add";
static final String ANY_CMD = "any";
static final String HLP_CMD = "hlp";

static final String[] TOOLBAR_ACTIONS = {
	INSERT_CMD, ADDS_CMD, ADDD_CMD, REN_CMD, DEL_CMD
};

static final String[] POPUP_ACTIONS = {
	INSERT_CMD, ADDS_CMD, ADDD_CMD, REN_CMD, ANY_CMD, HLP_CMD, DEL_CMD
};

private static final String LOCK_PNG = "lock.png";
private static final String LOCK_OPEN_PNG = "lock_open.png";

private JTree t;
private JToolBar tb;
private NodeTransfertHandler th;

public SnippetUI() {
	setLayout( new BorderLayout() );

	tb = new JToolBar();

	for ( int i = 0; i < TOOLBAR_ACTIONS.length; i++ ) {
		tb.add( 
				EditixFrame.THIS.getBuilder().getActionById( 
						"snp." + TOOLBAR_ACTIONS[ i ] ) );
	}

	add(tb, BorderLayout.NORTH);

	add(
			new JScrollPane( t = new JTree() ), 
			BorderLayout.CENTER
	);

	clean();
	t.setCellRenderer(new SnippetRenderer());
	t.setTransferHandler( th = new NodeTransfertHandler() );
	setEnabledAction( DEL_CMD, false );

}

private JPopupMenu buildPopupMenu() {
	JPopupMenu menu = new JPopupMenu();
	for ( int i = 0; i < POPUP_ACTIONS.length; i++ ) {
		if (  POPUP_ACTIONS[ i ] == null )
			menu.addSeparator();
		else
			menu.add( 
					EditixFrame.THIS.getBuilder().getActionById( 
							"snp." + POPUP_ACTIONS[ i ] ) );
	}		
	return menu;
}

void init() {
	t.setSelectionPath( new TreePath( t.getModel().getRoot() ) );
	valueChanged( null );
}

public void addNotify() {
	super.addNotify();
	t.addMouseListener( this );
	t.addMouseMotionListener( this );
	t.addTreeSelectionListener( this );
}

public void removeNotify() {
	super.removeNotify();
	t.removeMouseListener( this );
	t.removeMouseMotionListener( this );
	t.removeTreeSelectionListener( this );
}

void setEnabledAction(String name, boolean enabled) {
	ActionModel.setEnabled( "snp." + name, enabled );
}

public void valueChanged( TreeSelectionEvent e ) {
	TreePath tp = t.getSelectionPath();
	if ( tp != null ) {
		FPNode node = ( FPNode )tp.getLastPathComponent();
		setEnabledAction( DEL_CMD, !node.isRoot() || node.isAttribute() );
		setEnabledAction( ADDS_CMD, node.isRoot()
				|| ( node.matchContent( "group" ) || node.matchContent( "item" ) ) );
		setEnabledAction( ADDD_CMD, node.isRoot()
				|| node.matchContent( "group" ) );
		setEnabledAction( REN_CMD, node.isRoot() || 
				( node.matchContent( "group" ) || node.matchContent( "item") || node.isAttribute() ) );
		setEnabledAction( INSERT_CMD, node.matchContent( "item" ) );
		setEnabledAction( ANY_CMD, node.matchContent( "item" ) && node.isTag() );
		setEnabledAction( HLP_CMD, node.matchContent( "item" ) && node.isTag() );		
	}
}

public void actionPerformed(ActionEvent e) {
	String cmd = e.getActionCommand();
	
	if ( t.getSelectionPath() == null ) {
		return;
	}
	if (ADDS_CMD.equals(cmd)) {
		addSnippet();
	} else 
	if (ADDD_CMD.equals(cmd)) {
		addGroup();
	} else 
	if (DEL_CMD.equals(cmd)) {
		delete();
	} else 
	if ( REN_CMD.equals( cmd ) ) {
		rename( (FPNode) t.getSelectionPath().getLastPathComponent() );
	} else 
	if ( INSERT_CMD.equals( cmd ) ) {
		insertSelectedItem();
	} else 
	if ( ANY_CMD.equals( cmd ) ) {
		FPNode n = ( FPNode )t.getSelectionPath().getLastPathComponent(); 
		String param = n.getAttribute( "param", "" );
		int i = param.indexOf( "*" );
		// Delete it
		if ( i > -1 ) {
			param = param.substring( 0, i ) + param.substring( i + 1 );
		} else
			param = param + "*";
		n.setAttribute( "param", param );
	} else
	if ( HLP_CMD.equals( cmd ) ) {
		FPNode n = ( FPNode )t.getSelectionPath().getLastPathComponent();
		String hlp = EditixFactory.buildAndShowInputDialog( "Help", n.getAttribute( "hlp", "" ) );
		if ( hlp != null ) {
			n.setAttribute( "hlp", hlp );
		}
	}

	refresh();
}

void refresh() {
	TreePath tp = t.getSelectionPath();
	((SnippetTreeModel) t.getModel()).refresh();
	t.repaint();
	if ( tp != null )
		t.setSelectionPath( tp );
}

private void addSnippet() {
	
	if ( t.getSelectionPath() == null ) {
		EditixFactory.buildAndShowWarningDialog( "No selected node ?" );
		return;
	}
	
	XMLContainer container = EditixFrame.THIS.getSelectedContainer();
	FPNode current = ( FPNode )t.getSelectionPath().getLastPathComponent();
	
	if ( container == null ) {
		EditixFactory
				.buildAndShowErrorDialog(
						"You need to have an opened XML document for adding a snippet !" );
	} else {

		FPNode n = null;

		// Check from the tree
		if ( container.getTree() != null &&
				container.getTree().getSelectionPath() != null ) {
			n = ( FPNode )container.getTree().getSelectionPath().getLastPathComponent();
			if ( n.isText() )
				n = n.getFPParent();
		}
		if ( n == null )
			EditixFactory.buildAndShowWarningDialog( "Please select an element before adding" );
		else {

			String name = n.getContent();
			
			if ( n.getNameSpacePrefix() != null ) 
					name = n.getNameSpacePrefix() + ":" + name;
			FPNode sw = new FPNode( FPNode.TAG_NODE, "item" );
			sw.setAttribute( "name", name );
			
			if ( n.hasTextChildNode() )
				sw.setAttribute( "param", "T" );
			else
				sw.setAttribute( "param", "-" );

			// Add all the attributes
			for ( int i = 0; i < n.getViewAttributeCount(); i++ ) {
				String attName = n.getViewAttributeAt( i );
				sw.setAttribute(
						"att" + i,
						attName + "=" + n.getAttribute( attName ) );
			}
			current.appendChild( sw );
			selectNode( sw );
		}
	}
}

private void addGroup() {
	String name = EditixFactory.buildAndShowInputDialog("Group name ?");
	if (name != null) {
		FPNode parentNode = (FPNode) t.getSelectionPath()
				.getLastPathComponent();
		
		FPNode g = new FPNode( FPNode.TAG_NODE, "group" );
		g.setAttribute( "name", name );
		parentNode.appendChild( g );
		
		selectNode(g);
	}
}

private void delete() {		
	TreePath[] tps = t.getSelectionPaths();
	if ( tps != null ) {
		for ( int i = 0; i < tps.length; i++ ) {
			FPNode selectedNode = ( FPNode )( tps[ i ].getLastPathComponent() );
			FPNode parentNode = selectedNode.getFPParent();
			if (selectedNode.matchContent("group")) {
				if (!EditixFactory
						.buildAndShowConfirmDialog("Please confirm you wish to delete "
								+ selectedNode.getAttribute("name"))) {
					continue;
				}
			} else {
				if ( selectedNode.isAttribute() ) {
					parentNode.setAttribute( selectedNode.getContent(), null );
				}
			}
			parentNode.removeChildNode( selectedNode );			
			selectNode(parentNode);				
		}
	}
}

private TreePath convertToTreePath(FPNode node) {
	ArrayList al = new ArrayList();
	while (node != null) {
		al.add(0, node);
		node = (node.getFPParent());
	}
	return new TreePath(al.toArray());
}

private void selectNode(FPNode node) {
	final FPNode test = node;
	SwingUtilities.invokeLater(new Runnable() {
		public void run() {
			TreePath tp = convertToTreePath(test);
			t.setSelectionPath(tp);
		}
	});
}

private void expandNode( FPNode node ) {
	final FPNode test = node;
	SwingUtilities.invokeLater(new Runnable() {
		public void run() {
			TreePath tp = convertToTreePath(test);
			t.expandPath(tp);
		}
	});		
}

public void clean() {
	try {
		t.setModel(new SnippetTreeModel(SnippetTool.getRoot()));
		t.setSelectionPath(new TreePath(SnippetTool.getRoot()));
	} catch (IOException e) {

		EditixFactory.buildAndShowErrorDialog( "Can't load the snippet file " + e.getMessage() );
		
	}
}

boolean dragAndDropInUse = false;

public void mouseDragged(MouseEvent e) {
	if ( !dragAndDropInUse ) {
		TreePath tp = t.getPathForLocation( e.getX(), e.getY() );
		if ( tp != null ) {
			if ( !( ( FPNode )tp.getLastPathComponent() ).isAttribute() ) { 
				th.exportAsDrag( t, e, TransferHandler.MOVE );		
				if ( tp != null )
					t.setSelectionPath( tp );
				dragAndDropInUse = true;
			}
		}
	}
}

public void mouseReleased(MouseEvent e) {
	dragAndDropInUse = false;
}

public void mouseMoved(MouseEvent e) {
	TreePath tp = t.getPathForLocation( e.getX(), e.getY() );
	if ( tp != null ) {
		FPNode node = ( FPNode )tp.getLastPathComponent();
		String path = node.getAttribute( "path" );			
		( (SnippetRenderer)t.getCellRenderer() ).fastlabel.setToolTipText(
				path );
		t.setToolTipText( path );
		((JComponent)t.getParent()).setToolTipText( path );
	}
}

public void mouseClicked(MouseEvent e) {		
	if ( e.isPopupTrigger() || 
			e.getButton() >= 2 ) {
		JPopupMenu jpm = buildPopupMenu();
		jpm.show( (Component)e.getSource(), e.getX(), e.getY() );
		return;
	}

	if ( e.getClickCount() >= 2 ) {
		insertSelectedItem();
	}
}

private void insertSelectedItem() {
	if ( t.getSelectionPath() == null )
		return;
	FPNode node = ( FPNode )t.getSelectionPath()
			.getLastPathComponent();
	if ( node.matchContent( "item" ) ) {

		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container == null ) {
			EditixFactory.buildAndShowErrorDialog( "No document found for inserting a snippet" );
			return;
		}

		String selectedText = container.getEditor().getSelectedText();
		String xmlToInsert = resolveNodeToXMLText( node );
		if ( selectedText != null ) {
			xmlToInsert = xmlToInsert.replaceFirst( TEXT_TEMPLATE, selectedText );
			xmlToInsert = xmlToInsert.replaceAll( TEXT_TEMPLATE, "" );
			container.getEditor().replaceSelection( xmlToInsert );
		} else {
			xmlToInsert = xmlToInsert.replaceAll( TEXT_TEMPLATE, "" );
			container.getEditor().insertText( xmlToInsert );
		}
	}
}

private void rename( FPNode node ) {
	
	String oldName;
	if ( node.isAttribute() ) {
		oldName = node.getNodeValue();
	} else
		oldName = node.getAttribute( "name" );

	String newName = EditixFactory.buildAndShowInputDialog(
			"New name", oldName);

	if (newName != null) {

		if ( !node.isAttribute() ) {
			StringBuffer sb = new StringBuffer();			
			// Clean newname
			for ( int i = 0; i < newName.length(); i++ ) {
				if ( Character.isLetterOrDigit( newName.charAt( i ) ) )
					sb.append( newName.charAt( i ) );
			}
			if ( sb.length() == 0 ) {
				if ( node.matchContent( "group" ) )
					sb.append( "My nodes" );
				else
					sb.append( "myElement" );
			}
			newName = sb.toString();			
		} else {
			if ( newName == null || newName.indexOf( "=" ) == -1 )
				newName = "myAtt=myVal";
		}

		if ( node.isAttribute() ) {
			node.setNodeValue( newName );
		} else {
			if (!newName.equals(oldName)) {
				node.setAttribute( "name", newName );			
				refresh();
			}
		}

	}
}

public void mouseEntered(MouseEvent e) {}
public void mouseExited(MouseEvent e) {}
public void mousePressed(MouseEvent e) {}

// ///////////////////////////////////////////////////////////////

class SnippetTreeModel implements TreeModel {

	private FPNode root;

	SnippetTreeModel(FPNode root) {
		this.root = root;
	}

	private ArrayList listeners;

	public void addTreeModelListener(TreeModelListener l) {
		if (listeners == null)
			listeners = new ArrayList();
		listeners.add(l);
	}

	public void refresh() {
		if (listeners != null) {
			for (int i = 0; i < listeners.size(); i++) {
				TreeModelListener l = (TreeModelListener) listeners.get(i);
				l.treeStructureChanged(new TreeModelEvent(t, new TreePath(
						root)));
			}
		}
	}
	
	public Object getChild(Object parent, int index) {
		FPNode sn = (FPNode) parent;
		if (sn.matchContent("group")) {
			return sn.childAt(index);
		} else {
			if ( sn.matchContent( "item" ) ) {
				
				if ( index < getAttCount( sn ) ) {
					return sn.getViewAttributeNodes().get( index + 2 );
				} else									
					return sn.childAt( index - getAttCount( sn ) );
			}
		}
		return null;
	}

	public int getChildCount( Object parent ) {
		FPNode sn = (FPNode) parent;
		if ( sn.matchContent("group" ) ) {
			return sn.childCount();
		} else
		if ( sn.matchContent( "item" ) ) {
			return sn.childCount() + getAttCount( sn );
		}
		return 0;
	}

	private int getAttCount( FPNode sn ) {
		if ( sn.getViewAttributeCount() > 2 ) {
			return sn.getViewAttributeCount() - 2 - ( sn.hasAttribute( "hlp" ) ? 1 : 0 );
		}
		else
			return 0;
	}
	
	public int getIndexOfChild(Object parent, Object child) {
		FPNode sn = (FPNode) parent;
		if ( sn.matchContent("group") ) {
			return sn.childNodeIndex((FPNode) child);
		} else
		if ( sn.matchContent( "item" ) ) {
			
			int i = -1;
			
			if ( sn.getViewAttributeCount() > 0 ) {
				i = sn.getViewAttributeNodes().indexOf( child );
			}
			if ( i == -1 ) {
				i =  getAttCount( sn ) + 
						sn.childNodeIndex( ( FPNode )child );
			}
			return i;
		}
		return -1;
	}

	public Object getRoot() {
		return root;
	}

	public boolean isLeaf(Object node) {
		FPNode sn = (FPNode) node;
		return ( sn.getChildCount() == 0 && getAttCount( sn ) == 0 );
	}

	public void removeTreeModelListener(TreeModelListener l) {
		if (listeners != null)
			listeners.remove(l);
	}

	public void valueForPathChanged(TreePath path, Object newValue) {
	}
}

// /////////////////////////////////////////////////////////////

class SnippetRenderer implements TreeCellRenderer {
	FastLabel fastlabel = new FastLabel(false);
	Icon element = null;
	Icon elementAny = null;	
	Icon folder = null;
	Icon folder_closed = null;
	Icon attribute = null;
	
	public SnippetRenderer() {
		try {
			element = new ImageIcon(ClassLoader
					.getSystemResource("images/element.png"));
			elementAny = new ImageIcon(ClassLoader
					.getSystemResource("images/element_refresh.png"));			
			attribute = new ImageIcon(ClassLoader
					.getSystemResource("images/attribute.png"));			
			folder = new ImageIcon(ClassLoader
					.getSystemResource("images/folder.png"));
			folder_closed = new ImageIcon(ClassLoader
					.getSystemResource("images/folder_closed.png"));
		} catch (Throwable th) {
			System.err.println("Can't init icons ? : " + th.getMessage());
		}
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {

		Icon icon = null;
		if ( !( value instanceof FPNode ) ) {
			fastlabel.setText( "?" );
			fastlabel.setIcon( null );
			return fastlabel;
		}
		FPNode node = (FPNode) value;
		String label;

		if ( node.isAttribute() ) {
			label = node.getNodeValue();
			icon = attribute;
		} else {
			if ( node.matchContent( "item" ) ) {
				String name = node.getAttribute( "name" );
				label = name;
				icon = element;

				String hlp = node.getAttribute( "hlp" );

				if ( node.getAttribute( "param", "" ).indexOf( "*" ) > -1 )
					icon = elementAny;

				if ( hlp != null )
					label = label + " (" + hlp + ")";

			} else {
				if (node.matchContent("group"))
					label = node.getAttribute("name");
				else
					label = node.getContent();
				icon = !expanded ? folder_closed : folder;
			}

		}

		fastlabel.setIcon(icon);
		fastlabel.setText(label);

		if (selected) {
			fastlabel.setForeground(UIManager
					.getColor("List.selectionForeground"));
			fastlabel.setBackground(UIManager
					.getColor("List.selectionBackground"));
		} else {
			Color foreground = tree.getForeground();
			Color background = tree.getBackground();
			fastlabel.setForeground(foreground);
			fastlabel.setBackground(background);
		}

		if ( "false".equals( node.getAttribute( "test", "true" ) ) )
			fastlabel.setForeground( Color.red );

		return fastlabel;
	}
}

private String resolveNodeToXMLText( FPNode elementNode ) {
	mapResolveOcc = null;
	FPNode root = extractXMLNode( elementNode );
	resolveNodes( elementNode, root );
	mapResolveOcc = null;
	return root.getRawXML();
}

private FPNode extractXMLNode( FPNode ref ) {
	FPNode newRoot = new FPNode( FPNode.TAG_NODE,
			ref.getAttribute( "name" ) );
	for ( int i = 0; i < 100; i++ ) {
		String val = ref.getAttribute( "att" + i );
		if ( val == null )
			break;
		int j = val.indexOf( "=" );
		if ( j > -1 )
			newRoot.setAttribute( 
					val.substring( 0, j ), 
					val.substring( j + 1 ) );
	}

	// Force a text for explicit closing tag
	if ( ref.getAttribute( "param" ) != null
			&& ref.getAttribute( "param" ).startsWith( "T" ) ) {
		newRoot.appendChild( new FPNode( FPNode.TEXT_NODE, TEXT_TEMPLATE ) );
	}

	return newRoot;
}

private HashMap mapResolveOcc = null;

private void resolveOcc( FPNode n ) {
	String param = n.getAttribute( "param" );
	if ( param.indexOf( "*" ) > -1 ) {
		String name = n.getAttribute( "name" );
		int ii = new Integer( 
				EditixFactory.buildAndShowInputDialog( 
					"Number of " + name + " ?", "1" ) 
		);
		n.setApplicationObject( ii );
	}
	for ( int i = 0; i < n.childCount(); i++ ) {
		resolveOcc( n.childAt( i ) );
	}
}

private void resolveNodes( FPNode refNode, FPNode newNode ) {
	resolveOcc( refNode );
	resolveNodesOcc( refNode, newNode );
}

private void resolveNodesOcc( FPNode refNode, FPNode newNode ) {
	
	for ( int i = 0; i < refNode.childCount(); i++ ) {
		FPNode n = refNode.childAt( i );
		int occ = 1;
		if ( n.getApplicationObject() instanceof Integer ) {
			occ = ( Integer )n.getApplicationObject();
		}
		for ( int j = 0; j < occ; j++ ) {
		
			FPNode nw = extractXMLNode( n );
			newNode.appendChild( nw );
			if ( !n.isLeaf() ) {
				resolveNodesOcc( n, nw );
			}
		}
	}		
}

/////////////////////////////////////////////////////////////////////////:

DataFlavor flavors[] = { DataFlavor.stringFlavor };

class NodeTransfertHandler extends TransferHandler {

	public int getSourceActions(JComponent c) {
		return TransferHandler.MOVE;
	}
	
	public boolean canImport( JComponent comp, DataFlavor[] transferFlavors ) {
		
		if ( comp instanceof JTree ) {
			if ( transferedPath != null ) { 	// Internal drag'n drop
				return true;
			} else {
/*				if ( transferFlavors.length > 0 ) {
					if ( "Simple node".equals( transferFlavors[ 0 ].getHumanPresentableName() ) ) {
						return true;
					}
				} */
			}
		}

		return false;
	}

	private TreePath transferedPath = null;

	protected Transferable createTransferable( JComponent c ) {
		
		transferedPath = t.getSelectionPath();
		Transferable transferable = new Transferable() {			
			String txt = null;

			public Object getTransferData( DataFlavor flavor )
					throws UnsupportedFlavorException, IOException {					
				if ( isDataFlavorSupported( flavor ) ) {
					
					if ( txt != null )
						return txt;
					
					FPNode n = ( ( FPNode )transferedPath.getLastPathComponent() );
					if ( n.matchContent( "group" ) )
						txt = n.getAttribute( "name" );
					else
						txt = resolveNodeToXMLText( n );

					// Delete the text marker
					txt = txt.replaceAll( TEXT_TEMPLATE, "" );

					return txt;
				}					
				return null;
			}
			public DataFlavor[] getTransferDataFlavors() {
				return flavors;
			}
			public boolean isDataFlavorSupported(DataFlavor flavor) {
				return flavor.equals( DataFlavor.stringFlavor );
			}
		};
		return transferable;
	}

	protected void exportDone(JComponent source, Transferable data, int action) {
		super.exportDone(source, data, action);
		dragAndDropInUse = false;
	}		

	public boolean importData(JComponent arg0, Transferable arg1) {		
		try {
			TreePath tp = t.getSelectionPath();
			if ( tp == null ) {
				return false;
			}
			FPNode currentNode = ( FPNode )tp.getLastPathComponent();
			FPNode movingNode = null;

			if ( transferedPath != null )
				movingNode = ( FPNode )transferedPath.getLastPathComponent();

			if ( !tp.equals( transferedPath ) ) {
				FPNode newParentNode = currentNode;
				if ( transferedPath != null )
					movingNode.getFPParent().removeChildNode( movingNode );	// Detach
				newParentNode.appendChild( movingNode );	// Attach
				refresh();
				selectNode( movingNode );
				return true;
			}
			return false;
		} finally {
			dragAndDropInUse = false;
			transferedPath = null;
		}
	}
}

}
