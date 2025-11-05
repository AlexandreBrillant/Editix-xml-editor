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

package com.japisoft.framework.xml.parser.dom;

import org.w3c.dom.*;

/**
 * DOMImplementation
 *
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
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


