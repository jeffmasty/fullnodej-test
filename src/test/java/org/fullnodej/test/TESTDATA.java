package org.fullnodej.test;

import static java.lang.reflect.Modifier.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.fullnodej.RpcParams;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** A run-sheet for fullnode tests */
@NoArgsConstructor @Slf4j
public class TESTDATA {

	public static final String PROPERTIES_FILE = TESTDATA.class.getSimpleName() + ".properties";
	/** comma-separated lists is .properties files mean no whitespace between elements, please */
	public static final String COMMA = ",";
	/** Passcode to unlock the wallet, if null or empty string the expectation is that the wallet is not encrypted */
	public String walletPassphrase;

	/**Port to open and listen for incoming bitcoind notifications, requires bitcoind command line options.
	 * If using port 7931, perhaps, then add to your bitcoind startup:
	 * -blocknotify="echo '%s' | nc 127.0.0.1 7931" -walletnotify="echo '%s' | nc 127.0.0.1 7931"
	 * -alertnotify="echo '%s' | nc 127.0.0.1 7931"  */
	public Integer listenerPort = 7931;

	/**Which chain is the rpc expected to be running? test|main|regtest, default: test */
	public String chain = "test";
	/**how many confirmations required for "confirmed" status for testing purposes, default: 2*/
	public int confirmations = 2;
	/**true or false expectation of rpc's mining status. default: false */
	public boolean generate;
	/**true or false expectation of rpc's transaction database status
	 * (-txindex bitcoind command line option). default: true*/
	public boolean txindex = true;

	/** receive that incoming tx within x minutes or stop waiting for it to come */
	public int notificationTimeoutMinutes = 10;

	/** what is the expected current total balance? not used*/
	public BigDecimal currentBalance;

	/** list of accounts to check their existence in the wallet, creating them if not in wallet */
	public List<String> walletAccounts = Arrays.asList(new String[] {"fullnodej-test1", "fullnodej-test2"});
	/** list of address to check for, not really useful on fresh wallets */
	public List<String> walletAddresses = new ArrayList<>();

	/** hash of any transactions the wallet has received */
	public List<String> walletTxsRcvd;

	/** hash of any transactions the wallet has sent */
	public List<String> walletTxsSent;

	/**path relative to the connected bitcoind's working directory to import.
	 * test skipped if blank, test fails if 0 keys added, test warns if balance does not change. */
	public String importWallet;

	/** peer to test connection to */
	public String addnode;

	// external Signature test
	public String signature;
	public String sigmessage;
	public String sigaddress;

	////////////////////////////////////////
	// Spending Script
	public String sendToAddress;
	public BigDecimal sendToAmount;
	public String sendComment = "fullnodej";
	public String sendToLabel;

	////////////////////////////////////////
	// PING PONG
	public int listenerPort2;
	public int rpc2port;
	public String rpc2password;
	public String wallet2Passphrase;
	public BigDecimal pingPongAmount;

	// The End.
	/////////////////////////////////////////

	private final Properties props = new Properties();


	/**@param props load test setup from supplied Properties object, keys searched for are the field names in this class*/
	public TESTDATA(Properties props) {
		this.props.putAll(props);
		try {
			reflect();
		} catch (IllegalAccessException e) {
			log.error(e.getMessage(), e);
		}
	}

	/**Attempts to load {@link #PROPERTIES_FILE} from the same package as the supplied class instance.
	 * @param clazz instance of a class in the same package as your PROPERTIES_FILE
	 * @throws FileNotFoundException if PROPERTIES_FILE not streamable */
	public TESTDATA(Class<?> clazz) throws FileNotFoundException, IOException, IllegalAccessException {
		InputStream in = null;
		try {
			in = clazz.getResourceAsStream(PROPERTIES_FILE);
			if (in == null) throw new FileNotFoundException(PROPERTIES_FILE + " via classloader of " + clazz);
			props.load(in);
			reflect();
		} finally {
			if (in != null) try {
				in.close();
			} catch (IOException e) { log.error(e.getMessage()); }
		}
	}

	/**read variables in TESTDATA.properties, search sequence:
	 * <ol> <li>fullpath passed from shell variable</li>
	 * 		<li>the working directory</li>
	 * 		<li>this package</li> </ol>
	 * @throws IOException
	 * @throws IllegalAccessException */
	public TESTDATA load() throws IOException, IllegalAccessException {
		InputStream in = null;
		try {
			// try to load properties from fullpath from shell
			String fullpath = System.getProperty(PROPERTIES_FILE);
			if (fullpath != null && !fullpath.isEmpty()) {
				try {
					File file = new File(fullpath);
					if (file.exists()) in = new FileInputStream(file);
				} catch (Throwable t) {
					log.warn("Couldn't read setup from supplied System property '" + PROPERTIES_FILE + "': " + fullpath + " ");
				}
			}
			if (in == null) { // try from working directory
				File file = new File(System.getProperty("user.dir"), PROPERTIES_FILE);
				if (file.exists()) in = new FileInputStream(file);
			}
			if (in == null) // try from package
				in = getClass().getResourceAsStream(PROPERTIES_FILE);
			if (in == null) {
				log.warn(PROPERTIES_FILE + " test settings not found, using basic defaults.");
				return this;
			}
			props.load(in);
			reflect();
		} finally {
			if (in != null) try {
				in.close();
			} catch (IOException e) { log.error(e.getMessage()); }
		}
		return this;
	}

	public RpcParams getRpcParams() {
		try { return new RpcParams(props); }
		catch (Throwable t) {log.error(t.getMessage(), t);return null;}
	}

	/**Look for properties and fields that match, set the field value from the property using reflection.*/
	private void reflect() throws IllegalAccessException {
		String fieldname;
		String val;
		Class<?> clazz;

		for (Field field : this.getClass().getFields()) {
			fieldname = field.getName();
			if (correctModifiers(field.getModifiers()) && props.containsKey(fieldname)) {
				val = props.getProperty(fieldname);
				if (val.isEmpty()) continue;
				if (!field.isAccessible()) field.setAccessible(true);

				clazz = field.getType();
				if (clazz == String.class) field.set(this, val);
				else if (clazz == boolean.class)  field.set(this, "true".equalsIgnoreCase(val));
				else if (clazz == int.class) field.set(this, Integer.parseInt(val));
				else if (clazz == Integer.class) field.set(this, Integer.parseInt(val));
				else if (clazz == BigDecimal.class) field.set(this, new BigDecimal(val));
				else if (clazz == String[].class) field.set(this, val.split(COMMA));
				else if (clazz == List.class) field.set(this, Arrays.asList(val.split(COMMA)));
				else log.warn("Field: " + fieldname + " - Unknown property type: " + clazz.getCanonicalName());
			}
		}
	}

	/** fields loaded through reflection must be non-static, non-final and public */
	private boolean correctModifiers(int mod) {
		return !isStatic(mod) && !isFinal(mod) && isPublic(mod);
	}


}
