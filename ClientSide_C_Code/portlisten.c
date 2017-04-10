#include <errno.h>
#include <string.h>
#include <unistd.h>
#include <netdb.h>
#include <sys/socket.h>
#include <netinet/in.h>

const char* hostname=0; /* wildcard */
const char* portname="daytime";
struct addrinfo hints;
memset(&hints,0,sizeof(hints));
hints.ai_family=AF_UNSPEC;
hints.ai_socktype=SOCK_DGRAM;
hints.ai_protocol=0;
hints.ai_flags=AI_PASSIVE|AI_ADDRCONFIG;
struct addrinfo* res=0;
int err=getaddrinfo(hostname,portname,&hints,&res);
if (err!=0) {
    die("failed to resolve local socket address (err=%d)",err);
}

int fd=socket(res->ai_family,res->ai_socktype,res->ai_protocol);
if (fd==-1) {
    die("%s",strerror(errno));
}

if (bind(fd,res->ai_addr,res->ai_addrlen)==-1) {
    die("%s",strerror(errno));
}

freeaddrinfo(res);
