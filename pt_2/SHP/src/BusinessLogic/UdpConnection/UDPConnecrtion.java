package BusinessLogic.UdpConnection;

public class UDPConnecrtion {
    int port;
    String Endpoint;
    String MediaPlayerIp;
    String requestedMovie;


    public String getRequestedMovie() {
        return requestedMovie;
    }

    public void setRequestedMovie(String requestedMovie) {
        this.requestedMovie = requestedMovie;
    }

    public UDPConnecrtion() {
    }


    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getEndpoint() {
        return Endpoint;
    }

    public void setEndpoint(String endpoint) {
        Endpoint = endpoint;
    }

    public String getMediaPlayerIp() {
        return MediaPlayerIp;
    }

    public void setMediaPlayerIp(String mediaPlayerIp) {
        MediaPlayerIp = mediaPlayerIp;
    }
}
