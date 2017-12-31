package com.japisoft.xmlpad.tree;

import java.util.ArrayList;

import javax.swing.JTree;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.parser.tools.XMLToolkit;
import com.japisoft.xmlpad.Debug;
import com.japisoft.xmlpad.error.ErrorListener;

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
class TreeErrorListener implements ErrorListener {

	private JTree tree;
	
	TreeErrorListener( JTree tree ) {
		if ( tree == null ) {
			throw new RuntimeException( "No tree found. Illegal usage for class TreeErrorListener" );
		}
		this.tree = tree;
	}

	public void initErrorProcessing() {}

	public void stopErrorProcessing() {
		if ( tree.getModel() == null ||	
				tree.getModel().getRoot() == null ) {	// Skip wrong tree state
			savedErrors = null;
			return;	
		}
		if ( savedErrors != null ) {
			for ( int i = 0; i < savedErrors.size(); i+= 2 ) {
				int offset = ( ( Integer )savedErrors.get( i ) ).intValue();
				String message = ( String )savedErrors.get( i + 1 );
				addErrorOnTheTree( offset, message );
			}
			savedErrors = null;
			tree.repaint();
		}
	}

	private ArrayList savedErrors = null;

	public void notifyError(Object context,boolean localError, String sourceLocation,
			int line, int col, int offset, String message, boolean onTheFly) {
		if ( localError ) {
			if ( savedErrors == null )
				savedErrors = new ArrayList();
			savedErrors.add( new Integer( offset ) );
			savedErrors.add( message );
		}
	}

	public void notifyNoError(boolean onTheFly) {
		savedErrors = null;
	}

	private void addErrorOnTheTree(
			int offset,
			String message ) {
		// Get the XML document
		FPNode node = ( FPNode )tree.getModel().getRoot();
		if ( node != null ) {
			try {
				FPNode targetNode = ( FPNode )XMLToolkit.getNodeForOffset( node.getDocument(), offset );
				while ( targetNode != null ) {
					if ( targetNode.errorMessage == null )
						targetNode.errorMessage = message;
					else
						targetNode.errorMessage = "\n" + message;
					targetNode = ( FPNode )targetNode.getFPParent();
				}
			} catch (RuntimeException e) {
				Debug.debug( e );
			}
		}
	}

}
