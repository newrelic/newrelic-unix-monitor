package com.newrelic.infra.unix;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class ScriptDriver {

	public static void main(String[] args) throws IOException {

		// Initializations

		String sTempOutFile="/tmp/script_status.out";
		String sPostFixContents=" time=`date -u` >>"+ sTempOutFile;
		String sScriptFolderLocation = new java.io.File(".").getCanonicalPath()+"/scripts";
		String sDriverPath=new java.io.File(".").getCanonicalPath()+"/script_driver.sh";
		FileWriter fWriter = null;
		File f = null;
		// Creates an array in which we will store the names of files and directories
		String[] pathnames = null;

		try {
			fWriter= new FileWriter(sDriverPath);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 

		// declare a variable that will store the user input
		String userInput;

		//Declare a scanner object to read the command line input by user
		Scanner sn = new Scanner(System.in);

		//loop the utility in loop until the user makes the choice to exit
		while(true){
			//Print the options for the user to choose from
			System.out.println("\n*****ScriptDriver Options*****\n");
			System.out.println("*. Press 1 to update the scripts' folder location: ["+sScriptFolderLocation+"]");
			System.out.println("*. Press 2 to continue");
			System.out.println("*. Press 3 to exit");
			// Prompt the use to make a choice
			System.out.print("\nEnter your choice: ");
			//Capture the user input in scanner object and store it in a pre decalred variable
			userInput = sn.next();
			f = new File(sScriptFolderLocation);
			//Check the user input
			switch(userInput){
			case "1":
				//do the job number 1
				System.out.print("Enter scripts' location: ");

				//Capture the user input in scanner object and store it in a pre decalred variable
				userInput = sn.next();
				sScriptFolderLocation=userInput;
				// Populates the array with names of files 
				f = new File(sScriptFolderLocation);
				if (f.exists())
					System.out.println("Info: scripts' folder location: ["+sScriptFolderLocation+"] exist ");
				// Creates a new File instance by converting the given pathname string
				else
					System.out.println("Error: scripts' folder location: ["+sScriptFolderLocation+"] doesn't exist ");

				break;

			case "2":

				// Populates the array with names of files and directories
				if (f !=null && f.exists())
					pathnames = f.list();

				// into an abstract pathname
				if (pathnames!=null  && pathnames.length==0)
				{
					System.out.println("Error: scripts' folder location: ["+sScriptFolderLocation+"] doesn't have any scripts. ");
					break;
				}

				// For each pathname in the pathnames array
				for (String pathname : pathnames) {

					// skip sub directories
					String sCommand=sScriptFolderLocation+"/"+pathname;
					File file = new File(sCommand);
					boolean bExists=file.isFile();
					if (bExists) {

						try {
							sCommand= sCommand+" ; echo  status=$? command="+pathname+sPostFixContents+"\n";
							// Writing into file
							// Note: The content taken above inside the
							// string
							fWriter.write(sCommand);

							// Printing the contents of a file
							System.out.println(sCommand); 

						}

						// Catch block to handle if exception occurs
						catch (IOException e) {

							// Print the exception
							//System.out.print(e.getMessage());
							System.out.println(sScriptFolderLocation + " doesn't exist");
						}
						catch (NullPointerException e) {

							// Print the exception
							//System.out.print(e.getMessage());
							System.out.println(sScriptFolderLocation + " doesn't exist");
						}
					}// closing if
				}
				// Closing the file writing connection
				try {
					fWriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("\nInfo: Driver file created. Please create a cron job as per your required frequency \n");
				System.out.println("Here is an example to run every minute \n");
				System.out.println("* * * * * "+sDriverPath+" >/dev/null 2>&1 \n");
				System.out.println("You may exit !!  \n");
				break;
			case "3":
				//exit from the program
				System.out.println("Exiting...");
				System.exit(0);
			default:
				//inform user in case of invalid choice.
				System.out.println("Invalid choice. Read the options carefully...");
			}
		}



	}

}
