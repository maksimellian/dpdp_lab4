import akka.actor.AbstractActor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActorStorage extends AbstractActor {
    private Map<String, ArrayList<Test>> store = new HashMap();

    public void addTest(Test test) {
        String packageId = test.getPackage().getPackageId();
        if (!store.containsKey(packageId)) {
            ArrayList<Test> testsArr = new ArrayList<>();
            testsArr.add(test);
            store.put(packageId, testsArr);
        } else {
            store.get(packageId).add(test);
        }
    }

    public Map<String, String> getTestResults(String packageId) {
        Map<String, String> testResults = new HashMap<>();
        if (store.containsKey(packageId)) {
            for (Test test : store.get(packageId)) {
                if (test.getRealResult().equals(test.getExpecteResult())) {
                    testResults.put(test.getTestName(), "OK");
                } else {
                    testResults.put(test.getTestName(), "FAIL");
                }
            }
        }
        return testResults;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Test.class, test -> addTest(test))
                .match(String.class, pId -> sender().tell(getTestResults(pId), self()))
                .build();
    }
}