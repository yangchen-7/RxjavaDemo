package com.example.rxjavademo;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private TextView observerTextView;
    private Button mObserverBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        observerTextView = findViewById(R.id.observerTextView);
        mObserverBtn = findViewById(R.id.observerBtn);

        Observer observer = new Observer<String>() {//观察者
            @Override
            public void onSubscribe(@NonNull Disposable d) {//新的onStart，会在事件还未被发送之前调用，做一些准备工作，Disposable用于取消操作,isDisposed()判断是否取消

            }

            @Override
            public void onNext(String str) {
                Log.d(TAG,"next=="+str);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Completed!");
            }
        };

//        Observable observable = Observable.create(new ObservableOnSubscribe<String>() {//被观察者  同步写法subscribe（）在哪里被调用就在哪个线程执行
//            @Override
//            public void subscribe(ObservableEmitter<String> e) throws Exception {
//                e.onNext("hello");
//                e.onNext("Rxjava");
//                e.onComplete();
//            }
//        });

        Observable<String> observable = Observable
                .just("hello", "RxJava")
                .delay(2, TimeUnit.SECONDS)      // 模拟耗时
                .subscribeOn(Schedulers.io())   // 子线程执行
                .observeOn(AndroidSchedulers.mainThread()); // 回主线程

        mObserverBtn.setOnClickListener(v -> {
            observerTextView.setText("");
            observable.subscribe(observer);//被观察者订阅观察者
        });






        Subscriber subscriber = new Subscriber<String>() {
            @Override
            public void onSubscribe(Subscription s) {//新的onStart，会在事件还未被发送之前调用，做一些准备工作，Disposable用于取消操作

            }

            @Override
            public void onNext(String o) {

            }

            @Override
            public void onError(Throwable t) {
                Log.d(TAG,"Error");
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Completed!");
            }
        };
    }
}