
1st -> language is the way to implement, python , java, c#, go etc...

PA# - Project Contex

PRIMITIVES AND CRYPTO METHODS,
ALGS AND CONSTRUCTIONS
	Sym Crypto
	Secure Hashing
	Mac Constructions
Combination is Hybrid Construction


|--------------------------------------------------------------|
|THE PROJECT NEEDS TO BE RESPONSIVE TO THE CONFIGURACTION FILES|
|--------------------------------------------------------------|

CREATE A UDP Datagram Based java Applications
Basicly 2 machines connected, implement a datagram chanel on UDP -> assuming is not secure & not reliable

WE WILL NOT SOLVE RELYABILITY PROBLEMS OF UDP, PACKECTS CAN BE LOSS , but we want to provide security~


we will develop another layey, DSTP API and DSTP Protocol




Datagram sockets are a final class


What security propertys i want to implement ? 
define prescisly what are we implementing

use terms and correct terms from frameworks



1)
connectionless confidenctialaty and connectionless integrity whit no recovery  -> detect problems of integrity, but not recovery, if detected - dont process
detect tampered payloads

2)
Soluction transparently and easly used by programmmers to write new Secure Datagram based Aplications, using peovided Api Support

3)
Smooth porting of ore-existen applications, to adopt the provided secure channel properties and guaranties
ex: Stream cyphers, Shash 20, the only stream cypher that is standerdized on TLS, for example


4)
All the oproject needs to be configurable by a cng file -> in order to change something we should not recompile the coe, but change the cfg file
protect unwanted inputs on the cfg file , like using AES whith an ashe function
see uf the cfg file is valid -> HMAC's or MAC, or Secure Hash Algoritms

cryptoconfig.txt


WE WILL USE ONLY SIMETRIC STUFF
the distribuision of simetric cfg's isnt the goal of the projet



-------------/------------

UDP PAYLOAD
	DSTP HEADER 
		Version
		Realese
		Payload len (checksum)



	Encrypetd payload
		-seq number
		-data
		-H or MAC depending on the cfg
       |   |
       |   |
	vvv
	 v

ALL ON THE IP PACKET




|-------------------------------------------------------|
|So basicly we want to create a secure layer on UDP data|
|-------------------------------------------------------|



User Wireshark to watch the upd packets

Soluction must be able to be used on Singlecast Or Multicast


