package uicc.hci.test.services.connectivity.api_2_cns_sce;

import org.globalplatform.contactless.GPCLSystem;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.AppletEvent;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import uicc.hci.framework.HCIDevice;
import uicc.hci.framework.HCIException;
import uicc.hci.framework.HCIListener;
import uicc.hci.framework.HCIMessage;
import uicc.hci.services.cardemulation.CardEmulationListener;
import uicc.hci.services.cardemulation.CardEmulationService;
import uicc.hci.services.connectivity.ConnectivityService;
import uicc.toolkit.ToolkitException;

/**
 * The method with the following header shall be compliant to its definition in
 * the API.<br />
 * <code>void prepareAndSendConnectivityEvent() throws HCIException</code>
 */
public class Api_2_CNs_Sce_6 extends Applet implements CardEmulationListener, AppletEvent {

	private static final byte INS_PREPARE_AND_SEND_CONN_EVT_HCI_DISABLED = 0x02;
	
	private static final short SW_UNKNOWN_PROP = (short) (ISO7816.SW_UNKNOWN + 1);

	/*
	 * Define HCI specific constants
	 */
	private CardEmulationService ceService;
	private ConnectivityService cnnService;

	/**
	 * Applet tests CardEmulationMessage
	 */
	private Api_2_CNs_Sce_6() {
		/*
		 * JavaCard applet register
		 */
		register();

		/*
		 * HCI listener register
		 */
		try {
			ceService = (CardEmulationService) HCIDevice.getHCIService(HCIDevice.CARD_EMULATION_SERVICE_ID);
			ceService.register(this);
			cnnService = (ConnectivityService) HCIDevice.getHCIService(HCIDevice.CONNECTIVITY_SERVICE_ID);
//			cnnService.register(this);
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
		new Api_2_CNs_Sce_6();
	}

	/**
	 * Not used.<br />
	 * This method is called by the HCI framework to inform the Listener Object
	 * about a specific event and pass the corresponding HCIMessage to the
	 * Listener Object.
	 * 
	 * @see HCIListener#onCallback(byte, HCIMessage)
	 */
	public void onCallback(byte event, HCIMessage hcimessage) {

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

		case INS_PREPARE_AND_SEND_CONN_EVT_HCI_DISABLED: 

			GPCLSystem.setCommunicationInterface(GPCLSystem.GPCL_INTERFACE_ISO14443, false);
			
			try {				
				cnnService.prepareAndSendConnectivityEvent();
			} catch (HCIException e) {
				if (e.getReason() == HCIException.HCI_CURRENTLY_DISABLED)
					return;
			}
			ISOException.throwIt(SW_UNKNOWN_PROP);
			

		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}

	public void processToolkit(short arg0) throws ToolkitException {
		// TODO Auto-generated method stub
		return;
	}

	public void uninstall() {
		GPCLSystem.setCommunicationInterface(GPCLSystem.GPCL_INTERFACE_ISO14443, true);
		
	}

}
