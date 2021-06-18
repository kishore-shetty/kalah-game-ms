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

package com.backbase.kalah.utils;

import com.backbase.kalah.constants.KalahConstants;
import com.backbase.kalah.exception.ErrorTypeEnum;
import com.backbase.kalah.exception.KalahException;
import com.backbase.kalah.model.KalahGame;
import com.backbase.kalah.model.PlayerEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class ValidationUtils {

    private ValidationUtils() {
    }

    /**
     * While starting game if player to start with  is empty or null, Defaulting it to PLAYER_FIRST.
     * @param player - Player who who will start the kalah game
     * @return player - Player who who will start the kalah game
     */
    public static String validateStartKalahGameInput(String player) {
        if(StringUtils.isBlank(player)){
            log.error("Player - {} to start is empty or null. So starting with PLAYER FIRST", player);
            player = PlayerEnum.PLAYER_FIRST.toString();
        }

        return player;
    }

    /**
     * validates the gameId for blank and null.
     * validates the pitId for range
     * @param gameId - UniqueId to distinguish game
     * @param pitId  - Index of the pit
     */
    public static void validateSowInputs(String gameId, int pitId) throws KalahException{

        if(StringUtils.isBlank(gameId)){
            log.error("Empty or Null gameId - {}", gameId);
            throw new KalahException(ErrorTypeEnum.KGVE_01);
        }

        if(pitId < 1
                || pitId == KalahConstants.PLAYER1_STORE
                || pitId >= KalahConstants.PLAYER2_STORE){
            log.error("Null or empty pitId - {}", pitId);
            throw new KalahException(ErrorTypeEnum.KGVE_02);
        }

    }

    /**
     * validates the gameId for blank and null.
     * @param gameId - UniqueId to distinguish game
     */
    public static void validateGameId(String gameId) throws KalahException{

        if(StringUtils.isBlank(gameId)){
            log.error("Empty or Null gameId - {}", gameId);
            throw new KalahException(ErrorTypeEnum.KGVE_01);
        }

    }

    /**
     * validates of Kalah Game already End or not
     * @param kalahGame - Object that is saved in mongo
     * @throws KalahException - User defined exception
     */
    public static void validateKalahGame(KalahGame kalahGame) throws KalahException {
        if(KalahConstants.STATUS_END.equals(kalahGame.getGameStatus())){
            log.error("Game already ended for gameId {}", kalahGame.getId());
            throw new KalahException(ErrorTypeEnum.KGVE_03);
        }
    }
}
