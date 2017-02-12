package com.concurrent.just.concurrent_pratical;

/**
 * <b>@项目名：</b> Helmet<br>
 * <b>@包名：</b>com.ucmap.helmet<br>
 * <b>@创建者：</b> cxz --  just<br>
 * <b>@创建时间：</b> &{DATE}<br>
 * <b>@公司：</b> 宝诺科技<br>
 * <b>@邮箱：</b> cenxiaozhong.qqcom@qq.com<br>
 * <b>@描述</b><br>
 */

public class Data {

    volatile char[] buffer;

    public Data(int size) {
        buffer = new char[size];
    }

    public String read() {

        synchronized (this) {
            StringBuffer sb = new StringBuffer();
            for (char c : buffer) {
                sb.append(c);
                System.out.println("result:" + Thread.currentThread().getName() + "    ::  " + c);
                sleep(20);
            }
            return sb.toString();
        }

    }

    public synchronized void write(char c) {
        System.out.println("write  -- :" + "Thread:" + Thread.currentThread() + "   start");
        for (int i = 0; i < buffer.length; i++) {

            buffer[i] = c;
            sleep(20);
        }

        System.out.println("write  -- :" + "Thread:" + Thread.currentThread() + "   end");

    }

    private void sleep(long time) {


        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
