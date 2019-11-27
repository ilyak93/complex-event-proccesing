package sase.config.path;

public class WindowsPathConfig extends PathConfig {

	public WindowsPathConfig() {
		companyToRegionDirectoryPath = "Regions";
		testInputFilePath = "testInputFile";
		firstInputFilePath = "NASDAQ_20080204_1.txt";
		firstInputDirectoryPath = "input_dir_path";
		outputFilePath = "output.csv";
		outputFilePathProb = "outputProb.csv";
		selectivityEstimatorsFilePath = "selectivity.ser";
	}
}
