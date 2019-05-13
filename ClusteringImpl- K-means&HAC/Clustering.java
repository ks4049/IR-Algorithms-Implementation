
import java.util.*;

/**
 * 612 Lab 5
 * Document clustering
 */
public class Clustering {
	
	int numDocs;
	int numClusters;
	int vSize;
	String[] myDocs;
	Doc[] docList;
	Doc[] clusters;
	HashMap<String, Integer> termIdMap;
	HashMap<Integer,double[]>[] clusterDocs;	
	Doc[] centroids;

	//hierarchical clustering
	ArrayList<ArrayList<Double>> similarityMatrix;


	public Clustering(int numC)
	{
		numClusters = numC;
 		clusterDocs = new HashMap[numClusters];
		centroids = new Doc[numClusters];
		termIdMap = new HashMap<String, Integer>();
	}
	
	/**
	 * Load the documents to build the vector representations
	 * @param docs
	 */
	public void preprocess(String[] docs){
		myDocs = docs;
		numDocs = docs.length;
		docList = new Doc[numDocs];
		int termId = 0;
		
		//collect the term counts, build term id map and the idf counts
		int docId = 0;
		for(String doc:docs){
			String[] tokens = doc.split(" ");
			Doc docObj = new Doc(docId);
			for(String token: tokens){
				if(!termIdMap.containsKey(token)){
					termIdMap.put(token, termId);
					docObj.termIds.add(termId);
					docObj.termWeights.add(1.0);					
					termId++;
				}
				else{
					Integer tid = termIdMap.get(token);
					int index = docObj.termIds.indexOf(tid);
					if (index >0){
						double tw = docObj.termWeights.get(index);
						docObj.termWeights.add(index, tw+1);
					}
					else{
						docObj.termIds.add(termIdMap.get(token));
						docObj.termWeights.add(1.0);
					}
				}
			}
			docList[docId] = docObj;
			docId++;
		}
		vSize = termId;
		System.out.println("vSize: " + vSize);
		
		//compute the tf-idf weights of documents
		for(Doc doc: docList){
			double docLength = 0;
			double[] termVec = new double[vSize];
			for(int i=0;i<doc.termIds.size();i++){
				Integer tid = doc.termIds.get(i);
				double tfidf = (1+Math.log(doc.termWeights.get(i)));//Math.log(numDocs/idfMap.get(tid));				
				doc.termWeights.set(i, tfidf);
				docLength += Math.pow(tfidf, 2);
			}
			docLength = Math.sqrt(docLength);
			//normalize the doc vector			
			for(int i=0;i<doc.termIds.size();i++){
				double tw = doc.termWeights.get(i);
				doc.termWeights.set(i, tw/docLength);
				//System.out.println(doc.termIds.get(i));
				termVec[doc.termIds.get(i)] = tw/docLength;

			}
			doc.termVec = termVec;
			//doc.termIds = null;
			//doc.termWeights = null;
		}
	}
	
	/**
	 * Cluster the documents
	 * For kmeans clustering, use the first and the ninth documents as the initial centroids
	 */
	public void cluster(){
		//Number of clusters k=2
		double[][] centroidVector = new double[numClusters][vSize];
		clusters = new Doc[numClusters];
		centroids[0] = new Doc();//docList[0];
		centroids[1] = new Doc();//docList[8];
		centroids[0].docId = docList[0].docId;
		centroids[1].docId = docList[8].docId;		
		centroids[0].termVec = new double[vSize];
		centroids[1].termVec = new double[vSize];
		//Assigning initial centroids
		for(int i=0; i<docList[0].termVec.length; i++){
			centroids[0].termVec[i] = docList[0].termVec[i];
		}
		for(int i=0; i<docList[8].termVec.length; i++){
			centroids[1].termVec[i] = docList[8].termVec[i];
		}
		//numDocs-numClusters : number of docs to compute the distance(euclidean distance)
		double[][] similarity = new double[numClusters][numDocs];	
		double[][] newCentroids=null;	
		int counter=0;
		System.out.println("\n*********************K-Means Clustering*********************\n");
		do{
		//compute the eucledian distance
			if(newCentroids!=null){
				centroids[0].termVec = newCentroids[0];
				centroids[1].termVec = newCentroids[1];
			}
			printCentroids();	
			centroidVector[0] = centroids[0].termVec;//cluster 0
			centroidVector[1] = centroids[1].termVec; //cluster 1
			for(int i=0; i<centroidVector.length; i++){
				for(int j=0; j<docList.length; j++){													     
					double cosineSim=0;	
					int currentDocID = docList[j].docId;
					double[] currentTermVec = docList[j].termVec;
					for(int l=0; l<currentTermVec.length; l++){
						cosineSim+=currentTermVec[l]*centroidVector[i][l];
					}
					similarity[i][currentDocID] = cosineSim;
				}
			}
			//Assigns the documents to the clusters
			assignDocsToCluster(similarity);
			//Recompute the new centroids	
			newCentroids = computeNewCentroids();
			counter+=1;
		}while(!checkIfEqual(newCentroids));
 	}
 	
 	/**
 	*@param newCentroids the new centroid of the clusters 
 	*@return returns true if the centroids are equal, otherwise false
 	*/
 	public boolean checkIfEqual(double[][] newCentroids){
 		double[][] oldCentroids = new double[numClusters][vSize];
 		oldCentroids[0] = centroids[0].termVec;
 		oldCentroids[1] = centroids[1].termVec;
 		int count=0;
 		for(int i=0; i<vSize; i++){
			if((newCentroids[0][i]!=oldCentroids[0][i]) || newCentroids[1][i]!=oldCentroids[1][i]){
				return false;
			}else if((newCentroids[0][i]==oldCentroids[0][i]) &&(newCentroids[1][i]==oldCentroids[1][i])){
				count++;
			}else{
				return false;
			}			 			
 		}
 		if(count==vSize){
 			return true;
 		}
 		return false; 			 		
 	}

	/**
	*Computes the new centroids(average of the data points in the cluster)
	*@return the new centroid
	*/
 	public double[][] computeNewCentroids(){
 		double[][] newCentroid = new double[numClusters][vSize];
 		for(int i=0; i<clusterDocs.length; i++){ 	 					
 			HashMap<Integer,double[]> documents = clusterDocs[i];
 			for(int docID : documents.keySet()){
 				double[] currentVec = documents.get(docID);
 				for(int m=0; m<currentVec.length; m++){
 					//System.out.print(" "+currentVec[m] +" ");
 					newCentroid[i][m]+=currentVec[m]; 					
 				}
 			}
 			for(int l=0; l<vSize; l++){
 				newCentroid[i][l]/=documents.size(); // new centroids
 			}
 		}
 		return newCentroid;
 	} 

 	/**
	*@param	similarity The similarity matrix for each clusternto the documents
	* Assigns the documents to the clusters
 	*/
 	public void assignDocsToCluster(double[][] similarity){
 		//Initialize clusters
 		for(int i=0; i<numClusters; i++){
 			clusterDocs[i]= new HashMap<Integer, double[]>(); 
 		}
 		//Based on the minimum distance assign data points to cluster 		
 		for(int i=0; i<docList.length; i++){ 			
			if(similarity[0][docList[i].docId] >= similarity[1][docList[i].docId]){
				clusterDocs[0].put(docList[i].docId, docList[i].termVec);
			}else if(similarity[0][docList[i].docId] < similarity[1][docList[i].docId]){
				clusterDocs[1].put(docList[i].docId, docList[i].termVec);
			}
 		}
 		printClusters();  		
 	}
 	/*
 	* Prints the centroid points
 	*/ 	
 	public void printCentroids(){
 		System.out.println("The centroids are "); 		
 		for(int i=0; i<numClusters; i++){
 			double[] vector = centroids[i].termVec;
 			System.out.print("[");
 			for(int j=0; j<vector.length; j++){
 				System.out.print(" "+vector[j]+",");
 			}
 			System.out.println("]");
 		}
 	}

 	/*
 	* Prints the documents in each cluster
 	*/
 	public void printClusters(){
 		for(int i=0; i< numClusters; i++){ 			
 			HashMap<Integer, double[]> docMap = clusterDocs[i];
 			System.out.print("\nNo of docs in cluster "+ i + " is "+docMap.size());
 			System.out.println("\nDocuments are");
 			for(int dID: docMap.keySet()){
 				System.out.println(" "+dID+ ": "+myDocs[dID]);
 			} 			
  		}
  		System.out.println("\n*******************************\n");
 	}

	//Hierarchical Clustering : Single Linkage
 	public void singleLinkageCluster(){
 		System.out.println("*********************SINGLE LINKAGE*********************");
 		ArrayList<ArrayList<Integer>> resultClusters = new ArrayList<ArrayList<Integer>>();
 		//Assigning initial cluster results 		
		for(int i=0; i<docList.length; i++){
			ArrayList<Integer> tempList = new ArrayList<Integer>();
			tempList.add(docList[i].docId);
			resultClusters.add(tempList);
		}
		printResultClusters(resultClusters); 		
 		computeSimilarityMatrix();
 		 do{
 			ArrayList<Integer> newClusters = findMaxSimilarityClusters();
 		 	ArrayList<ArrayList<Double>> computedMatrix =recomputeSimilarityMatrix(newClusters, resultClusters);
 			similarityMatrix = computedMatrix; 
 			printSimilarityMatrix();			
 		 }while(resultClusters.size()>1);
 	}

 	//computing initial similarity matrix 
 	public void computeSimilarityMatrix(){
 		similarityMatrix = new ArrayList<ArrayList<Double>>();
 		for(int i=0;i<docList.length; i++){
 			ArrayList<Double> docSimilarity = new ArrayList<Double>();
 			for(int j=0; j<=i; j++){
 				if(i==j){
 					docSimilarity.add(j, 1.0);
 				}else{
 					double[] termVec1 = docList[i].termVec;
 					double[] termVec2 = docList[j].termVec;
 					double cosSim=0.0;
 					for(int k=0; k<termVec1.length; k++){
 						cosSim+=termVec1[k]*termVec2[k];
 					}
 					docSimilarity.add(j, cosSim);
 				}
 			}
 			similarityMatrix.add(i, docSimilarity);
 		}
 		printSimilarityMatrix(); 		
 	}

 	//finding the maximum similarity clusters
 	public ArrayList<Integer> findMaxSimilarityClusters(){
 		double max=0;
 		HashMap<Integer, ArrayList<Integer>> maxIndices = new HashMap<Integer, ArrayList<Integer>>();
 		for(int i=0; i<similarityMatrix.size(); i++){
 			for(int j=0; j<similarityMatrix.get(i).size(); j++){
 				if(i!=j){
 					if(similarityMatrix.get(i).get(j) > max){
 						ArrayList<Integer> currentIndices; 
 						max = similarityMatrix.get(i).get(j); 						
						maxIndices= new HashMap<Integer, ArrayList<Integer>>();
						currentIndices = new ArrayList<Integer>();
						currentIndices.add(i); 				
 						maxIndices.put(j, currentIndices);
 					}else if((similarityMatrix.get(i).get(j)==max) && maxIndices.containsKey(j)){ 						
 						ArrayList<Integer> tempIndices = maxIndices.get(j);
 						tempIndices.add(i);
 						maxIndices.put(j, tempIndices);
 					}
 				}
 			}	
 		}
 		ArrayList<Integer> newClusters = new ArrayList<Integer>();
 		if(maxIndices.size()==1){
 			ArrayList<Integer> rowIndices=null;
 			for(int key: maxIndices.keySet()){
 				newClusters.add(key);
				rowIndices = maxIndices.get(key);
 			}
 			for(int index: rowIndices){
 				newClusters.add(index);
 			}
 		}
 		Collections.sort(newClusters);
 		return newClusters;
 	}

 	//Recompute the similarity matrix based on the new cluster formation
 	public ArrayList<ArrayList<Double>> recomputeSimilarityMatrix(ArrayList<Integer> clusters, ArrayList<ArrayList<Integer>> resultClusters){
 		int counter=0;
 		//index of element changes as we remove from the list 	
 		ArrayList<ArrayList<Integer>> tempClusters = new ArrayList<ArrayList<Integer>>();	
 		ArrayList<Integer> mergedCluster = new ArrayList<Integer>();
 		for(int index: clusters){
 			mergedCluster.addAll(resultClusters.remove(index-counter));
 			counter++;
 		}
 		resultClusters.add(0, mergedCluster);
 		printResultClusters(resultClusters);
 		tempClusters.add(0, clusters);
 		for(int i=0; i<similarityMatrix.size(); i++){
 			if(!clusters.contains(i)){
 				ArrayList<Integer> dummy = new ArrayList<Integer>();
 				dummy.add(i);
 				tempClusters.add(dummy);
 			}
 		}
 		ArrayList<ArrayList<Double>> computedMatrix = new ArrayList<ArrayList<Double>>();
 		//Update the similarity matrix
 		for(int i=0; i<tempClusters.size(); i++){
 			ArrayList<Double> temp = new ArrayList<Double>();
 			int rowIndex=-1;
 			if(tempClusters.get(i).size()==1){
 				rowIndex=tempClusters.get(i).get(0);
 			}else{
 				rowIndex=i;
 			}
 			for(int j=0; j<=i; j++){
 				int rowIdx = rowIndex;
 				if(i==j){
 					temp.add(j, 1.0);
 				}else if(tempClusters.get(j).size() > 1){
 					ArrayList<Integer> clusterIndices = tempClusters.get(j); 					
 					double maxSim =0;
 					for(int index: clusterIndices){
 						rowIdx = rowIndex;
 						if(index > rowIdx){
 							int temp1 = index;
 							index = rowIdx;
 							rowIdx = temp1;
 						} 
 						if(similarityMatrix.get(rowIdx).get(index) > maxSim){
 							maxSim = similarityMatrix.get(rowIdx).get(index);
 						}
 					} 					
 					temp.add(j, maxSim);	
 				}else{ 					
 					int colIdx = tempClusters.get(j).get(0);
 					if(colIdx > rowIdx){
 						int temp1=rowIdx;
 						rowIdx = colIdx;
 						colIdx = temp1;
 					}
 					temp.add(j, similarityMatrix.get(rowIdx).get(colIdx));
 				}
 			}
 			computedMatrix.add(i, temp);
 		}
 		//System.out.println("The newly computed matrix is "+computedMatrix);
 		return computedMatrix; 		
 	}

 	//prints the initial similarity matrix
 	public void printSimilarityMatrix(){
 		System.out.println("\nThe similarity matrix \n");
 		for(int i=0; i<similarityMatrix.size(); i++){
 			System.out.print("[");
 			for(int j=0; j<similarityMatrix.get(i).size(); j++){
 				System.out.print(similarityMatrix.get(i).get(j)+", ");
 			}
 			System.out.print("]\n");
 		}
 		System.out.println("\n#####################################");
 	}

 	//prints the clusters for every iteration
 	public void printResultClusters(ArrayList<ArrayList<Integer>> resultClusters){
 		System.out.println("\nThe clusters are ");
 		for(int i=0; i<resultClusters.size(); i++){
 			System.out.print("\n[");
 			for(int j=0; j<resultClusters.get(i).size(); j++){
 				System.out.print(resultClusters.get(i).get(j)+ ": "+myDocs[resultClusters.get(i).get(j)]+ ", "); 				
 			}
 			System.out.println("]\n");
 		}
 	}

 
	public static void main(String[] args){
		String[] docs = {"hot chocolate cocoa beans",
				 "cocoa ghana africa",
				 "beans harvest ghana",
				 "cocoa butter",
				 "butter truffles",
				 "sweet chocolate can",
				 "brazil sweet sugar can",
				 "suger can brazil",
				 "sweet cake icing",
				 "cake black forest"
				};
		Clustering c = new Clustering(2);
		c.preprocess(docs);
		System.out.println("Vector space representation:");
		for(int i=0;i<c.docList.length;i++){
			System.out.println(c.docList[i]);
		}
		c.cluster(); //k-means implementation
		c.singleLinkageCluster(); //HAC - Single Linkage
	}
}

/**
 * 
 * Document id class that contains the document id and the term weight in tf-idf
 */
class Doc{
	int docId;
	ArrayList<Integer> termIds;
	ArrayList<Double> termWeights;
	double[] termVec;
	public Doc(){
		
	}
	public Doc(int id){
		docId = id;
		termIds = new ArrayList<Integer>();
		termWeights = new ArrayList<Double>();
	}
	public void setTermVec(double[] vec){
		termVec = vec;
	}
   
	public String toString()
	{
		String docString = "[";
		for(int i=0;i<termVec.length;i++){
			docString += termVec[i] + ",";
		}
		return docString+"]";
	}
	
}