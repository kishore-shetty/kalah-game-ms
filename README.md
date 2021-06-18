# Kalah Game

It's a board game. The board has 6 small pits, called houses, on each side; and a big pit called Store. 
The object of the game is to capture more seeds than one's opponent.

## Game Rules
* At the beginning of the game, Six seeds are placed in each house.
* Each player controls the six houses, and their seeds on the player's side of the board. 
* The player's score is the number of seeds in the store to their right.
* Players take turns sowing their seeds. On a turn, the player removes all seeds from one of the houses under their control. Moving counter-clockwise, the player drops one seed in each house in turn, including the player's own store but not their opponent's.
* If the last sown seed lands in an empty house owned by the player, and the opposite house contains seeds, both the last seed, and the opposite seeds are captured and placed into the player's store.
* If the last sown seed lands in the player's store, the player gets an additional move. There is no limit on the number of moves a player can make in their turn.
* When one player no longer has any seeds in any of their houses, the game ends. The other player moves all remaining seeds to their store.
* The player with the most seeds in their store wins.

## Installation

Build & test application
```bash
mvn clean install
```

Running the Application
```bash
mvn spring-boot:run
```

## Swagger Link
* Application should be started to view swagger link
```bash
http://localhost:8081/swagger-ui/#/
```

## DB Usage
* This application uses embedded mongo DB

```properties
host=localhost
port=27017
database=kalah-db
```

## Endpoint Usage
* Endpoint to start game(player is non-mandatory field which will tell who will start first)
```yaml
POST: http://localhost:8081/games/start?player=PLAYER_FIRST
``` 

* Endpoint to sow seeds
```yaml
PUT: http://localhost:8081/games/{gameId}/pits/{pitId}
```

* Endpoint to get game status
```yaml
GET: http://localhost:8081/games/{gameId}
```

## Reference
* For Game Rules: https://en.wikipedia.org/wiki/Kalah