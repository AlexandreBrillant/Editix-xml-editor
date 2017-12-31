package com.japisoft.editix.ui.panels.project2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import org.apache.commons.io.FileUtils;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableNode;

import com.japisoft.editix.document.DocumentModel;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.panels.project2.synchro.SynchroChoosePanel;
import com.japisoft.editix.ui.panels.project2.synchro.Synchronizer;
import com.japisoft.editix.ui.panels.project2.synchro.SynchronizerListener;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.ApplicationModel.ApplicationModelListener;
import com.japisoft.framework.desktop.SystemDesktop;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.ui.FastLabel;
import com.japisoft.framework.ui.toolkit.Toolkit;
import com.japisoft.framework.xml.Encoding;
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
public class ProjectUI extends JPanel 
		implements 
			MouseListener, 
			MouseMotionListener, 
			ApplicationModelListener, 
			SynchronizerListener,
			TreeSelectionListener,
			TreeExpansionListener {

	private JXTreeTable tree = null;
	private JScrollPane sp = null;
	private JLabel message = null;
	
	public ProjectUI() {	
		tree = 
			new JXTreeTable();		

		tree.getActionMap().put( "cut", new CutAction() );
		tree.getActionMap().get( "cut" ).setEnabled( false );		

		tree.getActionMap().put( "copy", new CopyAction() );
		tree.getActionMap().put( "paste", new PasteAction() );
		
		tree.getActionMap().put( "refresh", new RefreshAction() );
		
		tree.getActionMap().put( "rename", new RenameAction() );
		
		tree.getActionMap().put( "newDirectory", new NewDirectoryAction() );

		setLayout( new BorderLayout() );
		JToolBar tb = createToolBar1();
		add( 
			tb, 
			BorderLayout.NORTH 
		);
				
		sp = 
			new JScrollPane( tree ); 
		
		add( 
			sp,
			BorderLayout.CENTER
		);
		
		tree.setTreeCellRenderer(
			new ProjectRenderer() 
		);
		
		tree.getInputMap().put( 
				KeyStroke.getKeyStroke(
					KeyEvent.VK_C, 
					java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() 
				), 
				"copy" 
		);
		
		tree.getInputMap().put( 
			KeyStroke.getKeyStroke(
				KeyEvent.VK_X, 
				java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() 
			), 
			"cut" 
		);

		tree.getInputMap().put( 
				KeyStroke.getKeyStroke(
					KeyEvent.VK_DELETE,0
				), 
				"cut" 
			);
				
		tree.getInputMap().put( 
			KeyStroke.getKeyStroke(
				KeyEvent.VK_P,
				java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
			), 
			"paste" 
		);		
		
		tree.getInputMap().put(
			KeyStroke.getKeyStroke(
				KeyEvent.VK_F5, 0
			), 
			"refresh"
		);
		
		tree.getInputMap().put(
				KeyStroke.getKeyStroke(
					KeyEvent.VK_F2, 0
				), 
				"rename"
			);
		
		add( message = new JLabel(), BorderLayout.SOUTH );
	}

	private boolean visibleProject = false;

	private boolean init = false;
	private boolean init2 = false;	// Due to bug
	
	@Override
	public void addNotify() {
		super.addNotify();
		if ( !init ) {
			if ( !init2 ) {
				sp.getColumnHeader().getView().addMouseListener( this );
				init2 = true;
			}
			tree.addMouseListener( this );
			tree.addMouseMotionListener( this );
			if ( project != null ) {
				setProject( project );
			}
			visibleProject = true;
			init = true;
		}
		ApplicationModel.addApplicationModelListener( this );
		tree.addTreeSelectionListener( this );
		tree.addTreeExpansionListener( this );
	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		if ( init ) {
			if ( sp.getColumnHeader().getView() != null )
				sp.getColumnHeader().getView().removeMouseListener( this );
			tree.removeMouseListener( this );
			tree.removeMouseMotionListener( this );
			tree.removeTreeSelectionListener( this );
			tree.removeTreeExpansionListener( this );
			init = false;
		}
		
		ApplicationModel.removeApplicationModelListener( this );
	}
	
	// Upload / Download

	public void start() {
		synchroDownAction.setEnabled( false );
		synchroUpAction.setEnabled( false );
	}

	public void stop() {
		synchroDownAction.setEnabled( true );
		synchroUpAction.setEnabled( true );		
	}

	public void fireApplicationData( String key, Object... values ) {
		if ( "open".equals( key ) ) {
			if ( values[ 0 ] instanceof File ) {
				File f = ( File )values[ 0 ];
				String type = null;
				if ( values.length > 1 )
					type = ( String )values[ 1 ];
				if ( project != null ) {
					project.openFile( f.toString(), type );
				}
			}
		} else
		if ( "close".equals( key ) ) {
			if ( values[ 0 ] instanceof XMLContainer ) {
				XMLContainer container = ( XMLContainer )values[ 0 ];
				String location = container.getCurrentDocumentLocation();
				Map m = container.getPropertiesMap();
				if ( project != null )
					project.closeFile( location, m );
			}
		} else
		if ( "document.saveAs".equals( key ) ) {
			String filePath = ( String )values[ 0 ];
			if ( filePath != null ) {
				if ( project.contains( new File( filePath ) ) ) {
					// Must refresh the current project
					refresh( project.getRoot() );
				}
			}
		}
	}

	private Project project = null;
	private NodeTreeTableModel treeModel = null;

	public void setProject( Project project ) {
		this.project = project;
		if ( visibleProject ) {
			message.setText( "Preparing project..." );
			SwingUtilities.invokeLater(
					new Runnable() {
						public void run() {
							tree.setTreeTableModel(
									treeModel = new NodeTreeTableModel( 
										ProjectUI.this.project.getRoot()
									)
								);
							tree.getColumn( 0 ).setPreferredWidth( 150 );
							if ( Preferences.getPreference( "file", "restoreProject", false ) ) {
								restoreNodeState();	
							}
							message.setText( "" );
						}
					} );
		}
	}

	private void restoreNodeState() {
		String[] state = project.getNodeState();
		if ( state != null && 
				state.length > 0 ) {
			for ( String path : state ) {
				tree.expandPath( getTreePath( path ) );
			}
		}		
	}
	
	private TreePath getTreePath( String path ) {
		Node root = project.getRoot();
		String[] parts = path.split( "/" );
		TreePath result = new TreePath( root );
		for ( String part : parts ) {
			for ( int i = 0; i < root.getChildCount(); i++ ) {
				if ( part.equalsIgnoreCase( 
					root.getChildAt( i ).toString() ) ) {
					result = result.pathByAddingChild(
						root.getChildAt( i )
					);
					root = ( Node )root.getChildAt( i );
				}
			}
		}
		return result;
	}

	public void save() throws IOException {
		if ( project != null )
			project.save();
	}

	private Action synchroDownAction,synchroUpAction;
	
	private JToolBar createToolBar1() {
		JToolBar tb = new JToolBar();
		tb.setFloatable( false );
		tb.add( tree.getActionMap().get( "newDirectory" ) );
		tb.add( tree.getActionMap().get( "refresh" ) );
		tb.add( synchroDownAction = new SynchroDownAction() );
		tb.add( synchroUpAction = new SynchroUpAction() );
		tb.add( tree.getActionMap().get( "cut" ) );
		synchroDownAction.setEnabled( false );
		synchroUpAction.setEnabled( false );
		return tb;
	}

	public Node getSelection() {
		TreePath tp = tree.getTreeSelectionModel().getSelectionPath();
		if ( tp != null )
			return ( DefaultNode )tp.getLastPathComponent();
		return null;
	}

	public Node[] getSelections() {
		TreePath[] tp = tree.getTreeSelectionModel().getSelectionPaths();
		if ( tp != null ) {
			Node[] nodes = new Node[ tp.length ];
			for ( int i = 0; i < tp.length; i++ )
				nodes[ i ] = ( Node )tp[ i ].getLastPathComponent();
			return nodes;
		}
		return null;
	}

	private void refresh( Node node ) {
		if ( node.isLeaf() )	// File selection
			node = ( Node )node.getParent();
		node.reload();
		( ( NodeTreeTableModel )tree.getTreeTableModel() ).reload( node );
		restoreNodeState();
	}
	
	private JPopupMenu popup = null;
	private JMenu encodingPopupMenu = null;

	private void showPopup( MouseEvent e ) {
		Node node = getSelection();
		if ( popup == null ) {
			popup = new JPopupMenu();
			popup.add( tree.getActionMap().get( "newDirectory" ) );
			popup.add( tree.getActionMap().get( "rename" ) );
			JMenu m = new JMenu( "Open As..." );
			m.setName( "openas" );
			for ( int i = 0; i < DocumentModel.getDocumentCount(); i++ ) {
				m.add( new OpenAsAction( DocumentModel.getDocumentAt( i ) ) );
			}
			popup.add( m );
			popup.addSeparator();
			popup.add( tree.getActionMap().get( "cut" ) );
			popup.add( tree.getActionMap().get( "copy" ) );			
			popup.add( tree.getActionMap().get( "paste" ) );
			popup.addSeparator();
			popup.add( new OpenExplorer() );
			
			JMenu mm = new JMenu( "Copy path" );
			mm.add( new CopyPath( false ) );
			mm.add( new CopyPath( true ) );						
			popup.add( mm );
			popup.addSeparator();


			encodingPopupMenu = new JMenu( "Default encoding" );

			popup.add( encodingPopupMenu );
			popup.addSeparator();
			
			popup.add( tree.getActionMap().get( "refresh" ) );
		}
		
		encodingPopupMenu.removeAll();
		String[] encoding = Encoding.XML_ENCODINGS;
		ButtonGroup bg = new ButtonGroup();
		for ( int i = 0; i < encoding.length; i++ ) {
			EncodingAction action = new EncodingAction( encoding[ i ] );
			JRadioButtonMenuItem item = new JRadioButtonMenuItem( action );
			if ( node != null ) {
				if ( encoding[ i ].equals( node.getProject().getEncoding( node.getPath().toString() ) ) ) {
					item.setSelected( true );
				}
			}
			encodingPopupMenu.add( item );
		}
	
		popup.show( tree, e.getX(), e.getY() );
	}
	
	public void mouseClicked(MouseEvent e) {
		
		if ( e.getSource() == tree ) {
			if ( e.getClickCount() > 1 ) {
				Node node = getSelection();
				if ( node != null ) {
					open( node );
				}
			}
		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		if ( e.isPopupTrigger() ) {
			showPopup( e );
		}
	}

	public void mouseReleased(MouseEvent e) {
		if ( e.isPopupTrigger() ) {
			showPopup( e );
		}		
	}

	public void mouseDragged(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
		TreePath tp = tree.getPathForLocation( e.getX(), e.getY() );
		if ( tp != null ) {
			Node n = ( Node )tp.getLastPathComponent();
			tree.setToolTipText( n.getPath().toString() );
		}
	}

	public void valueChanged(TreeSelectionEvent e) {
		synchroDownAction.setEnabled( true );
		synchroUpAction.setEnabled( true );
		tree.getActionMap().get( "cut" ).setEnabled( true );
		
		Node[] nodes = getSelections();
		if ( nodes == null || nodes.length == 0 ) {
			message.setText( "" );
		}
		else {
			if ( nodes.length == 1 ) {
				message.setText( "1 item selected" );
			} else
				message.setText( nodes.length + " items selected" );
		}
	}

	public void treeCollapsed(TreeExpansionEvent event) {
		saveTreeState();
	}
	public void treeExpanded(TreeExpansionEvent event) {
		saveTreeState();
	}

	private void saveTreeState() {
		Enumeration e = tree.getExpandedDescendants( 
			new TreePath( 
				project.getRoot() ) 
		);
		ArrayList<String> expandedPath = new ArrayList<String>();
		while ( e.hasMoreElements() ) {
			TreePath tp = ( TreePath )e.nextElement();
			StringBuffer sb = new StringBuffer();
			for ( int i = 0; i < tp.getPathCount(); i++ ) {
				if ( sb.length() > 0 )
					sb.append( "/" );
				sb.append( tp.getPathComponent( i ).toString() );
			}
			expandedPath.add( sb.toString() );
		}
		project.setNodeState( 
			expandedPath.toArray( 
				new String[ 
				    expandedPath.size() ] ) 
		);
	}

	private void open( Node node ) {
		String type = project.getLastType( node.getPath().toString() );
		open( node, type );
	}

	private void open( Node node, String type ) {
		Map properties = null;
		
		// Auto-detect the document type
		if ( type == null || "".equals( type ) ) {
			type = DocumentModel.getTypeForFileName2( node.getPath().toString() );
		}
		
		// Search for another encoding from this node to the ancestors
		String encoding = null;
		
		Node nodeTmp = node;
		
		while ( nodeTmp != null ) {
			String tmp = nodeTmp.getProject().getEncoding( nodeTmp.getPath().toString() );
			if ( tmp != null && !"".equals( tmp ) ) {
				encoding = tmp;
				break;
			}
			nodeTmp = ( Node )nodeTmp.getParent();
		}
		
		Map m = project.getProperties( 
				node.getPath().toString() );
		
		if ( encoding != null && !"".equals( encoding ) )
			m.put( "encoding", encoding );
		
		ApplicationModel.fireApplicationValue(
			"open",
			( ( Node )node ).getPath(),
			type,
			flatProperties( 
				m )
		);		
	}

	private String flatProperties( Map m ) {
		StringBuffer sb = new StringBuffer();
		Set<String> keys = m.keySet();
		for ( String key : keys ) {
			if ( sb.length() > 0 )
				sb.append( ";" );
			String value = ( String )m.get( key );
			sb.append( key ).append( "=" ).append( value );
		}
		return sb.toString();
	}

	// ------------------------------------------------------------

	class EncodingAction extends AbstractAction {
		String encoding;
		public EncodingAction( String encoding ) {
			this.encoding = encoding;
			putValue( Action.NAME, encoding );
		}
		public void actionPerformed(ActionEvent e) {
			Node n = getSelection();
			Project project = n.getProject();
			project.setEncoding( n.getPath().toString(), encoding );
		}
	}

	class CopyPath extends AbstractAction {
		private boolean urlMode;
		public CopyPath( boolean urlMode ) {
			this.urlMode = urlMode;
			putValue( 
				Action.NAME, 
				"Copy path" + ( urlMode ? " as URL" : " as System path" ) );
			putValue( 
				Action.SHORT_DESCRIPTION, 
				"Copy the current file path to the system clipboard" 
			);
		}
		public void actionPerformed(ActionEvent e) {
			Node n = getSelection();
			File f = n.getPath();
			String s = f.toString();
			if ( urlMode ) {
				try {
					s = f.toURL().toExternalForm();
				} catch( MalformedURLException exc ) {
					s = "Invalid URL";
				}
			}
			StringSelection stringSelection = new StringSelection( s );
		    Clipboard clipboard = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
		    clipboard.setContents(stringSelection, null);
		}
	}

	class OpenExplorer extends AbstractAction {
		public OpenExplorer() {
			putValue( Action.NAME, "Explore" );
			putValue( Action.SHORT_DESCRIPTION, "Open the native explorer at this directory" );
		}
		public void actionPerformed(ActionEvent e) {
			Node n = getSelection();
			try {
				SystemDesktop.openExplorer( n.getPath() );
			} catch( IOException exc ) {
				EditixFactory.buildAndShowErrorDialog( "Can't open : " + exc.getMessage() );
			}
		}
	}

	class SynchroDownAction extends AbstractAction {
		public SynchroDownAction() {
			putValue( 
					Action.SMALL_ICON, 
					new ImageIcon( 
						getClass().getResource( "arrow_down_green.png" )
					)
				);
				putValue(
					Action.SHORT_DESCRIPTION,
					"Synchronize your files from a server"
				);			
		}
		public void actionPerformed(ActionEvent e) {
			Node[] nn = 
				getSelections();
			if ( nn != null ) {
				SynchroChoosePanel scp = 
					new SynchroChoosePanel( 
						project.getSynchronizers(), 
						project.getSelectedSynchronizer() );
				if ( DialogManager.showDialog(
					ApplicationModel.MAIN_FRAME,
					"Synchronization"
					,
					"Download files"
					,
					"Synchronized the selection from a server", 
					null,
					scp
				) == DialogManager.OK_ID ) {
					project.setSynchronizers( scp.getSynchronizers() );
					Synchronizer s = scp.getSelectedSynchronizer();
					if ( s != null ) {
						try {
							project.setSelectedSynchronized( s );
							s.setListener( ProjectUI.this );
							for ( Node n : nn )
								s.download( project.getPath(), n );
						} catch( IOException exc ) {
							EditixFactory.buildAndShowErrorDialog( "Can't synchronize : " + exc.getMessage() );
						}
					}
				}				
			}
		}
	}

	class SynchroUpAction extends AbstractAction {
		public SynchroUpAction() {
			putValue( 
					Action.SMALL_ICON, 
					new ImageIcon( 
						getClass().getResource( "arrow_up_blue.png" )
					)
				);
				putValue(
					Action.SHORT_DESCRIPTION,
					"Synchronize your server from the selected item"
				);			
		}
		public void actionPerformed(ActionEvent e) {
			Node[] nn = getSelections();
			if ( nn != null ) {
				SynchroChoosePanel scp = 
					new SynchroChoosePanel( project.getSynchronizers(), project.getSelectedSynchronizer() );
				if ( DialogManager.showDialog(
					ApplicationModel.MAIN_FRAME,
					"Synchronization"
					,
					"Upload files"
					,
					"Synchronized the server from the selection", 
					null,
					scp
				) == DialogManager.OK_ID ) {
					project.setSynchronizers( scp.getSynchronizers() );
					Synchronizer s = scp.getSelectedSynchronizer();
					if ( s != null ) {
						try {
							project.setSelectedSynchronized( s );
							s.setListener( ProjectUI.this );
							for ( Node n : nn )
								s.upload( 
									project.getPath(), n 
								);
						} catch( IOException exc ) {
							EditixFactory.buildAndShowErrorDialog( "Can't synchronize : " + exc.getMessage() );
						}
					}
				}				
			}
		}
	}

	class NewDirectoryAction extends AbstractAction {
		public NewDirectoryAction() {
			putValue( 
				Action.NAME, 
				"New directory..." 
			);
			putValue( 
				Action.SMALL_ICON, 
				new ImageIcon( 
					getClass().getResource( "folder_new.png" )
				)
			);
			putValue(
				Action.SHORT_DESCRIPTION,
				"Create a new directory from the selection or from the root directory"
			);
		}

		public void actionPerformed(ActionEvent e) {
			Node selection = getSelection();
			if ( selection == null )
				selection = ( Node )tree.getTreeTableModel().getRoot();
			if ( !selection.getPath().isDirectory() )
				selection = ( Node )selection.getParent();
			String directory = EditixFactory.buildAndShowInputDialog( "Your directory ?" );
			if ( directory != null ) {
				new File( selection.getPath(), directory ).mkdir();
				refresh( selection );
			}
		}
	}

	class CutAction extends AbstractAction {
		public CutAction() {
			putValue( Action.NAME, "Delete" );
			putValue( Action.SMALL_ICON, 
					new ImageIcon( 
							getClass().getResource( "delete.png" ) ) 
			);
			putValue( Action.SHORT_DESCRIPTION, "Delete the selected item(s)" );
		}
		public void actionPerformed(ActionEvent e) {
			Node[] selections = getSelections();
			if ( selections != null ) {
				for ( Node selection : selections ) {
					Node parentNode = ( Node )selection.getParent();
					if ( parentNode == null ) {
						EditixFactory.buildAndShowErrorDialog( "You can't delete this node" );
						return;
					}
					if ( selection.getChildCount() > 0 ) {
						if ( EditixFactory.buildAndShowConfirmDialog( "Delete this directory ?" ) ) {
							try {
								FileUtils.deleteDirectory( selection.getPath() );
							} catch( IOException exc ) {
								EditixFactory.buildAndShowErrorDialog( "Error : " + exc.getMessage() );
							}
						} else
							return;
					} else {
						selection.getPath().delete();
					}
					refresh( parentNode );
				}	
			}
		}
	}

	private File[] copyIt = null;
	
	class CopyAction extends AbstractAction {
		public CopyAction() {
			putValue( Action.NAME, "Copy" );
		}
		public void actionPerformed(ActionEvent e) {
			Node[] selections = getSelections();
			if ( selections != null ) {
				copyIt = new File[ selections.length ];
				for ( int i = 0; i < selections.length; i++ ) {
					copyIt[ i ] = selections[ i ].getPath();
				}
			}
		}
	}

	class PasteAction extends AbstractAction {
		public PasteAction() {
			putValue( Action.NAME, "Paste" );			
		}
		public void actionPerformed(ActionEvent e) {
			Node selection = getSelection();
			if ( selection != null ) {
				if ( copyIt == null ) {
					EditixFactory.buildAndShowWarningDialog( "Use the copy action before" );
				} else {
					for ( File cp : copyIt ) {					
						if ( cp.equals( selection.getPath() ) ) {
							continue;
						} else {
							try {
								if ( cp.isDirectory() )
									FileUtils.copyDirectoryToDirectory( cp, selection.getPath() );
								else
									FileUtils.copyFileToDirectory( cp, selection.getPath() );
								refresh( selection );
							} catch( IOException exc ) {
								EditixFactory.buildAndShowWarningDialog( "Can't copy : " + exc.getMessage() );
							}
						}	
					}
				}
			}
		}
	}

	class OpenAsAction extends AbstractAction {
		private XMLDocumentInfo docInfo;

		public OpenAsAction( XMLDocumentInfo docInfo ) {
			this.docInfo = docInfo;
			putValue( 
				Action.NAME, 
				docInfo.getDocumentDescription() 
			);
			putValue(
				Action.SMALL_ICON,
				docInfo.getDocumentIcon()
			);
		}
		public void actionPerformed( ActionEvent e ) {
			Node[] nn = getSelections();
			if ( nn != null ) {
				for ( Node n : nn ) {
					if ( n.isLeaf() ) {
						ApplicationModel.fireApplicationValue(
								"open",
								( ( Node )n ).getPath(),
								docInfo.getType()
						);
					}
				}
			}
		}
	}

	class RefreshAction extends AbstractAction {
		public RefreshAction() {
			putValue( 
				Action.SMALL_ICON, 
				new ImageIcon( 
					getClass().getResource( "refresh.png" ) 
				) 
			);
			putValue( Action.NAME, "Refresh" );
			putValue( Action.SHORT_DESCRIPTION, "Update the content" );
		}

		public void actionPerformed( ActionEvent e ) {
			Node n = getSelection();
			if ( n == null ) {
				n = ( Node )tree.getTreeTableModel().getRoot();
			}
			refresh( n );
		}
	}

	class RenameAction extends AbstractAction {
		public RenameAction() {
			putValue( Action.NAME, "Rename..." );
		}
		public void actionPerformed(ActionEvent e) {
			Node node = getSelection();
			if ( node != null ) {
				String newName = EditixFactory.buildAndShowInputDialog( "Choose a new name", node.toString() );
				if ( newName != null ) {
					File f = node.getPath();
					if ( !f.renameTo( new File( f.getParentFile(), newName ) ) ) {
						EditixFactory.buildAndShowWarningDialog( "Can't rename" );
					} else {
						node.setUserObject( newName );
						tree.repaint();
					}
				}
			}
		}
	}
	
	// ------------------------------------------------------------
	
	class ProjectRenderer implements TreeCellRenderer {
		FastLabel fastlabel = new FastLabel(false);
		Icon drive = null;
		Icon document = null;
		Icon folder = null;
		Icon folder_closed = null;
		
		public ProjectRenderer() {
			try {
				drive = new ImageIcon( getClass().getResource( "diskdrive.png" ) );
				document = new ImageIcon( getClass().getResource( "document.png" ) );
				folder = new ImageIcon( getClass().getResource( "folder.png" ) );
				folder_closed = new ImageIcon( getClass().getResource( "folder_closed.png" ) );
			} catch (Throwable th) {
				System.err.println("Can't init icons ? : " + th.getMessage());
			}
		}

		public Component getTreeCellRendererComponent(
				JTree tree, 
				Object value,
				boolean selected, 
				boolean expanded, 
				boolean leaf, 
				int row,
				boolean hasFocus ) {

			Icon icon = null;

			if ( !( value instanceof Node ) ) {
				fastlabel.setText( value.toString() );
				fastlabel.setIcon( null );
				return fastlabel;
			}

			Node node = (Node) value;
			String label = node.toString();
			Color c = Color.BLACK;
			
			if ( node.isLeaf() ) {
				String type = node.getType();
				XMLDocumentInfo doc = DocumentModel.getDocumentForType2(type);
				if ( doc != null )
					icon = doc.getDocumentIcon();
				if ( icon == null ) {
					icon = document;
					c = Color.GRAY;
				}
			} else {
				icon = folder;
				if ( !expanded ) {
					icon = folder_closed;
				}
			}

			fastlabel.setToolTipText( node.getPath().toString() );
			
			fastlabel.setIcon(icon);
			fastlabel.setText(label);

			if ( selected ) {
				fastlabel.setForeground(UIManager
						.getColor("List.selectionForeground"));
				fastlabel.setBackground(UIManager
						.getColor("List.selectionBackground"));
			} else {
				Color foreground = c;
				Color background = tree.getBackground();
				fastlabel.setForeground(foreground);
				fastlabel.setBackground(background);
			}

			return fastlabel;
		}
	}

	class NodeTreeTableModel extends DefaultTreeTableModel {
		
		public NodeTreeTableModel(TreeTableNode root) {
			super( root );
		}
		
		public void reload() {
			modelSupport.fireTreeStructureChanged( new TreePath( root ) );
		}

		public void reload( Node n ) {
			TreePath tp = Toolkit.getPath( n );
			modelSupport.fireTreeStructureChanged( tp );
		}
				
		@Override
		public int getColumnCount() {
			return 2;
		}
		@Override
		public String getColumnName(int column) {
			if ( column == 0 )
				return "File";
			return "Size";
		}

	}
}
