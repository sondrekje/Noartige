package no.artige.handlers.data;

public class PlayerData {

	private int rank;
	private String ip;
	
	public PlayerData(int rank, String ip) {
		this.rank = rank;
		this.ip = ip;
	}
	
	public void setRank(int rank) {
		this.rank = rank;
	}
	
	public int getRank() {
		return this.rank;
	}
	
	public void setIP(String ip) {
		this.ip = ip;
	}
	
	public String getIP() {
		return this.ip;
	}
}
