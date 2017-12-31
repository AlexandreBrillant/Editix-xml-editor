package com.japisoft.framework.xml.parser.dom;

import org.w3c.dom.*;

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
public class DOMImplementationImpl implements DOMImplementation {
	public DOMImplementationImpl() {
		super();
	}

	/**
	 * Test if the DOM implementation implements a specific feature.
	 * @param feature The package name of the feature to test. In Level 1, the 
	 *   legal values are "HTML" and "XML" (case-insensitive).
	 * @param version This is the version number of the package name to test.  
	 *   In Level 1, this is the string "1.0". If the version is not specified, 
	 *   supporting any version of the  feature will cause the method to return 
	 *   <code>true</code>. 
	 * @return <code>true</code> if the feature is implemented in the specified 
	 *   version, <code>false</code> otherwise.
	 */
	public boolean hasFeature(String feature, String version) {
		return false;
	}

	public DocumentType createDocumentType(
		String qualifiedName,
		String publicId,
		String systemId)
		throws DOMException {
		throw new DOMExceptionImpl(
			DOMException.NOT_SUPPORTED_ERR,
			"Not supported");
	}

	public Document createDocument(
		String namespaceURI,
		String qualifiedName,
		DocumentType doctype)
		throws DOMException {
		return new DocumentImpl( (Element)null );
	}

	public Object getFeature(String feature, String version) {
		// TODO Auto-generated method stub
		return null;
	}

}

// DOMImplementationImpl ends here
