### Implementation of Inverted Index Model.
- Performs parsing of the text documents. Parsing steps include tokenization, Removal of Stop Words and Stemming (Porter's  Stemming algorithm)
- Query Processing module for performing specific Boolean operations such as AND, OR are implemented.
- Optimized solution for handling n number of queries.

### Steps to run:
- Compile and run the Parser java file
```
javac Parser.java
java Parser
```
- Displays the inverted index matrix constructed using the given files under Lab1_Data directory as input files.
- Specify the queries and boolean operations to be performed. (Query processing module)
- Displays the documents where the queries were present.
