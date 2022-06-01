/*
    Copyright (c) 1996-2015 Ariba, Inc.
    All rights reserved. Patents pending.

    $Id$

    Responsible: mohammed.aehthesham
*/
package com.sample.ratelimiter.featuremanager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FeaturePhase
{
    public enum Phase {Development, Production};
    Phase value () default Phase.Development;
}

