import java.util.*;

public class BTreeIndex2 {
	String[] myDocs;
	BinaryTree termList = new BinaryTree();
	BTNode root;
	
	/**
	 * Construct binary search tree to store the term dictionary 
	 * @param docs List of input strings
	 * 
	 */
	public BTreeIndex2(String[] docs)
	{
		//TO BE COMPLETED		
		myDocs=docs;
		for(int i=0; i<docs.length; i++){
			String[] words = docs[i].split(" ");
			for(String word : words){
				ArrayList<Integer> postingList = new ArrayList<Integer>();
				if(root!=null){
					BTNode searchTermNode =termList.search(root,word);
					if(searchTermNode ==null){
						postingList.add(i);
						BTNode newNode = new BTNode(word, postingList);
						termList.add(root, newNode);
					}else{
						postingList = searchTermNode.docLists;
						boolean isFound = false;
						for(int docID : postingList){
							if(i == docID){
								isFound = true;
								break;
							}
						}
						if(!isFound){
							postingList.add(i);
							searchTermNode.docLists = postingList;
						}
					}
				}else{
					postingList.add(i);
					BTNode newNode = new BTNode(word, postingList);
					root = newNode;
				}
			}
		}
	}


	public void printInOrder(){
		if(root!=null){
			termList.printInOrder(root);
		}
	}
	
	/**
	 * Single keyword search
	 * @param query the query string
	 * @return doclists that contain the term
	 */
	public ArrayList<Integer> search(String query)
	{
			BTNode node = termList.search(root, query);
			if(node==null)
				return null;
			return node.docLists;
	}
	
	/**
	 * conjunctive query search
	 * @param query the set of query terms
	 * @return doclists that contain all the query terms
	 */
	public ArrayList<Integer> search(String[] query)
	{
		ArrayList<Integer> result = search(query[0]);
		int termId = 1;
		while(termId<query.length)
		{
			ArrayList<Integer> result1 = search(query[termId]);
			result = merge(result,result1);
			termId++;
		}		
		return result;
	}
	
	/**
	 * 
	 * @param wildcard the wildcard query, e.g., ho (so that home can be located)
	 * @return a list of ids of documents that contain terms matching the wild card
	 */
	public ArrayList<Integer> wildCardSearch(String wildcard)
	{	
		ArrayList<Integer> resultDocIDs = new ArrayList<Integer>();	
		ArrayList<BTNode> matchingTerms = termList.wildCardSearch(root, wildcard);
		resultDocIDs = matchingTerms.get(0).docLists;
		for(int i=1; i<matchingTerms.size(); i++){
			ArrayList<Integer> termDocIds = matchingTerms.get(i).docLists;
			resultDocIDs = mergeUnion(resultDocIDs, termDocIds);
		}
		return !resultDocIDs.isEmpty()? resultDocIDs:null;	
	}

	/**
	 * 
	 * @param  list1 containing the document IDs of term1
	 * @param  list2 containing the document IDs of term2
	 * @return a list of document IDs i.e. the union of list1 and list2
	 */
	private ArrayList<Integer> mergeUnion(ArrayList<Integer> l1, ArrayList<Integer> l2){
		ArrayList<Integer> resultList = new ArrayList<Integer>();
		resultList.addAll(l1);
		int idx1=0, idx2=0;
		while((idx1 < resultList.size()) && (idx2 < l2.size())){
			if(resultList.get(idx1)==l2.get(idx2)){
				idx1++;
				idx2++;				
			}else if(resultList.get(idx1)< l2.get(idx2)){
				idx1++;
			}else{
				resultList.add(idx1, l2.get(idx2));
				idx1++;
				idx2++;
			}	
		}
		while(idx2<l2.size()){
			resultList.add(l2.get(idx2));
			idx2++;
		}
		return resultList;
	}

	/**
	 * 
	 * @param  l1:list1 containing the document IDs of term1
	 * @param  l2:list2 containing the document IDs of term2
	 * @return a list of document IDs i.e. the intersection of list1 and list2
	 */
	private ArrayList<Integer> merge(ArrayList<Integer> l1, ArrayList<Integer> l2)
	{
		ArrayList<Integer> mergedList = new ArrayList<Integer>();
		int id1 = 0, id2=0;
		while(id1<l1.size()&&id2<l2.size()){
			if(l1.get(id1).intValue()==l2.get(id2).intValue()){
				mergedList.add(l1.get(id1));
				id1++;
				id2++;
			}
			else if(l1.get(id1)<l2.get(id2))
				id1++;
			else
				id2++;
		}
		return mergedList;
	}
	
	public void printResultDocs(ArrayList<Integer> resultDocs, String query){
		System.out.println("\nThe query/wildcard \""+ query +"\" is present in the following documents");
		for(int docID : resultDocs){
			System.out.println(myDocs[docID]+" ");
		}
	}
	
	/**
	 * Test cases
	 * @param args commandline input
	 */
	public static void main(String[] args)
	{
		String[] docs = {"text warehousing over big data",
                       "dimensional data warehouse over big data",
                       "nlp before text mining",
                       "nlp before text classification"};
		BTreeIndex2 btIndex = new BTreeIndex2(docs);
		btIndex.printInOrder();
		//Single Query search
		String singleQuery ="data";
		ArrayList<Integer> resultDocs = btIndex.search(singleQuery);
		btIndex.printResultDocs(resultDocs, singleQuery);		

		//Conjunctive query search
		String phraseQuery = "warehousing big data text";
		resultDocs = btIndex.search(phraseQuery.split(" "));		
		btIndex.printResultDocs(resultDocs, phraseQuery);		
		
		//WildCard search
		String wildCard = "nlp";
		resultDocs = btIndex.wildCardSearch(wildCard);
		btIndex.printResultDocs(resultDocs, wildCard);
	}
}