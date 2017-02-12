package test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
