package com.japisoft.universalbrowser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

import javax.swing.tree.TreeNode;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;

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
class FileObjectTreeNode implements TreeNode, Comparable {

	private FileObject fo;
	
	FileObjectTreeNode( FileObject fo ) {
		this.fo = fo;
	}

	public Enumeration children() {
		prepareChildren();
		return Collections.enumeration( children );
	}

	public boolean getAllowsChildren() {
		return true;
	}
	
	FileType currentType = null;
	
	public boolean isFolder() {
		try {
			if ( currentType != null )
				return currentType == FileType.FOLDER;
			return ( ( currentType = fo.getType() ) == FileType.FOLDER );
		} catch (FileSystemException e) {
			return false;
		}
	}

	public int compareTo(Object arg0) {
		FileObjectTreeNode fotn = ( FileObjectTreeNode )arg0;

		// Directory case

		if ( isFolder() ) {

			if ( fotn.isFolder() ) {
				
				return fo.getName().compareTo( fotn.fo.getName() );

			} else
				return +1;

		} else {
			
			if ( fotn.isFolder() ) {
				return -1;
			} else
				return fo.getName().compareTo( fotn.fo.getName() );
		}

	}	

	private ArrayList children = null;

	private void prepareChildren() {
		if ( children == null ) {
			children = new ArrayList();
			try {
				FileObject[] fos = fo.getChildren();
				if ( fos != null ) {
					for ( int i = 0; i < fos.length; i++ ) {
						children.add( 
								new FileObjectTreeNode( 
										fos[ i ] 
								) 
						);
					}
				}
				Collections.sort( children );
			} catch (FileSystemException e) {
			}				
		}
	}

	public TreeNode getChildAt(int childIndex) {
		prepareChildren();
		return ( TreeNode )children.get( childIndex );
	}

	public int getChildCount() {
		prepareChildren();
		return children.size();
	}

	public int getIndex(TreeNode node) {
		prepareChildren();
		return children.indexOf( node );
	}

	private TreeNode parent = null;
	
	public TreeNode getParent() {
		try {
			if ( parent == null )
				parent = new FileObjectTreeNode( fo.getParent() );
		} catch (FileSystemException e) {
		}
		return parent;
	}

	public boolean isLeaf() {
		prepareChildren();
		return children.size() == 0;
	}

	boolean isRoot() {
		return "/".equals( fo.getName().getPath() );
	}
	
	String cachedName = null;
	
	public String toString() {
		if ( isRoot() ) {
			if ( cachedName == null ) {
				cachedName = fo.getName().getURI();
				int i = cachedName.indexOf( "file:///" );
				int end = cachedName.lastIndexOf( "!" );
				
				if ( i > -1 ) {
					if ( end > -1 )
						cachedName = cachedName.substring( 
							i + 8,
							end );
					else
						cachedName = cachedName.substring( 
								i + 8 );
				}
				
				i = cachedName.lastIndexOf( "/" );
				if ( i > -1 && ( i + 1 < cachedName.length() ) )
					cachedName = cachedName.substring( i + 1 );
				
			} 
			return cachedName;
		}
		return fo.getName().getBaseName();
	}

	FileObject getSource() { return fo; }
}
