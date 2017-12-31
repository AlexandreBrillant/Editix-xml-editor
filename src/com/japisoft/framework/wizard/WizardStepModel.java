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
public interface WizardStepModel {

	/** Add a new wizard step */
	public void addWizardStep( WizardStep step );

	/** Insert a wizard step */
	public void insertWizardStep( int index, WizardStep step );

	/** Remove a wizard step */
	public void removeWizardStep( WizardStep step );

	/** @return an index starting from 0 for this wizard step */
	public int getWizardStepIndex( WizardStep step );

	/** @return a wizard by its name */
	public WizardStep getWizardByName( String name );

	/** @return the number of wizard step */
	public int getWizardStepCount();

	/** @return a wizard step at this index from 0 to the maximum size - 1 */
	public WizardStep getWizardStep( int index );

}
