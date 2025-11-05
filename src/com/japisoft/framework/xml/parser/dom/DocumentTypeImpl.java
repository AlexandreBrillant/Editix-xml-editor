// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2025 Alexandre Brillant
// 
// For non-commercial usage :
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// See the GNU General Public License for more details: https://www.gnu.org/licenses/gpl-3.0
// 
// For commercial use or integration into proprietary software :
// A commercial license is required. Visit https://www.editix.com for details.

package com.japisoft.framework.xml.parser.dom;

import org.w3c.dom.*;

/**
 * DocumentType : Not implemented
 *
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
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


