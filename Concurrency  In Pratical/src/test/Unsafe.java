package test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Unsafe {
	
   
//	public int getA(){
//		return a++;
//	}
	
	private AtomicInteger mAtomicInteger=new AtomicInteger();
	private volatile int a=0;
	public Unsafe(){
		mAtomicInteger.set(a);
	}
	public int getA(){
		for(;;){
			
			if(mAtomicInteger.compareAndSet(mAtomicInteger.get(), mAtomicInteger.get()+1)){
				return mAtomicInteger.get();
			}
		}
	}
	
	
	public synchronized void doSomething(){
		//.....logic
	}
	
	
	public static void main(String[]arg){
		
		Unsafe mUnsafe=new Unsafe();
		ExecutorService mExecutorService = Executors.newCachedThreadPool();
		 for(int i=0;i<100;i++){
			 
			 mExecutorService.submit(()->{
				 
				 System.out.println("Thread:"+Thread.currentThread().getName()+"   value:"+mUnsafe.getA());
			 });
		 }
		 mExecutorService.shutdown();
		
	}
	
}
