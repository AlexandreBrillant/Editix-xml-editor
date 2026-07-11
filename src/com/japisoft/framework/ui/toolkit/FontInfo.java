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

package com.japisoft.framework.ui.toolkit;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class FontInfo {
	
    public static String getDefaultFontDirectory() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return "C:\\Windows\\Fonts\\";
        } else if (os.contains("mac")) {
            return "/Library/Fonts/";
        } else if (os.contains("nix") || os.contains("nux") || os.contains("linux")) {
            File usrFonts = new File("/usr/share/fonts/");
            if (usrFonts.exists()) {
                return "/usr/share/fonts/truetype/";
            }
            String home = System.getProperty("user.home");
            File localFonts = new File(home + "/.local/share/fonts/");
            if (localFonts.exists()) {
                return home + "/.local/share/fonts/";
            }
            return "/usr/share/fonts/";
        } else {
            return "./fonts/";
        }
    }

    
    public static boolean isMonospace(Font font) {
        FontMetrics metrics = java.awt.Toolkit.getDefaultToolkit().getFontMetrics(font);
        
        int widthI = metrics.charWidth('i');
        int widthW = metrics.charWidth('W');
        int widthM = metrics.charWidth('m');
        int widthSpace = metrics.charWidth(' ');

        int tolerance = 2;
        return Math.abs(widthI - widthW) <= tolerance &&
               Math.abs(widthI - widthM) <= tolerance &&
               Math.abs(widthI - widthSpace) <= tolerance;
    }
    
    public static String[] getAvailableFontFamilyNames( boolean monoSpace ) {
    	Font[] font = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
    	ArrayList<String> res = new ArrayList<String>();
    	String lastFamily = null;
    	for ( Font f : font ) {
			if ( lastFamily == null || !lastFamily.equals( f.getFamily() ) ) {
		    		if ( monoSpace && isMonospace( f ) ) {
	    				res.add( lastFamily = f.getFamily() );
		    		} else
		    			res.add( lastFamily = f.getFamily() );				
			}
    	}
    	Collections.sort( res );
    	return res.toArray( new String[ res.size() ] );
    }

    public static void main( String[] args ) {
    	GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontNames = ge.getAvailableFontFamilyNames();

        for (String fontName : fontNames) {
            Font font = new Font(fontName, Font.PLAIN, 12);
            if (isMonospace(font)) {
                System.out.println(fontName + " → Monospace");
            } else {
                System.out.println(fontName + " → Proportionnelle");
            }
        }
    }
    
}