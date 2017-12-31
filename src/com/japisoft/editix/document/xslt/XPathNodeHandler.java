package com.japisoft.editix.document.xslt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.japisoft.editix.ui.xslt.XMLDataSourcePanel;
import com.japisoft.editix.ui.xslt.XSLTEditor;
import com.japisoft.framework.collection.FastVector;
import com.japisoft.framework.xml.parser.document.Document;
import com.japisoft.framework.xml.parser.node.FPNode;

import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.editor.XMLPadDocument;
import com.japisoft.xmlpad.helper.handler.AbstractHelperHandler;
import com.japisoft.xmlpad.helper.model.BasicDescriptor;

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
public class XPathNodeHandler extends AbstractHelperHandler {

	@Override
	protected String getActivatorSequence() {
		return null;
	}
	
	public boolean haveDescriptors(
			FPNode currentNode,
			XMLPadDocument document, 
			boolean insertBefore, 
			int offset,
			String activatorString ) {
		return activatorString == null && document.isInsideAttributeValue( offset );
	}
	
	protected void installDescriptors(
			FPNode currentNode,
			XMLPadDocument document,
			int offset, 
			String activatorString ) {
		
		XMLContainer container = document.getContainer();
		XSLTEditor panel = ( XSLTEditor )container.getParentPanel();
		XMLDataSourcePanel sourcePanel = panel.getSourcePanel();
		FPNode rootNode = sourcePanel.getRootNode();
		if ( rootNode != null ) {
			String context = getXPathContext( currentNode );
			List<String> res = choice( rootNode, context );
			for ( String r : res ) {
				addDescriptor( new BasicDescriptor( r ) );
			}
		}		
	}	

	private List<String> choice( FPNode rootNode, String context ) {
		ArrayList<String> res = new ArrayList<String>();
		Document doc = rootNode.getDocument();
		FastVector fv = doc.getFlatNodes();
		if ( fv != null ) {
			for ( int i = 0; i < fv.size(); i++ ) {
				FPNode node = ( FPNode )fv.get( i );
				if ( context == null ) {
					addResult( res, node );
				} else {
					if ( node.matchContent( context ) ) {
						// Attribute
						List<FPNode> attributes = node.getViewAttributeNodes();
						if ( attributes != null ) {
							for ( FPNode attNode : attributes ) {
								addResult( res, attNode );
							}
						}						
						// Children only
						for ( int j = 0; j < node.getChildCount(); j++ ) {
							addResult( res, ( FPNode )node.getChildAt( j ) );
						}
					}					
				}
			}
		}
		Collections.sort( res );
		return res;
	}

	private void addResult( List<String> res, FPNode node ) {
		if ( node.isTag() ) {
			if ( !res.contains( node.getContent() ) )
				res.add( node.getContent() );
		} else {
			if ( node.isAttribute() ) {
				if ( !res.contains( "@" + node.getContent() ) )
					res.add( "@" + node.getContent() );
			}
		}
	}

	private String getXPathContext( FPNode currentNode ) {
		String context = null;
		currentNode = currentNode.getFPParent();
		while ( currentNode != null ) {
			if ( currentNode.matchContent( "for-each" ) ) {
				context = currentNode.getAttribute( "select" );
				break;
			} else
			if ( currentNode.matchContent( "template" ) ) {
				context = currentNode.getAttribute( "match" );
				break;
			} else
				currentNode = currentNode.getFPParent();
		}
		if ( context != null ) {
			String[] tmp = context.split( "/" );
			for ( int i = 0; i < tmp.length; i++ ) {
				if ( !tmp[ i ].startsWith( "@" ) )
					context = tmp[ i ];
			}
			int i = context.lastIndexOf( "[" );
			if ( i > 0 )
				context = context.substring( 0, i );
		}
		if ( "/".equals( context ) )
			return null;
		return context;
	}

}
