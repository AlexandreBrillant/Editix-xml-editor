package com.japisoft.editix.ui.panels.project2;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;

import org.jdesktop.swingx.treetable.TreeTableNode;

import com.japisoft.editix.document.DocumentModel;

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
public class DefaultNode implements Node {

	private Project project;
	private String name;
	private String type;
	private Node parent;

	public DefaultNode( Project project, Node parent, String name ) {
		this.project = project;
		this.parent = parent;
		if( name == null ) {
			name = "project";
		}
		this.name = name;
		if ( name.indexOf( "." ) > -1 ) {
			type = DocumentModel.getTypeForFileName2( name );
		}
	}

	public File getPath() {
		if ( parent == null )
			return project.getPath();
		return new File( parent.getPath(), name );
	}
	
	public String getType() {
		return type;
	}

	public Project getProject() {
		return project;
	}

	private List<Node> children = null;
	
	public Enumeration children() {
		if ( children == null )
			return null;
		return Collections.enumeration( children );
	}
	
	public boolean getAllowsChildren() {
		return getPath().isDirectory();
	}

	public TreeTableNode getChildAt(int childIndex) {
		return children.get( childIndex );
	}

	public int getChildCount() {
		if ( children == null )
			reload();
		if ( children == null )
			return 0;
		return children.size();
	}

	public void reload() {
		sizeCache = null;
		File parentPath = null;
		if ( parent != null )
			parentPath = new File( parent.getPath(), name );
		else
			parentPath = project.getPath();
		String[] content = parentPath.list();
		if ( content != null ) {
			children = new ArrayList<Node>();
			for ( String file : content ) {
				if ( !project.skip( file ) )
					add( new DefaultNode( project, this, file ) );
			}
		}
	}

	private void add( Node newNode ) {
		boolean file = newNode.isLeaf();
		for ( int i = 0; i < getChildCount(); i++ ) {
			TreeTableNode ttn = getChildAt( i );
			String name = ttn.toString();
			if ( file ) {
				if ( ttn.isLeaf() ) {	// File
					if ( name.compareTo(
							newNode.toString() ) > -1 ) {
						children.add( i, newNode );
						return;
					}
				} else {
					if ( getProject().getSortMode() == NodeSortMode.FILE ) {
						children.add( i, newNode );
						return;											
					}
				}
			} else {
				if ( !ttn.isLeaf() ) {	// Project
					if ( name.compareTo(
							newNode.toString() ) > -1 ) {
						children.add( i, newNode );
						return;
					}
				} else {
					if ( getProject().getSortMode() == NodeSortMode.DIRECTORY ) {
						// Project priority
						children.add( i, newNode );
						return;
					}
				}
			}
		}
		children.add( newNode );
	}

	public int getIndex(TreeNode node) {
		return children.indexOf( node );
	}

	public TreeTableNode getParent() {
		return parent;
	}

	public boolean isLeaf() {
		return !getAllowsChildren();
	}
	
	public int getColumnCount() {
		return 2;
	}
	
	public Object getUserObject() {
		return null;
	}

	private static DecimalFormat df = new DecimalFormat("0.0");
	
	private String sizeCache;
	
	public String getSizeCache() {
		if ( sizeCache == null ) {
			sizeCache = df.format( ( getPath().length() ) / 1024.0 ) + " kb";
		}
		return sizeCache;
	}
	
	public Object getValueAt(int column) {
		if ( column == 1 ) {
			if ( isLeaf() )
				return getSizeCache();
			return "";
		}
		return name;			
	}
	public boolean isEditable(int column) {
		return false;
	}
	public void setUserObject(Object userObject) {
		if ( userObject instanceof String )
			name = ( String )userObject;
	}
	public void setValueAt(Object aValue, int column) {
	}

	@Override
	public String toString() {
		return name;
	}
	
	
}
