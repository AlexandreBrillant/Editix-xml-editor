package com.japisoft.framework.ui.toolkit;

import java.io.File;

public class FontDirectoryDetector {
	
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

}