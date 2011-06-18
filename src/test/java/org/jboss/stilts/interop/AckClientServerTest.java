package org.jboss.stilts.interop;

import static org.junit.Assert.*;

import org.jboss.stilts.StompMessages;
import org.jboss.stilts.client.ClientSubscription;
import org.jboss.stilts.client.ClientTransaction;
import org.jboss.stilts.logging.SimpleLoggerManager.Level;
import org.jboss.stilts.spi.Subscription.AckMode;
import org.junit.Test;

public class AckClientServerTest extends AbstractClientServerTest {

    static {
        SERVER_ROOT_LEVEL = Level.TRACE;
        CLIENT_ROOT_LEVEL = Level.NONE;
    }

    @Test
    public void testClientTransaction() throws Exception {
        client.connect();

        ClientSubscription subscription1 = client.subscribe( "/queues/foo" ).withMessageHandler( accumulator( "one", false, true ) ).withAckMode( AckMode.CLIENT_INDIVIDUAL ).start();
        ClientSubscription subscription2 = client.subscribe( "/queues/foo" ).withMessageHandler( accumulator( "two" ) ).withAckMode( AckMode.AUTO ).start();
        
        ClientTransaction tx = client.begin();

        for (int i = 0; i < 10; ++i) {
            tx.send( StompMessages.createStompMessage( "/queues/foo", "What? " + i ) );
        }

        Thread.sleep( 1000 );
        assertTrue( accumulator( "one" ).isEmpty() );
        assertTrue( accumulator( "two" ).isEmpty() );
        tx.commit();
        Thread.sleep( 1000 );
        subscription1.unsubscribe();
        Thread.sleep( 1000 );
        subscription2.unsubscribe();
        client.disconnect();
        
        System.err.println( "===========================================" );
        System.err.println( "===========================================" );
        System.err.println( accumulator("one").messageIds() );
        System.err.println( accumulator("two").messageIds() );
        System.err.println( "===========================================" );
        System.err.println( "===========================================" );
        assertEquals( 10, accumulator( "two" ).size() );
        
        
    }
}