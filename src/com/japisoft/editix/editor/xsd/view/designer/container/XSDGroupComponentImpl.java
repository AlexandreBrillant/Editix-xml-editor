package com.japisoft.editix.editor.xsd.view.designer.container;

import java.awt.Dimension;
import java.awt.Graphics;

import org.w3c.dom.Element;

import com.japisoft.editix.editor.xsd.view.designer.LeftToRightLayout;

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
public class XSDGroupComponentImpl extends XSDContainerComponentImpl {

	public XSDGroupComponentImpl() {
		setLayout( new LeftToRightLayout() );
	}

	private boolean foundRef = false;
	private Dimension refPreferredSize = null;
	private String lastRef;

	public void setElement(Element e) {
		super.setElement(e);
		resetSize( false );
	}	
	
	private void resetSize( boolean invalidateMode ) {
		if ( !foundRef ) {
			if ( e.hasAttribute( "ref" ) ) {
				foundRef = true;
				paintName = true;
				paintElementName = false;
				lastRef = e.getAttribute( "ref" );
				// Reduce the size
				refPreferredSize = getNameSize();
				if ( invalidateMode ) {
					invalidate();
					getParent().validate();
				}
			}
		} else {
			// The ref attribute has been remove
			if ( !e.hasAttribute( "ref" ) ) {
				foundRef = false;
				paintName = false;
				paintElementName = true;
				refPreferredSize = null;
				if ( invalidateMode ) {
					invalidate();
					getParent().invalidate();
					getParent().getParent().validate();
				}
			} else 
				if ( lastRef != null ) {
					if ( !lastRef.equals( e.getAttribute( "ref" ) ) ) {
						lastRef = e.getAttribute( "href" );
						refPreferredSize = getNameSize();
						if ( invalidateMode ) {
							invalidate();
							getParent().validate();
						}
					}
				}
		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		resetSize( true );
	}

	public Dimension getPreferredSize() {
		if ( refPreferredSize != null )
			return refPreferredSize;
		return super.getPreferredSize();
	}	

}
