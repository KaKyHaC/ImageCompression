package com.divan.imagecompression;

/**
 * Created by Димка on 13.10.2016.
 */

public class Flag {
    final static int OneFile=16,Enlargement=32,DC=64;
    enum QuantizationState {Without,First};
    enum Encryption{Without,First};
    byte f;

    Flag(byte val){
        f=val;
    }
    public QuantizationState getQuantization(){
        byte q=(byte)(f&3);
        if(q==0)
            return QuantizationState.Without;
        else if(q==1)
            return  QuantizationState.First;
        else
            return null;
    }
    public void setQuantization(QuantizationState qs){

        switch (qs) {
            case Without:f&=(~3);
                break;
            case First:f|=(1);f&=(~2);
                break;
        }


    }

    public Encryption getEncryption(){
        byte e=(byte)(f&12);
        switch (e){
            case 0:return Encryption.Without;
            case 4:return Encryption.First;
            default: return null;
        }
    }
    public void setEncryption(Encryption e){
        switch (e) {
            case Without: f&=(~12);
                break;
            case First:f|=(4);f&=(~8);
                break;
        }




    }

    public boolean isOneFile(){
        return (f&OneFile)!=0;
    }
    public void setOneFile(boolean o){
        if(o==true){
            f|=OneFile;
        }
        else {
            f&=(~OneFile);
        }
    }

    public boolean isEnlargement(){
        return (f&Enlargement)!=0;
    }
    public void setEnlargement(boolean o){
        if(o==true){
            f|=Enlargement;
        }
        else {
            f&=(~Enlargement);
        }
    }

    public boolean isDC(){
        return (f&DC)!=0;
    }
    public void setDC(boolean o){
        if(o==true){
            f|=DC;
        }
        else {
            f&=(~DC);
        }
    }

    @Override
    public String toString() {
        return Byte.toString(f);
    }
}
