package com.japisoft.editix.action.fop;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;

import org.apache.fop.apps.FopFactory;

import com.japisoft.editix.main.EditixApplicationModel;

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
public class EditixFOPFactory {
	
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
				m = FopFactory.class.getMethod( "newInstance", URI.class );
			} catch( NoSuchMethodException sme ) {
				throw new Exception( "Can't initialize fop, newInstance missing" );
			}

			// Can't be null
			if ( foLocation == null ) {
				File f = new File( EditixApplicationModel.getAppUserPath(), "fop.xml" );
				foLocation = f.toURL();
			}

			URI uri = new URI( foLocation.toExternalForm().replace( " ", "%20" ) );
			
			return ( ( FopFactory )m.invoke( null,  foLocation == null ? ( URI )null : uri ) );
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
