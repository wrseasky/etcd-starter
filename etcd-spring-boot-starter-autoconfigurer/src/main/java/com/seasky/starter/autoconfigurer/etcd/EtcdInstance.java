package com.seasky.starter.autoconfigurer.etcd;


import com.seasky.starter.autoconfigurer.EtcdProperties;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.Watch;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.WatchOption;
import io.etcd.jetcd.watch.WatchEvent;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class EtcdInstance {
    private Client client;
    private EtcdProperties etcdProperties;

    public EtcdInstance(Client client, EtcdProperties etcdProperties) {
        this.client = client;
        this.etcdProperties = etcdProperties;
    }

    public Client Client(){
        return client;
    }

    public List<KeyValue> getEtcdKeyWithPrefix(String sourceKey) {
        ByteSequence key = ByteSequence.from(sourceKey.getBytes());
        List<KeyValue> ruleList = null;
        try {
            ruleList = client.getKVClient().get(key, GetOption.newBuilder().withPrefix(key).build()).get().getKvs();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
//        for (KeyValue kv : ruleList) {
//            System.out.println(new String(kv.getKey().getBytes()));
//            System.out.println(new String(kv.getValue().getBytes()));
//        }
//        client.close();
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

    public static void main(String[] args) {
//        getOne();
    }

}
