package com.japisoft.editix.action.search.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.japisoft.framework.xml.XMLFileData;
import com.japisoft.framework.xml.XMLToolkit;
import com.japisoft.framework.xml.parser.FPParser;
import com.japisoft.framework.xml.parser.dom.DocumentImpl;
import com.japisoft.framework.xml.parser.dom.DomNodeFactory;
import com.japisoft.framework.xml.parser.node.FPNode;

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
public class XPathSearchEngineImpl implements SearchEngine {

	public List search( File f, String item ) {
		try {
			String l = null;
			
			/* XPath xp = XPathFactory.newInstance().newXPath();
			XPathExpression xe = xp.compile( item ); */

			ArrayList res = null;

			FPParser p = new FPParser();
			p.setNodeFactory( 
					new DomNodeFactory() 
			);

			XMLFileData xfd = XMLToolkit.getContentFromInputStream( 
					new FileInputStream( f ), 
					null 
			);

			Node nn = new DocumentImpl( (Element)p.parse(new StringReader(xfd.getContent())).getRoot() );
//			com.japisoft.fastparser.document.Document d2 = p.getDocument();
//
//			Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
//			d.adoptNode( ( Element )d2.getRoot() );
//			d.appendChild( ( Element )d2.getRoot() );
						
			
			org.jaxen.XPath xpath = new DOMXPath( item );			

			// NodeList nl = ( NodeList )xe.evaluate( nn , XPathConstants.NODESET );
			
			Object resTmp = xpath.evaluate( nn );
			if ( resTmp instanceof List ) {
				List nl = ( List )resTmp;
				res = new ArrayList();
				for ( int i = 0; i < nl.size(); i++ ) {
					FPNode sn = ( FPNode )nl.get( i );
					String line = xfd.getContent().substring( sn.getStartingOffset(), sn.getStoppingOffset() );
					SearchResult sr = new SearchResultImpl( line, sn.getStartingLine() - 1 );
					res.add( sr );
				}
			}

			return res;
		} catch (Throwable e) {
			return null;
		}

	}

	public String toString() {
		return "XPath expression";
	}

}
