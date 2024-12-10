package BusinessLogic.DeciferResult;

import java.util.Arrays;

public class TypeTwoResult extends DeciferResult {

    private byte[][] result;


    public TypeTwoResult(byte[][] result) {
        this.result = result;
    }

    public byte[][] getResult() {
        return result;
    }

    public void setResult(byte[][] result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "TypeTwoResult{" +
                "result=" + Arrays.toString(result) +
                '}';
    }
}
