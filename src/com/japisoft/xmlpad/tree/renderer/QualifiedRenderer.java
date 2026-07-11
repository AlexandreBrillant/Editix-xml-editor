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

package com.japisoft.xmlpad.tree.renderer;

import javax.swing.ImageIcon;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.tree.renderer.FastTreeRenderer.TreeLabel;

public class QualifiedRenderer extends DefaultRenderer {

	public QualifiedRenderer(
			ImageIcon i1,
			ImageIcon i2,
			TreeLabel tl ) {
		super( 
			i1, 
			i2, 
			tl 
		);
	}

	public void prepare(
			FPNode n, 
			boolean selected ) {

		ImageIcon i = i1;
		String prefix = n.getNameSpacePrefix();
		if (prefix == null)
			tl.setContent(n.isTag() ? n.getNodeContent() : null, null,
					selected, i);
		else
			tl.setContent(prefix + ":" + n.getNodeContent(), null,
					selected, i);

	}

}
