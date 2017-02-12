package test;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

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
				tag=true;	
			}
			
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
