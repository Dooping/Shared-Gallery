package sd.tp1.common;

import java.io.Serializable;

public class LamportClock implements Serializable{
    private static final long serialVersionUID = 0L;
	public String serverUrl;
	public int lamportNumber;
	
	public LamportClock(String serverUrl, int lamportNumber) {
		this.serverUrl = serverUrl;
		this.lamportNumber = lamportNumber;
	}
	
<<<<<<< HEAD
	public LamportClock() {}
	
//	public String getServerUrl() {
//		return serverUrl;
//	}
=======
	public String getServerUrl() {
		return serverUrl;
	}
>>>>>>> 0f4fe37ca6f95cc21633b7601f86a325cdc627a3
	
	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}
	
//	public int getLamportNumber() {
//		return lamportNumber;
//	}
	
	public void setLamportNumber(int lamportNumber) {
		this.lamportNumber = lamportNumber;
	}

	

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LamportClock other = (LamportClock) obj;
		if (lamportNumber != other.lamportNumber)
			return false;
		if (serverUrl == null) {
			if (other.serverUrl != null)
				return false;
		} else if (!serverUrl.equals(other.serverUrl))
			return false;
		return true;
	}

	public int compareTo(LamportClock other) {
		if (this.lamportNumber != other.lamportNumber)
	       return this.lamportNumber-other.lamportNumber;
		else
			return this.serverUrl.compareTo(other.serverUrl);
	} 

}
