#!/bin/sh

# Backup good config
mkdir -p /etc/nginx/backup
rm -rf /etc/nginx/backup/*
cp -rf /etc/nginx/conf.d/* /etc/nginx/backup/

# Copy new configs
cp -rf /tmp/gen-dns/* /etc/nginx/conf.d/

# Test config
nginx -t

if [ $? -eq 0 ]
then
    # Activate config
  nginx -s reload
else
  # Restore
  rm -rf /etc/nginx/conf.d/*
  cp -rf /etc/nginx/backup/* /etc/nginx/conf.d/
fi

exit 0