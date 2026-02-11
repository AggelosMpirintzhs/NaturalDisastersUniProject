import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import org.apache.commons.math3.util.Pair;
import org.junit.Before;
import org.junit.Test;

import dom2app.IMeasurementVector;
import dom2app.ISingleMeasureRequest;
import dom2app.MeasurementVector;
import dom2app.SingleMeasureRequest;
import engine.MainController;

public class MainControllerTest {
	private MainController MC;
	//Creating an expected result to compare it with the result of the load method 
	private List<IMeasurementVector> expectedResult = new ArrayList<>();
	private List<IMeasurementVector> loadResult = new ArrayList<>();
	//I am sure there is a better way to do this but this the only way i know that the expected result is right
	@Before
	public void setUp() throws Exception {
	MC = new MainController();
	loadResult = MC.load("//2023-24_4741_4899_4905_NaturalDisasters/src/test/resources/input/gre.tsv", "\t");
	//Getting the Strings for the expected result of the measurement values
	String[] header = {"1980","1981","1982","1983","1984","1985","1986","1987","1988","1989","1990","1991","1992","1993","1994","1995","1996","1997","1998","1999","2000","2001","2002","2003","2004","2005","2006","2007","2008","2009","2010","2011","2012","2013","2014","2015","2016","2017","2018","2019","2020","2021","2022"};
	String[] GrDrought = {"0","0","0","0","0","0","0","0","0","0","1","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0"};
	String[] GrExTemp = {"0","0","0","0","0","1","0","1","1","0","0","1","0","0","0","0","0","0","0","0","1","0","0","0","0","0","0","1","0","0","0","0","1","0","0","0","0","1","0","0","0","0","0"};
	String[] GrFlood = {"0","0","0","0","0","0","0","0","0","0","0","0","0","0","1","0","0","1","1","0","2","2","4","3","0","0","2","1","0","0","1","0","1","0","1","2","1","1","0","0","2","0","0"};
	String[] GrStorm = {"0","0","0","0","0","0","0","1","0","0","1","0","0","0","0","0","0","0","0","0","0","0","1","0","2","0","1","0","0","0","0","0","0","0","0","0","0","0","0","1","0","0","0"};
	String[] GrTot = {"0","0","0","1","2","2","0","3","1","0","2","1","0","0","1","1","0","1","2","0","5","2","5","3","2","0","3","4","0","2","1","0","2","0","1","2","1","2","1","1","2","1","1"};
	String[] GrFire = {"0","0","0","1","2","1","0","1","0","0","0","0","0","0","0","1","0","0","1","0","2","0","0","0","0","0","0","2","0","2","0","0","0","0","0","0","0","0","1","0","0","1","1"};
	String[] GrdDrought = {"0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","1","0","0","0","0","0","0","0","0","0","0","0","0"};
	String[] GrdStorm = {"1","0","0","0","0","0","0","0","0","0","1","0","0","0","0","0","0","0","0","1","0","0","1","0","1","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0"};
	String[] GrdTot = {"1","0","0","0","0","0","0","0","0","0","1","0","0","0","0","0","0","0","0","1","0","0","1","0","1","0","0","0","0","0","1","0","0","0","0","0","0","0","0","0","0","0","0"};
	List<Pair<Integer,Integer>> GrDroughtMes = processData(header,GrDrought);
	List<Pair<Integer,Integer>> GrExTempMes = processData(header,GrExTemp);
	List<Pair<Integer,Integer>> GrFloodMes = processData(header,GrFlood);
	List<Pair<Integer,Integer>> GrStormMes = processData(header,GrStorm);
	List<Pair<Integer,Integer>> GrFireMes = processData(header,GrFire);
	List<Pair<Integer,Integer>> GrTotMes = processData(header,GrTot);
	List<Pair<Integer,Integer>> GrdDroughtMes = processData(header,GrdDrought);
	List<Pair<Integer,Integer>> GrdStormMes = processData(header,GrdStorm);
	List<Pair<Integer,Integer>> GrdTotMes = processData(header,GrdTot);
	expectedResult.add(new MeasurementVector("Greece","Drought",GrDroughtMes));
	expectedResult.add(new MeasurementVector("Greece","Extreme temperature",GrExTempMes));
	expectedResult.add(new MeasurementVector("Greece","Flood",GrFloodMes));
	expectedResult.add(new MeasurementVector("Greece","Storm",GrStormMes));
	expectedResult.add(new MeasurementVector("Greece","TOTAL",GrTotMes));
	expectedResult.add(new MeasurementVector("Greece","Wildfire",GrFireMes));
	expectedResult.add(new MeasurementVector("Grenada","Drought",GrdDroughtMes));
	expectedResult.add(new MeasurementVector("Grenada","Storm",GrdStormMes));
	expectedResult.add(new MeasurementVector("Grenada","TOTAL",GrdTotMes));
	}
	
	//
	private List<Pair<Integer, Integer>> processData(String[] header, String[] data) {
        List<Pair<Integer, Integer>> result = new ArrayList<>();
        // Check if the input arrays are valid
        if (header == null || data == null || header.length != data.length) {
            // Handle invalid input
            throw new IllegalArgumentException("Invalid input: header and data arrays must not be null and must have the same length.");
        }

        // Iterate through the arrays and create pairs
        for (int i = 0; i < header.length; i++) {
            try {
                int headerValue = Integer.parseInt(header[i]);
                int dataValue = Integer.parseInt(data[i]);
                Pair<Integer, Integer> pair = new Pair<>(headerValue, dataValue);
                result.add(pair);
            } catch (NumberFormatException e) {
                // Handle the case where parsing fails (e.g., non-integer values in the arrays)
                System.err.println("Error parsing values at index " + i);
                e.printStackTrace();
            }
        }

        return result;
    }
	// !!!!
	//For the HappyDay scenario of load i could only create only 1 expected result so to test it give the path to the gre.tsv file
	@Test
	public final void testLoadHappyDay() throws FileNotFoundException, IOException {
		List<IMeasurementVector> testResult = MC.load("C:/Users/mavra/Desktop/CSE/2023-24_AM1_AM2_AM3_NaturalDisasters/src/test/resources/input/gre.tsv", "\t");
		int tester = 1;
		if(expectedResult.size() != testResult.size()) {
			tester = 0;
			assertEquals(1,tester);
		}
		for(int i = 0; i < expectedResult.size(); i++ )	{
			if(!expectedResult.get(i).getCountryName().equals(testResult.get(i).getCountryName()) || !expectedResult.get(i).getIndicatorString().equals(testResult.get(i).getIndicatorString())) {				
				   tester = 2;
				   assertEquals(1,tester);
			}
			for(int j = 0; j < expectedResult.size(); j++) {
				if(expectedResult.get(i).getMeasurements().size() != testResult.get(i).getMeasurements().size()) {
					tester = 3;
					assertEquals(1,tester);
				}
			}
			for(int k = 0; k < expectedResult.get(i).getMeasurements().size(); k++ ) {
				if(!expectedResult.get(i).getMeasurements().get(k).getFirst().equals(testResult.get(i).getMeasurements().get(k).getFirst()) || !expectedResult.get(i).getMeasurements().get(k).getSecond().equals(testResult.get(i).getMeasurements().get(k).getSecond())) {
			tester = 4;
					assertEquals(1,tester);
				}
			}	
		}
		assertEquals(1,tester);		
	}
	//For the Rainy day scenario the method throws a FileNotFoundException
	@Test
	public final void testLoadRainyDay() throws FileNotFoundException, IOException {
		List<IMeasurementVector> testResult = MC.load("wrong Input", "\t");
		int tester = 1;
		if(expectedResult.size() != testResult.size()) {
			tester = 0;
			assertEquals(1,tester);
		}
		for(int i = 0; i < expectedResult.size(); i++ )	{
			if(!expectedResult.get(i).getCountryName().equals(testResult.get(i).getCountryName()) || !expectedResult.get(i).getIndicatorString().equals(testResult.get(i).getIndicatorString())) {				
				   tester = 2;
				   assertEquals(1,tester);
			}
			for(int j = 0; j < expectedResult.size(); j++) {
				if(expectedResult.get(i).getMeasurements().size() != testResult.get(i).getMeasurements().size()) {
					tester = 3;
					assertEquals(1,tester);
				}
			}
			for(int k = 0; k < expectedResult.get(i).getMeasurements().size(); k++ ) {
				if(!expectedResult.get(i).getMeasurements().get(k).getFirst().equals(testResult.get(i).getMeasurements().get(k).getFirst()) || !expectedResult.get(i).getMeasurements().get(k).getSecond().equals(testResult.get(i).getMeasurements().get(k).getSecond())) {
			tester = 4;
					assertEquals(1,tester);
				}
			}	
		}
		assertEquals(1,tester);		
	}
	
	//I am testing for results from the gre.tsv file if you want to check for other files change the input arguments
	@Test
	public final void testFindSingleCountryIndicator()throws IllegalArgumentException {
		ISingleMeasureRequest test = MC.findSingleCountryIndicator("GR-TOT", "Greece", "TOTAL");
		ISingleMeasureRequest expected = new SingleMeasureRequest("GR-TOT",loadResult.get(4),true);
		assertEquals(expected,test);
	}
	
	//I cannot override the equals method for the interfaces of IMeasurementVector and ISingleMeasureRequest
	//So its really hard to compare 2 objects of those types with assertEquals i guess there is a better way 
	//To create test cases but i could not find it 
}
