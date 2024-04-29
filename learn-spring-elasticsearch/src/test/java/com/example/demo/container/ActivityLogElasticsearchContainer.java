package com.example.demo.container;

import org.testcontainers.elasticsearch.ElasticsearchContainer;

public class ActivityLogElasticsearchContainer extends ElasticsearchContainer {
    private static final String ES_IMAGE = "elasticsearch:8.13.0";

    public ActivityLogElasticsearchContainer() {
        super(ES_IMAGE);
        addFixedExposedPort(9200, 9200);
        addEnv("discovery.type", "single-node");
        // todo: how to setup SSL for client.
        addEnv("xpack.security.enabled", "false");
        addEnv("cluster.name", "elasticsearch");
    }
}
