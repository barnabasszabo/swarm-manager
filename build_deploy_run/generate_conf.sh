#!/bin/sh

ENV_NAME="$1"
ENV_URL="$2"

mkdir -p /tmp/gen-dns

LOCATIONS=$(curl ${ENV_URL})
cat > /tmp/gen-dns/gen-${ENV_NAME}.conf <<EOL
server {
    server_name ${ENV_NAME}.loxon.swarm;
    listen 80;

    ${LOCATIONS}
}
EOL
