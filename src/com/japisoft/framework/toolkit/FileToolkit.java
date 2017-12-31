package com.japisoft.framework.toolkit;

import java.io.BufferedReader;
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
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

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
public class FileToolkit {

	// Available file charset encoding
	public static String[] FILE_ENCODING = new String[] { "DEFAULT", "ASCII",
			"Cp1252", "ISO8859_1", "UnicodeBig", "UnicodeBigUnmarked",
			"UnicodeLittle", "UnicodeLittleUnmarked", "UTF8", "UTF-16" };

	public static Reader getReaderForFile( File fileName, String encoding )
			throws IOException {
		Reader rr;
		if (encoding == null || "DEFAULT".equals(encoding))
			rr = new FileReader(fileName);
		else
			rr = new InputStreamReader(new FileInputStream(fileName), encoding);
		return rr;
	}

	public static Writer getWriterForFile( File fileName, String encoding )
			throws IOException {
		Writer w = null;
		if (encoding == null || "DEFAULT".equals(encoding))
			w = new FileWriter(fileName);
		else
			w = new OutputStreamWriter(new FileOutputStream(fileName), encoding);
		return w;
	}

	public static void writeFile( File fileName, String content, String encoding ) throws IOException {
		OutputStreamWriter w = new OutputStreamWriter( 
				new FileOutputStream( fileName ), 
				encoding == null ? "UTF-8" : encoding 
		);
		try {
			w.write( content );
		} finally {
			w.close();
		}
	}
	
	public static String getContentFromFileName( String fileName, String encoding )
			throws Throwable {

		StringBuffer sb = new StringBuffer();

		File f = new File(fileName);
		if (f.exists()) {
			Reader rr = null;

			if (encoding == null || "DEFAULT".equals(encoding))
				rr = new FileReader(fileName);
			else
				rr = new InputStreamReader(new FileInputStream(fileName),
						encoding);

			BufferedReader r = new BufferedReader(rr);

			try {
				String line = null;
				String rc = System.getProperty("line.separator");
				while ((line = r.readLine()) != null) {
					if (sb.length() > 0) {
						sb.append(rc);
					}
					sb.append(line);
				}
				// while ((c = r.read()) != -1) {
				// sb.append((char) c);
				// }
			} finally {
				r.close();
			}
		} else if (fileName.indexOf("://") > -1) {
			// URL case
			URL url = new URL(fileName);
			InputStream input = url.openStream();
			return getContentFromInputStream(input, encoding);
		}
		;
		return sb.toString();
	}

	public static String getContentFromInputStream(InputStream input,
			String encoding) throws Throwable {
		StringBuffer sb = new StringBuffer();
		char[] buffer = new char[1024];
		try {
			int c;

			Reader r = null;
			if (encoding == null || "DEFAULT".equals(encoding))
				r = new InputStreamReader(input);
			else
				r = new InputStreamReader(input, encoding);

			while ((c = r.read(buffer)) != -1) {
				sb.append(new String(buffer, 0, c));
			}
		} finally {
			input.close();
		}
		return sb.toString();
	}

	/**
	 * Retreive an initial resource location using this resource name
	 */
	public static String getPathForObject(String resource) {
		URL url = ClassLoader.getSystemResource(resource);
		if (url == null)
			return null;
		if (url.getProtocol().equals("jar")) {
			try {
				JarURLConnection jarCon = (JarURLConnection) url
						.openConnection();
				url = jarCon.getJarFileURL();
				String _ = url.toExternalForm();
				_ = _.replaceAll("%20", " ");
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

	public static String toURL( File f ) {
		return "file://" + f.toString();
	}

	public static String toFile( URL u ) {
		String all = u.toExternalForm();
		all = all.replace( "%20", " " );
		String normalized = null;
		if ( all.startsWith( "file://" ) ) {
			File f = new File( all.substring( 7 ) );
			if ( f.exists() ) {
				normalized = f.toString();
				return normalized;
			}
		}
		if ( all.startsWith( "file:/" ) ) {
			File f = new File( all.substring( 6 ) );
			if ( f.exists() ) {
				normalized = f.toString();
				return normalized;
			}
		}
		if ( all.startsWith( "file:" ) ) {
			File f = new File( all.substring( 5 ) );
			if ( f.exists() )
				normalized = f.toString();			
		}		
		//if ( normalized != null )
		//	normalized = normalized.replaceAll( "%20", " " );
		return normalized;
	}

	public static String getContentFromURL( URL url ) throws Throwable {
		return getContentFromInputStream( url.openStream(), null );
	}

	public static void copyFileFromURL( URL source, File destination )
			throws IOException {
		InputStream input = source.openStream();
		try {
			byte[] buffer = new byte[ 1024 ];
			int read = 0;
			FileOutputStream output = new FileOutputStream( destination );
			try {
				while ( ( read = input.read( buffer ) ) != -1 ) {
					output.write( buffer, 0, read );
				}
			} finally {
				output.close();
			}
		} finally {
			input.close();
		}
	}

	public static boolean writeToZip( File zipFile, String entryPath, String encoding, String content ) throws IOException {
		
		// Load the zip file into memory
		byte[] zipMem = new byte[ (int)zipFile.length() ];
		FileInputStream input = new FileInputStream( zipFile );
		try {
			input.read( zipMem );
		} finally {
			input.close();
		}

		ZipOutputStream output = new ZipOutputStream( 
				new FileOutputStream( zipFile ) );
		ZipInputStream input2 = new ZipInputStream(
				new ByteArrayInputStream( zipMem ) );
		
		ZipEntry entry = new ZipEntry( entryPath );
		
		if ( entryPath.indexOf( "/" ) > -1 ) {
			if ( !entryPath.startsWith( "/" ) )
				entryPath = "/" + entryPath;
		}
		
		output.putNextEntry( entry );
		output.write( content.getBytes( encoding ) );

		// Write other
		while ( ( entry = input2.getNextEntry() ) != null ) {
			
			String name = entry.getName();
			if ( name.indexOf( "/" ) > -1 ) {
				if ( !name.startsWith( "/" ) )
					name = "/" + name;
			}

			if ( !name.equals( entryPath ) ) {
				System.out.println("WRITE " + name );
				entry = new ZipEntry( entry.getName() );
				output.putNextEntry( entry );
				byte[] buffer = new byte[ 1024 ];
				int c;
				while ( ( c = input2.read( buffer ) ) != -1 ) {
					output.write( buffer, 0, c );
				}
			}
		}
		
		output.flush();
		output.finish();
		output.close();

		return true;
	}

	public static boolean sameFileName( String uri1, String uri2 ) {
		if ( uri1 == null || 
				uri2 == null )
			return false;
		return fileNameFromUri( uri1 ).equalsIgnoreCase(
				fileNameFromUri( uri2 ) );
	}

	public static String fileNameFromUri( String uri ) {
		if ( uri == null )
			return null;
		int i = uri.lastIndexOf( "/" );
		if ( i == -1 )
			i = uri.lastIndexOf( "\\" );
		if ( i == -1 )
			return uri;
		return uri.substring( i + 1 );
	}
	
}
