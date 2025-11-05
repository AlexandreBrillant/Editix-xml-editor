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

import java.io.File;
import javax.swing.JFileChooser;
import com.japisoft.xmlpad.action.XMLAction;

/**
 * Insert a file content at the current location
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public class InsertAction extends XMLAction {
	
	public static final String ID = InsertAction.class.getName();
	
	public InsertAction() {
		super();
		setToolbarable( false );
	}

	protected boolean autoRequestFocus() { return false; }

	public boolean notifyAction() {
		String content = null;
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(container.getDocumentInfo().getFileFilter());
		if (container.getDocumentInfo().getWorkingDirectory() != null)
			chooser.setCurrentDirectory(
				new File(container.getDocumentInfo().getWorkingDirectory()));

		int returnVal = chooser.showOpenDialog(editor);
		if (returnVal == JFileChooser.APPROVE_OPTION) {

			try {
				content =
					LoadAction.getContentFromFileName(
						chooser.getSelectedFile().toString()).getContent();
			} catch (Throwable th) {
				return INVALID_ACTION;
			}

			boolean rtSupport = editor.isEnabledRealTimeStructureChanged();
			editor.setEnabledRealTimeStructureChanged(false);
			try {
				editor.insertText(content);
				editor.notifyStructureChanged();
			} finally {
				editor.setEnabledRealTimeStructureChanged(rtSupport);
			}

			return VALID_ACTION;
		} else
			return INVALID_ACTION;
	}
}

