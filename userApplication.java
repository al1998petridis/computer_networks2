/*-------------------------------*
 *	Author: Alexandros Petridis	 * 
 *	Class: Computer Networks 2	 *
 *-------------------------------*/

package dyktia2;

import java.net.*;
import java.nio.*;
import java.text.*;
import java.util.*;
import javax.sound.sampled.*;
import java.io.*;

public class userApplication {
	
	// Codes change each time we log in to the Virtual Lab.
	
	public static int clientPort = 48021;
	public static byte[] serverIP = {(byte) 155, (byte) 207, 18, (byte) 208};
	public static int serverPort = 38021;
	public static String echo_request_code = "E0022"; 
	public static String image_request_code = "M7305";
	public static String audio_request_code = "A8498";
	public static String ithakicopter_code = "Q8996";
	public static String vehicle_OBD_code = "V5314";
	public static String[] pid = { "1F", "0F", "11", "0C", "0D", "05"};
	/*
	 * 1F -> Engine Run Time
	 * 0F -> Intake air Temperature
	 * 11 -> Throttle position
	 * 0C -> Engine RPM
	 * 0D -> Vehicle Speed
	 * 05 -> Coolant temperature
	 * 
	 */
	
	


	
	public static void main(String[] args) throws Exception {
		
		userApplication app = new userApplication();
		
		
		DateFormat df = new SimpleDateFormat();
		Date today = Calendar.getInstance().getTime();
		String str = df.format(today);
		String str1 = str.substring(0, 8);
		str1 = str1.replace('/', '.');
		String path = ("D:\\AUTH\\Δίκτυα 2\\projectDyktia\\" + str1);
		File file = new File(path);
		file.mkdirs();
		System.out.println("All Files from this section will be saved to: "+ path + "\n");
		
		
		app.vehicle(vehicle_OBD_code, pid[0], path);

		
		//Working
		/*
		 * 
		 * app.sound(audio_request_code, false,false, 999, path); 
		app.sound(audio_request_code, false,false, 999, path); 
		app.sound(audio_request_code, false,false, 999, path); 
		app.sound(audio_request_code, false,false, 999, path); 
		app.sound(audio_request_code, false,false, 999, path); 
		app.sound(audio_request_code, false,false, 999, path); 
		app.sound(audio_request_code, false,false, 999, path); 
		app.sound(audio_request_code, false,false, 999, path); 
		app.sound(audio_request_code, false,false, 999, path); 
		app.sound(audio_request_code, false,false, 999, path); 
		app.sound(audio_request_code, false,false, 999, path); 
		app.sound(audio_request_code, false,false, 999, path); 
		app.sound(audio_request_code, false,false, 999, path); 
		app.sound(audio_request_code, false,false, 999, path); 
		app.sound(audio_request_code, false,false, 999, path); 
		app.sound(audio_request_code, false,false, 999, path); 
		 */
		
		 
		
 		//Echo is For 4 minutes record
		/*
		app.echo(echo_request_code, path);//+TXX for Temperatures only TT00 works.
		app.echo(echo_request_code+"T00", path);
		app.echo("E0000", path); //E0000 for no delay
		
		app.image(image_request_code, true, path); //TRUE is for + CAM=PTZ in request code.
		app.image(image_request_code, false, path); //FALSE is for + CAM=FIX in request code.
		

		app.sound(audio_request_code, false,false, 999, path); //first boolean : true for AQ-DPCM and false for DCPM
		app.sound(audio_request_code, true,false, 999, path); //second boolean : true for T(generator) and false for F in audio code extension. (999 audio packets)
		app.sound(audio_request_code, false, true, 999, path);
		app.sound(audio_request_code, true, true, 999, path);
		
		// do it 2 times for 2 flight levels
		app.ithakiCopter(48078, path); //parallel with ithakicopter.jnlp // Creating a .txt with all the messages that has been received

		//Vehicle is for 4 minutes
		for (int i=0; i<6; i++) {
			app.vehicle(vehicle_OBD_code, pid[i], path); // Creating a .txt with Vehicle info for vehicle_OBD_code and pid[i] code.
		}
*/
		
		
	}

	public void echo(String echo_code, String path) throws IOException {

		DatagramSocket s = new DatagramSocket();
		DatagramSocket r = new DatagramSocket(clientPort);
		r.setSoTimeout(3200);
		InetAddress serverAddress = InetAddress.getByAddress(serverIP);
		long timeSend = 0;
		long timeReceived = 0;
		long timeTransfer = 0;
		String message = null;
		
		byte[] code = echo_code.getBytes();
		DatagramPacket packToIthaki = new DatagramPacket(code, code.length, serverAddress, serverPort);
		byte[] rBuffer = new byte[2048]; 
		DatagramPacket packToApp = new DatagramPacket(rBuffer, rBuffer.length);
		
		
		//Create files
		ArrayList<Long> samples = new ArrayList<Long>();

		String filename1;
		String filename2;
		boolean delay = false;
		if (echo_code != "E0000") {
			filename1 = path + "\\EchoTimesWithDelay.csv";
			filename2 = path + "\\EchoSumsOfTimesWithDelay.csv";
		}
		else {
			filename1 = path + "\\EchoTimesWithNoDelay.csv";
			filename2 = path + "\\EchoSumsOfTimesWithNoDelay.csv";
			delay = true;
		}
		
		File times = new File(filename1);
		if (!times.exists())
			times.createNewFile();
		FileWriter fw = new FileWriter(times);
		BufferedWriter bw = null;
		bw = new BufferedWriter(fw);
		
		System.out.println("Start record samples of echo packets for 4mins.");
		long startTime = System.currentTimeMillis();
		while((System.currentTimeMillis() - startTime) <240*1000) {
			s.send(packToIthaki);
			timeSend = System.currentTimeMillis();
			System.out.println("Echo Packet code "+ echo_code + " have been send.");

			try{
				r.receive(packToApp);
			}catch (IOException ex1) {
				System.out.println(ex1.getMessage());
			}
			timeReceived = System.currentTimeMillis();
			message = new String(rBuffer, 0, rBuffer.length);
			System.out.println("Echo Packet has been received.");
			timeTransfer = timeReceived - timeSend;
			System.out.println("Server delayed " + timeTransfer + " milliseconds, transferring the following message:");
			System.out.println(message);
			samples.add(timeTransfer);
		}
		for(int i = 0 ; i < samples.size() ; i++)
			bw.write("" + samples.get(i) + "\n");
		if(bw != null)
			bw.close();
		System.out.println("Samples set to " + filename1);

		float counterInt=0;
		long sumInt=0;
		ArrayList<Float> counters = new ArrayList<Float>();
		ArrayList<Long> sums = new ArrayList<Long>();
		for (int i=0;i<samples.size();i++){
			int j=i;
			while ((sumInt/1000<16) && (j<samples.size())){
			sumInt += samples.get(j);
			counterInt++;
			j++;
				}
			counterInt = counterInt/16;
			counters.add(counterInt);
			sums.add(sumInt);
			counterInt = 0;
			sumInt = 0;
		}
			
		File dSums = new File(filename2);
		if (!dSums.exists())
			dSums.createNewFile();
		FileWriter fw2 = new FileWriter(dSums);
		BufferedWriter bw2 = null;
		bw2 = new BufferedWriter(fw2);
		System.out.println("Start calculate sums of " + filename1);
		for(int i = 0 ; i < samples.size() ; i++)
			bw2.write(counters.get(i)+"\n");
		if(bw2 != null)
			bw2.close();
		System.out.println("Samples set to " + filename2);

		s.close();
		r.close();
		
	}
			
	public void image(String image_code, boolean choice, String path) throws IOException {
		DatagramSocket s = new DatagramSocket(); //socket to Send
		DatagramSocket r = new DatagramSocket(clientPort); //socket to Receive
		r.setSoTimeout(3200);
		InetAddress serverAddress = InetAddress.getByAddress(serverIP);
		String filename;
		if(choice) {
			image_code += " CAM=PTZ";
			filename = path + "\\PTZ_Image.jpeg";
		}
		else {
			image_code += " CAM=FIX";
			filename = path + "\\FIX_Image.jpeg";
		}		
		
		byte[] code = image_code.getBytes();
		DatagramPacket packToIthaki = new DatagramPacket(code, code.length, serverAddress, serverPort);
		byte[] rBuffer = new byte[2048]; 
		DatagramPacket packToApp = new DatagramPacket(rBuffer, rBuffer.length);
			
		s.send(packToIthaki);
		System.out.println("Image Request Code " + image_code + " have been send.");
		
		
		FileOutputStream outputImage = new FileOutputStream(filename);
		
		boolean stop = false;
		
		System.out.println("Downloading Image");
		for(;;) {
			r.receive(packToApp);				
			for(int i=0; i<=127; i++) {
				if ((rBuffer[i] == -1) && (rBuffer[i+1] == -39))
					stop = true;
				outputImage.write(rBuffer[i]);
				if (stop && (rBuffer[i] == -39))
					break;
			}
			if (stop)
				break;
		}
		
		outputImage.close();
		s.close();
		r.close();
		System.out.println("Image received and stored in " + filename + ".\n");
		
	}
	
	public byte[] sound(String audio_code, boolean A_or_N, boolean T_or_F, int numPackets, String path) throws IOException, LineUnavailableException {
		DatagramSocket s = new DatagramSocket();
		DatagramSocket r = new DatagramSocket(clientPort);
		r.setSoTimeout(3600);
		InetAddress serverAddress = InetAddress.getByAddress(serverIP);
		
		byte[] code = null;
		
		byte[] rBuffer = null;
		
		if (A_or_N) {
			audio_code += "AQ";
			rBuffer = new byte[132];
		}
		else {
			rBuffer = new byte[128];
		}
			
		if (T_or_F) {
			audio_code = (audio_code + "T" + String.valueOf(numPackets));
			code = audio_code.getBytes();
		}		
		else {
			audio_code = (audio_code + "F" + String.valueOf(numPackets));
			code = audio_code.getBytes();
		}		
		
		DatagramPacket packToIthaki = new DatagramPacket(code, code.length, serverAddress, serverPort);
		
		DatagramPacket packToApp = new DatagramPacket(rBuffer, rBuffer.length);
		
		s.send(packToIthaki);
		System.out.println("Audio Clip request code " + audio_code + " have been send.");
		
		int packCount = 0;
		byte[][] myBuffer = new byte[numPackets][rBuffer.length];
		System.out.println("Downloading audio clip...");
		for (int i=0; i<numPackets; i++) {
			try {
				r.receive(packToApp);
				packCount++;
				for (int j=0; j<rBuffer.length; j++) {
					myBuffer[i][j] = rBuffer[j];
				}	 
			} catch (IOException ex1) {
				System.out.println(ex1.getMessage());
			}
			
		}
		System.out.println("Audio Clip has received .");		
		s.close();
		r.close();
		
		
		byte[] bufferOut = null;
		ArrayList<Integer> subs = new ArrayList<Integer>();
		ArrayList<Integer> samples = new ArrayList<Integer>();
		int nibbleL;
		int nibbleM;
		int counter;
		int sub1 = 0;
		int sub2 = 0;
		int sample1 = 0;
		int sample2 = 0;
		
		AudioFormat linearPCM;
		if (A_or_N) {
			bufferOut = new byte[256*2*numPackets];
			ArrayList<Integer> means = new ArrayList<Integer>();
			ArrayList<Integer> betas = new ArrayList<Integer>();
			byte[] meanB = new byte[4];
			byte[] betta = new byte[4];
			byte sign;
			int mean = 0;
			int beta = 0;
			int hint = 0;
			counter = 4;
			for (int i=1; i<packCount; i++) {
				sign = (byte)( ( myBuffer[i][1] & 0x80) !=0 ? 0xff : 0x00); //converting byte[2] to integer
				meanB[3] = sign;
				meanB[2] = sign;
				meanB[1] = myBuffer[i][1];
				meanB[0] = myBuffer[i][0];
				mean = ByteBuffer.wrap(meanB).order(ByteOrder.LITTLE_ENDIAN).getInt();
				means.add(mean);
				sign = (byte)( ( myBuffer[i][3] & 0x80) !=0 ? 0xff : 0x00);
				betta[3] = sign;
				betta[2] = sign;
				betta[1] = myBuffer[i][3];
				betta[0] = myBuffer[i][2];
				beta = ByteBuffer.wrap(betta).order(ByteOrder.LITTLE_ENDIAN).getInt();
				betas.add(beta);
				
				for (int j = 4;j <= 131;j++){
					nibbleM = (int)(myBuffer[i][j] & 0x0000000F);
					nibbleL = (int)((myBuffer[i][j] & 0x000000F0)>>4);
					sub1 = (nibbleL-8);
					subs.add(sub1);
					sub2 = (nibbleM-8);
					subs.add(sub2);
					sub1 = sub1*beta;
					sub2 = sub2*beta;
					sample1 = hint + sub1 + mean;
					samples.add(sample1);
					sample2 = sub1 + sub2 + mean;
					hint = sub2;
					samples.add(sample2);
					counter += 4;
					bufferOut[counter] = (byte)(sample1 & 0x000000FF);
					bufferOut[counter + 1] = (byte)((sample1 & 0x0000FF00)>>8);
					bufferOut[counter + 2] = (byte)(sample2 & 0x000000FF);
					bufferOut[counter + 3] = (byte)((sample2 & 0x0000FF00)>>8);
					}
			}
			linearPCM = new AudioFormat(8000,16,1,true,false);	
			//Write AQ-DPCMsub(audio_code).csv
			System.out.println("Writing AQDPCMsub" + audio_code + ".csv");
			BufferedWriter bw1 = null;
			String filename1 = (path + "\\AQDPCMsub" + audio_code + ".csv");
			File file1 = new File(filename1);
			if (!file1.exists())
				file1.createNewFile();
			FileWriter fw1 = new FileWriter(file1);
			bw1 = new BufferedWriter(fw1);
			for (int i=0; i<subs.size(); i+=2) {
				bw1.write("" + subs.get(i) + " " + subs.get(i+1));
				bw1.newLine();
			}
			if (bw1 != null)
				bw1.close();
			System.out.println("AQDPCMsub" + audio_code + ".csv Finished.");
			
			//Write DPCMsamples(audio_code).csv
			System.out.println("Writing AQDPCMsamples" + audio_code + ".csv");
			BufferedWriter bw2 = null;
			String filename2 = (path + "\\AQDPCMsamples" + audio_code + ".csv");
			File file2 = new File(filename2);
			if (!file2.exists())
				file2.createNewFile();
			FileWriter fw2 = new FileWriter(file2);
			bw2 = new BufferedWriter(fw2);
			for (int i=0; i<samples.size(); i+=2) {
				bw2.write("" + samples.get(i) + " " + samples.get(i+1));
				bw2.newLine();
			}
			if (bw2 != null)
				bw2.close();
			System.out.println("AQDPCMsample" + audio_code + ".csv Finished.");
			
			//Write AQ-DPCMmean(audio_code).csv
			System.out.println("Writing AQDPCMmean" + audio_code + ".csv");
			BufferedWriter pw = null;
			String filename3 = (path + "\\AQDPCMmean" + audio_code + ".csv");
			File file3 = new File(filename3);
			if(!file3.exists()){
				file3.createNewFile();
			}
			FileWriter fw3 = new FileWriter(file3);
			pw = new BufferedWriter(fw3);
			for(int i = 0 ; i < means.size() ; i += 2){
				pw.write("" + means.get(i));
				pw.newLine();
				}
			if(pw != null)
				pw.close();
			System.out.println("AQDPCMmean" + audio_code + ".csv Finished.");

				
			//Write AQ-DPCMbetas(audio_code).csv
			System.out.println("Writing AQDPCMbetas" + audio_code + ".csv");
			BufferedWriter kw = null;
			String filename4 = (path + "\\AQDPCMbetas" + audio_code + ".csv");

			File file4 = new File(filename4);
			if(!file4.exists()){
				file4.createNewFile();
			}
				FileWriter fw4 = new FileWriter(file4);
				kw = new BufferedWriter(fw4);
				for(int i = 0 ; i < betas.size() ; i ++){
					kw.write("" + betas.get(i));
					kw.newLine();
				}
			if(kw != null)
				kw.close();
			System.out.println("AQDPCMbetas" + audio_code + ".csv Finished.");

			
			
			}
		else {
			
			bufferOut = new byte[512*packCount];
			int b = 4;
			int mask1 = 15;   //00001111
			int mask2 = 240;  //11110000
			counter = 0;
			
			for (int i=1; i<packCount; i++) {
				for (int j=0; j<myBuffer[0].length; j++) {
					nibbleM = myBuffer[i][j] & mask1;
					nibbleL = ((myBuffer[i][j] & mask2)>>4);
					sub1 = (nibbleM-8);
					subs.add(sub1);
					sub1 = sub1*b;
					sub2 = (nibbleL-8);
					subs.add(sub2);
					sub2 = sub2*b;
					sample1 = sample2 + sub1;
					samples.add(sample1);
					sample2 = sample1 + sub2;
					samples.add(sample2);
					bufferOut[counter] = (byte)sample1;
					bufferOut[counter + 1] = (byte)sample2;
					counter += 2;
				}
		
			}
			linearPCM = new AudioFormat(8000,8,1,true,false);
			
			//Write DPCMsub(audio_code).txt
			System.out.println("Writing DPCMsub" + audio_code + ".csv");
			BufferedWriter bw1 = null;
			String filename1 = (path + "\\DPCMsub" + audio_code + ".csv");
			File file1 = new File(filename1);
			if (!file1.exists())
				file1.createNewFile();
			FileWriter fw1 = new FileWriter(file1);
			bw1 = new BufferedWriter(fw1);
			for (int i=0; i<subs.size(); i+=2) {
				bw1.write("" + subs.get(i) + " " + subs.get(i+1));
				bw1.newLine();
			}
			if (bw1 != null)
				bw1.close();
			System.out.println("DPCMsub" + audio_code + ".csv Finished.");
			
			//Write DPCMsamples(audio_code).txt
			System.out.println("Writing DPCMsamples" + audio_code + ".csv");
			BufferedWriter bw2 = null;
			String filename2 = (path + "\\DPCMsamples" + audio_code + ".csv");
			File file2 = new File(filename2);
			if (!file2.exists())
				file2.createNewFile();
			FileWriter fw2 = new FileWriter(file2);
			bw2 = new BufferedWriter(fw2);
			for (int i=0; i<samples.size(); i+=2) {
				bw2.write("" + samples.get(i) + " " + samples.get(i+1));
				bw2.newLine();
			}
			if (bw2 != null)
				bw2.close();
			System.out.println("DPCMsample" + audio_code + ".csv Finished.");
			
			
		}
		
		SourceDataLine lineOut = AudioSystem.getSourceDataLine(linearPCM);
		lineOut.open(linearPCM,bufferOut.length);
		lineOut.start();
		System.out.println("Playing Audio Clip.");
		lineOut.write(bufferOut,0,bufferOut.length);
		lineOut.stop();
		lineOut.close();
		System.out.println("Clip finished."); 
		return bufferOut;
	}
	
	public void ithakiCopter(int servPort, String path) throws IOException {
		
		DatagramSocket r = new DatagramSocket(servPort);
		r.setSoTimeout(5000);
		String message = null;
		ArrayList<String> records = new ArrayList<String>();
		
		byte[] rxbuffer = new byte[5000];
		DatagramPacket packToApp = new DatagramPacket(rxbuffer,rxbuffer.length);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		
		for (int i=0; i<60; i++) {
			try{
				r.receive(packToApp);
				System.out.println("IthakiCopter Packet has been received.");
				message = new String(rxbuffer, 0, rxbuffer.length);
				records.add(message);
				System.out.println(message);
			} catch (IOException ex1) {
				System.out.println(ex1.getMessage());
			}
		}
		r.close();
		System.out.println("Creating ithakiCopter" + ithakicopter_code + ".txt");
		String filename = path + "\\ithakiCopter" + ithakicopter_code + ".txt";
		BufferedWriter bw = null;
		File file = new File(filename);
		if (!file.exists())
			file.createNewFile();
		FileWriter fw = new FileWriter(file);
		bw = new BufferedWriter(fw);
		for(int i=0; i<records.size(); i++) {
			bw.write("" + records.get(i));
			bw.newLine();
		}
		if (bw != null)
			bw.close();
		System.out.println("ithakiCopter" + ithakicopter_code + ".txt has been created.");
		
	}
	
	public void vehicle(String vehi_code, String PID, String path) throws IOException {
		DatagramSocket s = new DatagramSocket();
		DatagramSocket r = new DatagramSocket(clientPort);
		InetAddress serverAddress = InetAddress.getByAddress(serverIP);
		r.setSoTimeout(5000);
		
		String filename = path + "\\OBD" + vehi_code +"_M01_PID" + PID + ".txt";

		vehi_code += "OBD=01 " + PID + "\r";
		
		byte[] code = vehi_code.getBytes();
		DatagramPacket packToIthaki = new DatagramPacket(code, code.length, serverAddress, serverPort);
		
		byte[] rxbuffer = new byte[5000];
		DatagramPacket packToApp = new DatagramPacket(rxbuffer, rxbuffer.length);
		
		BufferedWriter bw = null;
		File file = new File(filename);
		if (!file.exists())
			file.createNewFile();
		FileWriter fw = new FileWriter(file, true);
		bw = new BufferedWriter(fw);
		
		long startTime = System.currentTimeMillis();
		String message = "";
		
		while((System.currentTimeMillis() - startTime)<240*1000) {
			s.send(packToIthaki);
			System.out.println("Packet send.");	
			try {
				r.receive(packToApp);
				System.out.println("Packet received.");
				message = new String(rxbuffer, 0, packToApp.getLength());
				System.out.println(message);
				bw.write("" + message.replace(' ', ',') + "\r");
			}catch (IOException ex1) {
				System.out.println(ex1.getMessage());
			}			
		}
		if (bw != null)
			bw.close();
		s.close();
		r.close();
	}

	
}

