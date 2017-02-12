package com.concurrent.just.concurrent_pratical_ticket;

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

public class TicketClients {


    public static void main(String[] args) {


        ExecutorService mExecutorService = Executors.newCachedThreadPool();
        TicketServer mTicketServer = TicketServer.getInstance();

        long startTime = System.currentTimeMillis();

        CountDownLatch mCountDownLatch = new CountDownLatch(600);

        System.out.println("startTime:" + startTime);
        for (int i = 0; i < 100; i++) {
            mExecutorService.execute(() -> {
                while (true) {
                    sleep(50);
                    TicketBean mTicketBean = mTicketServer.buyTicket(Thread.currentThread().getName(), 800);
                    if (mTicketBean != null) {
                        continue;
                    } else {
                        break;
                    }
                }
                try {
                    mCountDownLatch.countDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            });
        }


        for (int i = 0; i < 500; i++) {


            mExecutorService.execute(() -> {

                sleep(5);
                int member = mTicketServer.queryTicketNumber();
                try {
                    mCountDownLatch.countDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        }
        try {
            mCountDownLatch.await();
            mExecutorService.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("endTime:" + endTime + "      extra:" + (endTime - startTime));

    }


    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
