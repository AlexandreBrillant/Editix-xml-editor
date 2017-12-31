package com.japisoft.editix.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.xmlpad.XMLContainer;

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
public class PluginManager {

	public static void loadPlugin() {
		URL url = ClassLoader.getSystemClassLoader().getResource("plugin");
		if (url == null) {
			// System.out.println("Can't find plugin directory");
			return;
		}
		String form = url.toExternalForm();

		if (form.startsWith("file://"))
			form = form.substring(7);
		else if (form.startsWith("file:"))
			form = form.substring(5);

		File f = new File(form);
		String[] s = f.list();
		if (s == null)
			return;
		int i = 0;
		while (i < s.length) {
			String _ = s[i++];
			if (_.endsWith(".jar"))
				load(new File(f, _));
		}
	}

	static void storeInBuffer(HashMap buffer, String key, InputStream input) {
		byte[] tempon = new byte[ 1024 ];
		byte[] result = null;
		int c = 0;
		int offset = 0;
		try {
			while ( ( c = input.read( tempon ) ) > 0 ) {
				if (result == null) {
					result = new byte[c];
					System.arraycopy( tempon, 0, result, 0, c );
				}
				else {
					byte[] tmp = new byte[ result.length + c ];
					System.arraycopy( result, 0, tmp, 0, result.length );
					System.arraycopy( tempon, 0, tmp, tmp.length - c, c );
				}
			}
			if (result != null) {
				if ( key.endsWith( ".class" ) )
					key = key.replace( '/', '.' ).substring( 0, key.length() - 6 );
				buffer.put( key, result );
			}
		} catch (IOException exc) {
			exc.printStackTrace();
		}
	}

	static void load( File f ) {
		HashMap classMap = new HashMap();

		try {
			JarInputStream jar = new JarInputStream( new FileInputStream( f ) );
			
			Attributes a = jar.getManifest().getMainAttributes();
			if ( a == null ) {
				System.out.println( "No manifest attributes" );
				return;
			}

			String mainPlugin = a.getValue( "Main-Class" );

			if ( mainPlugin == null ) {
				System.out.println( "Cannot find Main-Class value in MANIFEST.MF" );
				return;
			}

			try {
				JarEntry entry = null;
				while ( ( entry = jar.getNextJarEntry() ) != null ) {
					String name = entry.getName();
					storeInBuffer( classMap, name, jar );
				}
			} finally {
				try {
					jar.close();
				} catch (Throwable th) {
				}
				if ( !classMap.containsKey( mainPlugin ) ) {
					System.out.println( "Cannot find main class " + mainPlugin );
					return;
				}
				addPlugin( mainPlugin, classMap ); 
			}
		} catch (FileNotFoundException exc) {

		} catch (IOException exc) {
			exc.printStackTrace();
		}
	}
		
	static ArrayList pluginList = null;
		
	public static Iterator getPlugins() {
		if ( pluginList == null )
			return null;
		return pluginList.iterator();
	}

	public static void start( int order ) {
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		( ( EditixPlugin )pluginList.get( order ) ).start(
			new PluginContext(
				container.getDocument(),
				container.getEditor().getCaretPosition(),
				container.getDocumentInfo().getCurrentDocumentLocation() ) );
	}

	static void addPlugin( String main, HashMap map ) {
		CustomClassLoader ccm = new CustomClassLoader( map );
		try {
			EditixPlugin plugin = ( EditixPlugin )ccm.loadClass( main ).newInstance();
			plugin.init();
			if ( pluginList == null )
				pluginList = new ArrayList();
			pluginList.add( plugin );			
			System.out.println( "add plugin " + plugin.getName() );
		} catch( Throwable th ) {
			th.printStackTrace();
		}		
	}

	static class CustomClassLoader extends ClassLoader {
		private HashMap map;
		
		public CustomClassLoader( HashMap map ) {
			this.map = map;
		}

		public Class findClass(String name) throws ClassNotFoundException {
			byte[] b = loadClassData(name);
			if ( b == null ) {
				throw new ClassNotFoundException( name );
			}
			return defineClass(name, b, 0, b.length);
		}

		private byte[] loadClassData(String name) {
			byte[] b = ( byte[] )map.get( name );
			return b;
		}

	}

}
