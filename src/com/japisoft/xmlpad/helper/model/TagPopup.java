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

package com.japisoft.xmlpad.helper.model;

import javax.swing.JMenuItem;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import com.japisoft.xmlpad.editor.XMLPadDocument;

/**
 * Popup for tag
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
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