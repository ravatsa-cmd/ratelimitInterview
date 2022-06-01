package com.sample.ratelimiter.featuremanager;

/**
 * List down features.
 */
public enum Features
{
    @FeatureGroup("RateLimiter")
    @Feature(id = "abc", name = "Name of feature", description = "description")
    @FeaturePhase(FeaturePhase.Phase.Production)
    firstFeature
}
