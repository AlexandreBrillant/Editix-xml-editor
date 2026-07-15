// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2026 Alexandre Brillant
//
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
// AI Training Restriction :
// This source code is provided for human use only.
// Using this code to train, fine-tune, or develop AI models,
// machine learning systems, or similar technologies is
// STRICTLY PROHIBITED. Violations will terminate all rights
// under the applicable license.

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

	public Class[] scanClasses( ClassLoader parent, String strPattern ) throws Exception {
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
							classes.add( loadClass( parent, className ) );
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

	public Class<?> loadClass( ClassLoader parent, String name ) throws ClassNotFoundException {
		try {
			return parent.loadClass( name );
		} catch( ClassNotFoundException exc ) {
			if ( loader == null ) {
				if ( jars != null ) {
					try {
						URL[] urls = new URL[ jars.size() ];
						for ( int i = 0; i < jars.size(); i++ )
							urls[ i ] = jars.get( i ).toURI().toURL(); 
						loader = new URLClassLoader( urls, parent );
					} catch( Exception exc2 ) {	
					}
				}
				if ( loader != null )
					return loader.loadClass( name );
			}			
		}
		
		throw new ClassNotFoundException( name );
	}

	public static void main(String[] args ) throws Exception {
	}
	
}
