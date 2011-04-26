package uk.ac.open.kmi.uciad.helper;

public class ActorAgentSetting {
	private String IP;
	private ActorAgent actorAgent;

	public void setIP(String iP) {
		this.IP = iP;
	}

	public String getIP() {
		return IP;
	}

	public void setActorAgent(ActorAgent actorAgent) {
		this.actorAgent = actorAgent;
	}

	public ActorAgent getActorAgent() {
		return actorAgent;
	}
}
