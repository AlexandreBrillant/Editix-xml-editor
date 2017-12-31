package com.japisoft.editix.action.file;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.application.descriptor.ActionModel;
import com.japisoft.editix.action.search.BookmarkAction;
import com.japisoft.editix.action.search.RemoveBookmarksAction;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.xmlpad.IXMLPanel;
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
public class CloseAction extends AbstractAction {

	public void actionPerformed(ActionEvent e) {
		XMLContainer container = null;

		if ( e.getSource() instanceof IXMLPanel ) {
			container = ( ( IXMLPanel )e.getSource() ).getMainContainer();
		} else
			container = EditixFrame.THIS.getSelectedContainer();

		if ( container != null ) {
			if ( container.getEditor().isDocumentModified() ) {
				if ( EditixFactory.buildAndShowChoiceDialog( "Save this document before closing ?" ) == JOptionPane.YES_OPTION ) {
					ActionModel.activeActionById( "save", e );				
				} else {
//					if ( EditixFactory.buildAndShowChoiceDialog( "Are you sure to close it ?" ) == JOptionPane.NO_OPTION )
//						return;
				}
			}
			
			String f = container.getCurrentDocumentLocation();
			String encoding = container.getDocumentInfo().getEncoding();
			String type = container.getDocumentInfo().getType();
			
			BookmarkAction.storeBookmarksInContainer( container );
			RemoveBookmarksAction.cleanBookmarks();
			OpenAction.synchronizedRecentFileMenu( container, f, type, encoding );
			EditixFrame.THIS.closeContainer( container.getView() );
			
			ApplicationModel.fireApplicationValue( "close", container );
		}
	}

}
