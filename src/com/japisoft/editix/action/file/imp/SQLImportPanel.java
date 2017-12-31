package com.japisoft.editix.action.file.imp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.japisoft.editix.action.file.OpenAction;
import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.framework.ui.toolkit.FileManager;

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
public class SQLImportPanel extends JPanel implements ActionListener, ListSelectionListener {
	
	public SQLImportPanel() {
		initComponents();
		setEditable( false );		
		txtName.setDocument( new SynchronizedDriverPlainDocument() );
		txtDefaultUrl.setDocument( new SynchronizedDriverPlainDocument() );
		txtDriverClass.setDocument( new SynchronizedDriverPlainDocument() );
		txtLibrairies.setDocument( new SynchronizedDriverPlainDocument() );
	}
	
	void setEditable( boolean editable ) {
		txtName.setEnabled( editable );
		txtDefaultUrl.setEnabled( editable );
		txtDriverClass.setEnabled( editable );
		txtLibrairies.setEnabled( editable );
		btLibrairies.setEnabled( editable );
	}

	private JDBCDriverModel driverModel;
	
	public void setDriverModel( JDBCDriverModel model ) {
		this.driverModel = model;		
		JDBCDriverListModel m = new JDBCDriverListModel( model );
		cbDatabase.setModel( m );
		lstDrivers.setModel( m );
	}
		
	void restoreStateDelay() {
		try {
			File f = new File( 
				EditixApplicationModel.getAppUserPath(),
				"sqlparameters.txt"
			);	
			if ( f.exists() ) {
				FileReader fw = 
					new FileReader( f );
				BufferedReader br = new BufferedReader( fw );
				try {
					String db = br.readLine();
					String url = br.readLine();
					String user = br.readLine();
					String password = br.readLine();
					String withElement = br.readLine();
					
					txtUser.setText( user );
					pfPassword.setText( password );
					cbConvertColToElement.setSelected( "true".equals( withElement ) );
					
					for ( int i = 0; i < driverModel.size(); i++ ) {
						if ( 
							db.equalsIgnoreCase( 
								driverModel.getDriver( i ).getName() ) ) {
							cbDatabase.setSelectedItem( 
								driverModel.getDriver( i ) 
							);
							break;
						}
					}
					
					txtUrl.setText( url );
					
					String l = null;
					String sql = null;
					while ( ( l = br.readLine() ) != null ) {
						if ( sql == null )
							sql = l;
						else
							sql += "\n" + l;
					}
					if ( sql != null ) {
						taQuery.setText( sql );
					}
					
				} finally {
					br.close();
				}
			}
		} catch( Exception exc ) {
			EditixApplicationModel.debug( exc );
		}
	}

	void storeState() throws Exception {
		if( chkSaveParameters.isSelected() ) {
			FileWriter fw = 
				new FileWriter(
						new File( 
							EditixApplicationModel.getAppUserPath(), 
							"sqlparameters.txt" ) );
			PrintWriter pw = new PrintWriter( fw );
			try {
				pw.println( "" + cbDatabase.getSelectedItem() );
				pw.println( txtUrl.getText() );
				pw.println( txtUser.getText() );
				pw.println( pfPassword.getText() );
				pw.println( cbConvertColToElement.isSelected() );
				pw.print( taQuery.getText() );
			} finally {
				pw.close();
			}
		}
	}

	@Override
	public void addNotify() {
		super.addNotify();
		btAdd.addActionListener( this );
		btRemove.addActionListener( this );
		btEdit.addActionListener( this );
		btLibrairies.addActionListener( this );
		btRun.addActionListener( this );
		btRunEdit.addActionListener( this );
		lstDrivers.getSelectionModel().addListSelectionListener( this );
		cbDatabase.addActionListener( this );
		cbDatabase.setSelectedIndex( 0 );
		restoreStateDelay();
	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		btAdd.removeActionListener( this );
		btRemove.removeActionListener( this );
		btEdit.removeActionListener( this );
		btLibrairies.removeActionListener( this );
		btRun.removeActionListener( this );
		btRunEdit.removeActionListener( this );
		lstDrivers.getSelectionModel().removeListSelectionListener( this );
		cbDatabase.removeActionListener( this );
	}

	public void actionPerformed(ActionEvent e) {
		if ( e.getSource() == cbDatabase ) {
			txtUrl.setText( ( ( JDBCDriver )cbDatabase.getSelectedItem() ).getDefaultUrl() );
		} else		
		if ( e.getSource() == btEdit ) {
			setEditable( true );
		} else
		if ( e.getSource() == btAdd ) {
			JDBCDriver driver = new JDBCDriver( "New Driver" );
			driverModel.addDriver( driver );
			lstDrivers.setSelectedValue( driver, true );
			setEditable( true );
		} else
		if ( e.getSource() == btRemove ) {
			JDBCDriver driver = ( JDBCDriver )lstDrivers.getSelectedValue();
			driverModel.removeDriver( driver );
			if ( driverModel.size() > 0 ) {
				lstDrivers.setSelectedIndex( 0 );
			}
			setEditable( false );
		} else
		if ( e.getSource() == btRun ) {
			runQuery( false );
		} else
		if ( e.getSource() == btRunEdit ) {
			runQuery( true );
		} else
		if ( e.getSource() == btLibrairies ) {
			File[] jars = FileManager.getSelectedFiles( true , "jar", "Jar files" );
			if ( jars != null ) {
				String tmp = "";
				for ( File j : jars ) {
					if ( !"".equals( tmp ) ) {
						tmp += ";";
					}
					tmp += j.toString();
				}
				txtLibrairies.setText( tmp );
			}
		}
	}

	private void runQuery( boolean edit ) {
		
		if ( taQuery == null || 
				"".equals( taQuery.getText() ) ) {
			EditixFactory.buildAndShowWarningDialog( "No SQL query ?" );
			return;
		}

		File res = FileManager.getSelectedFile( true, "xml", "XML SQL output file" );
		if ( res != null ) {
			JDBCDriver driver = ( JDBCDriver )cbDatabase.getSelectedItem();
			if ( driver != null ) {
				try {
					Connection con = driver.getConnection( 
						txtUrl.getText(), 
						txtUser.getText(), 
						pfPassword.getText() 
					);
					Statement st = con.createStatement();
					ResultSet rs = st.executeQuery( 
						taQuery.getText() );
					DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
					Document doc = db.newDocument();
					Element query = doc.createElement( "query" );
					query.setAttribute( 
						"source", 
						txtUrl.getText() 
					);
					doc.appendChild( query );
					int row = 1;
					while ( rs.next() ) {
						Element r = doc.createElement( "row" );
						r.setAttribute( "id", Integer.toString( row++ ) );
						query.appendChild( r );
						for ( int i = 1; i <= rs.getMetaData().getColumnCount(); i++ ) {
							String colName = rs.getMetaData().getColumnName( i );
							String value = rs.getString( i );
							Element c = null;
							
							if ( cbConvertColToElement.isSelected() ) {
								c = doc.createElement( colName );
							} else {
								c = doc.createElement( "col" );
								c.setAttribute( "id", colName );								
							}

							c.appendChild( doc.createTextNode( value ) );
							r.appendChild( c );
						}
					}
					Transformer t = TransformerFactory.newInstance().newTransformer();
					t.transform( new DOMSource( doc ), new StreamResult( res ) );
					if ( edit ) {
						OpenAction.openFile( "XML", false, res, null );
					}
				} catch( Exception exc ) {
					EditixApplicationModel.debug( exc );
					EditixFactory.buildAndShowErrorDialog( "Can't run the query [" + exc.getMessage() + "]" );
				}
			}
		}
	}

	public void valueChanged( ListSelectionEvent e ) {
		setEditable( false );
		JDBCDriver driver = ( JDBCDriver )lstDrivers.getSelectedValue();
		dispatchDriver( driver );
	}

	private JDBCDriver currentDriver = null;
	
	private void dispatchDriver( JDBCDriver driver ) {
		SynchronizedDriverPlainDocument[] docs = {
			( SynchronizedDriverPlainDocument )txtName.getDocument(),
			( SynchronizedDriverPlainDocument )txtDefaultUrl.getDocument(),
			( SynchronizedDriverPlainDocument )txtDriverClass.getDocument(),
			( SynchronizedDriverPlainDocument )txtLibrairies.getDocument()
		};

		for ( SynchronizedDriverPlainDocument doc : docs ) {
			doc.setEnabledSynchro( false );
		}

		txtName.setText( driver.getName() );
		txtDefaultUrl.setText( driver.getDefaultUrl() );
		txtDriverClass.setText( driver.getDriverClass() );
		txtLibrairies.setText( driver.getFlatUrls() );
		
		for ( SynchronizedDriverPlainDocument doc : docs ) {
			doc.setEnabledSynchro( true );
		}

		this.currentDriver = driver;
	}

	private void synchronizedDriver() {
		if ( currentDriver != null ) {
			currentDriver.setName( 
				txtName.getText() );
			currentDriver.setDefaultUrl( 
				txtDefaultUrl.getText() );
			currentDriver.setDriverClass( 
				txtDriverClass.getText() );
			currentDriver.setFlatUrls( 
				txtLibrairies.getText() );
			lstDrivers.repaint();
		}
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        tpMain = new javax.swing.JTabbedPane();
        pnlQuery = new javax.swing.JPanel();
        lblDatabase = new javax.swing.JLabel();
        cbDatabase = new javax.swing.JComboBox();
        jSeparator1 = new javax.swing.JSeparator();
        lblUrl = new javax.swing.JLabel();
        txtUrl = new javax.swing.JTextField();
        lblUser = new javax.swing.JLabel();
        txtUser = new javax.swing.JTextField();
        lblPassword = new javax.swing.JLabel();
        pfPassword = new javax.swing.JPasswordField();
        lblQuery = new javax.swing.JLabel();
        spQuery = new javax.swing.JScrollPane();
        taQuery = new javax.swing.JTextArea();
        jSeparator2 = new javax.swing.JSeparator();
        btRun = new javax.swing.JButton();
        btRunEdit = new javax.swing.JButton();
        chkSaveParameters = new javax.swing.JCheckBox();
        cbConvertColToElement = new javax.swing.JCheckBox();
        pnlDrivers = new javax.swing.JPanel();
        spDrivers = new javax.swing.JScrollPane();
        lstDrivers = new javax.swing.JList();
        btAdd = new javax.swing.JButton();
        btRemove = new javax.swing.JButton();
        btEdit = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        lblName = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        lblDriverClass = new javax.swing.JLabel();
        txtDriverClass = new javax.swing.JTextField();
        lblDefaultUrl = new javax.swing.JLabel();
        txtDefaultUrl = new javax.swing.JTextField();
        lblLibraries = new javax.swing.JLabel();
        txtLibrairies = new javax.swing.JTextField();
        btLibrairies = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        lblDatabase.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblDatabase.setForeground(new java.awt.Color(51, 51, 51));
        lblDatabase.setText("Database");

        lblUrl.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblUrl.setForeground(new java.awt.Color(51, 51, 51));
        lblUrl.setText("Url");

        lblUser.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblUser.setForeground(new java.awt.Color(51, 51, 51));
        lblUser.setText("User");

        lblPassword.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblPassword.setForeground(new java.awt.Color(51, 51, 51));
        lblPassword.setText("Password");

        lblQuery.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblQuery.setForeground(new java.awt.Color(51, 51, 51));
        lblQuery.setText("SQL Query");

        taQuery.setColumns(20);
        taQuery.setRows(5);
        spQuery.setViewportView(taQuery);

        btRun.setText("Run");

        btRunEdit.setLabel("Run and Edit");

        chkSaveParameters.setText("Save Parameters");

        cbConvertColToElement.setText("Convert column to element");

        org.jdesktop.layout.GroupLayout pnlQueryLayout = new org.jdesktop.layout.GroupLayout(pnlQuery);
        pnlQuery.setLayout(pnlQueryLayout);
        pnlQueryLayout.setHorizontalGroup(
            pnlQueryLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlQueryLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlQueryLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jSeparator2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
                    .add(spQuery, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
                    .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
                    .add(pnlQueryLayout.createSequentialGroup()
                        .add(lblDatabase)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(cbDatabase, 0, 422, Short.MAX_VALUE))
                    .add(lblUrl)
                    .add(txtUrl, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
                    .add(lblUser)
                    .add(txtUser, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
                    .add(lblPassword)
                    .add(pfPassword, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
                    .add(lblQuery)
                    .add(pnlQueryLayout.createSequentialGroup()
                        .add(btRun)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(btRunEdit)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 225, Short.MAX_VALUE)
                        .add(chkSaveParameters))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, cbConvertColToElement))
                .addContainerGap())
        );
        pnlQueryLayout.setVerticalGroup(
            pnlQueryLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlQueryLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlQueryLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblDatabase)
                    .add(cbDatabase, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblUrl)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtUrl, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(lblUser)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtUser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblPassword)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pfPassword, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(lblQuery)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(spQuery, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 93, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbConvertColToElement)
                .add(6, 6, 6)
                .add(jSeparator2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlQueryLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btRun)
                    .add(btRunEdit)
                    .add(chkSaveParameters))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tpMain.addTab("Query", pnlQuery);

        spDrivers.setViewportView(lstDrivers);

        btAdd.setText("Add");

        btRemove.setText("Remove");

        btEdit.setText("Edit");

        lblName.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblName.setForeground(new java.awt.Color(51, 51, 51));
        lblName.setText("Name");

        lblDriverClass.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblDriverClass.setForeground(new java.awt.Color(51, 51, 51));
        lblDriverClass.setText("Driver class");

        lblDefaultUrl.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblDefaultUrl.setForeground(new java.awt.Color(51, 51, 51));
        lblDefaultUrl.setText("Default url");

        lblLibraries.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblLibraries.setForeground(new java.awt.Color(51, 51, 51));
        lblLibraries.setText("Libraries");

        btLibrairies.setText("Set");

        org.jdesktop.layout.GroupLayout pnlDriversLayout = new org.jdesktop.layout.GroupLayout(pnlDrivers);
        pnlDrivers.setLayout(pnlDriversLayout);
        pnlDriversLayout.setHorizontalGroup(
            pnlDriversLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlDriversLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlDriversLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, spDrivers, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
                    .add(pnlDriversLayout.createSequentialGroup()
                        .add(btAdd)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btRemove)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btEdit))
                    .add(jSeparator3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
                    .add(lblName)
                    .add(txtName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
                    .add(lblDriverClass)
                    .add(txtDriverClass, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
                    .add(lblDefaultUrl)
                    .add(txtDefaultUrl, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
                    .add(lblLibraries)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlDriversLayout.createSequentialGroup()
                        .add(txtLibrairies, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 427, Short.MAX_VALUE)
                        .add(10, 10, 10)
                        .add(btLibrairies)))
                .addContainerGap())
        );
        pnlDriversLayout.setVerticalGroup(
            pnlDriversLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlDriversLayout.createSequentialGroup()
                .addContainerGap()
                .add(spDrivers, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(pnlDriversLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btAdd)
                    .add(btRemove)
                    .add(btEdit))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jSeparator3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblName)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(lblDriverClass)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtDriverClass, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(lblDefaultUrl)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtDefaultUrl, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(lblLibraries)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlDriversLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(btLibrairies)
                    .add(txtLibrairies, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(32, Short.MAX_VALUE))
        );

        tpMain.addTab("JDBC Drivers", pnlDrivers);

        add(tpMain, java.awt.BorderLayout.CENTER);
    }// </editor-fold>
	
    // Variables declaration - do not modify
    private javax.swing.JButton btAdd;
    private javax.swing.JButton btEdit;
    private javax.swing.JButton btLibrairies;
    private javax.swing.JButton btRemove;
    private javax.swing.JButton btRun;
    private javax.swing.JButton btRunEdit;
    private javax.swing.JCheckBox cbConvertColToElement;
    private javax.swing.JComboBox cbDatabase;
    private javax.swing.JCheckBox chkSaveParameters;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel lblDatabase;
    private javax.swing.JLabel lblDefaultUrl;
    private javax.swing.JLabel lblDriverClass;
    private javax.swing.JLabel lblLibraries;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblQuery;
    private javax.swing.JLabel lblUrl;
    private javax.swing.JLabel lblUser;
    private javax.swing.JList lstDrivers;
    private javax.swing.JPasswordField pfPassword;
    private javax.swing.JPanel pnlDrivers;
    private javax.swing.JPanel pnlQuery;
    private javax.swing.JScrollPane spDrivers;
    private javax.swing.JScrollPane spQuery;
    private javax.swing.JTextArea taQuery;
    private javax.swing.JTabbedPane tpMain;
    private javax.swing.JTextField txtDefaultUrl;
    private javax.swing.JTextField txtDriverClass;
    private javax.swing.JTextField txtLibrairies;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtUrl;
    private javax.swing.JTextField txtUser;
    // End of variables declaration

   
    // -------------------------------------------------------------------------------------------------------------
    
    class SynchronizedDriverPlainDocument extends PlainDocument {

    	private boolean enabledSynchro = true;
    	
    	public void setEnabledSynchro( boolean enabledSynchro ) {
    		this.enabledSynchro = enabledSynchro;
    	}

		@Override
		public void insertString(int offs, String str, AttributeSet a)
				throws BadLocationException {
			super.insertString(offs, str, a);
			if ( enabledSynchro )
				synchronizedDriver();
		}

		@Override
		public void remove(int offs, int len) throws BadLocationException {
			super.remove(offs, len);
			if ( enabledSynchro )
				synchronizedDriver();
		}

    }

}
