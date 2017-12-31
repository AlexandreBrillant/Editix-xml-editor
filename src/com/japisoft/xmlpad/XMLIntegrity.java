package com.japisoft.xmlpad;
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
public class XMLIntegrity {
	private boolean parseBeforeSaving;
	private boolean protectTag;

	/**
	 * @return true if the document is parsed before saving. If the document
	 * is not valid, the save action has no effect. By default false.
	 */
	public boolean isParseBeforeSaving() {
		return parseBeforeSaving;
	}

	/**
	 * Decide to parse the document before saving. If the document is wrong
	 * the saving action is canceled.
	 * @param b
	 */
	public void setParseBeforeSaving(boolean b) {
		parseBeforeSaving = b;
	}

	/**
	 * A protection mean user can't edit it
	 * @return true is a protection is available on tag. */
	public boolean isProtectTag() {
		return protectTag;
	}

	/**
	 * If true, it protects all tag from user insert or remove
	 * @param b true to protect all tag. */
	public void setProtectTag(boolean b) {
		protectTag = b;
	}

}
