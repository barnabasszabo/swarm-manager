#!/bin/sh

ssh root@proxy "rm -rf /tmp/gen-dns && mkdir -p /tmp/gen-dns"
scp -r /tmp/gen-dns/* root@proxy:/tmp/gen-dns/
ssh root@proxy "/opt/nginx_proxy_refresh.sh"
