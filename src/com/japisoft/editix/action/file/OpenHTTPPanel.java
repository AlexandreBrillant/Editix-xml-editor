package com.japisoft.editix.action.file;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFactory.XMLDocumentInfoFileFilter;
import com.japisoft.xmlpad.XMLDocumentInfo;

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
public class OpenHTTPPanel extends JPanel implements ActionListener, PopupMenuListener {

	/**
     * Creates new form GETPOSTPanel
     */
    public OpenHTTPPanel() {
        initComponents();
        
        tbHeaders.setEnabled( false );
        tbParameters.setEnabled( false );
        
        cbURL.setEditable( false );
        
        EditixFactory.fillComboBoxFilter( cbOpenAs );
        
        DefaultTableModel modelParameters = new DefaultTableModel( new String[] { "Name", "Value" }, 50 );
        tbParameters.setModel( modelParameters );

        DefaultTableModel modelHeaders = new DefaultTableModel( new String[] { "Name", "Value" }, 50 );
        tbHeaders.setModel( modelHeaders );
        
        btSave.setEnabled( false );
        btEdit.setEnabled( false );
        btRemove.setEnabled( false );
        
        init();
    }

	@Override
	public void addNotify() {
		super.addNotify();
		btNew.addActionListener( this );
		btSave.addActionListener( this );
		btRemove.addActionListener( this );
		btEdit.addActionListener( this );
		cbURL.addActionListener( this );
		
		cbURL.addPopupMenuListener( this );
	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		btNew.removeActionListener( this );
		btSave.removeActionListener( this );
		btRemove.removeActionListener( this );
		btEdit.removeActionListener( this );
		cbURL.removeActionListener( this );
		
		cbURL.removePopupMenuListener( this );
	}

	@Override
	public void popupMenuCanceled(PopupMenuEvent arg0) {
	}
	@Override
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
	}
	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		HTTPConfig config = ( HTTPConfig )cbURL.getSelectedItem();
		try {
			config.dom = getConfiguration();
		} catch( Exception exc ) {
			exc.printStackTrace();
		}
	}

	private File getConfigFile() {
		return new File( 
			EditixApplicationModel.getAppUserPath(), "http.xml" 
		);
	}
	
	private void init() {
		File f = getConfigFile(); 
		if ( f.exists() ) {
			
			btEdit.setEnabled( true );
			
			try {				
				DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document doc = db.parse( f );
				Element root = doc.getDocumentElement();
				NodeList nl = root.getElementsByTagName( "request" );

				cbURL.removeAllItems();

				for ( int i = 0; i < nl.getLength(); i++ ) {
					Element e = ( Element )nl.item( i );
					addConfiguration( e, false );
				}

				if ( nl.getLength() > 0 )
					selectConfig( 0 );

			} catch( Exception exc ) {
				exc.printStackTrace();
			}
		}
	}
	
	private void save( Element requests ) {
		try {
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.transform( new DOMSource( requests ), new StreamResult( getConfigFile() ) );
		} catch( Exception exc ) {
			EditixFactory.buildAndShowErrorDialog( "Can't save : " + exc.getMessage() );
		}
	}

	private void fillDOMFromTable( Document doc, JTable table, Element target ) {
		TableModel tm = table.getModel();
		for ( int i = 0; i < tm.getRowCount(); i++ ) {
			String name = ( String )tm.getValueAt( i, 0 );
			String value = ( String )tm.getValueAt( i, 1 );
			if ( name == null || "".equals( name ) )
				continue;
			Element p = doc.createElement( "param" );
			p.setAttribute( "name", name );
			p.setAttribute( "value", value );
			target.appendChild( p );
		}
	}

	public Element getConfiguration() throws Exception {
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = db.newDocument();
		Element request = doc.createElement( "request" );
		request.setAttribute( "method", rbGET.isSelected() ? "GET" : "POST" );

		HTTPConfig config = ( HTTPConfig )cbURL.getSelectedItem();
		request.setAttribute( "url", config.url );

		Element params = doc.createElement( "parameters" );
		fillDOMFromTable( doc, tbParameters, params );
		
		Element headers = doc.createElement( "headers" );
		fillDOMFromTable( doc, tbHeaders, headers );

		request.appendChild( params );
		request.appendChild( headers );
		
		XMLDocumentInfoFileFilter docInfo = ( XMLDocumentInfoFileFilter )cbOpenAs.getSelectedItem();
		request.setAttribute( "openAs", docInfo.getType() );
		
		return request;
	}

	private void fillTableFromDOM( JTable table, Element source ) {
		DefaultTableModel modelParameters = new DefaultTableModel( new String[] { "Name", "Value" }, 50 );
		NodeList nl = source.getElementsByTagName( "param" );
		int n = 0;
		for ( int i = 0; i < nl.getLength(); i++ ) {
			Element e = ( Element )nl.item( i );
			String name = e.getAttribute( "name" );
			String value = e.getAttribute( "value" );
			modelParameters.setValueAt( name, n, 0 );
			modelParameters.setValueAt( value, n++, 1 );
			if ( n >= 50 )
				break;
		}
		table.setModel( modelParameters );
	} 

	public void addConfiguration( Element request, boolean autoSave ) {
		
		btRemove.setEnabled( true );
		btSave.setEnabled( true );
		
		
		HTTPConfig config = null;
		if ( autoSave ) {
			// Save the last one
			config = ( HTTPConfig )cbURL.getSelectedItem();
			if ( config != null ) {
				try {
					config.dom = getConfiguration();
				} catch( Exception exc ) {
					exc.printStackTrace();
				}
			}
		}
		
		config = new HTTPConfig( request.getAttribute( "url" ), request );
		cbURL.addItem( config );
		tbHeaders.setEnabled( true );
		tbParameters.setEnabled( true );
	}

	private void selectConfig( Element request ) {
		String method = request.getAttribute( "method" );		

		if ( "POST".equals( method ) )
			rbPOST.setSelected( true );
		else
			rbGET.setSelected( true );
		
		NodeList nl = request.getChildNodes();
		for ( int i = 0; i < nl.getLength(); i++ ) {
			Node n = nl.item( i );
			if ( "parameters".equals( n.getNodeName() ) ) {
				fillTableFromDOM( tbParameters, ( Element )n );
			} else
			if ( "headers".equals( n.getNodeName() ) ) {
				fillTableFromDOM( tbHeaders, ( Element )n );				
			}
		}
		String openAs = request.getAttribute( "openAs" );
		for ( int i = 0; i < cbOpenAs.getItemCount(); i++ ) {
			XMLDocumentInfoFileFilter xdi = ( XMLDocumentInfoFileFilter )cbOpenAs.getItemAt( i );
			if ( openAs.equals( xdi.getType() ) ) {
				cbOpenAs.setSelectedIndex( i );
				break;
			}
		}
	}
	
	private void selectConfig( int index ) {
		HTTPConfig config = ( HTTPConfig )cbURL.getItemAt( index );
		Element element = config.dom;
		selectConfig( element );
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if ( e.getSource() == cbURL ) {
			HTTPConfig config = ( HTTPConfig )cbURL.getSelectedItem();
			selectConfig( config.dom );
		} else
		if ( e.getSource() == btNew ) {
			String url = EditixFactory.buildAndShowInputDialog( "URL ?" );
			if ( url != null ) {
				try {
					Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
					Element request = doc.createElement( "request" );
					request.setAttribute( "url", url );
					request.setAttribute( "method", "GET" );
					Element parameters = doc.createElement( "parameters" );
					Element headers = doc.createElement( "headers" );
					request.appendChild( parameters );
					request.appendChild( headers );
					addConfiguration( request, true );
					cbURL.setSelectedIndex( cbURL.getItemCount() - 1 );
					
				} catch( Throwable th ) {
					EditixFactory.buildAndShowErrorDialog( "Error detected " + th.getMessage() );
					th.printStackTrace();
				}
			}
		} else
		if ( e.getSource() == btSave ) {
			try {
				Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
				Element requests = doc.createElement( "requests" );
				for ( int i = 0; i < cbURL.getItemCount(); i++ ) {
					HTTPConfig config = ( HTTPConfig )cbURL.getItemAt( i );
					Element newElement = ( Element )doc.adoptNode( config.dom );
					requests.appendChild( newElement );
					save( requests );
				}
				btEdit.setEnabled( true );
			} catch( Throwable th ) {
				EditixFactory.buildAndShowErrorDialog( "Error detected " + th.getMessage() );
				th.printStackTrace();
			}
		} else
		if ( e.getSource() == btRemove ) {
			int i = cbURL.getSelectedIndex();
			if ( i >= 0 ) {
				cbURL.removeItemAt( i );
				selectConfig( 0 );
			}
		} else
		if ( e.getSource() == btEdit ) {
			OpenAction.openFile( "XML", false, getConfigFile(), null );
		}
	}

	class HTTPConfig {
		private String url;
		private Element dom;

		public HTTPConfig( String url, Element config ) {
			this.url = url;
			this.dom = config;
		}
		
		public String toString() {
			return url;
		}
	}
	

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        cbURL = new javax.swing.JComboBox();
        rbGET = new javax.swing.JRadioButton();
        rbPOST = new javax.swing.JRadioButton();
        tpMain = new javax.swing.JTabbedPane();
        pnParameters = new javax.swing.JPanel();
        spParameters = new javax.swing.JScrollPane();
        tbParameters = new javax.swing.JTable();
        pnHeaders = new javax.swing.JPanel();
        spHeaders = new javax.swing.JScrollPane();
        tbHeaders = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        cbOpenAs = new javax.swing.JComboBox();
        btSave = new javax.swing.JButton();
        btRemove = new javax.swing.JButton();
        btNew = new javax.swing.JButton();
        btEdit = new javax.swing.JButton();

        jLabel1.setText("URL");

        cbURL.setEditable(true);

        buttonGroup1.add(rbGET);
        rbGET.setSelected(true);
        rbGET.setText("GET");

        buttonGroup1.add(rbPOST);
        rbPOST.setText("POST");

        spParameters.setViewportView(tbParameters);

        javax.swing.GroupLayout pnParametersLayout = new javax.swing.GroupLayout(pnParameters);
        pnParameters.setLayout(pnParametersLayout);
        pnParametersLayout.setHorizontalGroup(
            pnParametersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(spParameters, javax.swing.GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
        );
        pnParametersLayout.setVerticalGroup(
            pnParametersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(spParameters, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
        );

        tpMain.addTab("Parameters", pnParameters);

        spHeaders.setViewportView(tbHeaders);

        javax.swing.GroupLayout pnHeadersLayout = new javax.swing.GroupLayout(pnHeaders);
        pnHeaders.setLayout(pnHeadersLayout);
        pnHeadersLayout.setHorizontalGroup(
            pnHeadersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(spHeaders, javax.swing.GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
        );
        pnHeadersLayout.setVerticalGroup(
            pnHeadersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(spHeaders, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
        );

        tpMain.addTab("HTTP Headers", pnHeaders);

        jLabel2.setText("Open as");

        btSave.setText("Save");

        btRemove.setText("Remove");

        btNew.setText("New");

        btEdit.setText("Edit");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tpMain)
                            .addComponent(cbURL, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(rbGET)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(rbPOST)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(19, 19, 19)
                        .addComponent(cbOpenAs, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btNew)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btRemove)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btEdit)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cbURL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbGET)
                    .addComponent(rbPOST))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tpMain)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(cbOpenAs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btSave)
                    .addComponent(btRemove)
                    .addComponent(btNew)
                    .addComponent(btEdit))
                .addGap(20, 20, 20))
        );

        tpMain.getAccessibleContext().setAccessibleName("Parameters");
    }// </editor-fold>                        


    // Variables declaration - do not modify                     
    private javax.swing.JButton btEdit;
    private javax.swing.JButton btNew;
    private javax.swing.JButton btRemove;
    private javax.swing.JButton btSave;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox cbOpenAs;
    private javax.swing.JComboBox cbURL;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel pnHeaders;
    private javax.swing.JPanel pnParameters;
    private javax.swing.JRadioButton rbGET;
    private javax.swing.JRadioButton rbPOST;
    private javax.swing.JScrollPane spHeaders;
    private javax.swing.JScrollPane spParameters;
    private javax.swing.JTable tbHeaders;
    private javax.swing.JTable tbParameters;
    private javax.swing.JTabbedPane tpMain;
    // End of variables declaration   

}
