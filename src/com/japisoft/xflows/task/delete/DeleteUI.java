package com.japisoft.xflows.task.delete;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import com.japisoft.framework.ui.text.FileTextField;
import com.japisoft.xflows.task.AbstractTaskUI;
import com.japisoft.xflows.task.FilesTaskRunner;
import com.japisoft.xflows.task.copy.FTPFileTextFieldHandler;
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
public class DeleteUI extends AbstractTaskUI {

	public DeleteUI() {
		super( "delete" );
		jbInit();
		cbSourceType.setName( SOURCETYPE );
		tfSourcePath.setName( SOURCEPATH );
		cbSourceFilter.setName( SOURCEFILTER );

		tfSourcePath.setDirectoryMode( true );
		tfSourcePath.setFileMode( false );
	}

	public static final String SOURCETYPE = "sourcetype";
	public static final String SOURCEPATH = FilesTaskRunner.SOURCEPATH;
	public static final String SOURCEFILTER = FilesTaskRunner.SOURCEFILTER;

	JLabel lblSourceType = new JLabel();
	JComboBox cbSourceType = new JComboBox( 
			new String[] { "FILE", "FTP" } );
	JLabel lblSourcePath = new JLabel();
	FileTextField tfSourcePath = new FileTextField( null, (String)null );
	JLabel lblSourceFilter = new JLabel();
	JComboBox cbSourceFilter = XFlowsFactory.getSourceFilter( "xml" );
	GridBagLayout gridBagLayout1 = new GridBagLayout();

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

		if ( "FTP".equals( cbSourceType.getSelectedItem() ) )
			tfSourcePath.setFileTextFieldHandler(
					new FTPFileTextFieldHandler() );
		else
			tfSourcePath.setFileTextFieldHandler(
					null );		
	}
	
	public void addNotify() {
		super.addNotify();
		cbSourceType.addActionListener( this );
		tfSourcePath.setActionListener( this );
	}

	public void removeNotify() {
		super.removeNotify();
		cbSourceType.removeActionListener( this );
		tfSourcePath.setActionListener( null );
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
		if ( e.getSource() == tfSourcePath ) {

			params.setParam( "source" + FTPFileTextFieldHandler.HOST,
					( String )tfSourcePath.getClientProperty( FTPFileTextFieldHandler.HOST ) );
		
			params.setParam( "source" + FTPFileTextFieldHandler.PASSWORD,
					( String )tfSourcePath.getClientProperty( FTPFileTextFieldHandler.PASSWORD ) );

			params.setParam( "source" + FTPFileTextFieldHandler.USER,
					( String )tfSourcePath.getClientProperty( FTPFileTextFieldHandler.USER ) );

		}
	}	

	void jbInit() {
		lblSourceType.setText("Source type");
		this.setLayout(gridBagLayout1);
		lblSourcePath.setText("Source path");
		tfSourcePath.setText("");
		lblSourceFilter.setText("Source filter");
		cbSourceFilter.setEditable(true);
		
		this.add(cbSourceType, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(7, 11, 0, 307), 0, 0));
		this.add(lblSourcePath, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(
						14, 11, 0, 0), 0, 0));

		this.add(lblSourceFilter, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(
						19, 11, 0, 0), 0, 0));
		this.add(lblSourceType, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(7,
						11, 0, 0), 0, 0));
		this.add(cbSourceFilter, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(14, 11, 0, 14), 470, 0));
		this.add(tfSourcePath, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(12, 11, 0, 14), 498, 0));
	}

}
