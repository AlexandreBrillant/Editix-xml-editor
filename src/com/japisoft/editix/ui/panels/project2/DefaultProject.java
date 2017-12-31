package com.japisoft.editix.ui.panels.project2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.japisoft.editix.ui.panels.project2.synchro.Synchronizer;
import com.japisoft.editix.ui.panels.project2.synchro.SynchronizerFactory;
import com.japisoft.framework.ApplicationModel;

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
public class DefaultProject implements Project {

	private static final String WORKSPACE_XML = "workspace.xml";
	private static final String CONFIG_XML = "config.xml";
	
	private File rootPath;
	private Document workspace;
	private Document config;

	public DefaultProject( File root ) {
		this.rootPath = root;
	}

	public File getPath() {
		return rootPath;
	}

	public static boolean isProjectPath( File f ) {
		return new File( f, ".editix" ).exists();
	} 
	
	public boolean skip( String name ) {
		return ".editix".equals( name );
	}

	public void load() throws IOException {

		// Workspace

		workspace = getDocument( WORKSPACE_XML );

		// Synchro

		config = getDocument( CONFIG_XML );

	}
	
	public boolean contains(File f) {
		while ( f != null ) {
			if ( f.equals( getPath() ) )
				return true;
			f = f.getParentFile();
		}
		return false;
	}

	private Document getDocument( String fileName ) {

		File configPath = new File( rootPath, ".editix" );
		if ( !configPath.exists() ) {
			configPath.mkdirs();
		}
				
		Document res = null;
		
		File f = new File( 
			configPath, 
			fileName 
		);

		if ( f.exists() ) {
			try {
				DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				res = builder.parse( f );
			} catch( Exception exc ) {
			}
		}
		
		if ( res == null ) {
			try {
				res = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
				Element root = res.createElement( "root" );
				res.appendChild( root );
			} catch( ParserConfigurationException exc ) {
				ApplicationModel.debug( exc );
			}
		}
		
		return res;
		
	}
	
	public void save() throws IOException {
		save( workspace, WORKSPACE_XML );
		save( config, CONFIG_XML );
	}
	
	private void save( Document doc, String name ) throws IOException {
		File configPath = new File( rootPath, ".editix" );
		if ( !configPath.exists() ) {
			configPath.mkdirs();
		}		
		File path = new File( configPath, name );
		TransformerFactory factory = TransformerFactory.newInstance();
		try {
			Transformer t = factory.newTransformer();
			t.setOutputProperty( OutputKeys.INDENT, "yes" );
			t.setOutputProperty( OutputKeys.METHOD, "xml" );
			t.transform( 
				new DOMSource( doc ), 
				new StreamResult( path ) 
			);
		} catch( TransformerException exc ) {
			throw new IOException( "Can't write to " + path + " : " + exc.getMessage() );
		}		
	}

	private Node root;

	public Node getRoot() {
		if ( root == null ) {
			root = new DefaultNode( this, null, null );
		}
		return root;
	}

	private NodeSortMode mode = NodeSortMode.DIRECTORY; 
	
	public void setSortMode(NodeSortMode mode) {
		this.mode = mode;
	}
	
	public NodeSortMode getSortMode() {
		return mode;
	}

	// ApplicationModel indirect events

	public void openFile( String path, String type) {
		Element element = getWorkspaceFile( path );
		element.setAttribute( "open", "true" );
		element.setAttribute( "type", type );
	}
	
	public String getEncoding(String path) {
		Element element = getWorkspaceFile( path );
		return element.getAttribute( "encoding" );
	}

	public void setEncoding(String path, String encoding) {
		Element element = getWorkspaceFile( path );
		element.setAttribute( "encoding", encoding );
	}
	
	public String getLastType(String path) {
		Element element = getWorkspaceFile( path );
		return element.getAttribute( "type" );
	}

	public void closeFile( String path, Map properties) {
		if ( path == null )	// New file
			return;
		Element element = getWorkspaceFile( path );
		element.setAttribute( "open", "false" );
		if ( properties != null ) {
			Set<String> ks = properties.keySet();
			for ( String key : ks ) {
				Object value = properties.get( key );
				if ( value instanceof String ) {
					Element parameter = getWorkspaceFileParam( path, key );
					parameter.setAttribute( "value", value.toString() );
				}
			}
		}
	}

	public Map getProperties(String path) {
		Element element = getWorkspaceFile( path );
		HashMap newMap = new HashMap();
		NodeList nl = element.getChildNodes();
		for ( int i = 0; i < nl.getLength(); i++ ) {
			if ( nl.item( i ) instanceof Element ) {
				Element pe = ( Element )nl.item( i );
				newMap.put( 
					pe.getAttribute( "name" ), 
					pe.getAttribute( "value" ) 
				);
			}
		}
		return newMap;
	}

	private String regularize( String path ) {
		path = path.replace( "\\", "/" );
		return path;
	}
	
	private Element getWorkspaceFile( String path ) {
		Element root = workspace.getDocumentElement();
		NodeList nl = root.getChildNodes();
		for ( int i = 0; i < nl.getLength(); i++ ) {
			if ( nl.item( i ) instanceof Element ) {
				Element element = ( Element )nl.item( i );
				if ( path.equalsIgnoreCase( 
						element.getAttribute( "path" ) ) )
					return element;
			}
		}
		Element file = workspace.createElement( "file" );
		file.setAttribute( "path", path );
		root.appendChild( file );
		return file;
	}

	private Element getWorkspaceFileParam( String path, String key ) {
		Element file = getWorkspaceFile( path );
		NodeList nl = file.getChildNodes();
		for ( int i = 0; i < nl.getLength(); i++ ) {
			if ( nl.item( i ) instanceof Element ) {
				Element pe = ( Element )nl.item( i );
				if ( key.equalsIgnoreCase( pe.getAttribute( "name" ) ) )
					return pe;
			}
		}
		Element pe = workspace.createElement( "param" );
		pe.setAttribute( "name", key );
		file.appendChild( pe );
		return pe;
	}

	public Synchronizer[] getSynchronizers() {
		Element root = config.getDocumentElement();
		NodeList nl = root.getChildNodes();
		ArrayList<Synchronizer> lst = null;
		for ( int i = 0; i < nl.getLength(); i++ ) {
			if ( nl.item( i ) instanceof Element ) {
				Element e = ( Element )nl.item( i );
				Synchronizer s = buildSynchronizer( e );
				if ( s != null ) {
					if ( lst == null )
						lst = new ArrayList<Synchronizer>();
					lst.add( s );
				}
			}
		}
		if ( lst == null )
			return null;
		return lst.toArray( 
			new Synchronizer[ 
			    lst.size() ] );
	}

	private Synchronizer buildSynchronizer( Element e ) {
		String sname = e.getAttribute( "name" );
		Synchronizer s = SynchronizerFactory.getInstance().getSynchronizer( sname );
		if ( s == null ) {
			return null;
		}
		NodeList nl = e.getChildNodes();
		for ( int i = 0; i < nl.getLength(); i++ ) {
			if ( nl.item( i ) instanceof Element ) {
				Element ee = ( Element )nl.item( i );
				String name = ee.getAttribute( "name" );
				String value = ee.getAttribute( "value" );
				if ( !"".equals( name ) && 
						!"".equals( value ) )
					s.setProperty( name, value );
			} 
		}
		return s;
	}

	public void setSynchronizers(Synchronizer[] synchronizers) {
		Element root = config.getDocumentElement();
		config.removeChild( root );
		root = config.createElement( "root" );
		config.appendChild( root );
		for ( Synchronizer s : synchronizers ) {
			Element e = config.createElement( "synchro" );
			e.setAttribute( "name", s.getName() );
			Set<String> properties = s.getProperties();
			if ( properties != null ) {
				for ( String property : properties ) {
					Element pe = config.createElement( "param" );
					pe.setAttribute( "name", property );
					pe.setAttribute( "value", s.getProperty( property ) );
					e.appendChild( pe );
				}
			}
			root.appendChild( e );
		}
	}
	
	public void setSelectedSynchronized(Synchronizer synchronizer) {
		Element root = config.getDocumentElement();
		NodeList nl = root.getChildNodes();
		for ( int i = 0; i < nl.getLength(); i++ ) {
			if ( nl.item( i ) instanceof Element ) {
				Element e = ( Element )nl.item( i );
				if ( synchronizer.getName().equalsIgnoreCase(
						e.getAttribute( "name" ) ) ) {
					e.setAttribute( "selected", "true" );
				} else
					e.setAttribute( "selected", "false" );
			}
		}
	}

	public int getSelectedSynchronizer() {
		Element root = config.getDocumentElement();
		NodeList nl = root.getChildNodes();
		int index = 0;
		for ( int i = 0; i < nl.getLength(); i++ ) {
			if ( nl.item( i ) instanceof Element ) {
				Element e = ( Element )nl.item( i );
				if ( "true".equalsIgnoreCase( 
						e.getAttribute( "selected" ) ) )
					return index;
				index++;
			}
		}
		return index;
	}
	
	public String[] getNodeState() {
		Element root = config.getDocumentElement();
		String nodestate = root.getAttribute( 
			"nodestate" 
		);
		return nodestate.split( ";" );
	}
	
	public void setNodeState( String[] expandedPath ) {
		Element root = config.getDocumentElement();
		root.setAttribute( 
			"nodestate", "" 
		);		
		if ( expandedPath != null && 
				expandedPath.length > 0 ) {
			String tmp = "";
			for ( int i = 0; i < expandedPath.length; i++ ) {
				if ( i > 0 )
					tmp += ";";
				tmp += expandedPath[ i ];
			}
			root.setAttribute( "nodestate", tmp );
		};
	}

	public static void main( String[] args ) {
		File rootPath = new File( "c:/travail/test" );
		DefaultProject tp = new DefaultProject( rootPath );
		tp.setSortMode( NodeSortMode.FILE );
		DefaultTreeTableModel dtm = new DefaultTreeTableModel( tp.getRoot() );
		JXTreeTable tt = new JXTreeTable( dtm );
		tt.getColumn( 1 ).setMaxWidth( 100 );
		tt.getColumn( 1 ).setHeaderValue( "Size" );
		tt.getColumn( 0 ).setHeaderValue( "File" );
		
		JFrame f = new JFrame();
		f.add( new JScrollPane( tt ) );
		f.setSize( 300, 300 );
		f.setVisible( true );
	}
	
}
