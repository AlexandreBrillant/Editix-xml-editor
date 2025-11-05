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

/*
 * Created on 26 f�vr. 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.japisoft.framework.dockable;

import java.io.Serializable;

/**
 * Interface for a JDock state. The user must call <code>getState</code> or
 * <code>setState</code> for using it from the <code>JDock</code> container.
 * For saving or reading it from a stream the <code>ObjectOutputStream</code> or
 * the <code>ObjectInputStream</code> must be used.
 * <p>
 * Here a sample for writing the current JDock state inside a file.
 * <pre>
 * JDock doc = new JDock();
 * ...
 * State currentState = doc.getState();
 * FileOutputStream fout = new FileOutputStream( "jdoc.state" );
 * ObjectOutputStream output = new ObjectOutputStream( fout );
 * try {
 * 	output.writeObject( currentState );
 * } finally {
 * 	output.close();
 * }
 * </pre>
 * </p>
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor) */
public interface State extends Serializable {

}

