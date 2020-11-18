import java.net.ServerSocket;
import java.util.Random;
import java.util.Scanner;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.Date;
import java.util.StringTokenizer;


public class GoServer 
{
	static Object o = new Object();

	public static class weiqi
	{
		private int n;	
		private int board[][];
		private int boardhistory[][][];
		private int step;
		private boolean eat[] = new boolean[4];
		private int sign[][];
		private int eatcount[] = new int[5];
		private int s;
		private int kostone[][];
		private int kostonelast[][];
		private boolean kosign;
		
		private int dir[][] = {{-1,0,1,0}, {0,1,0,-1}};  //👆👉👇👈
		
		Random rd = new Random();
		
		weiqi(int n)
		{
			this.n = n;
			this.step = 0;
			this.s = 1;

			kostone = new int [2][2];
			kostonelast = new int[2][2];
			for(int i=0;i<2;i++)
				for(int j=0;j<2;j++)
				{
					this.kostonelast[i][j] = -1;
					this.kostone[i][j] = -1;
				}
		}

		private void CreateRandomBoard()
		{
			board = new int[n][n];
			boardhistory = new int[512][n][n];
			for(int i=0;i<n;i++)
				for(int j=0;j<n;j++)
					board[i][j] = rd.nextInt(3);
		}
		
		private void CreateTestBoard()
		{
			board = new int[n][n];
			boardhistory = new int[512][n][n];
			for(int i=0;i<n;i++)
				for(int j=0;j<n;j++)
					board[i][j] = 0;
			
			
			//board[0][0] = 1;
			//board[0][1] = 1;
			//board[1][0] = 2;
			//board[1][1] = 2;
			
			//board[2][3] = 1;
			//board[2][4] = 2;
			//board[1][4] = 1;
			//board[2][5] = 1;
		}
		
		private void PrintBoard()
		{
			for(int i=0;i<n;i++)
				for(int j=0;j<n;j++)
					if(j<n-1)
						System.out.print(board[i][j]+" ");
					else System.out.println(board[i][j]);
		}

		private String BoardToString(int board[][])
		{
			String b="";
			for(int i=0;i<n;i++)
				for(int j=0;j<n;j++)
					b = b+Integer.toString(board[i][j]);
			return b;
		}

		//能放?
		private boolean CanSet(int stone, int x, int y)
		{
			if(board[x][y] != 0)
				return false;
			kostonelast = kostone;
			if(CanEat(stone, x, y))
			{
				if(ko())
				{

					return false;
				}
				else
				{
					for(int j=0;j<n;j++)
						for(int k=0;k<n;k++)
							for(int i=1;i<5;i++)
								if(sign[j][k]==i && eat[i-1])
									board[j][k] = 0;
					return true;
				}
			}
			else
			{
				board[x][y] = stone;
				if(FindLiberty(stone, x, y, sign, 5))
					return true;
				else {
					board[x][y] = 0;
					return false;
				}
			}
		}
		
		//当stone下在xLoc,yLoca时是否会使四周敌棋无气
		private boolean CanEat(int stone, int x, int y)
		{

			boolean ce = false;
			kosign = false;
			int xNext,yNext;
			sign = new int[n][n];
			for(int i=0;i<n;i++)
				for(int j=0;j<n;j++)
					sign[i][j]=0;
			kostone = new int[2][2];
			for(int i=0;i<2;i++)
				for(int j=0;j<2;j++)
					kostone[i][j] = -1;

			int Enemystone = 3 - stone;
			board[x][y] = stone;
			for(int i=0;i<4;i++)
			{
				eat[i] = false;
				eatcount[i]=0;
				xNext = x+dir[0][i];
				yNext = y+dir[1][i];
				if(xNext>=0 && xNext<n && yNext>=0 && yNext<n && board[xNext][yNext] == Enemystone && sign[xNext][yNext] == 0)
				{
					if(!FindLiberty(Enemystone, xNext, yNext, sign,i+1))
					{
						ce = true;
					}

				}
			}
			board[x][y] = 0;
			if(kosign)
			{
				kostone[0][0] = x;
				kostone[0][1] = y;
			}

			return ce;
		}

		//返回x行y列棋子及其相连棋子的气
		private boolean FindLiberty(int stone, int x, int y, int sign[][],int s)
		{
			int xNext, yNext;
			eatcount[s-1]++;
			sign[x][y] = s;
			for(int i=0;i<4;i++)
			{
				xNext = x+dir[0][i];
				yNext = y+dir[1][i];
				if(xNext>=0 && xNext<n && yNext>=0 && yNext<n)
					if(board[xNext][yNext]==0)
						return true;   //有气
			}
			for(int i=0;i<4;i++)
			{
				xNext = x+dir[0][i];
				yNext = y+dir[1][i];
				if(xNext>=0 && xNext<n && yNext>=0 && yNext<n && board[xNext][yNext] == stone && sign[xNext][yNext] == 0)
					return FindLiberty(stone, xNext, yNext,sign,s);
			}
			eat[s-1] = true;
			if(eatcount[s-1] == 1)
			{
				kosign = true;
				kostone[1][0] = x;
				kostone[1][1] = y;
			}
			return false;   //无气
		}

		private boolean ko()
		{
			boolean j1=false,j2=false;

			if(kostone[0][0] == kostonelast[1][0] && kostone[0][1] == kostonelast[1][1]
					&& kostone[0][0] != -1 && kostone[0][1] != -1 && kostonelast[0][0] == kostone[1][0]
					&& kostonelast[0][1] == kostone[1][1] && kostonelast[0][0] != -1 && kostonelast[0][1] != -1)
				return true;
			else return false;
		}
	}

	private static class GoThread extends Thread
	{
		private Socket s;
		private weiqi wq;
		private int n;

		GoThread(Socket connect, weiqi wq, int n)
		{
			this.s = connect;
			this.wq = wq;
			this.n = n;
		}

		public void run()
		{
			try
			{
				System.out.println(s.getInetAddress()+":"+s.getPort()+" connect");
				DataInputStream dis = new DataInputStream(s.getInputStream());
				DataOutputStream dos = new DataOutputStream(s.getOutputStream());

				dos.writeInt(n);
				boolean t=false;
				if(n == 2)
					Thread.sleep(50);
				while(true)
				{
					synchronized (o)
					{
						dos.writeUTF(wq.BoardToString(wq.board));

						int xinput = dis.readInt();
						int yinput = dis.readInt();

						if (t = wq.CanSet(n, xinput, yinput))
						{
							wq.board[xinput][yinput] = n;
							dos.writeUTF(wq.BoardToString(wq.board));
							System.out.println(t);
							//wq.s = 3 - wq.s;
							o.notify();
							o.wait();
						}
						else
						{
							dos.writeUTF("cantset");
							System.out.println(t);
						}


					}
				}

			}
			catch(IOException | InterruptedException ex){ex.printStackTrace();}


		}
	}
	
	public static void main(String[] args)
	{
		weiqi wq = new weiqi(19);
		wq.CreateTestBoard();

		try
		{
			ServerSocket server = new ServerSocket(123);

			Socket s1 = server.accept();
			Socket s2 = server.accept();

			Thread t1 = new GoThread(s1,wq,1);
			Thread t2 = new GoThread(s2,wq,2);
			t1.start();

			t2.start();


		}
		catch(IOException ex){ex.printStackTrace();}
	}
}	
