package com.japisoft.xflows.task.ui;

import java.io.File;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.preferences.Preferences;

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
public class XFlowsFactory {

	public static void buildAndShowErrorDialog(String message) {
		JOptionPane.showMessageDialog(ApplicationModel.MAIN_FRAME, message, "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	public static String buildAndShowInputDialog(String title) {
		return JOptionPane.showInputDialog(ApplicationModel.MAIN_FRAME, title);
	}

	public static void buildAndShowInformationDialog(String message) {
		JOptionPane.showMessageDialog(ApplicationModel.MAIN_FRAME, message, "Info",
				JOptionPane.INFORMATION_MESSAGE);
	}

	public static int buildAndShowChoiceDialog(String message) {
		return JOptionPane.showConfirmDialog(ApplicationModel.MAIN_FRAME, message,
				"Choice", JOptionPane.YES_NO_OPTION);
	}

	/** @return a FileChooser for opening a project file */
	public static JFileChooser buildProjectFileChooser() {
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new FileFilter() {
			public String getDescription() {
				return "*.xfl (XFlows project file)";
			}
			public boolean accept(File f) {
				return (
					f.isDirectory()
						|| f.toString().toLowerCase().endsWith(".xfl" ) );
			}
		});

		String currentDir = 
			Preferences.getPreference( "defaultpath", "project", ( String )null );

		if ( currentDir != null )
			fc.setCurrentDirectory( new File( currentDir ) );

		return fc;
	}

	public static JComboBox getSourceFilter( String type ) {

		String[] filters = null;

		if ( "csv".equals( type ) ) {
			filters = new String[] {
					"(.*).csv",
					"(.*)\\.(.*)"					
			};
		} else
		if ( "htmlxml".equals( type ) ) {
			filters = new String[] { 
					"(.*).html",
					"(.*).htm",
					"(.*)\\.(.*)"
			};
		} else
			filters = new String[] {
				"(.*).xml",
				"(.*)\\.(.*)"
			};

		JComboBox combo = new JComboBox(
				filters
		);
		return combo;
	}

	public static JComboBox getTargetName( String type ) {
		String [] filters = null;
		if ( "xml".equals( type ) ) {
			filters = new String[] {
				"$1.xml"
			};
		} else
			filters = new String[] { 
				"$1.html",
				"$1.htm",
				"$1.xml" };
		JComboBox combo = new JComboBox( filters );
		return combo;
	}

	public static ImageIcon getImageIcon(String resource) {
		URL url = ClassLoader.getSystemClassLoader().getResource(resource);
		if (url != null)
			return new ImageIcon(url);
		else
			com.japisoft.framework.toolkit.Logger.addWarning("Can't find " + resource);
		return null;
	}
	
}
