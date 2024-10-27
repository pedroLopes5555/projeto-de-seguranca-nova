project run:

DSTP folder is the library folder (sender and decrypt)

in SendReciveSimpleExample we can see a simple example of a implementaction of the DSTP developed.

In oder to test streaming:
pull from docker hub the proxy and the server docker containers:

proxy:
docker pull lopes5555/hjudp-proxy

stream server:
docker pull lopes5555/hjstream-server

to run:
proxy:
docker run --rm -it --network host lopes5555/hjudp-proxy endpoint1 endpoint2 cryptoconfig_path
example: docker run --rm -it --network host lopes5555/hjudp-proxy 192.168.68.55:9000 192.168.68.56:9000 cryptoconfig.txt

server:
docker run --rm -it --network host hjudp-server movie.dat prozy_endpoint cryptoconfig.txt
example: docker run --rm -it --network host hjudp-server cars.dat 127.0.0.2 9000 cryptoconfig.txt

![Alt text](/images/streaming.png)


