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

package com.japisoft.framework.ui;

import java.awt.Component;
import java.awt.Insets;
import java.awt.Point;

import javax.swing.JViewport;
import javax.swing.SwingUtilities;

public class AutoScroll { 
    Component comp; 
    Insets insets; 
    Insets scrollUnits; 
 
    public AutoScroll(Component comp, Insets insets){ 
        this(comp, insets, insets); 
    } 
 
    public AutoScroll(Component comp, Insets insets, Insets scrollUnits){ 
        this.comp = comp; 
        this.insets = insets; 
        this.scrollUnits = scrollUnits; 
    } 
 
    public void autoscroll(Point cursorLoc){ 
        JViewport viewport = getViewport(); 
        if(viewport==null) 
            return; 
        Point viewPos = viewport.getViewPosition(); 
        int viewHeight = viewport.getExtentSize().height; 
        int viewWidth = viewport.getExtentSize().width; 

        if((cursorLoc.y-viewPos.y)<insets.top){ // scroll up 
            viewport.setViewPosition( 
                    new Point(viewPos.x, 
                            Math.max(viewPos.y-scrollUnits.top, 0))); 
        } else if((viewPos.y+viewHeight-cursorLoc.y)<insets.bottom){ // scroll down 
            viewport.setViewPosition( 
                    new Point(viewPos.x, 
                            Math.min(viewPos.y+scrollUnits.bottom, 
                                    comp.getHeight()-viewHeight))); 
        } else if((cursorLoc.x-viewPos.x)<insets.left){ // scroll left 
            viewport.setViewPosition( 
                    new Point(Math.max(viewPos.x-scrollUnits.left, 0), 
                            viewPos.y)); 
        } else if((viewPos.x+viewWidth-cursorLoc.x)<insets.right){ // scroll right 
            viewport.setViewPosition( 
                    new Point(Math.min(viewPos.x+scrollUnits.right, comp.getWidth()-viewWidth), 
                            viewPos.y)); 
        } 
    } 
 
    JViewport getViewport(){ 
        return (JViewport)SwingUtilities.getAncestorOfClass(JViewport.class, comp); 
    } 
} 	
