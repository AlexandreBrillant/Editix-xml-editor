package com.japisoft.xmlpad.action.search;

import com.japisoft.framework.collection.FastVector;
import com.japisoft.framework.xml.parser.document.Document;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.action.XMLAction;

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
public class SearchElement extends XMLAction {

	public static final String ID = SearchElement.class.getName();
	
	public static final int NEXT_TAG = 0;
	public static final int PREVIOUS_TAG = 1;
	public static final int TAG_START = 2;
	public static final int TAG_END = 3;
	
	private int type;

	public SearchElement( int type ) {
		this.type = type;
	}

	protected boolean autoRequestFocus() { return false; }

	public boolean notifyAction() {
		FPNode node = ( FPNode )container.getTree().getModel().getRoot();
		if ( node == null )
			return INVALID_ACTION;

		FPNode current = container.getCurrentNode(); 
		if ( current == null ) {
			switch( type ) {
				case PREVIOUS_TAG : return INVALID_ACTION;
				case NEXT_TAG : 
					highlightLine( node.getStartingLine() );
					return VALID_ACTION;
				case TAG_START :
				case TAG_END : 
					return INVALID_ACTION;
			}
		}

		Document doc = node.getDocument();
		FastVector v = doc.getFlatNodes();
		int index = v.indexOf( current );

		switch( type ) {
			case PREVIOUS_TAG : 
				if ( index > 0 ) {
					FPNode n = ( FPNode )v.get( index - 1 );
					highlightLine( n.getStartingLine() );
					return VALID_ACTION;
				} else
					return INVALID_ACTION;
			case NEXT_TAG : 
				if ( index + 1 < v.size() ) {
					FPNode n = ( FPNode )v.get( index + 1 );
					highlightLine( n.getStartingLine() );
					return VALID_ACTION;
				} else
					return INVALID_ACTION;
			case TAG_START :
				highlightLine( current.getStartingLine() );
				return VALID_ACTION;
			case TAG_END :
				highlightLine( current.getStoppingLine() );
				return VALID_ACTION;
		}
		return VALID_ACTION;		
	}

	private void highlightLine( int line ) {
		container.getEditor().asynchronousHighlightLine( line );
	}

}
