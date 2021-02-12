# Compiling
The program uses modules from [libcurl](https://curl.se/libcurl/) and [openssl](https://www.openssl.org/source/) library. Please ensure that both libraries are installed
on your environment. \
For mac users you can install the missing library using `brew install`, for linux users you can use `sudo apt install`. \
Run `make` under cpp directory to compile the cpp file. Note: The directory path for openssl in the `makefile` is for macOS, please change it accordingly to point to the
correct `lib` and `include` path for linux OS.

# Running
The program reads the API key and secret key from the environment variable.\
Run the following commands: \
`EXPORT BINANCE_API_KEY="<your_api_key>"`\
`EXPORT BINANCE_API_SECRET="<your_secret_key>"`\
`./spot`
