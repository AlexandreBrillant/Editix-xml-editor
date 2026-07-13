package com.japisoft.editix.main.steps;

import java.io.File;

import com.japisoft.editix.ep.EPManager;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.framework.ApplicationStep;
import com.japisoft.framework.ApplicationStepAdapter;
import com.japisoft.framework.jar.JarsClassLoader;

public class EPStep extends ApplicationStepAdapter {

	private boolean epFlag = false;
	
	@Override
	public void start(String[] args) {

		if ( EPManager.instance().hasEp() ) {
			epFlag = true;
			
			File root = EPManager.instance().getEPHome();
			File libRoot = new File( root, "lib" );
			String[] librairies = libRoot.list();
			if ( librairies == null ) {
				EditixFactory.buildAndShowWarningDialog( "Can't find the Extension Pack libairies ?" );
			} else {
				try {
					JarsClassLoader loader = new JarsClassLoader();
					for ( String lib : librairies ) {
						loader.addJar( new File( libRoot, lib ) );
					}
					Class[] cls = loader.scanClasses( "Step" );
					for ( Class cl : cls  ) {
						ApplicationStep step = ( ApplicationStep )cl.newInstance();
						step.start( args );
					}
				} catch( Exception exc ) {
					EditixFactory.buildAndShowErrorDialog( "Can't use the Extension Pack : " + exc.getMessage() );
					exc.printStackTrace();
				}
			}

		}

	}

	@Override
	public void stop() {
		super.stop();
		if ( epFlag ) 
			EditixFactory.buildAndShowInformationDialog( "Extension Pack is ready" );
	}
	
}
