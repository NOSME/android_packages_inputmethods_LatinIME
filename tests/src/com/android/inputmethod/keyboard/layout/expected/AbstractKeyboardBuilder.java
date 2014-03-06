/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.inputmethod.keyboard.layout.expected;

import java.util.Arrays;

/**
 * This class builds a keyboard that is a two dimensional array of elements <code>E</code>.
 *
 * A keyboard consists of array of rows, and a row consists of array of elements. Each row may have
 * different number of elements. A element of a keyboard can be specified by a row number and a
 * column number, both numbers starts from 1.
 *
 * @param <E> the type of a keyboard element. A keyboard element must be an immutable object.
 */
abstract class AbstractKeyboardBuilder<E> {
    // A building array of rows.
    private final E[][] mRows;

    // Returns an instance of default element.
    abstract E defaultElement();
    // Returns an <code>E</code> array instance of the <code>size</code>.
    abstract E[] newArray(final int size);
    // Returns an <code>E[]</code> array instance of the <code>size</code>.
    abstract E[][] newArrayOfArray(final int size);

    /**
     * Construct a builder filled with the default element.
     * @param dimensions the integer array of each row's size.
     */
    AbstractKeyboardBuilder(final int ... dimensions) {
        mRows = newArrayOfArray(dimensions.length);
        for (int rowIndex = 0; rowIndex < dimensions.length; rowIndex++) {
            mRows[rowIndex] = newArray(dimensions[rowIndex]);
            Arrays.fill(mRows[rowIndex], defaultElement());
        }
    }

    /**
     * Construct a builder from template keyboard. This builder has the same dimensions and
     * elements of <code>rows</rows>.
     * @param rows the template keyboard rows. The elements of the <code>rows</code> will be
     *        shared with this builder. Therefore a element must be an immutable object.
     */
    AbstractKeyboardBuilder(final E[][] rows) {
        mRows = newArrayOfArray(rows.length);
        for (int rowIndex = 0; rowIndex < rows.length; rowIndex++) {
            final E[] row = rows[rowIndex];
            mRows[rowIndex] = Arrays.copyOf(row, row.length);
        }
    }

    /**
     * Return current constructing keyboard.
     * @return the array of the array of the element being constructed.
     */
    E[][] build() {
        return mRows;
    }

    /**
     * Get the current contents of the specified row.
     * @param row the row number to get the contents.
     * @return the array of elements at row number <code>row</code>.
     * @throws {@link RuntimeException} if <code>row</code> is illegal.
     */
    E[] getRowAt(final int row) {
        final int rowIndex = row - 1;
        if (rowIndex < 0 || rowIndex >= mRows.length) {
            throw new RuntimeException("Illegal row number: " + row);
        }
        return mRows[rowIndex];
    }

    /**
     * Set an array of elements to the specified row.
     * @param row the row number to set <code>elements</code>.
     * @param elements the array of elements to set at row number <code>row</code>.
     * @throws {@link RuntimeException} if <code>row</code> is illegal.
     */
    void setRowAt(final int row, final E[] elements) {
        final int rowIndex = row - 1;
        if (rowIndex < 0 || rowIndex >= mRows.length) {
            throw new RuntimeException("Illegal row number: " + row);
        }
        mRows[rowIndex] = elements;
    }

    /**
     * Set or insert an element at specified position.
     * @param row the row number to set or insert the <code>element</code>.
     * @param column the column number to set or insert the <code>element</code>.
     * @param element the element to set or insert at <code>row,column</code>.
     * @param insert if true, the <code>element</code> is inserted at <code>row,column</code>.
     *        Otherwise the <code>element</code> replace the element at <code>row,column</code>.
     * @throws {@link RuntimeException} if <code>row</code> or <code>column</code> is illegal.
     */
    void setElementAt(final int row, final int column, final E element, final boolean insert) {
        final E[] elements = getRowAt(row);
        final int columnIndex = column - 1;
        if (insert) {
            if (columnIndex < 0 || columnIndex >= elements.length + 1) {
                throw new RuntimeException("Illegal column number: " + column);
            }
            final E[] newElements = Arrays.copyOf(elements, elements.length + 1);
            // Shift the remaining elements.
            System.arraycopy(newElements, columnIndex, newElements, columnIndex + 1,
                    elements.length - columnIndex);
            // Insert the element at <code>row,column</code>.
            newElements[columnIndex] = element;
            // Replace the current row with one.
            setRowAt(row, newElements);
            return;
        }
        if (columnIndex < 0 || columnIndex >= elements.length) {
            throw new RuntimeException("Illegal column number: " + column);
        }
        elements[columnIndex] = element;
    }
}