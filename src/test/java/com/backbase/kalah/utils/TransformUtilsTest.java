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
import com.backbase.kalah.model.KalahGame;
import com.backbase.kalah.response.KalahGameResponse;
import com.backbase.kalah.response.KalahSowResponse;
import com.backbase.kalah.response.KalahStatusResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

class TransformUtilsTest {

    private KalahGame kalahGame;

    private MockHttpServletRequest httpServletRequest;

    @BeforeEach
    void setUp() {
        httpServletRequest = new MockHttpServletRequest();
        ServletRequestAttributes servletRequestAttributes = new ServletRequestAttributes(httpServletRequest);
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);
        kalahGame = new KalahGame();
        kalahGame.setId("TEST123");
    }

    @Test
    void getKalahGameResponse() {
        KalahGameResponse response = TransformUtils.getKalahGameResponse(kalahGame);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(kalahGame.getId(), response.getId());
        Assertions.assertNotNull(response.getUri());
    }

    @Test
    void getKalahSowResponse() {
        KalahSowResponse response = TransformUtils.getKalahSowResponse(kalahGame);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(kalahGame.getId(), response.getId());
        Assertions.assertNotNull(response.getUri());
        Assertions.assertNotNull(response.getStatus());
        Assertions.assertEquals(14, response.getStatus().size());
    }

    @Test
    void getKalahStatusResponse() {
        KalahStatusResponse response = TransformUtils.getKalahStatusResponse(kalahGame);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(kalahGame.getId(), response.getId());
        Assertions.assertNotNull(response.getUri());
        Assertions.assertEquals(KalahConstants.STATUS_ON, response.getGameStatus());
        Assertions.assertEquals(KalahConstants.AWAITED, response.getGameWinner());

    }
}