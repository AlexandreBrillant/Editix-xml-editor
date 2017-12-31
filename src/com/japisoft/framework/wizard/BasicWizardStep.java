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
public class BasicWizardStep implements WizardStep {

	private String name;
	private String shortTitle;
	private String longTitle;
	private Icon smallIcon;
	private Icon largeIcon;
	private StepView view;

	private BasicWizardStep() {}

	/** @param name Name of this wizard */
	public BasicWizardStep( String name ) {
		this.name = name;
	}

	/** 
	 * @param name Name of this wizard
	 * @param shortTitle Label for the left part, it can't be <code>null</code>
	 * @param longTitle Label for the top part
	 * @param view Main wizard component */
	public BasicWizardStep( 
			String name, 
			String shortTitle, 
			String longTitle, 
			StepView view ) {
		this( name );
		this.view = view;
		this.shortTitle = shortTitle;
		this.longTitle = longTitle;
	}

	/** 
	 * @param name Name of this wizard
	 * @param shortTitle Label for the left part, it can't be <code>null</code>
	 * @param longTitle Label for the top part
	 * @param minorIcon Step icon
	 * @param majorIcon main step view icon
	 * @param view Main wizard component */
	public BasicWizardStep( 
			String name, 
			String shortTitle, 
			String longTitle, 
			Icon smallIcon,
			Icon largeIcon,
			StepView view ) {
		this( name );
		this.view = view;
		this.smallIcon = smallIcon;
		this.largeIcon = largeIcon;
		this.shortTitle = shortTitle;
		this.longTitle = longTitle;
	}

	public String getName() {
		return name;
	}

	private boolean enabled = true;

	public boolean isEnabled() {
		return enabled;
	}

	/** Enabled or disable this wizard step. A non enabled wizard will be skipped */
	public void setEnabled( boolean enabled ) {
		this.enabled = enabled;
	}

	public void setShortTitle( String title ) {
		this.shortTitle = title;
	}

	public String getShortTitle() {
		return shortTitle;
	}

	public String getLongTitle() {
		return longTitle;
	}

	public void setLongTitle( String title ) {
		this.longTitle = title;
	}

	public StepView getStepView() {
		return view;
	}

	public void setView( StepView component ) {
		this.view = component;
	}

	public Icon getSmallIcon() {
		return smallIcon;
	}

	public void setSmallIcon( Icon icon ) {
		this.smallIcon = icon;
	}

	public Icon getLargeIcon() {
		return largeIcon;
	}

	public void setLargeIcon( Icon icon ) {
		this.largeIcon = icon;
	}

	protected WizardStepContext currentContext;
	
	private boolean acceptNext = true;

	/** Decide to accept the next action automatically. By default <code>true</code> */
	public void setAcceptNextActionByDefault( boolean acceptNext ) {
		this.acceptNext = acceptNext;
	}

	/** @return the default value for accepting a next step. By default <code>true</code> meaning the Next button will by activated */
	protected boolean mustAcceptNextByDefault() {
		return acceptNext;
	}

	private boolean acceptPrevious = true;

	/** Decide to accept the previous action automatically. By default <code>true</code> */
	public void setAcceptPreviousActionByDefault( boolean acceptPrevious ) {
		this.acceptPrevious = acceptPrevious;
	}

	/** @return the default value for accepting a next step. By default <code>true</code> meaning the Next button will by activated */
	protected boolean mustAcceptPreviousByDefault() {
		return acceptPrevious;
	}

	/** @return a default <code>true</code> value */
	public boolean canStart(WizardStepContext context) {
		return true;
	}

	public boolean start( WizardStepContext context ) {
		currentContext = context;
		context.acceptNext( mustAcceptNextByDefault() );
		context.acceptPrevious( mustAcceptPreviousByDefault() );
		return isEnabled();
	}

	public void stop( WizardStepContext context ) {
		currentContext = context;
	}

}
