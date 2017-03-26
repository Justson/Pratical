## <center/>Java线程安全与程序性能

### 定义
		线程安全:当多个线程访问某个类的时候,不管运行时采用何种调度方式或者这些线程如何交替执行并且在
	这主调代码中不需要任何额外的同步和协同,这个类都能表现出正确的行为, 那么就称这个类为线程安全.
		
      
		并发:并发当有多个线程在操作时,如果系统只有一个CPU,则它根本不可能真正同时进行一个以上的线程，
	它只能把CPU运行时间划分成若干个时间段,再将时间 段分配给各个线程执行，在一个时间段的线程代码运行时
	，其它线程处于挂起状。
	
	
### 简单说明
线程安全可能是非常复杂的,在没有足够的同步下,多线程执行的顺序是不可预测的,如下在单线程执行时没有任何问题的, 但是多线程执行时可能会出现两个或者多个值一致.

```
public class Unsafe {
	
	int a=0;
	public int getA(){
		return a++;
	}
	
}
```
因为在多个线程并发中, 对a++进行操作需要三步:读-改-写,首先在主存中读取a的值进入线程工作内存中,然后对a进行++操作, 最后写回主存中.假设线程1,2,3读取a的值都为10,那么线程1,2,3得到a++的值都会是11,因为这个过程中程序都是并行执行, 并不是串行.
以上最简单的解决方式就是在int前面加上隐式同步锁synchronized,但是这种方式并不高效,每个线程需要执行a++操作, 都需要获取Unsafe实例的锁, 竞争锁失败的线程都会被JVM系统挂起,线程1切换至线程2,我们称之为上下文切换, 这种行为需要系统内核到底层把线程2唤醒, 开销太大.有没有更好方式解决这个问题,不需要劳烦到系统底层呢, 答案是有的,如下

```
private AtomicInteger mAtomicInteger=new AtomicInteger();
	private volatile int a=0;
	public Safe(){
		mAtomicInteger.set(a);
	}
	public int getA(){
		for(;;){
			
			if(mAtomicInteger.compareAndSet(mAtomicInteger.get(), mAtomicInteger.get()+1)){
				return mAtomicInteger.get();
			}
		}
	}
```
CAS(CompareAndSwap)是一种比较交换,通过循环来保证线程安全的乐观锁.竞争失败的线程并不会被系统挂起, 而是通过自旋(Self-Spin)不断竞争直到成功返回.

### 性能
	主要从可伸缩性,(吞吐率(处理能力)),(服务时间, 延迟时间 (运行速度)) 展开~
**多线程性能需要建立线程安全基础上, 离开了线程安全线程性能变得没有意义.
>Amdahl定律 
>S<=1/(1-a+a/n)
>
>其中，a为并行计算部分所占比例，n为并行处理结点个数。这样，当1-a=0时，(即没有串行，只有并行)最大加速比s=n；当a=0时（即只有串行，没有并行），最小加速比s=1；当n→∞时，极限加速比s→ 1/（1-a），这也就是加速比的上限。例如，若串行代码占整个代码的25%，则并行处理的总体性能不可能超过4。这一公式已被学术界所接受，并被称做“阿姆达尔定律”。

根据Amdahl定律可知,程序性能是与串行,并行执行息息相关的,但是糟糕的程序设计并行会带来性能开销,不会提升性能如下说明

```
// 并行程序
public class Person {
	private static void sleep(long time){
		try{
			Thread.sleep(time);
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	public synchronized void takeATurnRound(){
		//run 
		sleep(1000*60*10);
	}
	public static void main(String[]arg){
		Person mPerson=new Person();
		ExecutorService mExecutorService = Executors.newCachedThreadPool();
		 for(int i=0;i<3;i++){
			 
			 mExecutorService.submit(()->{
				 mPerson.takeATurnRound();
			 });
		 }
		 mExecutorService.shutdown();
	}
	
}

```
```
//串行程序
public class Person {
	private static void sleep(long time){
		try{
			Thread.sleep(time);
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	public  void takeATurnRound(){
		sleep(1000*60*10);
	}
	public static void main(String[]arg){
		Person mPerson=new Person();
		mPerson.takeATurnRound();
		mPerson.takeATurnRound();
		mPerson.takeATurnRound();
	}
	
}
```
上面程序简单演示了一个人跑了三圈,这种并行程序设计严重影响程序性能,可伸缩性极低,不如串行程序,当并发数增加的时候计算能力反而下降了.

#### 提高可伸缩性
上面例子可以知道可伸缩最大的威胁就是独占方式的资源锁,所以我们可以通过减少竞争来提升可伸缩性,通过如下方式来减少竞争

1. 缩小锁的范围

 ```
 //错误做法
 public class PasswordStore {
	private HashMap<String,String>mPasswordMap=new HashMap<>();
	public synchronized boolean storePassword(String name,String password){
		boolean tag=false;
		if(password==null||password.length()<6)
			return tag;
		String mPasswordKey=name;
		String passwordEncord=doCrypto(password);
		try{
			//储存密码
			mPasswordMap.put(mPasswordKey, passwordEncord);
			tag=true;
		}catch(Exception e){
			tag=false;
		}
		return tag;
	}
	//加密
	private String doCrypto(String password){
		return password;
	}
}
 ```
 
 ```
 //正确做法
 public class PasswordStore {
	private HashMap<String,String>mPasswordMap=new HashMap<>();
	public boolean storePassword(String name,String password){
		boolean tag=false;
		if(password==null||password.length()<6)
			return tag;
		String mPasswordKey=name;
		String passwordEncord=doCrypto(password);
		try{
			synchronized (this) {
				mPasswordMap.put(mPasswordKey, passwordEncord);
				return true;	
			}
			
		}catch(Exception e){
			return false;
		}
		return tag;
	}
	//加密
	private String doCrypto(String password){
		return password;
	}
}
 ```
 
 通过缩小storePassword方法锁的范围,极大的减少了持有锁执行指令数量,根据Amdahl定律,减少串行代码增加并行代码量可以提升可伸缩性.在实际中其实我们可以使用ConcurrentHashMap来代替HashMap达到线程安全,以及代替HashTable提升锁性能,因为ConcurrentHashMap使用了锁分段Segment等技术, 控制粒度都处理的很好.不得不佩服创造ConcurrentHashMap这位大神Doug Lea!
 
2. 减少锁的粒度(锁分解)
		
	```
		
		public class LockDecomposed {
			private List<String>books=new ArrayList<String>();
			private Set<String> mEmployee=new HashSet<>();
			public void storeBook(String book){
			/**
			 * 错误加锁方式
			 * synchronized (this) {
			 *	books.add(book);
			 *}
			 */
			synchronized (books) {
			books.add(book);
			}
		}
		public void addStaff(String name){
			/**
			 *错误加锁方式
			 * synchronized (this) {
			 *	mEmployee.add(name);
			 *}
			 */
			synchronized (mEmployee) {
				mEmployee.add(name);
			}
		}	
	}

		
	```
	

		

	以上只是简单例子,可能不符合单一职责原则.如果synchronize(this)锁住的对象为添加员工和储存
	书本	时候线程会发生竞争, 竞争时候的线程会被挂起, 然后等待, 等待结束被唤醒在加入系统的线
	程调度队列中,通过锁分解把没必要开销去除

3. 锁分段

		参考ConcurrentHashMap.
		通过Segment把竞争性缩小	

4. 使用共享锁代替独占锁

	每年春运让人又爱有恨啊,以下模拟下买票系统来说明共享锁和独占锁
	首先进行简单的说明 , 以下用了JDK提供的共享锁 ReentrantReadWriteLock,
	ReentrantReadWriteLock是JDK(concurrent包下的实现类,内部包含ReadLock和	WriteLock)ReadLock和WriteLock 有什么区别呢?WriteLock可以理解为独占锁,ReadLock才是共	享锁的体现, ReentrantReadWriteLock把他们封装在一块,执WriteLock.lock(),TickerServer	内部只有单线程,所有读取线程会被Park挂起,当前线程执行	ReadLock.lock()当前线程会往后看看还有没有跟自己一样以SHAED模式被Park(挂起)起来的线程(存放	AQS里面双向链表),没有往下执行查询逻辑, 有的话会唤醒它(被唤醒的线程做同样的逻辑唤醒它下面的线	程)并行查询票.
	
	其实啊ReentrantReadWriteLock内部组合着一个很重要的变量Syn, Syn 是	ReentrantReadWriteLock的一个实现AQS的内部类,实际上WriteLock.lock和ReadLock.lock的逻	辑加锁都会交给Syn去完成.Syn会调用父类AQS去完成共享锁和独占锁.AQS可以说是Doug Lea一大杰作,	是	Java JDK的concurrent这个包下的核心类.



	独占锁实现的方式如下

	```
		//@author cenxiaozhong
		//独占锁方式实现的模拟服务器
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
        System.out.println(" queryTicketNumber  current size:" + 		mTicketBeans.size() + "   thread:" + Thread.currentThread().getName());
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
        System.out.println(" buy  current size:" + mTicketBeans.size() + "   		Thread:" + Thread.currentThread());
        return mTicketBean;
   		 }
		}

				
	```
	上面独占锁的方式实现卖票模拟器, synchronized的对象是TicketServer, 所以无论是执行买票逻辑	还是
	进行查询剩余的票数的时候内部都只有一个线程在执行,来达到线程安全, 但是这种做法是不提倡,因为这样	做线程的可伸缩性非常死, 提高并发量的时候性能并没有提升.

	```
/**
 * 
 * @author cenxiaozhong
 * 以共享锁模式卖票服务器,也就是说对查询票的数量允许
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
				Thread.sleep(10);// 睡一小会
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(" queryTicketNumber  current size:" + mTicketBeans.size() + "   thread:"
					+ Thread.currentThread().getName());
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
			System.out.println(" buy  current size:" + mTicketBeans.size() + "   Thread:" + Thread.currentThread());
			return mTicketBean;
		} finally {
			mWriteLock.unlock();//释放写锁
		}
	}

	}

	```

	可以看出查询和买票用了不同锁,共享锁大大提升程序性能. 能避免独占锁尽量避免独占锁



	以下是模拟客户端

	```
	//@author cenxiaozhong
public class Clients {
    public static void main(String[] args) {
    	//线程池
        ExecutorService mExecutorService = Executors.newCachedThreadPool();
        TicketServer mTicketServer = TicketServer.getInstance();
        //线程的计数器
        CountDownLatch mCountDownLatch = new CountDownLatch(1100);
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
        for (int i = 0; i < 1000; i++) {
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

	```

	####执行结果

		执行环境Mac os 10.12.2,四核 i7 16G运存
		
		endTime:1486803316222      extra:6170//使用了共享锁
		endTime:1486805339097      extra:17140//使用了独占锁
		
		可以看出来共享锁比独占锁效率高出了接近三倍, 其实不止, 会随之查询并发数增大而不同,很大一
		个原因是因为独占锁进行查询的时候会排斥所有线程.
		


5. 在高并发情况不使用对象池(享元模式).享元模式对资源重新利用,用空间换时间, 提高了性能.但是在并发情况反而带来开销.

	```
	 * Return a new Message instance from the global pool. Allows us to
     * avoid allocating new objects in many cases.
     */
    public static Message obtain() {
        synchronized (sPoolSync) {
            if (sPool != null) {
                Message m = sPool;
                sPool = m.next;
                m.next = null;
                m.flags = 0; // clear in-use flag
                sPoolSize--;
                return m;
            }
        }
        return new Message();
    }
	
	```
	以上可以看出从对象池获取对象是必须要加锁,不加锁会出现不同线程拿到相同对象Message,这是不允许的,加锁意味着在多线程访问中会出现阻塞, 阻塞,唤醒等开销足以new 出数百个Message对象了.所以在并发频率很高时候不使用对象池.
6. 等等.




 

		
		
