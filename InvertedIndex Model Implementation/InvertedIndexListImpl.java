import java.io.*;
import java.util.*;
/*
 *Implementation of Inverted Index (term with posting list(w/o using inbuilt Java's ArrayList))
 *612 LBE01 InvertedIndex (With the implementation of Linked List)
 *Khavya Seshadri
*/
public class InvertedIndexListImpl{
	String[] docs; // the input documents
	ArrayList<String> termList; //the dictionary of terms
	ArrayList<ListNode> docLists;

	//Constructor to form the invertedIndex matrix
	public InvertedIndexListImpl(String[] myDocs){
		docs = myDocs;
		termList = new ArrayList<String>();
		docLists = new ArrayList<ListNode>();
		for(int i=0; i<docs.length;i++){
			String[] words = docs[i].split(" ");
			for(String word: words){
				//if term doesnt exist in dictionary
				if(!termList.contains(word)){
					termList.add(word);
					ListNode newNode = new ListNode(i+1);
					ListNode head = newNode;
					docLists.add(head);
				}// if the term exists in the dictionary
				else{
					int index = termList.indexOf(word);
					ListNode listHead = docLists.get(index);
					insertNode(listHead, i+1);
				}
			}
		}
	}

	//Insert node into the Linked List
	public static void insertNode(ListNode head, int x){
		ListNode newNode = new ListNode(x);
		ListNode temp = head;
		while(temp.next!=null && temp.data!=x){
			temp = temp.next;
		}
		if(temp.data !=x)
			temp.next = newNode;
	}

	//Overriding the toString() method of Object class
	public String toString(){
		String outputStr = "";
		for(int i=0; i<termList.size(); i++){
			outputStr+=String.format("%-15s", termList.get(i));
			ListNode listHead = docLists.get(i);
			ListNode temp = listHead;
			while(temp!=null){
				outputStr+=temp.data+"->";
				temp = temp.next;
			} outputStr+="null\n";
		}
		return outputStr;
	}

	//Search for the query
	//returns the list of document ID's where the term is present
	public void search(String query){
		if(termList.contains(query)){
			int index = termList.indexOf(query);
			ListNode headNode = docLists.get(index);
			ListNode temp = headNode;
			// print the docIDs 
			System.out.println("The query is present in the following documents	");
			while(temp!= null){
				System.out.print(" "+temp.data);
				temp = temp.next;
			}
		}
	}

	//Merge Intersection algorithm (AND operation)
	public void mergeIntersection(String queries){
		String[] terms = queries.split(" ");
		if(termList.contains(terms[0]) && termList.contains(terms[1])){
			int idx1 = termList.indexOf(terms[0]);
			int idx2 = termList.indexOf(terms[1]);
			ListNode list1 = docLists.get(idx1);
			ListNode list2 = docLists.get(idx2);
			System.out.println("The queries are present in the following documents	");
			while(list1!=null && list2!=null){
				if(list1.data == list2.data){
					System.out.print(" "+list1.data);
					list1 = list1.next;
					list2 = list2.next;
				}else if(list1.data < list2.data){
					list1 = list1.next;
				}else{
					list2 =list2.next;
				}
			}
		}
	}

	// Merge Union (OR operation)
	public void mergeUnion(String queries){
		
	}

	//main method
	public static void main(String[] args) throws IOException{
		String[] myDocs = {"text data warehousing over big data",
                       "dimensional data warehousing over big data",
                       "nlp before text mining",
                       "nlp before text classification"}; 
      	InvertedIndexListImpl im = new InvertedIndexListImpl(myDocs);
      	System.out.println(im);
      	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
      	String query = br.readLine();
      	//Search for a term
      	im.search(query);
		String queries = br.readLine();
      	String op = br.readLine();
      	switch(op.toUpperCase()){
      		case "AND": 
      			System.out.println("performing AND operation");
      			im.mergeIntersection(queries);
      			break;
      		case "OR":
      			System.out.println("Performing OR operation");
      			im.mergeUnion(queries);
      			break;
  			default: 
  				im.search(queries);
      	}
     }
}

// Class ListNode
public class ListNode{
	int data;
	ListNode next;
	ListNode(int data){
		this.data = data;
		this.next = null;		
	}
}
