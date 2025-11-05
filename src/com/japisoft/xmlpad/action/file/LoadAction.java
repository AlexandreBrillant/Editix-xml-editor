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

package com.japisoft.xmlpad.action.file;

import java.io.*;
import java.net.URL;
import javax.swing.*;

import com.japisoft.xmlpad.Debug;
import com.japisoft.xmlpad.SharedProperties;
import com.japisoft.xmlpad.UIFactory;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.action.XMLAction;
import com.japisoft.xmlpad.toolkit.XMLFileData;
import com.japisoft.xmlpad.toolkit.XMLToolkit;

/**
 * Load a new XML document
 *
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.1
 * @since 1.0
 * @see XMLAction */
public class LoadAction extends XMLAction {
	
	public static final String ID = LoadAction.class.getName();
	
	public LoadAction() {
		super();
	}

	static XMLFileData getContentFromFileName(String fileName) throws Throwable {
		InputStream input = null;
		if (fileName.indexOf("://") > -1) {
			// URL case
			URL url = new URL(fileName);
			input = url.openStream();
		} else {
			input = new FileInputStream( fileName );
		}
		return XMLToolkit.getContentFromInputStream( input, null );
	}

	/** Reset the container with this fileName content 
	 * @param fileName a file system path or URL */
	public static void loadInBuffer(XMLContainer container, String fileName)
		throws Throwable {
		XMLFileData content = getContentFromFileName(fileName);
		container.setCurrentDocumentLocation(fileName);
		container.setText(content.getContent());
		Debug.debug( "Read with " + content.getEncoding() );
		container.getDocumentInfo().setEncoding( content.getEncoding() );
	}

	public boolean notifyAction() {
		JFileChooser chooser = UIFactory.getInstance().getOpenFileChooser();
		chooser.setFileFilter(container.getDocumentInfo().getFileFilter());
		if (container.getDocumentInfo().getWorkingDirectory() != null)
			chooser.setCurrentDirectory(
				new File(container.getDocumentInfo().getWorkingDirectory()));
		else {
			if ( SharedProperties.DEFAULT_LOAD_DIRECTORY != null ) {
				chooser.setCurrentDirectory(
						new File( SharedProperties.DEFAULT_LOAD_DIRECTORY ) );				
			}
		}

		int returnVal = chooser.showOpenDialog(editor);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			boolean rtSupport = editor.isEnabledRealTimeStructureChanged();
			editor.setEnabledRealTimeStructureChanged(false);
			String fileName = "" + chooser.getSelectedFile();
			try {
				loadInBuffer(container, fileName);
				//container.searchAndParseDTD();
			} catch (Throwable th) {
				JOptionPane.showMessageDialog(
					editor,
					th.getMessage(),
					"Error",
					JOptionPane.ERROR_MESSAGE);
				th.printStackTrace();
				return INVALID_ACTION;
			}
			editor.setEnabledRealTimeStructureChanged(rtSupport);
			editor.notifyStructureChanged();
		}
		return VALID_ACTION;
	}

}

