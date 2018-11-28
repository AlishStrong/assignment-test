import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import java.util.stream.Collectors;


public class Application {
	
	/*! IMPORTANT !*/
//	If you run the application in IDE !!! 
//	Uncomment lines 39-41, and comment lines 45-47 in Applications.java!
//	In WorkFile.java comment line 44, uncomment line 41!
//	Input directory is at: assignment-test/src/main/resources/input_files
//	Output directory is at: assignment-test/src/main/resources/output_files
//	
//	If you want to run the application from console !!!
//	Uncomment lines 45-47, and comment lines 39-41 in Applications.java!
//	In WorkFile.java comment line 41, uncomment line 44!
//	Open project folder (assignment-test) from console
//	execute the following maven commands:
//		mvn clean
//		mvn compile package
//	After that, navigate to generated target folder and execute this command:
//		java -jar assignment-test-made-by-Alisher-Aliev-1.jar
//	Input directory is at: assignment-test/target/classes/input_files
//	Output directory is at: assignment-test/target/classes/output_files
//	
//	If you want to test the application with other .csv files,
//	put the new files in according input_files directory!
	/*--------------------------------------------------*/

	//For work in IDE
//	private static final String inputFilesPath = "src\\main\\resources\\input_files";
//	private static final String outputFilesPath = "src\\main\\resources\\output_files";
//	private static final String aboutFilePath = "src\\main\\java\\about.txt";
	
	
	//For work through console after mvn compile package
	private static final String inputFilesPath = System.getProperty("user.dir") + "\\classes\\input_files";
	private static final String outputFilesPath = System.getProperty("user.dir") + "\\classes\\output_files";
	private static final String aboutFilePath = System.getProperty("user.dir") + "\\classes\\about.txt";

	private static boolean quit = false;
	private static Scanner input = new Scanner(System.in);
	private static List<String> inputFiles = new ArrayList<>();
	private static List<String> outputFiles = new ArrayList<>();
	private static WorkFIle workFile;
	
	public static void main(String[] args) throws Exception {

		inputFiles = getFileNames(inputFilesPath).stream()
				.filter(s -> s.substring(s.length()-4, s.length()).equals(".csv"))
				.collect(Collectors.toList());
		outputFiles = getFileNames(outputFilesPath).stream()
				.filter(s -> s.substring(s.length()-4, s.length()).equals(".csv"))
				.collect(Collectors.toList());

		Stack<String> sessionProgress = new Stack<>();

		System.out.println("  Welcome!" + "\n  How may I help you?_");
		sessionProgress.push("mm");

		while (!quit) {
			determineListener(sessionProgress);
		}

	}

	private static void actionsMenuListener(Stack<String> status) {
		String response = input.nextLine().trim().toLowerCase();
		String message = "There are these files in the ";
		switch (response) {
		case "1":
			System.out.println("Starting a new task");
			status.push("st");
			determineListener(status);
			break;
		case "2":
			message += "input_files directory";
			showFiles(inputFiles, message);
			showBackMenu();
			status.push("iof");
			determineListener(status);
			break;
		case "3":
			message += "output_files directory";
			showFiles(outputFiles, message);
			showBackMenu();
			status.push("iof");
			determineListener(status);
			break;
		case "g":
			goBack(status);
			break;
		case "q":
			terminateTheApp();
			break;
		default:
			System.out.println("I do not understand you!" + "\nEither use letters, specified in brackets,"
					+ "\nor digits within the right range!");
			System.out.println("");
			determineListener(status);
			break;
		}
	}

	private static void backListener(Stack<String> status) {
		String response = input.nextLine().trim().toLowerCase();
		switch (response) {
		case "g":
			goBack(status);
			break;
		case "q":
			terminateTheApp();
			break;
		default:
			System.out.println("I do not understand you!" + "\nPlease use the letters specified in brackets,");
			System.out.println("");
			showBackMenu();
			backListener(status);
			break;
		}
	}

	private static void determineListener(Stack<String> status) {
		String sessionStatus = status.peek();
		switch (sessionStatus) {
		case "mm":
			showMainMenu();
			mainMenuListener(status);
			break;
		case "am":
			showActionsMenu();
			actionsMenuListener(status);
			break;
		case "st":
			newTaskMenuListener(status);
			break;
		case "iof":
			backListener(status);
			break;
		case "wf":
			showWorkFile();
			workFileListener(status);
			break;
		}
	}

	private static List<String> getFileNames(String path) {
		List<String> directoryFiles = new ArrayList<>();
		File[] files = new File(path).listFiles();
		if (files.length > 0) {
			for (File file : files) {
				if (file.isFile()) {
					directoryFiles.add(file.getName());
				}
			}
		}
		return directoryFiles;
	}

	private static void goBack(Stack<String> status) {
		status.pop();
		determineListener(status);
	}

	private static void mainMenuListener(Stack<String> status) {
		String response = input.nextLine().trim().toLowerCase();
		switch (response) {
		case "m":
			status.push("am");
			determineListener(status);
			break;
		case "h":
			System.out.println("Showing help info");
			try {
				FileReader fr = new FileReader(new File(aboutFilePath).getAbsolutePath());
				BufferedReader br = new BufferedReader(fr);
				String line = br.readLine();
				while (line != null) {
					System.out.println(line);
					line = br.readLine();
				}
				fr.close();
				br.close();
			} catch (FileNotFoundException e) {
				System.out.println("Could not find about.txt file");
			} catch (IOException e) {
				e.printStackTrace();
			}
			determineListener(status);
			break;
		case "q":
			terminateTheApp();
			break;
		default:
			System.out.println("I do not understand you!" + "\nPlease use the letters specified in brackets,");
			System.out.println("");
			determineListener(status);
			break;
		}
	}

	private static void newTaskMenuListener(Stack<String> status) {
		String message = "Please, select the file to work with:";
		showFiles(inputFiles, message);
		showBackMenu();
		String response = input.nextLine().trim().toLowerCase();
		if (response.equals("g")) {
			goBack(status);
		} else if (response.equals("q")) {
			terminateTheApp();
		} else {
			try {
				int selection = Integer.parseInt(response) - 1;
				workFile = new WorkFIle(inputFiles.get(selection), outputFilesPath);
				status.push("wf");
				determineListener(status);
			} catch (Exception e) {
				System.out.println("Please, make the correct input!" + "\nEither use letters, specified in brackets,"
						+ "\nor digits within the right range!");
				System.out.println("");
				determineListener(status);
			}
		}
		// quit = true;
	}

	private static void showActionsMenu() {
		System.out.println("1) Press 1 to start a new task");
		System.out.println("2) Press 2 to browse input files");
		System.out.println("3) Press 3 to browse output files");
		showBackMenu();
	}

	private static void showBackMenu() {
		System.out.println("*-----------------------------*");
		System.out.println("|(G)o back      |      (Q)uit |");
		System.out.println("|-----------------------------|");
	}

	private static void showFiles(List<String> directory, String message) {
		if (directory.size() > 0) {
			System.out.println(message);
			for (int i = 1; i <= directory.size(); i++) {
				System.out.println(i + ") " + directory.get(i - 1));
			}
		} else {
			System.out.println("Sorry, there are no .csv files in the current directory");
			System.out.println("");
		}
	}

	private static void showMainMenu() {
		System.out.println("*-------------------------*");
		System.out.println("|(M)enu | (H)elp | (Q)uit |");
		System.out.println("|-------------------------|");
	}

	private static void showWorkFile() {
		System.out.println("Working with \n# " + workFile.getPath());
		System.out.println("Current window size is " + workFile.getWindowSize());
		System.out.println("Options:");
		System.out.println("- Show (w)indows");
		System.out.println("- Show (m)edians");
		System.out.println("- (C)reate an output file with median results");
		System.out.println("- Change windows (s)ize");
		showBackMenu();
	}

	private static void terminateTheApp() {
		System.out.println("Quiting the app! \nBye!");
		quit = true;
	}

	private static void workFileListener(Stack<String> status) {
		String response = input.nextLine().trim().toLowerCase();
		switch (response) {
		case "w":
			workFile.showWindows();
			showBackMenu();
			status.push("sw");
			backListener(status);
			break;
		case "m":
			workFile.showMedians();
			showBackMenu();
			status.push("sm");
			backListener(status);
			break;
		case "c":
			try {
				workFile.saveMediansToFile();
			} catch (Exception e) {
				System.out.println("Error in creating the file \nSomething went wrong");
				e.printStackTrace();
			}
			showBackMenu();
			status.push("sf");
			backListener(status);
			break;
		case "s":
			boolean correct = false;
			while (!correct) {
				System.out.println("Please, specify window size using digits");
				response = input.nextLine().trim().toLowerCase();
				try {
					int newWindowSize = Integer.parseInt(response);
					if (newWindowSize > 0) {
						correct = true;
						workFile.setWindowSize(newWindowSize);
						System.out.println("Window size is set to " + workFile.getWindowSize());
					} else {
						System.out.println("Please, specify a size bigger than zero");
					}
				} catch (Exception e) {
					System.out.println("Please, use only numbers!");
				}
			}

			showBackMenu();
			status.push("cws");
			backListener(status);
			break;
		case "g":
			goBack(status);
			break;
		case "q":
			terminateTheApp();
			break;
		default:
			System.out.println("I do not understand you!" + "\nPlease use the letters specified in brackets!");
			System.out.println("");
			determineListener(status);
			break;
		}
	}
}
