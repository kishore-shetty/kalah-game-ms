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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ValidationUtilsTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void validateStartKalahGameInput() {
        String response = ValidationUtils.validateStartKalahGameInput(null);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(PlayerEnum.PLAYER_FIRST.toString(), response);

        response = ValidationUtils.validateStartKalahGameInput(PlayerEnum.PLAYER_SECOND.toString());
        Assertions.assertNotNull(response);
        Assertions.assertEquals(PlayerEnum.PLAYER_SECOND.toString(), response);

        response = ValidationUtils.validateStartKalahGameInput(PlayerEnum.PLAYER_FIRST.toString());
        Assertions.assertNotNull(response);
        Assertions.assertEquals(PlayerEnum.PLAYER_FIRST.toString(), response);
    }

    @Test
    void validateSowInputs() {
        KalahException kalahException = Assertions.assertThrows(KalahException.class, () -> ValidationUtils.validateSowInputs(null, 0));
        Assertions.assertEquals(ErrorTypeEnum.KGVE_01.getErrorCode(), kalahException.getErrorCode());
        kalahException = Assertions.assertThrows(KalahException.class, () -> ValidationUtils.validateSowInputs("G1001", 0));
        Assertions.assertEquals(ErrorTypeEnum.KGVE_02.getErrorCode(), kalahException.getErrorCode());
        kalahException = Assertions.assertThrows(KalahException.class, () -> ValidationUtils.validateSowInputs("G1001", KalahConstants.PLAYER1_STORE));
        Assertions.assertEquals(ErrorTypeEnum.KGVE_02.getErrorCode(), kalahException.getErrorCode());
        kalahException = Assertions.assertThrows(KalahException.class, () -> ValidationUtils.validateSowInputs("G1001", KalahConstants.PLAYER2_STORE));
        Assertions.assertEquals(ErrorTypeEnum.KGVE_02.getErrorCode(), kalahException.getErrorCode());

    }

    @Test
    void validateGameId() {
        KalahException kalahException = Assertions.assertThrows(KalahException.class, () -> ValidationUtils.validateGameId(null));
        Assertions.assertEquals(ErrorTypeEnum.KGVE_01.getErrorCode(), kalahException.getErrorCode());
        Assertions.assertDoesNotThrow(() -> ValidationUtils.validateGameId("G1001"));
    }

    @Test
    void validateKalahGame() {
        KalahGame kalahGame = new KalahGame(PlayerEnum.PLAYER_FIRST);
        kalahGame.setGameStatus(KalahConstants.STATUS_END);
        KalahException kalahException = Assertions.assertThrows(KalahException.class, () -> ValidationUtils.validateKalahGame(kalahGame));
        Assertions.assertEquals(ErrorTypeEnum.KGVE_03.getErrorCode(), kalahException.getErrorCode());
    }
}