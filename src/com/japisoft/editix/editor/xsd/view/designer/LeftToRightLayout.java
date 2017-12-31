package com.japisoft.editix.editor.xsd.view.designer;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import javax.swing.JComponent;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
public class LeftToRightLayout implements LayoutManager2 {

	public void addLayoutComponent(Component comp, Object constraints) {
	}

	public Dimension maximumLayoutSize(Container target) {
		return null;
	}

	public float getLayoutAlignmentX(Container target) {
		return 0;
	}

	public float getLayoutAlignmentY(Container target) {
		return 0;
	}

	public void invalidateLayout(Container target) {
	}

	public void addLayoutComponent(String name, Component comp) {
	}

	public void removeLayoutComponent(Component comp) {
	}

	public Dimension preferredLayoutSize(Container parent) {
		if (parent.getComponentCount() == 0)
			return new Dimension(0, 0);
		int height = parent.getHeight();
		int x = 2;
		// Main component
		JComponent m = (JComponent) parent.getComponent(0);
		XSDComponent component = (XSDComponent) m;
		Element e = (Element) component.getElement().getParentNode();
		NodeList nl = e.getChildNodes();		
		return layoutChildren( parent, nl, null );
	}

	public Dimension minimumLayoutSize(Container parent) {
		return null;
	}

	public void layoutContainer(Container parent) {
		if (parent.getComponentCount() == 0)
			return;
		int height = parent.getHeight();
		int x = 2;
		// Main component
		JComponent m = (JComponent) parent.getComponent(0);
		XSDComponent component = (XSDComponent) m;
		Element e = (Element) component.getElement().getParentNode();
		if (e != null) {
			NodeList nl = e.getChildNodes();
			layoutChildren(parent, nl, null);
		}
	}

	private Dimension layoutChildren(Container parent, NodeList nl,
			JComponent ref) {
		Rectangle source = null;
		if (ref == null) {
			source = new Rectangle(0, (parent.getHeight() / 2) - 10, 0, 20);
		} else {
			source = ref.getBounds();
		}

		int childrenHeight = getChildrenHeight(nl);

		int x = (source.x + source.width + 5);
		int y = (source.y - (childrenHeight - source.height) / 2);

		int width = 0;
		int height = 0;
		XSDComponent lastComponent = null;

		for (int i = 0; i < nl.getLength(); i++) {
			if (!(nl.item(i) instanceof Element))
				continue;
			Element e = (Element) nl.item(i);
			XSDComponent c = (XSDComponent) e.getUserData("ui");
			if (c == null) {
				continue;
			}

			Dimension dc = c.getView().getPreferredSize();
			int realHeight = getHeight(e);

			int dy = (realHeight - dc.height) / 2;

			y += dy;
			
			c.getView().setBounds(x, y, dc.width, dc.height);

			if (c.getView().getParent() == parent) {
				width = Math.max(x + dc.width, width);
				height = Math.max(y + dc.height, height);
			}

			if (c != null) {
				y += realHeight - dy;
				// if ( i > 0 )
				y += 10;
			}
			if (e.hasChildNodes()) {
				Dimension d2 = layoutChildren(parent, e.getChildNodes(), c
						.getView());
				width = Math.max(d2.width, width);
				height = Math.max(d2.height, height);
			}
		}
		return new Dimension(width, height);
	}

	private int getChildrenHeight(NodeList nl) {
		int height = 0;
		if (nl != null) {
			boolean first = true;
			for (int i = 0; i < nl.getLength(); i++) {
				if (nl.item(i) instanceof Element) {
					Element e = ( Element )nl.item( i );
					if ( e.getUserData( "ui" ) != null ) {
						height += getHeight( e );
						if ( !first ) {
							height += 10;
						}
						first = false;
					}
				}
			}
		}
		return height;
	}

	private int getHeight(Node e) {
		XSDComponent c = (XSDComponent) e.getUserData("ui");
		if (c != null) {
			Dimension d = c.getView().getPreferredSize();
			return 
					Math.max(d.height, 
							getChildrenHeight(e.getChildNodes()));
		} else
			return 0;
	}

}
