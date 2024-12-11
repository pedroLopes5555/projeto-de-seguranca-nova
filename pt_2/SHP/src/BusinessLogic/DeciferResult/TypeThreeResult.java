package BusinessLogic.DeciferResult;

public class TypeTreeResult extends  DeciferResult{


    private byte[] result;

    public TypeTreeResult(byte[] encryptedPayload) {
        this.result = deciferPayload(encryptedPayload);
    }




    public byte[] getResult() {
        return result;
    }


    @Override
    public String toString() {
        return "TypeOneResult{" +
                "result='" + result + '\'' +
                '}';
    }


    private byte[] deciferPayload(byte[] encriptedPayload){

        byte[] test = new byte[10];


        return test;
    }
}
