import java.util.*;
/**
 * 612-2185 LBE07 Vector Space Model
 * Khavya Seshadri
 */
 
public class VSM {       
   //attributes
   private String[] myDocs;
   private ArrayList<String> termList;
   private ArrayList<ArrayList<Doc>> docLists;
   private double[] docLength;                     //for normalization
   
   // constructor
   public VSM(String[] docs) {         
      //instanciate the attributes
      myDocs = docs;
      termList = new ArrayList<String>();
      
      docLists = new ArrayList<ArrayList<Doc>>();
      ArrayList<Doc> docList;
      for(int i=0;i<myDocs.length;i++) {
         String[] words = myDocs[i].split(" ");
         String word;
         
         for(int j=0;j<words.length;j++) {
            boolean match = false;
            word = words[j];
            if(!termList.contains(word)) {
               termList.add(word);
               docList = new ArrayList<Doc>();
               Doc doc = new Doc(i, 1); // raw term freq is one
               docList.add(doc);
               docLists.add(docList);
            }
            else {
               int index = termList.indexOf(word);
               docList = docLists.get(index);
               
               for(Doc did:docList) {
                  if(did.docId == i) {
                     did.tw++;
                     match = true;
                     break;
                  }               
               }
               if(!match) {
                  Doc doc = new Doc(i,1);
                  docList.add(doc);
                  docLists.set(index, docList);
               }
            }
         }
      
      }
      int N = myDocs.length;
      docLength = new double[N];
      for(int i=0; i<termList.size(); i++){
         ArrayList<Doc> postingList = docLists.get(i);
         int df = postingList.size();         
         for(Doc doc : postingList){
            double tfidf = (1+Math.log10(doc.tw))*(Math.log10(1.0*N/df));
            doc.tw = tfidf;
            docLength[doc.docId]+= Math.pow(tfidf,2);
         }
      }
      for(int i=0; i<N; i++){
         if(docLength[i]!=0){
             docLength[i] = Math.sqrt(docLength[i]);
          }        
      }
   }
   

   public void rankSearch(String[] query) {
     double score=0;
     int N = myDocs.length;
     HashMap<Integer, Double> scores = new HashMap<Integer, Double>();
     double queryLength=0;
     for(String term : query){
      int index = termList.indexOf(term);
      if(index<0)
         continue;
      ArrayList<Doc> docList = docLists.get(index);
      double qtfidf = (1+Math.log10(1))*(Math.log10(1.0*N/docList.size()));
      queryLength+= Math.pow(qtfidf,2);
      for(Doc d : docList){
         score=qtfidf*d.tw;
         if(!scores.containsKey(d.docId)){
            scores.put(d.docId, score);
         }else{
            score+=scores.get(d.docId);
            scores.put(d.docId, score);
         }
      }
    }
    queryLength = Math.sqrt(queryLength);
    // System.out.println(" "+scores);
    for(int docID : scores.keySet()){      
     // System.out.println(" "+docLength[docID]+ " "+queryLength);
      double normScore = scores.get(docID)/(docLength[docID]*queryLength);
      scores.put(docID, normScore);      
    }
       // Map<Integer, Double> sortedScores = new LinkedHashMap<>();
      // scores.entrySet().stream().sorted(Map.Entry.<Integer, Double>comparingByValue(Comparator.reverseOrder())).forEachOrdered(s->sortedScores.put(s.getKey(), s.getValue()));
      System.out.println(scores);

   }
   public String toString() {
      String outString = new String();
      ArrayList<Doc> docList;       
      for(int i=0;i<termList.size();i++) {
         outString += String.format("%-15s", termList.get(i));
         docList = docLists.get(i);
         for(int j=0;j<docList.size();j++) {
            outString += docList.get(j).docId + "  "+docList.get(j).tw+"\t";
         }
         outString += "\n";
      }
      return outString;
   }
   
   public static void main(String[] args) {
      String[] docs = {"text warehousing over big data",
                       "dimensional data warehouse over big data",
                       "nlp before text mining",
                       "nlp before text classification"};
      // Test VSM 
      VSM vsm = new VSM(docs);
      System.out.println(vsm);

      String[] query1 = { "data"};
      String[] query2 = { "data", "warehousing"};
      String[] query3 = { "data", "big"};
      String[] query4 = { "text", "big","tech", "rochester"};
      String[] 
      
      vsm.rankSearch(query1);
      vsm.rankSearch(query2);
      vsm.rankSearch(query3);
      vsm.rankSearch(query4);     
   }
}

class Doc {
   int docId;
   double tw;
   Doc(int docId, double tw){
      this.docId = docId;
      this.tw = tw;
   }   
}
