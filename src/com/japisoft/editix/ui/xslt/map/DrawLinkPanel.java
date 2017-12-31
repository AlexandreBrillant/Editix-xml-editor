package com.japisoft.editix.ui.xslt.map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.w3c.dom.Element;

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
public class DrawLinkPanel extends JPanel {

	private JTree xsltTree;
	private JTree rightTree;	

	public DrawLinkPanel( 
			JTree xsltTree, 
			JTree rightTree ) {
		this.xsltTree = xsltTree;
		this.rightTree = rightTree;
	}

	private TreePath getPath( VirtualDomNode n ) {
		List<VirtualDomNode> l = new ArrayList<VirtualDomNode>();
		while ( n != null ) {
			l.add( 0, n );
			n = n.getParent();
		}
		if ( l.size() == 0 )
			return null;
		return new TreePath( l.toArray() );
	}

	public void dispose() {
		xsltTree = null;
		rightTree = null;
	}

	private void drawLinks( 
			Graphics g, 
			Rectangle left, 
			TreePath currentXsltPath ) {		

		if ( !( currentXsltPath.getLastPathComponent() instanceof VirtualDomNode ) )
			return;
		
		
		VirtualDomNode xsltNode = ( VirtualDomNode )currentXsltPath.getLastPathComponent();

		for ( int i = xsltNode.getChildCount( MapPanel.HTTP_WWW_W3_ORG_1999_XSL_TRANSFORM ) - 1; i >= 0; i-- ) {
			VirtualDomNode otherNode = xsltNode.getChildAt( MapPanel.HTTP_WWW_W3_ORG_1999_XSL_TRANSFORM, i );
			TreePath tpOther = getPath( otherNode );
			if ( tpOther == null ) {
				xsltNode.getChildAt( MapPanel.HTTP_WWW_W3_ORG_1999_XSL_TRANSFORM, i );
				continue;
			}
			rightTree.expandPath( tpOther );
			
			Element eTmp = ( Element )xsltNode.getSource();
			String lbl = eTmp.getAttribute( "match" );
			if ( "".equals( lbl ) ) {
				lbl = eTmp.getAttribute( "name" );
			}
			if ( "".equals( lbl ) )
				lbl = eTmp.getAttribute( "select" );

			Rectangle r2 = rightTree.getPathBounds( tpOther );
			if ( r2 != null ) {
				int xi = 0;
				int yi = left.y + left.height;
				
				int xj = getWidth();
				int yj = r2.y + r2.height;
			
				yi += 20;
				yj += 20;

				g.setColor( Color.BLACK );
				g.fillOval( -5 + xi, yi - 5, 10, 10 );
				g.fillOval( xj - 5, yj - 5, 10, 10 );

				g.setColor( Color.DARK_GRAY );
				g.drawString( lbl, ( xj - xi ) / 2, ( yi + ( yj - yi ) / 2 ) - 5 );

				g.setColor( Color.BLUE );
				g.drawLine( xi, yi, xj, yj );
			}
		}
	}

	@Override
	protected void paintComponent( Graphics g ) {
		super.paintComponent(g);
		
		g.setColor( Color.GRAY );
		g.drawRect( 0, 0, getWidth(), getHeight() - 2 );
		
		TreePath tp = xsltTree.getSelectionPath();
		Enumeration<TreePath> en = xsltTree.getExpandedDescendants( new TreePath( xsltTree.getModel().getRoot() ) );
		if ( en != null ) {
			while ( en.hasMoreElements() ) {
				TreePath tpCurrent = en.nextElement();
				while ( tpCurrent != null ) {
					Rectangle r = xsltTree.getPathBounds( tpCurrent );				
					drawLinks( g, r, tpCurrent );
					tpCurrent = tpCurrent.getParentPath();
				}
			}			
		}
	}

}
