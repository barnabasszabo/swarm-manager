#!/bin/bash

docker system prune -af

docker stack deploy -c docker-compose-good-cat1.yml test-good-cat1-1
docker stack deploy -c docker-compose-good-cat2.yml test-good-cat2-1
docker stack deploy -c docker-compose-good-cat2.yml test-good-cat2-2
docker stack deploy -c docker-compose-good-cat2.yml test-good-cat2-3
docker stack deploy -c docker-compose-good-cat2.yml test-good-cat2-4

