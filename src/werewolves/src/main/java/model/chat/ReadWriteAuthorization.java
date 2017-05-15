package model.chat;

public class ReadWriteAuthorization {
	private boolean read;
	private boolean write;

	public ReadWriteAuthorization(boolean read, boolean write) {
		this.read = read;
		this.write = write;
	}

	public boolean getRead() {
		return this.read;
	}

	public void read() {
		this.read = true;
	}

	public boolean getWrite() {
		return this.write;
	}

	public void readWrite() {
		this.read = true;
		this.write = true;
	}

	public String toJSON() {
		return "{ \"read\": " + this.read + ", \"write\": " + this.write + "}";
	}
}
