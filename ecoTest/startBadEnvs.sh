#!/bin/bash

docker system prune -af

docker stack deploy -c docker-compose-higherGap.yml test-higherGap 
docker stack deploy -c docker-compose-limitLowerThanReserved.yml test-limitLowerThanReserved 
docker stack deploy -c docker-compose-missingResource.yml test-missingResource 
docker stack deploy -c docker-compose-multipleCategory.yml test-multipleCategory 
docker stack deploy -c docker-compose-noCategory.yml test-noCategory 
docker stack deploy -c docker-compose-noInfo.yml test-noInfo 
docker stack deploy -c docker-compose-allMissing.yml test-allMissing 
docker stack deploy -c docker-compose-invalidPriority.yml test-invalidPriority 

