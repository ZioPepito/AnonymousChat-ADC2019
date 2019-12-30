package it.unisa.gianmarcorusso.anonymouschat;

import java.io.Serializable;
import java.util.HashSet;
import net.tomp2p.peers.PeerAddress;

public class Room implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String name;
	private HashSet<PeerAddress> peers;
	private PeerAddress creator;

	public Room(String name, PeerAddress creator) {
		this.name = name;
		this.creator = creator;
		peers = new HashSet<PeerAddress>();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HashSet<PeerAddress> getPeers() {
		return peers;
	}

	public void setPeers(HashSet<PeerAddress> peers) {
		this.peers = peers;
	}
	
	public boolean addPeer(PeerAddress pAdd) {
		return peers.add(pAdd);
	}
	
	public boolean removePeer(PeerAddress pAdd) {
		return peers.remove(pAdd);
	}

	public PeerAddress getCreator() {
		return creator;
	}

	public void setCreator(PeerAddress creator) {
		this.creator = creator;
	}

}
