#!/bin/sh

ssh root@proxy "rm -rf /tmp/gen-dns"
scp -r /tmp/gen-dns root@proxy:/tmp/
ssh root@proxy "/opt/nginx_proxy_refresh.sh"
