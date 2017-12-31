package com.japisoft.xflows.task.copy;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import com.japisoft.framework.ui.text.FileTextField;
import com.japisoft.xflows.task.AbstractTaskUI;
import com.japisoft.xflows.task.FilesTaskRunner;
import com.japisoft.xflows.task.ui.XFlowsFactory;

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
public class CopyUI extends AbstractTaskUI implements ActionListener {

	public static final String SOURCETYPE = "sourcetype";
	public static final String SOURCEPATH = FilesTaskRunner.SOURCEPATH;
	public static final String SOURCEFILTER = FilesTaskRunner.SOURCEFILTER;
	public static final String TARGETTYPE = "targettype";
	public static final String TARGETPATH = FilesTaskRunner.TARGETPATH;

	JLabel lblSourceType = new JLabel();
	JComboBox cbSourceType = new JComboBox( 
			new String[] { "FILE", "FTP" } );
	JLabel lblSourcePath = new JLabel();
	FileTextField tfSourcePath = new FileTextField( ( String )null, ( String )null );
	JLabel lblSourceFilter = new JLabel();
	JComboBox cbSourceFilter = XFlowsFactory.getSourceFilter( "xml" );
	JLabel lblTargetType = new JLabel();
	JComboBox cbTargetType = new JComboBox(
			new String[] { "FILE", "FTP" }			
			);
	JLabel lblTargetPath = new JLabel();
	FileTextField tfTargetPath = new FileTextField( null, ( String )null );
	GridBagLayout gridBagLayout1 = new GridBagLayout();

	public CopyUI() {
		super( "copy" );
		try {

			jbInit();
			cbSourceType.setName( SOURCETYPE );
			tfSourcePath.setName( SOURCEPATH );
			cbSourceFilter.setName( SOURCEFILTER );
			cbTargetType.setName( TARGETTYPE );
			tfTargetPath.setName( TARGETPATH );

			tfSourcePath.setDirectoryMode( true );
			tfTargetPath.setDirectoryMode( true );
			tfSourcePath.setFileMode( false );
			tfTargetPath.setFileMode( false );
			tfTargetPath.setOpenedMode( false );

		} catch ( Exception ex ) {
			ex.printStackTrace();
		}
	}

	protected void prepareComponents( Container container ) {
		super.prepareComponents( container );

		tfSourcePath.putClientProperty( 
				FTPFileTextFieldHandler.HOST, 
				params.getParamValue( "source" + FTPFileTextFieldHandler.HOST ) );

		tfSourcePath.putClientProperty(
				FTPFileTextFieldHandler.PASSWORD,
				params.getParamValue( "source" + FTPFileTextFieldHandler.PASSWORD )
		);

		tfSourcePath.putClientProperty(
				FTPFileTextFieldHandler.USER,
				params.getParamValue( "source" + FTPFileTextFieldHandler.USER )
				 );

		tfTargetPath.putClientProperty( 
				FTPFileTextFieldHandler.HOST, 
				params.getParamValue( "target" + FTPFileTextFieldHandler.HOST ) );

		tfTargetPath.putClientProperty(
				FTPFileTextFieldHandler.PASSWORD,
				params.getParamValue( "target" + FTPFileTextFieldHandler.PASSWORD )
		);

		tfTargetPath.putClientProperty(
				FTPFileTextFieldHandler.USER,
				params.getParamValue( "target" + FTPFileTextFieldHandler.USER )
				 );

		if ( "FTP".equals( params.getParamValue( SOURCETYPE ) ) )
			tfSourcePath.setFileTextFieldHandler(
					new FTPFileTextFieldHandler() );
		else
			tfSourcePath.setFileTextFieldHandler(
					null );

		if ( "FTP".equals( params.getParamValue( TARGETTYPE ) ) )
			tfTargetPath.setFileTextFieldHandler(
					new FTPFileTextFieldHandler() );
		else
			tfTargetPath.setFileTextFieldHandler(
					null );			

	}

	public void addNotify() {
		super.addNotify();
		cbSourceType.addActionListener( this );
		cbTargetType.addActionListener( this );
		tfSourcePath.setActionListener( this );
		tfTargetPath.setActionListener( this );
	}

	public void removeNotify() {
		super.removeNotify();
		cbSourceType.removeActionListener( this );
		cbTargetType.removeActionListener( this );
		tfSourcePath.setActionListener( null );
		tfTargetPath.setActionListener( this );
	}

	public void actionPerformed( ActionEvent e ) {
		super.actionPerformed( e );
		if ( e.getSource() == cbSourceType ) {
			if ( "FTP".equals( cbSourceType.getSelectedItem() ) )
				tfSourcePath.setFileTextFieldHandler(
						new FTPFileTextFieldHandler() );
			else
				tfSourcePath.setFileTextFieldHandler(
						null );
		} else
		if ( e.getSource() == cbTargetType ) {
			if ( "FTP".equals( cbTargetType.getSelectedItem() ) )
				tfTargetPath.setFileTextFieldHandler(
						new FTPFileTextFieldHandler() );
			else
				tfTargetPath.setFileTextFieldHandler(
						null );			
		} else
		if ( e.getSource() == tfSourcePath ) {

			params.setParam( "source" + FTPFileTextFieldHandler.HOST,
					( String )tfSourcePath.getClientProperty( FTPFileTextFieldHandler.HOST ) );
		
			params.setParam( "source" + FTPFileTextFieldHandler.PASSWORD,
					( String )tfSourcePath.getClientProperty( FTPFileTextFieldHandler.PASSWORD ) );

			params.setParam( "source" + FTPFileTextFieldHandler.USER,
					( String )tfSourcePath.getClientProperty( FTPFileTextFieldHandler.USER ) );

		} else
		if ( e.getSource() == tfTargetPath ) {

			params.setParam( "target" + FTPFileTextFieldHandler.HOST,
					( String )tfTargetPath.getClientProperty( FTPFileTextFieldHandler.HOST ) );
		
			params.setParam( "target" + FTPFileTextFieldHandler.PASSWORD,
					( String )tfTargetPath.getClientProperty( FTPFileTextFieldHandler.PASSWORD ) );

			params.setParam( "target" + FTPFileTextFieldHandler.USER,
					( String )tfTargetPath.getClientProperty( FTPFileTextFieldHandler.USER ) );

		}

	}	

	void jbInit() throws Exception {
		lblSourceType.setText("Source type");
		this.setLayout(gridBagLayout1);
		lblSourcePath.setText("Source path");
		tfSourcePath.setText("");
		lblSourceFilter.setText("Source filter");
		cbSourceFilter.setEditable(true);
		lblTargetType.setText("Target type");
		lblTargetPath.setText("Target path");
		tfTargetPath.setText("");
		
		this.add(cbSourceType, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(7, 11, 0, 307), 0, 0));
		this.add(lblSourcePath, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(
						14, 11, 0, 0), 0, 0));
		this.add(lblTargetType, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(
						22, 11, 0, 0), 0, 0));
		this.add(lblSourceFilter, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(
						19, 11, 0, 0), 0, 0));
		this.add(lblSourceType, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(7,
						11, 0, 0), 0, 0));
		this.add(lblTargetPath, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(
						19, 11, 22, 0), 0, 0));
		this.add(tfTargetPath, new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(17, 11, 22, 14), 498, 0));
		this.add(cbSourceFilter, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(14, 11, 0, 14), 470, 0));
		this.add(cbTargetType, new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(17, 11, 0, 307), 0, 0));
		this.add(tfSourcePath, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(12, 11, 0, 14), 498, 0));
	}

}
