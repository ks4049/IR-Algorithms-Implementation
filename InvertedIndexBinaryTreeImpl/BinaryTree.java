
import java.util.*;

/**
 * 
 * @author
 * Khavya Seshadri 
 * 
 */
class BTNode{
	BTNode left, right;
	String term;
	ArrayList<Integer> docLists;
	
	/**
	 * Create a tree node using a term and a document list
	 * @param term the term in the node
	 * @param docList the ids of the documents that contain the term
	 */
	public BTNode(String term, ArrayList<Integer> docList)
	{
		this.term = term;
		this.docLists = docList;
	}
	
}

/**
 * 
 * Binary search tree structure to store the term dictionary
 */
public class BinaryTree {

	/**
	 * insert a node to a subtree 
	 * @param node root node of a subtree
	 * @param iNode the node to be inserted into the subtree
	 */
	public void add(BTNode node, BTNode iNode)
	{
		if(node == null)
			return;
		else{
			if(node.term.compareTo(iNode.term) < 0){
				if(node.right!=null){
					add(node.right, iNode);				
				}else{
					node.right = iNode;
				}
			}else if(node.term.compareTo(iNode.term)>0){
				if(node.left!=null){
					add(node.left, iNode);
				}else{
					node.left = iNode;
				}
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
		if(n==null)
			return null;
		if(n.term.equals(key)){
			return n;
		}else if(n.term.compareTo(key) < 0){
			return search(n.right, key);
		}else{
			return search(n.left, key);
		}
	}
	
	/**
	 * Do a wildcard search in a subtree
	 * @param n the root node of a subtree
	 * @param key a wild card term, e.g., ho (terms like home will be returned)
	 * @return tree nodes that match the wild card
	 */
	public ArrayList<BTNode> wildCardSearch(BTNode n, String key)
	{
		ArrayList<BTNode> matchingTerms = new ArrayList<BTNode>();		
		if(n==null)
			return null;
		ArrayList<BTNode> tempTerms;
		if(n.term.startsWith(key)){
			matchingTerms.add(n);
			tempTerms = wildCardSearch(n.left, key);
			 if(tempTerms!=null)
			 	matchingTerms.addAll(tempTerms);
			tempTerms=wildCardSearch(n.right, key);
			if(tempTerms!=null)
				matchingTerms.addAll(tempTerms);			
			return matchingTerms;
		}else if (n.term.compareTo(key) < 0)
			 return wildCardSearch(n.right, key);
		else
			return wildCardSearch(n.left, key);			
		
	}
	
	/**
	 * Print the inverted index based on the increasing order of the terms in a subtree
	 * @param node the root node of the subtree
	 */
	public void printInOrder(BTNode node)
	{		
		if(node == null){
			return;			
		}
		printInOrder(node.left);
		System.out.println(node.term +" -->> "+node.docLists);
		printInOrder(node.right);
	}
}

