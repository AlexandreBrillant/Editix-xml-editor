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

package com.japisoft.editix.ui;

import com.japisoft.editix.ui.pathbuilder.XMLPathBuilder;
import com.japisoft.editix.ui.xslt.Factory;
import com.japisoft.framework.ui.text.PathBuilder;
import com.japisoft.xmlpad.XMLContainer;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public class EditixXSLTFactoryImpl implements Factory, com.japisoft.xmlform.designer.Factory {

	public XMLContainer buildNewContainer() {
		return buildNewContainer( null );
	}

	public XMLContainer buildNewContainer( String type ) {
		// No check for change
		return EditixFactory.buildNewContainer( 
				type, 
				(String)null ).getMainContainer();
	}

	public void buildAndShowInformationDialog( String info ) {
		EditixFactory.buildAndShowInformationDialog( info );
	}

	public void buildAndShowErrorDialog( String error ) {
		EditixFactory.buildAndShowErrorDialog( error );
	}

	public PathBuilder getPathBuilder() {
		return new XMLPathBuilder();
	}
	
	public boolean confirmDialog( String info ) {
		return EditixFactory.buildAndShowConfirmDialog( info );
	}

}

