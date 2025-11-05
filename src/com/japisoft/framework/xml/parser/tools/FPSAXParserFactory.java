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

package com.japisoft.framework.xml.parser.tools;

import org.xml.sax.*;
import javax.xml.parsers.*;

/**
 * SAX Parser Factory for JAXP 1.0
 *
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.1
 * @since 1.0
 * @see SAXParserFactory */
public class FPSAXParserFactory extends SAXParserFactory {
	public FPSAXParserFactory() {
		super();
	}

	public boolean getFeature(String name)
		throws
			ParserConfigurationException,
			SAXNotRecognizedException,
			SAXNotSupportedException {
		if ("http://xml.org/sax/features/namespaces".equals(name))
			return true;
		if ("http://xml.org/sax/features/namespace-prefixes".equals(name))
			return true;

		return false;
	}

	public void setFeature(String name, boolean value)
		throws
			ParserConfigurationException,
			SAXNotRecognizedException,
			SAXNotSupportedException {
		throw new SAXNotSupportedException("Unknown feature " + name);
	}

	public static SAXParserFactory newInstance() {
		return new FPSAXParserFactory();
	}

	public SAXParser newSAXParser()
		throws ParserConfigurationException, SAXException {
		return new CustomSAXParser();
	}

	///////////////////////////////////////

	public class CustomSAXParser extends SAXParser {
		public CustomSAXParser() {
			super();
		}

		public Parser getParser() {
			return new com.japisoft.framework.xml.parser.sax.SaxParser();
		}

		public XMLReader getXMLReader() {
			return new com.japisoft.framework.xml.parser.sax.Sax2Parser();
		}

		public Object getProperty(String name)
			throws SAXNotRecognizedException, SAXNotSupportedException {
			return null;
		}

		public boolean isNamespaceAware() {
			return true;
		}

		public boolean isValidating() {
			return false;
		}

		public void setProperty(String name, Object value)
			throws SAXNotRecognizedException, SAXNotSupportedException {
			throw new SAXNotSupportedException("Not support for " + name);
		}
	}

}

