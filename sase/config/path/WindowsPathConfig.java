package sase.config.path;

public class WindowsPathConfig extends PathConfig {

	public WindowsPathConfig() {
		companyToRegionDirectoryPath = "Regions";
		testInputFilePath = "";
		firstInputFilePath = "NASDAQ_20080204_1.txt";
		firstInputDirectoryPath = "";
		outputFilePath = "output.csv";
		selectivityEstimatorsFilePath = "selectivity.ser";
	}
}
