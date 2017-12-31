package com.japisoft.xmlpad.action.file;

import java.io.File;
import javax.swing.JFileChooser;
import com.japisoft.xmlpad.action.XMLAction;

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
