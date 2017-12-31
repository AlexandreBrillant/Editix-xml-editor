package com.japisoft.xmlpad.toolkit;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.japisoft.xmlpad.IView;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.action.ActionModel;
import com.japisoft.xmlpad.action.file.LoadAction;

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
public class MultipleDocumentEditor extends JFrame implements ChangeListener {
	private JToolBar toolBar;
	private JTabbedPane tabbedPane;

	private MultipleDocumentEditor() {
		super( "Multiple editor" );
		setSize( 550, 400 );
		setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		getContentPane().add( toolBar = new JToolBar(), BorderLayout.NORTH );
		getContentPane().add( tabbedPane = new JTabbedPane(), BorderLayout.CENTER );
		tabbedPane.addChangeListener( this );
	}

	public void stateChanged(ChangeEvent e) {
		JComponent component = (JComponent)tabbedPane.getSelectedComponent();
		if ( component instanceof IView ) {
			IView view = ( IView )component;
			XMLContainer container = view.getContainer();
			ActionModel.resetActionState( container.getEditor(), container );
		}
	}

	private ArrayList containers;

	/** @return all current XMLContainers */
	public ArrayList getXMLContainers() {
		return containers;
	}

	/** return a MultipleDocumentEditor editing all provided file path. If one file path is bad a
	 * FileNotFoundException is thrown
	 * @param files List of file path
	 * @return null if no files is found or a new <code>MultipleDocumentEditor</code> frame */
	public static MultipleDocumentEditor showEditor( String[] files ) throws FileNotFoundException, IOException {
		MultipleDocumentEditor frame = new MultipleDocumentEditor();
		ActionModel.buildToolBar( frame.toolBar );
		frame.containers = new ArrayList();

		// Build all containers
		for ( int i = 0; i < files.length; i++ ) {
			XMLContainer container = new XMLContainer( true );
			container.setToolBarAvailable( false );
			//ActionModel.buildPopupMenu( container.getCurrentPopup() );
			frame.containers.add( container );
			try {
				LoadAction.loadInBuffer( container, files[ i ] );
			} catch( Throwable th ) {
				frame.dispose();
				if ( th instanceof FileNotFoundException )
					throw (FileNotFoundException)th;
				else
					throw new IOException( th.getMessage() );
			}
			String tabName = files[ i ];
			int ii = tabName.lastIndexOf( "/" );
			if ( ii == -1 )
				ii = tabName.lastIndexOf( "\\" );
			if ( ii > -1 ) {
				tabName = tabName.substring( ii + 1 );			
			}
			frame.tabbedPane.addTab( tabName, container.getView() );
		}
		frame.setTitle( "JXMLPad" );
		frame.setVisible( true );
		return frame;
	}

	/** Dispose all XMLContainer */
	public void dispose() {
		if ( containers == null )
			return;
		for ( int i = 0; i < containers.size(); i++ ) {
			XMLContainer container = ( XMLContainer )containers.get( i );
			container.dispose();
		}
	}

	public static MultipleDocumentEditor showEditor( File filePath, FileFilter filter ) throws FileNotFoundException, IOException {
		String[] files = filePath.list();
		if ( files == null || 
				files.length == 0 )
			return null;
		ArrayList l = new ArrayList();
		for ( int i = 0; i < files.length; i++ ) {
			File f = new File( filePath, files[ i ] );
			if ( ( filter == null || filter.accept( f ) ) && f.isFile() )
				l.add( f.toString() );
		}
		if ( l.size() == 0 )
			return null;
		String[] res = new String[ l.size() ];
		for ( int i = 0; i < res.length; i++ )
			res[ i ] = ( String )l.get( i );
		return showEditor( res );
	}

	public static void main( String[] args ) throws Throwable {
		MultipleDocumentEditor.showEditor( new File( "/home/japisoft/xml-samples" ), null );
	}

}
