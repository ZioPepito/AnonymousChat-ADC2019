package it.unisa.gianmarcorusso.anonymouschat;

import junit.framework.TestCase;

public class AnonymousChatTest extends TestCase{

	AnonymousChatImpl master, p1, p2, p3;

	protected void setUp() throws Exception{
		master = new AnonymousChatImpl(0, "127.0.0.1", new MessageListenerImpl(0));
		p1 = new AnonymousChatImpl(1, "127.0.0.1", new MessageListenerImpl(1));
		p2 = new AnonymousChatImpl(2, "127.0.0.1", new MessageListenerImpl(2));
		p3 = new AnonymousChatImpl(3, "127.0.0.1", new MessageListenerImpl(3));
	}

	public void test() {

		//test create a room
		boolean fl = master.createRoom("AAA");
		assertTrue(fl);
		fl = p1.createRoom("BBB");
		assertTrue(fl);

		//test create a room with same name of existing room 
		fl = p2.createRoom("BBB");
		assertFalse(fl);

		//test join a room
		fl = master.joinRoom("AAA");
		assertTrue(fl);
		fl = p2.joinRoom("AAA");
		assertTrue(fl);

		fl = p1.joinRoom("BBB");
		assertTrue(fl);
		fl = p3.joinRoom("BBB");
		assertTrue(fl);

		//test join an inexistent room
		fl = p3.joinRoom("FFF");
		assertFalse(fl);

		//test send message in a room you joined
		fl = p1.sendMessage("BBB", "Hello bbb!");
		assertTrue(fl);

		//test send message in a room you didn't join
		fl = p1.sendMessage("AAA", "Hello aaa!");
		assertFalse(fl);

		//test send message in an inexistent room 
		fl = p1.sendMessage("FFF", "Hello fff!");
		assertFalse(fl);

		//test leave a room you joined
		fl = p3.leaveRoom("BBB");
		assertTrue(fl);

		//test leave a room you didn't join
		fl = p3.leaveRoom("AAA");
		assertFalse(fl);

		//test leave an inexistent room
		fl = p3.leaveRoom("FFF");
		assertFalse(fl);

		//test delete a room you created
		fl = p1.deleteRoom("BBB");
		assertTrue(fl);
		
		//test delete a room you dind't create
		fl = p1.deleteRoom("AAA");
		assertFalse(fl);
		
		//test delete an inexistent room
		fl = p1.deleteRoom("FFF");
		assertFalse(fl);
		
		//test leave chat
		fl = p1.leaveChat();
		assertTrue(fl);
		fl = p2.leaveChat();
		assertTrue(fl);
		fl = p3.leaveChat();
		assertTrue(fl);
		fl = master.leaveChat();
		assertTrue(fl);
	}

}
