package ru.dotkit.mqtt.utils;

import junit.framework.Assert;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by ssv on 30.11.2017.
 */
public class TopicFilterTest {

    @Test
    public void create() throws Exception {

        TopicFilter tf = new TopicFilter("abc/def/001");
        Assert.assertEquals("abc/def/001", tf.toString());

        tf = new TopicFilter(" abc/def /001 ");
        Assert.assertEquals("abc/def/001", tf.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void create_null_exception() throws Exception {
        TopicFilter tf = new TopicFilter(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void create_empty_exception() throws Exception {
        TopicFilter tf = new TopicFilter("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void create_arg1_exception() throws Exception {
        TopicFilter tf = new TopicFilter("abc//001");
    }

    @Test(expected = IllegalArgumentException.class)
    public void create_arg2_exception() throws Exception {
        TopicFilter tf = new TopicFilter("abc/#/001");
    }

    @Test
    public void match1() throws Exception {

        TopicFilter tf = new TopicFilter("abc/def/001");
        assertTrue(tf.match("abc/def/001"));
        assertFalse(tf.match("abc/def/101"));

        tf = new TopicFilter("abc/+/001");
        assertTrue(tf.match("abc/def/001"));
        assertTrue(tf.match("abc /de/001"));
        assertFalse(tf.match("abc/def/101"));

        tf = new TopicFilter("abc/def/#");
        assertTrue(tf.match("abc/def/001"));
        assertTrue(tf.match("abc/def/001/002"));
        assertFalse(tf.match("abc/de/101"));
    }

    @Test
    public void match2() throws Exception {

        TopicFilter tf = new TopicFilter("abc/def/001");
        assertTrue(tf.match(new String[]{"abc", "def", "001"}));
        assertFalse(tf.match(new String[]{"abc", "def", "101"}));

        tf = new TopicFilter("abc/+/001");
        assertTrue(tf.match(new String[]{"abc", "def", "001"}));
        assertFalse(tf.match(new String[]{"abc", "def", "101"}));
    }

    @Test
    public void split() throws Exception {

        String[] tokens = TopicFilter.split(null);
        assertArrayEquals(new String[0], tokens);

        tokens = TopicFilter.split("");
        assertArrayEquals(new String[0], tokens);

        tokens = TopicFilter.split("abc");
        assertArrayEquals(new String[]{"abc"}, tokens);

        tokens = TopicFilter.split("abc/def/001");
        assertArrayEquals(new String[]{"abc", "def", "001"}, tokens);

        tokens = TopicFilter.split(" abc/ def /001 ");
        assertArrayEquals(new String[]{"abc", "def", "001"}, tokens);

        tokens = TopicFilter.split("abc//def/001");
        assertArrayEquals(new String[]{"abc", "", "def", "001"}, tokens);
    }
}