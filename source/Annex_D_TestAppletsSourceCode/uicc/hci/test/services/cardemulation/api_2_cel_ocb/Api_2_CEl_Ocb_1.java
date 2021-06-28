package uicc.hci.test.services.cardemulation.api_2_cel_ocb;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import uicc.hci.framework.HCIDevice;
import uicc.hci.framework.HCIException;
import uicc.hci.framework.HCIListener;
import uicc.hci.framework.HCIMessage;
import uicc.hci.services.cardemulation.CardEmulationListener;
import uicc.hci.services.cardemulation.CardEmulationService;
import uicc.hci.services.cardemulation.CardEmulationMessage;

/**
 * The method with the following header shall be compliant to its definition in
 * the API. <code>void activateEvent(byte event) throws HCIException</code>
 */
public class Api_2_CEl_Ocb_1 extends Applet implements CardEmulationListener {

	/*
	 * Define specific SWs
	 */

	private static final short SW_METHOD_NOT_SUPPORTED = ISO7816.SW_UNKNOWN + (short) 2;

	/*
	 * Define specific INS bytes
	 */

	private final static byte INS_EVENT_SEND_DATA = (byte) 0x14;

	private final static byte INS_EVENT_ACTIVATE_SEND_DATA_VF = (byte) 0x24;


	/*
	 * 
	 */

	private CardEmulationService ceService;
	private byte[] sw;

	/**
	 * bit 0 is set if EVENT_HCI_TRANSMISSION_FAILED is notified
	 * bit 1 is set if EVENT_HCI_RECEPTION_FAILED is notified
	 * bit 2 is set if EVENT_GET_PARAMETER_RESPONSE is notified
	 * bit 3 is set if EVENT_ON_SEND_DATA is notified
	 * bit 4 is set if EVENT_FIELD_OFF is notified
	 */
	private static byte verificationByte;

	private Api_2_CEl_Ocb_1() {
		/*
		 * JavaCard applet register
		 */
		register();

        sw = new byte[2];
		verificationByte = 0x00;
		/*
		 * HCI listener register
		 */
		try {
			ceService = (CardEmulationService) HCIDevice.getHCIService(HCIDevice.CARD_EMULATION_SERVICE_ID);
			ceService.register(this);
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
		new Api_2_CEl_Ocb_1();
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

		case INS_EVENT_SEND_DATA:

			try {
				ceService.activateEvent(EVENT_ON_SEND_DATA);
			} catch (HCIException e) {
				ISOException.throwIt(SW_METHOD_NOT_SUPPORTED);
			}

			return;


		case INS_EVENT_ACTIVATE_SEND_DATA_VF:

			if ((verificationByte & 0x08) == 0x08) {
				ISOException.throwIt(SW_METHOD_NOT_SUPPORTED);
			}

			return;
			
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}

		/*
		 * prepare outgoing data if needed
		 */

		apdu.setOutgoingAndSend((short) 0, (short) 0);

	}

	/**
	 * bit 0 is set if EVENT_HCI_TRANSMISSION_FAILED is notified<br />
	 * bit 1 is set if EVENT_HCI_RECEPTION_FAILED is notified<br />
	 * bit 2 is set if EVENT_GET_PARAMETER_RESPONSE is notified<br />
	 * bit 3 is set if EVENT_ON_SEND_DATA is notified<br />
	 * bit 4 is set if EVENT_FIELD_OFF is notified<br />
	 * This method is called by the HCI framework to inform the Listener Object
	 * about a specific event and pass the corresponding HCIMessage to the
	 * Listener Object.
	 * 
	 * @see HCIListener#onCallback(byte, HCIMessage)
	 */
	public void onCallback(byte event, HCIMessage hciMessage) {		
		
		
		switch (event) {
		case EVENT_ON_SEND_DATA:
		  CardEmulationMessage message = (CardEmulationMessage) hciMessage;
		  sw[0] = (byte) 0x90;
		  sw[1] = 0;   
     	message.prepareAndSendSendDataEvent(sw, (short) 0, (short)2);

			verificationByte |= 0x08;

			return;

		case EVENT_GET_PARAMETER_RESPONSE:

			verificationByte |= 0x04;

			return;

		case EVENT_FIELD_OFF:

			verificationByte |= 0x10;

			return;

		case EVENT_HCI_TRANSMISSION_FAILED:

			verificationByte |= 0x01;

			return;

		case EVENT_HCI_RECEPTION_FAILED:

			verificationByte |= 0x02;

			return;
		default:
			return;
		}

	}

}
