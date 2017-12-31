package com.japisoft.framework.wizard;

import java.util.ArrayList;

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
public class DefaultWizardStepModel implements WizardStepModel {

	private ArrayList content;
	private JWizard wizard;

	public int getWizardStepIndex(WizardStep step) {
		if ( content == null )
			return -1;
		return content.indexOf( step );
	}

	public DefaultWizardStepModel( JWizard wizard ) {
		this.wizard = wizard;
	}

	public void addWizardStep(WizardStep step) {
		if ( content == null )
			content = new ArrayList();
		content.add( step );
	}

	public void insertWizardStep(int index, WizardStep step) {
		if ( content == null )
			content = new ArrayList();
		content.add( index, step );
	}

	public void removeWizardStep(WizardStep step) {
		if ( content != null ) {
			content.remove( step );
		}
	}

	public WizardStep getWizardByName(String name) {
		if ( name == null )
			throw new RuntimeException( "Illegal null name" );
		if ( content != null ) {
			for ( int i = 0; i < content.size(); i++ ) {
				WizardStep ws = ( WizardStep )content.get( i );
				if ( name.equals( ws.getName() ) )
					return ws;
			}
		}
		return null;
	}

	public WizardStep getWizardStep(int index) {
		if ( content == null )
			return null;
		return ( WizardStep )content.get( index );
	}

	public int getWizardStepCount() {
		if ( content == null )
			return 0;
		return content.size();
	}

}
