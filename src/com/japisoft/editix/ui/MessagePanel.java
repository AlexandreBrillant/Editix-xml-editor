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

import com.japisoft.editix.main.steps.EditiXLookAndFeel;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.ApplicationModel.ApplicationModelListener;

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
			if ( "information".equals( key ) ) {
				setForeground( EditiXLookAndFeel.INFORMATION_COLOR );
			} else
				setForeground( EditiXLookAndFeel.ERROR_COLOR );
			currentMessage = ( String )values[ 0 ];
			currentMessage = currentMessage.replace( '\n', '-' );
			visibleModeFlip = true;
			alpha = 0f;
			if ( timer != null ) {
				timer.stop();
			} else
				timer = new Timer( 20, this );
			timer.start();
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
