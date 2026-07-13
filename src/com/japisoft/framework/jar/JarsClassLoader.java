package com.japisoft.framework.jar;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JarsClassLoader {
	
	private URLClassLoader loader = null;
		
	private List<File> jars = null;
	
	public void addJar( File jar ) throws Exception {
		if ( jars == null )
			jars = new ArrayList<File>();
		jars.add( jar );
	}

	public Class[] scanClasses( String strPattern ) throws Exception {
		Pattern pattern = Pattern.compile( strPattern );
		if ( jars != null ) {
			List<Class> classes = null;
			for ( File jfile : jars ) {
				JarFile jar = new JarFile( jfile );
				try {
					Enumeration<JarEntry> en = jar.entries();
					while ( en.hasMoreElements() ) {
						JarEntry entry = en.nextElement();
						String name = entry.getName();
						Matcher m = pattern.matcher( name );
						if ( m.find()) {
							if ( classes == null )
								classes = new ArrayList<Class>();
							String className = name.replace( "/", "." ).replace( ".class", "" );
							classes.add( loadClass( className ) );
						}
					}
				} finally {
					jar.close();
				}
			}
			if ( classes == null )
				return new Class[] {};
			return classes.toArray( new Class[ classes.size() ] );
		}
		else
			return new Class[] {};
	}

	public Class<?> loadClass( String name ) throws ClassNotFoundException {
		if ( loader == null ) {
			if ( jars != null ) {
				try {
					URL[] urls = new URL[ jars.size() ];
					for ( int i = 0; i < jars.size(); i++ )
						urls[ i ] = jars.get( i ).toURI().toURL(); 
					loader = new URLClassLoader( urls, Thread.currentThread().getContextClassLoader() );
				} catch( Exception exc ) {
					throw new ClassNotFoundException( exc.getMessage(), exc );
				}
			}
			if ( loader != null )
				return loader.loadClass( name );
		}
		return ClassLoader.getSystemClassLoader().loadClass( name );
	}

	public static void main(String[] args ) throws Exception {
	}
	
}
