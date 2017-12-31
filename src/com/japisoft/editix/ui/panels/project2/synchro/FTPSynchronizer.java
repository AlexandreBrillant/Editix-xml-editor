package com.japisoft.editix.ui.panels.project2.synchro;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;

import com.japisoft.editix.ui.panels.project2.Node;

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
public class FTPSynchronizer extends AbstractSynchronizer {
	
	public String getName() {
		return "FTP";
	}

	public void uploadIt( 
			File rootPath, Node node ) throws IOException {
		FTPClient client = connect();
		if ( client == null )
			throw new IOException( "Can't connect" );
		try {
			upload( client, rootPath, node.getPath() );
		} finally {
			client.disconnect();
		}
	}

	private void upload( 
		FTPClient client, 
		File rootPath, 
		File target ) throws IOException {

		String relativePath = getRelativePath( rootPath, target );
		int i = relativePath.lastIndexOf( "/" );
		String name = relativePath;
		if ( i > -1 ) {
			String parent = relativePath.substring( 0, i );
			name = relativePath.substring( i + 1 );
			cd( client, parent );
		}		

		if ( target.isFile() ) {
			FileInputStream input = new FileInputStream( target );
			try {
				info( "Processing " + name );
				client.storeFile( name, input );
			} finally {
				input.close();
			}
		} else {
			String[] files = target.list();
			if ( files != null ) {
				for ( String file : files ) {
					upload( client, rootPath, new File( target, file ) );
				}
			}
		}
	}

	private void cd( FTPClient client, String path ) throws IOException {
		client.changeWorkingDirectory( getFTPRootPath() );
		String[] parts = path.split( "/" );
		for ( String part : parts ) {
			if ( "".equals( part ) )
				continue;
			client.makeDirectory( part );
			client.changeWorkingDirectory( part );
		}
	}
		
	public void downloadIt( 
			File rootPath, Node node ) throws IOException {
		FTPClient client = connect();
		if ( client == null )
			throw new IOException( "Can't connect" );
		try {
			download( client, rootPath, node.getPath() );
		} finally {
			client.disconnect();
		}				
	}

	private void download( 
		FTPClient client, File rootPath, File target ) throws IOException {

		String relativePath = getRelativePath( rootPath, target );
		String name = relativePath;
		
		if ( target.isDirectory() ) {
			cd( client, relativePath );
			name = null;
		} else {
			int i = relativePath.lastIndexOf( "/" );
			if ( i > -1 ) {
				String parent = relativePath.substring( 0, i );
				name = relativePath.substring( i + 1 );
				cd( client, parent );
			}
		}
		
		FTPFile[] files = client.listFiles( name );
		if ( files != null ) {
			for ( FTPFile file : files ) {
				if ( ".".equals( file.getName() ) )
					continue;
				if ( "..".equals( file.getName() ) )
					continue;				
				info( "Processing " + file.getName() );
				if ( file.isFile() ) {
					File f = target;
					if ( f.isDirectory() )
						f = new File( f, file.getName() );
					FileOutputStream output = new FileOutputStream( f );
					try {
						client.retrieveFile( file.getName(), output );
					} finally {
						output.close();
					}
				} 
			}
		}
		files = client.listDirectories( name );
		if ( files != null ) {
			for ( FTPFile file : files ) {
				if ( ".".equals( file.getName() ) )
					continue;
				if ( "..".equals( file.getName() ) )
					continue;
				target = new File( target, file.getName() );
				target.mkdir();
				download( client, rootPath, target );
			}
		}
	}		

	public boolean test() {
		FTPClient client = null;
		try {
			client = connect();
			return client != null;
		} catch( IOException exc ) {
			if ( client != null ) {
				try {
					client.disconnect();
				} catch( Exception exc2 ) {}
			}
		}
		return false;
	}

	private FTPClient connect() throws IOException {
		FTPClient client = new FTPClient();
		FTPClientConfig config = new FTPClientConfig();
		client.configure( config );
		try {
			client.connect( 
				getProperty( "host" ), 
				Integer.parseInt( getProperty( "port" ) ) 
			);
			client.login( getProperty( "user" ), getProperty( "password" ) );
			client.setFileType( FTPClient.BINARY_FILE_TYPE );
			client.enterLocalPassiveMode();
			if ( !client.changeWorkingDirectory( getFTPRootPath() ) )
				return null;
			return client;
		} catch( Exception exc ) {
			client.disconnect();
			return null;
		}
	}

	private String getFTPRootPath() {
		String path = getProperty( "path" );
		if ( path == null || "".equals( path ) )
			path = "/";
		return path;
	}
	
	@Override
	public String toString() {
		return super.toString() + " [" + getProperty( "host" ) + "]";
	}
	
	public static void main( String[] args ) {
		FTPSynchronizer t = new FTPSynchronizer();
		t.setProperty( "host", "www.unimailer.com" );
		t.setProperty( "port", "21" );
		t.setProperty( "user", "test@unimailer.com" );
		t.setProperty( "password", "abrillant" );
		t.setProperty( "path", "/aa" );
		System.out.println( "TEST=" + t.test() );
	}

}
