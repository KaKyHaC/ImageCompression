package com.divan.imagecompression.Utils;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.*;

/**
 * Created by Dima on 18.07.2017.
 */
public class DCTMultiThread_Test {
    final short[][] DEFAULT;
    short[][] afterDirect,afterReverse,beforDirect,beforReverse;

    public DCTMultiThread_Test() {
        Random random=new Random();
        DEFAULT = new short[8][8];
        for (int i = 0; i < DEFAULT.length; i++) {
            for (int j = 0; j < DEFAULT[i].length; j++) {
                DEFAULT[i][j]=(short)random.nextInt(255);
            }
        }
    }

    @Before
    public void setUp() throws Exception {
        beforDirect= DEFAULT;
        beforReverse= DEFAULT;

                afterDirect= DCTMultiThread.directDCT(beforDirect);
                afterDirect= DCTMultiThread.reverseDCT(afterDirect);

                afterReverse= DCTMultiThread.reverseDCT(beforReverse);
                afterReverse= DCTMultiThread.directDCT(afterReverse);

    }

    @Test
    public void directArrayEquals() throws Exception {
        assertEquals(afterDirect.length,beforDirect.length);
        for (int i = 0; i < beforDirect.length; i++) {
            assertArrayEquals(beforDirect[i],afterDirect[i]);
        }
    }
    @Test
    public void reverseArrayEquals() throws Exception {
        assertEquals(afterReverse.length,beforReverse.length);
        for (int i = 0; i < beforReverse.length; i++) {
            assertArrayEquals(beforReverse[i],afterReverse[i]);
        }
    }
    @Test
    public void directInInterval()throws Exception{
        assertTrue (isArrayEqual(beforDirect,afterDirect));
    }
    @Test
    public void reverseInInterval() throws Exception {
        assertTrue (isArrayEqual(afterReverse,beforReverse));
    }

    @Test
    public void directEqualsReverse() throws Exception {
        assertTrue(isArrayEqual(beforDirect,beforReverse));
        assertTrue(isArrayEqual(afterDirect,afterReverse));
    }

    private boolean isArrayEqual(short[][]a, short[][]b) {
        if(a.length!=b.length)
            return false;
        if(a[0].length!=b[0].length)
            return false;

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                if(a[i][j]!=b[i][j])
                    if(!isInInterval(a[i][j],b[i][j],3))
                        return false;
            }
        }
        return true;
    }
    private boolean isInInterval(short a,short b,int difference){
        for(int i=-difference;i<difference;i++){
            if(a==b+i)
                return true;
        }
        return false;
    }

}