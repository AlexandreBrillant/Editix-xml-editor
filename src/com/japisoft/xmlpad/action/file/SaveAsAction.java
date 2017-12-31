package com.japisoft.xmlpad.action.file;

import java.io.*;
import javax.swing.*;

import com.japisoft.xmlpad.UIFactory;

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
public class SaveAsAction extends SaveAction {
	
	public static final String ID = SaveAsAction.class.getName();
	public static final String CURRENT_DIR_PROPERTY = "currentDir";
	
	public SaveAsAction() {
		super();
	}
	
	private String currentDir = null;
	
	public void setProperty(String propertyName, Object value) {
		if ( CURRENT_DIR_PROPERTY.equals( propertyName ) ) {
			currentDir = ( String )value;
		}
	}
	
	public Object getProperty(String propertyName, Object defaultValue) {
		if ( CURRENT_DIR_PROPERTY.equals( propertyName ) )
			return currentDir;
		return super.getProperty( propertyName, defaultValue );
	}

	private boolean enabledExt = true;

	/** Force the XML extension when choosing a file name */
	public void setForceExtension(boolean enable) {
		this.enabledExt = enable;
	}

	public boolean notifyAction() {
		if ( container == null )
			return false;

		JFileChooser chooser = UIFactory.getInstance().getSaveFileChooser();
		if ( currentDir != null ) {
			chooser.setCurrentDirectory( new File( currentDir ) );
		}

		chooser.setFileFilter(container.getDocumentInfo().getFileFilter());
		if (container.getDocumentInfo().getWorkingDirectory() != null) {
			chooser.setCurrentDirectory(
				new File(container.getDocumentInfo().getWorkingDirectory()));
		} else
			if ( container.getDocumentInfo().getCurrentDocumentLocation() != null )
				chooser.setCurrentDirectory( 
					new File( container.getDocumentInfo().getCurrentDocumentLocation() ).getParentFile() );

		int returnVal = chooser.showSaveDialog(editor);
		if ( returnVal == JFileChooser.APPROVE_OPTION ) {
			String fileName = "" + chooser.getSelectedFile();
			
			try {
				currentDir = chooser.getSelectedFile().getParentFile().toString();
			} catch( Throwable th ) {}
			
			if (enabledExt) {
				// Check if the file name only contains a '.' or not
				if (chooser.getSelectedFile().getName().indexOf(".") == -1)
					fileName += "."
						+ container.getDocumentInfo().getDefaultFileExt();
			}			
			try {
				
				// Check if this is to another file name than the current one
				if ( container.getCurrentDocumentLocation() == null ||
						!container.getCurrentDocumentLocation().equals( fileName ) ) {
					File f = new File( fileName );
					if ( f.exists() ) {
						if ( JOptionPane.showConfirmDialog(
								container.getView(),
								"Overwrite " + fileName + " ?" ) !=
									JOptionPane.YES_OPTION ) {
							return false;
						}
					}
				}

				if (saveDocument(fileName)) {
					container.setCurrentDocumentLocation(fileName);
					return VALID_ACTION;
				} else
					return INVALID_ACTION;
			} catch (Throwable th) {
				JOptionPane.showMessageDialog(
					editor,
					th.getMessage(),
					"Error",
					JOptionPane.ERROR_MESSAGE);
				return INVALID_ACTION;
			}
		} else
			return INVALID_ACTION;
	}

}
