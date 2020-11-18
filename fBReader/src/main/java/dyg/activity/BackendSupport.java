package dyg.activity;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import java.util.ArrayList;
import java.util.List;

public class BackendSupport extends HandlerThread {
    private Handler handler = null;
    private static volatile BackendSupport instance = null;
    private List<Runnable> runnables = new ArrayList<>();

    public static BackendSupport getInstance() {
        if (instance == null) {
            synchronized (BackendSupport.class) {
                instance = new BackendSupport("back_worker");
                instance.start();
            }
        }
        return instance;
    }

    public BackendSupport(String name) {
        super(name);
    }


    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        handler = new Handler(getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };
        if (runnables.size() != 0) {
            for (int i = 0; i < runnables.size(); i++) {
                handler.post(runnables.get(i));
            }
        }
    }

    public void sendMessage(Message message) {
        handler.sendMessage(message);
    }

    public void post(Runnable runnable) {
        if (handler == null) {
            runnables.add(runnable);
        } else {
            handler.post(runnable);
        }
    }
}
