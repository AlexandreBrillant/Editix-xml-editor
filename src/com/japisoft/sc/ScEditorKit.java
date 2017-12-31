package com.japisoft.sc;

import java.io.*;
import javax.swing.*;
import javax.swing.text.*;
import java.util.*;
import java.net.*;
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
public class ScEditorKit extends DefaultEditorKit {
	private SyntaxLexer sl;

	/** The maximum characters by line */
	public static int MAXCHARBYLINE = 300;

	public ScEditorKit() {
		super();
		sl = new SyntaxLexer();
		vf.setSyntaxLexer(sl);
	}

	/** Read the property file describing the syntax attributes */
	public ScEditorKit(String propertyFile) throws FileNotFoundException {
		this();
		readSyntaxColorDescriptor(propertyFile);
	}

	/** Use this property file describing the syntax attributes */
	public ScEditorKit(Properties propertyFile) {
		this();
		setSyntaxColorDescriptor(propertyFile);
	}

//@@
	static {
		System.out.println("JSyntaxColor v1.2.9 evaluation version");
		System.out.println("(c) 2002-2005 JAPISoft");
		System.out.println("http://www.japisoft.com");
	}
//@@

	/** Build a default <code>PlainDocument</code> */
	public Document createDefaultDocument() {
		return new PlainDocument();
	}

	SyntaxFileMapper fm;

	/**
	 * @return a tool for making a relation between a syntax descriptor and a
	 *         loaded file extension
	 */
	public SyntaxFileMapper getSyntaxFileMapper() {
		if (fm == null)
			fm = new SyntaxFileMapper(this);
		return fm;
	}

	/**
	 * Read a syntax color table. This property file url contains all data for
	 * parsing the document and showing syntax color.
	 * 
	 * @param url
	 *            Location for the property file syntax descriptor
	 * @exception FileNotFoundException
	 *                if the property file can't be read
	 */
	public void readSyntaxColorDescriptor(URL url) throws FileNotFoundException {
		parseProperties(readPropertiesFromURL(url));
	}

	private Properties readPropertiesFromURL(URL url)
			throws FileNotFoundException {
		Properties p = null;
		try {
			p = new Properties();
			InputStream input = url.openStream();
			try {
				p.load(input);
			} finally {
				input.close();
			}
		} catch (IOException e) {
			throw new FileNotFoundException(e.getMessage());
		}
		return p;
	}

	/**
	 * Read a syntax color table. This property file contains all data for
	 * parsing the document and showing syntax color. This file is read with two
	 * methods, by an URL or by the classLoader and if this file is not found by
	 * a full path.
	 * 
	 * @exception FileNotFoundException
	 *                will be thrown for unknown file
	 * @exception RuntimeException
	 *                will be thrown for invalid format
	 */
	public void readSyntaxColorDescriptor(String propertyFile)
			throws FileNotFoundException {

		URL u = null;

		if (propertyFile.indexOf("://") > -1) {
			try {
				u = new URL(propertyFile);
			} catch (Throwable th) {
			}
		} else {
			try {
				ClassLoader s = ClassLoader.getSystemClassLoader();
				u = s.getResource(propertyFile);
			} catch (Throwable th) {
			}
		}

		Properties p = null;

		if (u != null) {
			p = readPropertiesFromURL(u);
		}
		if (p == null) {
			File f = new File(propertyFile);
			p = new Properties();
			try {
				FileInputStream input = new FileInputStream(f);
				try {
					p.load(input);
				} finally {
					input.close();
				}
			} catch (IOException e) {
				throw new FileNotFoundException(e.getMessage());
			}
		}
		parseProperties(p);
	}

	/** Reset the syntax color descriptor */
	public void setSyntaxColorDescriptor(Properties p) {
		parseProperties(p);
	}

	/** Add a new token with the item value */
	private Token addToken(Hashtable ht, String token, String item) {
		Vector v = (Vector) ht.get(token);
		if (v == null) {
			v = new Vector();
			ht.put(token, v);
		}
		Token t = null;

		boolean addIt = true;

		int i = item.indexOf("[[");
		if (i > -1) {
			int j = item.indexOf(']', i + 2);
			if (j > -1) {
				int k = item.indexOf(']', j + 1);
				if (k > -1) {
					try {
						String subToken = item.substring(i + 2, j);
						String number = item.substring(j + 1, k);
						Integer integer = new Integer(number);
						int howMany = integer.intValue();

						String first = "";
						if (i > 0)
							first = item.substring(0, i);
						String last = "";
						if (k < item.length() - 1)
							last = item.substring(k + 1);

						for (int z1 = 0; z1 < howMany; z1++) {
							for (int z2 = 0; z2 < subToken.length(); z2++) {
								String tmp = "" + subToken.charAt(z2);
								for (int z3 = 0; z3 < z1; z3++)
									tmp += subToken.charAt(z2);
								String content = first + tmp + last;
								t = new Token(content.toCharArray());
								v.addElement(t);
							}
						}

					} catch (Throwable th) {
					}
				}
			}
		}

		if (addIt) {
			item = resetItemWithMacro( item );
			v.addElement(t = new Token(item.toCharArray()));
		}
		return t;
	}

	private String getValueForMacroDef( String value ) {
		try {
			int i = Integer.parseInt( value );
			return Character.toString( (char)i );
		} catch( NumberFormatException exc ) {
			// Test for a macro
			if ( value.equals( ScPredefinedToken.DD_TOKEN ) )
				return ":";
		}
		return value;
	}

	private String resetItemWithMacro( String item ) {
		StringBuffer sb = new StringBuffer();
		int i = 0;
		while ( i < item.length() ) {
			char c = item.charAt( i );
			if ( c == '$' ) {
				if ( i + 1 < item.length() ) {
					char c2 = item.charAt( i + 1 );
					if ( c2 == '{' ) {

						StringBuffer sbMacroValue = new StringBuffer();
						boolean processed = false;
						int processNext = 0;

						for ( int j = ( i + 2 ); j < item.length(); j++ ) {
							char c3 = item.charAt( j );
							if ( c3 == '}' ) {
								sb.append( getValueForMacroDef( sbMacroValue.toString() ) );
								processed = true;
								processNext = j;
								break;
							} else
								sbMacroValue.append( c3 );
						}

						if ( processed ) {
							i = processNext;
						} else {
							sb.append( c );
						}
						
					} else
						sb.append( c );
				} else
					sb.append( c );
			} else
				sb.append( c );
			i++;
		}
		return sb.toString();
	}

	/** Add a new token with the item1 starting block and the item2 ending block */
	private Token addToken(Hashtable ht, String token, String item1,
			String item2) {
		Vector v = (Vector) ht.get(token);
		if (v == null) {
			v = new Vector();
			ht.put(token, v);
		}
		Token t = new Token(item1.toCharArray());
		Token endToken = new Token(item2.toCharArray());

		if (item2.endsWith("}}")) {
			int i = item2.indexOf("{{");
			if (i > -1) {
				try {
					String chrStr = item2.substring(i + 2, item2.length() - 2);

					endToken = new Token(item2.substring(0, i).toCharArray());
					endToken.setExcludeCharacter(chrStr.charAt(0));

				} catch (Throwable th) {
					System.err.println("Invalid token " + token);
				}
			}
		}

		t.setEndToken(endToken);
		v.add(t);
		return t;
	}

	private String getRealToken(String item) {
		if (ScPredefinedToken.DD_TOKEN.equals(item))
			return ":";
		else if (ScPredefinedToken.RC_TOKEN.equals(item))
			return "\n";
		return item;
	}

	private Token processBoundedValue(Hashtable ht, String name, String item) {

		String subItem = item.substring(item.indexOf("[") + 1, item
				.lastIndexOf("]"));
		int i = subItem.indexOf(";");
		if (i > -1) {
			return addToken(ht, name, subItem.substring(0, i),
					getRealToken(subItem.substring(i + 1)));
		} else {
			return addToken(ht, name, getRealToken(item));
		}

	}

	private void parseTokenDescriptor(boolean ignoreDelimiter, Hashtable ht,
			String name, String values) {
		if (ScPredefinedToken.getPredefinedTokenValues(values) != null)
			values = ScPredefinedToken.getPredefinedTokenValues(values);

		Token t = null;

		if (values.startsWith("[") && values.endsWith("]")) {
			t = processBoundedValue(ht, name, values);
			t.setIgnoreDelimiter(ignoreDelimiter);
			if (t.getEndToken() != null)
				t.getEndToken().setIgnoreDelimiter(ignoreDelimiter);
		} else {

			StringTokenizer st = new StringTokenizer(values, ":", false);

			while (st.hasMoreTokens()) {
				String item = st.nextToken();
				item = getRealToken(item);

				if (item.startsWith("[") && item.endsWith("]")) {
					t = processBoundedValue(ht, name, item);
				} else if (item.startsWith("(") && item.endsWith(")")) {
					// Collection case
					String subItem = item.substring(item.indexOf("(") + 1, item
							.lastIndexOf(")"));
					StringTokenizer _st = new StringTokenizer(subItem, ";");
					while (_st.hasMoreTokens()) {
						String str = getRealToken(_st.nextToken());
						Token _t = addToken(ht, name, str);
						_t.setCollection(name);
					}
				} else {
					t = addToken(ht, name, item);
				}
				if (t != null) {
					t.setIgnoreDelimiter(ignoreDelimiter);
					if (t.getEndToken() != null)
						t.getEndToken().setIgnoreDelimiter(ignoreDelimiter);
				}
			}

		}
	}

	private void parseTokenColorDescriptor(Hashtable ht, String name,
			String property, String values) {
		Vector v = (Vector) ht.get(name);
		if (v == null) {
			throw new RuntimeException("Unknown token " + name
					+ " for color description");
		}
		for (int i = 0; i < v.size(); i++) {
			Token t = (Token) v.get(i);
			t.setProperty(property, values);
		}
	}

	// Read syntax Color descriptor
	private void parseProperties(Properties p) {
		sl = new SyntaxLexer();
		vf.setSyntaxLexer(sl);

		Hashtable ht = new Hashtable();
		// Order the token descriptor read firstly each token°

		if (p.containsKey("tokenMatchIni")) {
			String v = p.getProperty("tokenMatchIni");
			char[] ini = new char[255];
			for (int i = 0; i < ini.length; i++)
				ini[i] = Character.MAX_VALUE;
			for (int i = 0; i < v.length(); i++) {
				char c = v.charAt(i);
				if (c < 255)
					ini[c] = c;
			}
			sl.setSyntaxStart(ini);
		}

		if (p.containsKey("tokenMatchEnd")) {
			String v = p.getProperty("tokenMatchEnd");
			char[] end = new char[255];
			for (int i = 0; i < end.length; i++)
				end[i] = Character.MAX_VALUE;
			for (int i = 0; i < v.length(); i++) {
				char c = v.charAt(i);
				if (c < 255)
					end[c] = c;
			}
			sl.setSyntaxStop(end);
		}

		for (Enumeration e = p.keys(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			if (key.startsWith("token_")) {
				String name = key.substring(6);
				String values = p.getProperty(key);
				parseTokenDescriptor("true".equals(p.getProperty(
						"ignoreTokenMatch_" + name, "false")), ht, name, values);
			}
		}

		for (Enumeration e = p.keys(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			if (!key.startsWith("token_")) {
				int i = key.indexOf("_");
				if (i > -1) {
					String property = key.substring(0, i);
					String name = key.substring(i + 1);
					String values = p.getProperty(key);
					parseTokenColorDescriptor(ht, name, property, values);
				}
			}
		}

		// Add tokens
		Vector v2 = new Vector();

		for (Enumeration enu = ht.elements(); enu.hasMoreElements();) {
			Vector v = (Vector) enu.nextElement();
			for (int i = 0; i < v.size(); i++) {
				Token t = (Token) v.get(i);
				v2.addElement(t);
			}
		}

		Token[] tks = new Token[v2.size()];
		for (int i = 0; i < v2.size(); i++)
			tks[i] = (Token) v2.get(i);

		boolean ic = "true".equals(p.getProperty("ignoreCase"));
		sl.setState(ic, tks);

	}

	ScViewFactory vf = new ScViewFactory();

	public ViewFactory getViewFactory() {
		return vf;
	}

	public static void main(String[] args) throws Throwable {
		Properties p = new Properties();
		p.load(new FileInputStream("/home/japisoft/java_syntax.prop"));
		final ScEditorKit sc = new ScEditorKit();
		sc.setSyntaxColorDescriptor(p);

		/*
		 * File ff = new File(
		 * "/home/japisoft/japisoft/japisoft/japisoft/test/sc/REPORT_DATA.sql" );
		 * FileInputStream in = new FileInputStream( ff ); byte[] b = new byte[
		 * (int)ff.length() ]; in.read( b ); in.close();
		 */

		JFrame f = new JFrame();

		JEditorPane ep = new JEditorPane() {
			public EditorKit getEditorKitForContentType(String type) {
				if ("content/unknown".equals(type))
					return sc;
				else
					return super.getEditorKitForContentType(type);
			}
		};

		ep.setEditorKit(sc);
		ep.setPage("file:///home/japisoft/syntax_bug.bsh");

		//ep.setText( new String( b ) );
		f.getContentPane().add(new JScrollPane(ep));
		f.setSize(400, 400);
		f.setVisible(true);
	}
}
