package com.japisoft.framework.app.toolkit;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.BufferedReader;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

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
public class Toolkit {

	public static Icon getImageIcon(String location) {
		return getImageIcon(location, false);
	}

	public static Icon getImageIcon(String location, boolean defaultEmptyImage) {
//		URL url = ClassLoader.getSystemClassLoader().getResource(location);
		
		URL url = Thread.currentThread().getContextClassLoader().getResource(
				location );

		if (url != null)
			return new ImageIcon(url);
		else {
			File f = new File(location);
			if (f.exists())
				return new ImageIcon(location);
			else
				com.japisoft.framework.toolkit.Logger.addWarning("Can't find "
						+ location);
		}

		if (defaultEmptyImage) {
			return getDefaultImage();
		}

		return null;
	}

	static String DEFAULT_FILE_ENCODING = "DEFAULT";
	
	// Available file charset encoding
	public static String[] FILE_ENCODING = new String[] {
			DEFAULT_FILE_ENCODING, "UTF8", "ASCII", "Cp1252", "ISO8859_1", "ISO8859_2",
			"UnicodeBig", "UnicodeBigUnmarked", "UnicodeLittle",
			"UnicodeLittleUnmarked", "UTF-16" };

	/** It uses the preference file/rw-encoding */
	public static String getCurrentFileEncoding() {
		// Choose the charset encoding
		String[] encoding = encoding = Preferences.getPreference("file",
				"rw-encoding", FILE_ENCODING);
		return encoding[0];
	}

	public static Reader getReaderForFile(File fileName) throws IOException {
		String encoding = getCurrentFileEncoding();
		return getReaderForFile(fileName, encoding);
	}

	public static Reader getReaderForFile(File fileName, String encoding)
			throws IOException {
		Reader rr;
		if (encoding == null || DEFAULT_FILE_ENCODING.equals(encoding))
			rr = new FileReader(fileName);
		else
			rr = new InputStreamReader(new FileInputStream(fileName), encoding);
		return rr;
	}

	public static Reader getReaderForFile(String fileName) throws IOException {
		return getReaderForFile(new File(fileName));
	}

	public static Writer getWriterForFile(File fileName, String encoding)
			throws IOException {
		Writer w = null;
		if (encoding == null || DEFAULT_FILE_ENCODING.equals(encoding))
			w = new FileWriter(fileName);
		else
			w = new OutputStreamWriter(new FileOutputStream(fileName), encoding);
		return w;
	}

	public static Writer getWriterForFile(File fileName) throws IOException {
		String encoding = getCurrentFileEncoding();
		return getWriterForFile(fileName, encoding);
	}

	public static String getEncodedString(byte[] data) throws IOException {
		String encoding = getCurrentFileEncoding();
		if (DEFAULT_FILE_ENCODING.equals(encoding))
			return new String(data);
		else
			return new String(data, encoding);
	}

	public static String getContentFromFileName(String fileName)
			throws Throwable {

		if (fileName.indexOf("://") > -1) {
			// URL case
			URL url = new URL(fileName);
			InputStream input = url.openStream();
			return getContentFromInputStream(input);
		} else {		

			StringBuffer sb = null;
	
			File f = new File(fileName);
			if (f.exists()) {
				Reader rr = null;
	
				String encoding = getCurrentFileEncoding();
	
				if ( ( DEFAULT_FILE_ENCODING.equals(encoding) ) || 
						"AUTOMATIC".equals( encoding )) {
					rr = new FileReader(fileName);
				} else {
					rr = new InputStreamReader(new FileInputStream(fileName),
							encoding);
				}
	
				BufferedReader r = new BufferedReader(rr);
	
				try {
					sb = new StringBuffer();
	
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
			} else 
			;
			if (sb != null)
				return sb.toString();
			else
				return null;
		}
	}

	/** It uses the default file encoding from the preference file/rw-encoding */
	public static String getContentFromInputStream(InputStream input)
			throws Throwable {
		return getContentFromInputStream(input, getCurrentFileEncoding());
	}

	public static String getContentFromInputStream(InputStream input,
			String encoding) throws Throwable {
		StringBuffer sb = new StringBuffer();
		char[] buffer = new char[1024];
		try {
			int c;

			Reader r = null;
			if (DEFAULT_FILE_ENCODING.equals(encoding) || encoding == null || "AUTOMATIC".equals( encoding ))
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

	public static String toURL(File f) throws MalformedURLException {
		return f.toURI().toURL().toString();
	}

	public static boolean removeDirectoryAndContent(File dir) {
		if (dir.isFile()) {
			return dir.delete();
		} else {
			String[] files = dir.list();
			boolean ok = true;
			if (files != null)
				for (int i = 0; i < files.length; i++) {
					File f = new File(dir, files[i]);
					ok = ok && removeDirectoryAndContent(f);
					if (!ok)
						break;
				}
			ok = ok && dir.delete();
			return ok;
		}
	}

	// Resole a template document with ${PARAM}, ${PARAM2}...
	public static String resolve(Map<String,String> ht, String template)
			throws Exception {
		if ( ht == null )
			return template;
		if (template == null )
			return null;
		StringBuffer sb = new StringBuffer();
		StringBuffer sbParam = null;
		boolean paramMode = false;
		char[] buffer = template.toCharArray();
		for (int i = 0; i < buffer.length; i++) {
			char c = buffer[i];
			if (paramMode) {
				if (c == '}') {
					paramMode = false;
					// Resolve the parameter
					String parameter = sbParam.toString();
					String value = (String) ht.get(parameter);
					if (value == null)
						throw new Exception("Unknown parameter " + parameter);
					sb.append(value);
				} else
					sbParam.append(c);
			} else {
				if (c == '{') {
					if (i - 1 >= 0) {
						if (buffer[i - 1] == '$') {
							sb.deleteCharAt(sb.length() - 1);
							sbParam = new StringBuffer();
							paramMode = true;
						} else
							sb.append(c);
					} else
						sb.append(c);
				} else
					sb.append(c);
			}
		}
		return sb.toString();
	}
	
	private static List<String> splitPath( File f ) {
		String tmp = f.toString();
		tmp = tmp.replace( "\\", "/" );
		ArrayList<String> l = new ArrayList<String>();
		Collections.addAll( l, tmp.split( "/" ) );
		return l;
	}

	/** Convert the filePath to a path relativly to the fileRefPath */
	public static String getRelativePath(File filePath, File fileRefPath) {		
		if ( fileRefPath == null )
			return filePath.toString();
		List<String> lFilePath = splitPath( filePath );
		List<String> lFileRefPath = splitPath( fileRefPath );
		try {			
			int i = 0;
			for ( ; i < lFilePath.size(); i++ ) {
				try {
					if ( !lFilePath.get( i ).equals( lFileRefPath.get( i ) ) ) {
						break;
					}
				} catch( ArrayIndexOutOfBoundsException e ) {
					break;
				}
			}
			StringBuffer sbRef = new StringBuffer();
			for ( int j = i + 1; j < lFileRefPath.size(); j++ ) {
				sbRef.append( ".." );
				sbRef.append( System.getProperty( "file.separator" ) );
			}
			for ( int k = i; k < lFilePath.size(); k++ ) {
				if ( k > i )
					sbRef.append( System.getProperty( "file.separator" ) );
				sbRef.append( lFilePath.get( k ) );
			}
			return sbRef.toString();
		} catch( Throwable e ) {
			return filePath.toString();
		}
		
	}

	private static EmptyIcon DEFAULT = null;

	public static Icon getDefaultImage() {
		if (DEFAULT == null)
			DEFAULT = new EmptyIcon();
		return DEFAULT;
	}

	/** Empty icon for alignment on popup */
	public static class EmptyIcon extends Object implements Icon {
		private final int height;

		private final int width;

		public EmptyIcon() {
			height = 16;
			width = 16;
		}

		public EmptyIcon(Dimension size) {
			this.height = size.height;
			this.width = size.width;
		}

		public EmptyIcon(int height, int width) {
			this.height = height;
			this.width = width;
		}

		public int getIconHeight() {
			return height;
		}

		public int getIconWidth() {
			return width;
		}

		public void paintIcon(Component c, Graphics g, int x, int y) {
		}
	}

	public static void main(String[] args) {
		File f = new File("d:\\doc.xml");
		File f2 = new File("C:\\titi\\toto\\personal.dtd");
		System.out.println(
				getRelativePath( f2, f ) );
	}
}
