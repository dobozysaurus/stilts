/*
 * Copyright 2011 Red Hat, Inc, and individual contributors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.projectodd.stilts.stomp.protocol.server;

import org.jboss.netty.buffer.ChannelBuffer;
import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.stomp.protocol.DefaultStompMessage;
import org.projectodd.stilts.stomp.spi.Headers;
import org.projectodd.stilts.stomp.spi.StompMessageFactory;

public class DefaultStompMessageFactory implements StompMessageFactory {
    
    public final static DefaultStompMessageFactory INSTANCE = new DefaultStompMessageFactory();

    @Override
    public StompMessage createMessage(Headers headers, ChannelBuffer content, boolean isError) {
        return new DefaultStompMessage( headers, content, isError); 
    }

}
