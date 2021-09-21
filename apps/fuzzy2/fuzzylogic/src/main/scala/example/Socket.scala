import java.io._;
import java.net._;
import scala.io.StdIn.readLine

object Socket {
  def sendMsg(msg:String){
    var host1= "10.1.8.80"
    val port= 12346
    val soc = new Socket(host1, port)
    val dout = new DataOutputStream(soc.getOutputStream());
    //val in = new DataInputStream(soc.getInputStream());
    
    dout.writeUTF(msg);
    dout.flush();
    //val rmsg = in.readUTF();
    dout.close()
    
    soc.close();
  }
}