package com.japisoft.framework;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JFrame;

import com.japisoft.framework.application.descriptor.InterfaceBuilder;
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
public class ApplicationModel {

	/** The application full name */
	public static String LONG_APPNAME = null;
	/** A short application name. This is used for the user home directory name */
	public static String SHORT_APPNAME = null;
	/** A build version */
	public static String BUILD = "010105";
	/** Inner build */
	public static String INNER_BUILD = null;
	/** A major version number */
	public static int MAJOR_VERSION = 1;
	/** Major year */
	public static int MAJOR_YEAR = 2000;
	/** Release Candidate version */
	public static int RELEASE_CANDIDATE = 0;
	/** Service Pack */
	public static int SERVICE_PACK = 0;
	/** A Minor version number */
		public static int MINOR_VERSION = 0;
	/** A Sub minor version number */
	public static int SUBMINOR_VERSION = 0;
	/** A Beta version number */
	public static int BETA_VERSION = 0;
	/** Main contact for email support */
	public static String MAIN_SUPPORT_EMAIL = null;
	/** Registered file name */
	public static String REGISTERED_FILE = null;
	/** URL for reporting a problem */
	public static String REPORTING_URL = null;
	/** URL for companies */
	public static String COMPANY_URL = null;
	/** URL for the product */
	public static String PRODUCT_URL = null;
	/** Main application descriptor */
	public static String USERINTERFACE_FILE = null;
	/** File for generating a documentation */
	public static String AUTODOC_FILE = "autodoc.xsl";
	/** Path (classpath) for an application logo image */
	public static String APP_IMG_PATH = null;
	/** Manual path */
	public static String  DEF_MANUAL_PATH = null;

	/////////// DYNAMIC VALUES ////////////
	
	/** Flag to know if it running under mac os x */
	public static boolean MACOSX_MODE = false;

	/** Debug mode. Bound to the system property 'application.debug' */
	public static boolean DEBUG_MODE = "true".equals( System.getProperty( "application.debug" ) );

	/** Temporary path for storing the generated documentation */
	public static String DEBUG_AUTODOC_FILE = null;

	/** The main application frame */
	public static JFrame MAIN_FRAME = null;
	
	/** The object that builds all the menus, popup and toolbar */
	public static InterfaceBuilder INTERFACE_BUILDER = null;

	/** Particular URL for having sub groups inside the preference */
	public static URL PREFERENCES_SUBMENU = null;
	
	static {
		MACOSX_MODE =  isMacOSXPlatform(); 
	}

	/** Called once for initializing the application state */
	static void init( String applicationDescriptorPath ) throws ApplicationException {
		URL url = ClassLoader.getSystemResource( applicationDescriptorPath );
		if ( url == null )
			throw new ApplicationException( "Cannot find the application descriptor file at " + applicationDescriptorPath );
		Properties prop = new Properties();
		try {
			prop.load( url.openStream() );
			init( prop );
		} catch (IOException e) {
			throw new ApplicationException( "Error while loading the application descriptor file", e );
		}
		
	}

	static void init( Properties desc ) throws ApplicationException {
		/* The application full name */
		LONG_APPNAME = desc.getProperty( "LONG_APPNAME" );
		/* A short application name. This is used for the user home directory name */
		SHORT_APPNAME = desc.getProperty( "SHORT_APPNAME" );
		/* A build version */
		BUILD = desc.getProperty( "BUILD" );
		try {
			/* A major version number */
			MAJOR_VERSION = Integer.parseInt( desc.getProperty( "MAJOR_VERSION", "1" ) );
			/* A Minor version number */
			MINOR_VERSION = Integer.parseInt( desc.getProperty( "MINOR_VERSION", "0" ) );
			/* A Sub minor version number */
			SUBMINOR_VERSION = Integer.parseInt( desc.getProperty( "SUBMINOR_VERSION", "0" ) );
			/* A Beta version number */
			BETA_VERSION = Integer.parseInt( desc.getProperty( "BETA_VERSION", "0" ) );
		} catch( NumberFormatException e ) {
			throw new ApplicationException( "Error inside the version number from the descriptor file " + e.getMessage() );
		}
		/* Main contact for email support */
		MAIN_SUPPORT_EMAIL = desc.getProperty( "MAIN_SUPPORT_EMAIL" );
		/* Registered file name */
		REGISTERED_FILE = desc.getProperty( "REGISTERED_FILE" );
		/* URL for reporting a problem */
		REPORTING_URL = desc.getProperty( "REPORTING_URL" );
		/* Main application descriptor */
		USERINTERFACE_FILE = desc.getProperty( "USERINTERFACE_FILE", "application.xml" );
		/* File for generating a documentation */
		AUTODOC_FILE = desc.getProperty( "AUTODOC_FILE", "autodoc.xsl" );
		
		/* Try to find the application steps */
		for ( int step = 1 ; ; step++ ) {
			String stepClassName = desc.getProperty( "step." + step );
			if ( stepClassName == null )
				break;
			try {
				Class cl = Class.forName( stepClassName );
				addApplicationStep( ( ApplicationStep )cl.newInstance() );
			} catch (ClassNotFoundException e1) {
				throw new ApplicationException( e1 );
			} catch (InstantiationException e1) {
				throw new ApplicationException( e1 );
			} catch (IllegalAccessException e1) {
				throw new ApplicationException( e1 );
			}
		}
	}

	/** For debugging usage */
	static void dumpProperties() {
		System.out.println( "LONG_APPNAME = " + LONG_APPNAME );
		System.out.println( "SHORT_APPNAME = " + SHORT_APPNAME );
		System.out.println( "BUILD = " + BUILD );
		System.out.println( "MAJOR_VERSION = " + MAJOR_VERSION );
		System.out.println( "MINOR_VERSION = " + MINOR_VERSION );
		System.out.println( "SUBMINOR_VERSION = " + SUBMINOR_VERSION );
		System.out.println( "BETA_VERSION = " + BETA_VERSION );
		System.out.println( "MAIN_SUPPORT_EMAIL = " + MAIN_SUPPORT_EMAIL );
		System.out.println( "REGISTERED_FILE = " + REGISTERED_FILE );
		System.out.println( "REPORTING_URL = " + REPORTING_URL );
		System.out.println( "USERINTERFACE_FILE = " + USERINTERFACE_FILE );
		System.out.println( "AUTODOC_FILE = " + AUTODOC_FILE );
	}

	/** Check if a short name is available */
	static void checkApplicationName() {
		if ( SHORT_APPNAME == null )
			throw new RuntimeException( "No application name found. Please reset APPNAME from the ApplicationModel" );
	}

	/** @return the user directory for this application. Useful for storing personal data */
	public static File getAppUserPath() {
		checkApplicationName();
		File home = new File(System.getProperty("user.home"));
		home = new File(home, "." +  SHORT_APPNAME );
		if (!home.exists()) {
			boolean rs = home.mkdirs();
			if (!rs) {
				return null;
			}
		}
		return home;
	}
	
	/** @return a complete application name + version */
	public static String getAppNameVersion() {
		checkApplicationName();
		 return LONG_APPNAME + "-" + getAppVersion(); 
	}

	/** @return a complete application version */
	public static String getAppVersion() {
		return MAJOR_VERSION + "." + MINOR_VERSION + ""
			+ (SUBMINOR_VERSION > 0 ? ("." + SUBMINOR_VERSION) : "")
			+ (BETA_VERSION > 0 ? (" Beta " + BETA_VERSION) : "");
	}

	public static String getAppYear() {
		return MAJOR_YEAR + getReleaseCandidateVersion() + getServicePackVersion() + " [Build " + BUILD + "]";
	}

	private static String getReleaseCandidateVersion() {
		if ( RELEASE_CANDIDATE > 0 )
			return " ( RC " + RELEASE_CANDIDATE + ( BETA_VERSION > 0 ? " Beta" + BETA_VERSION : "" ) + " )";
		else
			return ( BETA_VERSION > 0 ? " Beta " + BETA_VERSION : "" );
	}
	
	private static String getServicePackVersion() {
		if ( SERVICE_PACK > 0 )
			return " ( Service Pack " + SERVICE_PACK + ( BETA_VERSION > 0 ? " Beta" + BETA_VERSION : "" ) + " )";
		else
			return "";
	}	

	/** Console message while starting the application */
	public static void starting() {
		System.out.println( "Starting " + getAppNameVersion() );
	}

	// Available file charset encoding
	static String[] FILE_ENCODING = 
		new String[] {
			"DEFAULT",
			"ASCII",
			"Cp1252",
			"ISO8859_1",
			"UnicodeBig",
			"UnicodeBigUnmarked",
			"UnicodeLittle",
			"UnicodeLittleUnmarked",
			"UTF8",
			"UTF-16" 
	};

	public static String getCurrentFileEncoding() {
		// Choose the charset encoding
		String[] encoding = 
			encoding = Preferences.getPreference(
					"file", "rw-encoding",
					FILE_ENCODING );
		return encoding[ 0 ];
	}


	/** Show this message only in a debug mode */
	public static void debug( String message ) {
		if ( DEBUG_MODE )
			System.out.println( "DEBUG:" + message );
	}

	/** Show this stacktrace only in a debug mode */
	public static void debug( Throwable th ) {
		if ( DEBUG_MODE )
			th.printStackTrace();
	}
	
	private static Hashtable htSharedProperties;

	/** Stored a value available anywhere inside the application parts */
	public static void setSharedProperty( String key, Object value ) {
		if ( htSharedProperties == null )
			htSharedProperties = new Hashtable();
		htSharedProperties.put( key, value );
	}
	
	/** @return a value available anywhere inside the application parts */
	public static Object getSharedProperty( String key ) {
		if ( htSharedProperties == null )
			return null;
		return htSharedProperties.get( key );
	}
	
	private static Vector vApplicationStep;
	
	/** Add a new application step */
	public static void addApplicationStep( ApplicationStep step ) {
		if ( vApplicationStep == null )
			vApplicationStep = new Vector();
		vApplicationStep.add( step );
	}

	/** Add a new application step at this location (starting from 0) */
	public static void addApplicationStep( ApplicationStep step, int index ) {
		if ( vApplicationStep == null )
			vApplicationStep = new Vector();
		vApplicationStep.add( index, step );
	}
	
	/** Remove an application step */
	public static void removeApplicationStep( ApplicationStep step ) {
		if ( vApplicationStep == null )
			throw new RuntimeException( "There're no available application steps. Wrong method usage" );
		vApplicationStep.remove( step );
	}
	
	public static void dispose() {
		vApplicationStep = null;
	}
	
	/** @return the number of application step */
	public static int getApplicationStepCount() {
		if ( vApplicationStep == null )
			return 0;
		return vApplicationStep.size();
	}

	/** @return the following application, the index starts from 0 */
	public static ApplicationStep getApplicationStepAt( int index ) {
		if ( vApplicationStep == null )
			throw new RuntimeException( "There're no available application steps. Wrong method usage" );
		return ( ApplicationStep )vApplicationStep.get( index );
	}
	
	public static boolean isWindowsPlatform() {
		String os = System.getProperty( "os.name" );
		if ( os != null && 
				( os.toLowerCase().indexOf( WIN_ID ) > -1 ) )
			return true;
		else
			return false;
	}

	public static boolean isMacOSXPlatform() {
		String os = System.getProperty( "os.name" );
		if ( os != null && 
				( os.toLowerCase().indexOf( MACOSX_ID ) > -1 ) )
			return true;
		return false;
	}
	
	public static boolean isOtherPlatform() {
		return !isWindowsPlatform() && 
			!isMacOSXPlatform();
	}

	private static final String MACOSX_ID = "mac os x";
	private static final String WIN_ID = "windows";

	public static interface ApplicationModelListener {
		public void fireApplicationData( String key, Object...values );
	}

	private static List<ApplicationModelListener> listeners;
	
	public static void addApplicationModelListener( ApplicationModelListener listener ) {
		if ( listener == null ) {
			throw new RuntimeException( "Illegal listener ?" );
		}
		if ( listeners == null ) {
			listeners = new ArrayList<ApplicationModelListener>();
		}
		if ( !listeners.contains( listener ) )
			listeners.add( listener );
	}

	public static void removeApplicationModelListener( ApplicationModelListener listener ) {
		if ( listeners != null ) {
			listeners.remove( listener );
		}
	}

	public static void fireApplicationValue( String key, Object...values ) {
		if ( listeners != null ) {
			ApplicationModelListener[] snaps = 
				listeners.toArray( new ApplicationModelListener[ listeners.size() ] );
			for ( ApplicationModelListener listener : snaps ) {
				ApplicationModel.debug( 
					"ApplicationEvent [" + key + "] values [" + values + "] to [" + listener + "]" 
				);
				listener.fireApplicationData( key, values );
			}
		}
	}

}
