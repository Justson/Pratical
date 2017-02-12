package com.concurrent.just.concurrent_pratical_point_thread;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <b>@项目名：</b> Helmet<br>
 * <b>@包名：</b>com.ucmap.helmet<br>
 * <b>@创建者：</b> cxz --  just<br>
 * <b>@创建时间：</b> &{DATE}<br>
 * <b>@公司：</b> 宝诺科技<br>
 * <b>@邮箱：</b> cenxiaozhong.qqcom@qq.com<br>
 * <b>@描述</b><br>
 */

public class Client {

    LockManager mLockManager = new LockManager();

    public void doIt() {

        mLockManager.lock();
        try {

            System.out.println(" queryTicketNumber  current size:" + "   thread:" + Thread.currentThread().getName());
            sleep(20000);
        } finally {
            mLockManager.unlock();
        }

    }

    public static void main(String[] args) {

        ExecutorService mExecutorService = Executors.newCachedThreadPool();
        Client mClient = new Client();
        CountDownLatch mCountDownLatch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {


            mExecutorService.execute(() -> {


                mClient.doIt();
                mCountDownLatch.countDown();
            });
        }

        try {
            mCountDownLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("   System  end");
        mExecutorService.shutdownNow();

    }


    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
