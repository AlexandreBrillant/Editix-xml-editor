package com.japisoft.editix.main.steps;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXSource;

import org.apache.xml.resolver.Catalog;
import org.apache.xml.resolver.CatalogManager;
import org.apache.xml.resolver.tools.CatalogResolver;
import org.xml.sax.InputSource;

import com.japisoft.editix.action.xml.XMLCatalogAction;
import com.japisoft.xmlpad.DTDMapperUsage;

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
public class EditixEntityResolver extends CatalogResolver {

	CatalogManager manager;

	public EditixEntityResolver( CatalogManager manager) {
		super( manager );
		this.manager = manager;
		loadCatalogs();
		// manager.setCatalogFiles(fileList)
	}

	static EditixEntityResolver RESOLVER = null;

	public static EditixEntityResolver getInstance() {
		if ( RESOLVER == null ) {
			
			System.setProperty( "xml.catalog.ignoreMissing", "true" );
			// System.setProperty( "xml.catalog.verbosity", "100" );
			
			CatalogManager cm = new CatalogManager();

			RESOLVER = new EditixEntityResolver( 
					cm );

			// CatalogManager.getStaticManager().setRelativeCatalogs( false );
		}
		return RESOLVER;
	}

	public void loadCatalogs() {
		File catalog = XMLCatalogAction.getCatalogLstPath();
		if ( catalog.exists() ) {
			try {
				BufferedReader reader = new BufferedReader( 
						new FileReader( catalog ) );
				try {
					String line = null;
					StringBuffer sb = new StringBuffer();
					while ( ( line = reader.readLine() ) != null ) {
						if ( sb.length() > 0 )
							sb.append( ";" );
						sb.append( line );
					}
/*					if ( "true".equals( System.getProperty( "editix.debug" ) ) )
						System.out.println( "RESET CATALOG FILES = " + sb.toString() );
					System.setProperty( "xml.catalog.catalogs", sb.toString() ); */
					manager.setCatalogFiles( sb.toString() );
					manager.setUseStaticCatalog( true );
					System.setProperty( "xml.catalog.files", sb.toString() );
					manager.getCatalog().loadSystemCatalogs();
					// manager.getCatalog().resolvePublic( null, );
					
				} finally {
					reader.close();
				}

			} catch( IOException exc ) {}
		}		
		// try {
		//	manager.getCatalog().loadSystemCatalogs();
		// } catch( IOException exc ) {
		//	exc.printStackTrace();
		// }
	}

	public Catalog getCatalog() {
		Catalog c = super.getCatalog();
		
		File catalog = 
			XMLCatalogAction.getCatalogLstPath();
		if ( catalog.exists() ) {
			
			try {
				BufferedReader reader = new BufferedReader( 
						new FileReader( catalog ) );
				try {
					String line = null;
					while ( ( line = reader.readLine() ) != null ) {
						if ( line.contains( "://" ) ) {
							try {
								c.parseCatalog( 
									new URL( line ) );
							} catch (Exception e) {
								System.out.println( "Can't load " + line + " => " + e.getMessage() );
							}
						} else {
							c.parseCatalog( line );
						}
					}					
				} finally {
					reader.close();
				}
			} catch (FileNotFoundException e) {
			} catch (MalformedURLException e) {
			} catch (IOException e) {
			}
			
		}

		return c;
	}	

	public String resolveCatalog( String publicId, String systemId ) throws IOException {
		if ( manager == null )
			return null;
		if ( publicId == null )
			publicId = "";		
		return manager.getCatalog().resolvePublic( publicId, systemId );
	}

	public InputSource resolveEntity( String publicId, String systemId ) {

		InputSource source = 
			super.resolveEntity( publicId, systemId );

		String realPath = null;
		
		try {
			if ( publicId != null ) {
				realPath = manager.getCatalog().resolvePublic( publicId, systemId );
			} else {							
				realPath = manager.getCatalog().resolveSystem( systemId );
			}
		} catch (MalformedURLException e) {
		} catch (IOException e) {
		}

		if ( realPath != null ) {
			try {
				systemId = URLDecoder.decode( realPath, "UTF-8" );
			} catch (UnsupportedEncodingException e) {
				systemId = realPath;
			}
		}

		try {
			InputStream input = 
				EditixDTDMapper.getInstance().getStream( systemId );
			if ( input != null )
				return new CustomInputSource( systemId, input );
		} catch (IOException e) {
			
		}

		return source;
	}

	public Source resolve( String href, String base ) throws TransformerException {
		Source source = super.resolve( href, base );
		if ( source != null )
			return source;
		try {
			if ( href != null ) {
				InputStream input = EditixDTDMapper.getInstance().getStream( href );
				if ( input != null )
					return new SAXSource( new InputSource( input ) );
			}
		} catch( IOException exc ) {}		
		return source;
	}	

	// ---------------------------------------------------------------------------------

	class CustomInputSource extends InputSource implements DTDMapperUsage {
		public CustomInputSource( String systemId, InputStream input ) {
			super( input );
			if ( systemId != null )
				setSystemId(systemId);
		}
	}
	
}
