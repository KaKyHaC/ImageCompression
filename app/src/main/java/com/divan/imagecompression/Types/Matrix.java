package com.divan.imagecompression.Types;

public class Matrix
{

    public short [][] a,b,c;
    public State state;
    public Flag f;
    public int Width, Height;

    public Matrix(int width, int height,Flag flag) {
        this.f=flag;
        Width = width;
        Height = height;

        a=new short[width][height];
        b=new short[width][height];
        c=new short[width][height];


    }
}
