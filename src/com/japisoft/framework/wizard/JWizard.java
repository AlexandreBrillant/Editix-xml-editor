package com.japisoft.framework.wizard;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.EventListener;
import java.util.Hashtable;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.UIManager;
import javax.swing.event.EventListenerList;

import com.japisoft.framework.ApplicationMain;
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
public class JWizard {

//@@
	static {
		ApplicationMain.class.getName();
	}
//@@
	
	
	/** @return status for the wizard */
	public static int OK_ACTION = 0;
	/** @return status for the wizard */
	public static int CANCEL_ACTION = 1;

	private WizardStepModel model;
	private ResourceBundle labels = null;

	public JWizard() {
		if ( UIManager.get( "com.japisoft.jwizard.labelbg" ) == null )
			UIManager.put( "com.japisoft.jwizard.labelbg", Color.WHITE );

		try {
			labels = ResourceBundle.getBundle( "com/japisoft/framework/wizard/JWizard" );
		} catch( MissingResourceException exc ) {
			System.out.println( "No resource bundle found" );
		}
	}

	/** Reset the model containing a set of wizard step */
	public void setWizardStepModel( WizardStepModel model ) {
		this.model = model;
	}

	/** @return a model containing a set of wizard step. This method will never return <code>null</code> */
	public WizardStepModel getWizardStepModel() {
		if ( model == null )
			model = new DefaultWizardStepModel( this );
		return model;
	}

	private WizardView view;

	/** Reset the default WizardView by this one */
	public void setView( WizardView view ) {
		this.view = view;
	}

	/** @return the user interface part. This method will never return <code>null</code> */
	public WizardView getView() {
		if ( view == null )
			view = new DefaultWizardView( this );
		return view;
	}

	Icon image;

	/** Set a background image for the wizard steps */
	public void setImage( Icon image ) {
		this.image = image;
	}

	/** @return a background image for the wizard steps */
	public Icon getImage() {
		if ( image == null )
			return UIManager.getIcon( "com.japisoft.jwizard.image" );
		return image;
	}

	private boolean numberedStep = true;

	/** @return <code>true</code> if each step is numbered. By default <code>true</code> */
	public boolean isNumberedStep() {
		return numberedStep;
	}

	/** @param numberedStep Decide to show a number for each wizard step */
	public void setNumberedStep( boolean numberedStep ) {
		this.numberedStep = numberedStep;
	}

	private boolean wizardStepLabel = true;

	/** Show a title for the wizard step. By default <code>true</code> */
	public void setWizardStepTitle( boolean wizardStepLabel ) {
		this.wizardStepLabel = wizardStepLabel;
	}

	/** @return <code>true</code> if the wizard step has a major title */
	public boolean isWizardStepTitle() {
		return wizardStepLabel;
	}

	/** @return a label for the view part. It used a resource bundle called Bundle.properties in the same package */	
	protected String getLabel( String name, String defaultValue ) {
		try {
			String s = ( labels != null ) ? labels.getString( name ) : defaultValue;
			if ( s == null )
				s = defaultValue;
			return s;
		} catch( MissingResourceException exc ) {
			return defaultValue;
		}
	}

	void prepareWizards() {
		getView().updateView( getWizardStepModel() );

		if ( getWizardStepModel().getWizardStepCount() > 0 )
			startWizard( getWizardStepModel().getWizardStep( 0 ) );
	}

	/** Start a particular wizard step, this is called by default for the first one */
	protected void startWizard( WizardStep step ) {
		currentStep = getWizardStepModel().getWizardStepIndex( step );
		currentContext = new CustomWizardStepContext();
		activateCurrentStep();
	}

	private int currentStep = 0;
	private int lastStep = -1;

	private void stop( int step ) {
		WizardStep oldStep = getWizardStepModel().getWizardStep( step );
		notifyStopWizard( oldStep );
		oldStep.stop( currentContext );
		oldStep.getStepView().stop( currentContext );
	}

	private boolean activateCurrentStep() {
		if ( lastStep != -1 ) {
			stop( lastStep );
		}

		WizardStep step = getWizardStepModel().getWizardStep( currentStep );
		// Check if this new step accept to be started
		if ( !step.canStart( currentContext ) ) {
			if ( lastStep != -1 )
				currentStep = lastStep;
			return false;
		}

		lastStep = currentStep;
		
		// The next step and the previous step are disabled by default
		currentContext.acceptNext( false );
		currentContext.acceptPrevious( false );
		
		step.start( currentContext );
		step.getStepView().start( currentContext );
		notifyStartWizard( step );
		getView().activate( step );
		return true;
	}

	boolean preparedDialog = false;
	
	private void prepareDialog( JDialog dialog ) {
		preparedDialog = true;
		prepareWizards();
		dialog.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
		dialog.getContentPane().add( getView().getView() );
		dialog.pack();
		dialog.setModal( true );	
	}

	/**
	 * A new dialog will be created including the wizard
	 * @param owner The dialog parent owner
	 * @param title Title of the dialog
	 * @return The state of the wizard OK_ACTION or CANCEL_ACTION
	 */
	public int show( Frame owner, String title ) {
		dialog = new JDialog( owner, title );
		prepareDialog( dialog );
		dialog.setVisible( true );
		return currentState;
	}

	/**
	 * A new dialog will be created including the wizard
	 * @param owner The dialog parent owner
	 * @param title Title of the dialog
	 * @return The state of the wizard OK_ACTION or CANCEL_ACTION
	 */
	public int show( Dialog owner, String title ) {
		dialog = new JDialog( owner, title );
		return show( dialog, true );
	}

	/** 
	 * A new dialog will be created including the wizard
	 * @param title Title of the dialog
	 * @return The state of the wizard OK_ACTION or CANCEL_ACTION
	 */
	public int show( String title ) {
		dialog = new JDialog( (Frame)null, title );
		return show( dialog, true );
	}
	
	/**
	 * No new dialog will be created. This is the dialog from the first parameter
	 * that will be shown
	 * @param dialog A dialog that will contain the Wizard
	 * @param preparedDialog Decide to include the wizard view automatically
	 * @return The state of the wizard OK_ACTION or CANCEL_ACTION 
	 */
	public int show( JDialog dialog, boolean preparedDialog ) {
		this.dialog = dialog;
		if ( preparedDialog )
			prepareDialog( dialog );
		dialog.setVisible( true );
		return currentState;
	} 
	
	private Dimension dim = null;

	/** Force a default dimension for the wizard step view */
	public void setViewPreferredSize( Dimension dim ) {
		this.dim = dim;
	}

	/** @return a default dimension for the wizard step view */
	public Dimension getViewPreferredSize() {
		if ( dim == null )
			dim = new Dimension( 400, 200 );
		return dim;
	}

	private Dimension dim2 = null;

	/** Set the preferred size for the left labels with the current wizard step */	
	public void setLabelsPreferredSize( Dimension dim ) {
		this.dim2 = dim;
	}

	/** @return the preferredSize for the left labels */
	public Dimension getLabelsPreferredSize() {
		if ( dim2 == null )
			dim2 = new Dimension( 100, 200 );
		return dim2;
	}

	private Color colorbg = null;

	/** @return the color background for the left labels */
	public Color getStepLabelsBackground() {
		if ( colorbg == null )
			return UIManager.getColor( "com.japisoft.jwizard.labelbg" );
		return colorbg;
	}

	/** @return a color background for the left labels. By default <code>WHITE</code> */
	public void setStepLabelsBackground( Color color ) {
		this.colorbg = color;
	}

	void actionNext() {
		currentStep++;
		activateCurrentStep();
	}

	void actionPrevious() {
		currentStep--;
		activateCurrentStep();
	}

	private int currentState = CANCEL_ACTION;
	private JDialog dialog = null;

	void actionOk() {
		currentState = OK_ACTION;
		
		// Stop the last one
		stop( currentStep );
		
		if ( dialog != null)
			dialog.setVisible( false );
		dialog = null;
	}

	void actionCancel() {
		currentState = CANCEL_ACTION;
		if ( dialog != null)
			dialog.setVisible( false );
		dialog = null;
	}
	
	/** 
	 * This method is useful to get all the sharedData from each Wizard step
	 * @return the current wizard context */
	public WizardStepContext getWizardContext() {
		return currentContext;
	}

	/** Free all inner resource, it must be called for helping the garbage collector to do the best job. Note that you
	 * mustn't reuse your wizard instance after calling this method ! */
	public void dispose() {
		currentContext = null;
		dialog = null;
	}

	EventListenerList listeners = null;

	/** Add a listener for the start / stop wizard step */
	public void addWizardListener( WizardListener listener ) {
		if ( listeners == null )
			listeners = new EventListenerList();
		listeners.add( WizardListener.class, listener );
	}

	/** Remove a listener for the start / stop wizard step */
	public void removeWizardListener( WizardListener listener ) {
		if ( listeners != null )
			listeners.remove( WizardListener.class, listener );
	}

	void notifyStartWizard( WizardStep step ) {
		if  ( listeners != null ) {
			EventListener[] el = listeners.getListeners( WizardListener.class );
			for ( int i = 0; i < el.length; i++ ) {
				( ( WizardListener )el[ i ] ).startWizardStep( step );
			}
		}
	}

	void notifyStopWizard( WizardStep step ) {
		if ( listeners != null ) {
			EventListener[] el = listeners.getListeners( WizardListener.class );
			for ( int i = 0; i < el.length; i++ ) {
				( ( WizardListener )el[ i ] ).stopWizardStep( step );
			}
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	WizardStepContext currentContext = null;
	
	class CustomWizardStepContext implements WizardStepContext {
	
		private Hashtable ht = null;
	
		public void acceptNext( boolean accept ) {
			getView().setEnabledNextAction( accept );
		}

		public void goTo(WizardStep step) {
			startWizard( step );
		}		

		public void acceptPrevious( boolean accept ) {
			getView().setEnabledPreviousAction( accept );
		}

		public Object getSharedData(String key) {
			if ( ht == null )
				return null;
			return ht.get( key );
		}

		public Object getSharedData(String key, Object defaultValue) {
			Object o = getSharedData( key );
			if ( o == null )
				return defaultValue;
			return o;
		}
		
		public WizardStep getNextWizardStep() {
			if ( currentStep + 1 < getWizardStepModel().getWizardStepCount() ) {
				return getWizardStepModel().getWizardStep( currentStep + 1 );
			}
			return null;
		}

		public WizardStep getPreviousWizardStep() {
			if ( currentStep - 1 >= 0 )
				return getWizardStepModel().getWizardStep( currentStep - 1 );
			return null;
		}

		public JWizard getSource() {
			return JWizard.this;
		}

		public void setSharedData(String key, Object data) {
			if ( ht == null )
				ht =  new Hashtable();
			if ( data == null )
				ht.remove( key );
			else
				ht.put( key, data );
		}

	}

}
