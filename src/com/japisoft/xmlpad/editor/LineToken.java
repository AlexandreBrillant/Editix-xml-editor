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

import java.awt.Color;

import com.japisoft.xmlpad.SharedProperties;

/**
 * Line part from the parsing
 * 
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.1
 */
public final class LineToken {

	static final LineToken BLANKELEMENT = new LineToken(" ", LineToken.TEXT);
	static final LineToken TAG_MARKER = new LineToken( LineToken.TAG );
	
	public static final int ENTITY = 0;

	public static final int COMMENT = 1;

	public static final int DECLARATION = 2;

	public static final int DOCTYPE = 3;

	public static final int LITERAL = 4;

	public static final int TAG = 5;

	public static final int INVALID = 6;

	public static final int TEXT = 7;

	public static final int ATTRIBUTE = 8;

	
	
	public static final int TAG_DELIMITER_START = 9; // <

	public static final int ATTRIBUTE_SEPARATOR = 10;

	public static final int TAG_ENDER = 11;

	public static final int NAMESPACE = 12;

	public static final int TAG_UNDERLINE = 13;

	public static final int CDATA = 14;

	public static final int DTD_ELEMENT = 15;

	public static final int DTD_ATTRIBUTE = 16;

	public static final int DTD_ENTITY = 17;

	public static final int LITERAL2 = 18;

	public static final int DTD_NOTATION = 19;

	public static final int TAG_DELIMITER_END = 20; // >

	public static final int TAG_BACKGROUND = 21; // For commodity only

	public static final int DEC_BACKGROUND = 22; // For commodity only

	public static final int ENTITY_BACKGROUND = 23; // For commodity only

	public static final int COMMENT_BACKGROUND = 24; // For commodity only

	public static final int COMMENT_START = 25;

	public static final int COMMENT_END = 26;

	public static final int CDATA_START = 27;

	public static final int CDATA_END = 28;

	public static final int CDATAORCOMMENTORDOCTYPE = 29; // <!

	public static final int DOCTYPE_START = 30;

	public static final int DOCTYPE_END = 31;

	public static final int CDATA_BACKGROUND = 32; // For commodity only

	public static final int DOCTYPE_BACKGROUND = 33; // For commodity only

	public static final int LINE_SELECTION = 34; // Selected line

	public static final int LINE_ERROR = 35; // Error line

	public static final int DECLARATION_START = 36; // <?

	public static final int DECLARATION_END = 37; // ?>

	public static final int INNER_DTD = 38; // <!DOCTYPE root [ ]

	public static final int DTD_ELEMENT_CONTENT = 39;

	public static final int DTD_ATTRIBUTE_CONTENT = 40;

	public static final int DTD_ENTITY_CONTENT = 41;	
	
	public static final int DTD_ENTITY_PARAMETER = 42;	// %...;
	
	public static final int DTD_INNER_COMMENT = 43;	// -- ... --

	public static final int SPACE = 44;
	
	public static final int ATTRIBUTE_VALUE = 45;
	
	public String content;

	public int type;

	public int previousType;

	public int nextType;

	public int majorLineElement = 0;

	public float y;
	
	public int offset;
	
	public LineToken(String content, int type) {
		this.content = content;
		this.type = type;
	}

	public LineToken(char[] chars, int start, int end, int type ) {
		this( new String( chars, start, end - start ), type );
	}

	public LineToken(int type) {
		this(null, type);
	}

	boolean hasContent() {
		return content != null;
	}

	public String toString() {
		return "{" + content + ",type:" + typeToLabel( type ) + "}";
	}

	private String typeToLabel( int type ) {
		switch( type ) {
			case LineToken.TEXT:
				return "TEXT";
			case LineToken.DECLARATION:
				return "DECLARATION";
			case LineToken.DOCTYPE:
				return "DOCTYPE";
			case LineToken.DOCTYPE_START:
				return "DOCTYPE_START";
			case LineToken.DOCTYPE_END:
				return "DOCTYPE_END";
			case LineToken.DOCTYPE_BACKGROUND:
				return "DOCTYPE_BACKGROUND";
			case LineToken.LITERAL:
				return "LITERAL";
			case LineToken.TAG_BACKGROUND:
				return "TAG_BACKGROUND";
			case LineToken.DECLARATION_START:
				return "DECLARATION_START";
			case LineToken.DECLARATION_END:
				return "DECLARATION_END";
			case LineToken.DEC_BACKGROUND:
				return "DEC_BACKGROUND";
			case LineToken.TAG:
				return "TAG";
			case LineToken.TAG_ENDER:
				return "TAG_ENDER";
			case LineToken.ATTRIBUTE:
				return "ATTRIBUTE";
			case LineToken.ATTRIBUTE_VALUE:
				return "ATTRIBUTE_VALUE";
			case LineToken.TAG_DELIMITER_END:
				return "TAG_DELIMITER_END";

			case LineToken.TAG_DELIMITER_START:
				return "TAG_DELIMITER_START";	
			case LineToken.ATTRIBUTE_SEPARATOR:
				return "ATTRIBUTE_SEPARATOR";
			case LineToken.LITERAL2:
				return "LITERAL2";
			case LineToken.DTD_INNER_COMMENT :
				return "DTD_INNER_COMMENT";
			case LineToken.COMMENT:
				return "COMMENT";
			case LineToken.COMMENT_END:
				return "COMMENT_END";
			case LineToken.COMMENT_START:
				return "COMMENT_START";
			case LineToken.COMMENT_BACKGROUND:
				return "COMMENT_BACKGROUND";
			case LineToken.NAMESPACE:
				return "NAMESPACE";
			case LineToken.TAG_UNDERLINE:
				return "TAG_UNDERLINE";
			case LineToken.ENTITY:
				return "ENTITY";
			case LineToken.ENTITY_BACKGROUND:
				return "ENTITY_BACKGROUND";
			case LineToken.CDATA:
				return "CDATA";
			case LineToken.CDATA_START:
				return "CDATA_START";
			case LineToken.CDATA_END:
				return "CDATA_END";
			case LineToken.CDATA_BACKGROUND:
				return "CDATA_BACKGROUND";
			case LineToken.INVALID:
				return "INVALID";
			case LineToken.DTD_ATTRIBUTE:
				return "DTD_ATTRIBUTE";
			case LineToken.DTD_ELEMENT:
				return "DTD_ELEMENT";
			case LineToken.DTD_ENTITY_PARAMETER :
				return "DTD_ENTITY_PARAMETER";
			case LineToken.DTD_ENTITY:
				return "DTD_ENTITY";
			case LineToken.DTD_NOTATION:
				return "DTD_NOTATION";
			case LineToken.LINE_SELECTION:
				return "LINE_SELECTION";
			case LineToken.SPACE:
				return "SPACE";
		}
		return "??";
	}

	
	static Color getColor(XMLEditor host, boolean lineError,
			boolean lineSelected, int ptype, int startingOffset,
			int stoppingOffset) {

		if ( host.getXMLContainer().getDocumentIntegrity().isProtectTag() ) {
			if ( ptype == LineToken.TEXT )
				return host.getColorForLiteral();
			else
			if ( ptype == LineToken.TAG )
				return Color.DARK_GRAY;
			else
				return Color.GRAY;
		}

		switch ( ptype ) {

		case LineToken.TEXT:
			return host.getColorForText();
		case LineToken.DECLARATION:
			return host.getColorForDeclaration();
		case LineToken.DOCTYPE:
			return host.getColorForDocType();
		case LineToken.DOCTYPE_START:
			return host.getColorForDocTypeStart();
		case LineToken.DOCTYPE_END:
			return host.getColorForDocTypeEnd();
		case LineToken.DOCTYPE_BACKGROUND:
			return host.getColorForDocTypeBackground();
		case LineToken.LITERAL:
		case LineToken.ATTRIBUTE_VALUE:
			return host.getColorForLiteral();
		case LineToken.TAG_BACKGROUND:

			if (startingOffset >= host.getSelectionStart()
					&& stoppingOffset <= host.getSelectionEnd())
				return host.getSelectionColor();

			return host.getColorForTagBackground();
		case LineToken.DECLARATION_START:
			return host.getColorForDeclarationStart();
		case LineToken.DECLARATION_END:
			return host.getColorForDeclarationEnd();
		case LineToken.DEC_BACKGROUND:

			if (startingOffset >= host.getSelectionStart()
					&& stoppingOffset <= host.getSelectionEnd())
				return host.getSelectionColor();

			return host.getColorForDeclarationBackground();
		case LineToken.TAG:
			return host.getColorForTag();
		case LineToken.TAG_ENDER:
			return host.getColorForTagEnd();

		case LineToken.ATTRIBUTE:
			return host.getColorForAttribute();
		case LineToken.TAG_DELIMITER_END:
		case LineToken.TAG_DELIMITER_START:

			if ( !SharedProperties.FULL_TEXT_VIEW ) {			
				if (startingOffset >= host.getSelectionStart()
						&& stoppingOffset <= host.getSelectionEnd())
					return host.getSelectedTextColor();
	
				if (lineError) {
					if (host.getColorForLineError() != null)
						return host.getColorForLineError();
				} else if (lineSelected) {
					if (host.getColorForLineSelection() != null)
						return host.getColorForLineSelection();
				}
			}

			return host.getColorForTagDelimiter();

		case LineToken.ATTRIBUTE_SEPARATOR:
			return host.getColorForAttributeSeparator();
		case LineToken.LITERAL2:
			return host.getColorForLiteral();

		case LineToken.DTD_INNER_COMMENT :
		case LineToken.COMMENT:
			return host.getColorForComment();

		case LineToken.COMMENT_END:
			return host.getColorForCommentEnd();
		case LineToken.COMMENT_START:
			return host.getColorForCommentStart();
		case LineToken.COMMENT_BACKGROUND:
			return host.getColorCommentBackground();

		case LineToken.NAMESPACE:
			return host.getColorForNameSpace();
		case LineToken.TAG_UNDERLINE:
			return host.getColorForTagUnderline();
		case LineToken.ENTITY:
			return host.getColorForEntity();
		case LineToken.ENTITY_BACKGROUND:

			if (startingOffset >= host.getSelectionStart()
					&& stoppingOffset <= host.getSelectionEnd())
				return host.getSelectionColor();

			return host.getColorForEntityBackground();
		case LineToken.CDATA:
			return host.getColorForCDATA();
		case LineToken.CDATA_START:
			return host.getColorForCDATAStart();
		case LineToken.CDATA_END:
			return host.getColorForCDATAEnd();
		case LineToken.CDATA_BACKGROUND:
			return host.getColorForCDATABackground();
		case LineToken.INVALID:
			return host.getColorForInvalid();
		case LineToken.DTD_ATTRIBUTE:
			return host.getColorForDTDAttribute();
		case LineToken.DTD_ELEMENT:
			return host.getColorForDTDElement();
		case LineToken.DTD_ENTITY_PARAMETER :
		case LineToken.DTD_ENTITY:
			return host.getColorForDTDEntity();
		case LineToken.DTD_NOTATION:
			return host.getColorForDTDNotation();
		case LineToken.LINE_SELECTION:
			return host.getColorForLineSelection();
		}
		return host.getColorForText();

	}

}

