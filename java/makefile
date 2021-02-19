MAKEFLAGS += --silent

default: build

build: clean
	javac -d ./ Spot.java \
	Main.java \
	lib/*.java
clean:
	rm -rf *.class 