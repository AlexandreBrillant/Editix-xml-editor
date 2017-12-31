 // (c) ALEXANDRE BRILLANT : http://www.japisoft.com
// All this work is confidential, you have rights to
// change and evolve it for your products but you
// have no rights to sell it, propose concurrent works.
// Morever any changes to bugs or evolutions should
// be send to JAPISOFT that needs to maintain a
// valid version and has all rights on the product.


package com.japisoft.xpath;

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
public interface Navigator {

    /** @param refNode XML node
	@param axis 'ancestor', 'ancestor-or-self', 'attribute', 'child', 'descendant', 'descendant-or-self', 'following',
	  'following-sibling', 'namespace', 'parent', 'preceding', 'preceding-sibling', 'self'.
	@param nodeType comment', 'text', 'processing-instruction', 'node'.
	@param name name test
        @param namespaceURI null or the namespace URI computed by a prefix and a context namespace declaration
        @param attributeMode for notifying the the name is an attribute name
    */
    public NodeSet getNodes( Object refNode, String axis, String nodeType, String name, String namespaceURI, boolean attributeMode );

    /** @return the root node */
    public Object getRoot( Object refNode );

    /** A special node containing the root as the unique child */
    public Object getDocumentRoot( Object refNode );

}

// Navigator ends here
