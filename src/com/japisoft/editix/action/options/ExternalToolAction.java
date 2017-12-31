package com.japisoft.editix.action.options;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.swing.AbstractAction;

import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.editix.main.Main;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.dialog.command.ExternalToolPane;
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
public class ExternalToolAction extends AbstractAction {

	public void actionPerformed(ActionEvent e) {

		ExternalToolPane frame = new ExternalToolPane();

		frame.setMacro(
				new String[] {
						"Current Document path - ${cf}",
						"Current Directory Path - ${cd}",
						"Current Document file name - ${cn}",
						"Home Directory - ${home}"
				} );

		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if (container != null) {
			String documentLocation = container.getCurrentDocumentLocation();
			if (documentLocation != null) {
				frame.setMacro(ExternalToolPane.CURRENT_PATH_MACRO,
						documentLocation);
				File f = new File(documentLocation);
				frame.setMacro(ExternalToolPane.CURRENT_DIRECTORY_MACRO, f
						.getParent());
				int i = documentLocation.lastIndexOf("/");
				if (i == -1)
					i = documentLocation.lastIndexOf("\\");
				if (i != -1)
					frame.setMacro(ExternalToolPane.CURRENT_FILENAME_MACRO,
							documentLocation.substring(i + 1));
				else
					frame.setMacro(ExternalToolPane.CURRENT_FILENAME_MACRO,
							documentLocation);
			}
		}

		// Reload previous choice
		File f = EditixApplicationModel.getAppUserPath();
		if (f != null) {
			File f2 = new File(f, "tools.dat");

			if (f2.exists()) {
				try {
					ObjectInputStream input = new ObjectInputStream(
							new FileInputStream(f2));
					try {
						ArrayList list = (ArrayList) input.readObject();
						frame.setActionItems(list);
					} finally {
						input.close();
					}
				} catch (IOException exc) {
					exc.printStackTrace();
				} catch (ClassNotFoundException exc2) {
					exc2.printStackTrace();
				}
			}
		}

		DialogManager.showDialog( 
				EditixFrame.THIS,
				"External Tools", 
				"External Tools", 
				"Call an external tool",
				null,
				frame );				
				
		if (f != null) {
			File f2 = new File(f, "tools.dat");
			try {

				ObjectOutputStream output = new ObjectOutputStream(
						new FileOutputStream(f2));
				try {
					output.writeObject(frame.getActionsItems());
				} finally {
					output.close();
				}

			} catch (IOException exc) {
				exc.printStackTrace();
			}
		}
	}

}
