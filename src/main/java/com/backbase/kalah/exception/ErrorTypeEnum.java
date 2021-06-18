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

package com.backbase.kalah.exception;

public enum ErrorTypeEnum {

    KGVE_01("KGVE_01", "Invalid gameId"),
    KGVE_02("KGVE_02", "Invalid pitId"),
    KGVE_03("KGVE_03", "Game Ended"),

    KGE_01("KGE_01","gameId not found. Start the game"),
    KGE_02("KGE_02","Incorrect pitId selected to sow. Choose correct one"),
    KGE_03("KGE_03","Empty pitId selected to sow. Choose the one with seeds"),

    KGE_99("INTERNAL_SERVER_ERROR", "Please contact support team");

    String errorCode;

    String errorMessage;

    ErrorTypeEnum(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
