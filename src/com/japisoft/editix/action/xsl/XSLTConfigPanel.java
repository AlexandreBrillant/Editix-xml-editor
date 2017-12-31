package com.japisoft.editix.action.xsl;

import java.awt.Font;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.japisoft.editix.ui.pathbuilder.XHTMLPathBuilder;
import com.japisoft.editix.ui.pathbuilder.XMLPathBuilder;
import com.japisoft.editix.ui.pathbuilder.XQueryPathBuilder;
import com.japisoft.editix.ui.pathbuilder.XSLTPathBuilder;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.ui.table.ExportableTable;
import com.japisoft.framework.ui.text.FileTextField;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.p3.Manager;
import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;

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
public class XSLTConfigPanel extends javax.swing.JPanel {
    
	private boolean xqueryMode = false;

	public static String PARAM_PREFIX = "xslt";

	/** Creates new form XSLTParam */
    public XSLTConfigPanel( boolean xqueryMode ) {
    	this.xqueryMode = xqueryMode;
    	if ( xqueryMode ) {
    		PARAM_PREFIX = "xquery";
    	}
        initComponents();

        jLabel1.setFont( jLabel1.getFont().deriveFont( Font.BOLD ) );
        jLabel2.setFont( jLabel2.getFont().deriveFont( Font.BOLD ) );
        jLabel3.setFont( jLabel3.getFont().deriveFont( Font.BOLD ) );
        jLabel4.setFont( jLabel4.getFont().deriveFont( Font.BOLD ) );
        
        if ( Preferences.getPreference( "xslt", "check whitespaces in path", false ) ) {
        	tfDocumentSource.setWhitespaceChecker( true );
        	tfResultDocument.setWhitespaceChecker( true );
        	tfStylesheet.setWhitespaceChecker( true );
        }
    }

	public void init( IXMLPanel container ) {
		if ( container.getProperty( PARAM_PREFIX + ".xslt.file" ) == null )
			if ( container.getMainContainer().getCurrentDocumentLocation() != null ) {
				String type = "";
				if ( container.getMainContainer().getDocumentInfo().getType() != null )
					type = container.getMainContainer().getDocumentInfo().getType();
				if ( type.startsWith( "XSLT" ) || "XQR".equals( type ) )
					container.setProperty( 
							PARAM_PREFIX + ".xslt.file", 
							container.getMainContainer().getCurrentDocumentLocation() );
			}
		
		if ( container.getProperty( PARAM_PREFIX + ".data.file" ) == null ) {
			if ( container.getMainContainer().getCurrentDocumentLocation() != null ) {
				String type = "";
				if ( container.getMainContainer().getDocumentInfo().getType() != null )
					type = container.getMainContainer().getDocumentInfo().getType();
				if ( type.indexOf( "XML" ) > -1 ) {
					container.setProperty( 
							PARAM_PREFIX + ".data.file", 
							container.getMainContainer().getCurrentDocumentLocation() );
				}
			}
		}

		tfStylesheet.setText( "" + container.getProperty( PARAM_PREFIX + ".xslt.file", "" ) );	
		tfDocumentSource.setText( "" + container.getProperty( PARAM_PREFIX + ".data.file", "" ) );
		tfResultDocument.setText( "" + container.getProperty( PARAM_PREFIX +  ".result.file", "" ) );

		if ( "true".equals( container.getProperty( PARAM_PREFIX + ".openFile" ) ) )
			rbEdit.setSelected( true );
		if ( "true".equals( container.getProperty( PARAM_PREFIX + ".displayFile" ) ) )
			rbStartBrowser.setSelected( true );
		if ( "true".equals( container.getProperty( PARAM_PREFIX +  ".fop" ) ) )
			rbFOPOp.setSelected( true );

		if ( !rbStartBrowser.isSelected() && 
				!rbEdit.isSelected() && 
					!rbFOPOp.isSelected() )
			rbNoOp.setSelected( true ); 

		// bg.getSelection().getSelectedObjects()

		DefaultTableModel model = new DefaultTableModel(
			new String[] { xqueryMode ? "Prefix" : "Param", xqueryMode ? "Namespace" : "Value" }, 0 );

		// Fill with the current parameters
		ArrayList al = null;
		FPNode sn = container.getMainContainer().getRootNode();
		if ( sn != null ) {
			for ( int i = 0; i < sn.childCount(); i++ ) {
				FPNode child = sn.childAt( i );
				if ( child.matchContent( "param" ) ) {
					if ( al == null )
						al = new ArrayList();
					if ( child.getAttribute( "name" ) != null )
						al.add( child.getAttribute( "name" ) );
				}
			}
		}

		for ( int i = 0; i < Preferences.getPreference( PARAM_PREFIX, "parameter", 10 ); i++ ) {
			String param = ( String )container.getProperty( PARAM_PREFIX + ".param.name." + i );
			String value = ( String )container.getProperty( PARAM_PREFIX + ".param.value." + i );
			if ( param != null && value != null ) {
				model.addRow( new String[] { param, value } );
			} else
				if ( al == null )
					model.addRow( new String[] { "", "" } );

			if ( al != null )
				al.remove( param );
		}
		
		if ( al != null )
			for ( int i = 0; i < al.size(); i++ ) {
				if ( al.get( i ) != null )
				model.addRow( new String[] { ( String )al.get( i ), "" } );
			}
		tbParameters.setModel( model );
		
	}

	private String getDefaultResultFileNameExt( IXMLPanel c ) {
		XMLContainer container = c.getMainContainer();
		FPNode rootNode = container.getRootNode();
		if ( rootNode != null ) {
			for ( int i = 0; i < rootNode.childCount(); i++ ) {
				FPNode n = rootNode.childAt( i );
				if ( n.matchContent( "output" ) ) {
					String m = n.getAttribute( "method", "html" );
					if ( "html".equals( m ) || "xhtml".equals( m ) )
						return ".html";
					else
						if ( "text".equals( m ) )
							return ".txt";
								else
									return ".xml";
				}
			}
			return ".html";
		} else
			return ".html";
	}
	
	public void store( IXMLPanel container ) {
		container.setProperty( PARAM_PREFIX + ".ok", "true" ) ;
		container.setProperty( PARAM_PREFIX + ".xslt.file", tfStylesheet.getText() );
		container.setProperty( PARAM_PREFIX + ".data.file", tfDocumentSource.getText() );
		
		String tmpResultFile = tfResultDocument.getText();
		if ( tmpResultFile == null ) {
			if ( xqueryMode )
				tmpResultFile = "xqueryResult.xml";
			else
				tmpResultFile = "result" + getDefaultResultFileNameExt( container );
		}
		else
			if ( tmpResultFile.indexOf( "." ) == -1 ) 
				tmpResultFile += getDefaultResultFileNameExt( container );

		if ( tmpResultFile.indexOf( "/" ) == -1 &&
				tmpResultFile.indexOf( "\\" ) == -1 ) {
			// Store it inside the source directory

			if ( tfStylesheet.getText() != null ) {
				File f = new File( tfStylesheet.getText() );
				File parentFile = f.getParentFile();
				if ( parentFile != null )
					tmpResultFile = new File( parentFile, tmpResultFile ).toString();
			}
		}

		container.setProperty( PARAM_PREFIX + ".result.file", tmpResultFile );
		container.setProperty( PARAM_PREFIX + ".openFile", "" + rbEdit.isSelected() );
		container.setProperty( PARAM_PREFIX + ".displayFile", "" + rbStartBrowser.isSelected() );		
		container.setProperty( PARAM_PREFIX + ".fop", "" + rbFOPOp.isSelected() );		

		TableModel model = tbParameters.getModel();
		for ( int i = 0; i < model.getRowCount(); i++ ) {
			String param = ( String )model.getValueAt( i, 0 );
			String value = ( String )model.getValueAt( i, 1 );
			if ( !( "".equals( param) || "".equals( value ) ) ) {
				container.setProperty( PARAM_PREFIX +  ".param.name." + i, param );
				container.setProperty( PARAM_PREFIX +  ".param.value." + i, value );
			} else {
				container.setProperty( PARAM_PREFIX + ".param.name." + i,null);
				container.setProperty( PARAM_PREFIX + ".param.value." + i, null );
			}
		}
	}

	public String getXSLTFile() { 
		return tfStylesheet.getText();
	}

	public String getDataFile() {
		return tfDocumentSource.getText();
	}

	public String getResultFile() {
		return tfResultDocument.getText( "result1.html" );
	}

	public boolean isOpenNewDocument() {
		return rbNoOp.isSelected();
	}

	public boolean isDisplayWithBrowser() {
		return rbStartBrowser.isSelected();
	}
	
	public boolean isFOPProcessing() {
		return rbFOPOp.isSelected();
	}

	public void setEnabledFOP( boolean fop ) {
		rbFOPOp.setEnabled( fop );
	}
	
	public void setParametersTitle( String name ) {
		
	}
	
	public TableModel getParameters() {
		return tbParameters.getModel();
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">
    private void initComponents() {

    	if ( !xqueryMode )
    		tfStylesheet = new FileTextField( null, null, new String[] { "xsl", "xslt" }, new XSLTPathBuilder() );
    	else
    		tfStylesheet = new FileTextField( null, null, new String[] { "xq", "xqr", "xquery" }, new XQueryPathBuilder() );
    	
    	tfDocumentSource = new FileTextField( null, null,  "xml", new XMLPathBuilder() );
    	tfResultDocument = new FileTextField( null, null, new String[] { "htm", "html", "xml" }, new XHTMLPathBuilder() );

        buttonGroup1 = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        pnlMain = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        rbNoOp = new javax.swing.JRadioButton();
        rbEdit = new javax.swing.JRadioButton();
        rbStartBrowser = new javax.swing.JRadioButton();
        rbFOPOp = new javax.swing.JRadioButton();
        jLabel4 = new javax.swing.JLabel();
        pnlParameters = new javax.swing.JPanel();
        spParameters = new javax.swing.JScrollPane();
        
        jLabel1.setText( xqueryMode ? "XQuery documents (*.xq, *.xql, *.xquery)" : "XSLT Document (*.xsl *.xslt)");

        jLabel2.setText("XML Document source (*.xml)");

        jLabel3.setText("Result document (*." + ( xqueryMode ? "xml" : "html" ) + "...)" );

        buttonGroup1.add(rbNoOp);
        rbNoOp.setSelected(true);
        rbNoOp.setText("No operation");
        rbNoOp.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbNoOp.setMargin(new java.awt.Insets(0, 0, 0, 0));

        buttonGroup1.add(rbEdit);
        rbEdit.setText("Edit");
        rbEdit.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbEdit.setMargin(new java.awt.Insets(0, 0, 0, 0));

        buttonGroup1.add(rbStartBrowser);
        rbStartBrowser.setText("Display with a system browser (IE/Firefox...)");
        rbStartBrowser.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbStartBrowser.setMargin(new java.awt.Insets(0, 0, 0, 0));

        buttonGroup1.add(rbFOPOp);
        rbFOPOp.setText("FOP (the result document must be *.pdf, *.rtf...)");
        rbFOPOp.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbFOPOp.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jLabel4.setText("Result document processing :");

        org.jdesktop.layout.GroupLayout pnlMainLayout = new org.jdesktop.layout.GroupLayout(pnlMain);
        pnlMain.setLayout(pnlMainLayout);
        pnlMainLayout.setHorizontalGroup(
            pnlMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlMainLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(tfStylesheet, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
                    .add(jLabel1)
                    .add(jLabel2)
                    .add(tfDocumentSource, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
                    .add(jLabel3)
                    .add(tfResultDocument, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
                    // .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
                    .add(rbNoOp)
                    .add(rbEdit)
                    .add(rbStartBrowser)
                    .add(rbFOPOp)
                    .add(jLabel4))
                .addContainerGap())
        );
        pnlMainLayout.setVerticalGroup(
            pnlMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlMainLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(tfStylesheet, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(tfDocumentSource, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(tfResultDocument, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                // .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                //.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel4)
                //.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 11, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rbNoOp)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rbEdit)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rbStartBrowser)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rbFOPOp)
                .addContainerGap())
        );
        jTabbedPane1.addTab("Main", pnlMain);

        org.jdesktop.layout.GroupLayout pnlParametersLayout = new org.jdesktop.layout.GroupLayout(pnlParameters);
        pnlParameters.setLayout(pnlParametersLayout);
        pnlParametersLayout.setHorizontalGroup(
            pnlParametersLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(spParameters, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
        );
        pnlParametersLayout.setVerticalGroup(
            pnlParametersLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(spParameters, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
        );
        jTabbedPane1.addTab(xqueryMode ? "Namespaces" : "Parameters", pnlParameters);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 399, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE)
        );
        tbParameters = new ExportableTable();        
		spParameters.getViewport().add(tbParameters, null);

		DefaultTableModel model = new DefaultTableModel(
			new String[] { xqueryMode ? "Prefix" : "Param", xqueryMode ? "Namespace" : "Value" },
			Preferences.getPreference( "xslt", "parameter", 10 ) );	
		tbParameters.setModel( model );

    }// </editor-fold>

    private JTable tbParameters;    
    
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlParameters;
    private javax.swing.JRadioButton rbEdit;
    private javax.swing.JRadioButton rbFOPOp;
    private javax.swing.JRadioButton rbNoOp;
    private javax.swing.JRadioButton rbStartBrowser;
    private javax.swing.JScrollPane spParameters;
    private FileTextField tfDocumentSource;
    private FileTextField tfResultDocument;
    private FileTextField tfStylesheet;    
    

}
