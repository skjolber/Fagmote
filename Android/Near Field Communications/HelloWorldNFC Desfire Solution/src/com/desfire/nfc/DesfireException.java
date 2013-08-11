package com.desfire.nfc;

public class DesfireException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final byte status;
	
	public DesfireException(byte status) {
		super();
		
		this.status = status;
	}

	public DesfireException(byte status, String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
		this.status = status;
	}

	public DesfireException(byte status, String detailMessage) {
		super(detailMessage);
		this.status = status;
	}

	public DesfireException(byte status, Throwable throwable) {
		super(throwable);
		this.status = status;
	}

	public byte getStatus() {
		return status;
	}
}
