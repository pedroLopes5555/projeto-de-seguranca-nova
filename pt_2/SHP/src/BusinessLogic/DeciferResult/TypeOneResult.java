package BusinessLogic.DeciferResult;

public class TypeOneResult extends DeciferResult{

    private String result;


    @Override
    public String getValue(){
        return result;
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
