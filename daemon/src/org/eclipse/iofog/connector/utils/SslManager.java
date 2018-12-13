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

package org.eclipse.iofog.connector.utils;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class SslManager {

	private static SslContext sslContext;

	public static SslContext getSslContext() {
		return sslContext;
	}

	public static void initSslContext(boolean isDevMode) throws Exception {
		if (!isDevMode) {
			sslContext = SslContextBuilder.forServer(
				new File(Constants.CERTITICATE_FILENAME),
				new File(Constants.KEY_FILENAME))
				.build();
		}
	}
	
	public static SSLContext getSSLContext() throws Exception {        
	    byte[] certBytes = fileToByte(Constants.CERTITICATE_FILENAME);
	    
	    X509Certificate cert = generateCertificateFromDER(certBytes);
	    
	    TrustManagerFactory tmf = TrustManagerFactory
	    	    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
	    	KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
	    	ks.load(null); // You don't need the KeyStore instance to come from a file.
	    	ks.setCertificateEntry("caCert", cert);

	    	tmf.init(ks);

	    	SSLContext sslContext = SSLContext.getInstance("TLS");
	    	sslContext.init(null, tmf.getTrustManagers(), null);
	    return sslContext;
	}
	
	protected static X509Certificate generateCertificateFromDER(byte[] certBytes) throws Exception {
	    CertificateFactory factory = CertificateFactory.getInstance("X.509");

	    return (X509Certificate)factory.generateCertificate(new ByteArrayInputStream(certBytes));      
	}
	
	private static byte[] fileToByte(String fileName) {
		FileInputStream fileInputStream = null;

        File file = new File(fileName);

        byte[] bFile = new byte[(int) file.length()];

	    try {
			fileInputStream = new FileInputStream(file);
		    fileInputStream.read(bFile);
		    fileInputStream.close();
		    return bFile;
		} catch (Exception e) {
			return null;
		}
	}

}
