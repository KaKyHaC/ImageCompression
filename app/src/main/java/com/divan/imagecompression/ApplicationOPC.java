package com.divan.imagecompression;

import android.os.AsyncTask;

/**
 * Created by Димка on 09.10.2016.
 */
public class ApplicationOPC {//singelton


    final static int SIZEOFBLOCK=8;
    private Matrix matrix;
    private BoxOfOPC opcs;
    private int duWidth,duHeight;
    private int Width,Height;
    private FileStream fs;

    Parameters param=Parameters.getInstanse();
    Steganography st=Steganography.getInstance();

    static private ApplicationOPC Aopc=new ApplicationOPC();
    private ApplicationOPC(){}

    public static ApplicationOPC getInstance(){return Aopc;}


    private void directOPC(short[][]dataOrigin,DataOPC[][]dopc){

        duWidth=dopc.length;
        duHeight=dopc[0].length;
        Width=dataOrigin.length;
        Height=dataOrigin[0].length;

        OPC opc=OPC.getInstance();


        short[][]buf=new short[SIZEOFBLOCK][SIZEOFBLOCK];

        for (int i = 0; i < duWidth; i++) {
            for (int j = 0; j < duHeight; j++) {

                for (int x = 0; x < DCT.SIZEOFBLOCK; x++) {
                    for (int y = 0; y < DCT.SIZEOFBLOCK; y++) {
                        short value = 0;
                        int curX=i * DCT.SIZEOFBLOCK + x;
                        int curY=j * DCT.SIZEOFBLOCK + y;
                        if (curX< Width && curY < Height)
                            value = dataOrigin[curX][curY];
                        buf[x][y] = value;
                        // DU[i][j].setValue(val,x,y);
                    }
                }
                dopc[i][j]=opc.getDataOPC(buf,matrix.f);

            }
        }
    }
    private short[][] reverceOPC(DataOPC[][]dopc){

        duWidth=dopc.length;
        duHeight=dopc[0].length;
        Width=duWidth*SIZEOFBLOCK;
        Height=duHeight*SIZEOFBLOCK;
        short[][] res=new short[Width][Height];

        OPC opc=OPC.getInstance();
        boolean DC=fs.getFlag().isDC();
        short[][]buf;//=new short[SIZEOFBLOCK][SIZEOFBLOCK];

        for (int i = 0; i < duWidth; i++) {
            for (int j = 0; j < duHeight; j++) {//j=3 erro

                buf=opc.getDataOrigin(dopc[i][j],matrix.f);

                for (int x = 0; x < DCT.SIZEOFBLOCK; x++) {
                    for (int y = 0; y < DCT.SIZEOFBLOCK; y++) {

                        int curX=i * DCT.SIZEOFBLOCK + x;
                        int curY=j * DCT.SIZEOFBLOCK + y;
                        if (curX< Width && curY < Height)
                            res[curX][curY] = buf[x][y];
                        // DU[i][j].setValue(val,x,y);

                    }
                }


            }
        }
        return res;
    }

    public void directOPC(){
        directOPC(matrix.a,opcs.a);
        directOPC(matrix.b,opcs.b);
        directOPC(matrix.c,opcs.c);
    }
    public void reverceOPC(){// create matrix with corect size of b and c (complite)
       matrix.a=reverceOPC(opcs.a);
       matrix.b=reverceOPC(opcs.b);
       matrix.c=reverceOPC(opcs.c);

    }

    private void WriteToFile(String file){

        fs.Write(file,opcs,matrix.f);
    }
    private void ReadFromFile(String file){

        opcs=fs.Read(file);

    }

    public void FromMatrixToFile(MainActivity.FromBMPtoFile As,Matrix matrix, String file){
        this.matrix = matrix;
        opcs=new BoxOfOPC(matrix.Width,matrix.Height,matrix.f.isEnlargement());
        duWidth=opcs.width;
        duHeight=opcs.height;
        Width=matrix.Width;
        Height=matrix.Height;

        fs=FileStream.getInstance();

        if(matrix.f.isSteganography()||param.isSteganography)//TODO interface
            st.WriteMassageFromFileToMatrix(matrix,param.PathReadMassage);

        if(matrix.f.isGlobalBase())
            directOpcGlobalBase(param.n,param.m);
        else
            directOPC();

        if(matrix.f.isPassword())
        {
            Encryption(Parameters.getPasswordFinal());
        }


        As.Update(70);
        WriteToFile(file);
    }
    public Matrix FromFileToMatrix( MainActivity.FromFileToBMP As,String file){
        fs=FileStream.getInstance();
        ReadFromFile(file);
        if(opcs.a.length==opcs.b.length){
            duWidth=opcs.width=opcs.b.length;
            duHeight=opcs.height=opcs.b[0].length;
        }

        else {
            duWidth = opcs.width = opcs.b.length * 2;// size like 2 Chromasity (we lose 0-7 pixels)
            duHeight = opcs.height = opcs.b[0].length * 2;
        }
        Width=duWidth*SIZEOFBLOCK;
        Height=duHeight*SIZEOFBLOCK;
        matrix = new Matrix(Width,Height,fs.getFlag());

        if(matrix.f.isPassword())
        {
            Encryption(Parameters.getPasswordFinal());
        }

        As.Update(30);
        reverceOPC();
        matrix.state=State.DCT;

        if(matrix.f.isSteganography()||param.isSteganography)//TODO interface
            st.ReadMassageFromMatrixtoFile(matrix,param.PathWriteMassage);

        return matrix;
    }

    /****************************************/

    private void directOpcGlobalBase(int n,int m){

        directOpcGlobalBase(n,m,matrix.a,opcs.a ); //TODO set a
        directOpcGlobalBase(n,m,matrix.b,opcs.b);
        directOpcGlobalBase(n,m,matrix.c,opcs.c);
    }
    private void directOpcGlobalBase(int n,int m,short[][]dataOrigin,DataOPC[][]dopc){
        duWidth=dopc.length;
        duHeight=dopc[0].length;
        Width=dataOrigin.length;
        Height=dataOrigin[0].length;

        findAllBase(dataOrigin,dopc);

        setMaxBaseForAll(n, m,dopc);

        directOPCwithGlobalBase(dataOrigin, dopc);

    }
    private void findAllBase(short[][]dataOrigin,DataOPC[][]dopc){
        OPC opc=OPC.getInstance();


        short[][]buf=new short[SIZEOFBLOCK][SIZEOFBLOCK];

        for (int i = 0; i < duWidth; i++) {
            for (int j = 0; j < duHeight; j++) {

                for (int x = 0; x < DCT.SIZEOFBLOCK; x++) {
                    for (int y = 0; y < DCT.SIZEOFBLOCK; y++) {
                        short value = 0;
                        int curX=i * DCT.SIZEOFBLOCK + x;
                        int curY=j * DCT.SIZEOFBLOCK + y;
                        if (curX< Width && curY < Height)
                            value = dataOrigin[curX][curY];
                        buf[x][y] = value;
                        // DU[i][j].setValue(val,x,y);
                    }
                }
                dopc[i][j]=opc.findBase(buf,matrix.f);

            }
        }
    }
    private void setMaxBaseForAll(int n,int m,DataOPC[][]dopc){
        int i = 0;
        int j = 0;
        int IndexI=i,IndexJ=j;

        for (; i < duWidth; IndexI+=n,i=IndexI,IndexJ=0,j=IndexJ) {
            for (; j < duHeight; i=IndexI,IndexJ+=m,j=IndexJ) {

                short [] maxBase=new short[SIZEOFBLOCK];
                for(int a=0;a<n&&i<duWidth;a++,i++){
                    j=IndexJ;
                    for(int b=0;b<m&&j<duHeight;b++,j++){
                        maxBase=findMaxInArry(maxBase,dopc[i][j].base);
                    }
                }

                i=IndexI;
                for(int a=0;a<n&&i<duWidth;a++,i++){
                    j=IndexJ;
                    for(int b=0;b<m&&j<duHeight;b++,j++){
                        dopc[i][j].base=maxBase;
                    }
                }
            }
        }
    }
    private short[] findMaxInArry(short[] a,short[] b){
        assert (a.length==b.length);
        for(int i=0;i<a.length;i++){
            a[i]=(a[i]>b[i])?a[i]:b[i];
        }
        return a;
    }
    private void directOPCwithGlobalBase(short[][]dataOrigin,DataOPC[][]dopc){
        OPC opc=OPC.getInstance();


        short[][]buf=new short[SIZEOFBLOCK][SIZEOFBLOCK];

        for (int i = 0; i < duWidth; i++) {
            for (int j = 0; j < duHeight; j++) {

                for (int x = 0; x < DCT.SIZEOFBLOCK; x++) {
                    for (int y = 0; y < DCT.SIZEOFBLOCK; y++) {
                        short value = 0;
                        int curX=i * DCT.SIZEOFBLOCK + x;
                        int curY=j * DCT.SIZEOFBLOCK + y;
                        if (curX< Width && curY < Height)
                            value = dataOrigin[curX][curY];
                        buf[x][y] = value;
                        // DU[i][j].setValue(val,x,y);
                    }
                }
                dopc[i][j]=opc.directOPCwithFindedBase(buf,dopc[i][j],matrix.f);

            }
        }
    }


    private void Encryption(String key){
        Encryption.encode(opcs,key);

    }

}
