package com.turnitin.serverlessv1demo.api;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.lambda.runtime.tests.EventLoader;
import com.turnitin.serverlessv1demo.TestContext;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ServerlessV1DemoGetTest {

    @Test
    void testWithNoInput() {
        final APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        final Context context = new TestContext();
        final ServerlessV1DemoGet lambda = new ServerlessV1DemoGet();
        final APIGatewayProxyResponseEvent response = lambda.handleRequest(event, context);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SC_METHOD_NOT_ALLOWED);
    }

    @Test
    void testWithInput() {
        final APIGatewayProxyRequestEvent event = EventLoader.loadEvent("APIGatewayRequestEvent-hello.json",
                                                                        APIGatewayProxyRequestEvent.class);
        final Context context = new TestContext();
        final ServerlessV1DemoGet lambda = new ServerlessV1DemoGet();
        final APIGatewayProxyResponseEvent response = lambda.handleRequest(event, context);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SC_OK);
    }
}
