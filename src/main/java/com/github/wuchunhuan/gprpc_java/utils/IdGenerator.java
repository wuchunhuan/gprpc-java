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

import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator {
    private AtomicInteger seq = new AtomicInteger(0);
    private static  IdGenerator instance = new IdGenerator();
    public static IdGenerator getInstance() {
        return instance;
    }

    public final int getId() {
        int current;
        int next;
        do {
            current = this.seq.get();
            next = current >= Integer.MAX_VALUE ? 0 : (current + 1);
        } while(!this.seq.compareAndSet(current, next));

        return current;
    }

    /*public static void main (String[] args) {
        int test1 = IdGenerator.getInstance().getId();
        int test2 = IdGenerator.getInstance().getId();
        int test3 = IdGenerator.getInstance().getId();
        int test4 = IdGenerator.getInstance().getId();
        System.out.println(test1+ "," + test2 + ","+ test3 + ","+ test4);
    }*/
}
