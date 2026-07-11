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

package com.japisoft.editix.action.panels;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.leftpanels.LeftPanel;
import com.japisoft.framework.ui.toolkit.BrowserCaller;

public class PanelAction extends AbstractAction {

	private LeftPanel p;
	
	public boolean isPrepared() {
		return p != null;
	}
	
	protected LeftPanel getPanel() {
		return p;
	}
	
	public LeftPanel preparePanel() {
		if ( p == null ) {
			try {
				String cl = ( String )getValue( "param" );
				if ( cl == null )
					throw new RuntimeException( "No param found ???" );
				Class _c = Class.forName( cl );
				p = ( LeftPanel )_c.newInstance();
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
