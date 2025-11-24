package com.japisoft.editix.main.steps;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.ApplicationStepAdapter;

public class Release2026Step extends ApplicationStepAdapter {

	@Override
	public void start(String[] args) {
		File regFile = EditixApplicationModel.getAppFile( "editix20.reg" );
		if ( !regFile.exists() ) {
			File regFile2 = EditixApplicationModel.getAppFile( "editix19.reg" );
			if ( regFile2.exists() ) {
				try {
					Path source = Paths.get( regFile2.getAbsolutePath() );
					Path destination = Paths.get( regFile.getAbsolutePath() );
					Files.copy( source, destination );
				} catch( Exception exc ) {
					ApplicationModel.debug( exc );
				}
			}
		}
	}


}
