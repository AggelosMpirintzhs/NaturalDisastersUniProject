package dom2app;

public class SingleMeasureRequest implements ISingleMeasureRequest{
	
	
	private String requestName;
	private boolean answerFlag = false;
	private IMeasurementVector answer = null;
	private String descriptiveStats = null;
	private String regression = null;
	private String countryName;
	private String indicatorString;
	
	//constructor for when there is no result for the request
	public SingleMeasureRequest(String requestName,String countryName,String indicatorString,boolean answerFlag) {
		this.requestName = requestName;
		this.answerFlag = answerFlag;
		this.countryName = countryName;
		this.indicatorString = indicatorString;
	}
	//constructor that accepts the result of the request without computed statistics
	public SingleMeasureRequest(String requestName,IMeasurementVector answer,boolean answerFlag) {
		this.requestName = requestName;
		this.answer = answer;
		this.answerFlag = answerFlag;
		this.countryName = answer.getCountryName();
		this.indicatorString = answer.getIndicatorString();
	}
	
	@Override
	public String getRequestName() {
		return requestName;
	}

	@Override
	public String getRequestFilter() {
		return "Country: "+ countryName + " " + "Indicator: "+ indicatorString;
	}

	@Override
	public boolean isAnsweredFlag() {
		return answerFlag;
	}

	@Override
	public IMeasurementVector getAnswer() {
		return answer;
	}

	@Override
	public String getDescriptiveStatsString() {
		if(answer != null) {
			descriptiveStats = answer.getDescriptiveStatsAsString();	
		}
		return descriptiveStats;
	}

	@Override
	public String getRegressionResultString() {
		if(answer != null) {
			regression = answer.getRegressionResultAsString();	
		}
		return regression;
	}
}
