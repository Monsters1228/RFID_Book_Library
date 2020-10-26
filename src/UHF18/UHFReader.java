package UHF18;

import UHF.Reader18;

public class UHFReader {
	static{
	 	System.loadLibrary("UHF_Reader18");
	}
    int[] Recv=new int[5000];
    int[] SendBuff=new int[5000];
    int result=0x30;
    public int FrmHandle=0;
    int fComAddr=0;
    public int CardNum=0;
    Reader18 tnt = new Reader18();
    int array_clear(int array_clear0[])
    {
        int clear_len=array_clear0.length;
        for(int i=0;i<clear_len;i++)
        {
                array_clear0[i]=0;
        }
        return 0;
    }
    
//    打开设备;ComPort:串口号，ComAddr：读写器地址;baudRate：波特率;
    public int OpenByCom(int ComPort,byte ComAddr,byte baudRate)
    {
    	array_clear(SendBuff);
        SendBuff[0]=ComPort;
        SendBuff[1]=ComAddr;
        SendBuff[2]=baudRate;
        Recv=tnt.OpenComPort(SendBuff);
        if(Recv[0]==0)
        {
            result=0;
            fComAddr=Recv[1];
            FrmHandle=Recv[2];
        }
        else
        {
            result=Recv[0];
            fComAddr=255;
            FrmHandle=-1;
        }
        return result;
    }
//   自动打开串口
    public int AutoOpenCom(byte ComAddr,byte baudRate){
        array_clear(SendBuff);
        SendBuff[0] = ComAddr;
        SendBuff[1]=baudRate;
        Recv=tnt.AutoOpenComPort(SendBuff);
        if(Recv[0]==0)
        {
            result=0;
            fComAddr=Recv[2];
            FrmHandle=Recv[3];
        }
        else
        {
            result=Recv[0];
            fComAddr=255;
            FrmHandle=-1;
        }
        return result;
    }

//    关闭设备,FrmHandle设备句柄;
    public int CloseByCom(int FrmHandle)
    {
    	return tnt.CloseSpecComPort(FrmHandle);
    }

    public int OpenByTcp(String Ipaddr,int Port)
    {
    	array_clear(SendBuff);
        Recv=tnt.OpenNetPort(255, Port, Ipaddr);
        if(Recv[0]==0)
        {
            result=0;
            fComAddr=Recv[1];
            FrmHandle=Recv[2];
        }
        else
        {
            result=Recv[0];
            fComAddr=255;
            FrmHandle=-1;
        }
        return result;
    }
    
    public int CloseByTcp(int FrmHandle)
    {
    	return tnt.CloseNetPort(FrmHandle);
    }
    public String byteToString(byte[] b){
			StringBuffer sb = new StringBuffer("");
			for(int i = 0; i < b.length; i++){
                            String temp=Integer.toHexString(b[i] & 0xff);
                            if(temp.length()==1)
                            {
                                temp="0"+temp;
                            }
			    sb.append(temp);
			}
			return sb.toString().toUpperCase();
    }

    public byte[] stringToByte(String str){
			byte[] b = new byte[str.length()/2];
			for(int i = 0; i < str.length()/2; i++){
				b[i] =(byte)(0xff & Integer.parseInt(str.substring(i*2, i*2+2),16));
			}
			return b;
    }
    
    //询查标签
    public String[] Inventory()
    {
        CardNum=0;
        array_clear(SendBuff);
        SendBuff[0]=fComAddr;
        SendBuff[1]=FrmHandle;
        array_clear(Recv);
        Recv=tnt.Inventory_G2(SendBuff);
        if((Recv[0]!= 0x30)&&(Recv[1]>0))
        {
            CardNum=Recv[1];
            if(CardNum==0) return null;
            String[] EPC=new String[CardNum];
            int index=0;
            for(int m=0;m<CardNum;m++)
            {
                int len=Recv[3+index];
                byte[] epc_arr=new byte[len];
                for(int n=0;n<len;n++)
                {
                    epc_arr[n]=(byte)Recv[4+index+n];
                }
                EPC[m]=byteToString(epc_arr);
                index+=len+1;
            }
            if(CardNum>0)
            return EPC;
        }
        return null;
    }
   
    //设置功率,Power功率
    public int SetPower(byte Power)
    {
        array_clear(SendBuff);
        SendBuff[0]=fComAddr;
        SendBuff[1]=Power;
        SendBuff[2]=FrmHandle;
        return tnt.SetPowerDbm(SendBuff);
    }

    //设置读写器地址
    public int SetAddress(byte Address)
    {
        array_clear(SendBuff);
        SendBuff[0]=fComAddr;
        SendBuff[1]=Address;
        SendBuff[2]=FrmHandle;
        return tnt.WriteComAdr(SendBuff);
    }

  //设置读写器工作频率
    public int SetRegion(byte MaxFre,byte MinFre)
    {
        array_clear(SendBuff);
        SendBuff[0]=fComAddr;
        SendBuff[1]=MaxFre;
        SendBuff[2]=MinFre;
        SendBuff[3]=FrmHandle;
        return tnt.Writedfre(SendBuff);
    }

    
  //设置读写器波特率
    public int SetBaudRate(byte BaudRate)
    {
    	array_clear(SendBuff);
        SendBuff[0]=fComAddr;
        SendBuff[1]=BaudRate;
        SendBuff[2]=FrmHandle;
        return tnt.Writebaud(SendBuff);
    }
    

    //读数据，EPC号，WordPtr读取地址，Num读取长度，Mem读取区域，Psd访问密码
    public String ReadData(String EPC,byte WordPtr,byte Num,byte Mem,byte[] Psd)
    {
        int len=EPC.length()/2;
        byte[]epc_arr=new byte[len];
        epc_arr=stringToByte(EPC);
        array_clear(SendBuff);
        array_clear(Recv);
        SendBuff[0]=fComAddr;
        SendBuff[1]=len;//EPC字节长度
        for(int n=0;n<len;n++)
        {
            SendBuff[2+n]=epc_arr[n];
        }
        SendBuff[2+len]=Mem;
        SendBuff[3+len]=WordPtr;
        SendBuff[4+len]=Num;
        SendBuff[5+len]=Psd[0];
        SendBuff[6+len]=Psd[1];
        SendBuff[7+len]=Psd[2];
        SendBuff[8+len]=Psd[3];
        SendBuff[9+len]=0;//掩码区域，
        SendBuff[10+len]=0;
        SendBuff[11+len]=0;
        SendBuff[12+len]=FrmHandle;
        Recv = tnt.ReadCard_G2(SendBuff);
        if(Recv[0]==0)
        {
            byte[]data=new byte[Num*2];
            for(int m=0;m<Num*2;m++ )
            {
                data[m]=(byte)Recv[2+m];
            }
            return byteToString(data);
        }
        else
        return "";
    }

    //写数据,EPC号，WordPtr写入地址，Num写入长度，Data写入数据,Mem写入区域，Psd访问密码
    public int WriteData(String EPC,byte WordPtr,byte Num,byte[] Data,byte Mem,byte[] Psd)
    {
        int result=0;
        int len=EPC.length()/2;
        byte[]epc_arr=new byte[len];
        epc_arr=stringToByte(EPC);
        array_clear(SendBuff);
        array_clear(Recv);
        SendBuff[0]=fComAddr;
        SendBuff[1]=len;//EPC字节长度
        for(int n=0;n<len;n++)
        {
            SendBuff[2+n]=epc_arr[n];
        }
        SendBuff[2+len]=Mem;
        SendBuff[3+len]=WordPtr;
        SendBuff[4+len]=Num*2;//写入字节数
        int wnum= Num*2;
         for(int p=0;p<wnum;p++)
        {
            SendBuff[5+len+p]=Data[p];
        }
        SendBuff[5+len+wnum]=Psd[0];
        SendBuff[6+len+wnum]=Psd[1];
        SendBuff[7+len+wnum]=Psd[2];
        SendBuff[8+len+wnum]=Psd[3];
        SendBuff[9+len+wnum]=0;//掩码区域
        SendBuff[10+len+wnum]=0;
        SendBuff[11+len+wnum]=0;
        SendBuff[12+len+wnum]=FrmHandle;
        Recv = tnt.WriteCard_G2(SendBuff);
        result=Recv[0];
        return result;
    }

  //写数据,EPC号，WordPtr写入地址，Num写入长度，Data写入数据,Mem写入区域，Psd访问密码
    public int WriteEPC(String EPC,byte[] Psd)
    {
        int result=0;
        int len=EPC.length()/2;
        byte[]epc_arr=new byte[len];
        epc_arr=stringToByte(EPC);
        array_clear(SendBuff);
        array_clear(Recv);
        SendBuff[0]=fComAddr;
        SendBuff[1]=len/2;//EPC字长度
        for(int n=0;n<len;n++)
        {
            SendBuff[2+n]=epc_arr[n];
        }
        SendBuff[2+len]=Psd[0];
        SendBuff[3+len]=Psd[1];
        SendBuff[4+len]=Psd[2];
        SendBuff[5+len]=Psd[3];
        SendBuff[6+len]=FrmHandle;
        Recv = tnt.WriteEPC_G2(SendBuff);
        result=Recv[0];
        return result;
    }

    
    
    
    
    
    
    
    
}
