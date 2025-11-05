// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2025 Alexandre Brillant
// 
// For non-commercial usage :
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
// For commercial use or integration into proprietary software :
// A commercial license is required. Visit https://www.editix.com for details.

package com.japisoft.xmlpad;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import com.japisoft.xmlpad.action.ActionModel;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
class XMLContainerAccessibility implements Accessibility {

	XMLContainer container;
	
	XMLContainerAccessibility( XMLContainer container ) {
		this.container = container;
	}
	
	public String getText() {
		return container.getText();
	}

	public boolean invokeAction(String actionName) {
		return ActionModel.activeActionByName(actionName,
				container, container.getEditor());
	}

	public void read(Reader reader) throws IOException {
		BufferedReader br = new BufferedReader(reader);
		try {
			StringBuffer sb = new StringBuffer();
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line)
						.append(System.getProperty("line.separator"));
			}
			setText(sb.toString());
		} finally {
			br.close();
		}
	}

	public void setText(String text) {
		container.setText(text);
	}

	public void write(Writer writer) throws IOException {
		BufferedWriter bw = new BufferedWriter(writer);
		try {
			bw.write(getText());
		} finally {
			bw.close();
		}
	}

	public void dispose() {
		this.container = null;
	}	
	
}

