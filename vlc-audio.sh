cvlc -vvv stream:///dev/stdin
cvlc -vvv alsa://plughw:0,0 --sout '#standard{access=http,mux=ogg,dst=:8090}'