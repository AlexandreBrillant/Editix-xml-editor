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

package com.japisoft.editix.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

public abstract class AbstractNodeDb implements TreeNode, NodeDb {

	protected String url = null;
	protected String user = null;
	protected String password = null;

	protected ArrayList children = null;
	protected MutableTreeNode parent = null;
	
	public void setParent(NodeDb parent) {
		this.parent = parent;
	}	

	public void setParent(MutableTreeNode newParent) {
		this.parent = newParent;
	}	
	
	public void addChild( NodeDb node ) {
		if ( children == null )
			children = new ArrayList();
		node.setParent( this );
		
		// Order it
		
		String name = node.toString();
		boolean added = false;
		
		for ( int i = 0; i < children.size(); i++ ) {
			
			NodeDb n = ( NodeDb )children.get( i );
			if ( n.toString().compareTo( name ) > 0 ) {
			
				children.add( i, node );
				added = true;
				break;
				
			}
			
			
		}
	
		if ( !added )
			children.add( node );
	}

	public Enumeration children() {
		checkOpen();
		if ( children == null )
			return null;
		return Collections.enumeration( children );
	}

	public boolean getAllowsChildren() {
		return true;
	}

	public TreeNode getChildAt(int childIndex) {
		checkOpen();
		return ( TreeNode )children.get( childIndex );
	}

	private boolean open = false;
	private boolean error = false;
	private String errorMessage = null;
	
	private void checkOpen() {
		if ( !open ) {
			if ( children == null ) {
				try {
					open();
				} catch (Exception e) {
					error = true;
					errorMessage = e.getMessage();
				}
			}
			open = true;
		}
	}
	
	public int getChildCount() {
		checkOpen();
		if ( children == null )
			return 0;
		return children.size();
	}

	public int getIndex(TreeNode node) {
		if ( children == null )
			return -1;
		return children.indexOf( node );
	}

	public TreeNode getParent() {
		return parent;
	}

	public boolean isLeaf() {
		checkOpen();
		return 
			( children == null ) || 
				( children.size() == 0 );
	}

	public void close() {
		open = false;
		children = null;
		errorMessage = null;
		error = false;
	}

	public void open() throws Exception {
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}

	public boolean hasError() {	
		return error;
	}
	
	public void setUserObject(Object object) {
	}
	
	public void insert(MutableTreeNode child, int index) {
	}
	public void remove(int index) {
	}
	public void remove(MutableTreeNode node) {
		if ( children != null )
			children.remove( node );
	}
	public void removeFromParent() {
	}

	protected String driverName;
	
	public void setDriverName( String driverName ) {		
		this.driverName = driverName;
	}

	public String getDriverName() {
		return driverName;
	}
	
	public boolean canBeDeleted() {		
		return true;
	}

	public boolean delete() throws Exception {
		if ( parent != null ) {
			close();
			parent.remove( this );
			return true;
		} else
			return false;
	}
	
	protected String name;

	protected void prepareName() {}
	
	public String toString() {
		if ( name == null )
			prepareName();
		return name;
	}

	public void refresh() throws Exception {
		// Go into
		for ( int i = 0; i < getChildCount(); i++ ) {
			NodeDb node = ( NodeDb )getChildAt( i );
			node.close();
		}
		// Remove children link
		children = null;
		open();
	}
	
}
