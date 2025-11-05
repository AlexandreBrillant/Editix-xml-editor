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

package com.japisoft.framework.xml.parser.walker;

import com.japisoft.framework.xml.parser.node.*;

/**
 * Test for 2 elements criteria.
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public class AndCriteria extends AbstractCriteria {
    private ValidCriteria criteria1;
    private ValidCriteria criteria2;

    /** Match criteria1 and criteria2 */
    public AndCriteria( ValidCriteria criteria1, ValidCriteria criteria2 ) {
	super();   
	this.criteria1 = criteria1;
	this.criteria2 = criteria2;
    }

    public boolean isValid( FPNode node ) {
	return criteria1.isValid( node ) && criteria2.isValid( node );
    }

}


