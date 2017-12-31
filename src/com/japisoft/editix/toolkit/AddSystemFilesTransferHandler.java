package com.japisoft.editix.toolkit;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import com.japisoft.editix.action.file.OpenAction;
import com.japisoft.editix.action.file.OpenProjectAction;
import com.japisoft.editix.document.DocumentModel;
import com.japisoft.editix.project.ProjectManager;
import com.japisoft.xmlpad.XMLDocumentInfo;

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
public class AddSystemFilesTransferHandler extends TransferHandler {
	
	public boolean canImport( JComponent comp, DataFlavor[] transferFlavors ) {			
		return transferFlavors[ 0 ].isFlavorJavaFileListType();
	}

	@Override
	public boolean canImport(TransferSupport support) {
		if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            return false;
        }
        return true;
	}
	
	@Override
	public boolean importData(TransferSupport info) {
        if (!info.isDrop()) {
            return false;
        }

        // Check for FileList flavor
        if (!info.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
         
            return false;
        }

        // Get the fileList that is being dropped.
        Transferable t = info.getTransferable();
        
        try {
        	List<File> list = (List<File>)t.getTransferData(DataFlavor.javaFileListFlavor);
			for ( int i = 0; i < list.size(); i++ ) {
				String filePath = list.get( i ).toString();
				
				if ( filePath.endsWith( ".pre" ) ) {
					OpenProjectAction.openProject( false, filePath );
				}
				
				XMLDocumentInfo info2 = DocumentModel.getDocumentByFileName( filePath );
				OpenAction.openFile( info2.getType(), false, filePath, null, null );
			}
			return list.size() > 0;            
        }
        catch (Exception e) { return false; }

	}

	/*
	public boolean importData(JComponent arg0, Transferable arg1) {
        if (!arg1.isDrop()) {
            return false;
        }
		
		try {
			java.util.List list = ( java.util.List )arg1.getTransferData(
					DataFlavor.javaFileListFlavor
			);
			if ( list != null ) {
			} 
		}
		catch (UnsupportedFlavorException e) {}
		catch( IOException e ) {}
		return false;
	}
	*/

}
