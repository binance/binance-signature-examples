MAKEFLAGS += --silent

default: build

build: clean
	g++ -Wall -std=c++11 spot.cpp -o spot \
	-I /usr/local/opt/openssl/include \
	-L/usr/local/opt/openssl/lib \
    -lcurl \
    -lcrypto \
    -lssl
clean:
	rm -rf spot 