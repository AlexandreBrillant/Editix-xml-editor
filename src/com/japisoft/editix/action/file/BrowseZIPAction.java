// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2025 Alexandre Brillant
// 
// For non-commercial usage :
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
// For commercial use or integration into proprietary software :
// A commercial license is required. Visit https://www.editix.com for details.

package com.japisoft.editix.action.file;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;

import com.japisoft.editix.action.panels.PanelAction;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.editix.ui.leftpanels.universalbrowser.ZIPBrowserPanel;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.ui.toolkit.BrowserCaller;
import com.japisoft.p3.Manager;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public class BrowseZIPAction extends PanelAction {

	static class CustomFileFilter extends FileFilter {
		
		private String description;
		private ArrayList exts = null;
		
		CustomFileFilter( String extDesc ) throws Throwable {
			StringTokenizer st = new StringTokenizer( extDesc, "-" );
			String title = st.nextToken();
			description = title.substring( 1, title.length() - 1 );
			while ( st.hasMoreTokens() ) {
				if ( exts == null )
					exts = new ArrayList();
				String star = "." + st.nextToken().toLowerCase(); 
				exts.add( star );
				description += " (*" + star + ")";
			}
		}

		public boolean accept(File f) {
			if ( f.isDirectory() )
				return true;
			String file = f.toString().toLowerCase();
			if ( exts != null )
				for ( int i = 0; i < exts.size(); i++ )
					if ( file.endsWith( ( String )exts.get( i ) ) )
						return true;
			return false;
		}
		public String getDescription() {
			return description;
		}
	}

	public static void addFileFilterForZipArchives( JFileChooser chooser, boolean selectFirstOne ) {
		String zipFiles = 
			Preferences.getPreference( "zipbrowser", "zip extensions", "[Default archive]-zip;[Java Files]-jar-sar-ear-par-ejb3-war;[Microsoft Office documents]-docx-xlsx-pptx;[Open office documents]-odt-ods-odp-odg-odf" );

		StringTokenizer st = new StringTokenizer( zipFiles, ";" );
		FileFilter firstOne = null;
		String val = null;
		while ( st.hasMoreTokens() ) {
			try {
				val = st.nextToken();
				CustomFileFilter cff = new CustomFileFilter( val );
				chooser.addChoosableFileFilter( cff );
				if ( firstOne == null )
					firstOne = cff;
			} catch (Throwable e1) {
				EditixFactory.buildAndShowWarningDialog( "Wrong zipbrowser preference value :" + val + " ??" );
			}
		}
		
		if ( firstOne != null && selectFirstOne )
			chooser.setFileFilter( firstOne );
	}

	public static boolean isFileArchive( String path ) {
		int i = path.lastIndexOf( "." );
		if ( i == -1 )
			return false;
		String ext = path.substring( i + 1 ).toLowerCase();
		String zipFiles = 
			Preferences.getPreference( "zipbrowser", "zip extensions", "[Default archive]-zip;[Java Files]-jar-sar-ear-par-ejb3-war;[Microsoft Office documents]-docx-xslx-pptx;[Open office documents]-odt-ods-odp-odg-odf" );
		StringTokenizer st = new StringTokenizer( zipFiles.toLowerCase(), ";-" );
		while ( st.hasMoreTokens() ) {
			if ( ext.equals( st.nextToken() ) )
				return true;
		}
		return false;
	}

	public void actionPerformed(ActionEvent e) {

		if ( Manager.isFree() ) {

			EditixFactory.buildAndShowInformationDialog( "This action is not available inside the Free Edition.\nPlease look at http://www.editix.com" );
			BrowserCaller.displayURL( "http://www.editix.com" );
			return;
			
		}			

		//���		
		
		JFileChooser chooser = new JFileChooser();
		addFileFilterForZipArchives( chooser, true );
		
		if ( chooser.showOpenDialog( EditixFrame.THIS ) != 
			JFileChooser.APPROVE_OPTION ) {			
			return;
		}

		File selection = chooser.getSelectedFile();
		browse( selection );
		
		//��
	}
	
	public void browse( File selection ) {

		try {
			FileSystemManager fsManager = VFS.getManager();

			// VFS URI format
			String uri = 
				"zip" + ":file://" + selection.toString();
			
			( ( ZIPBrowserPanel )preparePanel() ).browse(
					uri,
					null,
					null );

		} catch (FileSystemException e1) {
			EditixFactory.buildAndShowErrorDialog( "Inner Error : " + e1.getMessage() );
			return;
		}

		alwaysShown = true;
		super.actionPerformed( null );
	}

}

