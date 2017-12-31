package com.japisoft.framework.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.application.descriptor.ActionModel;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.ui.toolkit.BrowserCaller;

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
public class InitPanel extends JPanel implements MouseListener, ActionListener {
	
	static final String PREFERENCE_GROUP = "interface";
	static final String PREFERENCE_NAME = "initialDocument";
	
	/** Depending the preference value */
	public static boolean canBeShown() {
		return Preferences.getPreference(
				PREFERENCE_GROUP, 
				PREFERENCE_NAME,
				true );
	}

	public InitPanel() {
		this( null );
	}

	public InitPanel( String message ) {
		initComponents( message );
	}

	public void mouseClicked(MouseEvent e) {
		if ( e.getSource() == openLbl ) {
			ActionModel.activeActionById( "new", null );
		} else
		if ( e.getSource() == helpLbl ) {
			ActionModel.activeActionById( "Manual", null );
		} else
		if ( e.getSource() == urlLbl ) {
			BrowserCaller.displayURL( ApplicationModel.COMPANY_URL );
		}
	}

	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}

	public void actionPerformed(ActionEvent e) {
		Preferences.setPreference(
				PREFERENCE_GROUP, 
				PREFERENCE_NAME, 
				openNextCb.isSelected() );
	}	

    private void initComponents( String message ) {
        logoLbl = new javax.swing.JLabel();
        
        
        if ( ApplicationModel.APP_IMG_PATH != null ) {        
	        logoLbl.setIcon( 
	        		new ImageIcon( 
	        				ClassLoader.getSystemResource( 
	        						ApplicationModel.APP_IMG_PATH ) ) );
        }
        logoLbl.setHorizontalAlignment( JLabel.CENTER );
        
        openLbl = new javax.swing.JLabel();
        helpLbl = new javax.swing.JLabel();
        openNextCb = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        versionLbl = new javax.swing.JLabel();
        urlLbl = new javax.swing.JLabel();

        logoLbl.setBackground(new java.awt.Color(255, 255, 255));
        logoLbl.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        logoLbl.setOpaque(true);

        
        
        if ( message != null ) {
        	openLbl.setText( message );
        } else {
        
        	openLbl.setForeground(new java.awt.Color(51, 51, 255));
        	openLbl.setText("Create a new document");

        }
       	
        helpLbl.setForeground(new java.awt.Color(51, 51, 255));
       	helpLbl.setText("User Manual");
        	
        openNextCb.setSelected(true);
       	openNextCb.setText("Open next time");
        openNextCb.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        openNextCb.setMargin(new java.awt.Insets(0, 0, 0, 0));

       	jLabel1.setText("Version :");
        versionLbl.setFont(new java.awt.Font("Tahoma", 1, 11));
       	versionLbl.setText( ApplicationModel.getAppVersion() );

        urlLbl.setFont(new java.awt.Font("Tahoma", 0, 12));
        urlLbl.setForeground(new java.awt.Color(0, 102, 51));
        urlLbl.setText( ApplicationModel.COMPANY_URL );
        
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
                .add(logoLbl, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 170, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(openLbl)
                    .add(openNextCb))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(helpLbl)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(versionLbl)
                    .add(urlLbl))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>                        

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
    private javax.swing.JLabel helpLbl;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel urlLbl;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel logoLbl;
    private javax.swing.JLabel openLbl;
    private javax.swing.JCheckBox openNextCb;
    private javax.swing.JLabel versionLbl;
    // End of variables declaration                   

}
