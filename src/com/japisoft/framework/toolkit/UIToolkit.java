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