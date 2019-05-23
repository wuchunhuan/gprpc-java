/*
 *
 * Copyright © 2019 Patrick Wu(Wu chunhuan)/wuchunhuan@gmail.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.wuchunhuan.gprpc_java.utils;

import java.util.HashMap;
import java.util.Map;

public class ErrInfo {
    private static final Map<Integer, String> errs = new HashMap<Integer, String>() {
        {
            put(0, "Rpc call success!");
            put(-1, "Request message too large!");
            put(-2, "Resolving remote address failed!");
            put(-3, "Connecting remote address failed!");
            put(-4, "Send request message failed!");
            put(-5, "Receive response header failed!");
            put(-6, "Not a rpc response!");
            put(-7, "Response message too large!");
            put(-8, "Receive response message failed!");
            put(-9, "Corrupted rpc data received!");
            put(-10, "Corrupted rpc response message received!");
            put(-11, "Error detail: ");
        }
    };

    public static String getErrStr(int errCode) {
        return errs.get(errCode);
    }
}
