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

public class WThread extends Thread {

    private String b;
    private Data mData;

    public WThread(String b, Data data) {
        this.b = b;
        this.mData = data;
    }

    @Override
    public void run() {

        int length = b.length();
        for (int i = 0; i < length; i++) {
            System.out.println("write:" +"Thread:"+ Thread.currentThread()+"   char:"+ b.charAt(i));
            mData.write(b.charAt(i));


        }
    }
}
