package com.japisoft.editix.ui.panels.project;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
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

import org.apache.batik.dom.util.DocumentDescriptor;

import com.japisoft.editix.action.file.NextSelectionAction;
import com.japisoft.editix.action.file.OpenProjectAction;
import com.japisoft.editix.document.DocumentModel;
import com.japisoft.editix.project.ProjectManager;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.application.descriptor.ActionModel;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.ui.FastLabel;
import com.japisoft.framework.ui.MultiChoiceButton;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.XMLDocumentInfo;

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
public class ProjectUI extends JPanel implements 
		MouseListener, 
		MouseMotionListener, 
		ActionListener,
		TreeSelectionListener {

	static final String OPEN_CMD = "open";	
	static final String REN_CMD = "ren";
	static final String LOCK_CMD = "lock";
	static final String DEL_CMD = "del";
	static final String ADDD_CMD = "addd";
	static final String ADDF_CMD = "addf";
	static final String ADDFS_CMD = "addfs";
	public static final String ADDF2_CMD = "addf2";
	static final String CHECK_CMD = "check";
	static final String ZIP_CMD = "zip";
	
	static final String[] TOOLBAR_ACTIONS = {
		ADDF_CMD, ADDD_CMD, REN_CMD, DEL_CMD, LOCK_CMD
	};

	static final String[] POPUP_ACTIONS = {
		OPEN_CMD, ADDF_CMD, ADDFS_CMD, ADDD_CMD, REN_CMD, DEL_CMD, CHECK_CMD, LOCK_CMD,
		null, ZIP_CMD
	};
		
	private static final String LOCK_PNG = "lock.png";
	private static final String LOCK_OPEN_PNG = "lock_open.png";

	private JTree t;
	private JToolBar tb;
	private NodeTransfertHandler th;

	public ProjectUI() {
		setLayout( new BorderLayout() );

		tb = new JToolBar();

		for ( int i = 0; i < TOOLBAR_ACTIONS.length; i++ ) {
			tb.add( EditixFrame.THIS.getBuilder().getActionById( "prj." + TOOLBAR_ACTIONS[ i ] ) );
		}
		
		// Add the filter button
		AbstractAction aa = new AbstractAction() {			
			public void actionPerformed(ActionEvent e) {
				if ( e.getActionCommand() != null ) {
					int index = Integer.parseInt( e.getActionCommand() );
					
					XMLDocumentInfo d = null;
					if ( index > -1 ) {
						d = DocumentModel.getDocumentAt( index );
					}
					
					FilterActivatorAction faa = new FilterActivatorAction( d );
					faa.actionPerformed( e );
				}
			}
		};

		int j = 1;

		aa.putValue( "label" + ( j ), "All" );
		aa.putValue( "cmd" + ( j++ ), "-1" );
		
		for ( int i = 0; i < DocumentModel.getDocumentCount(); i++ ) {
			aa.putValue( "label" + ( j ), DocumentModel.getDocumentAt( i ).getDocumentDescription() );
			aa.putValue( "icon" + ( j ), DocumentModel.getDocumentAt( i ).getDocumentIcon() );
			aa.putValue( "cmd" + ( j++ ), Integer.toString( i ) );
		}

		MultiChoiceButton mcb = new MultiChoiceButton( aa );
		tb.add( mcb );

		add(tb, BorderLayout.NORTH);

		boolean projectLock = Preferences.getPreference(
				Preferences.SYSTEM_GP,
				"project.lock", 
				true );

		add(
				new JScrollPane( t = new JTree() ), 
				BorderLayout.CENTER
		);

		clean();
		t.setCellRenderer(new ProjectRenderer());
		t.setTransferHandler( th = new NodeTransfertHandler() );
		setEnabledAction( DEL_CMD, false );

		getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT ).put( 
				KeyStroke.getKeyStroke(
					KeyEvent.VK_DELETE, 0
				), "delete" );

		getActionMap().put( 
				"delete", 
				EditixFrame.THIS.getBuilder().getActionById( "prj." + DEL_CMD ) );
		

	}

	private JPopupMenu buildPopupMenu() {
		JPopupMenu menu = new JPopupMenu();
		for ( int i = 0; i < POPUP_ACTIONS.length; i++ ) {
			if (  POPUP_ACTIONS[ i ] == null )
				menu.addSeparator();
			else
				menu.add( EditixFrame.THIS.getBuilder().getActionById( "prj." + POPUP_ACTIONS[ i ] ) );
		}
		
		JMenu filter = new JMenu( "Filter" );
		filter.add( new FilterActivatorAction( null ) );
		filter.addSeparator();
		for ( int i = 0; i < DocumentModel.getDocumentCount(); i++ ) {
			filter.add(
					new FilterActivatorAction( 
							DocumentModel.getDocumentAt( i ) ) );
		}
		
		menu.addSeparator();
		menu.add( filter );
		
		return menu;
	}

	public void init() {
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
		ActionModel.setEnabled( "prj." + name, enabled );
	}

	public void valueChanged( TreeSelectionEvent e ) {
		TreePath tp = t.getSelectionPath();
		if ( tp != null ) {
			FPNode node = ( FPNode )tp.getLastPathComponent();
			setEnabledAction( DEL_CMD, !node.isRoot() );
			setEnabledAction( ADDF_CMD, node.isRoot()
					|| node.matchContent( "group" ) );
			setEnabledAction( ADDFS_CMD, node.isRoot()
					|| node.matchContent( "group" ) );			
			setEnabledAction( ADDD_CMD, node.isRoot()
					|| node.matchContent( "group" ) );
			setEnabledAction( REN_CMD, node.isRoot() || 
					node.matchContent( "group" ) );
			setEnabledAction( OPEN_CMD, node.matchContent( "item" ) );
		}
	}

	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if ( LOCK_CMD.equals( cmd ) ) {
			lock();
		} else {
			if ( t.getSelectionPath() == null ) {
				return;
			}
			if (ADDF_CMD.equals(cmd)) {
				addFile();
			} else if (ADDD_CMD.equals(cmd)) {
				addGroup();
			} else if (DEL_CMD.equals(cmd)) {
				delete();
			} else if ( REN_CMD.equals( cmd ) ) {
				rename( (FPNode) t.getSelectionPath().getLastPathComponent() );
			} else if ( CHECK_CMD.equals( cmd ) ) {
				ProjectManager.checkFilesPath();
			} else if ( OPEN_CMD.equals( cmd ) ) {
				openSelectedItem();
			} else if ( ADDF2_CMD.equals( cmd ) ) {
				addFile( true );
			} else if ( ADDFS_CMD.equals( cmd ) ) {
				addFiles( false );
			}

			refresh();
		}
	}

	void refresh() {
		TreePath tp = t.getSelectionPath();
		((ProjectTreeModel) t.getModel()).refresh();
		t.repaint();
		if ( tp != null )
			t.setSelectionPath( tp );
	}

	private void addFile() {
		addFile( false );
	}

	private void addFile(boolean rootOnly) {
		FPNode parentNode = null;
		if ( rootOnly ) {
			parentNode = ( FPNode )t.getModel().getRoot();
		} else
			parentNode = ( FPNode )t.getSelectionPath()
				.getLastPathComponent();
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		addFile( container, parentNode );
	}
	
	private void addFiles(boolean rootOnly) {
		FPNode parentNode = null;
		if ( rootOnly ) {
			parentNode = ( FPNode )t.getModel().getRoot();
		} else
			parentNode = ( FPNode )t.getSelectionPath()
				.getLastPathComponent();
		boolean added = false;
		for ( int i = 0; i < EditixFrame.THIS.getXMLContainerCount(); i++ ) {
			XMLContainer container = EditixFrame.THIS.getXMLContainer( i );
			if ( container != null && container.getCurrentDocumentLocation() != null ) {
				addFile( container, parentNode );
				added = true;
			}
		}
		if ( !added ) {
			EditixFactory
			.buildAndShowErrorDialog("You need to have at least one opened XML document !");			
		}
	}

	private void addFile( XMLContainer container, FPNode parentNode ) {
		if ( container == null ) {
			EditixFactory
					.buildAndShowErrorDialog("You need to have an opened XML document !");
		} else {
			
			if ( container.hasProperty( "save.delegate" ) ) {

				EditixFactory.buildAndShowWarningDialog( "Can't add this file" );
				
			} else {
			
				if ( container.getCurrentDocumentLocation() == null ) {
					EditixFactory
					.buildAndShowErrorDialog("You must save your file before adding to the project");
				} else {
					FPNode node = ProjectManager.addItem(container, parentNode);
					if ( "select".equals( node.getApplicationObject() ) )
						selectNode( node );
					else
						expandNode( parentNode );
				}
			
			}
		}
	}
	
	private void addGroup() {
		String name = EditixFactory.buildAndShowInputDialog("Group name ?");
		if (name != null) {
			FPNode parentNode = (FPNode) t.getSelectionPath()
					.getLastPathComponent();
			selectNode(ProjectManager.addGroup(name, parentNode));
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
									+ selectedNode.getAttribute("path"))) {
						continue;
					}
				}
				ProjectManager.delete(selectedNode);
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
	
	static boolean isLocked() {
		return Preferences.getPreference(Preferences.SYSTEM_GP, "project.lock",
				true);
	}

	private void lock() {
		boolean projectLock = isLocked();

		projectLock = !projectLock;

		Preferences.setPreference(Preferences.SYSTEM_GP, "project.lock",
				projectLock);

		Action a = ActionModel.restoreAction( "lock" );
		a.putValue(
				Action.SMALL_ICON,
				new ImageIcon(ClassLoader.getSystemResource("images/"
						+ (projectLock ? LOCK_PNG : LOCK_OPEN_PNG))));
	}

	public void clean() {
		t.setModel(new ProjectTreeModel(ProjectManager.getProjectRoot()));
		t.setSelectionPath(new TreePath(ProjectManager.getProjectRoot()));
	}

	public void loadProject(String file) {
		t.setModel(new ProjectTreeModel(ProjectManager.getProjectRoot()));
		t.setSelectionPath(new TreePath(ProjectManager.getProjectRoot()));
	}

	boolean dragAndDropInUse = false;
	
	public void mouseDragged(MouseEvent e) {
		if ( !dragAndDropInUse ) {
			if ( e.getPoint().distance( initLocation ) > 10 ) {
				th.exportAsDrag( t, e, TransferHandler.MOVE );
				TreePath tp = t.getPathForLocation( e.getX(), e.getY() );
				if ( tp != null )
					t.setSelectionPath( tp );
				dragAndDropInUse = true;
			}
		}
	}

	public void mouseReleased(MouseEvent e) {
		dragAndDropInUse = false;
		initLocation = null;
	}
	
	public void mouseMoved(MouseEvent e) {
		TreePath tp = t.getPathForLocation( e.getX(), e.getY() );
		if ( tp != null ) {
			FPNode node = ( FPNode )tp.getLastPathComponent();
			String path = node.getAttribute( "path" );			
			( (ProjectRenderer)t.getCellRenderer() ).fastlabel.setToolTipText(
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
			openSelectedItem();
		}
	}

	private void openSelectedItem() {
		if ( t.getSelectionPath() == null )
			return;
		FPNode node = ( FPNode )t.getSelectionPath()
				.getLastPathComponent();
		if ( node.matchContent( "item" ) )
			ProjectManager.openItem( node );
	}

	private void rename( FPNode node ) {
		String oldName = ProjectManager.getGroupName(node);
		String newName = EditixFactory.buildAndShowInputDialog(
				"New name", oldName);
		if (newName != null) {

			StringBuffer sb = new StringBuffer();
			
			// Clean newname
			for ( int i = 0; i < newName.length(); i++ ) {
				if ( Character.isLetterOrDigit( newName.charAt( i ) ) )
					sb.append( newName.charAt( i ) );
			}
			
			if ( sb.length() == 0 )
				sb.append( "MyProject" );

			newName = sb.toString();
			
			if (!newName.equals(oldName)) {
				ProjectManager.updateGroupName(newName, node);
				refresh();
			}
		}
	}

	private Point initLocation = null;
	
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {
		initLocation = e.getPoint();
	}

	// ///////////////////////////////////////////////////////////////

	class ProjectTreeModel implements TreeModel {

		private FPNode root;

		ProjectTreeModel(FPNode root) {
			this.root = root;
		}

		private String type = null;

		public void setFilter( String type ) {
			this.type = type;
			refresh();
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
			if (sn.isRoot() || sn.matchContent("group")) {
				if ( type == null )
					return sn.childAt(index);
				else {
					int cpt = -1;
					for ( int i = 0; i < sn.childCount(); i++ ) {
						FPNode c = sn.childAt( i );
						if ( c.matchContent( "group" ) ) {
							cpt++;
						} else
						if ( c.matchContent( "item" ) ) {
							if ( type.equals( c.getAttribute( "type" ) ) )
								cpt++;
						}
						if ( cpt == index )
							return c;
					}
				}
			}
			return null;
		}

		public int getChildCount(Object parent) {
			FPNode sn = (FPNode) parent;
			if ( sn.isRoot() || 
					sn.matchContent( "group" ) ) {
				if ( type == null )
					return sn.childCount();
				else {
					int cpt = 0;
					for ( int i = 0; i < sn.childCount(); i++ ) {
						FPNode c = sn.childAt( i );
						if ( c.matchContent( "group" ) ) {
							cpt++;
						} else
						if ( c.matchContent( "item" ) ) {
							if ( type.equals( c.getAttribute( "type" ) ) )
								cpt++;
						}
					}
					return cpt;
				}
			}
			return 0;
		}

		public int getIndexOfChild(Object parent, Object child) {
			FPNode sn = (FPNode) parent;
			if (sn.isRoot() || sn.matchContent("group")) {
				return sn.childNodeIndex((FPNode) child);
			}
			return -1;
		}

		public Object getRoot() {
			return root;
		}

		public boolean isLeaf(Object node) {
			FPNode sn = (FPNode) node;
			return (sn.matchContent("item") || sn.getChildCount() == 0);
		}

		public void removeTreeModelListener(TreeModelListener l) {
			if (listeners != null)
				listeners.remove(l);
		}

		public void valueForPathChanged(TreePath path, Object newValue) {
		}

	}

	// /////////////////////////////////////////////////////////////

	class ProjectRenderer implements TreeCellRenderer {
		FastLabel fastlabel = new FastLabel(false);
		Icon drive = null;
		Icon document = null;
		Icon folder = null;
		Icon folder_closed = null;
		
		public ProjectRenderer() {
			try {
				drive = new ImageIcon(ClassLoader
						.getSystemResource("images/diskdrive.png"));
				document = new ImageIcon(ClassLoader
						.getSystemResource("images/document.png"));
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

			if (node.matchContent("item")) {
				String type = node.getAttribute("type");
				XMLDocumentInfo doc = DocumentModel.getDocumentForType(type);
				icon = doc.getDocumentIcon();
				String path = node.getAttribute("path");
				int i = path.lastIndexOf('/');
				if (i == -1)
					i = path.lastIndexOf('\\');
				if (i == -1)
					label = path;
				else
					label = path.substring(i + 1);
			} else {
				if (node.matchContent("group"))
					label = node.getAttribute("path");
				else
					label = node.getContent();

				icon = !expanded ? folder_closed : folder;
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
				fastlabel.setForeground( Color.RED );

			return fastlabel;
		}
	}
	
	/////////////////////////////////////////////////////////////////////////:
	
	DataFlavor flavors[] = { DataFlavor.stringFlavor };

	class NodeTransfertHandler extends TransferHandler {

		public int getSourceActions(JComponent c) {
			return TransferHandler.MOVE;
		}

		public void exportAsDrag(JComponent comp, InputEvent e, int action) {
			super.exportAsDrag(comp, e, action);
		}		
		
		public boolean canImport( JComponent comp, DataFlavor[] transferFlavors ) {			
			if ( transferedPath != null ) { 	// Internal drag'n drop
				return ( comp instanceof JTree );
			} else {
				return 
					transferFlavors[ 0 ].isFlavorJavaFileListType();
			}
		}

		TreePath transferedPath = null;
		
		protected Transferable createTransferable( JComponent c ) {
			transferedPath = t.getSelectionPath();
			Transferable transferable = new Transferable() {
				public Object getTransferData( DataFlavor flavor )
						throws UnsupportedFlavorException, IOException {					
					if ( isDataFlavorSupported( flavor ) ) {
						String path = ( ( FPNode )transferedPath.getLastPathComponent() ).getAttribute( "path" );
						if ( path != null ) {
							path = path.replace( '\\', '/' );
						}
						return path;
					}					
					return null;
				}
				public DataFlavor[] getTransferDataFlavors() {
					return flavors;
				}
				public boolean isDataFlavorSupported(DataFlavor flavor) {
					return flavor.equals( DataFlavor.stringFlavor ) || 
						flavor.isFlavorJavaFileListType();
				}
			};
			return transferable;
		}

		protected void exportDone(JComponent source, Transferable data, int action) {
			super.exportDone(source, data, action);
			dragAndDropInUse = false;
		}		

		private FPNode addDirectory( File f, FPNode parentNode ) {
			FPNode movingNode = new FPNode( FPNode.TAG_NODE, "group" );
			movingNode.setAttribute( "path", f.getName() );
			
			String[] l = f.list();
			if ( l != null ) {
				for ( int i = 0; i < l.length; i++ ) {
					File f2 = new File( f, l[ i ] );
					if ( f2.isDirectory() ) {
						addDirectory( f2, movingNode );
					} else {
						if ( f2.isFile() ) {
							FPNode fNode = new FPNode( FPNode.TAG_NODE, "item" );	
							String fileName = f2.toString();
							fNode.setAttribute( "path", fileName );
							XMLDocumentInfo info = DocumentModel.getDocumentByFileName( fileName );
							fNode.setAttribute( "type", info.getType() );
							movingNode.appendChild( fNode );
						}
					}
				}
			}

			parentNode.appendChild( movingNode );
			return movingNode;
		}

		public boolean importData(JComponent arg0, Transferable arg1) {
			try {
				TreePath tp = t.getSelectionPath();
				if ( tp == null ) {
					return false;
				}
				FPNode currentNode = ( FPNode )tp.getLastPathComponent();				
				if ( !( currentNode.matchContent( "group" )
					|| currentNode.isRoot() ) ) {
					try {
						if ( arg1.getTransferData( DataFlavor.javaFileListFlavor ) != null ) {
							currentNode = currentNode.getFPParent(); 
						} else
							return false;
					} catch (UnsupportedFlavorException e) {
						return false;
					} catch (IOException e) {
						return false;
					}
				}		

				FPNode movingNode = null;
				
				if ( transferedPath != null )
					movingNode = ( FPNode )transferedPath.getLastPathComponent();
				else {
					try {
						java.util.List list = ( java.util.List )arg1.getTransferData(
							DataFlavor.javaFileListFlavor
						);
						if ( list != null && list.size() > 0 ) {
							for ( int i = 0; i < list.size(); i++ ) {
								String fileName = list.get( i ).toString();		

								if ( ProjectManager.hasItem( fileName ) ) {
									EditixFactory.buildAndShowErrorDialog( "File [" + fileName + "] already added" );
									transferedPath = null;
									return false;
								}
								File f = new File( fileName );
								if ( f.isDirectory() ) {
									addDirectory( f, currentNode );
									refresh();
									break;
								} else {
									
									if ( f.toString().endsWith( ".pre" ) ) {
										// Import a project
										OpenProjectAction.openProject( false, f.toString() );
										return false;
									}

									movingNode = new FPNode( FPNode.TAG_NODE, "item" );							
									movingNode.setAttribute( "path", fileName );
									XMLDocumentInfo info = DocumentModel.getDocumentByFileName( fileName );
									movingNode.setAttribute( "type", info.getType() );
									if ( i < list.size() - 1 )
										currentNode.appendChild( movingNode );
								}
							}
						}
						transferedPath = null; 
					}
					catch (UnsupportedFlavorException e) {}
					catch (IOException e) {}
				}

				if ( !tp.equals( transferedPath ) || 
						( transferedPath == null && movingNode != null ) ) {
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

	class FilterActivatorAction extends AbstractAction {
		private String type = null;
		public FilterActivatorAction( XMLDocumentInfo di ) {
			if ( di != null ) {
				putValue( 
						Action.SMALL_ICON, 
						di.getDocumentIcon() );
				putValue( 
						Action.NAME, 
						di.getDocumentDescription() );
				this.type = di.getType();
			} else
				putValue( Action.NAME, "No filter" );
		}
		public void actionPerformed(ActionEvent e) {
			( ( ProjectTreeModel )t.getModel() ).setFilter( type );
		}
	}

}
