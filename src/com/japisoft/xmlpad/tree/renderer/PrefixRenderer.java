package com.japisoft.xmlpad.tree.renderer;

import javax.swing.ImageIcon;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.tree.renderer.FastTreeRenderer.TreeLabel;

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
