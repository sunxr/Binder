package com.sun.alone.aldltest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import java.util.List;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = "BookManagerActivity";
  private static final int MESSAGE_NEW_BOOK_ARRIVED = 1;

  private IBookManager remoteBookManager;

  private Handler handler = new Handler() {
    @Override
    public void handleMessage(@NonNull Message msg) {
      switch (msg.what) {
        case MESSAGE_NEW_BOOK_ARRIVED:
          Log.d(TAG, "receive new book :" + msg.obj);
          break;
        default:
          super.handleMessage(msg);
      }
    }
  };

  private IOnNewBookArrivedListener onNewBookArrivedListener = new IOnNewBookArrivedListener.Stub() {
    @Override
    public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

    }

    @Override
    public void onNewBookArrived(Book newBook) throws RemoteException {
      handler.obtainMessage(MESSAGE_NEW_BOOK_ARRIVED, newBook)
          .sendToTarget();
    }
  };

  private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
    @Override
    public void binderDied() {
      if (remoteBookManager == null) return;
      remoteBookManager.asBinder().unlinkToDeath(mDeathRecipient, 0);
      remoteBookManager = null;
      // 重新绑定
      Intent intent = new Intent(getBaseContext(), BookManagerService.class);
      bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }
  };

  private ServiceConnection connection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
      IBookManager bookManager = IBookManager.Stub.asInterface(iBinder);
      try {
        remoteBookManager = bookManager;
        //设置死亡代理
        iBinder.linkToDeath(mDeathRecipient, 0);
        List<Book> list = bookManager.getBookList();
        Log.i(TAG, "query book list, list type:" + list.getClass()
        .getCanonicalName());
        Log.i(TAG, "query book list:" + list.toString());

        Book newBook = new Book(3, "Android开发艺术探索");
        bookManager.addBook(newBook);
        Log.i(TAG, "add book:" + newBook);
        List<Book> newList = bookManager.getBookList();
        Log.i(TAG, "query book list:" + newList.toString());
        bookManager.registerListener(onNewBookArrivedListener);
      } catch (RemoteException e) {
        e.printStackTrace();
      }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
      remoteBookManager = null;
      Log.e(TAG, "binder died.");
      Log.e(TAG, "binder died......");
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Intent intent = new Intent(this, BookManagerService.class);
    bindService(intent, connection, Context.BIND_AUTO_CREATE);
  }

  @Override
  protected void onDestroy() {
    if (remoteBookManager != null && remoteBookManager.asBinder().isBinderAlive()) {
      try {
        Log.i(TAG, "unregister listener:" + onNewBookArrivedListener);
        remoteBookManager.unregisterListener(onNewBookArrivedListener);
      } catch (RemoteException e) {
        e.printStackTrace();
      }
    }
    unbindService(connection);
    super.onDestroy();
  }
}