
package com.japisoft.editix.action.file;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import javax.swing.DefaultComboBoxModel;
import javax.swing.table.TableModel;

import com.japisoft.editix.action.file.imp.HTMLImport;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.ui.table.ExportableTable;
import com.japisoft.framework.xml.XMLFileData;
import com.japisoft.framework.xml.XMLToolkit;

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
public class ImportURLUI extends javax.swing.JPanel 
        implements ActionListener, ItemListener {
    private String storingPath = ApplicationModel.getAppUserPath().toString();

    /** Creates new form ImportURLUI */
    public ImportURLUI() {
        initComponents();
    }
    
    public void addNotify() {
        btnSave.addActionListener( this );
        btnRemove.addActionListener( this );
        cbURL.addItemListener( this );
        super.addNotify();
        init();
    }

    public void removeNotify() {
        btnSave.removeActionListener( this );
        btnRemove.removeActionListener( this );
        cbURL.removeItemListener( this );
        super.removeNotify();
    }
    
    public void actionPerformed( ActionEvent e ) {       
        if ( e.getSource() == btnSave ) {
            saveIt();
        } else {
            deleteIt();
        }
    }

    public void itemStateChanged(ItemEvent e) {
        String url = getURL();
        select( url );
    }
    
    public void select( String url ) {
        File f = getFileForURL( url );
        if ( f == null )
            return;
        try {
            BufferedReader br = 
                       new BufferedReader(  new FileReader( f ) );
            try {
               br.readLine();
               rbGet.setSelected(
                       br.readLine().equals( "true" ) );
               rbPost.setSelected(
                       br.readLine().equals( "true" ) );

               String paramValue = null;
               TableModel bm = tbParams.getModel();
               int cptParam = 0;
               
               while ( ( paramValue = br.readLine() ) != null ) {
                   if ( paramValue.indexOf( "=" ) > -1 ) {
                        int p = paramValue.indexOf( "=" );
                        String param = paramValue.substring( 0, p );
                        String value = paramValue.substring( p + 1 );
                        bm.setValueAt( param, cptParam, 0 );
                        bm.setValueAt( value, cptParam, 1 );
                        cptParam++;
                   } else {
                       cbConvertHTML.setSelected( paramValue.equals( "true" ) );
                       break;
                   }
               }

               for ( int i = cptParam; i < bm.getRowCount(); i++ ) {
                   bm.setValueAt( "", i, 0 );
                   bm.setValueAt( "", i, 1 );
               }
               
            } finally {
                br.close();
            }
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        }
    }

    public String getURL() {
        return "" + cbURL.getSelectedItem();
    }
    
    private void saveIt() {
        if ( "".equals( getURL() ) )
            return;
        
        File f = getFileForURL( getURL() );
        if ( f == null ) {
            // Search for available file name
            String[] files = new File( storingPath ).list();   
            int max = 0;
            for ( int i = 0; ( files != null && i < files.length ); i++ ) {
                String name = files[ i ];
                if ( name.endsWith( ".url" ) ) {
                    int j = name.lastIndexOf( "." );
                    String prefix = name.substring( 0, j );
                    try {
                        max = Math.max( Integer.parseInt( prefix ), max );
                    } catch (NumberFormatException ex) {
                    }
                }
            }
            f = new File( storingPath, ( max + 1 ) + ".url" );
        }
        try {
            BufferedWriter bw = new BufferedWriter(
                    new FileWriter( f ) );
            try {
               bw.write( getURL() );
               bw.newLine();
               bw.write( "" + rbGet.isSelected() );
               bw.newLine();
               bw.write( "" + rbPost.isSelected() );
               TableModel bm = tbParams.getModel();
               for ( int i = 0; i < bm.getRowCount(); i++ ) {
                   String param = ( String )bm.getValueAt( i, 0 );
                   String value = ( String )bm.getValueAt( i, 1 );
                   if ( param != null && value != null ) {
                       bw.newLine();
                       bw.write( param + "=" + value );
                   }
               }
               bw.newLine();
               bw.write( "" + cbConvertHTML.isSelected() );

               boolean found = false;
               for ( int i = 0; i < cbURL.getItemCount(); i++ ) {
                   if ( getURL().equals( cbURL.getItemAt( i ) ) ) {
                       found = true;
                       break;
                   }
               }
               if ( !found )
                cbURL.addItem( getURL() );
            } finally {
                bw.close();
            }
        } catch (IOException ex) {
        }
    }

    private void deleteIt() {
        File f = getFileForURL( getURL() );
        if ( f != null ) {
            f.delete();
        }
        init();
    }

    private void init() {
        updateState();
        if ( cbURL.getItemCount() > 0 )
            select( ( String )cbURL.getItemAt( 0 ) );        
    }
    
    private void updateState() { 
        DefaultComboBoxModel
                dcbm = new DefaultComboBoxModel();
        String[] files = new File( storingPath ).list();
        for ( int i = 0; files != null && i < files.length; i++ ) {
            String name = files[ i ];
            if ( name.endsWith( ".url" ) ) {
                File f = new File( storingPath, name );
                try {
                    FileReader fr = new FileReader( f );
                    BufferedReader brea =
                            new BufferedReader( fr );
                    try {
                        String url = brea.readLine();
                        dcbm.addElement( url );
                    } finally {
                        brea.close();
                    }
                } catch (FileNotFoundException ex) {
                } catch (IOException ex) {
                }                
            }
        }
        cbURL.setModel( dcbm );
        if( cbURL.getItemCount() > 0 )
            cbURL.setSelectedIndex( 0 );
    }

    private File getFileForURL( String pUrl ) {
        // Search file with this URL
        String[] files = new File( storingPath ).list();
        for ( int i = 0; files != null && i < files.length; i++ ) {
            String name = files[ i ];
            if ( name.endsWith( ".url" ) ) {
                File f = new File( storingPath, name );
                try {
                    FileReader fr = new FileReader( f );
                    BufferedReader brea =
                            new BufferedReader( fr );
                    try {
                        String url = brea.readLine();
                        if ( url.equalsIgnoreCase( pUrl ) )
                            return f;
                    } finally {
                        brea.close();
                    }
                } catch (FileNotFoundException ex) {
                } catch (IOException ex) {
                }
            }
        }        
        return null;
    }
    
    public XMLFileData connect( String encodingMode ) throws Throwable {
        String url = ( String )cbURL.getSelectedItem();
        // Add parameters for GET usage
        TableModel tm = tbParams.getModel();
        URL urlObj = null;
        
        if ( rbPost.isSelected() ) {
            urlObj = new URL( url );
        }
        String params = "";
        for ( int i = 0; i < tm.getRowCount(); i++ ) {
            String param = ( String )tm.getValueAt( i, 0 );
            String value = ( String )tm.getValueAt( i, 1 );
            if ( param != null &&
                    value != null &&
                    !"".equals( param ) &&
                    !"".equals( value ) ) {
                if ( rbGet.isSelected() ) {
                    if ( url.indexOf( "?" ) == -1 )
                        url += "?";
                }
                if ( !"".equals( params ) )
                    params += "&";
                params += URLEncoder.encode( param ) + "=" + URLEncoder.encode( value );
            }
        }
        
        if ( rbGet.isSelected() ) {
            urlObj = new URL( url + params );
        }

        URLConnection connection = urlObj.openConnection();
        connection.setDoOutput( rbPost.isSelected() );
        connection.setRequestProperty( "user-agent", "Mozilla" );
        if ( rbPost.isSelected() ) {
            if ( !params.equals( "" ) )
                connection.getOutputStream().write( params.getBytes() );
        }
        InputStream input = connection.getInputStream();
        
        if ( cbConvertHTML.isSelected() ) {
        	byte[] data = HTMLImport.convertHTMLInputStream(
        			input );
        	if ( data != null ) {
        		input = new ByteArrayInputStream( data );
        	}
        }
              
        XMLFileData xfd = XMLToolkit.getContentFromInputStream( input, encodingMode );

        
        return xfd;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        bgpMethod = new javax.swing.ButtonGroup();
        lblURL = new javax.swing.JLabel();
        cbURL = new javax.swing.JComboBox();
        lblMethod = new javax.swing.JLabel();
        rbGet = new javax.swing.JRadioButton();
        rbPost = new javax.swing.JRadioButton();
        spParams = new javax.swing.JScrollPane();
        tbParams = new ExportableTable();
        cbConvertHTML = new javax.swing.JCheckBox();
        btnSave = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();

        lblURL.setText("URL :");

        cbURL.setEditable(true);

        lblMethod.setText("HTTP Method :");

        bgpMethod.add(rbGet);
        rbGet.setSelected(true);
        rbGet.setText("GET");
        rbGet.setToolTipText("HTTP GET Method");
        rbGet.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbGet.setMargin(new java.awt.Insets(0, 0, 0, 0));

        bgpMethod.add(rbPost);
        rbPost.setText("POST");
        rbPost.setToolTipText("HTTP Post Method");
        rbPost.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbPost.setMargin(new java.awt.Insets(0, 0, 0, 0));

        tbParams.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Param", "Value"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        spParams.setViewportView(tbParams);

        cbConvertHTML.setText("Convert HTML to XHTML");
        cbConvertHTML.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbConvertHTML.setMargin(new java.awt.Insets(0, 0, 0, 0));

        btnSave.setText("Save");
        btnSave.setToolTipText("Save in the history");

        btnRemove.setText("Remove");
        btnRemove.setToolTipText("Remove from the history");


        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, spParams, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(lblURL)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cbURL, 0, 350, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(lblMethod)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(rbGet)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(rbPost)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 85, Short.MAX_VALUE)
                        .add(btnSave)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnRemove))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, cbConvertHTML))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblURL)
                    .add(cbURL, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(20, 20, 20)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblMethod)
                    .add(rbGet)
                    .add(rbPost)
                    .add(btnSave)
                    .add(btnRemove))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(spParams, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbConvertHTML)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgpMethod;
    private javax.swing.JButton btnRemove;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox cbConvertHTML;
    private javax.swing.JComboBox cbURL;
    private javax.swing.JLabel lblMethod;
    private javax.swing.JLabel lblURL;
    private javax.swing.JRadioButton rbGet;
    private javax.swing.JRadioButton rbPost;
    private javax.swing.JScrollPane spParams;
    private ExportableTable tbParams;
    // End of variables declaration//GEN-END:variables
    
}
