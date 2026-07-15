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

package com.japisoft.editix.db.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.japisoft.editix.action.file.OpenAction;
import com.japisoft.editix.action.file.SaveActionDelegate;
import com.japisoft.editix.db.ContainerNodeDb;
import com.japisoft.editix.db.Driver;
import com.japisoft.editix.db.DriverDbManager;
import com.japisoft.editix.db.FileNodeDb;
import com.japisoft.editix.db.NodeDb;
import com.japisoft.editix.db.RootNodeDb;
import com.japisoft.editix.document.DocumentModel;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.windows.EditixFrame;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.toolkit.Toolkit;
import com.japisoft.framework.xml.XMLFileData;
import com.japisoft.xmlpad.SharedProperties;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.XMLDocumentInfo;

public class DbBrowser extends JPanel 
		implements 
			MouseListener, 
			TreeSelectionListener {

	protected JTree t = null;
	
	public DbBrowser() {
		initActions();
		initUi();
	}

	RefreshAction ra = null;
	DeleteAction da = null;
	EditAction ea = null;
	DbRequestPanel reqPnl = null;

	protected void initActions() {
		ra = new RefreshAction();
		da = new DeleteAction();
		ea = new EditAction();	
	}

	protected void initUi() {
		setLayout( new BorderLayout() );

		JToolBar tb = new JToolBar();
		tb.add( new ConnectAction() );
		tb.add( ra );
		tb.add( new NewAction() );
		tb.add( da );
		add( tb, BorderLayout.NORTH );

		DefaultMutableTreeNode dmt = new DefaultMutableTreeNode( "Your databases" );

		JSplitPane sp = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
		sp.setTopComponent( new JScrollPane( 
				t = new JTree( 
						new DefaultTreeModel( 
								dmt ) ) ) );

		reqPnl = new DbRequestPanel( this );
		sp.setBottomComponent( reqPnl );
		sp.setDividerLocation( 180 );

		add( sp, BorderLayout.CENTER );
		
		t.setCellRenderer( new DbBrowserTreeRenderer() );
		sp.setOneTouchExpandable( true );
	}

	public DefaultMutableTreeNode getRoot() { 
		return ( DefaultMutableTreeNode )t.getModel().getRoot(); 
	}

	public void refreshRoot() {	
		( ( DefaultTreeModel )t.getModel() ).nodeStructureChanged( getRoot() );
	}

	public RootNodeDb getCurrentConnection() {
		
		TreePath tp = t.getSelectionPath();
		if ( tp == null )
			return null;
		Object obj = tp.getLastPathComponent();
		if ( obj instanceof NodeDb ) {
			
			NodeDb n = ( NodeDb )obj;
			while ( !( n instanceof RootNodeDb ) ) {
				if ( n.getParent() instanceof NodeDb ) {
					n = ( NodeDb )n.getParent();
				} else
					break;
			}
			
			if ( n instanceof RootNodeDb )
				return ( RootNodeDb )n;
			else
				return null;
			
		} else
			return null;

	}
	
	public Driver getCurrentDriver() {
		
		RootNodeDb root = getCurrentConnection();
		if ( root == null )
			return null;
		String driver = root.getDriverName();
		if ( driver == null )
			return null;
		return DriverDbManager.getDriverByName( driver );
		
	}

	public ContainerNodeDb getCurrentContainer() {

		TreePath tp = t.getSelectionPath();
		if ( tp == null )
			return null;
		Object obj = tp.getLastPathComponent();
		if ( obj instanceof NodeDb ) {

			NodeDb n = ( NodeDb )obj;
			while ( !( n instanceof ContainerNodeDb ) ) {
				n = ( NodeDb )n.getParent();
			}

			return ( ContainerNodeDb )n;
			
		} else
			return null;

	}

	public void close() {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode)getRoot();
		for ( int i = 0; i < root.getChildCount(); i++ ) {
			TreeNode child = root.getChildAt( i );
			if ( child instanceof NodeDb ) {
				( (NodeDb)child ).close();
			}
		}
		root.removeAllChildren();
	}

	public void addNotify() {
		super.addNotify();
		t.addMouseListener( this );
		t.addTreeSelectionListener( this );
	}

	public void removeNotify() {
		super.removeNotify();
		t.removeMouseListener( this );
		t.removeTreeSelectionListener( this );
	}

	public void initTree() {
		DefaultMutableTreeNode dmt = getRoot();
		if ( dmt.getChildCount() > 0 ) {
			// Open it
			t.expandPath( new TreePath( dmt ) );

			com.japisoft.framework.ui.toolkit.Toolkit.selectFirstChild( t, dmt );
			
		}
	}

	public void refresh( NodeDb ndb ) {
		
		ndb.close();
		
		try {
			ndb.open();
			
			( ( DefaultTreeModel )t.getModel() ).nodeStructureChanged( ndb );
			
		} catch ( Exception e1 ) {

			EditixFactory.buildAndShowErrorDialog( "Can't refresh " + e1.getMessage() );
			
		}
		
	}

	public void valueChanged(TreeSelectionEvent e) {
		
		TreePath tp = t.getSelectionPath();
		if ( tp != null ) {

			if ( tp.getLastPathComponent() instanceof RootNodeDb ) {
				
				da.setEnabled( true );
				
			} else

			if ( tp.getLastPathComponent() instanceof ContainerNodeDb ) {
				
				Driver d = getCurrentDriver();

				if ( d != null && !d.canRemoveContainer ) {
					da.setEnabled( false );
				} else 
					da.setEnabled( ( ( NodeDb )tp.getLastPathComponent() ).canBeDeleted() );
				
			} else
			
			if ( tp.getLastPathComponent() instanceof NodeDb ) {
				da.setEnabled( ( ( NodeDb )tp.getLastPathComponent() ).canBeDeleted() );
			} else
				da.setEnabled( false );

		}
		
		Driver d = getCurrentDriver();
		if ( d != null ) {
		
			reqPnl.setEnabledRequest( d.query );
			
		}
		
	}	

	////////////////////////////////////////////////////////////////////
	
	class AddContainerAction extends AbstractAction {
		public AddContainerAction() {
			putValue( Action.NAME, "Add a container" );
			putValue( Action.SHORT_DESCRIPTION, "Add a container" );
		}

		public void actionPerformed(ActionEvent e) {
			
			TreePath tp = t.getSelectionPath();
			if ( tp != null ) {
				
				if ( tp.getLastPathComponent() instanceof ContainerNodeDb ) {
		
					ContainerNodeDb cnd = ( ContainerNodeDb )tp.getLastPathComponent();
					if ( cnd.canCreateSubContainer() ) {
						
						String containerName = EditixFactory.buildAndShowInputDialog( "Container name ?" );
						if ( containerName != null ) {
							
							try {
								ContainerNodeDb container = cnd.createSubContainer( containerName );
								
								if ( container != null ) {
									
									( ( DefaultTreeModel )t.getModel() ).nodeStructureChanged( cnd );
									com.japisoft.framework.ui.toolkit.Toolkit.selectNode( t, container );
									
								}
								
							} catch (Exception e1) {

								EditixFactory.buildAndShowErrorDialog( "Can't create a container : " + e1.getMessage() );
								
							}

						}
						
					} else
						
						EditixFactory.buildAndShowWarningDialog( "Can't create a container" );
					
				}
			
			}
			
		}
		
	}

	class EditAction extends AbstractAction {
		
		public EditAction() {
			putValue( Action.NAME, "Edit" );
			putValue( Action.SHORT_DESCRIPTION, "Edit the current node (or double - click )" );				
		}

		private String forceType = null;
		
		public EditAction( String forceType, String name, Icon icon ) {
			this();
			this.forceType = forceType;
			putValue( Action.NAME, "Edit as " + name );
			putValue( Action.SMALL_ICON, icon );
		}

		public void actionPerformed(ActionEvent e) {

			readCurrentFileNode( forceType );
			
		}
		
	}

	private FileNodeDb getFileNodeByName( TreeNode parent, String name ) {
	
		for ( int i = 0; i < parent.getChildCount(); i++ ) {
			NodeDb c = ( NodeDb )parent.getChildAt( i );
			if ( c instanceof FileNodeDb ) {			
				if ( name.equalsIgnoreCase( c.toString() ) ) {
					return ( FileNodeDb )c;
				}
			}
		}
		
		return null;
	}

	
	class NewAction extends AbstractAction {
		
		public NewAction() {
			
			putValue( Action.NAME, "New" );
			putValue( Action.SHORT_DESCRIPTION, "Create a new document" );
			putValue( Action.SMALL_ICON, Toolkit.getImageIcon( "images/document_new.png" ) );
			
		}

		public void actionPerformed(ActionEvent e) {

			TreePath tp = t.getSelectionPath();
			if ( tp != null ) {

				TreeNode n = ( TreeNode )tp.getLastPathComponent();
				
				if ( n instanceof FileNodeDb ) {
					
					n = n.getParent();
					
				}
				
				if ( n instanceof ContainerNodeDb ) {
					
					String fileName = EditixFactory.buildAndShowInputDialog( "Your file name ?" );

					if ( fileName != null ) {
					
						boolean ok = true;
						
						FileNodeDb fileNode = getFileNodeByName( n, fileName );
						if ( fileNode != null ) {						
								EditixFactory.buildAndShowWarningDialog( "This file already exists" );
								ok = false;
						}

						if ( ok ) {
					
							try {

								( ( ContainerNodeDb )n ).setContent( fileName, "<?xml version='1.0'?>\n\n<myFile/>" );
								refresh( ( NodeDb )n );
								fileNode = getFileNodeByName( n, fileName );
								if ( fileNode == null ) {
									EditixFactory.buildAndShowWarningDialog( "The file cannot be created" );	
								} else {
									com.japisoft.framework.ui.toolkit.Toolkit.selectNode( t, fileNode );
								}

							} catch (Exception e1) {

								EditixFactory.buildAndShowErrorDialog( "Can't create " + fileName + " / " + e1.getMessage() );
							
							}
							
						}
					
					}
					
					
				} else {
					
					EditixFactory.buildAndShowWarningDialog( "Please select a container for creating your file" );

				}
				
			} else {
				
				EditixFactory.buildAndShowWarningDialog( "Please select a container for creating your file" );
				
			}

		}
		
	}

	class DeleteAction extends AbstractAction {
	
		public DeleteAction() {
			
			putValue( Action.NAME, "Delete" );
			putValue( Action.SHORT_DESCRIPTION, "Delete a file or a connection" );
			putValue( Action.SMALL_ICON, Toolkit.getImageIcon( "images/delete2.png" ) );

		}

		public void actionPerformed(ActionEvent e) {	

			TreePath tp = t.getSelectionPath();
			if ( tp != null ) {
				
				TreeNode n = ( TreeNode )tp.getLastPathComponent();

				if ( n instanceof NodeDb ) {

					NodeDb db = ( NodeDb )n;
					TreeNode parent = db.getParent();
					
					if ( !db.canBeDeleted() ) {
						
						EditixFactory.buildAndShowWarningDialog( "Can't delete this node " );
						
					} else {
						
						try {
							
							String title = "Delete " + db + " ?";
							
							if ( db instanceof RootNodeDb ) {
								title = "Remove this connection to " + db + " ?";
							}
							
							if ( EditixFactory.buildAndShowConfirmDialog( title ) ) {
							
								if( !db.delete() ) {
									
									EditixFactory.buildAndShowWarningDialog( "Can't delete this node " );
									
								} else {
									
									( ( DefaultTreeModel )t.getModel() ).nodeStructureChanged( parent );
									
								}
							
							}
						} catch (Exception e1) {

							EditixFactory.buildAndShowErrorDialog( "Can't delete : " + e1.getMessage() );
							
						}
						
					}
					
				} else
					EditixFactory.buildAndShowWarningDialog( "Can't delete this node " );

				
/*				if ( n instanceof FileNodeDb ) {
				
					NodeDb parent = ( NodeDb )n.getParent();
					try {
						
						if ( EditixFactory.buildAndShowConfirmDialog( "Are you sure to delete " + n.toString() + " ?" ) ) {
						
							((FileNodeDb) n).delete();						
							refresh( parent );
						
						}
					} catch (Exception e1) {
						
						EditixFactory.buildAndShowErrorDialog( "Can't delete " + e1.getMessage() );
						
					}
					
				} else
					if ( n instanceof RootNodeDb ) {
						
						if ( EditixFactory.buildAndShowConfirmDialog( "Confirm ?" ) ) {						
						
							((RootNodeDb) n).close();
							getRoot().remove( ( RootNodeDb )n );
							( ( DefaultTreeModel )t.getModel() ).nodeStructureChanged( n );
						
						}
						
					} else
						
						EditixFactory.buildAndShowWarningDialog( "Can't delete this node " ); */
						

			}

		}
		
	}

	class RefreshAction extends AbstractAction {
	
		public RefreshAction() {

			putValue( Action.NAME, "Refresh" );
			putValue( Action.SHORT_DESCRIPTION, "Refresh the current container or connection" );
			putValue( Action.SMALL_ICON, Toolkit.getImageIcon( "images/data_refresh.png" ) );

		}

		public void actionPerformed(ActionEvent e) {
			
			TreePath tp = t.getSelectionPath();
			if ( tp != null ) {

				TreeNode n = ( TreeNode )tp.getLastPathComponent();
				if ( n instanceof NodeDb ) {
					
					refresh( ( NodeDb )n );

				}
				
			}

		}
		
	}

	class ConnectAction extends AbstractAction {

		public ConnectAction() {

			putValue( Action.NAME, "Connect" );
			putValue( Action.SHORT_DESCRIPTION, "Add a database connection" );
			putValue( Action.SMALL_ICON, Toolkit.getImageIcon( "images/data_add.png" ) );
			
		}

		public void actionPerformed(ActionEvent e) {
			
			DriverDbManager.install();
			
			DbConnectionPanel panel = new DbConnectionPanel();
			
			if ( DialogManager.showDialog( 
					EditixFrame.THIS, 
					"Connection", 
					"Database connection", 
					"Choose a database driver and connect. We advise to Test your connection before", 
					null,
					panel ) == 
						DialogManager.OK_ID ) {

				Driver d = panel.getDriver();
				try {

					RootNodeDb dbRootNode = d.getRoot( 
							panel.getUrl(),
							panel.getUser(),
							panel.getPassword() );
					
					//dbRootNode.setDriverName( d.toString() );
					
					DefaultMutableTreeNode root = ( DefaultMutableTreeNode )t.getModel().getRoot();
					root.add( dbRootNode );
					( ( DefaultTreeModel )t.getModel() ).nodeStructureChanged( root );
					com.japisoft.framework.ui.toolkit.Toolkit.selectFirstChild( t, dbRootNode );

				} catch ( Exception e1 ) {

					EditixFactory.buildAndShowErrorDialog( "Can't connect : " + e1.getMessage() );

				}
				
			}
			
		}

	}

	public void mouseClicked(MouseEvent e) {
		if ( e.getClickCount() > 1 ) {
			readCurrentFileNode( null );
		}
	}
	
	public void read( FileNodeDb fnd, String forceType ) {

		if ( fnd instanceof Editable ) {
			Editable e = ( Editable )fnd;
			if ( !e.canEdit() ) {
				EditixFactory.buildAndShowWarningDialog( "Can't edit this node" );
				return;
			}
		}
		
		try {
			String content = fnd.getContent();
			String type = null;
			
			XMLFileData xfd = new XMLFileData( null, content );
			String name = fnd.toString();
			
			EntityResolver res = null;
			if ( fnd.getParent() instanceof ContainerNodeDb ) {
				
				res = new DbEntityResolver(
						( ContainerNodeDb )fnd.getParent() );

			}

			if ( forceType != null )
				type = forceType;
			else
				type = DocumentModel.getTypeForFileName( name );

			OpenAction.openFile( 
					type, 
					true, 
					null, 
					name, 
					null, 
					xfd, 
					new SaveActionDelegateImpl( fnd ), 
					res 
			);

		} catch ( Exception e ) {

			EditixFactory.buildAndShowErrorDialog( "Can't read : " + e.getMessage() );
			
		}
		
	}

	private void readCurrentFileNode( String forceType ) {

		if ( t.getSelectionPath() != null ) {
			
			TreeNode tn = ( TreeNode )t.getSelectionPath().getLastPathComponent();
			if ( tn instanceof FileNodeDb ) {
				
				FileNodeDb fnd = ( FileNodeDb )tn;
				read( fnd, forceType );
				
			}
			
		}

	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		if ( e.getButton() == e.BUTTON3 ) {
			
			TreePath tp = t.getSelectionPath();
			TreeNode selection = null;
			if ( tp != null )
				selection = ( TreeNode )tp.getLastPathComponent();

			JPopupMenu menu = new JPopupMenu();

			if ( selection instanceof FileNodeDb ) {
				menu.add( ea );
				
				JMenu m = new JMenu( "Edit as" );
				for ( int i = 0; i < DocumentModel.getDocumentCount(); i++ ) {
					
					XMLDocumentInfo info = DocumentModel.getDocumentAt( i );
					String name = info.getDocumentDescription();
					String type = info.getType();
					m.add( new EditAction( type, name, info.getDocumentIcon() ) );
					
				}
				
				menu.add( m );
			} 
				
			menu.add( ra );

			Driver d = getCurrentDriver();
			boolean canDeleteContainerFromDriver = true;
			boolean canAddContainerFromDriver = true;
			if ( d != null ) {
				canDeleteContainerFromDriver = d.canRemoveContainer;
				canAddContainerFromDriver = d.canAddContainer;
			}

			
			if ( ( selection instanceof NodeDb ) && 
					( ( NodeDb )selection ).canBeDeleted() ) {

				if ( selection instanceof ContainerNodeDb ) {
					if ( canDeleteContainerFromDriver ) {

						menu.addSeparator();
						menu.add( da );
						
					}
				} else {

					menu.addSeparator();
					menu.add( da );

				}

			}

			if ( selection instanceof ContainerNodeDb ) {
				
				ContainerNodeDb cnb = ( ContainerNodeDb )selection;
				if ( canAddContainerFromDriver && cnb.canCreateSubContainer() ) {
					
					menu.addSeparator();
					menu.add( 
					
							new AddContainerAction()
							
					);
					
				}
					
			}
			
			menu.show( t, e.getX(), e.getY() );
			
		}
	}

	public void mouseReleased(MouseEvent e) {
	}
	
	// -------------------------------------------------------
	
	class SaveActionDelegateImpl implements SaveActionDelegate {
		private FileNodeDb fileRef;
		SaveActionDelegateImpl( FileNodeDb fileRef ) {
			this.fileRef = fileRef;
		}
		public boolean save( XMLContainer container ) {
			try {
				fileRef.setContent( container.getText() );
				container.setModifiedState( false );
			} catch ( Exception e ) {
				EditixFactory.buildAndShowErrorDialog( "<html><body><b>Can't save this document</b><p style='width:500px'>" + e.getMessage() + "</p></body></html>" );
				return false;
			}
			return true;
		}
	}

	class DbEntityResolver implements EntityResolver {

		private ContainerNodeDb container;
		
		public DbEntityResolver( ContainerNodeDb container ) {
			this.container = container;
		}

		public InputSource resolveEntity(String publicId, String systemId)
			throws SAXException, IOException {
			
			if ( systemId != null ) {
				
				// Wrong local path from the parser
				// Convert it to a relative one
				if ( systemId.startsWith( "file:" ) ) {
					int i = systemId.lastIndexOf( "/" );
					if ( i > -1 ) {
						systemId = systemId.substring( i + 1 );
					}
				}

				if ( !systemId.contains( "/" ) && 
						!systemId.contains( "\\" ) ) {
					
					// Relative access to the container
					for ( int i = 0; i < container.getChildCount(); i++ ) {
						
						NodeDb node = ( NodeDb )container.getChildAt( i );
						if ( node instanceof FileNodeDb ) {

							FileNodeDb fnode = ( FileNodeDb )node;
							if ( systemId.equalsIgnoreCase( fnode.toString() ) ) {
								
								try {
									String content = fnode.getContent();
									// return new InputSource( new StringReader( content ) );
									return new InputSource( new ByteArrayInputStream( content.getBytes() ) );
								} catch (Exception e) {
								}

							}

						}

					}
					
				}
			}
			
			// Default one
			if ( SharedProperties.DEFAULT_ENTITY_RESOLVER != null ) {
				return SharedProperties.DEFAULT_ENTITY_RESOLVER.resolveEntity(publicId, systemId);
			}

			return null;
		}

	}

}
