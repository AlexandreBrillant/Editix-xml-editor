package com.japisoft.xmlpad.editor;

import java.awt.Color;

import com.japisoft.xmlpad.SharedProperties;

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
public final class LineElement {

	static final LineElement BLANKELEMENT = new LineElement(" ", LineElement.TEXT);
	static final LineElement TAG_MARKER = new LineElement( LineElement.TAG );
	
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

	public String content;

	public int type;

	public int previousType;

	public int nextType;

	public int majorLineElement = 0;
	
	public int offset;
	
	public LineElement(String content, int type) {
		this.content = content;
		this.type = type;
	}

	public LineElement(int type) {
		this(null, type);
	}

	boolean hasContent() {
		return content != null;
	}

	public String toString() {
		return "{" + content + ",type:" + type + "offset:," + offset + "}";
	}

	static Color getColor(XMLEditor host, boolean lineError,
			boolean lineSelected, int ptype, int startingOffset,
			int stoppingOffset) {

		if ( host.getXMLContainer().getDocumentIntegrity().isProtectTag() ) {
			if ( ptype == LineElement.TEXT )
				return Color.BLUE;
			else
			if ( ptype == LineElement.TAG )
				return Color.DARK_GRAY;
			else
				return Color.GRAY;
		}

		switch ( ptype ) {

		case LineElement.TEXT:
			return host.getColorForText();
		case LineElement.DECLARATION:
			return host.getColorForDeclaration();
		case LineElement.DOCTYPE:
			return host.getColorForDocType();
		case LineElement.DOCTYPE_START:
			return host.getColorForDocTypeStart();
		case LineElement.DOCTYPE_END:
			return host.getColorForDocTypeEnd();
		case LineElement.DOCTYPE_BACKGROUND:
			return host.getColorForDocTypeBackground();
		case LineElement.LITERAL:
			return host.getColorForLiteral();
		case LineElement.TAG_BACKGROUND:

			if (startingOffset >= host.getSelectionStart()
					&& stoppingOffset <= host.getSelectionEnd())
				return host.getSelectionColor();

			return host.getColorForTagBackground();
		case LineElement.DECLARATION_START:
			return host.getColorForDeclarationStart();
		case LineElement.DECLARATION_END:
			return host.getColorForDeclarationEnd();
		case LineElement.DEC_BACKGROUND:

			if (startingOffset >= host.getSelectionStart()
					&& stoppingOffset <= host.getSelectionEnd())
				return host.getSelectionColor();

			return host.getColorForDeclarationBackground();
		case LineElement.TAG:
			return host.getColorForTag();
		case LineElement.TAG_ENDER:
			return host.getColorForTagEnd();
		case LineElement.ATTRIBUTE:
			return host.getColorForAttribute();
		case LineElement.TAG_DELIMITER_END:
		case LineElement.TAG_DELIMITER_START:

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
		case LineElement.ATTRIBUTE_SEPARATOR:
			return host.getColorForAttributeSeparator();
		case LineElement.LITERAL2:
			return host.getColorForLiteral();

		case LineElement.DTD_INNER_COMMENT :
		case LineElement.COMMENT:
			return host.getColorForComment();

		case LineElement.COMMENT_END:
			return host.getColorForCommentEnd();
		case LineElement.COMMENT_START:
			return host.getColorForCommentStart();
		case LineElement.COMMENT_BACKGROUND:
			return host.getColorCommentBackground();

		case LineElement.NAMESPACE:
			return host.getColorForNameSpace();
		case LineElement.TAG_UNDERLINE:
			return host.getColorForTagUnderline();
		case LineElement.ENTITY:
			return host.getColorForEntity();
		case LineElement.ENTITY_BACKGROUND:

			if (startingOffset >= host.getSelectionStart()
					&& stoppingOffset <= host.getSelectionEnd())
				return host.getSelectionColor();

			return host.getColorForEntityBackground();
		case LineElement.CDATA:
			return host.getColorForCDATA();
		case LineElement.CDATA_START:
			return host.getColorForCDATAStart();
		case LineElement.CDATA_END:
			return host.getColorForCDATAEnd();
		case LineElement.CDATA_BACKGROUND:
			return host.getColorForCDATABackground();
		case LineElement.INVALID:
			return host.getColorForInvalid();
		case LineElement.DTD_ATTRIBUTE:
			return host.getColorForDTDAttribute();
		case LineElement.DTD_ELEMENT:
			return host.getColorForDTDElement();
		case LineElement.DTD_ENTITY_PARAMETER :
		case LineElement.DTD_ENTITY:
			return host.getColorForDTDEntity();
		case LineElement.DTD_NOTATION:
			return host.getColorForDTDNotation();
		case LineElement.LINE_SELECTION:
			return host.getColorForLineSelection();
		}
		return host.getColorForText();

	}

}

// LineElement ends here
