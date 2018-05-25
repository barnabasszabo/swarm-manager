#!/bin/bash

VERSION=0.1.3-alpha1-alpine

rm -rf dist
mkdir -p dist

cd ../client/webapp
#rm -rf node_modules
#yarn install
#npm run build

cd ../../server
./mvnw clean install -DskipTests

cd ../build_deploy_run

cp -rf ../client/webapp/dist/webapp dist/webapp
cp -rf ../server/target/swarmManager-0.0.1-SNAPSHOT.jar dist/app.jar

time docker build --pull -f Dockerfile-webapp -t szabobar/swarm-manager-proxy:$VERSION .
docker push szabobar/swarm-manager-proxy:$VERSION

time docker build --pull -f Dockerfile-server --build-arg JAR_FILE=dist/app.jar -t szabobar/swarm-manager-server:$VERSION .
docker push szabobar/swarm-manager-server:$VERSION
