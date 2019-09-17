package com.seasky.starter.autoconfigurer.etcd;


import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.options.GetOption;

import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class EtcdInstance {
    private Client client;

    public EtcdInstance(Client client) {
        this.client = client;
    }

    public Client getClient() {
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
        return ruleList;
    }

    public void delEtcdByKey(String sourceKey){
        client.getKVClient().delete(ByteSequence.from(sourceKey.getBytes()));
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

    public void putEtcdSource(String sourceKey, String sourceValue){
        ByteSequence key = ByteSequence.from(sourceKey, Charset.defaultCharset());
        KV kvClient = client.getKVClient();
        kvClient.put(key, ByteSequence.from(sourceValue.getBytes()));
    }

    public static void main(String[] args) {
//        getOne();
    }

}
