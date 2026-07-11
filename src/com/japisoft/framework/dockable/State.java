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
