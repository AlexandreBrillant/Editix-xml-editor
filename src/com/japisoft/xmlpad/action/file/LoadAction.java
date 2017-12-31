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
