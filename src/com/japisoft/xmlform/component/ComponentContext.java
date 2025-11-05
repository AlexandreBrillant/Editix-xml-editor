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

package com.japisoft.xmlform.component;

import org.w3c.dom.Document;
import com.japisoft.xmlform.designer.data.GrammarNodeTreeNode;

public interface ComponentContext {


	public static final int SELECT_ACTION = 0;
	public static final int DELETE_ACTION = 1;
	public static final int WARNING_MESSAGE = 2;
	
	public GrammarNodeTreeNode getCurrentTreeNode();

	public XMLFormComponentFactory getComponentFactory();

	public Document getDocument();

	public void action( int actionCode, Object parameter );
	

}

