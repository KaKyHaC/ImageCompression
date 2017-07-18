package com.divan.imagecompression.Utils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.*;

/**
 * Created by Dima on 18.07.2017.
 */
@RunWith(Parameterized.class)
public class DCTMultiThread_MultiThreadTest {
    final int THREAD_COUNT,delta,SIZE=8;
    LinkedList<short[][]> after,befor;

    @Parameterized.Parameters(name = "delta={0} ,threads={1}")
    public static Collection<Object[]> data() {
        Object[][] res=new Object[][]{
                {0,1},{1,1},{2,1},{3,1},{4,1},
                {0,2},{1,2},{2,2},{3,2},{4,2},
                {0,3},{1,3},{2,3},{3,3},{4,3},
                {0,10},{1,10},{2,10},{3,10},{4,10}};
        return Arrays.asList(res);
    }
    private void factory(){
        after=new LinkedList<>();
        befor=new LinkedList<>();

        Random random=new Random();
        short[][] buf;
        for (int c = 0; c<THREAD_COUNT; c++) {
            buf = new short[SIZE][SIZE];
            for(int i=0;i<SIZE;i++){
                for (int j = 0; j < SIZE; j++) {
                    buf[i][j]=(short)random.nextInt(255);
                }
            }
            befor.add(buf);
        }

        ExecutorService es= Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future<short[][]>> futures=new ArrayList<>();
        for (int i=0;i<befor.size();i++) {
            int finalI = i;
            futures.add(es.submit(()->{
                short[][] buffer=DCTMultiThread.directDCT(befor.get(finalI));
                return DCTMultiThread.reverseDCT(buffer);
            }));
        }
        for (Future<short[][]> future : futures) {
            try {
                after.add(future.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

    }
    /*public DCTMultiThread_MultiThreadTest() {
        factory();
    }*/
    public DCTMultiThread_MultiThreadTest(int delta,int threads){
        this.delta=delta;
        THREAD_COUNT=threads;
        factory();
    }


//    @Test
    public void ArrayEquals() throws Exception {
        for (int i = 0; i < befor.size(); i++) {
            assertEquals(befor.get(i).length, after.get(i).length);
            for (int j = 0; j < befor.get(i).length; j++) {
                assertArrayEquals(befor.get(i)[i], after.get(i)[i]);
            }
        }
    }

    @Test
    public void ArrayInInterval()throws Exception{
        for (int i = 0; i < befor.size(); i++) {
            assertTrue (isArrayEqual(befor.get(i), after.get(i),delta));
        }
    }


    private boolean isArrayEqual(short[][]a, short[][]b,int delta) {
        if(a.length!=b.length)
            return false;
        if(a[0].length!=b[0].length)
            return false;

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                if(a[i][j]!=b[i][j])
                    if(!isInInterval(a[i][j],b[i][j],delta))
                        return false;
            }
        }
        return true;
    }
    private boolean isInInterval(short a,short b,int difference){
        for(int i=-difference;i<difference;i++){
            if(a==(b+i))
                return true;
        }
        return false;
    }
}


