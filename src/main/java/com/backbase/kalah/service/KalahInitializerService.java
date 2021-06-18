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

package com.backbase.kalah.service;

import com.backbase.kalah.constants.KalahConstants;
import com.backbase.kalah.exception.ErrorTypeEnum;
import com.backbase.kalah.exception.KalahException;
import com.backbase.kalah.model.KalahGame;
import com.backbase.kalah.model.PlayerEnum;
import com.backbase.kalah.repository.KalahGameRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class KalahInitializerService {

    private final KalahGameRepository kalahGameRepository;

    public KalahInitializerService(KalahGameRepository kalahGameRepository) {
        this.kalahGameRepository = kalahGameRepository;
    }

    /**
     * This service initializes KalahGame with given player to start with
     * This service saves the KalahGame to mongoDB
     * @param player - Player who who will start the kalah game
     * @return KalahGame - Object that is saved in mongo
     */
    public KalahGame initAndStartGame(String player) {
        KalahGame kalahGame = new KalahGame(KalahConstants.INITIAL_HOUSE_SEEDS, PlayerEnum.valueOf(player));
        return kalahGameRepository.save(kalahGame);
    }


    /**
     * This service loads the KalahGame for a given gameId
     * @param gameId - UniqueId to distinguish game
     * @return KalahGame - Object that is saved in mongo
     */
    public KalahGame getGameById(String gameId) {
        Optional<KalahGame> optionalKalahGame = kalahGameRepository.findById(gameId);
        if(!optionalKalahGame.isPresent()){
            throw new KalahException(ErrorTypeEnum.KGE_01);
        }
        return optionalKalahGame.get();
    }
}
