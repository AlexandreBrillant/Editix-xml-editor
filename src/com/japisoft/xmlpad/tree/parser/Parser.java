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

package com.japisoft.xmlpad.tree.parser;

import java.io.Reader;

import com.japisoft.framework.xml.parser.ErrorParsingListener;
import com.japisoft.framework.xml.parser.ParseException;
import com.japisoft.framework.xml.parser.document.Document;
import com.japisoft.framework.xml.parser.node.NodeFactory;

public interface Parser {

	public static final int CONTINUE_PARSING_MODE = 0;

	public boolean isInterrupted();

	public void interruptParsing();

	public void setFlatView(boolean b);

	// for text, attribute update
	public void setLightweightMode( boolean b );
	public boolean isLightweightMode();	
	
	public void setBackgroundMode(boolean b);

	public void setParsingMode(int continueParsingMode);

	public void setErrorSignal(ErrorParsingListener parsingErrorListener);

	public void setNodeFactory(NodeFactory factory);

	public Document parse(Reader reader, Object context) throws ParseException;

	public boolean hasError();

}

