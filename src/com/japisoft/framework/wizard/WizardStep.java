package com.japisoft.framework.wizard;

import javax.swing.Icon;
import javax.swing.JComponent;

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
public interface WizardStep {

	/** @return a name for this wizard */
	public String getName();
	
	/** @return a short title for this wizard. This title will appear on the left part */
	public String getShortTitle();
	
	/** @return a long title for this wizard. This title will appear on the center part */
	public String getLongTitle();

	/** This is the user interface part for the wizard step */
	public StepView getStepView();

	/** @return a small icon for the left part */
	public Icon getSmallIcon();

	/** @return a big icon for the center part */
	public Icon getLargeIcon();

	/** @return <code>true</code> if the wizard step is accessible */
	public boolean isEnabled();

	/**
	 * Called before the wizard step is started. If the step return <code>false</code> then the
	 * step will be canceled and the wizard will stay to the current one
	 * @param context The wizard step context
	 * @return <code>true</code> to start really the step or <code>false</code> to stay on the current one
	 */
	public boolean canStart( WizardStepContext context );

	/** Called when the wizard step is started, so the final user can interact with the view provided by the <code>getView</code> method 
	 * @param context Information about the wizard step
	 * @return <code>true</code> when it accept to be started else the current wizard step will be skipped
	 * */
	public boolean start( WizardStepContext context );

	/** Called before a new wizard step is started
	 * @param context Information about the wizard step
	 */
	public void stop( WizardStepContext context );

}
