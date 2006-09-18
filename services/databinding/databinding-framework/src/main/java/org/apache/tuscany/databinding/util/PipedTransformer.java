/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.apache.tuscany.databinding.util;

import org.apache.tuscany.databinding.DataPipe;
import org.apache.tuscany.databinding.PushTransformer;
import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.databinding.PullTransformer;

/**
 *
 * @param <S>
 * @param <I>
 * @param <R>
 */
public class PipedTransformer<S, I, R> implements PullTransformer<S, R> {
    private PushTransformer<S, I> pusher;

    private DataPipe<I, R> pipe;

    /**
     * @param pumper
     * @param pipe
     */
    public PipedTransformer(PushTransformer<S, I> pumper, DataPipe<I, R> pipe) {
        super();
        this.pusher = pumper;
        this.pipe = pipe;
    }

    public R transform(S source, TransformationContext context) {
        pusher.transform(source, pipe.getSink(), context);
        return pipe.getResult();
    }

    public String getSourceBinding() {
        return pusher.getSourceBinding();
    }

    public String getTargetBinding() {
        return pipe.getTargetBinding();
    }

    public int getWeight() {
        return pusher.getWeight() + pipe.getWeight();
    }

}
