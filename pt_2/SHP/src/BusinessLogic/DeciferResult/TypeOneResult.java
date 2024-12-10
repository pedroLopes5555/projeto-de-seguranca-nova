package BusinessLogic.DeciferResult;

public class TypeOneResult extends DeciferResult{

    private String result;


    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public TypeOneResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "TypeOneResult{" +
                "result='" + result + '\'' +
                '}';
    }
}
