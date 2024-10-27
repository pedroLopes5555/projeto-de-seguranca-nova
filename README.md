# About the project

When developing the project, we faced some difficulties that led into failing to complete the implementation of all the project requirements. 

- **DSTP implementation:** Successfully implemented.
- **Test Multicast:** Successfully implemented, working with docker and in different machines.
- **TFTP Master:** Not implemented.
- **Streaming Service:** Successfully implemented, working with docker and in different machines, but failing some integrity checks.

## Library implementation

Among the folders in our project, DSTP folder is the one responsible for the library implmentation, containing the code used to make both the encryption and decryption. The following images show the process that the data is submited when going through encryption.

![Code](/images/bible1.png)

![Code](/images/bible2.png)

# Demonstration

In this chapter we will provide some images that will show the implementation of the programs requested.

### Test Multicast

![Multicasting](/images/multicasting.png)

### Streaming Service

![Streaming image 1](/images/streaming.png)

![Streaming image 2](/images/streaming2.png)

As it is possible to see, the streaming is working with docker and running in different machines. Despite this achievements, we didnt manage to secure the data integrity.

# Docker Download Guide

Since the implementation is supported by docker, here is a guide that will allow the download and running the images on different machines.

## Test Multicast

First we need to pull from docker both sender and receiver images using the following command:
```bash
docker pull lopes5555/multicast-sender

docker pull lopes5555/multicast-reciver
```

When successfuly pulled the images we can run them as test them, using the following commands:

---
```bash
docker run --network host <MulticastSender> <grupo_multicast> <porto> <time-interval> <cryptoconfig_path>

example:   docker run --network host multicast-sender 224.0.0.3 4446 2 cryptoconfig.txt
```
---
```bash
docker run --network host <MulticastReceiver> <grupo_multicast> <porto> <cryptoconfig_path>

example:   docker run --network host multicast-receiver 224.0.0.3 4446 cryptoconfig.txt
```
---

## Streaming Service

First we need to pull from docker both proxy and server images using the following command:
```bash
docker pull lopes5555/hjudp-proxy

docker pull lopes5555/hjstream-server
```

When successfuly pulled the images we can run them as test them, using the following commands:

---
```bash
docker run --rm -it --network host <hjudp-proxy> <endpoint1> <endpoint2> <cryptoconfig_path>

example:   docker run --rm -it --network host lopes5555/hjudp-proxy 192.168.68.55:9000 192.168.68.56:9000 cryptoconfig.txt
```
---
```bash
docker run --rm -it --network host <hjudp-server> <movie.dat> <prozy_endpoint> <cryptoconfig.txt>

example:   docker run --rm -it --network host hjudp-server cars.dat 127.0.0.2 9000 cryptoconfig.txt
```
---