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

package com.japisoft.xmlpad.tree.renderer;

import javax.swing.ImageIcon;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.tree.renderer.FastTreeRenderer.TreeLabel;

public class PrefixRenderer extends DefaultRenderer {

	public PrefixRenderer(
			ImageIcon i1,
			ImageIcon i2,
			TreeLabel tl ) {
		super( i1, i2, tl );
	}

	public void prepare(FPNode n, boolean selected) {
		ImageIcon i = i1;
		String prefix = n.getNameSpacePrefix();
		if (prefix == null)
			tl.setContent("?", n.isTag() ? n.getNodeContent() : null,
					selected, i);
		else
			tl.setContent(
				prefix, 
				n.getNodeContent(), 
				selected, 
				i );
	}

}

