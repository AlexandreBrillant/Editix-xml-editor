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
