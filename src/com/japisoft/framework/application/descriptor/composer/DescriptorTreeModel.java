package com.japisoft.framework.application.descriptor.composer;

import java.util.HashMap;
import java.util.Map;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
public class DescriptorTreeModel implements TreeModel {

	private TreeModelListener l;
	
	private Element root;
	
	private Map<String,Element> items = null;
	private Map<String,Boolean> legalElements = null;
	
	public DescriptorTreeModel( Element root ) {
		this.root = root;

		legalElements = new HashMap<String, Boolean>();
		
		String[] le = new String[] {
				"menuBar",
				"menu",
				"item",
				"itemRef",
				"separator",
				"toolBar",
				"popup"
		};
		
		for ( String l : le ) {
			legalElements.put( l, Boolean.TRUE );
		}
		
		// Store item by id for itemRef usage
		items = new HashMap<String, Element>();
		NodeList nl = root.getElementsByTagName( "item" );
		for ( int i = 0; i < nl.getLength(); i++ ) {
			Element item = ( Element )nl.item( i );
			items.put( item.getAttribute( "id" ), item );
		}
	}

	public void addTreeModelListener(TreeModelListener l) {
		this.l = l;
	}

	public void fireStructurChange( TreePath tp ) {
		if ( l != null )
			l.treeStructureChanged( new TreeModelEvent( this, tp ) );
	}
	
	public Object getChild(Object parent, int pindex) {
		int index = -1;
		NodeList nl = ( ( Element )parent ).getChildNodes();
		for ( int i = 0; i < nl.getLength(); i++ ) {
			Node n = nl.item( i );
			if ( n instanceof Element ) {
				if ( legalElements.containsKey( n.getNodeName() ) ) {
					index++;
					if ( index == pindex ) {
						if ( "itemRef".equals( 
								n.getNodeName() ) ) {
							return items.get( ( ( Element )n ).getAttribute( "ref" ) );
						}
						return n;
					}
				}
			}
		}
		return null;
	}

	public int getChildCount(Object parent) {
		int cpt = 0;
		NodeList nl = ( ( Element )parent ).getChildNodes();
		for ( int i = 0; i < nl.getLength(); i++ ) {
			Node n = nl.item( i );
			if ( n instanceof Element ) {
				if ( legalElements.containsKey( n.getNodeName() ) )
					cpt++;
			}
		}
		return cpt;
	}

	public int getIndexOfChild(Object parent, Object child) {
		int index = -1;
		NodeList nl = ( ( Element )parent ).getChildNodes();
		for ( int i = 0; i < nl.getLength(); i++ ) {
			Node n = nl.item( i );
			if ( n instanceof Element ) {
				if ( legalElements.containsKey( n.getNodeName() ) ) {
					index++;
					if ( n == child ) {
						break;
					}
				}
			}
		}
		return index;
	}

	public Object getRoot() {
		return root;
	}

	public boolean isLeaf(Object node) {
		Node n = ( Node )node;
		String name = n.getNodeName();
		if ( "item".equals( name ) )
			return true;
		if ( "itemRef".equals( name ) )
			return true;
		if ( "separator".equals( name ) )
			return true;
		return false;
	}

	public void removeTreeModelListener(TreeModelListener l) {
		this.l = null;
	}

	public void valueForPathChanged(TreePath path, Object newValue) {
	}

}
