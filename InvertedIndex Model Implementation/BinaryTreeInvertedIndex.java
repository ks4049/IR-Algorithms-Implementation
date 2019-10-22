
import java.util.*;
import java.io.*;

/**
 * 
 * @author 
 * a node in a binary search tree
 */
class BTNode{
	BTNode left, right;
	String term;
	ArrayList<Integer> postingList;
	
	/**
	 * Create a tree node using a term and a document list
	 * @param term the term in the node
	 * @param docList the ids of the documents that contain the term
	 */
	public BTNode(String term, ArrayList<Integer> docList)
	{
		this.term = term;
		this.postingList = docList;
	}
	
}

/**
 * 
 * Binary search tree structure to store the term dictionary
 */
public class BinaryTreeInvertedIndex {

	BTNode root;
	String[] myDocs;


	//Inverted Index Construction using BST
	public BinaryTreeInvertedIndex(String[] docs){
		myDocs = docs;
		for(int i=0; i<myDocs.length; i++){
			String[] words = myDocs[i].split(" ");
			for(String word: words){
				if(root != null){
					ArrayList<Integer> postList;
					BTNode termNode = search(root, word);
					if(termNode!=null){
						postList = termNode.postingList;
						if(!postList.contains(i)){
							postList.add(i);
							termNode.postingList =postList;
						}
					}else{
					 	postList = new ArrayList<Integer>();
					 	postList.add(i);
						BTNode newNode = new BTNode(word, postList);
						add(root, newNode);
					}
				}else{
					ArrayList<Integer> postList = new ArrayList<Integer>();
					postList.add(i);
					root = new BTNode(word, postList);
				}
			}
		}
		printInOrder(root);
	}
	/**
	 * insert a node to a subtree 
	 * @param node root node of a subtree
	 * @param iNode the node to be inserted into the subtree
	 */
	public void add(BTNode node, BTNode iNode)
	{
		//TO BE COMPLETED
		if(node.term.compareTo(iNode.term) > 0){
			if(node.left!=null){
				add(node.left, iNode);
			}else{
				node.left = iNode;
			}
		}else if(node.term.compareTo(iNode.term) < 0){
			if(node.right !=null){
				add(node.right, iNode);
			}else{
				node.right = iNode;
			}
		}
	}
	
	/**
	 * Search a term in a subtree
	 * @param n root node of a subtree
	 * @param key a query term
	 * @return tree nodes with term that match the query term or null if no match
	 */
	public BTNode search(BTNode n, String key)
	{
		if(n == null)
			return null;
		if(n.term.equals(key)){
			return n;
		}
		if(n.term.compareTo(key) > 0){
			return search(n.left, key);
		}else{
			return search(n.right, key);
		}
	}
	
	/**
	 * Do a wildcard search in a subtree
	 * @param n the root node of a subtree
	 * @param key a wild card term, e.g., ho (terms like home will be returned)
	 * @return tree nodes that match the wild card
	 */
	// public ArrayList<BTNode> wildCardSearch(BTNode n, String key)
	// {
	// 	//TO BE COMPLETED
	// }
	
	/**
	 * Print the inverted index based on the increasing order of the terms in a subtree
	 * @param node the root node of the subtree
	 */
	public void printInOrder(BTNode node)
	{
		if(node!=null){
			printInOrder(node.left);
			System.out.println("Term : "+node.term + " Posting List "+node.postingList);	
			printInOrder(node.right);
		}		
	}

	/**
	*Merge algorithm for single term query and conjunctive queries
	**/
	public ArrayList<Integer> merge(String query){
		String[] queries = query.split(" ");
		if(queries.length==1){
			BTNode resultNode = search(root, queries[0]);
			return resultNode!=null ? resultNode.postingList : null;
		}else{
			ArrayList<ArrayList<Integer>> reqPostingList = new ArrayList<ArrayList<Integer>>();
			for(String q : queries){
				BTNode current = search(root, q);
				if(current!=null){
					reqPostingList.add(current.postingList);
				}else{
					return null;
				}				
			}
			sortByPostingListSize(queries, reqPostingList);
			System.out.println("the Query Processing order");
			for(int j=0; j<queries.length; j++){
				System.out.print(queries[j]+ " ");
			}
			ArrayList<Integer> mergedList = new ArrayList<Integer>();
			while(reqPostingList.size() >1){
				mergedList = mergeIntersection(reqPostingList.get(0), reqPostingList.remove(1));
				if(mergedList.size()>0){
					reqPostingList.set(0, mergedList);
				}else{
					return null;
				}
			}
			return mergedList;
		}
	}

	public ArrayList<Integer> mergeIntersection(ArrayList<Integer> list1, ArrayList<Integer> list2){
		ArrayList<Integer> resultList = new ArrayList<Integer>();
		int idx1 =0, idx2=0;
		while((idx1<list1.size()) && (idx2<list2.size())){
			if(list1.get(idx1)== list2.get(idx2)){
				resultList.add(list1.get(idx1));
				idx1++;
				idx2++;			
			}else if(list1.get(idx1) < list2.get(idx2)){
				idx1++;
			}else
				idx2++;
		}
		return resultList;
	}

	public void sortByPostingListSize(String[] terms,ArrayList<ArrayList<Integer>> reqPostList){
		for(int i=0; i<reqPostList.size()-1; i++){
	      for(int j=i+1; j<reqPostList.size(); j++){
	        ArrayList<Integer> list1 = reqPostList.get(i);
	        ArrayList<Integer> list2 = reqPostList.get(j);
	        if(list1.size() > list2.size()){
	          ArrayList<Integer> tempList = reqPostList.get(i);
	          String tempTerm = terms[i];
	          reqPostList.set(i, reqPostList.get(j));
	          terms[i] = terms[j];
	          reqPostList.set(j, tempList);
	          terms[j] = tempTerm;
	        }
	      }
	    }    
	}

	//Main method
	public static void main(String[] args){
		String[] docs = {"text data warehousing over big data",
                       "dimensional data warehousing over big data",
                       "nlp before text mining",
                       "nlp before text classification"}; 
	  System.out.println("The inverted index in order of the terms ");	                       
	  BinaryTreeInvertedIndex bt = new BinaryTreeInvertedIndex(docs);
	  //Merge operation on query or conjunctive queries
	  String query = "warehousing";
	  String queries = "text data warehousing";
	  ArrayList<Integer> resultDocs = bt.merge(query);
	 if(resultDocs!=null){
	  	 for(int docId : resultDocs){
	  		System.out.println(" "+docs[docId]);
	  	}
	  }
	  resultDocs = bt.merge(queries);
	  if(resultDocs!=null){
	  	 for(int docId : resultDocs){
	  		System.out.println(" "+docs[docId]);
	  	}
	  }
	}
}

