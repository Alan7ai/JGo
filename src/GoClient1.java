import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class GoClient1
{
    public static void main(String[] args)
    {
        try
        {
            InetAddress ip = InetAddress.getLocalHost();
            int port = 123;
            Socket s = new Socket(ip,port);
            Scanner sc = new Scanner(System.in);

            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            int xinput,yinput;
            int sign;
            String board;
            sign = dis.readInt();
            System.out.println(sign);
            while(true)
            {
                board = dis.readUTF();
                PrintBoard(board,19);

                System.out.println("input x:");
                xinput = Integer.parseInt(sc.nextLine());
                System.out.println("input y:");
                yinput = Integer.parseInt(sc.nextLine());

                dos.writeInt(xinput);
                dos.writeInt(yinput);

                board = dis.readUTF();
                if(board.equals("cantset"))
                    System.out.println("cantset");
                else PrintBoard(board,19);

            }

        }
        catch(IOException ex){ex.printStackTrace();}

    }

    public static void PrintBoard(String board, int n)
    {
        if(board.length() != n*n)
            System.out.print("error");
        for(int i=0;i<n*n;i++)
            if(i%n!=n-1)
                System.out.print(board.charAt(i)+" ");
            else System.out.println(board.charAt(i));
        System.out.println('\n');
    }
}
