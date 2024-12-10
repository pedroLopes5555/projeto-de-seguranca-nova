package Objects;

public class Payload {

    protected byte[] payload;

    public byte[] getByteArray() {
        return this.payload;
    }

    public int getPayloadLenght(){
        return this.payload.length;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }


}
