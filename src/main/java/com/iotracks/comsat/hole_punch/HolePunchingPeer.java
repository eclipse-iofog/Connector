package com.iotracks.comsat.hole_punch;

import io.netty.channel.ChannelHandlerContext;

public class HolePunchingPeer {
	
	private String localIp;
	private String certificate;
	private ChannelHandlerContext ctx;
	
	public String getLocalIp() {
		return localIp;
	}
	public void setLocalIp(String localIp) {
		this.localIp = localIp;
	}
	public ChannelHandlerContext getCtx() {
		return ctx;
	}
	public void setCtx(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}

	public HolePunchingPeer(String localIp, ChannelHandlerContext ctx, String certificate) {
		this.localIp = localIp;
		this.ctx = ctx;
		this.certificate = certificate;
	}
	
	public boolean equals(Object other) {
		HolePunchingPeer o = (HolePunchingPeer) other;
		return o.ctx.equals(this.ctx) && o.localIp.equals(this.localIp);
	}
	
	public String getCertificate() {
		return certificate;
	}
	public void setCertificate(String certificate) {
		this.certificate = certificate;
	}

}
