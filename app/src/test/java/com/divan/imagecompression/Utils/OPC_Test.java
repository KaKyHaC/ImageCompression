package com.divan.imagecompression.Utils;

import android.test.mock.MockContext;

import com.divan.imagecompression.Objects.DataOPC;
import com.divan.imagecompression.Objects.Flag;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.sql.Time;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Created by Dima on 18.07.2017.
 */
@RunWith(Parameterized.class)
public class OPC_Test {
    private final short[][] DEFAULT;
    short[][] origin,result;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{{},{},{},{},{},{},{}});

    }

    public OPC_Test() {
        DEFAULT = new short[8][8];
        Random random=new Random(new Date().getTime());
        for (int i=0;i<DEFAULT.length;i++){
            for (int j=0;j<DEFAULT[0].length;j++){
                DEFAULT[i][j]=(short)random.nextInt(255);
            }
        }
    }

    @Before
    public void setUp() throws Exception {
        OPC opc=OPC.getInstance();
        origin=DEFAULT;
        Flag f= new Flag((short)0);
        DataOPC dataOPC=opc.getDataOPC(origin,f);
        result=opc.getDataOrigin(dataOPC,f);
    }

    @Test
    public void Size() throws Exception {
        assertEquals(origin.length,result.length);
        assertEquals(origin[0].length,result[0].length);
    }

    @Test
    public void valuesEquals() throws Exception {
        for (int i = 0; i < origin.length; i++) {
            for (int j = 0; j < origin[0].length; j++) {
                assertEquals(origin[i][j],result[i][j]);
            }
        }
    }

}