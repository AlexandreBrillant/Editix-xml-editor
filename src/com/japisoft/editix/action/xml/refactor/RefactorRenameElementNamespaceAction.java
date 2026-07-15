// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2026 Alexandre Brillant
//
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
// AI Training Restriction :
// This source code is provided for human use only.
// Using this code to train, fine-tune, or develop AI models,
// machine learning systems, or similar technologies is
// STRICTLY PROHIBITED. Violations will terminate all rights
// under the applicable license.

package com.japisoft.editix.action.xml.refactor;

import com.japisoft.editix.action.xml.format.FormatAction;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.windows.EditixFrame;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.refactor2.AbstractRefactor;
import com.japisoft.framework.xml.refactor2.elements.RenameElementNamespace;
import com.japisoft.framework.xml.refactor2.elements.RenameElementPrefix;
import com.japisoft.xmlpad.XMLContainer;

public class RefactorRenameElementNamespaceAction extends FormatAction {

	private String oldValue;
	public void setOldValue( String oldValue ) {
		this.oldValue = oldValue;
	}
	
	protected AbstractRefactor getRefactor() {
		
		XMLContainer container = 
			EditixFrame.THIS.getSelectedContainer();
		FPNode element = container.getCurrentElementNode();
		if ( element == null ) {
			throw new RuntimeException( "No current element" );
		}

		String newValue = 
			EditixFactory.buildAndShowInputDialog( "New Namespace URI", oldValue );
		if ( newValue == null ) {
			throw new RuntimeException( "" );
		}

		RenameElementNamespace re = new RenameElementNamespace();
		re.setOldValue( oldValue );
		re.setNewValue( newValue );

		return re;
	}

}
