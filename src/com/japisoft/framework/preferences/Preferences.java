package com.japisoft.framework.preferences;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.StringTokenizer;

import com.japisoft.framework.ApplicationMain;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.toolkit.Logger;

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
public class Preferences {

//@@
	static {
		ApplicationMain.class.getName();
	}
//@@

	public final static int INTEGER = 0;
	public final static int COLOR = 1;
	public final static int FONT = 2;
	public final static int RECTANGLE = 3;
	public final static int CHOICE = 4;
	public final static int CHAR = 5;
	public final static int STRING = 6;
	public final static int BOOLEAN = 7;
	public final static int DOUBLE = 8;

	/** Group for the system preferences. Such preferences are not visible to the user */
	public static String SYSTEM_GP = "system";
	
	public static String PREF_FILENAME = "prefDef.prop";

	static Properties preferences;
	static HashMap valueCache;
	
	static {
		valueCache = new HashMap();
	}

	static void cleanPreferences() {
		preferences = new Properties();
	}

	public static Properties getCurrentPreferences() {
		if ( preferences == null )
			loadPreferences();
		return preferences;
	}

	public static void savePreferences() {
		if ( preferences != null ) {
			File f = ApplicationModel.getAppUserPath();
			File p = new File( f, PREF_FILENAME );
			try {
				OutputStream output = null;
				try {
					preferences.store( output = new FileOutputStream( p ), null );
				} finally {
					try {
						output.close();
					} catch (Throwable th) {
					}
				}
			} catch (IOException exc) {
				exc.printStackTrace();
				System.err.println( "Can't save user preferences" );
			}
		}
	}

	public static void loadPreferences() {
		// Load default preferences from the classpath
		URL u = ClassLoader.getSystemClassLoader().getResource(
					PREF_FILENAME );
		Properties defaultPreferences = null;
		if ( u != null ) {
			try {
				InputStream input = u.openStream();
				try {
					defaultPreferences = new Properties();
					defaultPreferences.load( input );
				} finally {
					input.close();
				}
			} catch( Throwable th ) {
				Logger.addWarning( "Can't load pref.prop : " + th.getMessage() );
			}
		} 
		//else
		// Logger.addWarning( "Can't find the default pref.prop " );

		File f = ApplicationModel.getAppUserPath();
		if ( !f.exists() )
			f.mkdir();
		File p = new File( f, PREF_FILENAME );
		preferences = new Properties();

		if ( p.exists() ) {
			InputStream input = null;
			try {
				try {
					preferences.load( input = new FileInputStream( p ) );
				} finally {
					try {
						input.close();
					} catch (Throwable th) {
						Logger.addWarning( "Can't load user preferences : " + th.getMessage() );
					}
				}

			} catch (IOException exc) {
				Logger.addWarning( "Can't load user preferences : " + exc.getMessage() );
			}
		} else {
			
		}
		
		// Load other default preferences
		if ( defaultPreferences != null ) {			
			for ( Enumeration enume = defaultPreferences.keys(); enume.hasMoreElements(); ) {
				String property = ( String )enume.nextElement();
				if ( !preferences.containsKey( property ) )
					preferences.setProperty( property, defaultPreferences.getProperty( property ) );
				if ( property.startsWith( "-" ) ) {
					//System.out.println( "Remove property" + property );
					preferences.remove( property.substring( 1 ) );
				}							
			}
		}
	}

	public static void removeAllPreferencesForType( int type ) {
		Iterator it = preferences.keySet().iterator();
		String waitFor = ".type";
		ArrayList toRemove = new ArrayList();
		while ( it.hasNext() ) {
			String key = ( String )it.next();
			String value = preferences.getProperty( key );
			if ( key.endsWith( waitFor ) ) {
				if ( value.equals( "" + type ) ) {
					//System.out.println( "remove " + key );
					it.remove();
					int i = key.lastIndexOf( "." );
					String key2 = key.substring( 0, i ) + ".value";
					toRemove.add( key2 );
				}
			}
		}
		for  (int i = 0; i < toRemove.size(); i++ ) {
			preferences.remove( toRemove.get( i ) );
		}
	}

	static void checkPreferencesLoad() {
		if ( preferences == null ) {
			try {
				loadPreferences();
			} catch( Throwable th ) {
				
				th.printStackTrace();
				preferences = new Properties();
			}
		}
	}
	
	static Object getValueByType(int type, String value) {
		try {

			switch ( type ) {
				case INTEGER :
					return new Integer(value);
				case COLOR :
					StringTokenizer st = new StringTokenizer(value, ",");
					return new Color(
						Integer.parseInt( st.nextToken() ),
						Integer.parseInt( st.nextToken() ),
						Integer.parseInt( st.nextToken()) );

				case FONT :
					StringTokenizer st2 = new StringTokenizer(value, ",");
					return new Font(
						st2.nextToken(),
						Integer.parseInt( st2.nextToken() ),
						Integer.parseInt( st2.nextToken()) );
				case RECTANGLE :
					StringTokenizer st3 = new StringTokenizer( value, "," );
					return new Rectangle(
						Integer.parseInt( st3.nextToken() ),
						Integer.parseInt( st3.nextToken() ),
						Integer.parseInt( st3.nextToken() ),
						Integer.parseInt( st3.nextToken() ) );
				case CHOICE : 
					StringTokenizer st4 = new StringTokenizer( value, "," );
					ArrayList l = new ArrayList();
					while ( st4.hasMoreTokens() ) {
						String token = st4.nextToken();
						l.add( token );
					}
					String[] s = new String[ l.size() ];
					for ( int i = 0; i < l.size(); i++ )
						s[ i ] = (String )l.get( i );
					return s;
				case CHAR : 
					if ( value.length() == 0 )
						return new Character( Character.MIN_VALUE );
					else
						return new Character( value.charAt( 0 ) );
				case BOOLEAN :
					return new Boolean( "true".equals( value ) );
				case DOUBLE :
					return new Double( value );
				default : 
					return value;
			}
			
		} catch (NoSuchElementException exc) {
			Logger.addWarning( "Bad value format for property in " + value );
		} catch( NumberFormatException exc ) {
			Logger.addWarning( "Bad value for property, can't retreive number in " + value ); 
		}
		return null;
	}

	static int getTypeFromObject(Object obj) {
		if (obj instanceof Integer)
			return INTEGER;
		else if (obj instanceof Color)
			return COLOR;
		else if (obj instanceof Font)
			return FONT;
		else if ( obj instanceof Rectangle )
			return RECTANGLE;
		else if ( obj instanceof String[] )
			return CHOICE;
		else if ( obj instanceof Character )
			return CHAR;
		else if ( obj instanceof String )
			return STRING;
		else if ( obj instanceof Boolean )
			return BOOLEAN;
		return -1;
	}

	static String getStringValueFromObject(Object obj) {
		if (obj instanceof Integer)
			return "" + ( ( Integer ) obj ).intValue();
		else if (obj instanceof Color) {
			Color _ = ( Color ) obj;
			return _.getRed() + "," + _.getGreen() + "," + _.getBlue();
		} else if ( obj instanceof Font ) {
			Font _ = ( Font ) obj;
			return _.getName() + "," + _.getStyle() + "," + _.getSize();
		} else if ( obj instanceof Rectangle ) {
			Rectangle _ = ( Rectangle )obj;
			return _.x + "," + _.y + "," + _.width + "," + _.height;
		} else if ( obj instanceof String[] ) {
			StringBuffer sb = new StringBuffer();
			String[] ss = ( String[] )obj;
			for ( int i = 0; i < ss.length; i++ ) {
				if ( sb.length() > 0 )
					sb.append( "," );
				sb.append( ss[ i ] );
			}
			return sb.toString();
		} else
		if ( obj instanceof Character ) {
			return obj.toString();
		} else
			if ( obj instanceof String )
				return (String)obj;
			else 
				if(  obj instanceof Boolean )
					return obj.toString();
		return null;
	}

	/** Reset a preference */
	public static void setRawPreference(String group, String name, Object value) {
		int type = getTypeFromObject(value);
		String val = getStringValueFromObject(value);
		String key1 = group + "." + name + ".type";
		String key2 = group + "." + name + ".value";
		if ( preferences == null )
			throw new RuntimeException( "No preferences found" );
		preferences.put(key1, "" + type);
		if ( val== null ) {
			preferences.remove( key2 );
			valueCache.remove( group + "." + name + ".value" );
		}
		else {
			preferences.put(key2, val);
			valueCache.put( group + "." + name + ".value", value );
		}
	}

	public static void removePreference(String group, String name) {
		if ( preferences == null )
			throw new RuntimeException( "No preferences found" );		
		preferences.remove( group + "." + name + ".type" );
		preferences.remove( group + "." + name + ".value" );
	}

	public static void setPreference(String group, String name, boolean value ) {
		setRawPreference( group, name, new Boolean( value ) );
	}

	public static void setPreference(String group, String name, String value ) {
		setRawPreference( group, name, (Object)value );
	}

	public static void setPreference(String group, String name, int value ) {
		setRawPreference( group, name, new Integer( value ) );
	}

	/** @return an int preference */
	public static int getPreference(
		String group,
		String name,
		int defaultValue) {

		String key = group + "." + name + ".value";
		if (valueCache.containsKey(key))
			return ((Integer) valueCache.get(key)).intValue();

		checkPreferencesLoad();		
		
		if (preferences.containsKey(key)) {
			try {
				Integer _;
				valueCache.put(
					key,
					_ =
						(Integer) getValueByType(INTEGER,
							preferences.getProperty(key)));
				return _.intValue();
			} catch (NumberFormatException exc) {
			}
		} else {
			String type = group + "." + name + ".type";
			preferences.setProperty(key, "" + defaultValue);
			preferences.setProperty(type, "" + INTEGER);
		}

		return defaultValue;
	}

	/** @return an int preference */
	public static double getPreference(
		String group,
		String name,
		double defaultValue) {

		String key = group + "." + name + ".value";
		if (valueCache.containsKey(key))
			return ((Double) valueCache.get(key)).doubleValue();

		checkPreferencesLoad();		
		
		if (preferences.containsKey(key)) {
			try {
				Double _;
				valueCache.put(
					key,
					_ =
						(Double) getValueByType(DOUBLE,
							preferences.getProperty(key)));
				return _.doubleValue();
			} catch (NumberFormatException exc) {
			}
		} else {
			String type = group + "." + name + ".type";
			preferences.setProperty(key, "" + defaultValue);
			preferences.setProperty(type, "" + INTEGER);
		}

		return defaultValue;
	}
		
	/** @return a boolean preference */
	public static boolean getPreference(
		String group,
		String name,
		boolean defaultValue) {

		String key = group + "." + name + ".value";
		if ( valueCache.containsKey( key ) )
			return ( ( Boolean )valueCache.get( key ) ).booleanValue();
		
		checkPreferencesLoad();		
		
		if ( preferences.containsKey( key ) ) {
			Boolean _;
			valueCache.put(
				key,
				_ =
					(Boolean) getValueByType(BOOLEAN,
						preferences.getProperty(key)));
			return _.booleanValue();
		} else {
			String type = group + "." + name + ".type";
			preferences.setProperty(key, "" + defaultValue);
			preferences.setProperty(type, "" + BOOLEAN);
		}

		return defaultValue;
	}

	public static Color getPreference(
		String group,
		String name,
		Color defaultValue) {

		String key = group + "." + name + ".value";
		if (valueCache.containsKey(key))
			return ((Color) valueCache.get(key));

		checkPreferencesLoad();		
		
		if (preferences.containsKey(key)) {
			try {
				String _ = preferences.getProperty(key);
				Color __;
				valueCache.put(key, __ = (Color) getValueByType(COLOR, _));
				return __;
			} catch (Throwable exc) {
			}
		} else {
			if ( defaultValue != null ) {
				String type = group + "." + name + ".type";
				preferences.setProperty(
					key,
					getStringValueFromObject(defaultValue));
				preferences.setProperty(type, "" + COLOR);
			}
		}

		return defaultValue;
	}

	public static Font getPreference(
		String group,
		String name,
		Font defaultValue) {

		String key = group + "." + name + ".value";

		if (valueCache.containsKey(key))
			return (Font) valueCache.get(key);

		checkPreferencesLoad();		
		
		if (preferences.containsKey(key)) {
			try {
				String _ = preferences.getProperty(key);
				Font __ = (Font) getValueByType(FONT, _);
				valueCache.put(key, __);
				return __;
			} catch (Throwable exc) {
			}
		} else {
			String type = group + "." + name + ".type";
			if ( defaultValue != null ) {
				preferences.setProperty(
					key,
					getStringValueFromObject(defaultValue));
				preferences.setProperty(type, "" + FONT);
			}
		}
		return defaultValue;
	}

	public static Rectangle getPreference(
		String group,
		String name,
		Rectangle defaultValue) {

		String key = group + "." + name + ".value";

		if (valueCache.containsKey(key))
			return (Rectangle) valueCache.get(key);

		checkPreferencesLoad();		
		
		if (preferences.containsKey(key)) {
			try {
				String _ = preferences.getProperty(key);
				Rectangle __ = (Rectangle) getValueByType(RECTANGLE, _);
				valueCache.put(key, __);
				return __;
			} catch (Throwable exc) {
			}
		} else {
			String type = group + "." + name + ".type";
			if  (defaultValue != null ) {
				preferences.setProperty(
					key,
					getStringValueFromObject(defaultValue));
				preferences.setProperty(type, "" + RECTANGLE );
			}
		}
		return defaultValue;
	}
	
	public static String[] getPreference(
		String group,
		String name,
		String[] defaultValue) {

		String key = group + "." + name + ".value";

		if (valueCache.containsKey(key))
			return (String[]) valueCache.get(key);

		checkPreferencesLoad();		
		
		if (preferences.containsKey(key)) {
			try {
				String _ = preferences.getProperty(key);
				String[] __ = (String[]) getValueByType(CHOICE, _);
				valueCache.put(key, __);
				return __;
			} catch (Throwable exc) {
			}
		} else {
			String type = group + "." + name + ".type";
			if ( defaultValue != null ) {
				preferences.setProperty(
						key,
						getStringValueFromObject(defaultValue));
				preferences.setProperty(type, "" + CHOICE );
			}
		}
		return defaultValue;
	}

	public static char getPreference(
		String group,
		String name,
		char defaultValue) {

		String key = group + "." + name + ".value";

		if (valueCache.containsKey(key))
			return ((Character) valueCache.get(key)).charValue();

		checkPreferencesLoad();		
		
		if (preferences.containsKey(key)) {
			try {
				String _ = preferences.getProperty(key);
				Character __ = (Character) getValueByType(CHAR, _);
				valueCache.put(key, __);
				return __.charValue();
			} catch (Throwable exc) {
			}
		} else {
			String type = group + "." + name + ".type";
			preferences.setProperty(
				key,
				getStringValueFromObject( new Character( defaultValue ) ));
			preferences.setProperty(type, "" + CHAR );
		}
		return defaultValue;
	}

	public static String getPreference(
		String group,
		String name,
		String defaultValue) {

		String key = group + "." + name + ".value";

		if (valueCache.containsKey(key))
			return (String) valueCache.get(key);

		checkPreferencesLoad();		
		
		if (preferences.containsKey(key)) {
			try {
				String _ = preferences.getProperty(key);
				valueCache.put(key, _);
				return _;
			} catch (Throwable exc) {
			}
		} else {
			if ( defaultValue != null ) {
				String type = group + "." + name + ".type";
				preferences.setProperty(
					key,
					defaultValue );
				preferences.setProperty(type, "" + STRING );
			}
		}
		return defaultValue;
	}

}
