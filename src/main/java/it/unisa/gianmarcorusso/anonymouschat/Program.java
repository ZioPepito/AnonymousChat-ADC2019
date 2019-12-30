package it.unisa.gianmarcorusso.anonymouschat;

import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class Program {
	@Option(name="-m", aliases="--masterip", usage="the master peer ip address", required=true)
	private static String master;

	@Option(name="-id", aliases="--identifierpeer", usage="the unique identifier for this peer", required=true)
	private static int id;

	public Program() {}
	
	public static void main(String args[]) throws Exception{
		Program p = new Program();
		final CmdLineParser cmdLineParser = new CmdLineParser(p);
		try {
			cmdLineParser.parseArgument(args);
			TextIO textIO = TextIoFactory.getTextIO();
			TextTerminal terminal = textIO.getTextTerminal();
			AnonymousChatImpl aChatImpl = new AnonymousChatImpl(id, master, new MessageListenerImpl(id));
			
			terminal.printf("\nStarting peer id: %d on master node: %s\n",id, master);
			
			while(true) {
				printMenu(terminal);
				
				int option = textIO.newIntInputReader().withMaxVal(7).withMinVal(1).read("Option");
				
				switch (option) {
				case 1:
					terminal.printf("\nENTER ROOM NAME\n");
					String name1 = textIO.newStringInputReader()
					        .withDefaultValue("def-room-name")
					        .read("Name:");
					if(aChatImpl.createRoom(name1))
						terminal.printf("\nROOM %s SUCCESSFULLY CREATED\n",name1);
					else
						terminal.printf("\nERROR IN ROOM CREATION, RETRY\n");
					break;
					
				case 2:
					terminal.printf("\nENTER ROOM NAME\n");
					String name2 = textIO.newStringInputReader()
					        .withDefaultValue("def-room-name")
					        .read("Name:");
					if(aChatImpl.joinRoom(name2))
						terminal.printf("\nROOM %s SUCCESSFULLY JOINED\n",name2);
					else
						terminal.printf("\nERROR IN JOINING THE ROOM, RETRY\n");
					break;
					
				case 3:
					terminal.printf("\nENTER ROOM NAME\n");
					String name3 = textIO.newStringInputReader()
					        .withDefaultValue("def-room-name")
					        .read("Name:");
					if(aChatImpl.leaveRoom(name3))
						terminal.printf("\nROOM %s SUCCESSFULLY LEAVED\n",name3);
					else
						terminal.printf("\nERROR IN LEAVING THE ROOM, RETRY\n");
					break;
					
				case 4:
					terminal.printf("\nENTER ROOM NAME\n");
					String name4 = textIO.newStringInputReader()
					        .withDefaultValue("def-room-name")
					        .read("Name:");
					terminal.printf("\nENTER MESSAGE\n");
					String mex = textIO.newStringInputReader()
					        .withDefaultValue("def-message")
					        .read("Message:");
					if(aChatImpl.sendMessage(name4,mex))
						terminal.printf("\nSUCCESSFULLY SENT MESSAGE IN ROOM %s\n",name4);
					else
						terminal.printf("\nERROR IN SENDING THE MESSAGE, RETRY\n");
					break;
					
				case 5:
					terminal.printf("\nENTER ROOM NAME\n");
					String name5 = textIO.newStringInputReader()
					        .withDefaultValue("def-room-name")
					        .read("Name:");
					if(aChatImpl.deleteRoom(name5))
						terminal.printf("\nROOM %s SUCCESSFULLY DELETED\n",name5);
					else
						terminal.printf("\nERROR IN DELETING THE ROOM, RETRY\n");
					break;
				case 6:
					for(String room : aChatImpl.getJoinedRooms()) 
						terminal.printf("\n - "+room+"\n");
					break;
					
				case 7:
					terminal.printf("\nARE YOU SURE TO LEAVE THE NETWORK?\n");
					boolean exit = textIO.newBooleanInputReader().withDefaultValue(false).read("exit?");
					if(exit) {
						aChatImpl.leaveChat();
						System.exit(0);
					}
					break;
					
				default:
					break;
				}
			}
			
		} catch (CmdLineException clEx)  {  
			System.err.println("ERROR: Unable to parse command-line options: " + clEx);  
		}  
	}
	
	public static void printMenu(TextTerminal terminal) {
		terminal.printf("\n1 - CREATE ROOM\n");
		terminal.printf("\n2 - JOIN A ROOM\n");
		terminal.printf("\n3 - LEAVE A ROOM\n");
		terminal.printf("\n4 - SEND A MESSAGE IN A ROOM\n");
		terminal.printf("\n5 - DELETE A ROOM\n");
		terminal.printf("\n6 - LIST JOINED ROOMS\n");
		terminal.printf("\n7 - EXIT\n");
	}
}
