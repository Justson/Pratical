package com.concurrent.just.concurrent_pratical_point_thread;



import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * <b>@项目名：</b> Helmet<br>
 * <b>@包名：</b>com.ucmap.helmet<br>
 * <b>@创建者：</b> cxz --  just<br>
 * <b>@创建时间：</b> &{DATE}<br>
 * <b>@公司：</b> 宝诺科技<br>
 * <b>@邮箱：</b> cenxiaozhong.qqcom@qq.com<br>
 * <b>@描述</b><br>
 */

public class LockManager implements Lock, Serializable {


    private final Syner mSyner;

    public LockManager() {
        mSyner = new Syner();
    }

    @Override
    public void lock() {
        mSyner.acquireShared(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        mSyner.acquireInterruptibly(1);
    }

    @Override
    public boolean tryLock() {
        return mSyner.tryAcquireShared(1) > 0;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return mSyner.tryAcquireSharedNanos(1, unit.toNanos(time));
    }

    @Override
    public void unlock() {
        mSyner.releaseShared(1);
    }

    @Override
    public Condition newCondition() {
        return mSyner.newCondition();
    }


    private static class Syner extends AbstractQueuedSynchronizer {


        @Override
        protected boolean tryReleaseShared(int arg) {

//            System.out.println("释放当前锁:" + arg + "  currentState:" + getState());
            while (true) {
                if (compareAndSetState(getState(), getState() - arg)) {
//                    System.out.println("成功释放-----:"+getState());
                    return true;
                }
            }

        }

        @Override
        protected int tryAcquireShared(int arg) {
            System.out.println("Thread:" + Thread.currentThread().getName() + "  arg: " + arg + "   state:" + getState());
            while (getState() <= 2) {

                int number = arg + getState();
                if (compareAndSetState(getState(), number)) {
                    System.out.println("已经获取到锁:" + Thread.currentThread()+"    :"+getState());
                    return number;
                } else {
                    System.out.println("竞争失败");
                }

            }
            return -1;


        }

        public Condition newCondition() {
            return newCondition();
        }
    }
}
