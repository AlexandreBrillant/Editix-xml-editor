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

package com.japisoft.editix.document;

import java.awt.Dimension;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import com.japisoft.editix.ui.llm.RunnablePrompterPanel;
import com.japisoft.editix.ui.llm.SimplePrompterPanel;
import com.japisoft.editix.ui.llm.config.LLMRunner;
import com.japisoft.editix.ui.llm.config.LLMRunnerListener;
import com.japisoft.editix.ui.windows.EditixFrame;
import com.japisoft.editix.wizard.document.DocumentWizard;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.llm.LLM;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.XMLDocumentInfo;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class TemplateInfo {

	public String label;
	public String location;
	public String type;
	public boolean system;
	public Icon icon;
	public String encoding;
	public String content;
	public String defDTDLocation;
	public String defDTDRoot;
	public String help;
	public String wizard;
	
	public DocumentWizard wizardInstance = null;

	public boolean hasWizard() { return wizard != null; }
	
	public String startWizard() {
		if ( wizard == null )
			return null;
		if ( wizard != null ) {
			if ( wizardInstance == null ) {
				try {
					wizardInstance = ( DocumentWizard )( Class.forName( wizard ) ).newInstance();
				} catch( Exception exc ) {
					exc.printStackTrace();
					wizard = null;
					return null;
				}
			}
			return wizardInstance.start();
		}
		return null;
	}

	public boolean askLLM( XMLDocumentInfo info, LLMRunnerListener listener )  {
		if ( location.contains( "_llm_" ) ) {
			
			String prompt = info.getTemplate();
			String defaultLLM = null;
			
			String[] tmp = prompt.split( "\n" );
			if ( tmp.length > 1 ) {
				// [MODEL "DEFAULT"]
				String regex = "\\[(\\w+)\\s+\"([^\"]+)\"\\]";
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher( tmp[ 0 ] );
				if ( matcher.find() ) {
					prompt = tmp[ 1 ];
					String command = matcher.group( 1 );
					String value = matcher.group( 2 );
					if ( "MODEL".equalsIgnoreCase( command ) )
						defaultLLM = value;
				}

			}
			
			RunnablePrompterPanel pp = new RunnablePrompterPanel( prompt, listener );
			if ( defaultLLM != null )
				pp.selectLLM( defaultLLM );
			
			if ( DialogManager.showDialog( 
					EditixFrame.THIS,
					"Ask LLM",
					"Ask to your LLM",
					"Create a new document with this request.\nPress ENTER for running the request",
					null,
					pp,
					new Dimension( 400, 300 )) == DialogManager.OK_ID ) {
				if ( !pp.hasRequest() ) {
					pp.runPrompt();
				}
			}
			return true;
		}
		return false;
	}

	public File getWizardSource() { return wizardInstance.getSource(); }
	
	public FPNode toXML() {
		FPNode node2 = new FPNode( FPNode.TAG_NODE, "template" );
		String labelTmp = label.replaceAll( "<", "&lt;" ).replaceAll( ">", "&gt;" );
		node2.setAttribute( "label", labelTmp );
		node2.setAttribute( "type", type );
		if ( help != null )
			node2.setAttribute( "help", help );
		if ( location != null ) {
			node2.setAttribute( "location", location );
		}
		node2.setAttribute( "system", ( system ) ? "true" : "false" );
		if ( wizard != null ) {
			node2.setAttribute( "wizard", wizard );
		}
		return node2;
	}
	
}
