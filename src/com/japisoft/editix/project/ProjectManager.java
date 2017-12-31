package com.japisoft.editix.project;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.editix.ui.panels.project.ProjectUI;
import com.japisoft.editix.ui.xslt.XSLTEditor;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.application.descriptor.ActionModel;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.xml.SchemaLocator;
import com.japisoft.framework.xml.parser.FPParser;
import com.japisoft.framework.xml.parser.document.Document;
import com.japisoft.framework.xml.parser.node.NodeFactoryImpl;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.parser.walker.AndCriteria;
import com.japisoft.framework.xml.parser.walker.AttributeCriteria;
import com.japisoft.framework.xml.parser.walker.NodeNameCriteria;
import com.japisoft.framework.xml.parser.walker.TreeWalker;
import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;

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
public class ProjectManager {
	private static final String OPENED_MODE = "open";
	private static String lastProjectFile;

	/** Reset this project */
	public static void cleanProject() {
		if ( lastProjectFile != null ) {
			saveProject( lastProjectFile );
		}
		lastProjectFile = null;
		cleanProjectContent();
		if ( pl != null )
			pl.clean();
		initProjectActions( false );
		cleanProjectContent();
	}

	public static boolean hasItem( String filePath ) {
		TreeWalker tw = new TreeWalker( getProjectRoot() );
		return tw.getOneNodeByCriteria(
				new AttributeCriteria( "path", filePath ), true ) != null;
	}

	private static ProjectListener pl = null;
	public static void setProjectListener( ProjectListener plv ) {
		pl = plv;
	}
	public static ProjectListener getProjectListener() { return pl; }

	/** Open a new project */
	public static boolean openProject( String file ) {
		if ( lastProjectFile != null ) {
			saveProject( lastProjectFile );
		}		
		lastProjectFile = file;
		if ( file != null ) {
			try {
				loadProject( file );
				
				ActionModel.setEnabled( "prj." + ProjectUI.ADDF2_CMD, true );
			} catch( Throwable th ) {
				lastProjectFile = null;
				ApplicationModel.debug( th );
				ActionModel.setEnabled( "prj." + ProjectUI.ADDF2_CMD, false );
				return false;
			}
		} else 
			ActionModel.setEnabled( "prj." + ProjectUI.ADDF2_CMD, false );

		return true;
	}

	public static void updateFileType( String filePath, String type ) {
		if ( projectContent == null )
			return;
		FPNode root = projectContent;
		TreeWalker tw = new TreeWalker( root );
		Enumeration enu = tw.getNodeByCriteria( 
				new NodeNameCriteria( "item" ), 
				true );
		while ( enu.hasMoreElements() ) {
			FPNode itemNode = ( FPNode )enu.nextElement();
			String path = itemNode.getAttribute( "path" );
			if ( path.equals( filePath ) ) {
				itemNode.setAttribute( "type", type );
			}
		}
	}

	public static void searchFilesForType( List l, String type ) {
		searchFilesForType( getProjectRoot(), l, type );
	}

	private static void searchFilesForType( FPNode node, List l, String type ) {
		if ( type.equals( node.getAttribute( "type" ) ) ) {
			if ( !l.contains( node.getAttribute( "path" ) ) )
				l.add( node.getAttribute( "path" ) );
		}
		for ( int i = 0; i < node.childCount(); i++ )
			searchFilesForType( node.childAt( i ), l, type );
	}

	// ArrayList of File
	public static ArrayList getAllFiles() {
		
		if ( projectContent == null ) {
			// Load it ?
			
			String lastProjectFile = 
				Preferences.getPreference( 
						Preferences.SYSTEM_GP,
						"project.lastFile", ( String )null );
			if ( lastProjectFile != null ) {
				openProject( lastProjectFile );
			} else
				return null;

		} 
		
		TreeWalker tw = new TreeWalker( projectContent );
		Enumeration enu = tw.getNodeByCriteria(
					new NodeNameCriteria( "item" ), 
					true );
		ArrayList res = null;
		while ( enu.hasMoreElements() ) {
			FPNode itemNode = ( FPNode )enu.nextElement();
			if ( res == null )
				res = new ArrayList();
			if ( itemNode.hasAttribute( "path" ) ) {
				res.add( 
						new File( 
								itemNode.getAttribute( 
										"path" ) ) );
			}
		}
		
		return res;
		
	}

	public static void checkFilesPath() {
		checkFilesPath( getProjectRoot() );
	}

	public static void checkFilesPath( FPNode node ) {
		if ( node.matchContent( "item" ) ) {
			String path = node.getAttribute( "path" );			
			if ( path.indexOf( "://" ) == -1 ) {
				if ( path.startsWith( "$PJPATH/" ) ) {
					File prjFile = new File( lastProjectFile );
					path = 
						new File( prjFile.getParentFile(),
								path.substring( 8 ) ).toString();
					node.setAttribute( "path", path );
				}	
				File f = new File( path );				
				node.setAttribute( "test", f.exists() );
			}
			for ( int i = 0; i < node.childCount(); i++ ) {
				FPNode child = node.childAt( i );
				if ( child.matchContent( "property" ) ) {
					String value = child.getAttribute( "value", "" );
					if ( value.startsWith( "$PJPATH/" ) ) {
						File prjFile = new File( lastProjectFile );
						path = 
							new File( prjFile.getParentFile(),
									value.substring( 8 ) ).toString();
						child.setAttribute( "value", path );
					}						
				}
			}
		} else {
			for ( int i = 0; i < node.childCount(); i++ )
				checkFilesPath( node.childAt( i ) );
		}
	}

	public static void updateFileEncoding( String filePath, String encoding ) {
		if ( projectContent == null )
			return;
		FPNode root = projectContent;
		TreeWalker tw = new TreeWalker( root );
		Enumeration enu = tw.getNodeByCriteria( 
				new NodeNameCriteria( "item" ), 
				true );
		while ( enu.hasMoreElements() ) {
			FPNode itemNode = ( FPNode )enu.nextElement();
			String path = itemNode.getAttribute( "path" );
			if ( path.equals( filePath ) ) {				
				itemNode.setAttribute( "encoding", encoding );
			}
		}
	}

	public static void updateFilePath( String oldFilePath, String newFilePath ) {
		if ( projectContent == null )
			return;
		FPNode root = projectContent;
		TreeWalker tw = new TreeWalker( root );
		Enumeration enu = tw.getNodeByCriteria( 
				new NodeNameCriteria( "item" ), 
				true );
		while ( enu.hasMoreElements() ) {
			FPNode itemNode = ( FPNode )enu.nextElement();
			String path = itemNode.getAttribute( "path" );
			if ( path.equals( oldFilePath ) ) {				
				itemNode.setAttribute( "path", newFilePath );
				pl.refresh();
			}
		}			
	}
	
	public static void initProjectActions( boolean canSave ) {
		// Disable save project actions
/*
		ActionModel.setEnabled( "prjSave", canSave );
		ActionModel.setEnabled( "prjSaveAs", canSave );
		ActionModel.setEnabled( "prjClose", canSave );
*/
	}
	
	static void loadProject( String file ) throws Throwable {
		FPParser p = new FPParser();
		FPNode n = ( FPNode )p.parse( new FileInputStream( file ) ).getRoot();

		if ( Preferences.getPreference( "project", "relativePath", true ) ) {
			resolveRelativePath( file, n );
		}

		checkFilesPath(n);		
		
		projectContent = n;
		n.setAttribute( "path", file );
		// Load the default opened
		Iterator it = getOpenedItems();
		while ( it.hasNext() ) {
			openItem( ( FPNode ) it.next() );
		}
		String fileTmp = n.getAttribute( "lastbrowsed" );
		if ( pl != null )
			pl.loadProject( file );
		initProjectActions( true );
	}
	
	private static void resolveRelativePath( String refPath, FPNode node ) {
		if ( node.matchContent( "item" ) ) {

			String path = node.getAttribute( "path" );
			node.setAttribute( "path", Path.getAbsolutePath( refPath, path ) );
			
		} else
		if ( node.matchContent( "property" ) ) {
			
			String name = node.getAttribute( "name" );
			if ( name.endsWith( ".file" ) ) {
				node.setAttribute( "value", Path.getAbsolutePath( refPath, node.getAttribute( "value" ) ) );	
			}

		} 
		for ( int i = 0; i < node.getViewChildCount(); i++ )
			resolveRelativePath( refPath, ( FPNode )node.getViewChildAt( i ) );
	}

	public static FPNode addItem( XMLContainer container, FPNode parentNode ) {
		String path = container.getCurrentDocumentLocation();
		
		// Check if the node exist already
		TreeWalker tw = new TreeWalker( getProjectRoot() );
		FPNode sn = ( FPNode )tw.getOneNodeByCriteria(
				new AndCriteria(
						new NodeNameCriteria( "item" ),
						new AttributeCriteria( "path", path ) ),
				true );
		if ( sn != null ) {
			sn.setApplicationObject( "select" );
			return sn;
		}

		FPNode item = new FPNode( FPNode.TAG_NODE, "item" );
		item.setAttribute( "path", path );
		item.setAttribute( "type", container.getDocumentInfo().getType() );
		if ( container.getProperty( "encoding" ) != null )
			item.setAttribute( 
					"encoding", 
					( String )container.getProperty( "encoding" ) );

		if ( container.getSchemaAccessibility().getRelaxNGValidationLocation() != null ) 
			container.setProperty( 
					RELAXNG_PROPERTY, 
					container.getSchemaAccessibility().getRelaxNGValidationLocation() );

		synchronizedItemWithXMLContainerProperties( item, container );

		parentNode.appendChild( item );
		return item;
	}
	
	public static FPNode addGroup( String name, FPNode parentNode ) {
		FPNode gp = new FPNode( FPNode.TAG_NODE, "group" );		
		gp.setAttribute( "path", name );
		parentNode.appendChild( gp );
		return gp;
	}

	public static String getGroupName( FPNode groupNode ) {
		if ( groupNode.isRoot() )
			return groupNode.getContent();
		return groupNode.getAttribute( "path" );
	}

	public static void updateGroupName( String newName, FPNode groupNode ) {
		if ( groupNode.isRoot() ) {
			groupNode.setContent( newName );
		} else
			groupNode.setAttribute( "path", newName );
	}
	
	public static void delete( FPNode parentNode ) {
		parentNode.getFPParent().removeChildNode( parentNode );
	}

	static FPNode projectContent = null;

	public static int getProjectElementCount() { 
		if ( projectContent == null )
			return 0;
		return projectContent.childCount();
	}

	public static FPNode getProjectRoot() {
		if ( projectContent == null ) {
			projectContent = 
				new FPNode( FPNode.TAG_NODE, "MyProject" );
			Document d = new Document();
			projectContent.setDocument( d );
			initProjectActions( true );
		}
		return projectContent;
	}

	// Return the current opened items
	public static Iterator getOpenedItems() {
		ArrayList al = new ArrayList();
		TreeWalker tw = new TreeWalker( getProjectRoot() );
		Enumeration enu = tw.getNodeByCriteria( 
				new NodeNameCriteria( "item" ), 
				true );
		while ( enu.hasMoreElements() ) {
			FPNode itemNode = ( FPNode )enu.nextElement();
			if ( "true".equals( itemNode.getAttribute( OPENED_MODE ) ) ||
					!itemNode.hasAttribute( OPENED_MODE) )
				al.add( itemNode );
		}		
		return al.iterator();
	}

	// Add the item to a visible state inside the tabbedPane
	public static boolean openItem( FPNode node ) {
		String path = node.getAttribute( "path" );
		String type = node.getAttribute( "type" );
		String encoding = node.getAttribute( "encoding" );
		checkFilesPath( node );
		boolean found = EditixFrame.THIS.activeXMLContainer( path );
		if ( found ){
			node.setAttribute( OPENED_MODE, true );
			return true;
		}

		HashMap tempo = ProjectManager.getProjectPropertiesAt( node );		
		String user = ( String )tempo.get( "vfs.user" );
		String password = ( String )tempo.get( "vfs.password" );
		ActionEvent ae = null;
		
		if ( user != null )
			ae = new ActionEvent( user, 0, user + "$$" + password );

		ActionModel.activeActionById(
				ActionModel.OPEN, 
				ae, 
				path, 
				type, 
				encoding );

		if ( ActionModel.LAST_ACTION_STATE ) {
			// Reset the properties from the project content
			
			EditixFrame.THIS.getSelectedContainer().resetProperties(
				new HashMap() );

			IXMLPanel panel = EditixFrame.THIS.getSelectedPanel();
 				
			Iterator it = tempo.keySet().iterator();
			while ( it.hasNext() ) {
				String key = ( String )it.next();
				String value = ( String )tempo.get( key );
				panel.setProperty( key, value );
				if ( ProjectManager.RELAXNG_PROPERTY.equals( key ) ) {
					panel.getMainContainer().getSchemaAccessibility().setRelaxNGValidationLocation(
							value );
				}
			}
			panel.setProperty( "system-end", null );
			node.setAttribute( OPENED_MODE, true );
			
			// Synchronize actions for this new one
			ActionModel.synchronizeState( panel.getMainContainer() );
		}

		return ActionModel.LAST_ACTION_STATE;
	}

	public static String getProjectElementAt( int index ) {		
		return projectContent.childAt( index ).getAttribute( "path" );
	}

	public static String getProjectTypeAt( int index ) {
		return projectContent.childAt( index ).getAttribute( "type" );
	}

	public static String getProjectEncodingAt( int index ) {
		return projectContent.childAt( index ).getAttribute( "encoding" );
	}
	
	public static HashMap getProjectPropertiesAt( int index ) {
		FPNode item = ( FPNode )projectContent.childAt( index );
		return getProjectPropertiesAt( item );
	}

	public static HashMap getProjectPropertiesAt( FPNode item ) {
		HashMap map = new HashMap();
		TreeWalker walker = 
			new TreeWalker( item );
		Enumeration enu = walker.getNodeByCriteria(
			new NodeNameCriteria( "property" ), false );
		while ( enu.hasMoreElements() ) {
			FPNode pn = ( FPNode )enu.nextElement();
			map.put( 
				pn.getAttribute( "name" ),
				pn.getAttribute( "value" ) );
		}

		return map;
	}

	public static boolean isEmpty() { return projectContent == null; }
	
	public static void cleanProjectContent() {
		projectContent = null;
	}

	public static final String RELAXNG_PROPERTY = "relaxng";
	
	/** Add a project element */
	public static void addProjectElement( XMLContainer container ) {
		String path = container.getCurrentDocumentLocation();
		FPNode item = new FPNode( FPNode.TAG_NODE, "item" );
		item.setAttribute( "path", path );
		item.setAttribute( "type", container.getDocumentInfo().getType() );
		if ( container.getProperty( "encoding" ) != null )
			item.setAttribute( 
					"encoding", 
					( String )container.getProperty( "encoding" ) );

		if ( container.getSchemaAccessibility().getRelaxNGValidationLocation() != null ) 
			container.setProperty( 
					RELAXNG_PROPERTY, 
					container.getSchemaAccessibility().getRelaxNGValidationLocation() );

		synchronizedItemWithXMLContainerProperties( item, container );

		String xsltDockingState = ( String )container.getProperty( 
				XSLTEditor.MAXIMIZED_CMD );
		if ( xsltDockingState != null ) {
			FPNode p = new FPNode( FPNode.TAG_NODE, "property" );
			p.setAttribute( "name", XSLTEditor.MAXIMIZED_CMD );
			p.setAttribute( "value", xsltDockingState );
			item.appendChild( p );
		}

		getProjectRoot().appendChild( item );
	}

	private static void synchronizedItemWithXMLContainerProperties( FPNode itemNode, XMLContainer container ) {		
		// Remove all properties node for the itemNode
		int i = 0;
		while ( i < itemNode.childCount() ) {
			FPNode childNode = itemNode.childAt( i );
			if ( childNode.matchContent( "property" ) ) {
				itemNode.removeChildNode( childNode );
			} else
				i++;
		}
		
		Iterator properties = container.getProperties();
		if ( properties != null ) {
			for ( ; properties.hasNext(); ) {
				String property = ( String )properties.next();
				Object value = container.getProperty( property );
				if ( value instanceof SchemaLocator ) {
					SchemaLocator loc = ( SchemaLocator )value;
					if ( loc.location != null )
						value = loc.location;
				}
				if ( value instanceof String ) {
					String str = ( String )value;
					FPNode p = new FPNode( FPNode.TAG_NODE, "property" );
					p.setAttribute( "name", property );
					p.setAttribute( "value", str );
					itemNode.appendChild( p );
				}
			}		
		}
	}

	/** @return the last open project */
	public static String getLastOpenProject( boolean forceANew ) {
		if ( lastProjectFile == null && forceANew ) {
			lastProjectFile = new File( ApplicationModel.getAppUserPath(), "myproject.pre" ).toString();
			return lastProjectFile.toString();
		}
		return lastProjectFile;
	}
	
	/** Save the current project to this file */
	public static boolean saveProject( String file ) {
		if ( projectContent == null )
			return false;
		if ( file.indexOf( ".") == -1 )
			file += ".pre";
		try {
			
			Document d = synchronizeProjectContent();

			if ( Preferences.getPreference( "project", "relativePath", true ) ) {
				// Clone it before saving for avoiding relative path in the current tree				
				d = new Document( ( ( FPNode )d.getRoot() ).clone( true ) );
				createRelativePathDocument( d, file );
			}
			d.write( new FileOutputStream( file ) );

		} catch( Throwable th ) {
			ApplicationModel.debug( th );
			return false;			
		}
		return true;
	}

	private static void createRelativePathDocument( Document source, String path ) {
		createRelativePathDocument( ( FPNode )source.getRoot(), path );
	}
	
	private static void createRelativePathDocument( FPNode source, String refPath ) {
		if ( source.matchContent( "item" ) ) {
			
			String path = 
				source.getAttribute( "path" );
			source.setAttribute( 
					"path", 
					Path.getRelativePath( refPath, path ) );

		} else
		if ( source.matchContent( "property" ) ) {

			String name = source.getAttribute( "name" );
			if ( name.endsWith( ".file" ) ) {
				String value = source.getAttribute( "value" );
				source.setAttribute(
						"value",
						Path.getRelativePath( refPath, value ) );
			}
			
		}
		
	
		for ( int i = 0; i < source.childCount(); i++ ) {
			createRelativePathDocument( 
					source.childAt( i ), 
					refPath );
		}
	}

	public static Document synchronizeProjectContent() {
		if ( projectContent == null )
			return null;
		Document doc = new Document( projectContent );
		ArrayList toSynchronized = null;
		
		// Mark the opened documents for the next usage
		FPNode root = ( FPNode )doc.getRoot();
		TreeWalker tw = new TreeWalker( root );
		Enumeration enu = tw.getNodeByCriteria( 
				new NodeNameCriteria( "item" ), 
				true );
		while ( enu.hasMoreElements() ) {
			FPNode itemNode = ( FPNode )enu.nextElement();
			String path = itemNode.getAttribute( "path" );
			itemNode.setAttribute( OPENED_MODE, false );
			for ( int i = 0; i < EditixFrame.THIS.getXMLContainerCount(); i++ ) {
				XMLContainer xc = EditixFrame.THIS.getXMLContainer( i );
				if ( xc == null )
					continue;
				if ( path.equals( xc.getCurrentDocumentLocation() ) ) {
					if ( toSynchronized == null )
						toSynchronized = new ArrayList();
					toSynchronized.add( itemNode );
					toSynchronized.add( xc );
					itemNode.setAttribute( OPENED_MODE, true );
				} 
			}
		}
		if ( toSynchronized != null ) {
			for ( int i = 0; i < toSynchronized.size(); i += 2 ) {
				FPNode item = ( FPNode )toSynchronized.get( i );
				XMLContainer c = ( XMLContainer )toSynchronized.get( i + 1 );
				synchronizedItemWithXMLContainerProperties( item, c );
			}
		}
		return doc;
	}
	
	public static void exportToZip( File target ) throws Throwable {
		ZipOutputStream zout = new ZipOutputStream( 
			new FileOutputStream( target ) );
		try {
			FPNode pnode = getProjectRoot().clone( true );
			writeContent( "", zout, pnode, pnode );
			// Write the project file
			String prjFile = getProjectRoot().getContent() + ".pre";
			ZipEntry e = new ZipEntry( prjFile );
			zout.putNextEntry( e );
			Document d = new Document( pnode );
			pnode.setDocument( d );
			d.write( zout );
		} finally {
			try {
				zout.close();
			} catch( ZipException e ) {
				EditixFactory.buildAndShowErrorDialog( "Can't export to a zip file" );
			}
		}
	}

	private static void writeContent( 
			String relPath, 
			ZipOutputStream zout, 
			FPNode rootNode,
			FPNode node ) throws Throwable {
		if ( node.matchContent( "group" ) || node.isRoot() ) {
			if ( !node.isRoot() )
				relPath = relPath + "/" + node.getAttribute( "path" ) + "/";
			for ( int i = 0; i < node.childCount(); i++ )
				writeContent( relPath, zout, rootNode, node.childAt( i ) );
		} else
		if ( node.matchContent( "item" ) ) {
			String filePath = node.getAttribute( "path" );
			if ( filePath.indexOf( "://" ) == -1 ) {
				String iniPathStr = filePath;
				File initPath = new File( filePath );
				if ( initPath.exists() ) {
					int i = filePath.lastIndexOf( "\\" );
					if ( i == -1 )
						i = filePath.lastIndexOf( "/" );
					if ( i != -1 )
						filePath = filePath.substring( i + 1 );
					filePath = relPath + filePath;
					String newPath = "$PJPATH";
					if ( filePath.startsWith( "/" ) )
						newPath = newPath + filePath;
					else
						newPath = newPath + "/" + filePath;
					node.setAttribute( "path", newPath );
					replacePathInsideParams( iniPathStr, newPath, rootNode );
					
					ZipEntry ze = new ZipEntry( filePath );
					zout.putNextEntry( ze );
					FileInputStream input = new FileInputStream( initPath );
					try {
						byte[] buffer = new byte[ 1024 ];
						int c = 0;
						while ( ( c = input.read( buffer ) ) != -1 ) {
							zout.write( buffer, 0, c );
						}
					} finally {
						input.close();
					}
				}
			}
		}
	}

	private static void replacePathInsideParams( String oldPath, String newPath, FPNode node ) {
		if ( node.matchContent( "property" ) ) {
			if ( oldPath.equals( node.getAttribute( "value" ) ) ) {
				node.setAttribute( "value", newPath );
			}
		}
		for ( int i = 0; i < node.childCount(); i++ )
			replacePathInsideParams( oldPath, newPath, node.childAt( i ) );
	}

}
