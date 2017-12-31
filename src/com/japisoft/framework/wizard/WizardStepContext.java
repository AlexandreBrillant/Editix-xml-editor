package com.japisoft.framework.wizard;

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
public interface WizardStepContext {

	/** @return the main component, user can access to other wizard step using the <code>WizardStepModel</code> */
	public JWizard getSource();

	/** @return the previous step or <code>null</code> for the first wizard step */
	public WizardStep getPreviousWizardStep();

	/** @return the next step or <code>null</code> for the last wizard step */
	public WizardStep getNextWizardStep();

	/** Force the wizard to go the following step */
	public void goTo( WizardStep step );
	
	/** Notify the main wizard component that the next step is accessible. By default the next
		step is never accessible without calling acceptNext( true ) */
	public void acceptNext( boolean accept );

	/** Notify the main wizard component that the previous step is accessible. By default the
		previous step is always accessible */ 
	public void acceptPrevious( boolean accept );

	/** Store a data, this value will be available for all the wizard next step and user can retreive
		these values on the JWizard component */
	public void setSharedData( String key, Object data );

	/** Get a value. This value has been set by previous wizard step */
	public Object getSharedData( String key );

	/** Get a value. This value has been set by previous wizard step
	 * @param key the Shared data key
	 * @param defaultValue the value returned if the key is unknown
	 *  */
	public Object getSharedData( String key, Object defaultValue );

}
