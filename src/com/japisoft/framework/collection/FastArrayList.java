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

package com.japisoft.framework.collection;

public final class FastArrayList {

	protected Object elementData[];
	protected int elementCount;

	public FastArrayList(int initialCapacity) {
		super();
		this.elementData = new Object[initialCapacity];
	}

	public final void removeAllElements() {
		elementCount = 0;
	}

	public final Object lastElement() {
		return elementData[elementCount - 1];
	}

	public final boolean remove(Object obj) {
		int i = indexOf(obj);
		if (i >= 0) {
			removeElementAt(i);
			return true;
		}
		return false;
	}

	public final void removeElementAt(int index) {
		elementCount--;
		elementData[elementCount] = null;
	}

	public final void insertElementAt(Object obj, int index) {
		if ( index < elementData.length ) {
			elementData[index] = obj;
			elementCount++;
		}
	}

	public final int size() {
		return elementCount;
	}

	public final int indexOf(Object elem) {
		return indexOf(elem, 0);
	}

	public final int indexOf(Object elem, int index) {
		for (int i = index; i < elementCount; i++) {
			if (elem.equals(elementData[i])) {
				return i;
			}
		}
		return -1;
	}

	public final Object get(int index) {
		return elementData[index];
	}

	public final boolean contains(Object obj) {
		return indexOf(obj) >= 0;
	}

	public final void add(Object obj) {
		if ( elementCount < elementData.length )
			elementData[elementCount++] = obj;
	}
	
	@Override
	public String toString() {
		String tmp = "";
		for ( int i = 0; i < size(); i++ )
			tmp += get( i );
		return tmp;
	}

}
