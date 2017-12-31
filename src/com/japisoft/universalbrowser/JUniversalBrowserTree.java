package com.japisoft.universalbrowser;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.provider.local.LocalFile;

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
public class JUniversalBrowserTree extends JTree 
		implements 
		TreeSelectionListener, 
		MouseListener, 
		MouseMotionListener {

	public JUniversalBrowserTree() {
		setCellRenderer( new TreeCellRendererImpl() );
	}

	public void addNotify() {
		super.addNotify();
		addTreeSelectionListener( this );
		addMouseListener( this );
		addMouseMotionListener( this );
	}

	public void removeNotify() {
		super.removeNotify();
		removeTreeSelectionListener( this );
		removeMouseListener( this );
		removeMouseMotionListener( this );
	}

	public void setFileView( FileView fv ) {
		( ( TreeCellRendererImpl )getCellRenderer() ).setFileView( fv );
	}

	public void setFileFilter( FileFilter ff ) {
		( ( TreeCellRendererImpl )getCellRenderer() ).setFileFilter( ff );
		repaint();
	}

	public FileObject getBrowsedFileObject() {
		if ( getModel().getRoot() instanceof FileObjectTreeNode )
			return ( ( FileObjectTreeNode )getModel().getRoot() ).getSource();
		return null;
	}

	public void refresh() throws Exception {
		if ( lastUri != null )
			browse( lastUri );
	}
	
	private boolean errorMode = false;

	public void browse( FileObject fo ) {

		errorMode = false;
		
		if ( fo instanceof LocalFile ) {
			try {
				
				lastUri = fo.getURL().toExternalForm();
				
				if ( !fo.exists() ) {
					errorMode = true;
					setModel( 
							new DefaultTreeModel( 
									new DefaultMutableTreeNode( 
											"Can't find the root path" ) ) );
				} else
					setModel( 
							new DefaultTreeModel( 
									new FileObjectTreeNode( fo ) ) );
			} catch (FileSystemException e) {
				setModel( 
						new DefaultTreeModel( 
								new DefaultMutableTreeNode( 
										"Can't find the root path" ) ) );
				errorMode = true;
			}
		} else
		
			setModel( 
					new DefaultTreeModel( 
							new FileObjectTreeNode( fo ) ) );
	}

	private String lastUri = null;

	public void browse( String uri ) throws FileSystemException {
		this.lastUri = uri;
		FileSystemManager fsManager = VFS.getManager();
		browse( fsManager.resolveFile( uri ) );
	}

	public void close() {
		try {
			if ( getModel().getRoot() instanceof FileObjectTreeNode ) {
				FileSystemManager fsManager = VFS.getManager();
				fsManager.closeFileSystem( 
						( (FileObjectTreeNode)getModel().getRoot() ).getSource().getFileSystem() 
				);
			}
		} catch (FileSystemException e) {
		}
	}
	
	private UniversalBrowserListener listener = null;
	
	public void setUnivervalBrowserListener( UniversalBrowserListener listener ) {
		this.listener = listener;
	}

	public void valueChanged(TreeSelectionEvent e) {
		if ( errorMode )
			return;
		
		FileObjectTreeNode fo = ( FileObjectTreeNode )e.getPath().getLastPathComponent();
		if ( !fo.isRoot() )
			if ( listener != null )
				listener.select( fo.getSource() );
	}	

	public FileObject getSelectedFileObject() {
		
		FileObjectTreeNode fotn = ( FileObjectTreeNode )getSelectionPath().getLastPathComponent();
		if ( fotn == null || fotn.isRoot() )
			return null;
		return fotn.getSource();
		
	}

	public void select( String uri ) {
		int i = uri.lastIndexOf( "!/" );
		if ( i > -1 ) {
			String path = uri.substring( i + 2 );
			StringTokenizer st = 
				new StringTokenizer( path, "/\\" );

			TreeNode currentParent = ( TreeNode )getModel().getRoot();
			ArrayList res = new ArrayList();
			res.add( currentParent );
			
			while ( st.hasMoreTokens() ) {

				currentParent = search( st.nextToken(), currentParent );
				if ( currentParent == null ) 
					break;
				res.add( currentParent );
				
			}
			
			setSelectionPath( new TreePath( res.toArray() ) );
		}
	}
	
	private TreeNode search( String name, TreeNode parent ) {
		for ( int i = ( parent.getChildCount() - 1 ); i >= 0 ;i-- ) {
			TreeNode tn = parent.getChildAt( i );
			if ( tn.toString().equals( name ) )
				return tn;
		}
		return null;
	}

	public void mouseDragged(MouseEvent e) {}

	public void mouseMoved(MouseEvent e) {
		if ( errorMode )
			return;

		TreePath tp = getPathForLocation( e.getX(), e.getY() );
		if ( tp != null ) {
			FileObjectTreeNode fon = ( FileObjectTreeNode )tp.getLastPathComponent();
			FileObject fo = fon.getSource();
			setToolTipText( fo.getName().getURI() );
		}
	}

	public void mouseClicked(MouseEvent e) {
		if ( errorMode )
			return;

		if ( e.getClickCount() >= 2 ) {
			if ( listener != null ) {
				if ( getSelectionPath() != null ) {
					FileObjectTreeNode fotn = ( FileObjectTreeNode )getSelectionPath().getLastPathComponent();				
					if ( fotn.isLeaf() )
						listener.doubleClick( getSelectedFileObject() );
				}
			}
		}
	}

	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}

	/**
	 * @param args */
	public static void main(String[] args) throws Throwable {

		FileSystemManager fsManager = VFS.getManager();
		
		JUniversalBrowserTree ub = new JUniversalBrowserTree();
		//ub.browse(
		//		fsManager.resolveFile( "jar:file://c:/cdam-ab.zip" ) );

		ub.browse(
				fsManager.resolveFile( "file:///c:/titi/" ) );		

		JFrame f = new JFrame();
		f.add( new JScrollPane( ub ) );
		f.setSize( 300, 300 );
		f.setVisible( true );

	}

}
