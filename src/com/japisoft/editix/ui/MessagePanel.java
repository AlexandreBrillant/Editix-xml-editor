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
// 
// AI Training Restriction :
// This source code is provided for human use only.
// Using this code to train, fine-tune, or develop AI models,
// machine learning systems, or similar technologies is
// STRICTLY PROHIBITED. Violations will terminate all rights
// under the applicable license.

package com.japisoft.editix.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.Timer;

import com.japisoft.editix.main.steps.lookandfeel.EditiXLookAndFeel;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.ApplicationModel.ApplicationModelListener;

public class MessagePanel extends JPanel 
	implements ApplicationModelListener, ActionListener {

	public MessagePanel() {
		ApplicationModel.addApplicationModelListener( this );
		setOpaque( false );
		setFont( getFont().deriveFont( Font.BOLD ) );
	}

	private String currentMessage = null;
	private Timer timer = null;
	
	public void fireApplicationData( 
			String key, Object... values ) {
				
		if ( "information".equals( key ) || "error".equals( key ) ) {

			currentMessage = ( String )values[ 0 ];
					
			if ( currentMessage == null )
				currentMessage = "[EMPTY Message]";
			
			if ( !EditixFactory.externalMessage() ) {
			
				if ( "information".equals( key ) ) {
					setForeground( EditiXLookAndFeel.INFORMATION_COLOR );
				} else
					setForeground( EditiXLookAndFeel.ERROR_COLOR );
				currentMessage = currentMessage.replace( '\n', '-' );
				visibleModeFlip = true;
				alpha = 0f;
				if ( timer != null ) {
					timer.stop();
				} else
					timer = new Timer( 10, this );
				timer.start();
			
			} else {
				
				if ( "information".equals( key ) ) {
					EditixFactory.buildAndShowInformationDialog( currentMessage );
				} else {
					EditixFactory.buildAndShowErrorDialog( currentMessage );
				}
				
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		repaint();
	}

	private float alpha = 0f;
	private boolean visibleModeFlip = true;
	
	public void paint( Graphics g ) {
		Graphics2D g2 = ( Graphics2D ) g.create();
		g2.setComposite( 
			AlphaComposite.getInstance( 
					AlphaComposite.SRC_OVER, Math.min( alpha, 1f ) ) 
		);
		if ( currentMessage != null ) {
			if ( visibleModeFlip ) {
				alpha += 0.01;
				if ( alpha > 1.8 ) {
					alpha = 1.0f;
					visibleModeFlip = false;
				}
			} else {
				alpha -= 0.02;
				if ( alpha < 0 ) {
					alpha = 0;
					currentMessage = null;
					timer.stop();
				}
			}
		} else {
			if ( alpha > 0 ) {
				alpha -= 0.02;
				if( alpha < 0 ) {
					alpha = 0;
					timer.stop();
				}
			}
		}
		super.paint( g2 );
		g2.dispose();
	}
	
	public void paintComponent( Graphics g ) {  
		super.paintComponent( g );
		if ( currentMessage != null ) {
			int width = g.getFontMetrics().stringWidth( currentMessage );
			g.setColor( getForeground() );
			if ( EditixStatusBar.ACCESSOR == null )
				return;
			int bottom = EditixStatusBar.ACCESSOR.getY() + EditixStatusBar.ACCESSOR.getHeight();
			int left = getWidth() - width - 100; 
			g.fillRoundRect(
				left,
				bottom,
				width + 50,
				g.getFontMetrics().getHeight() + 20,
				10,
				10
			);
			g.setColor( Color.WHITE );
			g.drawString(
				currentMessage,
				left + 25,
				bottom + ( g.getFontMetrics().getHeight() + 20 ) / 2 + g.getFontMetrics().getDescent()
			);
		}
	}

}

