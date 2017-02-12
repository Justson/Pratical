package com.concurrent.just.concurrent_pratical_syn;

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

public class Clients {
    public static void main(String[] args) {
    	//线程池
        ExecutorService mExecutorService = Executors.newCachedThreadPool();
        TicketServer mTicketServer = TicketServer.getInstance();
        //线程的计数器
        CountDownLatch mCountDownLatch = new CountDownLatch(600);
        //记录一下当前时间
        long startTime = System.currentTimeMillis();
        System.out.println("startTime:" + startTime);
        //for循环启动100个线程买票
        for (int i = 0; i < 100; i++) {
            mExecutorService.execute(() -> {
                while (true) {
                    sleep(50);//睡一会提高并发量
                    TicketBean mTicketBean = mTicketServer.buyTicket(Thread.currentThread().getName(), 800);
                    if (mTicketBean != null) {
                        continue;
                    } else {
                        break;
                    }
                }
                try {//减一
                    mCountDownLatch.countDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        	//启动一千个线程查询还有多少票
        for (int i = 0; i < 500; i++) {
            mExecutorService.execute(() -> {
                sleep(5);//睡一小会
                int member = mTicketServer.queryTicketNumber();//查询票
                try {
                	//线程减一
                    mCountDownLatch.countDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        }
        try {
        	//主线程等待   知道1100个线程执行完毕后唤醒
            mCountDownLatch.await();
            //关闭线程池
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
