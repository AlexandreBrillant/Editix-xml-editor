package com.japisoft.framework.dialog.help;

import java.awt.Dimension;
import java.awt.Frame;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.dialog.BasicOKDialogComponent;

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
public class ManualDialog extends BasicOKDialogComponent implements
		HyperlinkListener {

	/** This is a path from the classpath */
	public static String DEF_MANUAL_PATH = "doc/manual.html";

	private JEditorPane pane = null;

	static String getTITLE() {
		return "Manual of " + ApplicationModel.SHORT_APPNAME;
	}

	static String getCOMMENT() {
		if (DEF_MANUAL_PATH.startsWith("http://"))
			return "This manual is located at " + DEF_MANUAL_PATH;
		return null;
	}

	/** It uses the default location */
	public ManualDialog(Frame owner) {
		this( owner, null );
	}
	
	/**
	 * @param owner The owner frame
	 * @param location The location as a path or from the classpath
	 */
	public ManualDialog(Frame owner, String location ) {
		super( owner, "Manual", getTITLE(), getCOMMENT(), null );
		setModal( false );

		try {
			
			String tmp = location;
			if ( tmp == null )
				tmp = DEF_MANUAL_PATH;
			URL url = null;
			if ( tmp.startsWith( "http:" ) )
				url = new URL( tmp );
			else
				url = ClassLoader.getSystemResource( tmp );
			
			pane = new JEditorPane();
			//pane = new JEditorPane( url );
			pane.setEditable(false);
			pane.setText("Reading " + DEF_MANUAL_PATH + " ...");

			JScrollPane sp = new JScrollPane(pane);
			sp.setPreferredSize(new Dimension(650, 500));
			setUI(sp);

			load(url);

		} catch (IOException exc) {
		}
	}

	private void load(URL url) {
		final URL url2 = url;
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					pane.setPage(url2);
				} catch (Exception exc) {
					pane.setText("Can't read " + url2);
				}
			}
		});
	}

	public void addNotify() {
		super.addNotify();
		pane.addHyperlinkListener(this);
	}

	public void removeNotify() {
		super.removeNotify();
		pane.removeHyperlinkListener(this);
	}

	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			pane.scrollToReference(e.getDescription().substring(1));
		}
	}

}
