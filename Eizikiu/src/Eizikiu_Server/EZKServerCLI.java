package Eizikiu_Server;

import java.util.Scanner;
import Eizikiu_Tools.*;
import Eizikiu_Tools.CmdLineParser.Option;

public class EZKServerCLI implements Runnable {

	private Scanner scanner;
	private boolean shutdown;
	private CmdLineParser parser;
	private String command;
	private String input;
	private String[] inputParts;
	private String[] noOptionArgs;
	
	// constructor
	public EZKServerCLI() {
		scanner = new Scanner(System.in);
		shutdown = false;
		parser = new CmdLineParser();
	}
	
	// functions
	@Override
	public void run() {
		
		// parser options
		Option<Boolean> show = parser.addBooleanOption('s', "show");
		//Option<Boolean> set = parser.addBooleanOption("set");
		Option<Boolean> enable = parser.addBooleanOption('e', "enable");
		Option<Boolean> disable = parser.addBooleanOption('d', "disable");
		Option<Boolean> allUsers = parser.addBooleanOption("allusers");
		Option<Boolean> publicRooms = parser.addBooleanOption("publicrooms");
		Option<Boolean> privateRooms = parser.addBooleanOption("privaterooms");
		Option<Boolean> connectionList = parser.addBooleanOption("connections");
		Option<Boolean> console = parser.addBooleanOption("console");
		Option<Boolean> file = parser.addBooleanOption("file");
		Option<String> fileName = parser.addStringOption("filename");
		Option<Integer> logLevel = parser.addIntegerOption("loglevel");
		Option<Boolean> warn = parser.addBooleanOption("warn");
		Option<Boolean> kick = parser.addBooleanOption("kick");
		Option<Boolean> ban = parser.addBooleanOption("ban");
		Option<Boolean> unban = parser.addBooleanOption("unban");
		Option<String> name = parser.addStringOption("name");
		Option<String> password = parser.addStringOption("password");
		Option<String> create = parser.addStringOption("create");
		Option<String> delete = parser.addStringOption("delete");
		
		do {
			System.out.print("EZK> ");
			// waiting on getting input from System.in
			input = scanner.nextLine();
			EZKlogger.CLIinput(input);
			
			// split string
			inputParts = input.split(" ");
			
			// parse
			try {
				parser.parse(inputParts);
			} catch	(CmdLineParser.OptionException oe) {
				EZKlogger.CLIerror(oe.getMessage());
				continue;
			}
			noOptionArgs = parser.getRemainingArgs();
			command = noOptionArgs[0];
			if (!command.isEmpty())	EZKlogger.debug("command: " + command);
			
			// execute
			switch (command) {

			case "show":
				EZKlogger.debug("command 'show'");
				// options
				if (parser.getOptionValue(allUsers, Boolean.FALSE)) {
					EZKlogger.info("global user list:");
					EZKlogger.info("*****************");
					for (User user : Eizikiu_Server.getGlobalUserList()) {
						EZKlogger.info(user.everythingToString());
					}
					break;
				}
				if (parser.getOptionValue(privateRooms, Boolean.FALSE)) {
					EZKlogger.info("private rooms:");
					EZKlogger.info("**************");
					for (Room room : Eizikiu_Server.getPrivateRooms()) {
						EZKlogger.info(room.toString());
					}
					break;
				}
				if (parser.getOptionValue(publicRooms, Boolean.FALSE)) {
					EZKlogger.info("public rooms:");
					EZKlogger.info("**************");
					for (Room room : Eizikiu_Server.getPublicRooms()) {
						EZKlogger.info(room.toString());
					}
					break;
				}
				if (parser.getOptionValue(connectionList, Boolean.FALSE)) {
					EZKlogger.info("connected users:");
					EZKlogger.info("****************");
					for (ConnectionToClient ctc : Eizikiu_Server.getConnectionList()) {
						EZKlogger.info(ctc.toString());
					}
					break;
				}
				break;
				
			case "logger":
				EZKlogger.debug("command 'logger'");
				// options
				if (parser.getOptionValue(show, Boolean.FALSE)) {
					EZKlogger.info(EZKlogger.show());
				}
				if (parser.getOptionValue(logLevel) != null) {
					 EZKlogger.setLoglevel(parser.getOptionValue(logLevel));
				}
				if (parser.getOptionValue(console, Boolean.FALSE)) {
					if (parser.getOptionValue(enable, Boolean.FALSE)) EZKlogger.setConsoleOutput(true);
					if (parser.getOptionValue(disable, Boolean.FALSE)) EZKlogger.setConsoleOutput(false);
				}
				if (parser.getOptionValue(file, Boolean.FALSE)) {
					if (parser.getOptionValue(enable, Boolean.FALSE)) EZKlogger.setFileOutput(true);
					if (parser.getOptionValue(disable, Boolean.FALSE)) EZKlogger.setFileOutput(false);
				}
				if (parser.getOptionValue(fileName) != null) {
					EZKlogger.setLogfile(parser.getOptionValue(fileName));
				}
				break;
				
			case "user":
				EZKlogger.debug("command 'user'");
				// options
				if (parser.getOptionValue(show, Boolean.FALSE)) {
					for (int i=1; i<noOptionArgs.length; i++) {
						User user = null;
						for (User x : Eizikiu_Server.getGlobalUserList()) {
							if (x.getName().equals(noOptionArgs[i])) {
								user = x;
								break;
							}
						}
						if (user == null) {
							EZKlogger.CLIerror("no user named '" + noOptionArgs[i] + "'!");
						} else {
							EZKlogger.info(user.everythingToString());
						}
					}
					break;
				}
				if (parser.getOptionValue(warn, Boolean.FALSE)) {
					User user = null;
					for (User x : Eizikiu_Server.getGlobalUserList()) {
						if (x.getName().equals(noOptionArgs[1])) {
							user = x;
							break;
						}
					}
					if (user == null) {
						EZKlogger.CLIerror("no user named '" + noOptionArgs[1] + "'!");
					} else {
						String message = "";
						for (int i=1; i<noOptionArgs.length; i++) {
							message += noOptionArgs[i] + " ";
						}
						if (!message.equals("")) {
							Eizikiu_Server.warnUser(user, message);						
						} else {
							EZKlogger.CLIerror("You entered no message!");
						}
					}
					break;
				}
				if (parser.getOptionValue(kick, Boolean.FALSE)) {
					User user = null;
					for (User x : Eizikiu_Server.getGlobalUserList()) {
						if (x.getName().equals(noOptionArgs[1])) {
							user = x;
							break;
						}
					}
					if (user == null) {
						EZKlogger.CLIerror("no user named '" + noOptionArgs[1] + "'!");
					} else {
						Eizikiu_Server.warnUser(user, "You have been kicked from server!");
						try {
							user.getConnection().shutdown();
						} catch (Exception e) {
							EZKlogger.CLIerror(e.getMessage());
						}
					}
					break;
				}
				if (parser.getOptionValue(ban, Boolean.FALSE)) {
					User user = null;
					for (User x : Eizikiu_Server.getGlobalUserList()) {
						if (x.getName().equals(noOptionArgs[1])) {
							user = x;
							break;
						}
					}
					if (user == null) {
						EZKlogger.CLIerror("no user named '" + noOptionArgs[1] + "'!");
					} else {
						Eizikiu_Server.warnUser(user, "You have been banned from server!");
						try {
							user.getConnection().shutdown();
						} catch (Exception e) {
							EZKlogger.CLIerror(e.getMessage());
						}
						user.setBanned(true);
					}
					break;
				}
				if (parser.getOptionValue(unban, Boolean.FALSE)) {
					User user = null;
					for (User x : Eizikiu_Server.getGlobalUserList()) {
						if (x.getName().equals(noOptionArgs[1])) {
							user = x;
							break;
						}
					}
					if (user == null) {
						EZKlogger.CLIerror("no user named '" + noOptionArgs[1] + "'!");
					} else {
						user.setBanned(false);
					}
					break;
				}
				if (parser.getOptionValue(name) != null) {
					User user = null;
					for (User x : Eizikiu_Server.getGlobalUserList()) {
						if (x.getName().equals(noOptionArgs[1])) {
							user = x;
							break;
						}
					}
					if (user == null) {
						EZKlogger.CLIerror("no user named '" + noOptionArgs[1] + "'!");
					} else {
						user.setName(parser.getOptionValue(name, user.getName()));
					}
					break;
				}
				if (parser.getOptionValue(password) != null) {
					User user = null;
					for (User x : Eizikiu_Server.getGlobalUserList()) {
						if (x.getName().equals(noOptionArgs[1])) {
							user = x;
							break;
						}
					}
					if (user == null) {
						EZKlogger.CLIerror("no user named '" + noOptionArgs[1] + "'!");
					} else {
						user.setPassword(parser.getOptionValue(password, user.getPassword()));
					}
					break;
				}
				
				
				break;
			
			case "room":
				EZKlogger.debug("command 'room'");
				// options
				if (parser.getOptionValue(show, Boolean.FALSE)) {
					
					break;
				}
				if (parser.getOptionValue(name) != null) {

					break;
				}
				if (parser.getOptionValue(create) != null) {

					break;
				}
				if (parser.getOptionValue(delete) != null) {

					break;
				}
				break;
				
			case "listener":
				EZKlogger.debug("command 'listener'");
				// options
				if (parser.getOptionValue(enable, Boolean.FALSE)) {

					break;
				}
				if (parser.getOptionValue(disable, Boolean.FALSE)) {

					break;
				}
				break;
				
			case "exit":
			case "shutdown":
				EZKlogger.debug("command 'shutdown'");
				Eizikiu_Server.latch.countDown();
				shutdown = true;
				break;
				
		
				// show all users
				// show private rooms
				// show public rooms
				// show connection list
				
				// logger show
				// logger set log level
				// logger set console output
				// logger set gui output
				// logger set file output
				// logger set file name
				
				// user show
				// user warn
				// user kick
				// user ban
				// user unban
				// user name
				// user password
				
				// room show
				// room name
				// room create
				// room delete
				
				// listener enable
				// listener disable
				
				// shutdown

			default:
				if (!command.isEmpty())	EZKlogger.CLIerror("bad command!");
				break;
			}
			
			
		} while (!shutdown);
		
		Eizikiu_Server.latch.countDown();
	}
	
	// getter
	// setter

}
