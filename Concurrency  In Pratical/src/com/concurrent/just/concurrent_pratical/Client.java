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

public class Client {


    public static void main(String[] args) {


        Data mData = new Data(12);

        for (int i = 0; i < 30; i++) {
            new JThread(mData).start();
        }

        new WThread("123456789", mData).start();
        new WThread("ABCDEFGHIJ", mData).start();







    }

}
