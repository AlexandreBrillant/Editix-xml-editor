package com.japisoft.framework.xml.refactor2.elements;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.japisoft.framework.xml.refactor2.AbstractRefactor;

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
public class ConvertAttributeToElement extends AbstractRefactor {

	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {

		String nextName = null;
		String nextValue = null;
		
		if (atts != null && atts.getLength() > 0) {
			
			for ( int i = 0; i < atts.getLength(); i++ ) {
				if ( oldValue.equals( atts.getQName( i ) ) ) {
					nextName = oldValue;
					nextValue = atts.getValue( i );
					break;
				}
			}

			// Delete the good attribute
			atts = new AttributesProxy(atts);
		}

		super.startElement(uri, localName, qName, atts);
		
		if ( nextName != null ) {
			super.startElement( uri, nextName, nextName, null );
			super.characters( nextValue.toCharArray(), 0, nextValue.length() );
			super.endElement( uri, nextName, nextName );
		}

	}

	class AttributesProxy implements Attributes {

		private Attributes ref;

		AttributesProxy(Attributes ref) {
			this.ref = ref;
		}

		public int getIndex(String uri, String localName) {
			return ref.getIndex(uri, localName);
		}

		public int getIndex(String qName) {
			return ref.getIndex(qName);
		}

		public int getLength() {
			return ref.getLength();
		}

		public String getLocalName(int index) {
			String name = ref.getLocalName(index);
			if (oldValue.equals(name))
				return null;
			else
				return name;
		}

		public String getQName(int index) {
			String name = ref.getQName(index);
			if (oldValue.equals(name))
				return null;

			int i = name.indexOf(":");
			if (i > -1) {
				String tmp = name.substring(i + 1);
				if (oldValue.equals(tmp))
					return null;
			}

			return name;
		}

		public String getType(int index) {
			return ref.getType(index);
		}

		public String getType(String uri, String localName) {
			return ref.getType(uri, localName);
		}

		public String getType(String qName) {
			return ref.getType(qName);
		}

		public String getURI(int index) {
			return ref.getURI(index);
		}

		public String getValue(int index) {
			return ref.getValue(index);
		}

		public String getValue(String uri, String localName) {
			return ref.getValue(uri, localName);
		}

		public String getValue(String qName) {
			return ref.getValue(qName);
		}

	}

}
