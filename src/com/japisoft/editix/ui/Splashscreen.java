package com.japisoft.editix.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;

import com.japisoft.p3.Manager;
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
public class Splashscreen extends JWindow {

	private JProgressBar pb = null;

	private Splashscreen() {
		super();
		
		try {
			GraphicsEnvironment ge = 
	            GraphicsEnvironment.getLocalGraphicsEnvironment();
	        GraphicsDevice gd = ge.getDefaultScreenDevice();
	        final boolean isTranslucencySupported = 
	            gd.isWindowTranslucencySupported(WindowTranslucency.TRANSLUCENT);		
	        if ( isTranslucencySupported ) {
	        	setOpacity( 0.9f );
	        }
		} catch( Throwable th ) {}
		
		JLabel l = ( JLabel )getContentPane().add(
				new JLabel(EditixFactory.getImageIcon("images/logo.png")));

		getContentPane().add( pb = new JProgressBar( ), BorderLayout.SOUTH );
		
		pb.setBorderPainted( false );
		pb.setForeground( new Color( Integer.parseInt( "58A27D", 16 ) ) );
		
			
		getContentPane().add( new JLabel( "EditiX Xml Editor - Community Edition" ), BorderLayout.NORTH );

		pack();
	}

	static Splashscreen ss = null;

	public static void start() {
		if (ss == null) {
			ss = new Splashscreen();
			adapter = new SplashAdapter();
		}
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		ss.setLocation(
			(dim.width - ss.getWidth()) / 2,
			(dim.height - ss.getHeight()) / 2);
		ss.setBackground( Color.white );
		ss.setVisible(true);
	}
	
	public static void progress( int indice, int max ) {
		ss.pb.setMaximum( max );
		ss.pb.setValue( indice );
	}

	public static void restart() {
	}

	public static void stopNow() {
		try {
			ss.setVisible(false);
			ss.dispose();
		} catch (Throwable th) {
		}
	}

	static void hideSplashScreen() {
		ss.removeWindowListener(adapter);
		ss.setVisible(false);
		ss.dispose();
		adapter = null;
		ss = null;
		
		if ( EditixFrame.THIS == null )
			return;
		
		EditixFrame.THIS.toFront();
		EditixFrame.THIS.requestFocus();
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if (container != null)
			container.requestFocus();
	}

	public static void stop(boolean delayed) {
		if (delayed) {
			Timer t = new Timer();
			t.schedule(new TimerTask() {
				public void run() {
					ss.toFront();
				}
			}, 500);
			t.schedule(new TimerTask() {
				public void run() {
					hideSplashScreen();
				}
			}, 2500);
		} else
			hideSplashScreen();
	}

	static SplashAdapter adapter = null;

	static class SplashAdapter extends WindowAdapter {
		public SplashAdapter() {
			super();
		}
		public void windowDeactivated(WindowEvent e) {
			ss.toFront();
		}

		public void windowLostFocus(WindowEvent e) {
			ss.toFront();
		}
	}

}
