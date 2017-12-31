package com.japisoft.xmlpad.helper.model;

import javax.swing.JMenuItem;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import com.japisoft.xmlpad.editor.XMLPadDocument;

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
public class TagPopup extends CommonPopup {
	protected TagDescriptor[] tags;
	private int offset;
	private Document doc;

	public TagPopup(TagDescriptor[] tags, int offset, Document doc) {
		super("Elements");
		this.tags = tags;
		this.offset = offset;
		this.doc = doc;
		buildit();
	}

	protected void buildit() {
		for (int i = 0; i < tags.length; i++) {
			add(buildMenuItem(tags[i]));
		}
	}

	protected JMenuItem buildMenuItem(TagDescriptor tag) {
		JMenuItem item = getMenuItem();
		item.setText(tag.getName());
		return item;
	}

	protected String defaultInsertion = "<";

	protected void notifySelection(int selection) {
		if (selection >= 1) {
			TagDescriptor td = tags[selection];
			try {
				String element = td.getBuiltTag();
				doc.insertString(offset, element, null);

				if (!td.isRaw()) {
					int i = element.indexOf( "\"" );

					if (i > -1) {
						((XMLPadDocument) doc)
							.getCurrentEditor()
							.setCaretPosition(
							offset + i + 1);
					} else {
						i = element.indexOf("><");
						if (i > -1)
							((XMLPadDocument) doc)
								.getCurrentEditor()
								.setCaretPosition(
								offset + i + 1);
					}
				} else {
					int i = element.indexOf( " " );
					if ( i > -1 ) {
						( (XMLPadDocument) doc )
							.getCurrentEditor()
							.setCaretPosition(
								offset + i + 1 );	
					}
				}

			} catch (BadLocationException exc) {
			}
		} else if (selection == 0) {
			boolean state = ((XMLPadDocument) doc).isSyntaxPopup();
			((XMLPadDocument) doc).setSyntaxPopup(false);
			try {
				doc.insertString(offset, defaultInsertion, null);
			} catch (BadLocationException exc) {
			}
			((XMLPadDocument) doc).setSyntaxPopup(state);
		}
	}
}
