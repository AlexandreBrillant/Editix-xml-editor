package com.japisoft.editix.toolkit;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.SwingUtilities;

import com.japisoft.editix.ui.EditixStatusBar;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.xml.Encoding;
import com.japisoft.framework.xml.XMLFileData;
import com.japisoft.framework.xml.XMLToolkit;

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
public class Toolkit {

	// Available file charset encoding
	static String[] FILE_ENCODING = Encoding.XML_ENCODINGS;

	public static String getCurrentFileEncoding() {
		// Choose the charset encoding
		String[] encoding = 
			encoding = Preferences.getPreference(
					"file", "rw-encoding",
					FILE_ENCODING );
		return encoding[ 0 ];
	}

	public static Reader getReaderForFile( File fileName ) throws IOException {
		String encoding = getCurrentFileEncoding();
		Reader rr;
		if ( "DEFAULT".equals( encoding ) || "AUTOMATIC".equals( encoding ) )
			rr = new FileReader( fileName );
		else
			rr = new InputStreamReader( new FileInputStream( fileName ), encoding );
		return rr;
	}

	public static Reader getReaderForFile( String fileName ) throws IOException {
		return getReaderForFile( new File( fileName ) );
	}
	
	public static Writer getWriterForFile( File fileName ) throws IOException {
		String encoding = getCurrentFileEncoding();
		Writer w = null;
		if ( "DEFAULT".equals( encoding ) )
			w = new FileWriter( fileName );
		else
			w = new OutputStreamWriter( 
				new FileOutputStream( fileName ), encoding );
		return w;
	}

	public static XMLFileData getEncodedString( byte[] data ) throws Throwable {
		String encoding = getCurrentFileEncoding();
		XMLFileData r = XMLToolkit.getContentFromInputStream(
				new ByteArrayInputStream( data ),
				Toolkit.getCurrentFileEncoding() );
		return r;
	}
	
	public static String getContentFromInputStream(InputStream input)
		throws Throwable {
		StringBuffer sb = new StringBuffer();
		char[] buffer = new char[ 1024 ];
		try {
			int c;

			Reader r = null;
			String encoding = getCurrentFileEncoding();
			if ( "DEFAULT".equals( encoding ) )
				r = new InputStreamReader( input );
			else
				r = new InputStreamReader( input, encoding );

			while ((c = r.read( buffer )) != -1) {
				sb.append( new String( buffer,0 , c ) );
			}
		} finally {
			input.close();
		}
		return sb.toString();
	}

	/** Retreive an initial resource location using this resource name
	 */
	public static String getPathForObject(String resource) {
		URL url = ClassLoader.getSystemResource(resource);
		if (url == null)
			return null;
		if (url.getProtocol().equals("jar")) {
			try {
				JarURLConnection jarCon =
					(JarURLConnection) url.openConnection();
				url = jarCon.getJarFileURL();
				String _ = url.toExternalForm();
				_ = _.replaceAll( "%20", " " );
				if (_.startsWith("file://"))
					return _.substring(6);
				else if (_.startsWith("file:/"))
					return _.substring(5);
				return _;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return url.toExternalForm();
	}

	public static String toURL( File f ) throws MalformedURLException {
		String res = f.toURI().toURL().toString();
		res = res.replace( " ", "%20" );
		return res;
	}
	
	public static void startJob( String name, Runnable run ) {
		EditixStatusBar.ACCESSOR.setMessage( name );
		SwingUtilities.invokeLater( run );
	}
	
	public static void stopJob() {
		EditixStatusBar.ACCESSOR.setXPathLocation( "Completed" );
	}

	public static String toString( URL u ) {
		return u.toExternalForm().replace( " ", "%20" );
	}
	
}
