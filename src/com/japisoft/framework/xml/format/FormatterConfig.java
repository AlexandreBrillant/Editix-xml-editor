package com.japisoft.framework.xml.format;

import com.japisoft.framework.preferences.Preferences;

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
public class FormatterConfig {

	private boolean replaceAPos;
	private boolean replaceAmp;
	private boolean replaceGt;
	private boolean replaceLt;
	private boolean replaceQuote;
	private int formatSpaceQty;
	private String formatSpaceChr = "\t";
	private boolean indentProcessingInstruction = true;
	private boolean autoCloseElement = true;
	private boolean indentElement = true;
	private boolean trimedText = false;
	
	public FormatterConfig() {
		replaceAPos = 
			Preferences.getPreference(
					"xmlconfig", "format-replaceAPos", false );
		replaceQuote = 
			Preferences.getPreference(
					"xmlconfig", "format-replaceQuote", true );
		replaceGt = 
			Preferences.getPreference(
					"xmlconfig", "format-replaceGt", true );
		replaceLt = 
			Preferences.getPreference(
					"xmlconfig", "format-replaceLt", true );
		replaceAmp = 
			Preferences.getPreference(
					"xmlconfig", "format-replaceAmp", true );

		formatSpaceQty = 
			Preferences.getPreference(
					"xmlconfig", "format-space", 1 );
		indentProcessingInstruction =
			Preferences.getPreference(
					"xmlconfig", "format-processingInstruction", true );
	}

	public void setAutoCloseElement(boolean autoCloseElement) {
		this.autoCloseElement = autoCloseElement;
	}

	public void setIndentElement(boolean indentElement) {
		this.indentElement = indentElement;
	}

	public void setTrimedText( boolean trim ) {
		this.trimedText = trim;
	}

	public boolean isTrimedText() { return trimedText; }
	public boolean isReplaceAPos() { return replaceAPos; }
	public boolean isReplaceAmp() { return replaceAmp; }
	public boolean isReplaceGt() { return replaceGt; }
	public boolean isReplaceLt() { return replaceLt; }
	public boolean isReplaceQuote() { return replaceQuote; }
	public int getFormatSpaceQty() { return formatSpaceQty; }
	public String getFormatSpaceChr() { return formatSpaceChr; }
	public boolean isIndentProcessingInstruction() { return indentProcessingInstruction; }
	public boolean isAutoCloseElement() { return autoCloseElement; }
	public boolean isIndent() { return indentElement; }
	public boolean isXMLSpace() { return Preferences.getPreference( "xmlconfig", "format-xml:space", true ); }
	
}
