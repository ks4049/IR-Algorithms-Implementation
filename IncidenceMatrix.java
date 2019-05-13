import java.io.*;
import java.util.*;
/**
 * 612 LBE01 IncidenceMatrix
 * Khavya Seshadri
 */
 
public class IncidenceMatrix {
   //attributes
   private String[] myDocs;               //Documents
   private ArrayList<String> termList;    //List of terms in the dictionary
   private ArrayList<int[]> docLists;
   
   /*
   ** Construction of Incidence Matrix
   */
   public IncidenceMatrixA1(String[] docs) {
     myDocs = docs;
     termList = new ArrayList<String>();
     docLists = new ArrayList<int[]>();
     
     for(int i=0;i<myDocs.length;i++) {
      String[] words = myDocs[i].split(" ");
      for(String word:words) {
         if(!termList.contains(word)) {
            termList.add(word);
            int[] docList = new int[myDocs.length];
            docList[i] = 1;
            docLists.add(docList);
          }
         else {
            int index =termList.indexOf(word);
            int[] docList = docLists.remove(index);
            docList[i] = 1;
            docLists.add(index, docList);
         
         }
      }
     }
   }
   
   public ArrayList<Integer> search(String query) {
      String[] words = query.split(" ");
      ArrayList<int[]> queryList = new ArrayList<int[]>();
      for(String word : words){
        if(termList.contains(word)){
          int index = termList.indexOf(word);
          int[] array =  new int[myDocs.length];
          array = docLists.get(index);
          queryList.add(array);
        }
      }
      System.out.println("the list to be processed based on query "+queryList);
      //process the queryList 
      int[] flag = new int[myDocs.length];
      ArrayList<Integer> resultDocs = new ArrayList<Integer>();
      for(int[] docArray : queryList){  
        for(int i=0; i<docArray.length; i++){
            if(docArray[i]==1){
              flag[i]++;
            }
        }
      }
      //traverse through flag array
      for(int i=0; i<myDocs.length; i++){
        if(flag[i]== words.length){
          resultDocs.add(i+1);
        }
      }
      return resultDocs;
   }
   // toString() will be invoked internally whenever the object of the class is printed
   // Overriding the toString() method of Object class
   public String toString() {
      String outputString = new String();
      for(int i=0;i<termList.size();i++) {
         outputString += String.format("%-15s", termList.get(i));
         int[] docList = docLists.get(i);
         for(int j=0;j<docList.length;j++) {
            outputString += docList[j] + "\t";
         }
         outputString += "\n";
      }
      return outputString;
   }
   
   public static void main(String[] args) throws IOException{
      //input document collection: corpus
      String[] docs = {"text data warehousing over big data",
                       "dimensional data warehousing over big data",
                       "nlp before text mining",
                       "nlp before text classification"}; 
      IncidenceMatrixA1 im = new IncidenceMatrixA1(docs);
      System.out.println(im);   
      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
      String query = br.readLine();    
      //Query search
      ArrayList<Integer> result = im.search(query);
      System.out.println("Search Results found in the following Documents "+ result);
   }
}
