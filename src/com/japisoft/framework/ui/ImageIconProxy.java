package com.japisoft.framework.ui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;

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
public class ImageIconProxy extends ImageIcon {
	private ImageIcon rootIcon;
	private ImageIcon overridingIcon;
	private int xicon, yicon;
	
	/** Main icon */
	public ImageIconProxy( ImageIcon rootIcon ) {
		this.rootIcon = rootIcon;
	}

	/** Main icon and a secondary one more little that will be drawn on the main one
	 * @param rootIcon the main icon
	 * @param x the location of the second one
	 * @param x the location of the second one
	 * @param overridingIcon the second one
	 */
	public ImageIconProxy( ImageIcon rootIcon, int x, int y, ImageIcon overridingIcon ) {
		this( rootIcon );
		setOverridingIcon( x, y, overridingIcon );
	}

	public void setOverridingIcon( int x, int y, ImageIcon overridingIcon ) {
		this.xicon = x;
		this.yicon = y;
		this.overridingIcon = overridingIcon;
	}

	public int getIconHeight() {
		return rootIcon.getIconHeight();
	}

	public int getIconWidth() {
		return rootIcon.getIconWidth();
	}

	public Image getImage() {
		return rootIcon.getImage();
	}

	public int getImageLoadStatus() {
		return rootIcon.getImageLoadStatus();
	}

	public ImageObserver getImageObserver() {
		return rootIcon.getImageObserver();
	}

	public boolean activeOverringImage = false;

	public synchronized void paintIcon(Component arg0, Graphics arg1, int x, int y) {
		arg1.drawImage( rootIcon.getImage(), x, y, null );
		if ( activeOverringImage && overridingIcon != null ) {
			arg1.drawImage( overridingIcon.getImage(), x + xicon, y + yicon, null );
		}
	}
	
	public void setImage(Image arg0) {
		rootIcon.setImage(arg0);
	}

	public void setImageObserver(ImageObserver arg0) {
		rootIcon.setImageObserver(arg0);
	}

	public static void main( String[] args ) {
		JFrame fr = new JFrame();
		fr.setSize( 300, 300 );
		ImageIcon ii = new ImageIcon( "C:\\travail\\soft\\jxmlpad\\src\\images\\OkCancelDialog.gif" );
		ImageIcon ii2 = new ImageIcon( "C:\\travail\\soft\\jxmlpad\\src2\\com\\japisoft\\xmlpad\\tree\\little_bug_red.png" );
		ImageIconProxy iip = new ImageIconProxy( ii );
		iip.setOverridingIcon( 10, 10, ii2 );
		iip.activeOverringImage = true;
		fr.add( new JButton( iip ) );
		fr.setVisible( true );
	}
	
}
