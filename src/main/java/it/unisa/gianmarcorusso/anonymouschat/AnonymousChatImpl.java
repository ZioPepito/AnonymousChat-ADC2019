package it.unisa.gianmarcorusso.anonymouschat;

import java.net.InetAddress;
import java.util.ArrayList;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.futures.FutureDirect;
import net.tomp2p.p2p.Peer;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.rpc.ObjectDataReply;
import net.tomp2p.storage.Data;
import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.PeerBuilderDHT;

public class AnonymousChatImpl implements AnonymousChat{
	final private Peer peer;
	final private PeerDHT _dht;
	final private int DEFAULT_MASTER_PORT=4000;
	private PeerAddress myID;
	private ArrayList<String> joinedRooms;

	public AnonymousChatImpl(int _id, String _master_peer, final MessageListener _listener) throws Exception{
		
		joinedRooms = new ArrayList<String>();
		
		peer= new PeerBuilder(Number160.createHash(_id)).ports(DEFAULT_MASTER_PORT+_id).start();
		_dht = new PeerBuilderDHT(peer).start();
		
		FutureBootstrap fB = peer.bootstrap().inetAddress(InetAddress.getByName(_master_peer)).ports(DEFAULT_MASTER_PORT).start();
		fB.awaitUninterruptibly();
		
		if(fB.isSuccess()) {
			peer.discover().peerAddress(fB.bootstrapTo().iterator().next()).start().awaitUninterruptibly();
			myID = _dht.peer().peerAddress();
		} else {
			throw new Exception("Error in master peer bootstrap!");
		}
		
		peer.objectDataReply(new ObjectDataReply() {			
			public Object reply(PeerAddress sender, Object request) throws Exception {
				return _listener.parseMessage(request);
			}
		});
	}

	public boolean createRoom(String _room_name) {
		try {
			Room room = new Room(_room_name, myID);
			FutureGet fGet = _dht.get(Number160.createHash(_room_name)).start();
			fGet.awaitUninterruptibly();
			if(fGet.isSuccess() && fGet.isEmpty()) {
				_dht.put(Number160.createHash(_room_name)).data(new Data(room)).start().awaitUninterruptibly();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean joinRoom(String _room_name) {
		try {
			FutureGet fGet = _dht.get(Number160.createHash(_room_name)).start();
			fGet.awaitUninterruptibly();
			if(fGet.isSuccess()) {
				if(fGet.isEmpty()) return false;
				Room room; 
				room = (Room) fGet.dataMap().values().iterator().next().object();
				if(!room.getPeers().contains(peer.peerAddress())) {
					room.addPeer(peer.peerAddress());
					_dht.put(Number160.createHash(_room_name)).data(new Data(room)).start().awaitUninterruptibly();
					joinedRooms.add(room.getName());
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean leaveRoom(String _room_name) {
		try {
			FutureGet fGet = _dht.get(Number160.createHash(_room_name)).start();
			fGet.awaitUninterruptibly();
			if(fGet.isSuccess()) {
				if(fGet.isEmpty()) return false;
				Room room; 
				room = (Room) fGet.dataMap().values().iterator().next().object();
				if(room.getPeers().contains(peer.peerAddress())) {
					room.removePeer(peer.peerAddress());
					_dht.put(Number160.createHash(_room_name)).data(new Data(room)).start().awaitUninterruptibly();
					joinedRooms.remove(room.getName());
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean sendMessage(String _room_name, String _text_message) {
		try {
			FutureGet fGet = _dht.get(Number160.createHash(_room_name)).start();
			fGet.awaitUninterruptibly();
			if(fGet.isSuccess()) {
				if(fGet.isEmpty()) return false;
				Room room;
				room = (Room) fGet.dataMap().values().iterator().next().object();
				if(room.getPeers().contains(peer.peerAddress())) {
					for(PeerAddress p : room.getPeers()) {
						FutureDirect futureDirect = _dht.peer().sendDirect(p).object(_text_message+" (in room "+_room_name+")").start();
						futureDirect.awaitUninterruptibly();
					}
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean deleteRoom(String _room_name) {
		try {
			FutureGet fGet = _dht.get(Number160.createHash(_room_name)).start();
			fGet.awaitUninterruptibly();
			if(fGet.isSuccess()) {
				if(fGet.isEmpty()) return false;
				Room room; 
				room = (Room) fGet.dataMap().values().iterator().next().object();
				if(!room.getCreator().equals(myID)) return false;
				joinedRooms.remove(room);
				return _dht.remove(Number160.createHash(_room_name)).start().awaitUninterruptibly().isSuccess();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean leaveChat() {
		_dht.peer().announceShutdown().start().awaitUninterruptibly();
		return true;
	}
	
	public ArrayList<String> getJoinedRooms() {
		//before returning joined rooms checks if some room ha been deleted
		for(String room : joinedRooms) {
			FutureGet fGet = _dht.get(Number160.createHash(room)).start();
			fGet.awaitUninterruptibly();
			if(fGet.isSuccess() && fGet.isEmpty()) joinedRooms.remove(room);
		}
		return joinedRooms;
	}
}
