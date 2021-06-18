/*
 *  /*
 *  * Copyright (c) 2021 /  Kishore B Shetty
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *          http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.backbase.kalah.controller;

import com.backbase.kalah.exception.KalahException;
import com.backbase.kalah.model.KalahGame;
import com.backbase.kalah.response.KalahGameResponse;
import com.backbase.kalah.response.KalahSowResponse;
import com.backbase.kalah.response.KalahStatusResponse;
import com.backbase.kalah.service.KalahInitializerService;
import com.backbase.kalah.service.KalahSowService;
import com.backbase.kalah.utils.TransformUtils;
import com.backbase.kalah.utils.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class KalahController {

    private final KalahInitializerService kalahInitializerService;

    private final KalahSowService kalahSowService;

    public KalahController(KalahInitializerService kalahInitializerService,
                           KalahSowService kalahSowService) {
        this.kalahInitializerService = kalahInitializerService;
        this.kalahSowService = kalahSowService;
    }

    /**
     * This endpoint will initialize the kalah game with 6 seeds
     * Non mandatory Query parameter player tells who will start first.
     * If not provided PLAYER_FIRST will start first
     * @param player - Player who who will start the kalah game
     * @return KalahGameResponse - Response contains uri and id of game
     */
    @PostMapping(value = "/games", produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public KalahGameResponse startKalahGame(@RequestParam(required = false) String player) throws KalahException{
        log.info("IN: KalahController - startKalahGame.  Player to start the game will be {}", player);

        player = ValidationUtils.validateStartKalahGameInput(player);
        KalahGame kalahGame = kalahInitializerService.initAndStartGame(player);

        log.info("OUT: KalahController - startKalahGame");
        return TransformUtils.getKalahGameResponse(kalahGame);
    }

    /**
     * This endpoint will sow the seeds to right of the pit
     * @param gameId - UniqueId to distinguish game
     * @param pitId - Index of the pit
     * @return KalahSowResponse - response with id uri and status with pit index and seed count
     */
    @PutMapping(value = "/games/{gameId}/pits/{pitId}", produces= MediaType.APPLICATION_JSON_VALUE)
    public KalahSowResponse sowSeeds(@PathVariable(value = "gameId") String gameId,
                                     @PathVariable(value = "pitId") int pitId) throws KalahException {
        log.info("IN: KalahController - sowSeeds with gameId: {} and pitId: {}", gameId, pitId);

        ValidationUtils.validateSowInputs(gameId, pitId);
        KalahGame kalahGame = kalahInitializerService.getGameById(gameId);
        ValidationUtils.validateKalahGame(kalahGame);
        KalahGame kalahGameAfterSowing = kalahSowService.sowSeeds(kalahGame, pitId);

        log.info("OUT: KalahController - sowSeeds");
        return TransformUtils.getKalahSowResponse(kalahGameAfterSowing);
    }

    /**
     * This endpoint will give the current status of game
     * @param gameId - UniqueId to distinguish game
     * @return KalahStatusResponse - current status of game
     */
    @GetMapping(value = "/games/{gameId}", produces= MediaType.APPLICATION_JSON_VALUE)
    public KalahStatusResponse getKalahGameStatus(@PathVariable(value = "gameId") String gameId) throws KalahException {
        log.info("IN: KalahController - getKalahGameStatus with gameId: {}", gameId);

        ValidationUtils.validateGameId(gameId);
        KalahGame kalahGame = kalahInitializerService.getGameById(gameId);

        log.info("OUT: KalahController - getKalahGameStatus");
        return TransformUtils.getKalahStatusResponse(kalahGame);
    }

}
