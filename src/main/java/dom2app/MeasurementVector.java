package dom2app;

import java.util.List;
import org.apache.commons.math3.util.Pair;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.apache.commons.math3.stat.descriptive.moment.Kurtosis;
import org.apache.commons.math3.stat.regression.*;
import java.util.ArrayList;

public class MeasurementVector implements IMeasurementVector {
	
	//private String ObjectId;
	private String countryName;
	//private String iso2;
	//private String iso3;
	private String indicatorString;
	private List<Pair<Integer,Integer>> measurements;
	
	public MeasurementVector(String countryName,String indicatorString,List<Pair<Integer,Integer>> measurements ) {
		//this.ObjectId = dataLine[0];
		this.countryName = countryName;
		//this.iso2 = dataLine[2];
		//this.iso3 = dataLine[3];
		this.indicatorString = indicatorString;
		this.measurements = new ArrayList<>();
		this.measurements = measurements;
		
	}
	
	@Override
	public String getCountryName() {
		return countryName;
	}
	
	@Override
	public String getIndicatorString() {
		return indicatorString;
	}
	
	@Override
	public List<Pair<Integer, Integer>> getMeasurements() {
		return measurements;
	}
	
	@Override
	public String getDescriptiveStatsAsString() {
		String stats = computeDescriptiveStats();
		return stats;
	}
	//Assisting method that computes the values of the descriptive statistics
	private String computeDescriptiveStats() {
		List<Integer> counterList = new ArrayList<>();
		for (Pair<Integer, Integer> pair : measurements) {
            counterList.add(pair.getSecond());
        }
		DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics();
	    for (int counter : counterList) {
	        descriptiveStatistics.addValue(counter);
	    }
	    String count = "Count = " + descriptiveStatistics.getN();
		String min = "Min = " + descriptiveStatistics.getMin();
		String max = "Max = " + descriptiveStatistics.getMax();
		String mean = "Mean = " + descriptiveStatistics.getMean();
		String stdev = "stdev = " + descriptiveStatistics.getStandardDeviation();
		String sum = "Sum: " + descriptiveStatistics.getSum(); 
		double gMeanRes = Math.pow(descriptiveStatistics.getGeometricMean(), 1.0 / descriptiveStatistics.getN());
		String gMean = "gMean = " + gMeanRes;
		Percentile percentile = new Percentile();
	    percentile.setData(counterList.stream().mapToDouble(Integer::doubleValue).toArray());
	    double medianRes = percentile.evaluate(50);
	    String median = "median = " + medianRes;
	    Kurtosis thekurtosis = new Kurtosis();
        double kurtosisValue = thekurtosis.evaluate(counterList.stream().mapToDouble(Integer::doubleValue).toArray());
        String kurtosis = "kurtosis = " + kurtosisValue;
        
		return "[" +count +", "+ min +", "+gMean+", "+mean+", "+median+", "+max+", "+kurtosis+", "+stdev+", "+sum+"]";
	}
	@Override
	public String getRegressionResultAsString() {
		String regression = computeRegression();
		return regression;
	}
	//Assisting method that computes the regression values
	private String computeRegression(){
		SimpleRegression regression = new SimpleRegression();

        for (Pair<Integer, Integer> measurement : measurements) {
            regression.addData(measurement.getFirst(), measurement.getSecond());
        }

    
        double intercept = regression.getIntercept();
        double slope = regression.getSlope();
        double slopeError = regression.getSlopeStdErr();
        String tentency = calculateTendency(slope);
		return "["+ "Intercept = "+intercept+", "+"Slope = "+slope+", "+"SlopeError = "+slopeError+", "+tentency+"]";
	}
	//Assisting method that calculates the Tendency based on the slope 
	private static String calculateTendency(double slope) {
	    if (Double.isNaN(slope)) {
	         return "Tendency Undefined";
	     } else if(slope > 0.1) {
	         return "Increased Tendency";
	     } else if(slope < -0.1) {
	         return "Decreased Tendency";
	     }else {
	    	 return "Tendency stable";
	     }
	}
	
}
