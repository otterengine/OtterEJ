package net.xdefine.servlet.vo;

public class ResultObject {
	private boolean result;
	private String message;
	private boolean success;
	
	public boolean getResult() { return result; }
	public void setResult(boolean result) { this.result = result; }
	
	public String getMessage() { return message; }
	public void setMessage(String message) { this.message = message; }

	public boolean getSuccess() {
		return this.success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
}
