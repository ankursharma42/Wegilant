package com.wegilant;

import java.util.LinkedList;

/*
 * Producer Panel
 */

public class ProducerPanel {

	private static LinkedList<String> list = new LinkedList<String>();
	private static int maxsize=10;
	   public static void enqueue(String item) {
	      list.addLast(item);
	   }
	   public static String dequeue() {
	      return list.poll();
	   }
	   public static boolean hasItems() {
	      return !list.isEmpty();
	   }
	   public static int size() {
	      return list.size();
	   }
	   public static boolean isFull(){
		   if(list.size()==maxsize){
			   return true;
		   }
		   return false;
	   }
	   public static boolean present(String item){
		   if(list.contains(item)){
			   return true;
		   }
		   return false;
	   }
	   public static StringBuilder print(){
		   StringBuilder mergeString=new StringBuilder();
		   for(int i=0;i<list.size();i++){
			  // System.out.println(list.get(i)+"\n");
			   mergeString.append(list.get(i)+"\n");
		   }
		   System.out.println(mergeString);
		   return mergeString;
	   }

}
