package com.japisoft.framework.toolkit;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.util.HashSet;
import java.util.Set;

public class UIToolkit {

	public static void inspectFonts(Component component) {
        inspectFonts(component, 0, new HashSet<>());
    }

    private static void inspectFonts(Component component, int depth, Set<Component> visited) {
        if (component == null || visited.contains(component)) {
            return;
        }

        visited.add(component);

        // Indentation pour la hiérarchie
        String indent = "  ".repeat(depth);

        // Récupérer la police
        Font font = component.getFont();
        String fontInfo = (font != null)
            ? String.format("%s (taille: %d, style: %s)",
                font.getFontName(),
                font.getSize(),
                getStyleName(font.getStyle()))
            : "null";

       

        // Afficher les informations
        System.out.printf("%s[%s] %s - Police: %s%n",
            indent,
            component.getClass().getSimpleName(),
            (component.getName() != null && !component.getName().isEmpty())
                ? "'" + component.getName() + "'" : "",
            fontInfo);

        // Parcourir les enfants
        if (component instanceof Container) {
            Container container = (Container) component;
            for (Component child : container.getComponents()) {
                inspectFonts(child, depth + 1, visited);
            }
        } 
                
    }

    private static String getStyleName(int style) {
        if (style == Font.PLAIN) return "PLAIN";
        if (style == Font.BOLD) return "BOLD";
        if (style == Font.ITALIC) return "ITALIC";
        if (style == (Font.BOLD | Font.ITALIC)) return "BOLD+ITALIC";
        return String.valueOf(style);
    }		
	
}
