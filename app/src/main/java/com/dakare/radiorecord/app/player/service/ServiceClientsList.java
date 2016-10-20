package com.dakare.radiorecord.app.player.service;

import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.List;

public class ServiceClientsList {

    private final List<Messenger> serviceClients;
    private final Object lock = new Object();

    public ServiceClientsList() {
        serviceClients = new ArrayList<>();
    }

    public void registerClient(final Messenger serviceClient) {
        synchronized (lock) {
            serviceClients.add(serviceClient);
        }
    }

    public void unregisterClient(final Messenger serviceClient) {
        synchronized (lock) {
            serviceClients.remove(serviceClient);
        }
    }

    public void clear() {
        synchronized (lock) {
            serviceClients.clear();
        }
    }

    public void sendBroadcastMessage(final Message message) {
        synchronized (lock) {
            for (int i = serviceClients.size() - 1; i >= 0; i--) {
                try {
                    serviceClients.get(i).send(Message.obtain(message));
                } catch (RemoteException e) {
                    serviceClients.remove(i);
                }
            }
        }
    }
}
