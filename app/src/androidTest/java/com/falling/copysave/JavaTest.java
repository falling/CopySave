package com.falling.copysave;

import com.falling.copysave.bean.NoteBean;

import org.junit.Test;

/**
 * Created by falling on 2017/7/18.
 */

public class JavaTest {
    @Test
    public void test(){
        NoteBean note = new NoteBean("hello world");
        System.out.println(note.getComment());
        System.out.println(note.getCopyContent());
        System.out.println(note.getDate());
    }
}
