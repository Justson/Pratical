package com.concurrent.just.concurrent_pratical_syn;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * <b>@项目名：</b> Helmet<br>
 * <b>@包名：</b>com.ucmap.helmet<br>
 * <b>@创建者：</b> cxz --  just<br>
 * <b>@创建时间：</b> &{DATE}<br>
 * <b>@公司：</b> 宝诺科技<br>
 * <b>@邮箱：</b> cenxiaozhong.qqcom@qq.com<br>
 * <b>@描述</b><br>
 */

public class TicketServer {
    private static final List<TicketBean> mTicketBeans = new ArrayList<>();
    private static TicketServer mTicketServer = null;
    private Random mRandom = null;
    /**
     * 默认有一万张票
     */
    static {
        for (int i = 0; i < 10000; i++) {
            mTicketBeans.add(new TicketBean(i + 1));
        }
    }
    private TicketServer() {
        mRandom = new Random();
    }
    public List<TicketBean> queryTicket() {
        return mTicketBeans;
    }
    public synchronized int queryTicketNumber() {
        System.out.println(" queryTicketNumber  current size:" + mTicketBeans.size() + "   thread:" + Thread.currentThread().getName());
        try {
            Thread.sleep(10);//做耗时的logic
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mTicketBeans.size();
    }
   
    /**单例模式**/
    public static TicketServer getInstance() {
        if (mTicketServer == null) {
            synchronized (TicketServer.class) {
                if (mTicketServer == null) {
                    mTicketServer = new TicketServer();
                }
            }
        }
        return mTicketServer;
    }
    /**加锁模拟买票系统**/
    public synchronized TicketBean buyTicket(String name, double price) {

        int position = -1;
        int size = mTicketBeans.size();
        if (size <= 0) {//查询一下有没有票
            return null;
        } else {
            position = mRandom.nextInt(size);//随机一张票
        }
        TicketBean mTicketBean = mTicketBeans.get(position);
        if (price < mTicketBean.getTicketMoney()) {//判断要付的金额
            return null;
        }
        mTicketBeans.remove(position);//系统移除该票, 改票已经卖出来了
        mTicketBean.setTicketBelong(name);//设置一下票的所属者
        mTicketBean.setTicketKey(Thread.currentThread().getName());
        System.out.println(" buy  current size:" + mTicketBeans.size() + "   Thread:" + Thread.currentThread());
        return mTicketBean;
    }
}
