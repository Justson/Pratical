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

public class JThread extends Thread {

    private Data mData;

    public JThread(Data data) {
        this.mData = data;
    }

    @Override
    public void run() {

        String read = this.mData.read();

        System.out.println("result:" + Thread.currentThread().getName() + "    ::  " + read);

    }
}
