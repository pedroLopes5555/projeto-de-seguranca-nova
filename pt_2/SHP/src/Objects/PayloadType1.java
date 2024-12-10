package Objects;

import java.nio.charset.StandardCharsets;

public class PayloadType1 extends Payload{
    public PayloadType1(String message) {
        this.payload = message.getBytes(StandardCharsets.UTF_8);
    }
}
