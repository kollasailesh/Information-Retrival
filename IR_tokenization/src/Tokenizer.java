import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
/*
Name: Sailesh KOlla
Netid: sxk145331

CS6322.501: Information Retrieval
HomeWork 1

This is the Tokenizer class java file

*/
public class Tokenizer {
	
	static public TreeMap<String, Integer> tokenCollection = new TreeMap<>();
	static public TreeMap<String, Integer> stemCollection = new TreeMap<>();
	public static void main(String[] args) {
		//final File folder = new File("C:/Users/SAILESH/OneDrive/Studies/4-Fall 2015/IR/Cranfield");
		final File folder = new File("/people/cs/s/sanda/cs6322/Cranfield");
		try {
			long startParseTime = System.currentTimeMillis();
			int docsNO = listFilesForFolder(folder);
			printCharacteristics("tokens",docsNO);
			long endParseTime = System.currentTimeMillis();
			long elapsedTime = (long) (endParseTime - startParseTime);
			System.out.println("Time taken to find token details:"+"\t"+elapsedTime+" ms");
			
			stemming(tokenCollection);
			System.out.println("");
			System.out.println("_______________________________________________________________");
			printCharacteristics("stems",docsNO);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	
	//stemmer method call
	private static void stemming(TreeMap<String, Integer> tokens) {
		Stemmer stm = new Stemmer();
		
		for ( Entry<String, Integer> token : tokens.entrySet()){
			char [] temp = token.getKey().toCharArray();
			for(int j=0; j <temp.length; j++){
			stm.add(temp[j]);
			}
			stm.stem();
			String stemResult = stm.toString();
			if (stemCollection.containsKey(stemResult)) {
				int count = stemCollection.get(stemResult);
				count++;
				stemCollection.put(stemResult, count);
			} else {
				stemCollection.put(stemResult, 1);
			}
		}
		
	}


	// Method to print the text characteristics
	private static void printCharacteristics(String word, int totalDocs){
		if(word == "tokens"){
		System.out.println(word+" Characteristics:");
		System.out.println("1: Number of "+word+" in the collection is"+"\t"+ collectionCount(tokenCollection));
		System.out.println("2: Number of unique "+word+" in the collection is"+ "\t" + tokenCollection.size());
		System.out.println("3: Number of "+word+" with occuring only once in the collection is"+ "\t" + countSingularOccurance(tokenCollection));
		System.out.println("No of documents"+"\t"+totalDocs);
		System.out.println("5: The average number of word tokens per document"+"\t"+collectionCount(tokenCollection)/totalDocs);
		System.out.println("4: The top 30 frequently occuring "+word+" :"+"\t");
		System.out.println("______________________________________________________");
		printSortByValues(tokenCollection);
		System.out.println("______________________________________________________");
		
		}
		
		
		if(word == "stems"){System.out.println(word+" Characteristics:");
		System.out.println("1: Number of "+word+" in the collection is"+"\t"+ collectionCount(stemCollection));
		System.out.println("2: Number of unique "+word+" in the collection is"+ "\t" + stemCollection.size());
		System.out.println("3: Number of "+word+" with occuring only once in the collection is"+ "\t" + countSingularOccurance(stemCollection));
		System.out.println("No of documents"+"\t"+totalDocs);
		System.out.println("5: The average number of word tokens per document"+"\t"+collectionCount(stemCollection)/totalDocs);
		System.out.println("4: The top 30 frequently occuring "+word+" :"+"\t");
		System.out.println("______________________________________________________");
		printSortByValues(stemCollection);
		System.out.println("______________________________________________________");
		
		}			
	}

	private static int countSingularOccurance(TreeMap<String, Integer> collection) {
		int countSingular = 0;
		for(Entry<String, Integer> key : collection.entrySet()){
			if(key.getValue() == 1)
				countSingular++;
		}
		return countSingular;
	}





	private static int collectionCount(TreeMap<String, Integer> collection) {
		//method to find total number of tokens
		int count = 0;
		for(Entry<String, Integer> key : collection.entrySet()){
			int i = key.getValue();
			count = count+i;
		}
		return count;
	}





	public static int listFilesForFolder(final File folder) throws FileNotFoundException {
		// for listing out files in a folder
		int docsNO = 0 ;
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				// if the fileEtry is a directory - make recursive call
				docsNO += listFilesForFolder(fileEntry);
			} else {
				try {
					// if the fileEntry is not a directory then reading the file
					// line by line.
					readFile(fileEntry);
					docsNO ++;
				} catch (IOException e) {
					// Handling any exceptions
					e.printStackTrace();
				}
			}
		}
		return docsNO;
	}

	private static void readFile(File fileEntry) throws IOException {
		// reading file line by line
		if (fileEntry != null) {
			try (BufferedReader br = new BufferedReader(new FileReader(fileEntry))) {
				for (String line; (line = br.readLine()) != null;) {
					// System.out.println(line+" "+line.length());
					String noHtmlLine = line.toString().replaceAll("\\<.*?>", "");
					// System.out.println(noHtmlLine+" "+noHtmlLine.length());
					if (noHtmlLine.length() < 1) {
						continue;
					}
					// replace 's with null 
					noHtmlLine = noHtmlLine.replaceAll("\\'s", "");

					if (noHtmlLine != null) {
						//splitting the words separated by whitespace.
						String[] words = noHtmlLine.split(" ");
						for (String word : words) {
							ArrayList<String> processedToken = new ArrayList<String>();
							processedToken = processWord(word);
							for (String token : processedToken) {
								//Storing the tokens,count in a tree map 
								if(token.length()<1)
									continue;
								if (tokenCollection.containsKey(token)) {
									int count = tokenCollection.get(token);
									count++;
									tokenCollection.put(token, count);
								} else {
									tokenCollection.put(token, 1);
								}
							}
						}
					}
				}
				br.close();
			}
		}
	}

	private static ArrayList<String> processWord(String word) {
		// To generate a processed token free of punctuation
		ArrayList<String> tokens = new ArrayList<String>();
		word = word.toLowerCase();
		// Return null if the word is null
		if (word == null)
			return null;

		// removing the "," at the end of the token
		if (word.endsWith(",")) {
			word = word.replaceAll("[\\,]", "").trim();
		}
		// removing the "." if there is only one.
		String xyz = word;
		if (xyz.length() - xyz.replace(".", "").length() == 1) {
			word = word.replaceAll("\\.", "").trim();
		}
		// removing the "," present inside the token and splitting the token
		if (word.contains(",")) {
			word = word.replaceAll(",", " ").trim();
		}
		// handling cases like cases..
		if (xyz.length() - xyz.replace(".", "").length() > 1)
			word = word.replaceAll("\\.\\.", "").trim();
		// If more than one "." in a token it indicates that it is an
		// abbreviation
		// So I am capitalizing the token and keeping it as is
		xyz = word;
		if (xyz.length() - xyz.replace(".", "").length() > 1)
			word = word.toUpperCase();
		// considering the terms separated by "-" as a single term.
		if (word.contains("-")) {
			word = word.replaceAll("\\-", "");
		}
		//checking if the token is not null and adding
		if (word != "" || word != " " || word != null) {
			//If the word contains any spaces then splitting the word and treating 
			//it as different tokens
			xyz = word;
			if (xyz.length() - xyz.replaceAll("\\s", "").length() >= 1) {
				String[] splits = word.split(" ");
				for (String split : splits) {
					tokens.addAll(processWord(split));
				}
			} else {
				word = word.replaceAll("[\\d+]", "");	
				word = word.replaceAll("[\\W+&&[^\\.]]", "").trim();
				tokens.add(word);
			}
		}
		return tokens;
	}



	// method to print tree map by sorting with keys and values
	public static void printSortByKeys() {
		System.out.println("Tree sort using keys");
		for (Map.Entry<String, Integer> entry : tokenCollection.entrySet()) {
			String key = entry.getKey();
			int value = entry.getValue();
			System.out.println(key +" "+ value);
		}
	}
	
	//Method to print 30 most frequently repeated words. 
	public static void printSortByValues(TreeMap<String, Integer> collection){
		TreeMap<String, Integer> sortedTree = new TreeMap<String, Integer>();
		sortedTree = (TreeMap<String, Integer>) sortByValues(collection);
		System.out.println("Tree sorted by values");
		int top30 = 1;
		System.out.println("SNo"+"\t"+"word"+ "\t " +"Frequency");
		for (Map.Entry<String, Integer> entry : sortedTree.entrySet()) {
			if(top30 > 30)return;
			String key = entry.getKey();
			int value = entry.getValue();
			System.out.println(top30+"\t"+key + "\t " + value);
			top30++;
		}
	}
	
	// over writing comparator to sort tree map by values
	public static <K, V extends Comparable<V>> Map<K, V> sortByValues(final Map<K, V> map) {
		Comparator<K> valueComparator = new Comparator<K>() {
			public int compare(K k1, K k2) {
				int compare = map.get(k2).compareTo(map.get(k1));
				if (compare == 0)
					return 1;
				else
					return compare;
			}
		};
		Map<K, V> sortedByValues = new TreeMap<K, V>(valueComparator);
		sortedByValues.putAll(map);
		return sortedByValues;
	}

}
