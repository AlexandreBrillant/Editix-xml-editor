package com.japisoft.editix.ui.container.xpath;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.editix.ui.container.FilterView;
import com.japisoft.editix.ui.container.SerializeStateObject;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.ui.table.ExportableTable;
import com.japisoft.framework.ui.toolkit.FileManager;
import com.japisoft.framework.xml.DOMToolkit;
import com.japisoft.framework.xml.XMLToolkit;
import com.japisoft.xmlpad.helper.HelperManager;

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
public class XPathInnerView extends JPanel 
		implements 
			FilterView, 
			ActionListener,
			ListSelectionListener,
			SerializeStateObject {

	public XPathInnerView() {
		initComponents();
		btAddXPath.setIcon( 
			new ImageIcon( getClass().getResource( "element_add.png" ) ) 
		);
		btRemoveXPath.setIcon( 
			new ImageIcon( getClass().getResource( "element_delete.png" ) ) 
		);
		btEdit.setIcon(
			new ImageIcon( getClass().getResource( "element_edit.png" ) )
		);
		btRun.setIcon( 
			new ImageIcon( getClass().getResource( "element_run.png" ) ) 
		);
		btCut.setIcon(
			new ImageIcon( getClass().getResource( "element_delete.png" ) )				
		);
		btCopy.setIcon(
			new ImageIcon( getClass().getResource( "element_copy.png" ) )				
		);
		btPaste.setIcon(
			new ImageIcon( getClass().getResource( "element_new_after.png" ) )				
		);

		btImport.setIcon(
			new ImageIcon( 
				getClass().getResource( "import.png" ) 
			)
		);

		btImport.setToolTipText( "Import a CSV file" );

		btExport.setIcon(
			new ImageIcon( 
				getClass().getResource( "export.png" ) 
			)
		);
		
		btExport.setToolTipText( "Export a CSV file" );

		btCut.setToolTipText( "Cut the selected row" );
		btCopy.setToolTipText( "Copy the selected row" );
		btPaste.setToolTipText( "Paste the last copied row" );

		btAddXPath.setText( null );
		btRemoveXPath.setText( null );
		btRun.setText( null );
		updateButtonsStates();
	}

	public void copy() {
	}

	public void cut() {
	}

	public void paste() {
	}

	public void restoreState(String serialize) {
		String[] tmp = serialize.split( "§" );
		cbAutomaticActivation.setSelected( Boolean.parseBoolean( tmp[ 0 ] ) );
		DefaultComboBoxModel cbm = new DefaultComboBoxModel();
		XPathEditorModel selected = null;
		for ( int i = 1;i < tmp.length; i++ ) {
			boolean ok = false;
			if ( tmp[ i ].startsWith( "*" ) ) {
				ok = true;
				tmp[ i ] = tmp[ i ].substring( 1 );
			}
			XPathEditorModel xem = new XPathEditorModel();
			xem.restoreState( tmp[ i ] );
			cbm.addElement( xem );
			if ( ok ) {
				selected = xem; 
			}
		}
		cbXPath.setModel( cbm );
		if ( selected != null ) {
			cbXPath.setSelectedItem( selected );
			updateButtonsStates();
		}
	}

	public String serializeState() {
		StringBuffer sb = new StringBuffer( 
			Boolean.toString( 
				cbAutomaticActivation.isSelected() 
			) 
		);
		for ( int i = 0; i < cbXPath.getItemCount(); i++ ) {
			XPathEditorModel model = ( XPathEditorModel )cbXPath.getItemAt( i );
			sb.append( "§" );
			if ( cbXPath.getSelectedItem() == model ) {
				sb.append( "*" );
			}
			sb.append( model.serializeState() );
		}
		return sb.toString();
	}

	private Node copy = null;

	private void updateButtonsStates() {
		btRemoveXPath.setEnabled( 
			cbXPath.getItemCount() > 0 
		);
		btRun.setEnabled( 
			cbXPath.getItemCount() > 0 
		);
		btEdit.setEnabled(
			cbXPath.getItemCount() > 0
		);
		btCut.setEnabled( tbResult.getSelectedRow() > -1 );
		btCopy.setEnabled( tbResult.getSelectedRow() > -1 );
		btPaste.setEnabled( copy != null );
	}

	public void valueChanged(ListSelectionEvent e) {
		updateButtonsStates();
	}

	public String getName() {		
		return "Filter";
	}

	public JComponent getView() {
		return this;
	}
	
	public boolean isModified() {
		if ( tbResult.getModel() instanceof XPathResultTableModel ) {
			if ( tbResult.getCellEditor() != null )
				tbResult.getCellEditor().stopCellEditing();						
			XPathResultTableModel m = ( XPathResultTableModel )tbResult.getModel();
			return m.isModified();
		}
		return false;
	}	

	public void dispose() {
		this.xmlContent = null;
		this.copy = null;
		if ( tbResult.getModel() instanceof XPathResultTableModel ) {
			XPathResultTableModel m = ( XPathResultTableModel )tbResult.getModel();
			m.dispose();
		}
	}

	private String xmlLocation;
	private Document xmlContent;
	
	public void init(
			HelperManager manager,
			String location,
			Document xmlContent ) throws Exception {

		this.xmlLocation = location;
		this.xmlContent = xmlContent;		

		if( cbAutomaticActivation.isSelected() ) {
			run();
		}
		if ( tbResult.getModel() instanceof XPathResultTableModel ) {
			XPathResultTableModel m = ( XPathResultTableModel )tbResult.getModel();
			m.setModified( false );
		}
	}

	@Override
	public void addNotify() {
		super.addNotify();
		btAddXPath.addActionListener( this );
		btRemoveXPath.addActionListener( this );
		btRun.addActionListener( this );
		btEdit.addActionListener( this );
		tbResult.getSelectionModel().addListSelectionListener( this );
		btCopy.addActionListener( this );
		btCut.addActionListener( this );
		btPaste.addActionListener( this );
		cbXPath.addActionListener( this );
		
		btImport.addActionListener( this );
		btExport.addActionListener( this );
	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		btAddXPath.removeActionListener( this );
		btRemoveXPath.removeActionListener( this );
		btRun.removeActionListener( this );
		btEdit.removeActionListener( this );
		tbResult.getSelectionModel().removeListSelectionListener( this );
		btCopy.removeActionListener( this );
		btCut.removeActionListener( this );
		btPaste.removeActionListener( this );
		cbXPath.removeActionListener( this );
		
		btImport.removeActionListener( this );
		btExport.removeActionListener( this );		
	}

	private void run() {
		try {
			Document doc = xmlContent;
			XPathResultTableModel model = new XPathResultTableModel( doc, ( XPathEditorModel )cbXPath.getSelectedItem() );
			XPathRunner xpr = new XPathRunner( model );
			xpr.run();			
			tbResult.setModel( model );
			tbResult.getSelectionModel().setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
			EditixFactory.fillDefaultTableRenderer( tbResult );
			updateButtonsStates();
		} catch( Exception exc ) {
			ApplicationModel.debug( exc );
			EditixFactory.buildAndShowErrorDialog( 
				"Can't run the query : " + exc.getMessage() 
			);
		}		
	}
	
	private String cleanField( String field ) {
		if ( field == null )
			return null;
		if ( field.startsWith( "\"" ) && field.endsWith( "\"" ) ) {
			return field.substring( 1, field.length() - 1 );
		} else
			return field;
	}
	
	public void actionPerformed( ActionEvent e ) {
		if ( e.getSource() == btImport ) {
			File res = FileManager.getSelectedFile( 
				true, 
				"csv", 
				"CSV File" 
			);
			if ( res != null ) {
				try {
					BufferedReader br = new BufferedReader(
						new InputStreamReader( 
							new FileInputStream( res ), 
							"UTF-8" 
						) 
					);
					try {
						TableModel tm = tbResult.getModel();
						String l = null;
						int index = 0;
						while ( ( l = br.readLine() ) != null ) {
							if ( "".equals( l ) )
								continue;
							String[] fields = l.split( ";" );
							for ( int i = 0; i < fields.length; i++ ) {
								if ( i == tm.getColumnCount() ) {
									// Too much column
									break;
								}
								
								tm.setValueAt( cleanField( fields[ i ] ), index, i );
							}
							index++;
						}
						tbResult.repaint();
					} finally {
						br.close();
					}
				} catch( IOException exc ) {
					EditixFactory.buildAndShowWarningDialog( "Can't read " + res + " : " + exc.getMessage() );
				}
			}
		} else
		if ( e.getSource() == btExport ) {
			File res = FileManager.getSelectedFile( 
				false, 
				"csv", 
				"CSV File" 
			);

			if ( res != null ) {
				TableModel tm = tbResult.getModel();
				if ( tm.getRowCount() == 0 ) {
					EditixFactory.buildAndShowWarningDialog( "No data" );
				} else {
					try {
					
						BufferedWriter bw = new BufferedWriter( 
								new OutputStreamWriter( 
									new FileOutputStream( 
										res 
									),
									"UTF-8"
								)
						);
						try {
							for ( int i = 0; i < tm.getRowCount(); i++ ) {
								if ( i > 0 ) {
									bw.newLine();
								}
								for ( int j = 0; j < tm.getColumnCount(); j++ ) {
									String val = ( String )tm.getValueAt( i, j );
									if ( j > 0 ) {
										bw.write( ";" );
									}
									if ( val == null )
										val = "";
									
									val = val.replace( '\n', ' ' );

									bw.write( val );
								}
							}
						} finally {
							try {
								bw.close();
							} catch( Exception exc ) {}
						}					
					} catch( IOException exc ) {						
						EditixFactory.buildAndShowErrorDialog( "Can't write to " + res + " : " + exc.getMessage() );
					}
				}
			}
			
		} else		
		if ( e.getSource() == cbXPath ) {
			if ( cbAutomaticActivation.isSelected() ) {
				run();
			}
		} else
		if ( e.getSource() == btAddXPath ) {
			XPathEditor xpe = new XPathEditor();
			if ( 
				DialogManager.showDialog( 
					EditixFrame.THIS,
					"XPath Expression",
					"Choose your XPath Expression",
					"Choose an XPath level expression, then select which part you wish to edit for each occurence found", 
					null,
					xpe ) == DialogManager.OK_ID ) {
				XPathEditorModel model = xpe.getEditorModel();
				cbXPath.addItem( model );
			}
		} else
		if( e.getSource() == btRemoveXPath ) {
			cbXPath.removeItemAt( cbXPath.getSelectedIndex() );
		} else
		if ( e.getSource() == btRun ) {
			run();
		}
		if ( e.getSource() == btEdit ) {
			XPathEditorModel model = ( XPathEditorModel )cbXPath.getSelectedItem();
			XPathEditor xpe = new XPathEditor();
			xpe.setEditorModel( model );
			if ( 
					DialogManager.showDialog( 
						EditixFrame.THIS,
						"Edit XPath Expression",
						"Edit your XPath Expression",
						"Choose an XPath 1.0 expression and your editing columns", 
						null,
						xpe ) == DialogManager.OK_ID ) {
				cbXPath.removeItem( model );
				cbXPath.addItem( xpe.getEditorModel() );
				cbXPath.setSelectedIndex( cbXPath.getItemCount() - 1 );
			}
		} else
		if ( e.getSource() == btCopy ) {
			XPathResultTableModel model = ( XPathResultTableModel )tbResult.getModel();
			copy = model.getNodeAt( tbResult.getSelectedRow() );
		} else
		if ( e.getSource() == btCut ) {
			XPathResultTableModel model = ( XPathResultTableModel )tbResult.getModel();
			copy = model.getNodeAt( tbResult.getSelectedRow() );
			model.removeNodeAt( tbResult.getSelectedRow() );
		} else
		if ( e.getSource() == btPaste ) {
			if ( tbResult.getSelectedRow() > -1 ) {
				XPathResultTableModel model = ( XPathResultTableModel )tbResult.getModel();
				try {
					model.addNodeAt( tbResult.getSelectedRow(), copy );
				} catch( Exception exc ) {
					EditixFactory.buildAndShowErrorDialog( "Can't add this node : " + exc.getMessage() );
				}
			} else
				EditixFactory.buildAndShowWarningDialog( "Select a line for inserting the copy" );
		}
		updateButtonsStates();		
	}

//  /** This method is called from within the constructor to
//  * initialize the form.
//  * WARNING: Do NOT modify this code. The content of this method is
//  * always regenerated by the Form Editor.
//  */
// @SuppressWarnings("unchecked")
 // <editor-fold defaultstate="collapsed" desc="Generated Code">
 private void initComponents() {

     lblXPath = new javax.swing.JLabel();
     cbXPath = new javax.swing.JComboBox();
     btAddXPath = new javax.swing.JButton();
     btRemoveXPath = new javax.swing.JButton();
     jSeparator1 = new javax.swing.JSeparator();
     lblResult = new javax.swing.JLabel();
     spResult = new javax.swing.JScrollPane();
     tbResult = new ExportableTable();
     cbAutomaticActivation = new javax.swing.JCheckBox();
     btEdit = new javax.swing.JButton();
     btRun = new javax.swing.JButton();
     btCut = new javax.swing.JButton();
     btCopy = new javax.swing.JButton();
     btPaste = new javax.swing.JButton();

     btImport = new javax.swing.JButton();
     btExport = new javax.swing.JButton();     

     lblXPath.setText("XPath");

     btAddXPath.setText("+");
     btRemoveXPath.setText("-");

     lblResult.setText("Result");
     
     tbResult.setModel(new javax.swing.table.DefaultTableModel(
         new Object [][] {
             {},
             {},
             {},
             {}
         },
         new String [] {

         }
     ));
     spResult.setViewportView(tbResult);

	 cbAutomaticActivation.setFont(new java.awt.Font("Tahoma", 1, 11));
	 cbAutomaticActivation.setForeground(new java.awt.Color(51, 51, 51));
	 cbAutomaticActivation.setText("Automatic Activation");

     org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
     this.setLayout(layout);
     layout.setHorizontalGroup(
         layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
         .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
             .addContainerGap()
             .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                 .add(org.jdesktop.layout.GroupLayout.LEADING, spResult, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 728, Short.MAX_VALUE)
                 .add(org.jdesktop.layout.GroupLayout.LEADING, jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 728, Short.MAX_VALUE)
                 .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                     .add(lblXPath)
                     .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                     .add(cbXPath, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 503, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                     .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                     .add(btAddXPath)
                     .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                     .add(btEdit)
                     .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                     .add(btRemoveXPath)
                     .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                     .add(btRun))
                 .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                     .add(lblResult)
                     .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 408, Short.MAX_VALUE)
                     .add(btCut)
                     .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                     .add(btCopy)
                     .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                     .add(btPaste)
                     .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                     .add(cbAutomaticActivation))
                     .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(btImport)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(btExport)))
             .addContainerGap())
     );
     layout.setVerticalGroup(
         layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
         .add(layout.createSequentialGroup()
             .addContainerGap()
             .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                 .add(lblXPath)
                 .add(cbXPath, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                 .add(btAddXPath)
                 .add(btRemoveXPath)
                 .add(btEdit)
                 .add(btRun))
             .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
             .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
             .add(8, 8, 8)
             .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                 .add(lblResult)
                 .add(cbAutomaticActivation)
                 .add(btPaste)
                 .add(btCopy)
                 .add(btCut))
             .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
             .add(spResult, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
             .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
             .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                 .add(btImport)
                 .add(btExport)))
     );
 }// </editor-fold>


 // Variables declaration - do not modify
 private javax.swing.JButton btAddXPath;
 private javax.swing.JButton btCopy;
 private javax.swing.JButton btCut;
 private javax.swing.JButton btEdit;
 private javax.swing.JButton btPaste;
 private javax.swing.JButton btRemoveXPath;
 private javax.swing.JButton btRun;
 private javax.swing.JButton btExport;
 private javax.swing.JButton btImport; 
 private javax.swing.JCheckBox cbAutomaticActivation;
 private javax.swing.JComboBox cbXPath;
 private javax.swing.JSeparator jSeparator1;
 private javax.swing.JLabel lblResult;
 private javax.swing.JLabel lblXPath;
 private javax.swing.JScrollPane spResult;
 private javax.swing.JTable tbResult;
 // End of variables declaration
}
