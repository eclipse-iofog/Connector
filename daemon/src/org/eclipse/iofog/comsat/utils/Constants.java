/*
 * *******************************************************************************
 *  * Copyright (c) 2018 Edgeworx, Inc.
 *  *
 *  * This program and the accompanying materials are made available under the
 *  * terms of the Eclipse Public License v. 2.0 which is available at
 *  * http://www.eclipse.org/legal/epl-2.0
 *  *
 *  * SPDX-License-Identifier: EPL-2.0
 *  *******************************************************************************
 *
 */

package org.eclipse.iofog.comsat.utils;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.nio.charset.StandardCharsets;

/**
 * Created by saeid on 6/27/16.
 */
public class Constants {

    public static final String CONFIG_FILENAME = "/etc/comsat/configs.json";
    public static final String SETTINGS_FILENAME = "/etc/comsat/comsat.conf";
    public static final byte[] BEAT = "BEAT".getBytes(StandardCharsets.US_ASCII);
    public static final byte[] AUTHORIZED = "AUTHORIZED".getBytes(StandardCharsets.US_ASCII);
    public static final String CERTITICATE_FILENAME = "/etc/comsat/server-cert.pem";
    public static final String KEY_FILENAME = "/etc/comsat/server-key.pem";
    public static final String LOG_FILENAME = "/var/log//comsat/comsat.log";
    public static final String VERSION = "2.0.2";
    
    public static final String API_DIRECT_REQUEST = "/api/v2/direct";
    public static final String API_DIRECT_ADD = "/api/v2/direct/add";
    public static final String API_DIRECT_REMOVE = "/api/v2/direct/remove";
    public static final String API_PORT_ADD = "/api/v2/mapping/add";
    public static final String API_PORT_REMOVE = "/api/v2/mapping/remove";
    public static final String API_COMMAND_LINE = "/api/v2/commandline";
    public static final String API_STATUS = "/api/v2/status";

    public static EventLoopGroup bossGroup = new NioEventLoopGroup(10);
    public static EventLoopGroup workerGroup = new NioEventLoopGroup(200);
}
