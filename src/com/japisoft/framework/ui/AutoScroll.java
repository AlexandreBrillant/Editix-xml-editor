package com.japisoft.framework.ui;

import java.awt.Component;
import java.awt.Insets;
import java.awt.Point;

import javax.swing.JViewport;
import javax.swing.SwingUtilities;

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
