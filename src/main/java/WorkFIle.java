import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class WorkFIle{
	private String path;
	private List<String> content;
	private int windowSize;
	private List<Double> medians;
	private String outputDir;

	public WorkFIle(String path, String outputPath) {
		this.path = path;
		
		// default window size
		this.windowSize = 3;
		
		this.content = readFile(this.path);
		this.medians = determineMedians();
		
		this.outputDir = outputPath;
	}

	public int getWindowSize() {
		return windowSize;
	}

	public String getPath() {
		return path;
	}

	private List<String> readFile(String path) {
		//For work in IDE
		//String filePath = new File("src\\main\\resources\\input_files\\" + path).getAbsolutePath();
		
		//for maven
		String filePath = new File(System.getProperty("user.dir") + "\\classes\\input_files\\" + path).getAbsolutePath();
				
		try {
			FileReader fr = new FileReader(filePath);
			BufferedReader br = new BufferedReader(fr);

			ArrayList<String> lines = new ArrayList<>();
			String line = br.readLine();
			while (line != null) {
				if (line.length() > 0) {
					lines.add(line);
				}
				line = br.readLine();
			}
			fr.close();
			br.close();
			if (lines.size() > 0) {
				return lines;
			} else {
				return null;
			}
	
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private List<Double> determineMedians() {
		List<Double> medianList = new ArrayList<>();
		if (this.content != null) {
			List<Integer> contentList = this.content.stream().filter(i -> i.matches("[0-9]+")).map(Integer::valueOf)
					.collect(Collectors.toList());
			LinkedList<Integer> window = new LinkedList<>();
			
			for (Integer value : contentList) {
				if (window.size() < this.windowSize) {
					window.add(value);
				} else {
					window.poll();
					window.add(value);
				}
				medianList.add(findMedian(window));
			}
		} else {
			medianList = null;
		}
		return medianList;
	}

	private Double findMedian(List<Integer> input) {
		List<Double> windowDoubles = input.stream().sorted().map(Double::valueOf).collect(Collectors.toList());
		int listSize = windowDoubles.size();
		Double median = 0.0;
		if (listSize == 1) {
			median = windowDoubles.get(0).doubleValue();
		}
		if (listSize % 2 == 1) {
			int position = (listSize - 1) / 2;
			median = windowDoubles.get(position).doubleValue();
		}
		if (listSize % 2 == 0) {
			int p1 = listSize / 2;
			int p2 = p1 - 1;
			median = (windowDoubles.get(p1) + windowDoubles.get(p2)) / 2.0;
		}
		
		return median;
	}

	public void setWindowSize(int windowSize) {
		this.windowSize = windowSize;
		this.medians = determineMedians();
	}

	public void showWindows() {
		if (this.content != null) {
			//In case the file contains non-digits also, only numbers will be used
			List<Integer> contentList = this.content.stream().filter(i -> i.matches("[0-9]+")).map(Integer::valueOf)
					.collect(Collectors.toList());
			LinkedList<Integer> window = new LinkedList<>();
			System.out.println("Showing windows"
					+ "\nINDEX) | WINDOW");
			int index = 0;
			for (Integer value : contentList) {
				index++;
				if (window.size() < this.windowSize) {
					window.add(value);
				} else {
					window.poll();
					window.add(value);
				}
				System.out.println(index + ") | " + window);
			}
		} else {
			System.out.println("Selected file has no records" + 
		"\nThere is nothing to show");
		}
	}

	public void showMedians() {
		if (this.medians == null) {
			System.out.println("There are no medians! \nInput file, perhaps, has no numerical records");
		} else {
			System.out.println("Showing medians"
					+ "\nINDEX) MEDIAN");
			for (int i = 1; i <= this.medians.size(); i++) {
				System.out.println(i + ") " + medians.get(i-1));
			}
		}
	}

	public void saveMediansToFile() throws Exception {
		String creationDate = DateTimeFormatter.ofPattern("yyyy_MM_dd").format(LocalDate.now());
		String fileHeadName = this.path.replaceAll(".csv", "").replaceAll(" ", "_").replace(".", "") + "-window_size_" + this.windowSize + "-date-" + creationDate + "-v1";
		String fileFullName = fileHeadName + ".csv";

		List<String> outputFiles = new ArrayList<>();
		File[] existingFiles = new File(this.outputDir).listFiles();
		for (File file : existingFiles) {
			outputFiles.add(file.getName());
		}

		boolean uniqueName = false;
		int version = 1;
		while (!uniqueName) {
			version++;
			if (outputFiles.contains(fileFullName)) {
				fileHeadName = fileHeadName.substring(0, fileHeadName.length() - 1) + version;
				fileFullName = fileHeadName + ".csv";
			} else {
				uniqueName = true;
			}
		}

		File medianFile = new File(this.outputDir + "\\" + fileFullName).getAbsoluteFile();

		medianFile.createNewFile();
		FileWriter fw = new FileWriter(medianFile);
		if (this.medians != null) {
			for (Double median : this.medians) {
				fw.append(median.toString());
				fw.append("\n");
			}
			fw.flush();
			fw.close();
			System.out.println("File is created. You can find it in output_files directory. Full path:");
			System.out.println(medianFile);
		} else {
			fw.append("No medians to show");
			fw.flush();
			fw.close();
			System.out.println("File is created. However, used file had 0 records - there are no medians!");
			System.out.println("Output file path:");
			System.out.println(medianFile);
		}
	}

}
