package com.turnitin.serverlessv1demo.api;

import com.turnitin.serverlessv1demo.api.dto.Hello;
import com.turnitin.serverlessv1demo.core.service.ServerlessV1DemoService;
import com.turnitin.commons.TurnitinContext;
import com.turnitin.commons.http.HttpMethod;
import com.turnitin.commons.lambda.ApiGatewayLambda;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerlessV1DemoGet extends ApiGatewayLambda<Hello> {

    private ServerlessV1DemoService serverlessV1DemoService;

    // This constructor is for regular flow
    public ServerlessV1DemoGet() {
        this.ctx = TurnitinContext.builder().build();
        this.serverlessV1DemoService = new ServerlessV1DemoService(ctx);
    }

    // This Constructor is use in tests if you want to mock the context or parts there of.
    public ServerlessV1DemoGet(TurnitinContext ctx) {
        this.ctx = ctx;
        this.serverlessV1DemoService = new ServerlessV1DemoService(ctx);
    }

    @Override
    protected Hello handleMethod() throws Exception {
        Hello hello = new Hello();
        String who = ctx.getKeyFromPathParams(input, "who");
        hello.setResponse(serverlessV1DemoService.doHello(who));
        return hello;
    }

    @Override
    protected List<String> getSupportedHttpMethods() {
        return Arrays.asList(HttpMethod.GET.name());
    }
}
