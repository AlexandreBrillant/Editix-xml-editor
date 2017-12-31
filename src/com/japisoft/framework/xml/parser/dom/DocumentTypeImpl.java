package com.japisoft.framework.xml.parser.dom;

import org.w3c.dom.*;

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
public class DocumentTypeImpl extends NodeImpl implements DocumentType {
	public DocumentTypeImpl() {
		super();
	}

	/**
	 * The name of DTD; i.e., the name immediately following the 
	 * <code>DOCTYPE</code> keyword.
	 */
	public String getName() {
		throw new RuntimeException("Not implemented");
	}

	/**
	 * A <code>NamedNodeMap</code> containing the general entities, both 
	 * external and internal, declared in the DTD. Duplicates are discarded. 
	 * For example in:&lt;!DOCTYPE ex SYSTEM "ex.dtd" [ &lt;!ENTITY foo 
	 * "foo"&gt; &lt;!ENTITY bar "bar"&gt; &lt;!ENTITY % baz "baz"&gt;]&gt;
	 * &lt;ex/&gt;  the interface provides access to <code>foo</code> and 
	 * <code>bar</code> but not <code>baz</code>. Every node in this map also 
	 * implements the <code>Entity</code> interface.
	 * <br>The DOM Level 1 does not support editing entities, therefore 
	 * <code>entities</code> cannot be altered in any way.
	 */
	public NamedNodeMap getEntities() {
		throw new RuntimeException("Not implemented");
	}
	/**
	 * A <code>NamedNodeMap</code> containing  the notations declared in the 
	 * DTD. Duplicates are discarded. Every node in this map also implements 
	 * the <code>Notation</code> interface.
	 * <br>The DOM Level 1 does not support editing notations, therefore 
	 * <code>notations</code> cannot be altered in any way.
	 */
	public NamedNodeMap getNotations() {
		throw new RuntimeException("Not implemented");
	}

	public String getPublicId() {
		throw new RuntimeException("Not implemented");
	}

	public String getSystemId() {
		throw new RuntimeException("Not implemented");
	}

	public String getInternalSubset() {
		return null;
	}
}

// DocumentTypeImpl ends here
