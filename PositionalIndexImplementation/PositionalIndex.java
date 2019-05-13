import java.util.*;
import java.io.*;
/**
 * ISTE-612-2185 Lab #2
 * Khavya Seshadri
 * 03/04/19
 */
 
public class PositionalIndex {
   //attributes
   private String[] myDocs;
   private ArrayList<String> termList;
   private ArrayList<ArrayList<Doc>> docLists;
   
   //constructor forming the positional index
   public PositionalIndex(String[] docs) {
      myDocs = docs;
      termList = new ArrayList<String>();
      docLists = new ArrayList<ArrayList<Doc>>();
      for(int i=0; i<myDocs.length; i++){
         String[] words = myDocs[i].split(" ");
         ArrayList<Doc> docList;
         for(int j=0; j<words.length; j++){
            boolean isFound = false;
            if(!termList.contains(words[j])){
               termList.add(words[j]);
               Doc d1 = new Doc(i,j);
               docList = new ArrayList<Doc>();
               docList.add(d1);
               docLists.add(docList);               
            }else{
               int index = termList.indexOf(words[j]);
               docList = docLists.get(index);
               for(Doc d : docList){
                  if(d.docId == i){
                     d.insertPosition(j);
                     isFound = true;
                     break;
                  }
               }
               if(!isFound){
                  Doc d = new Doc(i, j);
                  docList.add(d);
               }
               docLists.set(index, docList);               
            }
         }
      }
   }
   
   /**
    * Performing Merge of two postings
    * @param list1 first postings
    * @param list2 second postings
    * @return merged result of two postings (Array List of Doc(docID, postionList))
    */   
   public ArrayList<Doc> intersect(ArrayList<Doc> list1, ArrayList<Doc> list2) {
       ArrayList<Doc> resultList = new ArrayList<Doc>();
      int idx1=0, idx2 =0;
      while(idx1 < list1.size() && idx2 < list2.size() ){
         Doc d = list1.get(idx1);
         Doc d1 = list2.get(idx2);
         if(d.docId == d1.docId){
            //get the position list            
            ArrayList<Integer> positionList1 = d.positionList;
            ArrayList<Integer> positionList2 = d1.positionList;
            ArrayList<Integer> matchPositions = new ArrayList<Integer>();
            boolean isFound=false;
            for(int i=0; i<positionList1.size(); i++){
              for(int j=0; j<positionList2.size(); j++){
                if(positionList2.get(j)== positionList1.get(i)+1){
                      matchPositions.add(positionList2.get(j));  
                      break;          
                }
              }                             
            }
            if(!matchPositions.isEmpty()){
              Doc finalDoc = new Doc(d1.docId,-1);
              finalDoc.positionList = matchPositions;
              resultList.add(finalDoc);
            }    
            idx1++;idx2++;
         }else if(d.docId < d1.docId){
            idx1 ++;
         }else{
            idx2++;
         }
      }
      return !resultList.isEmpty() ? resultList : null;
   }
   
   public String toString() {
      String outString = new String();
      for(int i=0;i<termList.size();i++) {
         outString += String.format("%-15s", termList.get(i));
         ArrayList<Doc> docList = docLists.get(i);
         for(int j=0;j<docList.size();j++) {
            outString += docList.get(j) + "\t";
         }
         outString += "\n";
      }
      return outString;
   }  

   /**
    * 
    * @param phrase a phrase query that consists of any number of terms in the sequential order
    * @return ids of documents that contain the phrase
    */
   public void phraseQuery(String phrase){
      String[] queries = phrase.split(" "); 
      ArrayList<Doc> mergedList = new ArrayList<Doc>();               
     mergedList = intersect(docLists.get(termList.indexOf(queries[0])), docLists.get(termList.indexOf(queries[1])));
     //System.out.println("Initial list "+mergedList);
     if(mergedList!=null && mergedList.size()>0){      
       for(int i=2; i<queries.length;i++){
          mergedList = intersect(mergedList, docLists.get(termList.indexOf(queries[i])));      
          //System.out.println("Further list "+mergedList);      
       }
       System.out.print("\nThe phrase \""+phrase+"\" is present in the following documents ");
       System.out.print("\n"+mergedList);
       for(Doc d1 : mergedList){
          System.out.print("\n The document is \""+myDocs[d1.docId] + "\" \nDOCID: "+d1.docId + " Starting Positions of the query are: ");
          for(int pos : d1.positionList){
            System.out.print(pos-(queries.length-1)+" ");
          }
       }System.out.println();
     }          
     else{
     System.out.println("The given phrase query is not found in any of the documents ");
      }        
   }

   //Main Method
   public static void main(String[] args)throws IOException{
      String[] docs = {"text warehousing over big data nlp before text",
                       "dimensional data warehouse over big data nlp before",
                       "nlp before text mining",
                       "nlp before text classification"};
                       
      PositionalIndex pi = new PositionalIndex(docs);      
      System.out.println(pi);
      // Test Cases
      pi.phraseQuery("data warehouse");
      pi.phraseQuery("nlp before text");
      pi.phraseQuery("before text mining");
      pi.phraseQuery("data warehouse over big");
      pi.phraseQuery("text warehousing over big data");              
   }
}
//Doc class
class Doc {
   int docId;
   ArrayList<Integer> positionList; //position of occurences of term in a doc
   
   public Doc(int did, int position) {
      docId = did;
      positionList = new ArrayList<Integer>();
      if(position >=0)
        positionList.add(position);
   }
   

   public void insertPosition(int position) {
      if(position >= 0)
         positionList.add(position);
   }
   
   public String toString() {
      String docIdString = docId + ":<";
      for(Integer pos:positionList) {
         docIdString += pos + ",";
      }
      docIdString = docIdString.substring(0,docIdString.length()-1) + ">";
      return docIdString;
   }
}