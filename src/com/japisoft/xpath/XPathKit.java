// (c) ALEXANDRE BRILLANT : http://www.japisoft.com
// All this work is confidential, you have rights to
// change and evolve it for your products but you
// have no rights to sell it, propose concurrent works.
// Morever any changes to bugs or evolutions should
// be send to JAPISOFT that needs to maintain a
// valid version and has all rights on the product.


package com.japisoft.xpath;

import com.japisoft.xpath.function.Lib;

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
public interface XPathKit {

    /** @return the library resolver */
    public Lib getLibrary();

    /** @return the tree navigator toolkit */
    public Navigator getNavigator();

    // Particular methods

    /**
     * @param refNode a reference document element
     * @param id ID value to match
     * @return the node with the unique ID. The ID scope is theorically
     * limited to attribute defined as ID in the DTD
     */
    public Object getNodeForId( Object refNode, String id );


    /** Compute the string-value for this node */
    public String getStringValue( Object node );

    /** Compute the local name of the node */
    public String getLocalName( Object node );

    /** Compute the namespace URI for this node */
    public String getNamespaceURI( Object node );

    /** Compute the qualified name for this node */
    public String getName( Object node );

    /** Compute the language for this node */
    public String getLang( Object node );

    /** Set a feature support for the current kit. A RuntimeException
     * should be thrown by the kit that doesn't support such feature */
    public void setFeature( String feature, boolean enable );

    /** @return true if the feature is supported by the kit */
    public boolean isFeatureSupported( String feature );

    /** Return a list of supported features */
    public String[] getSupportedFeatures();
    
    /** Replace the reference node by another one */
    public Object getBetterReferenceNode( Object node );
}

// XPathKit ends here

