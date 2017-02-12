package test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LockDecomposed {
	private List<String>books=new ArrayList<String>();
	private Set<String> mEmployee=new HashSet<>();
	public void storeBook(String book){
		/**
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
		 * synchronized (this) {
		 *	mEmployee.add(name);
		 *}
		 */
		synchronized (mEmployee) {
			mEmployee.add(name);
		}
	}	
}
