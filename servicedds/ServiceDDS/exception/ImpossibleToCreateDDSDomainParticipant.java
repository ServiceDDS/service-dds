package ServiceDDS.exception;

public class ImpossibleToCreateDDSDomainParticipant extends Exception {
	String peerName;
	public ImpossibleToCreateDDSDomainParticipant(String pn) {
		super();
		this.peerName = pn;
	}
}
