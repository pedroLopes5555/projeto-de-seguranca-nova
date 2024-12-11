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


    @Override
    public byte[] getNonce3(){
        return result[2];
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
