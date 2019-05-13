import java.io.*;
import java.util.*;

public class NBClassifier{

	private int numClasses;
	private int[] classDocCounts;
	private File[] classFiles;
	private int[] classTokenCount;
	private HashMap<String, Double>[] termCondProb;
	private double[] priorProbability;
	private HashSet<String> vocabulary;
	private ArrayList<ArrayList<Integer>> predictions;
	
	/**
	 * Build a Naive Bayes classifier using a training document set
	 * @param trainDataFolder the training document folder
	 */
	public NBClassifier(File trainDataFolder)throws IOException{	
		vocabulary = new HashSet<String>();
		File[] files = trainDataFolder.listFiles();
		//finding number of classes
		classFiles = new File[files.length];				
		for(int i=0, classCounter=0; i<files.length; i++){
			if(files[i].isDirectory()){				
				numClasses++;				
				classFiles[classCounter] = files[i];
				classCounter++;							
			}
		}
		termCondProb = new HashMap[numClasses];
		classDocCounts = new int[numClasses];
		classTokenCount = new int[numClasses];
		priorProbability = new double[numClasses];
		int totalTrainDocs=0;		
		for(int i=0; i<numClasses; i++){						
			classDocCounts[i]= classFiles[i].listFiles().length;
			termCondProb[i]= new HashMap<String,Double>();
			totalTrainDocs+=classDocCounts[i];
			ArrayList<String> docTokens = preprocess(classFiles[i], i);
			classTokenCount[i] = docTokens.size();						
		}
		//update conditional probability and compute Prior Probability	
		int vocabSize = vocabulary.size();	
		for(int i=0; i<numClasses;i++){
			for(String token : termCondProb[i].keySet()){
				double frequency = termCondProb[i].get(token);
				double probability = (frequency+1)/(classTokenCount[i]+vocabSize);
				termCondProb[i].put(token, probability);
			}
			priorProbability[i]=(double)classDocCounts[i]/totalTrainDocs;
		}
	}

	 /**     
      *Tokenization on the file's contents
	  *@param fileName the file name to tokenize
	  *@return list of pure tokens
      */     
   	public ArrayList<String> tokenize(File fileName)throws IOException{
   		String[] tokens = null;
   		ArrayList<String> pureTokens = new ArrayList<String>();
   		Scanner sc = new Scanner(fileName);
   		String allLines = new String();
   		while(sc.hasNextLine()){
   			allLines += sc.nextLine();			
   		}
   		tokens = allLines.split("[\" ()_,?:;%&-]+");         
   		for(String token: tokens){   			
               pureTokens.add(token);   			
   		}
         return pureTokens;
   	}
	
	/**
	 * Classify a test doc
	 * @param docTerms List of terms in a test document
	 * @return predicted class label
	 */
	public int classify(ArrayList<String> docTerms){
		double[] actualProb = new double[numClasses];
		for(String term : docTerms){	
			for(int i=0; i<numClasses; i++){
				if(termCondProb[i].containsKey(term)){
					double currentTermProb = termCondProb[i].get(term);
					actualProb[i]+=Math.log10(currentTermProb);
				}else{
					actualProb[i]+=Math.log10((double)1/(classTokenCount[i]+vocabulary.size()));
				}
			}
		}
		for(int i=0;i<numClasses;i++){
			actualProb[i]+=Math.log10(priorProbability[i]);
		}
		double maxProb = actualProb[0];int label=0;
		for(int i=1; i<numClasses; i++){
			if(actualProb[i]>maxProb){
				maxProb=actualProb[i];
				label=i;
			}
		}
		return label;
	}

	/**
	 * Load the training documents
	 * @param trainDataFolder
	 */
	public ArrayList<String> preprocess(File trainDataFolder, int classNum)throws IOException{	
		File[] fileList = trainDataFolder.listFiles();
		ArrayList<String> classTokens = new ArrayList<String>();
		for(int i=0; i<fileList.length; i++){			
			ArrayList<String> pureTokens = tokenize(fileList[i]);
			classTokens.addAll(pureTokens);
			for(String token: pureTokens){
				vocabulary.add(token);
				if(termCondProb[classNum].containsKey(token)){
					double currentCount = termCondProb[classNum].get(token);
					termCondProb[classNum].put(token, currentCount+1);	
				}else{
					termCondProb[classNum].put(token, 1.0);
				}
			}
		}
		return classTokens;
	}
	
	/**
	 *  Classify a set of testing documents and report the accuracy
	 * @param testDataFolder fold that contains the testing documents
	 * @return classification accuracy
	 */
	public double classifyAll(File testDataFolder)throws IOException{
		predictions = new ArrayList<ArrayList<Integer>>();
		File files[] = testDataFolder.listFiles();
		for(int i=0, classCounter=0; i<files.length; i++){
			if(files[i].isDirectory()){
				//preprocess the test documents
				ArrayList<Integer> classPredictions = new ArrayList<Integer>();				
				File[] docs = files[i].listFiles();
				for(int j=0; j<docs.length; j++){					
					ArrayList<String> pureTokens = tokenize(docs[j]);
					classPredictions.add(classify(pureTokens));	
				}
				predictions.add(classPredictions);				
				classCounter++;
			}
		}
		//Compute Accuracy
		int trueValues = 0, totalTestDocs =0;
		int truePositive=0, trueNegative=0, falsePositive=0, falseNegative=0 ;
		for(int i=0; i<predictions.size(); i++){
			ArrayList<Integer> classPred = predictions.get(i);
			totalTestDocs+=classPred.size();
			for(int predictedLabel : classPred){
				if(predictedLabel==i){	
					trueValues++;				
					if(i==0){
						trueNegative++;
					}else{
						truePositive++;
					}
				}else{
					if(predictedLabel==0){
						falseNegative++;
					}else{
						falsePositive++;
					}
				}
			}
		}
		double accuracy= (double)(trueNegative+truePositive)/totalTestDocs;		 	
		System.out.println("{ True {Positive : "+truePositive+", Negative :"+trueNegative+"} } {False {Positive: "+falsePositive+ ", Negative:"+falseNegative+"} }");
		System.out.println("Correctly classified "+ trueValues + " out of "+totalTestDocs);		
		return accuracy;
	}
	
	
	public static void main(String[] args)throws IOException{		
		File trainData = new File("data/train");
		NBClassifier nb = new NBClassifier(trainData);
		File testData = new File("data/test");
		System.out.println("Classification Accuracy "+nb.classifyAll(testData));
	}
}


