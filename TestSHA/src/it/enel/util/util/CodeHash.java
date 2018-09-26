package it.enel.util.util;

public enum CodeHash {
	MD2    ("MD2"), 
	MD5    ("MD5"), 
	SHA1   ("SHA1"), 
	SHA224 ("SHA-224"), 
	SHA256 ("SHA-256"), 
	SHA384 ("SHA-384"), 
	SHA512 ("SHA-512");
	
	private final String type;
	
	CodeHash(String type) {
		this.type = type;
	}
	
	public String type() { return type; }
}
