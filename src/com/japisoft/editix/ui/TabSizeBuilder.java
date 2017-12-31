package com.japisoft.editix.ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

import com.japisoft.framework.application.descriptor.helpers.MenuBuilderDelegate;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.xml.Encoding;

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
public class TabSizeBuilder implements MenuBuilderDelegate {

	public void build(JMenu menu) {
		
		menu.setEnabled( true );
		String[] encoding = Encoding.XML_ENCODINGS;
		ButtonGroup bg = new ButtonGroup();
		
		int tabSize = Preferences.getPreference( "file", "tab-size", 4 );

		for ( int i = 1; i < 8; i++ ) {
			ChangeTabSizeAction action = new ChangeTabSizeAction( i );
			JRadioButtonMenuItem item = new JRadioButtonMenuItem( action );
			if ( i == tabSize )
				item.setSelected( true );
			bg.add( item );
			menu.add( item );
		}

	}

	class ChangeTabSizeAction extends AbstractAction {
		int size;
		ChangeTabSizeAction( int size ) {
			this.size = size;
			putValue( Action.NAME, "" + size + " whitespace" + ( ( size > 1 ) ? "s":"" ) );
		}
		public void actionPerformed(ActionEvent e) {
			Preferences.setPreference( "file", "tab-size", size );
			EditixFactory.buildAndShowInformationDialog( "Please restart EditiX for applying" );
		}
	}

}
