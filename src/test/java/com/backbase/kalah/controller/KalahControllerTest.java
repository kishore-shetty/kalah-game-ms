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

import com.backbase.kalah.constants.KalahConstants;
import com.backbase.kalah.exception.ErrorTypeEnum;
import com.backbase.kalah.exception.KalahException;
import com.backbase.kalah.model.KalahGame;
import com.backbase.kalah.model.PlayerEnum;
import com.backbase.kalah.response.KalahGameResponse;
import com.backbase.kalah.response.KalahSowResponse;
import com.backbase.kalah.response.KalahStatusResponse;
import com.backbase.kalah.service.KalahInitializerService;
import com.backbase.kalah.service.KalahSowService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(MockitoExtension.class)
class KalahControllerTest {

    private KalahController subjectUnderTest;

    @Mock
    private KalahInitializerService kalahInitializerService;

    @Mock
    private KalahSowService kalahSowService;

    private MockHttpServletRequest httpServletRequest;

    @BeforeEach
    void setUp() {
        httpServletRequest = new MockHttpServletRequest();
        ServletRequestAttributes servletRequestAttributes = new ServletRequestAttributes(httpServletRequest);
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);
        subjectUnderTest = new KalahController(kalahInitializerService, kalahSowService);
    }

    @Test
    void startKalahGame() {
        KalahGame kalahGame = new KalahGame(PlayerEnum.PLAYER_FIRST);
        kalahGame.setId("TEST123");
        Mockito
                .when(kalahInitializerService.initAndStartGame(Mockito.anyString()))
                .thenReturn(kalahGame);
        KalahGameResponse response = subjectUnderTest.startKalahGame(PlayerEnum.PLAYER_FIRST.toString());
        Assertions.assertNotNull(response);
        Assertions.assertEquals("TEST123", response.getId());

    }

    @Test
    void sowSeeds() {
        KalahException kalahException = Assertions.assertThrows(KalahException.class, () -> subjectUnderTest.sowSeeds("", 0));
        Assertions.assertEquals(ErrorTypeEnum.KGVE_01.getErrorCode(), kalahException.getErrorCode());

        kalahException = Assertions.assertThrows(KalahException.class, () -> subjectUnderTest.sowSeeds("G1001", 0));
        Assertions.assertEquals(ErrorTypeEnum.KGVE_02.getErrorCode(), kalahException.getErrorCode());

        KalahGame kalahGame = new KalahGame(PlayerEnum.PLAYER_FIRST);
        kalahGame.setId("TEST123");
        kalahGame.setGameStatus(KalahConstants.STATUS_END);
        Mockito
                .when(kalahInitializerService.getGameById(Mockito.anyString()))
                .thenReturn(kalahGame);

        kalahException = Assertions.assertThrows(KalahException.class, () -> subjectUnderTest.sowSeeds("G1001", 2));
        Assertions.assertEquals(ErrorTypeEnum.KGVE_03.getErrorCode(), kalahException.getErrorCode());

        kalahGame.setGameStatus(KalahConstants.STATUS_ON);
        Mockito
                .when(kalahSowService.sowSeeds(Mockito.any(), Mockito.anyInt()))
                .thenReturn(kalahGame);
        KalahSowResponse response = subjectUnderTest.sowSeeds("G1001", 3);
        Assertions.assertNotNull(response);
        Assertions.assertEquals("TEST123", response.getId());

    }

    @Test
    void getKalahGameStatus() {
        KalahException kalahException = Assertions.assertThrows(KalahException.class, () -> subjectUnderTest.getKalahGameStatus(""));
        Assertions.assertEquals(ErrorTypeEnum.KGVE_01.getErrorCode(), kalahException.getErrorCode());

        KalahGame kalahGame = new KalahGame(PlayerEnum.PLAYER_FIRST);
        kalahGame.setGameWinner(PlayerEnum.PLAYER_FIRST.toString());
        Mockito
                .when(kalahInitializerService.getGameById(Mockito.anyString()))
                .thenReturn(kalahGame);
        KalahStatusResponse response = subjectUnderTest.getKalahGameStatus("T1001");
        Assertions.assertNotNull(response);
        Assertions.assertEquals(PlayerEnum.PLAYER_FIRST.toString(), response.getGameWinner());

    }
}