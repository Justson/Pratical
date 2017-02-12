package com.concurrent.just.concurrent_pratical_ticket;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * <b>@项目名：</b> Helmet<br>
 * <b>@包名：</b>com.ucmap.helmet<br>
 * <b>@创建者：</b> cxz -- just<br>
 * <b>@创建时间：</b> &{DATE}<br>
 * <b>@公司：</b> 宝诺科技<br>
 * <b>@邮箱：</b> cenxiaozhong.qqcom@qq.com<br>
 * <b>@描述</b><br>
 */
/**
 * 
 * @author cenxiaozhong
 * 已共享锁模式卖票服务器,也就是说对查询票的数量允许
 * 多个线程进入查询, 但是对卖票必须进行锁定,只有一个线程
 * 可以进行对票操作
 */
public class TicketServer {
	private Random mRandom = null;
	//票容器
	private static final List<TicketBean> mTicketBeans = new ArrayList<>();
	private static TicketServer mTicketServer = null;
	// 原子类
	private static final AtomicReference<TicketServer> ATOMIC_REFERENCE = new AtomicReference<TicketServer>();
	//共享锁  JDK提供, author Doug Lea
	private ReentrantReadWriteLock mReentrantReadWriteLock = new ReentrantReadWriteLock();
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

	private int i=0;
	// 不加锁危险,
	// public List<TicketBean> queryTicket() {
	// return mTicketBeans;
	// }
	// 共享方式查询票数
	public int queryTicketNumber() {
		ReentrantReadWriteLock.ReadLock mReadLock = mReentrantReadWriteLock.readLock();// 获取读取lock
		
		mReadLock.lock();// lock , 读是共享, 也就是 读的时候 其他线程依然可进这段代码进行查询票数
		try {
			try {
				
				Thread.sleep(++i%1000);// 睡一小会
				System.out.println(" queryTicketNumber  current size:" + mTicketBeans.size() + "   thread:"
						+ Thread.currentThread().getName() +"  ReadLockHold: "+mReentrantReadWriteLock.getReadHoldCount()+"   ReadLock: "+mReentrantReadWriteLock.getReadLockCount());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return mTicketBeans.size();
		} finally {
			mReadLock.unlock();// 一定别忘了unLock 否则出现死锁 具体请查看AQS源码

		}

	}

	//单例模式
	public static TicketServer getInstance() {
		while (true) {
			mTicketServer = ATOMIC_REFERENCE.get();
			if (mTicketServer != null)
				return mTicketServer;
			mTicketServer = new TicketServer();
			if (ATOMIC_REFERENCE.compareAndSet(null, mTicketServer))
				return ATOMIC_REFERENCE.get();
		}

	}
	//买票
	public TicketBean buyTicket(String name, double price) {
		ReentrantReadWriteLock.WriteLock mWriteLock = mReentrantReadWriteLock.writeLock();
		int position = -1;
		System.out.println(" buy  current size:" + mTicketBeans.size() +"  ReadLockHold: "+mReentrantReadWriteLock.getReadHoldCount()+"   ReadLock: "+mReentrantReadWriteLock.getReadLockCount()+ "   Thread:" + Thread.currentThread());
		mWriteLock.lock();
		try {
			
			int size = mTicketBeans.size();//简单查询一下票的数量
			if (size <= 0) {
				return null;//没有票了
			} else {
				position = mRandom.nextInt(size);//随机一张票
			}
			TicketBean mTicketBean = mTicketBeans.get(position);
			if (price < mTicketBean.getTicketMoney()) {//简单的判断当前票的价格
				return null;
			}
			mTicketBeans.remove(position);//移除票
			mTicketBean.setTicketBelong(name);//设置票的所属者
			mTicketBean.setTicketKey(Thread.currentThread().getName());
			
			return mTicketBean;
		} finally {
			mWriteLock.unlock();//释放写锁
		}
	}

}
