package com.japisoft.editix.editor.xsd;

import javax.swing.JOptionPane;

import com.japisoft.framework.ui.text.PathBuilder;
import com.japisoft.xmlpad.XMLContainer;

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
public class SingleFactoryImpl implements Factory {

	public XMLContainer buildNewContainer() {
		XMLContainer container = new XMLContainer();
		container.setToolBarAvailable( false );
		return container;
	}

	public void buildAndShowInformationDialog(String info) {
		JOptionPane.showMessageDialog( null, info );
	}

	public void buildAndShowErrorDialog(String error) {
		JOptionPane.showMessageDialog( null, error );
	}

	public PathBuilder getPathBuilder() {
		return null;
	}

	public boolean confirmDialog(String info) {
		return ( JOptionPane.showConfirmDialog( null, info ) == JOptionPane.OK_OPTION );
	}

}
