package uicc.hci.test.framework.api_1_hsr_ace;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import uicc.hci.framework.HCIDevice;
import uicc.hci.framework.HCIException;
import uicc.hci.framework.HCIListener;
import uicc.hci.framework.HCIMessage;
import uicc.hci.services.readermode.ReaderListener;
import uicc.hci.services.readermode.ReaderService;

/**
 * The method with the following header shall be compliant to its definition in
 * the API. <code>void activateEvent(byte event) throws HCIException</code>
 */
public class Api_1_Hsr_Ace_3 extends Applet implements ReaderListener {

	/*
	 * Define specific SWs
	 */

	private static final short SW_EVENT_NOT_SUPPORTED = ISO7816.SW_UNKNOWN + (short) 1;
	private static final short SW_METHOD_NOT_SUPPORTED = ISO7816.SW_UNKNOWN + (short) 2;

	/*
	 * Define specific INS bytes
	 */

	private final static byte INS_EVENT_HR_FAILED = (byte) 0x10;
	private final static byte INS_EVENT_TARGET_DISCOVERED = (byte) 0x11;
	private final static byte INS_EVENT_WE_DATA_RESPONSE = (byte) 0x12;
	private final static byte INS_TM_FAILED = (byte) 0x13;
	private final static byte INS_GET_PARAM_RESP = (byte) 0x14;

	/*
	 * 
	 */

	private ReaderService rdrService;

	private Api_1_Hsr_Ace_3() {
		/*
		 * JavaCard applet register
		 */
		register();

		/*
		 * HCI listener register
		 */
		try {
			rdrService = (ReaderService) HCIDevice.getHCIService(HCIDevice.READER_SERVICE_ID);
			rdrService.register(this);
		} catch (HCIException e) {
			ISOException.throwIt(ISO7816.SW_DATA_INVALID);
		}

	}

	/**
	 * To create an instance of the <code>Applet</code> subclass, the Java Card
	 * runtime environment will call this static method first.
	 * 
	 * @see Applet#install(byte[], short, byte)
	 */
	public static void install(byte bArray[], short bOffset, byte bLength) throws ISOException {
		new Api_1_Hsr_Ace_3();
	}

	/**
	 * Called by the Java Card runtime environment to process an incoming APDU
	 * command.
	 * 
	 * @see Applet#process(APDU)
	 */
	public void process(APDU apdu) throws ISOException {
		/*
		 * Check for SELECT command
		 */
		if (selectingApplet())
			return;

		/*
		 * analyze incoming data
		 */

		byte buffer[] = apdu.getBuffer();

		switch (buffer[ISO7816.OFFSET_INS]) {

		/*
		 * HCIServce.activateEvent()
		 */
		case INS_EVENT_HR_FAILED:

			try {
				rdrService.activateEvent(EVENT_HCI_RECEPTION_FAILED);
			} catch (HCIException e) {
				ISOException.throwIt(SW_METHOD_NOT_SUPPORTED);
			}

			return;

		case INS_EVENT_TARGET_DISCOVERED:

			try {
				rdrService.activateEvent(EVENT_TARGET_DISCOVERED);
			} catch (HCIException e) {
				ISOException.throwIt(SW_METHOD_NOT_SUPPORTED);
			}

			return;

		case INS_EVENT_WE_DATA_RESPONSE:

			try {
				rdrService.activateEvent(EVENT_WRITE_EXCHANGE_DATA_RESPONSE);
			} catch (HCIException e) {
				ISOException.throwIt(SW_METHOD_NOT_SUPPORTED);
			}

			return;
			
		case INS_TM_FAILED:
			
			try {
				rdrService.activateEvent(EVENT_HCI_TRANSMISSION_FAILED);
			} catch (HCIException e) {
				ISOException.throwIt(SW_METHOD_NOT_SUPPORTED);
			}
			
			return;
			
		case INS_GET_PARAM_RESP:
			
			try {
				rdrService.activateEvent(EVENT_GET_PARAMETER_RESPONSE);
			} catch (HCIException e) {
				ISOException.throwIt(SW_METHOD_NOT_SUPPORTED);
			}
			
			return;

		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}


	}

	/**
	 * This method is called by the HCI framework to inform the Listener Object
	 * about a specific event and pass the corresponding HCIMessage to the
	 * Listener Object.
	 * 
	 * @see HCIListener#onCallback(byte, HCIMessage)
	 */
	public void onCallback(byte event, HCIMessage message) {
		}

}
