package com.japisoft.editix.action.file.project;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.application.descriptor.InterfaceBuilder;
import com.japisoft.framework.application.descriptor.helpers.MenuBuilderDelegate;

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
public class RecentFileMenuBuilder implements MenuBuilderDelegate {
	
	public RecentFileMenuBuilder() {
	}
	
	public void build( JMenu menu ) {
		menu.removeAll();
		InterfaceBuilder ib = new InterfaceBuilder();
		try {
			for ( int i = 0; i < RecentProjectManager.getInstance().getItemCount(); i++ ) {
				menu.add( new LoadProjectAction( RecentProjectManager.getInstance().getItem( i ) ) );
			}
			menu.setEnabled( RecentProjectManager.getInstance().getItemCount() > 0 );			
		} catch ( Throwable e ) {
		}
		
	}
	
	class LoadProjectAction extends AbstractAction {
		public LoadProjectAction( String location ) {
			putValue( Action.NAME, location );
		}
		public void actionPerformed( ActionEvent e ) {
			ApplicationModel.fireApplicationValue( 
				"lastProject", 
				( String )getValue( Action.NAME ) 
			);
		}
	}

}
