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

package com.japisoft.editix.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.jdesktop.layout.GroupLayout;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.application.descriptor.ActionModel;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.ui.LinkLabel;
import com.japisoft.framework.ui.toolkit.BrowserCaller;
import com.japisoft.p3.Manager;
import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.XMLDocumentInfo;
import com.japisoft.xmlpad.bookmark.BookmarkContext;
import com.japisoft.xmlpad.tree.parser.Parser;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 */
public class EditixInitPanel extends javax.swing.JPanel 
	implements 
		IXMLPanel, 
		MouseListener,
		ActionListener {
	
    /** Creates new form EditixInitPanel */
    public EditixInitPanel() {
        initComponents();
        initContent();
    }

    public Action getAction(String actionId) {
    	return null;
    }
    
    public IXMLPanel getPanelParent() {
    	return null;
    }
    
	@Override
	public String getCurrentDocumentLocation() {
		return null;
	}	
    
    
    public Parser createNewParser( boolean lightweightMode ) {
    	return null;
    }
    
    public void dispose() {}
    public void setAutoDisposeMode(boolean disposeMode) {}    

	public XMLContainer getMainContainer() {
		return null;
	}
	
	public BookmarkContext getBookmarkContext() {
		return null;
	}	
	
	public XMLContainer getSelectedContainer() {
		return null;
	}		
	
	public XMLContainer getSubContainerAt(int index) {
		return null;
	}
	
	public void selectSubContainer(IXMLPanel panel) {
	}	

	public int getSubContainerCount() {
		return 0;
	}
	
	public void setDocumentInfo(XMLDocumentInfo info) {
	}	

	public Iterator getProperties() {
		return null;
	}

	public Object getProperty(String name, Object def) {
		return null;
	}

	public Object getProperty(String name) {
		return null;
	}

	public XMLContainer getSubContainer(String type) {
		return null;
	}

	public JComponent getView() {
		return this;
	}

	public void prepareToSave() {
	}	
	public void postLoad() {
	}
	
	public boolean reload() {
		return false;
	}	
	
	public void copy() {
	}
	public void cut() {
	}
	public void paste() {
	}
		
	@Override
	public Object print() {
		return null;
	}
	
	public void setProperty(String name, Object content) {
	}

	public void mouseClicked(MouseEvent e) {
		if ( e.getSource() == openLbl ) {
			ActionModel.activeActionById( "new", null );
		} else
		if ( e.getSource() == helpLbl ) {
			if ( "User Manual".equals( helpLbl.getText() ) )
				ActionModel.activeActionById( "Manual", null );
			else {
				if ( !ApplicationModel.INTERFACE_BUILDER.runAction( "register" ) ) 
					BrowserCaller.displayURL( "https://www.editix.com/buy.html" );
			}
		} else
		if ( e.getSource() == urlLbl ) {
			BrowserCaller.displayURL( "https://www.editix.com" );
		}
	}
	
	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void actionPerformed(ActionEvent e) {
		Preferences.setPreference("interface", "initialDocument", openNextCb.isSelected() );
	}	

	private void initContent() {
		logoLbl.setIcon( new ImageIcon( ClassLoader.getSystemResource( "images/logo.png" ) ) );
        versionLbl.setText( ApplicationModel.getAppYear() );
        if ( Manager.hasValidRegisteredFile() ) {
        	helpLbl.setText( "User Manual" );
        } else {
        	helpLbl.setText( "Your version will expire in " + Manager.lastRegisteredDay() + " days, Activate Now");
        }
	}
	
   private void initComponents() {
        logoLbl = new javax.swing.JLabel();
        
        logoLbl.setHorizontalAlignment( JLabel.LEFT );

        openLbl = new LinkLabel("Create a new document");
        helpLbl = new LinkLabel( "Activate" );
        
        
        openNextCb = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        versionLbl = new javax.swing.JLabel();
        urlLbl = new LinkLabel("https://www.editix.com");
        
        logoLbl.setOpaque(true);

        // helpLbl.setForeground(new java.awt.Color(51, 51, 255));
        	
        openNextCb.setSelected(true);
       	openNextCb.setText("Open next time");
        openNextCb.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        openNextCb.setMargin(new java.awt.Insets(0, 0, 0, 0));
        
       	jLabel1.setText("Version :");
       	versionLbl.setText( ApplicationModel.getAppYear() );
        
        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(logoLbl, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 474, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(openLbl)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 275, Short.MAX_VALUE)
                        .add(openNextCb))
                    .add(helpLbl)
                    .add(layout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(versionLbl)
                        .add(47, 47, 47)
                        .add(urlLbl))
                    .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 474, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(logoLbl, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 195, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(openLbl,GroupLayout.PREFERRED_SIZE,20,GroupLayout.PREFERRED_SIZE)
                    .add(openNextCb))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(helpLbl,GroupLayout.PREFERRED_SIZE,20,GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(versionLbl)
                    .add(urlLbl,GroupLayout.PREFERRED_SIZE,20,GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
   }

    public String toString() {
		return "Starting";
	}    

    public void addNotify() {
    	super.addNotify();
    	openLbl.addMouseListener( this );
    	helpLbl.addMouseListener( this );
    	openNextCb.addActionListener( this );
    	urlLbl.addMouseListener( this );
    }

    public void removeNotify() {
    	super.removeNotify();
    	openLbl.removeMouseListener( this );
    	helpLbl.removeMouseListener( this );
    	urlLbl.removeMouseListener( this );
    	openNextCb.removeActionListener( this );
    }

    // Variables declaration - do not modify                     
    private LinkLabel helpLbl;
    private javax.swing.JLabel jLabel1;
    private LinkLabel urlLbl;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel logoLbl;
    private LinkLabel openLbl;
    private javax.swing.JCheckBox openNextCb;
    private javax.swing.JLabel versionLbl;
    // End of variables declaration                   
    
}

