package com.japisoft.xpath;
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
public class FastVector {

	Object elementData[];
	int elementCount;
	int capacityIncrement;

	public FastVector(int initialCapacity, int capacityIncrement) {
		super();
		this.elementData = new Object[initialCapacity];
		this.capacityIncrement = capacityIncrement;
	}

	public FastVector(int initialCapacity) {
		this(initialCapacity, 0);
	}

	public FastVector() {
		this(5);
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
		if (index >= elementCount) {
			throw new ArrayIndexOutOfBoundsException(
				index + " >= " + elementCount);
		} else if (index < 0) {
			throw new ArrayIndexOutOfBoundsException(index);
		}
		int j = elementCount - index - 1;
		if (j > 0) {
			System.arraycopy(elementData, index + 1, elementData, index, j);
		}
		elementCount--;
		elementData[elementCount] = null;
	}

	public final void insertElementAt(Object obj, int index) {
		int newcount = elementCount + 1;
		if (index >= newcount) {
			throw new ArrayIndexOutOfBoundsException(
				index + " > " + elementCount);
		}
		if (newcount > elementData.length) {
			ensureCapacityHelper(newcount);
		}
		System.arraycopy(
			elementData,
			index,
			elementData,
			index + 1,
			elementCount - index);
		elementData[index] = obj;
		elementCount++;
	}

	public final void ensureCapacity(int minCapacity) {
		if (minCapacity > elementData.length) {
			ensureCapacityHelper(minCapacity);
		}
	}

	private void ensureCapacityHelper(int minCapacity) {
		int oldCapacity = elementData.length;
		Object oldData[] = elementData;
		int newCapacity =
			(capacityIncrement > 0)
				? (oldCapacity + capacityIncrement)
				: (oldCapacity * 2);
		if (newCapacity < minCapacity) {
			newCapacity = minCapacity;
		}
		elementData = new Object[newCapacity];
		System.arraycopy(oldData, 0, elementData, 0, elementCount);
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

	public final Object elementAt(int index) {
		return elementData[index];
	}

	public final boolean contains(Object obj) {
		return indexOf(obj) >= 0;
	}

	public final void addElement(Object obj) {
		int newcount = elementCount + 1;
		if (newcount > elementData.length) {
			ensureCapacityHelper(newcount);
		}
		elementData[elementCount++] = obj;
	}
}
