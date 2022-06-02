package com.sun.alone.aldltest;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class BookManagerService extends Service {

  private static final String TAG = "BMS";

  private CopyOnWriteArrayList<Book> books = new CopyOnWriteArrayList<>();

  private RemoteCallbackList<IOnNewBookArrivedListener> listeners = new RemoteCallbackList<>();

  private AtomicBoolean isServiceDestroyed = new AtomicBoolean(false);

  private Binder binder = new IBookManager.Stub() {
    @Override
    public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

    }

    @Override
    public List<Book> getBookList() throws RemoteException {
      return books;
    }

    @Override
    public void addBook(Book book) throws RemoteException {
      books.add(book);
    }

    @Override
    public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {
      listeners.register(listener);
    }

    @Override
    public void unregisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
      listeners.unregister(listener);
    }
  };

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return binder;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    books.add(new Book(1, "Android"));
    books.add(new Book(2, "IOS"));
    new Thread(new ServiceWorker()).start();
  }

  @Override
  public void onDestroy() {
    isServiceDestroyed.set(true);
    super.onDestroy();
  }

  private void onNewBookArrived(Book book) throws RemoteException {
    books.add(book);
    final int N = listeners.beginBroadcast();
    for(int i = 0; i < N; ++i) {
      IOnNewBookArrivedListener listener = listeners.getBroadcastItem(i);
      if (listener != null) {
        try {
          listener.onNewBookArrived(book);
        } catch (RemoteException e) {
          e.printStackTrace();
        }
      }
    }
    listeners.finishBroadcast();
  }

  private class ServiceWorker implements Runnable {
    @Override
    public void run() {
      while (!isServiceDestroyed.get()) {
        try {
          Thread.sleep(5000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        int bookId = books.size() + 1;
        Book newBook = new Book(bookId, "new book#" + bookId);
        try {
          onNewBookArrived(newBook);
        } catch (RemoteException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
