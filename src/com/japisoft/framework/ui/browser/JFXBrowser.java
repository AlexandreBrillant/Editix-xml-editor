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

package com.japisoft.framework.ui.browser;

import javax.swing.JComponent;
import javax.swing.JPanel;

/*
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
*/

public class JFXBrowser implements Browser { // extends JFXPanel implements Browser {
/*	
    private WebEngine engine;

    public JFXBrowser() {
      super();
      createScene();
    }

    private void createScene() {
        Platform.runLater(new Runnable() {
            public void run() {
                WebView view = new WebView();
                engine = view.getEngine();
                setScene(new Scene(view));
            }
        }
        );
    }    
	
	public JComponent getView() {
		return this;
	}

	public void setHTML( final String content, final String baseURI ) {
        Platform.runLater( new Runnable() {
            public void run() {
               if ( baseURI != null )
            	   engine.loadContent( content, baseURI );
               else
            	   engine.loadContent( content );
            }
        });       		
	}
*/
	
	@Override
	public JComponent getView() {
		return new JPanel();
	}
	@Override
	public void setHTML(String content, String baseURI) {
	}
	
}
