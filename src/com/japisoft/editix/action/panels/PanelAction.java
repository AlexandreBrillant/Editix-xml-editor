package com.japisoft.editix.action.panels;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.panels.Panel;
import com.japisoft.framework.ui.toolkit.BrowserCaller;
import com.japisoft.p3.Manager;

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
public class PanelAction extends AbstractAction {

	private Panel p;
	
	public boolean isPrepared() {
		return p != null;
	}
	
	protected Panel getPanel() {
		return p;
	}
	
	public Panel preparePanel() {
		if ( p == null ) {
			try {
				String cl = ( String )getValue( "param" );
				if ( cl == null )
					throw new RuntimeException( "No param found ???" );
				Class _c = Class.forName( cl );
				p = ( Panel )_c.newInstance();
				p.setIcon( ( Icon )getValue( SMALL_ICON ) );
				p.setId( (String)getValue( "id" ) );
				p.init();
			} catch ( ClassNotFoundException e1 ) {
				e1.printStackTrace();
			} catch ( InstantiationException e1 ) {
				e1.printStackTrace();
			} catch ( IllegalAccessException e1 ) {
				e1.printStackTrace();
			}
		}	
		return p;
	}

	protected void stop() {
		if ( p != null )
			p.stop();		
	}

	protected boolean alwaysShown = false;

	public void setForceAlwaysShown( boolean alwaysShown ) {
		this.alwaysShown = alwaysShown;
	}
	
	protected Object initParameter = null; 

	public void setInitParameter( Object initParameter ) {
		this.initParameter = initParameter;
	}

	public void actionPerformed(ActionEvent e) {

		showHide();
		
	}

	
	protected void showHide() {

		p = preparePanel();
		
		if ( p != null ) {
			
			p.setParams( ( String )getValue( "param2" ) );

			if ( alwaysShown )
				p.showPanel();
			else
				p.showHidePanel();
		}
				
	}
	
	protected void hide() {

		p = preparePanel();
		
		if ( p != null ) {
			p.stop();
			//if ( p.isShown() )
				p.hidePanel();
		}
		
		
	}
	
}
