package com.sample.config;

import com.typesafe.config.Config;

import java.io.Serializable;

public class ApplicationConfig implements Serializable
{
    private Config config;

    public ApplicationConfig (Config config)
    {
        this.config = config;
    }

    public String getConfigValue(String configKey)
    {
        return config.getString(configKey);
    }
}
