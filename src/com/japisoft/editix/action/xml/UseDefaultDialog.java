package com.japisoft.editix.action.xml;

import java.awt.Dimension;
import java.io.File;

import com.japisoft.editix.toolkit.Toolkit;
import com.japisoft.editix.ui.EditixDialog;
import com.japisoft.framework.ui.text.PathBuilder;

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
public class UseDefaultDialog extends EditixDialog {
	private UseDefaultPanel panel;

	public UseDefaultDialog( 
		String title, 
		String comment, 
		String element, 
		String ext,
		PathBuilder builder ) {
		super( "Default", title, comment );
		panel = new UseDefaultPanel( element, ext, builder );
		getContentPane().add( panel );
	}

	public void setDelegateForRoots( UseDefaultRootBuilder builder ) {
		panel.setUseDefaultRootBuilder( builder );
	}
	
	protected Dimension getDefaultSize() { 
		return new Dimension( 350, 130 ); 
	}

	public void setRoot( String tag) {
		panel.tfRoot.setSelectedItem( tag );
	}
	
	public void disableRoot() {
		panel.tfRoot.setEnabled( false );
	}

	public void disableRelativePath() {
		panel.relPathCb.setSelected( false );
		panel.relPathCb.setEnabled( false );
	}
	
	public void setFileLocation( String loc ) {
		panel.ftfFile.setFilePath( loc );
	}
	
	public void setDefaultDirectoryForLocation( String defLoc ) {
		panel.ftfFile.setCurrentDirectory( defLoc );
	}
	
	public String getDefaultDirectoryForLocation() {
		return panel.ftfFile.getCurrentDirectory();
	}
	
	public String getRoot() {
		String _ = ( String )panel.tfRoot.getSelectedItem();
		if ( "".equals( _ ) )
			return null;
		return _;
	}

	private String currentXMLLocation = null;
	
	public void setXMLFileLocation( String location ) {
		currentXMLLocation = location;
		if ( currentXMLLocation == null )
			disableRelativePath();
	}

	public String getFileLocation() {
		if ( panel.relPathCb.isSelected() ) {
			if ( currentXMLLocation != null ) {
				if ( panel.ftfFile.getText() != null )
				return com.japisoft.framework.app.toolkit.Toolkit.getRelativePath(
					new File( panel.ftfFile.getText() ), new File( currentXMLLocation ) );
			}
		}
		return panel.ftfFile.getText();
	}

	public String toURILocation() {
		String s = getFileLocation();

		File f = new File( s );
		if ( f.exists() ) {
			return f.toString();
		}
		return s;
	}

}
