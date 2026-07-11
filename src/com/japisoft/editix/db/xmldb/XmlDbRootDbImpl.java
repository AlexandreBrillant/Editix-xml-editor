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

package com.japisoft.editix.db.xmldb;

import java.util.ArrayList;

import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.Service;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XPathQueryService;

import com.japisoft.editix.db.NodeDb;
import com.japisoft.editix.db.RootNodeDb;
import com.japisoft.editix.ui.EditixFactory;

public class XmlDbRootDbImpl extends XmlDbContainerDbImpl implements RootNodeDb {

	public static String XMLRESOURCE_TYPE = "XMLResource";
	public static String BINARYRESOURCE_TYPE = "BinaryResource";

	public XmlDbRootDbImpl( String driverName, String url, String user, String password ) throws Exception {
		
		super( null );
		
		this.driverName = driverName;
		this.url = url;
		this.user = user;
		this.password = password; 

		int i = url.indexOf( "/" );
		String protocol = url.substring( 0, i );
		i = url.indexOf( "//" );
		int j = url.indexOf( "/", i + 2 );
		String host = url.substring( i + 2, j );
		name = protocol + "-" + host; 		

		if ( url.endsWith( "/" ) )
			url = url + "db";
/*		else
			if ( !url.endsWith( "/db" ) )
				url = url + "/db"; */
		
		col =  
			DatabaseManager.getCollection( url, user, password );
		
		//DatabaseManager.getCollection( url, null, null );
	}
	
	public String toString() {
		return name;
	}

	public boolean canConnect() throws Exception {
		return ( col != null ) && ( col.isOpen() && col.getResourceCount() >= 0 );
	}	

	public NodeDb[] request(String request) throws Exception {
		return request( col, request );
	}

	public NodeDb[] request(Collection col, String request) throws Exception {

       Service[] services = col.getServices();
       if ( services == null )
    	   throw new Exception( "Can't execute a query for this database" );
       
	   XPathQueryService service = null;
	   
	   // Get the first request service

	   for ( int i = 0; i < services.length; i++ ) {
		   if ( services[ i ] instanceof XPathQueryService ) {
			   service = ( XPathQueryService )services[ i ];
			   break;
		   }
	   }

	   if ( service == null )
		   service = ( XPathQueryService ) col.getService( "XPathQueryService", "1.0" );
	   
	   if ( service == null )
		   throw new Exception( "Can't find a service for this query" );
	   
       service.setProperty("indent", "yes");  

       ResourceSet result = service.query( request );  
       ResourceIterator i = result.getIterator();
       ArrayList al = new ArrayList();
       
       XmlDbContainerDbImpl parentNode = null;
       String previousColName = null;
       
       while ( i.hasMoreResources() ) {  
    	   Resource r = i.nextResource();  
    	     
/*    	   System.out.println( r.getClass() );
    	   System.out.println( r.getResourceType() );
    	   System.out.println( r.getId() ); */
    	   
    	   Collection pcol = r.getParentCollection();
    	   if ( pcol == null )
    		   continue;
    	   
    	   // The parent collection may not work (eXist problem)

    	   if ( previousColName == null || !( 
    			   pcol.getName().equals( previousColName ) ) ) {

    		   // Avoid duplicated container in memory
    		   pcol = getCollectionByName( pcol.getName() );

    		   if ( pcol == null ) {
    			   // May be a numeric result
    			   EditixFactory.buildAndShowInformationDialog( "" + r.getContent() );
    			   continue;
    		   }

    		   parentNode = new XmlDbContainerDbImpl( pcol );
    	   }

    	   previousColName = pcol.getName();

    	   // The id may be null : Xindice problem
    	   if ( r.getId() != null ) {    	   
	    	   XmlDbFileNodeDbImpl
	    	   	file = new XmlDbFileNodeDbImpl( 
	    	   			parentNode, 
	    	   			r.getId() 
	    	   );
	    	   parentNode.addChild( file );
	           al.add( file );
    	   }
       }  		

       NodeDb[] nodes = new NodeDb[ al.size() ];
       for ( int j = 0; j < nodes.length; j++ ) {
    	   nodes[ j ] = ( NodeDb )al.get( j );
       }

       return nodes;

	}		

	public String toXml() {
		return "<connection driver='" + driverName + "' url='" + url + "' user='" + user + "' password='" + password + "'/>";
	}

	public boolean canBeDeleted() {
		return true;
	}

}
