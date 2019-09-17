package com.seasky.starter.etcd.web;


import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.options.GetOption;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class EtcdTest {
    Client client = null;

    @Before
    public void init() {
        client = Client.builder().endpoints("http://127.0.0.1:2379").build();
    }

    @Test
    public void getvalues(){
        List<KeyValue> springBootWeb = getEtcdKeyWithPrefix("/springBootWeb");
        System.out.println();
    }

    @Test
    public void getValue(){
        KeyValue etcdKey = getEtcdKey("springBootWeb/aaa");
        System.out.println(new String(etcdKey.getKey().getBytes()));
    }


    @Test
    public void put(){
        putEtcdSource("springBootWeb/aaa","bbb");
    }



    public List<KeyValue> getEtcdKeyWithPrefix(String sourceKey) {
        ByteSequence key = ByteSequence.from(sourceKey.getBytes());
        List<KeyValue> ruleList = null;
        try {
            ruleList = client.getKVClient().get(key, GetOption.newBuilder().withPrefix(key).build()).get().getKvs();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return ruleList;
    }

    public KeyValue getEtcdKey(String sourceKey) {
        ByteSequence key = ByteSequence.from(sourceKey.getBytes());
        List<KeyValue> ruleList = null;
        try {
            ruleList = client.getKVClient().get(key).get().getKvs();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return ruleList.get(0);
    }

    public void putEtcdSource(String sourceKey, String sourceValue) {
        ByteSequence key = ByteSequence.from(sourceKey, Charset.defaultCharset());
        KV kvClient = client.getKVClient();
        kvClient.put(key, ByteSequence.from(sourceValue.getBytes()));
    }
}
