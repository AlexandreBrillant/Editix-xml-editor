package com.japisoft.framework.collection;

import java.util.Enumeration;
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
public class FastVector implements Cloneable {

    /**
     * The array buffer into which the components of the vector are 
     * stored. The capacity of the vector is the length of this array buffer.
     *
     * @since   JDK1.0
     */
    protected Object elementData[];

    /**
     * The number of valid components in the vector. 
     *
     * @since   JDK1.0
     */
    protected int elementCount;

    /**
     * The amount by which the capacity of the vector is automatically 
     * incremented when its size becomes greater than its capacity. If 
     * the capacity increment is <code>0</code>, the capacity of the 
     * vector is doubled each time it needs to grow. 
     *
     * @since   JDK1.0
     */
    protected int capacityIncrement;

    /** use serialVersionUID from JDK 1.0.2 for interoperability */
    private static final long serialVersionUID = -2767605614048989439L;

    /**
     * Constructs an empty vector with the specified initial capacity and
     * capacity increment. 
     *
     * @param   initialCapacity     the initial capacity of the vector.
     * @param   capacityIncrement   the amount by which the capacity is
     *                              increased when the vector overflows.
     * @since   JDK1.0
     */
    public FastVector(int initialCapacity, int capacityIncrement) {
	super();
	this.elementData = new Object[initialCapacity];
	this.capacityIncrement = capacityIncrement;
    }

    /**
     * Constructs an empty vector with the specified initial capacity.
     * @param   initialCapacity   the initial capacity of the vector.
     * @since   JDK1.0
     */
    public FastVector(int initialCapacity) {
	this(initialCapacity, 0);
    }

    /**
     * Constructs an empty vector. 
     *
     * @since   JDK1.0
     */
    public FastVector() {
	this(50);
    }

    public final void removeAllElements() {
		elementCount = 0;
	}    

    public final Object lastElement() {
    	return elementData[ elementCount - 1 ];
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
	    throw new ArrayIndexOutOfBoundsException(index + " >= " + 
						     elementCount);
	}
	else if (index < 0) {
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
	    throw new ArrayIndexOutOfBoundsException(index
						     + " > " + elementCount);
	}
	if (newcount > elementData.length) {
	    ensureCapacityHelper(newcount);
	}
	System.arraycopy(elementData, index, elementData, index + 1, elementCount - index);
	elementData[index] = obj;
	elementCount++;
    }

    public final Enumeration elements() {
	return new VectorEnumerator(this);
    }

    /**
     * Trims the capacity of this vector to be the vector's current 
     * size. An application can use this operation to minimize the 
     * storage of a vector. 
     *
     * @since   JDK1.0
     */
    public final void trimToSize() {
	int oldCapacity = elementData.length;
	if (elementCount < oldCapacity) {
	    Object oldData[] = elementData;
	    elementData = new Object[elementCount];
	    System.arraycopy(oldData, 0, elementData, 0, elementCount);
	}
    }

    /**
     * Increases the capacity of this vector, if necessary, to ensure 
     * that it can hold at least the number of components specified by 
     * the minimum capacity argument. 
     *
     * @param   minCapacity   the desired minimum capacity.
     * @since   JDK1.0
     */
    public final void ensureCapacity(int minCapacity) {
	if (minCapacity > elementData.length) {
	    ensureCapacityHelper(minCapacity);
	}
    }

    /**
     * This implements the unsynchronized semantics of ensureCapacity.
     * methods in this class can internally call this 
     * method for ensuring capacity without incurring the cost of an 
     * extra synchronization.
     *
     * @see java.util.Vector#ensureCapacity(int)
     */ 
    private void ensureCapacityHelper(int minCapacity) {
	int oldCapacity = elementData.length;
	Object oldData[] = elementData;
	int newCapacity = (capacityIncrement > 0) ?
	    (oldCapacity + capacityIncrement) : (oldCapacity * 2);
	if (newCapacity < minCapacity) {
	    newCapacity = minCapacity;
	}
	elementData = new Object[newCapacity];
	System.arraycopy(oldData, 0, elementData, 0, elementCount);
    }
    
    /**
     * Sets the size of this vector. If the new size is greater than the 
     * current size, new <code>null</code> items are added to the end of 
     * the vector. If the new size is less than the current size, all 
     * components at index <code>newSize</code> and greater are discarded.
     *
     * @param   newSize   the new size of this vector.
     * @since   JDK1.0
     */
    public final void setSize(int newSize) {
	if ((newSize > elementCount) && (newSize > elementData.length)) {
	    ensureCapacityHelper(newSize);
	} else {
	    for (int i = newSize ; i < elementCount ; i++) {
		elementData[i] = null;
	    }
	}
	elementCount = newSize;
    }

    /**
     * Returns the number of components in this vector.
     *
     * @return  the number of components in this vector.
     * @since   JDK1.0
     */
    public final int size() {
	return elementCount;
    }

    /**
     * Searches for the first occurence of the given argument, testing 
     * for equality using the <code>equals</code> method. 
     *
     * @param   elem   an object.
     * @return  the index of the first occurrence of the argument in this
     *          vector; returns <code>-1</code> if the object is not found.
     * @see     java.lang.Object#equals(java.lang.Object)
     * @since   JDK1.0
     */
    public final int indexOf(Object elem) {
	return indexOf(elem, 0);
    }

    /**
     * Searches for the first occurence of the given argument, beginning 
     * the search at <code>index</code>, and testing for equality using 
     * the <code>equals</code> method. 
     *
     * @param   elem    an object.
     * @param   index   the index to start searching from.
     * @return  the index of the first occurrence of the object argument in
     *          this vector at position <code>index</code> or later in the
     *          vector; returns <code>-1</code> if the object is not found.
     * @see     java.lang.Object#equals(java.lang.Object)
     * @since   JDK1.0
     */
    public final int indexOf(Object elem, int index) {
	for (int i = index ; i < elementCount ; i++) {
	    if (elem.equals(elementData[i])) {
		return i;
	    }
	}
	return -1;
    }

    /**
     * Returns the component at the specified index.
     *
     * @param      index   an index into this vector.
     * @return     the component at the specified index.
     * @exception  ArrayIndexOutOfBoundsException  if an invalid index was
     *               given.
     * @since      JDK1.0
     */
    public final Object get(int index) {
    	return elementData[index];
    }

    public final boolean contains( Object obj ) {
	return indexOf( obj ) >= 0;
    }

    /**
     * Adds the specified component to the end of this vector, 
     * increasing its size by one. The capacity of this vector is 
     * increased if its size becomes greater than its capacity. 
     *
     * @param   obj   the component to be added.
     * @since   JDK1.0
     */
    public final void add(Object obj) {
	int newcount = elementCount + 1;
	if (newcount > elementData.length) {
	    ensureCapacityHelper(newcount);
	}
	elementData[elementCount++] = obj;
    }

    public Object clone() {
	try { 
	    FastVector v = (FastVector)super.clone();
	    v.elementData = new Object[elementCount];
	    System.arraycopy(elementData, 0, v.elementData, 0, elementCount);
	    return v;
	} catch (CloneNotSupportedException e) { 
	    // this shouldn't happen, since we are Cloneable
	    throw new InternalError();
	}
    }

    public void dump() {
    	for ( int i = 0; i < size(); i++ ) {
    		System.out.println( get( i ));
    	}
    }

}

final class VectorEnumerator implements Enumeration {
    FastVector vector;
    int count;

    VectorEnumerator(FastVector v) {
	vector = v;
	count = 0;
    }

    public boolean hasMoreElements() {
	return count < vector.elementCount;
    }

    public Object nextElement() {
	synchronized (vector) {
	    if (count < vector.elementCount) {
		return vector.elementData[count++];
	    }
	}
	throw new RuntimeException("FastVectorEnumerator");
    }
    
}

