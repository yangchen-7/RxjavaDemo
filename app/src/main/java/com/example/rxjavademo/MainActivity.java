package com.example.rxjavademo;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import java.util.concurrent.TimeUnit;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private TextView observerTextView;
    private Button mObserverBtn,mStartBtn,mStopBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        observerTextView = findViewById(R.id.observerTextView);
        mObserverBtn = findViewById(R.id.observerBtn);
        mStartBtn = findViewById(R.id.StartBtn);
        mStopBtn = findViewById(R.id.StopBtn);

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

        Observable<String> observable = Observable//被观察者
                .just("hello", "RxJava")
                .startWithItem("我要开始喽")
                .delay(2, TimeUnit.SECONDS)      // 模拟耗时
                .subscribeOn(Schedulers.io())   // 子线程执行
                .observeOn(AndroidSchedulers.mainThread()); // 回主线程

        mObserverBtn.setOnClickListener(v -> {
            observerTextView.setText("");
            observable.subscribe(observer);//被观察者订阅观察者
        });

        // 保存当前 Subscription
        final Subscription[] currentSub = new Subscription[1];//让停止按钮可以拿到当前在工作的Subscription
        mStartBtn.setOnClickListener(v -> {
            Flowable.range(0,10)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Integer>() {//同步写法
                        Subscription sub;
                        //当订阅后，会首先调用这个方法，其实就相当于onStart()，
                        //传入的Subscription s参数可以用于请求数据或者取消订阅
                        @Override
                        public void onSubscribe(Subscription s) {
                            Log.w("TAG","onsubscribe start");
                            sub=s;
                            currentSub[0] = s;
                            sub.request(1);//告诉上游：我现在最多还能处理1个数据   cancel()取消申请数据，断流，之后在request（）无法申请到数据
                            Log.w("TAG","onsubscribe end");
                        }

                        @Override
                        public void onNext(Integer o) {
                            Log.w("TAG","onNext--->"+o+" thread=" + Thread.currentThread().getName());
                            currentSub[0].request(1);
                        }
                        @Override
                        public void onError(Throwable t) {
                            t.printStackTrace();
                        }
                        @Override
                        public void onComplete() {
                            Log.w("TAG","onComplete");
                            currentSub[0] = null;
                        }
                    });
        });

        mStopBtn.setOnClickListener(v -> {
            if (currentSub[0] != null) {
                currentSub[0].cancel();
                Log.w("TAG","cancel called");
                currentSub[0] = null;
            }
        });

    }
}