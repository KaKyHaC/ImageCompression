package com.divan.imagecompression;

import java.math.BigInteger;

/**
 * Created by Димка on 27.09.2016.
 */
public class OPC { //singelton
    protected final int SIZEOFBLOCK = 8;

    static OPC opc=new OPC();

    private short  [][] dataOrigin;
    private DataOPC dataOPC;

    public static OPC getInstance()
    {
        return opc;
    }
    private OPC(){}



    private void directOPC(Flag flag){
        MakeUnSigned();
        if(flag.isDC())
            DCminus();
        FindeBase();

        if(flag.isLongCode())
            OPCdirectUseOnlyLong();
        else
            OPCdirect();
    }
    private void reverseOPC(Flag flag){
        if(flag.isLongCode())
            OPCreverseUseOnlyLong();
        else
            OPCreverse();

        if(flag.isDC())
            DCplus();
        MakeSigned();

    }


    private void FindeBase() {
        for(int i=0;i<SIZEOFBLOCK;i++)
        {
            dataOPC.base[i]=(short)(dataOrigin[i][0]);
            for(int j=0;j<SIZEOFBLOCK;j++)
            {
                if(dataOPC.base[i]<(dataOrigin[i][j]))
                {
                    dataOPC.base[i]=(dataOrigin[i][j]);
                }
            }
            dataOPC.base[i]++;
        }
    }

    private void MakeUnSigned ()    {
        for(int i=0;i<SIZEOFBLOCK;i++)
        {
            for(int j=0;j<SIZEOFBLOCK;j++)
            {
                if(dataOrigin[i][j]<(short)0)
                {
                    dataOrigin[i][j]=(short)(dataOrigin[i][j]*(-1));
                    dataOPC.sign[i][j]=false;
                }
                else
                {
                    dataOPC.sign[i][j]=true;
                }
            }
        }
    }
    private void MakeSigned()    {
        for(int i=0;i<SIZEOFBLOCK;i++)
        {
            for(int j=0;j<SIZEOFBLOCK;j++)
            {
                if(dataOPC.sign[i][j]==false)
                {
                    dataOrigin[i][j]=(short)(dataOrigin[i][j]*(-1));
                }

            }
        }
    }

    private void DCminus(){
        dataOPC.DC=(dataOrigin[0][0]);
        dataOrigin[0][0]=0;

    }
    private void DCplus(){
        dataOrigin[0][0]=dataOPC.DC;
    }


    private void OPCdirect(){//TODO diagonal for optimization
        dataOPC.N=OPCdirectLong();
/*        BigInteger base= new BigInteger("1");
        for(int i=SIZEOFBLOCK-1;i>=0;i--)
        {
            for(int j=SIZEOFBLOCK-1;j>=0;j--)
            {
                if(dataOrigin[i][j]!=0)
                {
                    dataOPC.N=dataOPC.N.add(base.multiply(BigInteger.valueOf(dataOrigin[i][j])));
                }
                base=base.multiply(BigInteger.valueOf(dataOPC.base[i]));

            }
        }*/
    }
    private BigInteger OPCdirectLong(){

        long base= 1;
        long res=0;
        long bufbase;
        for(int i=SIZEOFBLOCK-1;i>=0;i--)
        {
            for(int j=SIZEOFBLOCK-1;j>=0;j--)
            {
                bufbase=base*dataOPC.base[i];

               /* if(bufbase<res)
                    System.out.println("res");*/
/*                if(bufbase<base)
                    System.out.println("base");*/
                if(bufbase>Parameters.getMAXLONG())//is true ?
                {

                    System.out.println("go");
                    return OPCdirectBI(res,base,i,j);
                }
                if(dataOrigin[i][j]!=0)
                {
                    res+=base*(dataOrigin[i][j]);
                }
                base=bufbase;

            }
        }
        return BigInteger.valueOf(res);
    }
    private BigInteger OPCdirectBI(long res,long baseval,int i1,int j1){

        BigInteger val=BigInteger.valueOf(res);
        BigInteger base=BigInteger.valueOf(baseval);

        int i=i1;
        int j=j1;
        for(;i>=0;i--)
        {
            for(;j>=0;j--)
            {
                if(dataOrigin[i][j]!=0)
                {
                    val=val.add(base.multiply(BigInteger.valueOf(dataOrigin[i][j])));
                }
                base=base.multiply(BigInteger.valueOf(dataOPC.base[i]));

            }
            j=SIZEOFBLOCK-1;
        }
        return val;
    }

    private void OPCreverse() // method copy from C++ Project MAH
    {
        BigInteger copy=new BigInteger("1");
        for (int i = SIZEOFBLOCK - 1; i >= 0; i--)
        {
            for (int j = SIZEOFBLOCK - 1; j >= 0; j--)
            {
                BigInteger a, b;

                a = dataOPC.N.divide( copy);
                copy =copy.multiply(BigInteger.valueOf(dataOPC.base[i]));

                b =dataOPC.N.divide( copy);
                b =b.multiply(BigInteger.valueOf( dataOPC.base[i]));
                dataOrigin[i][j] = (a.subtract(b)).shortValue() ;
            }
        }
    }

    private void OPCdirectUseOnlyLong(){
        long base= 1;
        long res=0;
        long bufbase;
        for(int i=SIZEOFBLOCK-1;i>=0;i--)
        {
            for(int j=SIZEOFBLOCK-1;j>=0;j--)
            {
                bufbase=base*dataOPC.base[i];
                if(bufbase>Parameters.getMAXLONG())//is true ?
                {
                    dataOPC.Code.add(res);
                    base= 1;
                    res=0;
                    bufbase=base*dataOPC.base[i];

                }
                if(dataOrigin[i][j]!=0)
                {
                    res+=base*(dataOrigin[i][j]);
                }
                base=bufbase;


            }
        }
        dataOPC.Code.add(res);
    }
    private void OPCreverseUseOnlyLong(){
        long copy=1;
        int index=0;
        long curN=dataOPC.Code.elementAt(index);
        long nextcopy;
        for (int i = SIZEOFBLOCK - 1; i >= 0; i--)
        {
            if(dataOPC.base[i]==0)//for wrong password;
                dataOPC.base[i]=1;
            for (int j = SIZEOFBLOCK - 1; j >= 0; j--)
            {
                nextcopy=copy*dataOPC.base[i];
                if(nextcopy>Parameters.getMAXLONG()||nextcopy<0)
                {
                    copy=1;
                    index++;
                    nextcopy=copy*dataOPC.base[i];
                    if(index<dataOPC.Code.size())
                        curN=dataOPC.Code.elementAt(index);
                }
                long a, b;


                a = curN/( copy);
                copy =nextcopy;

                b =curN/( copy);
                b =b*(( dataOPC.base[i]));
                dataOrigin[i][j] =(short) (a-(b));
            }
        }
    }




    public short[][]getDataOrigin(DataOPC dopc,Flag flag){
        this.dataOPC=dopc;
        dataOrigin=new short[SIZEOFBLOCK][SIZEOFBLOCK];
        reverseOPC(flag);
        return dataOrigin;
    }
    public DataOPC getDataOPC(short[][] dataOrigin,Flag flag){
        this.dataOrigin = dataOrigin;
        dataOPC=new DataOPC();
        directOPC(flag);
        return  dataOPC;
    }

    public DataOPC findBase(short[][] dataOrigin,Flag flag){
        this.dataOrigin = dataOrigin;
        dataOPC=new DataOPC();

        MakeUnSigned();
        if(flag.isDC()) {
            DCminus();
        }
        FindeBase();

        return  dataOPC;}
    public DataOPC directOPCwithFindedBase(short[][] dataOrigin,DataOPC d,Flag flag){
        this.dataOrigin = dataOrigin;
        this.dataOPC=d;

        MakeUnSigned();
        if(flag.isDC()) {
            DCminus();
        }

        if(flag.isLongCode())
            OPCdirectUseOnlyLong();
        else
            OPCdirect();
        return dataOPC;
    }

}
