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

package com.japisoft.editix.ui;



/**
 * Constants used by TableLayout.
 *
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 */

public interface TableLayoutConstants
{



/** Indicates that the component is left justified in its cell */
public static final int LEFT = 0;

/** Indicates that the component is top justified in its cell */
public static final int TOP = 0;

/** Indicates that the component is centered in its cell */
public static final int CENTER = 1;

/** Indicates that the component is full justified in its cell */
public static final int FULL = 2;

/** Indicates that the component is bottom justified in its cell */
public static final int BOTTOM = 3;

/** Indicates that the component is right justified in its cell */
public static final int RIGHT = 3;

/** Indicates that the row/column should fill the available space */
public static final double FILL = -1.0;

/** Indicates that the row/column should be allocated just enough space to
    accomidate the preferred size of all components contained completely within
    this row/column. */
public static final double PREFERRED = -2.0;

/** Indicates that the row/column should be allocated just enough space to
    accomidate the minimum size of all components contained completely within
    this row/column. */
public static final double MINIMUM = -3.0;

/** Minimum value for an alignment */
public static final int MIN_ALIGN = 0;

/** Maximum value for an alignment */
public static final int MAX_ALIGN = 3;



}
