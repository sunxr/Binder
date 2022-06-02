// IOnNewBookArrivedListener.aidl
package com.sun.alone.aldltest;

// Declare any non-default types here with import statements

import com.sun.alone.aldltest.Book;

interface IOnNewBookArrivedListener {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    void onNewBookArrived(in Book newBook);
}