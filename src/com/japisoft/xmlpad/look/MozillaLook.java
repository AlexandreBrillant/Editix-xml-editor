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

package com.japisoft.xmlpad.look;

import java.awt.*;

import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.editor.*;

/**
 * Look with mozilla feature
 *
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * @see DefaultLook */
public class MozillaLook extends DefaultLook {
	public MozillaLook() {
		super();
	}

	public void install( XMLContainer container, XMLEditor editor) {
		super.install( container, editor );

		// Color

		editor.setColorForTagBorderLine( Color.gray );

		editor.setColorForEntity(new Color(255, 69, 0));
		editor.setColorForDocType(new Color(70, 130, 180));
		editor.setColorForDeclaration(new Color(70, 130, 180));

		editor.setColorForLiteral(new Color(0, 0, 255));
		editor.setColorForTag(new Color(128, 0, 128));
		editor.setColorForAttribute(new Color(0, 0, 0));
		editor.setColorForNameSpace(editor.getColorForTag().darker());
		editor.setColorForTagUnderline( Color.blue );
	}

}

