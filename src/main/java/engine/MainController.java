package engine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import org.apache.commons.math3.util.Pair;


import dom2app.IMeasurementVector;
import dom2app.ISingleMeasureRequest;
import dom2app.MeasurementVector;
import dom2app.SingleMeasureRequest;

public class MainController implements IMainController{

	private List<IMeasurementVector> fileData = new ArrayList<>();
	private Set<String> allRequestNames = new HashSet<>();
	private List<ISingleMeasureRequest> allRequests = new ArrayList<>();
		
	@Override
	public List<IMeasurementVector> load(String fileName, String delimiter) throws FileNotFoundException, IOException {
		BufferedReader inputStream = null;
		List<IMeasurementVector> data = new ArrayList<>();
		try {
			inputStream = new BufferedReader(new FileReader(fileName));
				
		}catch(FileNotFoundException e) {
			throw new FileNotFoundException("\nFileNotFoundException: unable to open " + fileName + "\n" + e.getLocalizedMessage()+ "\n");
		}
		String nextInputLine = "";
		nextInputLine = inputStream.readLine();
		String[] firstLineParts = nextInputLine.split(delimiter,-1);  //We save the first input of the first line separately 
		while((nextInputLine = inputStream.readLine()) != null){
			List<Pair<Integer,Integer>> measurements = new ArrayList<>();
			String[] stringParts = nextInputLine.split(delimiter,-1);
			fillEmptyFieldsWithZero(stringParts);
			for (int i = 5; i < stringParts.length; i++) {
				measurements.add(new Pair<>(Integer.parseInt(firstLineParts[i]),Integer.parseInt(stringParts[i])));
			}
			data.add(new MeasurementVector(stringParts[1],stringParts[4],measurements));
		}
		
		if(inputStream != null) {
			try {
				inputStream.close();
			}catch(IOException e){
				System.err.println("IOException: could	 not close input file stream for file: " + fileName);
			}
		}
		fileData = data;
		return data;
	}
	
	private void fillEmptyFieldsWithZero(String[] stringParts) {
	     for (int i = 5; i < stringParts.length; i++) {
	        if (stringParts[i].trim().isEmpty()) {
	             stringParts[i] = "0";
	         }
	     }
	}

	@Override
	public ISingleMeasureRequest findSingleCountryIndicator(String requestName, String countryName,String indicatorString) throws IllegalArgumentException {
		if (requestName.isEmpty() || countryName.isEmpty() || indicatorString.isEmpty()) {
		    throw new IllegalArgumentException("One or more arguments are empty");
		}
		for(IMeasurementVector measurementVector : fileData) {
			if(measurementVector.getCountryName().equalsIgnoreCase(countryName) && measurementVector.getIndicatorString().equalsIgnoreCase(indicatorString)) {
				ISingleMeasureRequest answer = new SingleMeasureRequest(requestName, measurementVector,true);
				allRequests.add(answer);
				allRequestNames.add(countryName);
				return answer;
			}
		}
		return new SingleMeasureRequest(requestName,countryName,indicatorString,false);
	}

	@Override
	public ISingleMeasureRequest findSingleCountryIndicatorYearRange(String requestName, String countryName,String indicatorString, int startYear, int endYear) throws IllegalArgumentException {
		if (requestName.isEmpty() || countryName.isEmpty() || indicatorString.isEmpty()) {
		    throw new IllegalArgumentException("One or more arguments are empty");
		}
		if (endYear < startYear) {
		    throw new IllegalArgumentException("End year is less than start year");
		}
		for(IMeasurementVector measurementVector : fileData) {
			if(measurementVector.getCountryName().equalsIgnoreCase(countryName) && measurementVector.getIndicatorString().equalsIgnoreCase(indicatorString)) {
				 List<Pair<Integer, Integer>> filteredList = getFilteredList(measurementVector.getMeasurements(), startYear, endYear);
				 IMeasurementVector answerVector = new MeasurementVector(measurementVector.getCountryName(),measurementVector.getIndicatorString(),filteredList);
				 ISingleMeasureRequest answer = new SingleMeasureRequest(requestName, answerVector,true);
				 allRequests.add(answer);
				 allRequestNames.add(countryName);
				 return answer;
			}
		}
		return new SingleMeasureRequest(requestName,countryName,indicatorString,false);
	}
	
	//Assisting method that returns a list for a specific year range
	private List<Pair<Integer, Integer>> getFilteredList(List<Pair<Integer, Integer>> measurements, int startYear, int endYear) {
	       List<Pair<Integer, Integer>> filteredList = new ArrayList<>();

	       for (Pair<Integer, Integer> measurement : measurements) {
	           int year = measurement.getFirst();
	           if (year >= startYear && year <= endYear) {
	               filteredList.add(measurement);
	           }
	       }
	        return filteredList;
	  }

	@Override
	public Set<String> getAllRequestNames() {
		return allRequestNames;
	}

	@Override
	public ISingleMeasureRequest getRequestByName(String requestName) {
		for (ISingleMeasureRequest request : allRequests) {
			if(request.getRequestName().equalsIgnoreCase(requestName)) {
				return request;
			}
		}
		return null;
	}

	@Override
	public ISingleMeasureRequest getRegression(String requestName) {
		if( getRequestByName(requestName) != null && getRequestByName(requestName).isAnsweredFlag()) {
			ISingleMeasureRequest request =  getRequestByName(requestName);
			request.getRegressionResultString();
			return request;
		}	
		return null;
	}

	@Override
	public ISingleMeasureRequest getDescriptiveStats(String requestName) {
		if( getRequestByName(requestName) != null && getRequestByName(requestName).isAnsweredFlag()) {
			ISingleMeasureRequest request = getRequestByName(requestName);
			request.getDescriptiveStatsString();
			return request;
		}	
		return null;
	}

	@Override
	public int reportToFile(String outputFilePath, String requestName, String reportType) throws IOException {
		ISingleMeasureRequest request =  getRequestByName(requestName);
		if(request != null && request.isAnsweredFlag()) {
			BufferedWriter bufferWriter = null;
			try {
				bufferWriter = new 	BufferedWriter(new FileWriter(outputFilePath,false));
				if(reportType.equals("text")) {
					int rows = formatAndOutputText(outputFilePath,request,bufferWriter);
					bufferWriter.close();
					return rows;
				}else if(reportType.equals("md")) {
					int rows = formatAndOutputMarkdown(outputFilePath,request,bufferWriter);
					bufferWriter.close();
					return rows;
				}else if(reportType.equals("html")) {
					int rows = formatAndOutputHtml(outputFilePath,request,bufferWriter);
					bufferWriter.close();
					return rows;
				}
				bufferWriter.close();
			}catch(IOException e){
				String msg = "IOException: Trying to work with file "+ outputFilePath + "for output";
				throw new IOException ("\n" + msg + "\n"+ e.getLocalizedMessage() +"\n");
			}
			
		}
		
		return -1;
	}
	private int formatAndOutputText(String filePath, ISingleMeasureRequest request,BufferedWriter bufferWriter) throws IOException {
		bufferWriter.write(request.getRequestName() + "\n");
		bufferWriter.write(request.getRequestFilter() + "\n");
		bufferWriter.write("Year"+"\t"+"Value"+ "\n");
		int rowCount = 5;
		List<Pair<Integer,Integer>> ReqMeasurements;
		ReqMeasurements = request.getAnswer().getMeasurements();
		for (Pair<Integer, Integer> measurement : ReqMeasurements) {
			bufferWriter.write(measurement.getFirst()+"\t"+measurement.getSecond() + "\n");
			rowCount++;
		}
		bufferWriter.write("DescriptiveStatsResult " + request.getDescriptiveStatsString() + "\n");
		bufferWriter.write("RegressionResult " + request.getRegressionResultString());
		return rowCount;
	}
	private int formatAndOutputMarkdown(String filePath, ISingleMeasureRequest request,BufferedWriter bufferWriter) throws IOException{
		int rowCount = 23;
		bufferWriter.write("**"+request.getRequestName()+"**" + "\n\n");
		bufferWriter.write("_"+request.getRequestFilter()+"_" + "\n\n");
		bufferWriter.write("|**Year**|"+"**Value**|"+ "\n");
		bufferWriter.write("|----|----|"+"\n");
		List<Pair<Integer,Integer>> ReqMeasurements;
		ReqMeasurements = request.getAnswer().getMeasurements();
		for (Pair<Integer, Integer> measurement : ReqMeasurements) {
			bufferWriter.write("|"+measurement.getFirst()+"|"+measurement.getSecond()+"|" + "\n");
			rowCount++;
		}
		bufferWriter.write("\n"+"Descriptive Statistics:\n");
		bufferWriter.write(request.getDescriptiveStatsString() + "\n");
		bufferWriter.write("\n"+"Regression Result:\n");
		bufferWriter.write(request.getRegressionResultString() + "\n");		
		return rowCount;
	}
	private int formatAndOutputHtml(String filePath, ISingleMeasureRequest request,BufferedWriter bufferWriter) throws IOException{
		int rowCount = 107;
		bufferWriter.write("<p><b>"+request.getRequestName()+"</p></b>"+ "\n");
		bufferWriter.write("<p><i>"+request.getRequestFilter()+"</p></i>" + "\n");
		bufferWriter.write("<table \n<tr>"+"<td><b>Year</td></b>"+"<td><b>Value</td></b>\n");
		List<Pair<Integer,Integer>> ReqMeasurements;
		ReqMeasurements = request.getAnswer().getMeasurements();
		for (Pair<Integer, Integer> measurement : ReqMeasurements) {
			bufferWriter.write("\n </tr> \n <tr> \n" +"<td>"+measurement.getFirst()+"</td> <td>"+measurement.getSecond()+"</td>");
			rowCount++;
		}
		bufferWriter.write("\n </tr> \n </table><p>"+"Descriptive Statistics:\n");
		bufferWriter.write(request.getDescriptiveStatsString() + "\n");
		bufferWriter.write("<p>"+"Regression Result:\n");
		bufferWriter.write(request.getRegressionResultString() + "\n");	
		return rowCount;
	}
}
