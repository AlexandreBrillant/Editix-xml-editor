package com.japisoft.dtdparser.document;

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
public class TraceDTDDocumentBuilder extends AbstractDTDDocumentBuilder {
	public TraceDTDDocumentBuilder() {
		super();
	}

	/** Start the DTD definition */
	public void notifyStartDTD() {
		System.out.println("* start the DTD");
	}

	/** Stop the DTD definition */
	public void notifyStopDTD() {
		System.out.println("* stop the DTD");
	}

	public void notifyComment(String comment) {
		System.out.println("* comment :" + comment);
	}

	public void notifyEntity(String entity, boolean reference, int type,
			String value) {
		System.out.println("* entity : [" + entity + "] = [" + value + "]");
	}

	public void notifyStartElement(String e) {
		System.out.println("* start element [" + e + "]");
	}

	public void notifyStopElement() {
		System.out.println("* stop element");
	}

	/** Item equals element name or #PCDATA */
	public void notifyElementChoiceItem(String item) {
		System.out.println("* item choice element [" + item + "]");
	}

	/** Item equals element name or EMPTY or ANY or #PCDATA */
	public void notifyElementIncludeItem(String item) {
		System.out.println("* item include element [" + item + "]");
	}

	/** Notify '(' meet for the element declaration */
	public void notifyStartElementChildren() {
		System.out.println("* notify start element children");
	}

	/** Notify operator '+' or '*' or '?' */
	public void notifyOperator(char operator) {
		System.out.println("* notify operator : " + operator);
	}

	/** Notify ')' meet for the element declaration */
	public void notifyStopElementChildren() {
		System.out.println("* notify stop element children");
	}

	/**
	 * @param element
	 *            Element tag
	 * @param id
	 *            Attribute id
	 * @param valueType
	 *            ID, IDREF, ENTITY, ENTITIES, NMTOKEN, NMTOKENS or CDATA
	 * @param enum
	 *            a <code>String[]</code> value
	 * @param attDec
	 *            REQUIRED, IMPLIED or FIXED
	 * @param def
	 *            a <code>String</code> value or ""
	 */
	public void notifyAttribute(String element, String id, int valueType,
			String[] enume, int attDec, String def) {
		System.out.println(" notify attribute [" + id + "] for [" + element
				+ "] /" + valueType + "/" + enume.length + "/" + attDec + " ["
				+ def + "]");
	}

	public String getFirstElement() {
		return null;
	}

}

// DTDDocumentBuilder ends here

