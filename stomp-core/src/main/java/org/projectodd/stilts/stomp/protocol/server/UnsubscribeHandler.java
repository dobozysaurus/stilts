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

import org.jboss.netty.channel.ChannelHandlerContext;
import org.projectodd.stilts.StompException;
import org.projectodd.stilts.stomp.protocol.StompFrame;
import org.projectodd.stilts.stomp.protocol.StompFrame.Command;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;
import org.projectodd.stilts.stomp.spi.StompProvider;

public class UnsubscribeHandler extends AbstractControlFrameHandler {

    public UnsubscribeHandler(StompProvider server, ConnectionContext context) {
        super( server, context, Command.UNSUBSCRIBE );
    }

    @Override
    public void handleControlFrame(ChannelHandlerContext channelContext, StompFrame frame) {
        String destinationOrId = frame.getHeader( Header.DESTINATION );
        if ( destinationOrId == null ) {
            destinationOrId = frame.getHeader( Header.ID );
        }
        
        if ( destinationOrId == null ) {
            sendError( channelContext, "Must supply 'destination' or 'id' header for UNSUBSCRIBE", frame );
            return;
        }
        
        try {
            getStompConnection().unsubscribe( destinationOrId, frame.getHeaders() );
        } catch (StompException e) {
            sendError( channelContext, e.getMessage(), frame );
        }
    }
    
}
