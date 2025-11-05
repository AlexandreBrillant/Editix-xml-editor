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

package com.japisoft.editix.action.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.japisoft.editix.main.EditixApplicationModel;

public class PinManager {

	private File file;

	private PinManager() {
		file = new File( EditixApplicationModel.getAppUserPath(), "pin.txt" );
		read();
	}
	
	private void read() {
		if ( file.exists() ) {
			try {
				BufferedReader reader = new BufferedReader( new FileReader( file ) );
				try {
					String l = null;
					files = new ArrayList<FileInfo>();
					while ( ( l = reader.readLine() ) != null ) {
						String[] tmp = l.split( ";" );
						String location = tmp[ 0 ];
						String type = tmp[ 1 ];
						String properties = tmp[ 2 ];
						files.add( new FileInfo( location, type, stringToMap( properties )) );
					}
				} finally {
					reader.close();
				}
			} catch( Exception exc ) {
			}
		}
	}
	
	private Map stringToMap( String s ) {
		Map m = new HashMap();
		String[] tmp = s.split( "@" );
		for ( String t : tmp ) {
			String[] tmp2 = t.split( "=" );
			m.put( tmp2[ 0 ], tmp2[ 1 ] );
		}
		return m;
	}

	private String mapToString( Map m ) {
		StringBuffer buffer = new StringBuffer();
		Set<String> keys = m.keySet();
		for( String s : keys ) {
			if ( m.get( s ) instanceof String ) {
				if ( buffer.length() > 0 )
					buffer.append( "@" );
				buffer.append( s + "=" + m.get( s ).toString().trim() );
			}
		}
		return buffer.toString();
	}
	
	private void write() {
		try {
			BufferedWriter writer = new BufferedWriter( new FileWriter( file ) );
			try {
				boolean first = true;
				for( FileInfo l : files ) {
					if ( !first )
						writer.newLine();
					writer.write( l.location );
					writer.write( ";" + l.type );
					writer.write( ";" + mapToString( l.properties ) );
					first = false;
				}
			} finally {
				writer.close();
			}
		} catch( Exception exc ) {
			
		}
	}
	
	private List<FileInfo> files = null;
	
	public static class FileInfo {
		public String location;
		public String type;
		public Map properties;
		public FileInfo( String location, String type, Map properties ) {
			this.location = location;
			this.type = type;
			this.properties = properties;
		}
		@Override
		public boolean equals(Object obj) {
			if ( obj instanceof String ) {
				return location.equalsIgnoreCase( obj.toString() );
			}
			return super.equals(obj);
		}
	}
	
	private static PinManager instance = null;

	public static PinManager Instance() {
		if ( instance == null )
			instance = new PinManager();
		return instance;
	}
	
	public void addFile( String location, String type, Map properties ) {
		if ( files == null )
			files = new ArrayList<FileInfo>();
		if ( !files.contains( location ) )
			files.add( new FileInfo( location, type, properties ) );
		write();
	}
	
	public void removeFile( String location ) {
		if ( files != null ) {
			for ( int i = 0; i < files.size(); i++ ) {
				if ( files.get( i ).equals( location ) ) {
					files.remove( i );
					break;
				}
			}
			write();
		}
	}

	public boolean contains( String location ) {
		if ( files != null ) {
			for ( int i = 0; i < files.size(); i++ ) {
				if ( files.get( i ).equals( location ) ) {
					return true;
				}
			}
		}
		return false;
	}
	
	public List<FileInfo> getFiles() {
		return files;
	}
	
	public int getItemCount() {
		if ( files == null )
			return 0;
		return files.size();
	}
	
	public FileInfo getItem( int index ) {
		return files.get( index );
	}
	
}

