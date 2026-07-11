// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2026 Alexandre Brillant
//
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
// AI Training Restriction :
// This source code is provided for human use only.
// Using this code to train, fine-tune, or develop AI models,
// machine learning systems, or similar technologies is
// STRICTLY PROHIBITED. Violations will terminate all rights
// under the applicable license.

package com.japisoft.xmlpad.editor;

import com.japisoft.xmlpad.XMLDocumentInfo;

/**
 * Template for XML. This is useful for the 'new' operation.
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.1 */
public class XMLTemplate {

	public XMLTemplate() {
		super();
	}

	/** Full XML template here */
	public XMLTemplate( String rawContent ) {
		this();
		setRawContent( rawContent );
	}
	
	private int majorVersion = 1;
	private int minorVersion = 0;

	/** XMLTemplate version */
	public void setVersion(int majorVersion, int minorVersion) {
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;
	}

	private String encoding = "UTF-8";

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	private String dtd = null;

	public void setDtd(String dtd) {
		this.dtd = dtd;
	}

	private String schema = null;

	public void setSchema(String schema) {
		this.schema = schema;
	}

	private String root = null;

	public void setRootNode(String root) {
		this.root = root;
	}

	private String content = null;

	/** End of the template */
	public void setContent(String content) {
		this.content = content;
	}

	private String comment = null;

	/** Initial comment */
	public void setComment(String comment) {
		this.comment = comment;
	}

	private boolean validate = false;

	/** Choose to validate this document */
	public void setValidating(boolean validating) {
		this.validate = validating;
	}

	public boolean isValidating() {
		return validate;
	}

	private String rawContent = null;

	/** Reset the template with only this content */
	public void setRawContent(String content) {
		this.rawContent = content;
	}

	private int cursor = -1;

	/** Particular case when the template contains a 'cursor' param, this is used by the XMLContainer to
	 * force a cursor location
	 * @return -1 for an empty value
	 */
	public int getCursorLocation() { return cursor; }

	/** @return a string format of the current template resolving all parameters ${...} using
	 * the XMLDocumentInfo values
	 * @param info A Document info
	 * @return 
	 */
	public String toString( XMLDocumentInfo info ) {
		String res = toString();
		StringBuffer sbRes = new StringBuffer();
		int i = 0;
		int oldI = 0;
		cursor = -1;
		while ( ( i = res.indexOf( "${", i ) ) != -1 ) {
			// Search for the end of the param
			int e = 0;
			for ( int j = i + 2; j < i + 50; j++ ) {
				if ( res.charAt( j ) == '}' ) {
					e = j;
					break;
				}
			}
			if ( e == 0 ) {
				// Ignore this
				e = i + 50;
				sbRes.append( res.substring( oldI, i ) );
			} else {
				sbRes.append( res.substring( oldI, i ) );
				String param = res.substring( i + 2, e );
				if ( "cursor".equals( param ) )
					cursor = sbRes.length();
				sbRes.append( info.getParamValue( param ) );
				e++;
			}
			oldI = e;
			i = e;
		}

		if ( sbRes.length() > 0 )
			sbRes.append( res.substring( oldI ) );
		else
				return res;
		return sbRes.toString();
	}

	public String toString() {
		String res = null;

		if (rawContent != null) {
			res = rawContent;
		} else {
			StringBuffer sb = new StringBuffer();
			sb
				.append("<?xml version=\"")
				.append(majorVersion)
				.append(".")
				.append(minorVersion)
				.append("\"");
			if (encoding != null)
				sb.append(" encoding=\"").append(encoding).append("\"");
			sb.append("?>").append("\n");
			if ((dtd != null) && (root != null)) {
				sb.append("<!DOCTYPE ").append(root).append(
					" SYSTEM \"").append(
					dtd).append(
					"\">");
				sb.append("\n");
			}
			if (comment != null) {
				sb.append("\n");
				sb.append("<!-- ").append(comment).append("-->");
				sb.append("\n");
			}

			if (root != null) {
				sb.append("<").append(root);
				if (schema != null) {
					sb.append(" ").append(
						"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
					sb.append(" xsi:schemaLocation=\"").append(schema).append(
						"\"");
				}
				sb.append(">\n");
			}

			if (content != null)
				sb.append(content);

			if (root != null) {
				sb.append("</").append(root).append(">");
			}
			res = sb.toString();
		}
		return res;
	}
   
}

