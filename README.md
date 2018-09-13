Swarm Manager
===============

# Run in production

# Development

## Szerver oldal

### Szerver oldal előfeltétele
* Java 8
* Docker (engine)
* Docker swarm (egy gépes swarm is tökéletes: ```docker swarm init```)
* Futó MongoDB adatbázis. Elindítása a dev-env mappából: ```docker-compose -f docker-compose-dev.yml up -d```
* Java Editor (pl Spring STS(Eclipse), vagy IDEA)
* ProjectLombok az editorban: https://projectlombok.org/setup/eclipse

Szerver kód Kiindulási könyvtár: ```server```
Maven alapú a kód, importálása a megszokott módon.

A Docker adatok eléréséhez, műveletvégzéshez a Docker Engine Rest API-t használjuk, egy Java library-n keresztül: https://github.com/docker-java/docker-java

#### Szerver indítása:
* STS esetén a bal alsó sarokban a springBoot Dashboard segít
* Parancssorból: ./mvnw clean spring-boot:run ## Van HotReload is, az egész context-et újraindítja, így nem kell mindig indítgatni.

#### DB Pach
* Ütes Mongo DB-t a Changelog001Init java osztály tölti fel sample adatokkal, és konfigurációval.

#### Limitációk
* Nincs admin felület, így minden config módosítást közvetlenül a DB-ben kell elvégezni kézzel.

#### Swarm adatok begyűjtése
Kiinduló osztály: ```SwarmService```. Itt egy scheduler egy task-ot készít, ami időnként (konfigurálható) lefut: ```SwarmCollectTask```. Itt gyűjtjük össze a swarm információit, mindent amit a felületen megjelenítünk. A felület időnként egy Controlleren keresztül ezeket kéri le.

## Kliens oldal - WebApp

### WebApp előfeltételek
* NodeJS
* yarn (```npm install -g yarn```)
* angular-cli (```yarn global add @angular/cli```)
* Angular Editor (pl: VSCode, vagy IDEA)

Webapp kód kiindulási könyvtára: ```client/webapp```
VSCode-ban megnyitni: ```code .```

#### Kód indítása:
Angul fejlesztői módban indítása: ```yarn start```
Proxy be van lőve, így amikor a böngészőben a localhost:4200 bejön, akkor minden /api kérést a localhost:8080 fele továbbít automatikusan(szerver portja)


## Példa ecosystem-ek:
Van pár példa compose file ecosystemekre, amivel fejlesztés közben mint docker adat használhatunk: ```ecoTest``` mappában.

## Éles környezet előállítása
a ```build_deploy_run``` mappa tartalmazza azokat a DockerFile-okat, amelyekkel elő lehet állítani az image-eket, valamint egy példa docker-compose file is a futtatáshoz (ez persze telepítésenként eltér)
