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


package com.github.wuchunhuan.gprpc_java.client;

import com.google.protobuf.RpcCallback;

public abstract class AsyncRpcCallback<T> implements RpcCallback<T> {
    private ClientRpcController controller;

    public AsyncRpcCallback(ClientRpcController controller) {
        this.controller = controller;
    }

    final public void run(T message) {
        call(controller, message);
    }

    public abstract void call(ClientRpcController controller, T message);
}
