// SMTPDemo.java
//SMTP client code taken from Jeff Friesen's Java Email demo
//http://www.quepublishing.com/articles/article.aspx?p=26672&seqNum=3


import java.io.*;
import java.net.*;

class SMTPDemo
{
  public static void main(String [] args)
  {
   String SMTPServer = "smtp.gmail.com";
   int SMTPPort = 587;

   Socket client = null;

   try
   {
     // Attempt to create a client socket connected to the SMTP 
     // server.
     // program.

     client = new Socket(SMTPServer, SMTPPort);

     // Create a buffered reader for line-oriented reading from the
     // standard input device.

     BufferedReader stdin;
     stdin = new BufferedReader(new InputStreamReader (System.in));

     // Create a buffered reader for line-oriented reading from the
     // socket.

     InputStream is = client.getInputStream ();
     BufferedReader sockin;
     sockin = new BufferedReader(new InputStreamReader (is));

     // Create a print writer for line-oriented writing to the 
     // socket.

     OutputStream os = client.getOutputStream ();
     PrintWriter sockout;
     sockout = new PrintWriter(os, true); // true for auto-flush

     // Display SMTP greeting from SMTP server program.

     System.out.println("S:" + sockin.readLine ());

     while (true)
     {
      // Display a client prompt.

      System.out.print("C:");

      // Read a command string from the standard input device.

      String cmd = stdin.readLine();

      // Write the command string to the SMTP server program.

      sockout.println(cmd);

      // Read a reply string from the SMTP server program.

      String reply = sockin.readLine();

      // Display the first line of this reply string.

      System.out.println("S:" + reply);

      // If the DATA command was entered and it succeeded, keep
      // writing all lines until a line is detected that begins
      // with a . character. These lines constitute an email
      // message.

      if (cmd.toLowerCase().startsWith("data") &&
        reply.substring(0, 3).equals("354"))
      {
        do
        {
          cmd = stdin.readLine();

          if (cmd != null && cmd.length() > 1 &&
            cmd.charAt (0) == '.')
            cmd = "."; // Must be no chars after . char.

          sockout.println(cmd);

          if (cmd.equals("."))
            break;
        }
        while (true);

        // Read a reply string from the SMTP server program.

        reply = sockin.readLine();

        // Display the first line of this reply string.

        System.out.println("S:" + reply);

        continue;
      }

      // If the QUIT command was entered, quit.

      if (cmd.toLowerCase().startsWith("quit"))
        break;
     }
   }
   catch(IOException e)
   {
     System.out.println(e.toString ());
   }
   finally
   {
     try
     {
      // Attempt to close the client socket.

      if (client != null)
        client.close();
     }
     catch (IOException e)
     {
     }
   }
  }
}