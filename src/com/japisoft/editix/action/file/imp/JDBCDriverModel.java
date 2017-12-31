package com.japisoft.editix.action.file.imp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
public class JDBCDriverModel {

	private List<JDBCDriver> data = null;
	
	public void addDriver( JDBCDriver driver ) {
		if ( data == null )
			data = new ArrayList<JDBCDriver>();
		data.add( driver );
		fireModelUpdated();
	}
	
	public void removeDriver( JDBCDriver driver ) {
		if ( data != null )
			data.remove( driver );
		fireModelUpdated();
	}

	private JDBCDriverModelListener listener = null;
	
	public void setListener( JDBCDriverModelListener listener ) {
		this.listener = listener;
	}

	private void fireModelUpdated() {
		if ( listener != null ) {
			listener.modelUpdated();
		}
	}

	public int size() {
		if ( data == null ) {
			return 0;
		}
		return data.size();
	}

	public JDBCDriver getDriver( int index ) {
		if ( data == null )
			return null;
		return data.get( index );
	}

	public void write( File output ) throws Exception {
		if ( data != null ) {
			if ( data.size() > 0 ) {
				Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
				Element root = doc.createElement( "jdbcdrivers" );
				doc.appendChild( root );
				for ( int i = 0; i < size(); i++ ) {
					root.appendChild( getDriver( i ).toXML( doc ) );
				}
				Transformer t = TransformerFactory.newInstance().newTransformer();
				t.transform( 
					new DOMSource( doc ), 
					new StreamResult( output ) 
				);
			}
		}
	}

	public void read( File input ) throws Exception {
		DocumentBuilder db = 
			DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = db.parse( input );
		Element root = doc.getDocumentElement();
		data = null;
		NodeList nl = root.getChildNodes();
		for ( int i = 0; i < nl.getLength(); i++ ) {
			Node n = nl.item( i );
			if ( n instanceof Element ) {
				addDriver( 
					new JDBCDriver( 
						( Element )n ) 
				);
			}
		}
	}

}
