package com.japisoft.framework.spreadsheet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import com.japisoft.framework.preferences.Preferences;

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
public class CsvSpreadsheet extends AbstractSpreadsheet {

	public void read(InputStream input) throws Exception {
		InputStreamReader r = new InputStreamReader( input, Preferences.getPreference( "CSV", "DefaultEncoding", "UTF-8" ) );
		BufferedReader reader = new BufferedReader( r );
		// Search for the first column with the @ value
		String line = null;
		while ( ( line = reader.readLine() ) != null ) {
			if ( !"".equals( line ) )
				readLine( line );
		}
	}

	public void write(OutputStream output) throws Exception {
		OutputStreamWriter w = new OutputStreamWriter( output, Preferences.getPreference( "CSV", "DefaultEncoding", "UTF-8" ) );
		BufferedWriter bw = new BufferedWriter( w );
		try {
			for ( int i = 0; i < getColumnCount(); i++ ) {
				if ( i > 0 )
					bw.write( "," );				
				bw.write( getColumnName( i ) );
			}
			bw.newLine();
			for ( int j = 0; j < getRowCount() - 1; j++ ) {
				if ( j > 0 )
					bw.newLine();				
				for ( int i = 0; i < getColumnCount(); i++ ) {
					bw.write( getValueAt( j, i ) );
					if ( i != getColumnCount() - 1 )
						bw.write( "," );
				}
			}
		} finally {
			bw.close();
		}
	};

	private String separator = ",|;";

	private void readLine( String line ) {
		String[] content = splitLine( line );
		readLine( content );
	}

	private String[] splitLine( String line ) {
		String[] datas = line.split( separator );
		for ( int i = 0; i < datas.length; i++ ) {
			if ( datas[ i ].startsWith( "\"" ) )
				datas[ i ] = datas[ i ].substring( 1 );
			if ( datas[ i ].endsWith( "\"" ) )
				datas[ i ] = datas[ i ].substring( 0, datas[ i ].length() - 1 );
		}
		return datas;
	}

}
