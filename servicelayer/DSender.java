package servicelayer;

import java.awt.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.imageio.*;
import javax.swing.*;
import net.coobird.thumbnailator.Thumbnails;

public class DSender {

    /**        
        This class is used to send the Screen Shots of own PC to the receiver and acts a Victim.
        @author: Rohit Methwani
    */

    //Data Members
    private Robot robot;
	private DatagramSocket socket;
	private DatagramPacket packet;
    private BufferedImage ss;
    private int packetNumber;
    private byte[] maxPacketsBytes;
    private int byteCounter;
    private boolean condition;
    private int i;
    private int packetNumberr;
    private byte[] buffer;
    private BufferedImage thumbnail;
    private InputStream in;
    private BufferedImage bImageFromConvert;
    private int z=0;
    private ServerSocket eventSocket;
    private Socket tcpSocket;
    private FileOutputStream fos;
    private Thread sending = new Thread(){
        public void run(){
            while(true){
                try{
                    ss = robot.createScreenCapture(new Rectangle(1366,768));
                    splitImage(toBytes(ss));
                    // sleep(3000);
                }
                catch(Exception ioe){
                    System.out.println("Exception from thread:"+ioe);
                }
            }
        }
    };

    /**
        Constructor:
        Initializes the DatagramSocket and Robot class objects.
    */
	DSender(){
		try{
            // eventSocket = new ServerSocket(5000);
			socket = new DatagramSocket();
            robot = new Robot();
		}
		catch(Exception ioe){
			System.out.println(ioe);
		}
    }

    /**
        This method converts the given BufferedImage into byte array using ByteArrayOutputStream.
        @param: BufferedImage which has to be converted.
        @return: Byte array of the given BufferedImage
    */
    private byte[] toBytes(BufferedImage image){
        try{
            
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image,"jpeg", out);
            byte[] imageBytes = out.toByteArray();
            return imageBytes;
        }
        catch(Exception ioe){
            System.out.println(ioe);
            return null;
        }
    }

    /**
        This method sends the Screen Shot of the current window.
        @param: Byte array of the image.
        @return: Nothing.
    */
   public void splitImage(byte[] mainImage) throws Exception{
        in = new ByteArrayInputStream(mainImage);
        bImageFromConvert = ImageIO.read(in);
        thumbnail = Thumbnails.of(bImageFromConvert).size(1280,720).asBufferedImage();

        byte[] image = toBytes(thumbnail);

        System.out.println("Split image");
        int packetNumber=1;
        String imageLength = "Count "+image.length;
        byte[] maxPacketsBytes = imageLength.getBytes();
        System.out.println("Total data:"+image.length);
        System.out.println("MaxPackets:"+(image.length/1024));
        System.out.println("After dividing:"+(image.length/1024)*1024);
        packet = new DatagramPacket(maxPacketsBytes,maxPacketsBytes.length,InetAddress.getByName("25.57.112.106"),8888);
        socket.send(packet);
        int byteCounter = 0;
        int packetNumberr=0;
        boolean condition = true;
        int i=0;
        while(condition) {
            byte[] buffer = new byte[1024];
            i = 0;
            while(byteCounter<image.length && i<1024) {
                buffer[i] = image[byteCounter];
                byteCounter++;
                i++;
                packetNumberr++;
            }
            DatagramPacket packetToBeSent = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("25.57.112.106"), 8888);
            socket.send(packetToBeSent);
            if (byteCounter == image.length) {
                condition = false;
            }
        }
        System.out.println("Total bytes sent:"+image.length);
    }

    public void acceptEvents(){
        try{
            tcpSocket = eventSocket.accept();
            Scanner scan = new Scanner(tcpSocket.getInputStream());
            String event = scan.nextLine();
            System.out.println("Received event:"+event);
        }
        catch(Exception ioe){
            System.out.println(ioe);
        }
    }

    /**
        FOR UNIT TESTING.
    */
    public static void main(String[] args)throws Exception {
        DSender re = new DSender();
        System.out.println("Created object");
        // re.ss = re.robot.createScreenCapture(new Rectangle(1366,768));
        // re.splitImage(re.toBytes(re.ss));       
        re.sending.start();
        // re.eventThread.start();
    }

    Thread eventThread = new Thread(){
        public void run(){
            acceptEvents();
        }
    };
}
