package com.japisoft.sc;

import java.awt.*;
import java.util.*;
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
class Token {
	private Color color;
	private char[] content;
	private Token endToken;
	private int contentLocation = 0;
	private boolean ignoreCase = false;
	private boolean hasAttributes = false;
	//    private boolean ignoreDelimiter = false;

	// Exclude then end Token if this character is the last one
	private char exclude = 0;

	public Token() {
		super();
	}

	public Token cloneToken() {
		Token t = new Token();
		t.color = color;
		t.content = content;
		t.endToken = endToken;
		t.contentLocation = contentLocation;
		t.ignoreCase = ignoreCase;
		t.hasAttributes = hasAttributes;
		t.collection = collection;
		t.defaultToken = defaultToken;
		t.border = border;
		t.underline = underline;
		t.exclude = exclude;
		return t;
	}

	// Particular case like \" to be ignored as a final token
	public void setExcludeCharacter( char exclude ) {
		this.exclude = exclude;
	}

	public char getExcludeCharacter() {
		return exclude;
	}

	/** Construct a default token */
	public Token(char[] content) {
		setTokenSignature(content);
		setDefaultToken(true);
	}

	/** Build a token that have an end Token delimiter */
	public Token(char[] content, Token endToken) {
		this(content);
		setEndToken(endToken);
	}

	public Token(Token ref, int depth) {
		setTokenSignature(ref.getTokenSignature());
		resetTokenAttributes(ref);
		setEndToken(ref.getEndToken());
		this.contentLocation = depth;
		this.collection = ref.getCollection();
	}

	private String collection = null;

	public void setCollection(String collection) {
		this.collection = collection;
	}

	public String getCollection() {
		return collection;
	}

	public boolean hasCollection() {
		return (collection != null);
	}

	public boolean isOufOfBounds() {
		return contentLocation >= content.length;
	}

	private boolean defaultToken = false;

	/** Set this token as a default one. Only one token type should be a default one */
	public void setDefaultToken(boolean def) {
		this.defaultToken = def;
	}

	/** @return <code>true</code> if this token is a default one */
	public boolean isDefaultToken() {
		return defaultToken;
	}

	/** Non mutable location in the data */
	public int getContentLocation() {
		return contentLocation;
	}

	////////////////////////////////////////////////////////////////////////////

	private boolean ignoreDelimiter;

	public void setIgnoreDelimiter(boolean ignoreDelimiter) {
		this.ignoreDelimiter = ignoreDelimiter;
	}

	public boolean isIgnoreDelimiter() {
		return ignoreDelimiter;
	}

	/** Set the color for this token */
	public void setColor(Color c) {
		this.color = c;
		if (endToken != null)
			endToken.setColor(c);
	}

	/** @return the tied color */
	public Color getColor() {
		return color;
	}

	private boolean underline;

	public void setUnderline(boolean underline) {
		this.underline = underline;
	}

	public boolean isUnderline() {
		return underline;
	}

	private boolean border;

	public void setBorder(boolean border) {
		this.border = border;
	}

	public boolean isBorder() {
		return border;
	}

	/** Extend the Token property */
	public void setProperty(String name, String value) {
		if ("color".equals(name))
			setColor(getColorForName(value));
		else if ("underline".equals(name))
			setUnderline("true".equals(value));
		else if ("border".equals(name))
			setBorder("true".equals(value));
	}

	/** Reset the token attributes with this one */
	public void resetTokenAttributes(Token ref) {
		this.setColor(ref.getColor());
		this.setUnderline(ref.isUnderline());
		this.setBorder(ref.isBorder());
		this.setIgnoreDelimiter(ref.isIgnoreDelimiter());
		setDefaultToken(false);
		hasAttributes = true;
	}

	/** @return <code>true</code> if this token has UI attributes */
	public boolean hasAttributes() {
		return hasAttributes;
	}

	private Color getColorForName(String name) {
		Color c = null;
		if ("white".equals(name)) {
			c = Color.white;
		} else if ("lightGray".equals(name)) {
			c = Color.lightGray;
		} else if ("gray".equals(name)) {
			c = Color.gray;
		} else if ("darkGray".equals(name)) {
			c = Color.darkGray;
		} else if ("black".equals(name)) {
			c = Color.black;
		} else if ("red".equals(name)) {
			c = Color.red;
		} else if ("pink".equals(name)) {
			c = Color.pink;
		} else if ("orange".equals(name)) {
			c = Color.orange;
		} else if ("yellow".equals(name)) {
			c = Color.yellow;
		} else if ("green".equals(name)) {
			c = Color.green;
		} else if ("magenta".equals(name)) {
			c = Color.magenta;
		} else if ("cyan".equals(name)) {
			c = Color.cyan;
		} else if ("blue".equals(name)) {
			c = Color.blue;
		} else {
			try {
				StringTokenizer st = new StringTokenizer(name, ":", false);
				int r = (new Integer(st.nextToken())).intValue();
				int g = (new Integer(st.nextToken())).intValue();
				int b = (new Integer(st.nextToken())).intValue();
				c = new Color(r, g, b);
			} catch (NoSuchElementException e) {
			}
		}
		return c;
	}

	////////////////////////////////////////////////////////////////////////////

	/** Set the data for knowing this is the good one */
	public void setTokenSignature(char[] data) {
		this.content = data;
	}

	/** @return the signature of the token */
	public char[] getTokenSignature() {
		return this.content;
	}

	/** Set the last token part. If no token is specified, the current token
	 * is the whole token. For instance a keyword shouldn't have an end token part,
	 * rather an XML comment like &gt;!-- will have the following &lt;!-- end token */
	public void setEndToken(Token t) {
		this.endToken = t;
		if (t != null)
			t.setColor(getColor());
	}

	public Token getEndToken() {
		return endToken;
	}

	public String toString() {
		return ("tok:" + (new String(content)).substring(contentLocation));
	}
}

// Token ends here
