// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2025 Alexandre Brillant
// 
// For non-commercial usage :
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
// For commercial use or integration into proprietary software :
// A commercial license is required. Visit https://www.editix.com for details.

package com.japisoft.editix.db.xmldb;

import javax.swing.tree.TreeNode;

import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.Service;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;

import com.japisoft.editix.db.AbstractNodeDb;
import com.japisoft.editix.db.ContainerNodeDb;
import com.japisoft.editix.db.NodeDb;
import com.japisoft.editix.db.RootNodeDb;

public class XmlDbContainerDbImpl extends AbstractNodeDb implements ContainerNodeDb {

	protected Collection col;
	
	public XmlDbContainerDbImpl( Collection col ) {
		this.col = col;
	}

	public void open() throws Exception {
		
		
		
		String[] childCol = col.listChildCollections();
		if ( childCol != null ) {		
			for ( int i = 0; i < childCol.length; i++ ) {
				addChild( 
					new XmlDbContainerDbImpl( 
							col.getChildCollection( 
								childCol[ i ] ) ) );
			}
		}
		
		
		String[] ids = col.listResources();
		if ( ids != null ) {
			for ( int i = 0; i < ids.length; i++ ) {
				addChild( new XmlDbFileNodeDbImpl( this, ids[ i ] ) );
			}
		}
	}
	

	public void close() {
		try {
			col.close();
			super.close();
		} catch (XMLDBException e) {
		}
	}

	public Collection getCollection() { return col; }
	
	public void delete(String id) throws Exception {
		Resource res = col.getResource( id );
		if ( res == null )
			throw new Exception( "Unknown resource " + id );
		col.removeResource( res );
		
		// Delete this tree node
		for ( int i = 0; i < children.size(); i++ ) {
			if ( children.get( i ) instanceof XmlDbFileNodeDbImpl ) {
				
				XmlDbFileNodeDbImpl xfndi = ( XmlDbFileNodeDbImpl )children.get( i );
				if ( xfndi.toString().equals( id ) ) {
					children.remove( i );
					break;
				}
				
			}
		}
	}

	public String getContent(String id) throws Exception {
		Resource res = col.getResource( id );

		if ( res == null )
			throw new Exception( "Unknown resource " + id );
		
		/*
		if ( res instanceof RemoteBinaryResource ) {
			RemoteBinaryResource rbr = ( RemoteBinaryResource )res;
			byte[] val = ( byte[] )rbr.getContent();
			return new String( val, 0, ( ( RemoteBinaryResource ) res ).getContentLength() );
		}
		*/

		Object content = res.getContent();
		if ( content == null )
			return null;
		return content.toString();
	}

	public void setContent( String id, String content ) throws Exception {
		Resource res = col.getResource( id );
		
		if ( res != null ) {
			
			res.setContent( content );

		}
		else {
			
			String type = "XMLResource";
			String tid = id.toLowerCase();
			String realContent = content;
			if ( tid.endsWith( "dtd" ) || tid.endsWith( "css" ) || tid.endsWith( "txt" ) ) {
				
				realContent = "";
				type = "BinaryResource";
				
			}
			
			res = col.createResource( id, type );
			res.setContent( realContent );
			
			if ( res == null ) 
				throw new Exception( "Can't create the file " + id );
		}
		

		col.storeResource( res );		

	}

	public NodeDb[] request( String request ) throws Exception {
		
		// Ask the request to the root node

		TreeNode parent = getParent();
		while ( !( parent instanceof RootNodeDb ) ) {
			parent = parent.getParent();
			if ( parent == null ) // ??
				return null;
		}

		return ( ( XmlDbRootDbImpl )parent ).request( col, request );

	}	

	private String cachedName = null;
	
	public String toString() {
		if ( cachedName == null ) {
			try {
				cachedName = col.getName();
				int i = cachedName.lastIndexOf( "/" );
				if ( i > -1 )
					cachedName = cachedName.substring( i + 1 );
			} catch (XMLDBException e) {
				cachedName = "col";
			}	
		}
		return cachedName;
	}
	
	public boolean canBeDeleted() {

	   
		// Can't work for eXist
		try {

		   Service[] services = col.getServices();

		   if ( services != null )
		   
			   for ( int i = 0; i < services.length; i++ ) {
				   if ( services[ i ] instanceof CollectionManagementService ) {
					   return true;
				   }
			   }

	   } catch (XMLDBException e) {
	   }		

	   return false;
	}

	public boolean delete() throws Exception {
		
		if ( col.getParentCollection() == null ) {	// Root collection can be deleted
			return super.delete();
		}
		
		Service[] services = col.getServices();	
	
		for ( int i = 0; i < services.length; i++ ) {
		   if ( services[ i ] instanceof CollectionManagementService ) {
		
			   CollectionManagementService cms = ( CollectionManagementService )services[ i ];
			   cms.removeCollection( getPath() );
			   // Remove it from the tree
			   super.delete();
			   return true;
			   
		   }
	   }

	   return false;
	}
	
	public Collection getCollectionByName( String name ) {
		
		try {
			for ( int i = 0; i < getChildCount(); i++ ) {
				
				TreeNode tn = ( TreeNode )getChildAt( i );
				if ( tn instanceof XmlDbContainerDbImpl ) {
					
					XmlDbContainerDbImpl container = ( XmlDbContainerDbImpl )tn;
					
					if ( container.getCollection().getName().equals( name ) )
						return container.getCollection();
					
				}
				
			}
			
			// Try deeper

			for ( int i = 0; i < getChildCount(); i++ ) {
				
				TreeNode tn = ( TreeNode )getChildAt( i );
				if ( tn instanceof XmlDbContainerDbImpl ) {
					
					XmlDbContainerDbImpl container = ( XmlDbContainerDbImpl )tn;
					Collection col = container.getCollectionByName( name );
					if ( col != null )
						return col;
					
				}
				
			}

		} catch (XMLDBException e) {
		}

		return null;

	}
	
	public ContainerNodeDb createSubContainer(String id) throws Exception {

		Service[] services = col.getServices();

		if ( services != null ) {

			for ( int i = 0; i < services.length; i++ ) {
				if ( services[ i ] instanceof CollectionManagementService ) {
					
					CollectionManagementService cms = ( CollectionManagementService )services[ i ];
					
					Collection subCol = cms.createCollection( getPath() + "/" + id );
					if ( subCol != null ) {
						
						XmlDbContainerDbImpl sub = new XmlDbContainerDbImpl( subCol );
						addChild( sub );
						return sub;
						
					}
					
				}
			}
		}

		return null;
	}

	public boolean canCreateSubContainer() {

	   try {

		Service[] services = col.getServices();

		if ( services != null )

			for ( int i = 0; i < services.length; i++ ) {
				if ( services[ i ] instanceof CollectionManagementService ) {
					return true;
				}
			}

		} catch (XMLDBException e) {
	
			return false;
		
		}

		return false;

	}	

	public String getPath() {
	
		try {
			String name = col.getName();
			if  (name.indexOf( "/" ) > -1 ) 
				return name;
			else {

				StringBuffer sb = new StringBuffer( name );
				TreeNode parent = getParent();
				while ( parent != null && ( parent instanceof NodeDb ) ) {
					if ( parent instanceof RootNodeDb ) {
						sb.insert( 0, "db/" );
						break;
					}
					sb.insert( 0, parent.toString() + "/" );
					parent = parent.getParent();
				}
				
				return sb.toString();
			}

		} catch (XMLDBException e) {

			return null;
			
		}
		
	}

}

