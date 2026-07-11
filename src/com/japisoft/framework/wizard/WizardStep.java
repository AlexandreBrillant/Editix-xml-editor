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

package com.japisoft.framework.wizard;

import javax.swing.Icon;
import javax.swing.JComponent;

/**
 * Step for a wizard. This wizard step is stored inside a WizardStepModel. It
 * gives informations about the wizard properties and order.
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @see JWizard
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
