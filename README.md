# cs1511-prolog

This project was for my CS1511 Theory of Computation class taken in the Spring of 2020 where we were given a choice of projects to deeply explore a topic we talked about in the class.
There are a lot of improvements that could be made including stuff that most prolog systems typically implement that I was not able to.
You can read the report I wrote for the project to get an idea of how I went about the implementation, stuff I wasn't able to implement, and an explanation of some examples I created.
For the project, I made 2 examples but one was a family tree and since I used my own family I excluded that example for privacy reasons and any names I mention in the report have been changed.

## Usage:

* You can pass in the filenames of any prolog programs you want to load into the database as command line arguments
* You can also load in prolog programs using the ```load(X).``` command in the repl where X is the name of the file (without the pl file extension)
* You can write to the output stream using ```write(X).```
* You can output a newline to the output stream using ```nl.```
* You can close the program using ```exit.```

### If you are running Mac / Linux:
  
#### Compile:
    
```./compile```
  
#### Run:
    
```./jlog <pl file> ...```

### If you are running Windows:
  
#### Compile:
    
```javac java/*.java```

#### Run:
    
```java -cp java Main <pl file> ...```
