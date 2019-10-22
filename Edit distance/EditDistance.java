/**
 * 612 LBE06 Edit Distance
 * Khavya Seshadri
 */
import java.lang.*;
import java.io.*;

public class EditDistance {
   public int editDistance(String x, String y) {
      //To be completed
   	  if(x.length()==0)
   	  	return y.length();
   	  else if(y.length()==0)
   	  	return x.length();
   	  else{
   	  	   int insertDistance = editDistance(x, y.substring(0,y.length()-1))+1;
   	  	   int deleteDistance = editDistance(x.substring(0,x.length()-1),y)+1;
   	  	   int substituteDistance = editDistance(x.substring(0,x.length()-1), y.substring(0,y.length()-1))+ ((x.charAt(x.length()-1)==y.charAt(y.length()-1))?0:1);

   	  	   return Math.min(insertDistance,Math.min(deleteDistance,substituteDistance));
   	  }
   }
   public static void main(String[] args) {
      EditDistance ed = new EditDistance();
      System.out.println(ed.editDistance("zeil","trial"));
      System.out.println(ed.editDistance("cat","act"));
   }
}
