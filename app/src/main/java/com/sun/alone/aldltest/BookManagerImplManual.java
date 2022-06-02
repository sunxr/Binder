package com.sun.alone.aldltest;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class BookManagerImplManual extends Binder implements IBookManagerManual {

  public BookManagerImplManual() {
    this.attachInterface(this, DESCRIPTOR);
  }

  @Override
  public List<Book> getBookList() throws RemoteException {
    return null;
  }

  @Override
  public void addBook(Book book) throws RemoteException {

  }

  @Override
  public IBinder asBinder() {
    return this;
  }

  @Override
  protected boolean onTransact(int code, @NonNull Parcel data, @Nullable Parcel reply, int flags) throws RemoteException {
    switch (code) {
      case INTERFACE_TRANSACTION: {
        reply.writeString(DESCRIPTOR);
        return true;
      }
      case TRANSACTION_getBookList: {
        data.enforceInterface(DESCRIPTOR);
        List<Book> result = this.getBookList();
        reply.writeNoException();
        reply.writeTypedList(result);
        return true;
      }
      case TRANSACTION_addBook: {
        data.enforceInterface(DESCRIPTOR);
        Book arg0;
        if (0 != data.readInt()) {
          arg0 = Book.CREATOR.createFromParcel(data);
        } else {
          arg0 = null;
        }
        this.addBook(arg0);
        reply.writeNoException();
        return true;
      }
    }
    return super.onTransact(code, data, reply, flags);
  }

  public static IBookManagerManual asInterface(IBinder obj) {
    if (obj == null) return null;
    IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
    if ((iin != null) && (iin instanceof IBookManagerManual)) {
      return (IBookManagerManual) iin;
    }
    return new Proxy(obj);
  }

  private static class Proxy implements IBookManagerManual {

    private IBinder mRemote;

    Proxy(IBinder remote) {
      mRemote = remote;
    }

    @Override
    public List<Book> getBookList() throws RemoteException {
      Parcel data = Parcel.obtain();
      Parcel reply = Parcel.obtain();
      List<Book> result;
      try {
        data.writeInterfaceToken(DESCRIPTOR);
        mRemote.transact(TRANSACTION_getBookList, data, reply, 0);
        reply.readException();
        result = reply.createTypedArrayList(Book.CREATOR);
      } finally {
        reply.recycle();
        data.recycle();
      }
      return result;
    }

    @Override
    public void addBook(Book book) throws RemoteException {
      Parcel data = Parcel.obtain();
      Parcel reply = Parcel.obtain();
      List<Book> result;
      try {
        data.writeInterfaceToken(DESCRIPTOR);
        if (book != null) {
          data.writeInt(1);
          book.writeToParcel(data, 0);
        } else {
          data.writeInt(0);
        }
        mRemote.transact(TRANSACTION_addBook, data, reply, 0);
        reply.readException();
      } finally {
        reply.recycle();
        data.recycle();
      }
    }

    @Override
    public IBinder asBinder() {
      return mRemote;
    }

    public String getInterfaceDescriptor() {
      return DESCRIPTOR;
    }
  }
}
