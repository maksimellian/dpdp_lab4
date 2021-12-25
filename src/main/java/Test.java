import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Test {
    private String testName;
    private String expectedResult;
    private Object[] params;
    private String realResult;
    private TestPackage testPckg;

    @JsonCreator
    public Test(
            @JsonProperty("testName") String jsonTestName,
            @JsonProperty("expectedResult") String jsonExpectedResult,
            @JsonProperty("params") Object[] jsonParams){
        this.testName = jsonTestName;
        this.expectedResult = jsonExpectedResult;
        this.params = jsonParams;
        this.realResult = "";
    }

    public TestPackage getPackage() {
        return testPckg;
    }

    public String getRealResult() {
        return realResult;
    }

    public String getExpecteResult() {
        return expectedResult;
    }

    public String getTestName() {
        return testName;
    }

    public Object[] getParams() {
        return params;
    }

    public void setTestPackage(TestPackage testPack) {
        testPckg = testPack;
    }

    public void setRealResult(String getRes) {
        realResult = getRes;
    }
}