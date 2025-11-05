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

package com.japisoft.framework.xml.action;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.xml.transform.TransformerFactory;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.xml.XMLConfigPanel;
import com.japisoft.framework.xml.XSLTTransformer;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class ChooseTransformerAction extends AbstractAction {

	public void actionPerformed(ActionEvent e) {
		XMLConfigPanel panel = null;
		if ( DialogManager.showDialog(
				ApplicationModel.MAIN_FRAME,
				"JAXP Transformer",
				"Install a JAXP Compatible transformer",
				"Add your java jars and select a java JAXP compatible class for changing the EditiX XSLT Transformer",
				null,
				panel = new XMLConfigPanel( 
						TransformerFactory.class,
						XSLTTransformer.CONFIG_FILE ), new Dimension( 400, 500 ) ) == 
								DialogManager.OK_ID ) {
			if ( panel.save() ) {
				JOptionPane.showMessageDialog(
						ApplicationModel.MAIN_FRAME,
						"Restart " + ApplicationModel.SHORT_APPNAME + " for using the new transformer" );				
			}
		}		
	}

}

