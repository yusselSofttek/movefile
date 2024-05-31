package com.turnitin.serverlessv1demo.core.service;

import com.turnitin.commons.TurnitinContext;

// This is the kind of server in which the business logic might go
public class ServerlessV1DemoService {

    TurnitinContext ctx;

    public ServerlessV1DemoService(TurnitinContext ctx) {
        this.ctx = ctx;
    }

    public String doHello(String who) {
        return "Hello " + who;
    }

}
