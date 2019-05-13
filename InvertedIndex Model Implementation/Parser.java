import java.io.*;
import java.util.*;
/**
 * ISTE-612 LAB1_2185 Text Processing 
 * Parsing the text in the files : tokenization, removing stop words and stemming
 * Khavya Seshadri
 **/
public class Parser{

	String[] stopWords = null;
 	
   	public Parser()throws IOException{
   		// read the stop words text file
   		File stopWordsFile = new File("stopwords.txt");
   		String allWords = new String();
   		Scanner s = new Scanner(stopWordsFile);
   		while(s.hasNextLine()){
   			allWords+= s.nextLine().toLowerCase()+" ";
   		}
   		stopWords = allWords.split("[ ']");
   		Arrays.sort(stopWords);
   	}

   	public ArrayList<String> parse(File fileName)throws IOException{
   		String[] tokens = null;
   		ArrayList<String> pureTokens = new ArrayList<String>();
   		ArrayList<String> stems = new ArrayList<String>();
   		Scanner sc = new Scanner(fileName);
   		String allLines = new String();
   		while(sc.hasNextLine()){
   			allLines += sc.nextLine().toLowerCase();			
   		}
   		tokens = allLines.split("[ '.,&#?!:;$%+()\\-\\/*\"]+"); 
         //Removing stop words
   		for(String token: tokens){
   			if(!token.equals("") && searchStopWord(token) == -1){
   				pureTokens.add(token);
   			}
   		}
         // Perform Stemming of pure tokens(the tokens where stop words have been removed)
         Stemmer st = new Stemmer();
         for(String token : pureTokens){
            st.add(token.toCharArray(), token.length());
            st.stem();
            //System.out.println("The stemmed word "+st.toString());
            stems.add(st.toString());
         }
         return stems;
   	}

   	public int searchStopWord(String key){
   		int low=0, high=stopWords.length-1;
   		while(low <= high){
   			int mid = (high+low)/2;
   			int result = key.compareTo(stopWords[mid]);
   			if(result < 0){
   				high = mid-1;
   			}else if(result > 0){
               low = mid+1;
   			}else{
   				return mid;
   			}
   		}
         return -1;
   	}

	public static void main(String[] args) throws IOException{
		//Read the fileNames in the folder
		Parser pObj = new Parser();
		File folder = new File("Lab1_Data");
		File[] files = folder.listFiles();
      InvertedIndex im = new InvertedIndex(files);  
      // Construction of Inverted Index Matrix       
		for(int index=0; index<files.length; index++){
			ArrayList<String> stemmedList = pObj.parse(files[index]);
         // traverse through the stemmed words to form the inverted index matrix
         im.constructInvertedIndex(stemmedList, index);        
		}
      //print the inverted index matrix (i.e. The term-document and its posting list)
     System.out.println("The inverted matrix is :\n"+im.toString());

      //Query Processing Module
      ArrayList<Integer> resultList;
      char ch;
      do{
         System.out.println("Enter the query/ queries, separated by space  ");
         BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
         String query = br.readLine();
         String operation;
         String[] queries = query.toLowerCase().split(" ");
         Stemmer st = new Stemmer();
         String[] stemmedQueries = new String[queries.length];
         for(int i=0; i<queries.length; i++){
            st.add(queries[i].toCharArray(), queries[i].length());
            st.stem();
            stemmedQueries[i]= st.toString();
            System.out.println("The stemmed word for the query "+ queries[i] + "is "+ stemmedQueries[i]);
         }
         if(stemmedQueries.length ==1){
            resultList = im.search(stemmedQueries[0]); 
         }else{
            System.out.println("Enter the operation to be performed between the queries : (AND | OR) ");
            operation = br.readLine();
            resultList = im.merge(stemmedQueries, operation); 
            System.out.println("The order of queries being processed :");
            for(int i=0; i<stemmedQueries.length; i++){
               for(int q=0; q<queries.length; q++){
                  if(queries[q].startsWith(stemmedQueries[i])){
                     System.out.println(" "+queries[q]);
                     break;
                  }
               }
            }           
         }
         //print the results
         if(resultList!=null){
            System.out.println("The queries/query are found in the following files ");
            for(int fileID : resultList){
               System.out.println(" "+files[fileID-1].getName());
            }System.out.println();
         }else{
            System.out.println("The query/queries are not found in any of the documents");
         }
         System.out.println("Do you wish to continue ? (Y/N)");
         ch = (char)br.read();
      }while(Character.toUpperCase(ch) == 'Y');      
	}
}














