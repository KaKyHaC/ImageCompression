package com.divan.imagecompression.Utils;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Created by Dima on 23.07.2017.
 */
@RunWith(Parameterized.class)
public class OPCMultiThreadTest {
    final int THREADPOOL_SIZE;
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{{1},{2},{3},{5},{9},{20}});
    }
    public OPCMultiThreadTest(int threadpool_size){
        THREADPOOL_SIZE=threadpool_size;
    }
}