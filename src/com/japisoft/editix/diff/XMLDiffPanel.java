// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2026 Alexandre Brillant
//
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
// AI Training Restriction :
// This source code is provided for human use only.
// Using this code to train, fine-tune, or develop AI models,
// machine learning systems, or similar technologies is
// STRICTLY PROHIBITED. Violations will terminate all rights
// under the applicable license.

package com.japisoft.editix.diff;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Highlighter.HighlightPainter;

import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlunit.diff.Comparison;
import org.xmlunit.diff.ComparisonListener;
import org.xmlunit.diff.ComparisonResult;
import org.xmlunit.diff.DOMDifferenceEngine;
import org.xmlunit.diff.Comparison.Detail;

import com.japisoft.editix.action.file.DocumentRenderer;
import com.japisoft.editix.main.steps.lookandfeel.EditiXLookAndFeel;
import com.japisoft.editix.main.steps.lookandfeel.EditiXPlasticTheme;
import com.japisoft.editix.main.steps.lookandfeel.EditixLook;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.framework.ui.text.FileTextField;
import com.japisoft.framework.xml.XMLFileData;
import com.japisoft.framework.xml.XMLToolkit;
import com.japisoft.framework.xml.dom.DOMLineParser;
import com.japisoft.framework.xml.parser.FPParser;

import com.japisoft.framework.xml.parser.dom.DomNodeFactory;
import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.XMLDocumentInfo;
import com.japisoft.xmlpad.bookmark.BookmarkContext;
import com.japisoft.xmlpad.tree.parser.Parser;


public class XMLDiffPanel extends JPanel 
		implements IXMLPanel, ActionListener, ComparisonListener, ListSelectionListener, XMLDiffBarListener {

	private XMLContainer leftXMLContainer;
	private XMLContainer rightXMLContainer;
	private XMLContainer focusXMLContainer;
	private DOMDifferenceEngine dde;
	private DefaultListModel<ComparisonItem> reportModel;

	public XMLDiffPanel() {
		leftXMLContainer = new FocusXMLContainer();
		
		leftXMLContainer.setProperty( "file.checker.ignore", "true" );
		
		rightXMLContainer = new FocusXMLContainer();
		
		rightXMLContainer.setProperty( "file.checker.ignore", "true" );
		
		focusXMLContainer = leftXMLContainer;

		XMLDocumentInfo docInfo = new XMLDocumentInfo();
		docInfo.setTreeAvailable( false );
		docInfo.setType( "XML" );
		
		leftXMLContainer.setToolBarAvailable( false );
		rightXMLContainer.setToolBarAvailable( false );
		
		leftXMLContainer.setDocumentInfo( docInfo );
		rightXMLContainer.setDocumentInfo( docInfo );
		
		initComponents();	

		lstReport.setModel( 
			reportModel = new DefaultListModel<XMLDiffPanel.ComparisonItem>() 
		);
	}

	public void setLeftContent( String location, String content ) {
		leftXMLContainer.setText( content );
		leftXMLContainer.setCurrentDocumentLocation( location );
		leftFileName.setText( location );
		leftOk = true;
	}

	@Override
	public void addNotify() {
		super.addNotify();
		leftFileName.setActionListener( this );
		rightFileName.setActionListener( this );	
		btSynchro.addActionListener( this );
		lstReport.addListSelectionListener( this );
		diffBar.setXMLDiffBarListener( this );
	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		leftFileName.setActionListener( null );
		rightFileName.setActionListener( null );
		btSynchro.addActionListener( this );
		lstReport.removeListSelectionListener( this );
		diffBar.setXMLDiffBarListener( null );
	}

	public void valueChanged(ListSelectionEvent e) {
		if ( lstReport.getSelectedIndex() > -1 ) {
			ComparisonItem item = reportModel.get( lstReport.getSelectedIndex() );
			if ( item.leftLine > 0 ) {
				leftXMLContainer.getEditor().setLineNumber( item.leftLine );
			}
			if ( item.rightLine > 0 ) {
				rightXMLContainer.getEditor().setLineNumber( item.rightLine );
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if ( e.getSource() == btSynchro ) {
			runCompare();
		} else {		
			if ( e.getSource() == leftFileName ) {
				readLeft( leftFileName.getText() );
			} else {
				if ( e.getSource() == rightFileName ) {
					readRight( rightFileName.getText() );
				}
			}
		}
	}

	@Override
	public String getCurrentDocumentLocation() {
		return leftFileName.getText();
	}
	
	@Override
	public Object print() {
		return lstReport;
	}
	
	private boolean leftOk = false, rightOk = false;
	
	public boolean readLeft( String fileName ) {
		try {
			XMLFileData xfd = XMLToolkit.getContentFromURI( 
				fileName,
				null
			);
			leftXMLContainer.setText( xfd.getContent() );
			leftXMLContainer.setCurrentDocumentLocationArg( fileName );
			leftOk = true;
			if ( leftOk && rightOk )
				runCompare();
			return true;
		} catch( Throwable th ) {
			EditixFactory.buildAndShowErrorDialog( th.getMessage() );
			return false;
		}		
	}
	
	public boolean readRight( String fileName ) {
		try {
			XMLFileData xfd = XMLToolkit.getContentFromURI(
				fileName,
				null
			);
			rightXMLContainer.setText( xfd.getContent() );
			rightXMLContainer.setCurrentDocumentLocation( fileName );
			rightOk = true;
			if ( leftOk && rightOk )
				runCompare();
			return true;
		} catch( Throwable th ) {
			EditixFactory.buildAndShowErrorDialog( th.getMessage() );
			return false;
		}
	}

	private boolean foundComparison = false;

	public void comparisonPerformed(Comparison comparison, ComparisonResult outcome) {
		foundComparison = true;
		reportModel.addElement( new ComparisonItem( comparison, outcome ) );
	}

	private void removeHighlights( XMLContainer container ) {
		Highlighter highligher = container.getEditor().getHighlighter();
		highligher.removeAllHighlights();
	}
	
	public void notifyLocation( boolean left, int line ) {
		if ( left ) {
			leftXMLContainer.getEditor().setLineNumber( line );
		} else {
			rightXMLContainer.getEditor().setLineNumber( line );
		}
	}

	private boolean runCompare() {
		foundComparison = false;
		reportModel.removeAllElements();
		diffBar.clear();
		removeHighlights( leftXMLContainer );
		removeHighlights( rightXMLContainer );		

		DOMDifferenceEngine dde = new DOMDifferenceEngine();
		dde.addDifferenceListener( this );

		FPParser parser = new FPParser();
		parser.setNodeFactory( new DomNodeFactory() );

		try {

			Document leftDocument = 
					DOMLineParser.getInstance().parseContent( leftXMLContainer.getText() );

			Document rightDocument = 
					DOMLineParser.getInstance().parseContent( rightXMLContainer.getText() );

			dde.compare(
				new DOMSource( leftDocument ),
				new DOMSource( rightDocument ) );
			
			if ( !foundComparison ) {
				EditixFactory.buildAndShowInformationDialog( "No difference found" );
			} else
				displayDifferences( leftDocument, rightDocument );
			
			return true;
			
		} catch( Exception pe ) {
			pe.printStackTrace();
			EditixFactory.buildAndShowErrorDialog( "Can't compare incorrect documents" );
			return false;
		}			
	}

	private void displayDifferences( Document leftDocument, Document rightDocument ) {
		for ( int i = 0; i < reportModel.getSize(); i++ ) {
			ComparisonItem item = reportModel.getElementAt( i );
			if ( item.leftXPath != null ) {
				displayDifference( leftXMLContainer, leftDocument, item.leftXPath, item.message, item );
			}
			if ( item.rightXPath != null ) {
				displayDifference( rightXMLContainer, rightDocument, item.rightXPath, item.message, item );
			}
		}
	}

	private void displayDifference( XMLContainer container, Document doc, String xpath, String message, ComparisonItem item ) {
		try {
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpathEngine = factory.newXPath();
			NodeList set = ( NodeList )xpathEngine.evaluate( xpath, doc, XPathConstants.NODESET );
			for ( int i = 0; i < set.getLength(); i++ ) {
				Node n = set.item( i );
				if ( n instanceof Element ) {
					Element e = ( Element )n;
					int startLine = ( ( Integer )e.getUserData( DOMLineParser.START_LINE_NUMBER_KEY_NAME ) );
					int stopLine = ( ( Integer )e.getUserData( DOMLineParser.STOP_LINE_NUMBER_KEY_NAME ) );
					
					if ( container == leftXMLContainer )
						item.leftLine = startLine;
					else
					if ( container == rightXMLContainer )
						item.rightLine = startLine;
					
					displayDifference( container, startLine, stopLine, message );
				}
			}
		} catch( XPathExpressionException xee ) {	
		}
	}
	
	private void displayDifference( XMLContainer container, int startLine, int stopLine, String message ) {
		javax.swing.text.Element startLineElement = container.getDocument().getDefaultRootElement().getElement( startLine - 1 );
		javax.swing.text.Element stopLineElement = container.getDocument().getDefaultRootElement().getElement( stopLine - 1 );
		
		Color INFORMATION_COLOR = EditiXLookAndFeel.INFORMATION_COLOR;
		Color ERROR_COLOR = EditiXLookAndFeel.ERROR_COLOR;
		
		if ( UIManager.getColor( "editix.diff.info" ) != null ) {
			INFORMATION_COLOR = UIManager.getColor( "editix.diff.info" );
		}
		if ( UIManager.getColor( "editix.diff.error" ) != null ) {
			ERROR_COLOR = UIManager.getColor( "editix.diff.error" );
		}

		if ( startLineElement != null && stopLineElement != null ) {
			try {
				Highlighter highligher = container.getEditor().getHighlighter();
				highligher.addHighlight( 
					startLineElement.getStartOffset(), 
					stopLineElement.getEndOffset(), 
					new ComparisonItemHighlighter( container == leftXMLContainer ? INFORMATION_COLOR : ERROR_COLOR )
				);

				Rectangle startView = container.getEditor().modelToView( startLineElement.getStartOffset() );
				Rectangle stopView = container.getEditor().modelToView( stopLineElement.getEndOffset() );

				diffBar.addRange(
					container == leftXMLContainer,
					startLine, 
					startView, 
					stopView, 
					container.getEditor().getHeight(),
					container == leftXMLContainer ? INFORMATION_COLOR : ERROR_COLOR,
					message 
				);

			} catch( BadLocationException ble ) {
			}		
		}
	}

	public IXMLPanel getPanelParent() {
		return null;
	}

	public XMLContainer getMainContainer() {
		return focusXMLContainer;
	}

	public XMLContainer getSelectedContainer() {
		return focusXMLContainer;
	}
	
	public BookmarkContext getBookmarkContext() {
		return getSelectedContainer().getBookmarkContext();
	}

	public XMLContainer getSubContainer(String type) {
		return null;
	}

	public Parser createNewParser( boolean lightweightMode ) {
		return getSelectedContainer().createNewParser( lightweightMode );
	}

	public int getSubContainerCount() {
		return 2;
	}

	public XMLContainer getSubContainerAt( int index ) {
		if ( index == 0 )
			return leftXMLContainer;
		return rightXMLContainer;
	}

	public void selectSubContainer(IXMLPanel panel) {
	}

	public JComponent getView() {
		return this;
	}

	public void setProperty(String name, Object content) {
		getSelectedContainer().setProperty( name, content );
	}

	public Object getProperty(String name) {
		return getSelectedContainer().getProperty( name );
	}

	public Object getProperty(String name, Object def) {
		return getSelectedContainer().getProperty( name, def );
	}

	public Iterator getProperties() {
		return getSelectedContainer().getProperties();
	}

	public void dispose() {
		leftXMLContainer.dispose();
		rightXMLContainer.dispose();
		focusXMLContainer = null;
	}
	
	public void prepareToSave() {
		getSelectedContainer().prepareToSave();
	}

	public void postLoad() {
		getSelectedContainer().postLoad();
	}

	public boolean reload() {
		return getSelectedContainer().reload();
	}

	public void cut() {
		getSelectedContainer().cut();
	}

	public void copy() {
		getSelectedContainer().copy();
	}

	public void paste() {
		getSelectedContainer().paste();		
	}

	public void setDocumentInfo( XMLDocumentInfo info ) {
		getSelectedContainer().setDocumentInfo( info );
	}

	public void setAutoDisposeMode( boolean disposeMode ) {
		leftXMLContainer.setAutoDisposeMode( disposeMode );
		rightXMLContainer.setAutoDisposeMode( disposeMode );
	}
	
	public Action getAction(String actionId) {
		return getSelectedContainer().getAction( actionId );
	}

	class FocusXMLContainer extends XMLContainer {
		@Override
		public void focus() {
			super.focus();
			focusXMLContainer = this;
		}
	}
	
	class ComparisonItemHighlighter implements HighlightPainter {

		private Color c;
		
		public ComparisonItemHighlighter( Color c ) {
			this.c = c;
		}
		
		public void paint(Graphics g, int p0, int p1, Shape bounds,
				JTextComponent textComponent) {

			FontMetrics metrics = g.getFontMetrics();
			javax.swing.text.Document doc = textComponent.getDocument();
			int lineNo = doc.getDefaultRootElement().getElementIndex(p0);

			Rectangle rect = (Rectangle) bounds;
			int height = metrics.getHeight();
			int x = rect.x;
			int y = rect.y + height * lineNo;
			int width = textComponent.getWidth();

			g.setColor( c );
			g.fillRect(x, y, width, height);

		}

	}
	
	class ComparisonItem {
		
		private String leftXPath,rightXPath;
		private String message;
		int leftLine, rightLine;
		
		public ComparisonItem( Comparison comparison, ComparisonResult outcome ) {

			Detail left = comparison.getControlDetails();
			leftXPath = left.getXPath();
			
			Detail right = comparison.getTestDetails();
			rightXPath = right.getXPath();
			
			message = comparison.toString();

		}
		
		@Override
		public String toString() {
			return message;
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
    	
        leftFileName = new FileTextField( null, "xml" );
        rightFileName = new FileTextField( null, "xml" );
        diffBar = new XMLDiffBar();
        lstReport = new javax.swing.JList();
        spReport = new JScrollPane( lstReport );
        
        btSynchro = new javax.swing.JButton();

        javax.swing.GroupLayout zoomDiffLayout = new javax.swing.GroupLayout(diffBar);
        diffBar.setLayout(zoomDiffLayout);
        zoomDiffLayout.setHorizontalGroup(
            zoomDiffLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 42, Short.MAX_VALUE)
        );
        zoomDiffLayout.setVerticalGroup(
            zoomDiffLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        btSynchro.setIcon( 
        	new ImageIcon( 
        		getClass().getResource( 
        			"documents_exchange.png" )
        	) 
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(spReport)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(leftFileName)
                            .addComponent(leftXMLContainer.getView(), javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(diffBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btSynchro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rightFileName)
                            .addComponent(rightXMLContainer.getView(), javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE))))
                .addGap(4, 4, 4))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(rightFileName, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(leftFileName)
                        .addComponent(btSynchro)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(diffBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(leftXMLContainer.getView(), javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 337, Short.MAX_VALUE)
                    .addComponent(rightXMLContainer.getView(), javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spReport, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>                        
    
    // Variables declaration - do not modify                     

    private javax.swing.JButton btSynchro;
    private javax.swing.JList lstReport;
    private javax.swing.JScrollPane spReport;
    private FileTextField leftFileName;
    private FileTextField rightFileName;
    
    private XMLDiffBar diffBar;

    // End of variables declaration         	
    
}
