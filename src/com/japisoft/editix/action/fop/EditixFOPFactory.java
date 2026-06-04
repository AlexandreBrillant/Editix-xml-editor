// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2025 Alexandre Brillant
// 
// For non-commercial usage :
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
// For commercial use or integration into proprietary software :
// A commercial license is required. Visit https://www.editix.com for details.

package com.japisoft.editix.action.fop;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;

import org.apache.fop.apps.FopFactory;

import com.japisoft.editix.main.EditixApplicationModel;

/**
 * For compatibility FOP 2.0 & FOP 1.0
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 */
public class EditixFOPFactory {
	
	public static File fopXML = new File( EditixApplicationModel.getAppUserPath(), "fop.xml" );
	
	private static FopFactory createFoFactory( URL foLocation ) throws Exception {
		Method m = null;
		try {
			m = FopFactory.class.getMethod( "newInstance", null );
			return ( FopFactory )m.invoke( null, null );
		} catch( NoSuchMethodException sme ) {
		}
		if ( m == null ) {
			// 2.0 case
			try {
				if ( fopXML.exists() )
					m = FopFactory.class.getMethod( "newInstance", File.class );
				else 
					m = FopFactory.class.getMethod( "newInstance", URI.class );
			} catch( NoSuchMethodException sme ) {
				throw new Exception( "Can't initialize fop, newInstance missing" );
			}
			
			// Can't be null
			if ( foLocation == null ) {
				File f = fopXML;
				foLocation = f.toURL();				
			}

			return ( ( FopFactory )m.invoke( null,  fopXML.exists() ? fopXML : foLocation.toURI() ) );		
		}
		
		throw new Exception( "Can't initialize fop, newInstance missing" );
	}

	public static FopFactory newInstance( URL foLocation ) throws Exception {
		FopFactory fopfactory = createFoFactory( foLocation );
		if ( fopfactory != null ) {
			try {
				// For 1.0
				Method m2 = FopFactory.class.getMethod( "setBaseURL", String.class );
				if ( m2 != null ) {			
					// Relative access
					try {
						String strFoLocation = null;
						if ( foLocation != null )
							strFoLocation = foLocation.toExternalForm();
						m2.invoke( fopfactory, strFoLocation.replace( " ", "%20" ) );
					} catch( Exception exc ) {
					}
				}
			} catch( NoSuchMethodException sme ) {
			}
			return fopfactory;
		} else 
			throw new Exception( "Can't initialize FOP, can't create fopfactory" );		
	}

	public static void main( String[] args ) throws Exception {
		System.out.println( 
			EditixFOPFactory.newInstance( 
				new URL( "file://C:\\Program Files (x86)\\editix-xmleditor2016\\bin" ) ) 
		);
	}

}

