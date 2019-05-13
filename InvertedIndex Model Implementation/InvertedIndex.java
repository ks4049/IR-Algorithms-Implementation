import java.io.*;
import java.util.*;
/**
 * ISTE-612 LAB1_2185 Inverted Index Constuction and Query Processing
 * Khavya Seshadri
 **/
public class InvertedIndex{
	String[] fileNames;
	ArrayList<String> termList = new ArrayList<String>();
	ArrayList<ArrayList<Integer>> termPostList = new ArrayList<ArrayList<Integer>>();

	//constructor to initialize the file names
	public InvertedIndex(File[] files){
		// Store the fileNames
		System.out.println("The files order is ");
		fileNames = new String[files.length];
		for(int i=0; i<files.length; i++){
			fileNames[i] = files[i].getName();		
			System.out.println(fileNames[i]);	
		}
	}

	//Print the terms and the posting list i.e. the inverted index matrix
	public String toString(){		
		String outputStr = "";
		//System.out.println("Inverted Index Matrix ");
		for(String term : termList){
			outputStr+=String.format("%-15s", term);
			ArrayList<Integer> postListing = termPostList.get(termList.indexOf(term));
			for(int fileID : postListing){
				outputStr+= fileID+" ";
			}outputStr+="\n";
		}
		return outputStr;
	}

	//form the inverted index with the given stemmed words and the the file index
	public void constructInvertedIndex(ArrayList<String> finalTerms, int fileIndex){
		for(String term : finalTerms){
			ArrayList<Integer> postList;
			if(!termList.contains(term)){
				termList.add(term);
				postList = new ArrayList<Integer>();
				postList.add(fileIndex+1);
				termPostList.add(postList);
			}else{
				int termIndex = termList.indexOf(term);
				postList = termPostList.get(termIndex);
				if(!postList.contains(fileIndex+1)){
					postList.add(fileIndex+1);
					termPostList.set(termIndex, postList);
				}
			}
		}
	}

	// Search for a Query
	public ArrayList<Integer> search(String query){
		int termIdx = termList.indexOf(query);
		if(termIdx < 0)
			return null;
		ArrayList<Integer> postingList = termPostList.get(termIdx);
		return postingList!=null && !postingList.isEmpty() ? postingList : null;
	}

	//Sort the posting lists based on Length
	public void sortPostingListByLength(String[] terms,ArrayList<ArrayList<Integer>> reqPostList){
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

	// perform merge operation based on the operation
	public ArrayList<Integer> merge(String[] queries, String operation){
		ArrayList<ArrayList<Integer>> reqPostList = new ArrayList<ArrayList<Integer>>();
		for(String query : queries){
			if(termList.contains(query)){
				int index = termList.indexOf(query);
				ArrayList<Integer> postList =  termPostList.get(index);
				reqPostList.add(postList);
			}
		}
		//Sorting the lists based on length
		sortPostingListByLength(queries,reqPostList);
		// Call the intersection/union algorithm based on operation
		if(operation.toUpperCase().equals("AND")){
			if(reqPostList.size()!= queries.length){
				return null;
			}
		    while(reqPostList.size() > 1){
		      ArrayList<Integer> mergedList = mergeIntersection(reqPostList.get(0), reqPostList.remove(1));
		      if(!mergedList.isEmpty())
		        reqPostList.set(0, mergedList);
		      else
		        return null;
		    }
		}else if(operation.toUpperCase().equals("OR")){
			System.out.println("Performing OR operation");
		    while(reqPostList.size() > 1){
		      ArrayList<Integer> mergedList = mergeUnion(reqPostList.get(0), reqPostList.remove(1));
		      if(!mergedList.isEmpty())
		        reqPostList.set(0, mergedList);
		      else
		        return null;
		    }
		}		
	    return reqPostList.get(0);
	}

	//Perform AND operation on queries
	public ArrayList<Integer> mergeIntersection(ArrayList<Integer> list1, ArrayList<Integer> list2){
		ArrayList<Integer> resultList = new ArrayList<Integer>();
		for(int item : list1){
			if(list2.contains(item))
				resultList.add(item);
		}
		return resultList;
	}

	//perform OR operation on queries
	public ArrayList<Integer> mergeUnion(ArrayList<Integer> list1, ArrayList<Integer> list2){
		ArrayList<Integer> resultList = new ArrayList<Integer>(list1);
		for(int item : list2){
			if(!resultList.contains(item)){
				resultList.add(item);
			}
		}
		return resultList;
	}

}
