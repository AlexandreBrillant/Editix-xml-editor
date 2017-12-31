package com.japisoft.framework.xml.parser.tools;

import org.xml.sax.*;
import javax.xml.parsers.*;

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
