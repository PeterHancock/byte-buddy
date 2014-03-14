package com.blogspot.mydailyjava.bytebuddy.instrumentation.method.bytecode;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ByteCodeAppenderSizeTest {

    private static final int LOWER = 3, BIGGER = 5;

    @Test
    public void testMerge() throws Exception {
        ByteCodeAppender.Size left = new ByteCodeAppender.Size(LOWER, BIGGER);
        ByteCodeAppender.Size right = new ByteCodeAppender.Size(BIGGER, LOWER);
        ByteCodeAppender.Size mergedLeft = left.merge(right);
        ByteCodeAppender.Size mergedRight = right.merge(left);
        assertThat(mergedLeft, equalTo(mergedRight));
        assertThat(mergedLeft.getOperandStackSize(), is(BIGGER));
        assertThat(mergedLeft.getLocalVariableSize(), is(BIGGER));
    }
}