Extract the given file(CS2410-Project2.zip) and from there extract the src.zip file to get all the source codes(.java files).

Class files are present in bin.zip .

The main zip file(CS2410-Project2.zip) includes:
	A. Project Report which contains a brief overview of design, implementation, benchmark and conclusion.
	B. src.zip which contains all the java source files.
	C. bin.zip which contains all the binary files.
	D. README.txt, a general info on what is submitted and how to run the project.
	E. configuration.txt, the configuration file which intialises the parameters of the project.
	F. trace1 and trace2 files as benchmark trace files for the project which are used for statistical analysis. 
	
Compiling Steps: javac Start.java

Running Steps:
	1.With Normal mode: just display the number of cycles each core takes, L1 miss rate and penalty and L2 miss rate
		java Start <configuration filename> <trace filename>
	2.With Debug mode: In addition to above we print the read trace file, status exchanges and at the end of execution we print the L1 and L2 cache block with status, tag and core list associated with each block directory.
		java Start <configuration filename> <trace filename> debug

		
