
package database;

import server.OmniServer;
import shared.Constants;
import shared.OmniFile;
import shared.OmniRepository;
import shared.Request;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by OmniBox on 08/11/14.
 */
public class RepositoriesDB {
    OmniServer omniServer;
    private HashSet<OmniRepository> repositories;
    private HashMap<OmniRepository,Long> timers;
    private PriorityBlockingQueue<OmniRepository> availability;

    public RepositoriesDB(OmniServer omniServer) {
        this.omniServer = omniServer;
        repositories = new HashSet<OmniRepository>();
        timers = new HashMap<OmniRepository, Long>();
        availability = new PriorityBlockingQueue(10,new AvailabilityComparator());
    }

    public synchronized void addRepo(final OmniRepository omniRepository) {
        if(!repositories.contains(omniRepository)) {
            omniServer.rebuildFileList(omniRepository);
            omniServer.sendServiceNotification();
        }
        repositories.remove(omniRepository);
        availability.remove(omniRepository);
        repositories.add(omniRepository);
        putTimer(omniRepository);
        availability.offer(omniRepository);
    }

    private synchronized void putTimer(final OmniRepository omniRepository) {
        timers.put(omniRepository,System.currentTimeMillis());
    }

    public synchronized int getNumberOfRepositories() {
        return repositories.size();
    }

    public synchronized HashSet<OmniRepository> getRepositories() { return repositories; }

    public synchronized void removeExpiredRepositories() {
        Iterator<Map.Entry<OmniRepository,Long>> it = timers.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<OmniRepository,Long> entry = it.next();
            if(entry.getValue() < System.currentTimeMillis() - Constants.EXPIRE_TIME) {
                repositories.remove(entry.getKey());
                it.remove();
                availability.remove(entry.getKey());
            }
        }
    }

    public synchronized void deleteBroadcast(final Request response) {
        for(OmniRepository omniRepository : repositories) {
            try {
                InetAddress repositoryAddress = InetAddress.getByName(omniRepository.getLocalAddr());
                int repositoryPort = omniRepository.getPort();

                DatagramSocket tempSocket = new DatagramSocket();
                omniRepository.sendUDPMessage(tempSocket,repositoryAddress,repositoryPort,response);
                tempSocket.close();
            } catch (InterruptedException e) {
            } catch (IOException e) {
            }
        }
    }

    public void replicationProcess(final OmniFile omniFile) {
        OmniRepository source;
        OmniRepository destination;

        synchronized (this) {
            source = getDownloadSource(omniFile);
            destination = getSource(omniFile, false);
        }

        if(destination != null) { // == null; File already exists in all the repositories
            if(source == null) {
                System.out.println("* No sources available *");
            } else {
                try {
                    System.out.println(source.getLocalAddr()+":"+source.getPort() + "->" + destination.getLocalAddr()+":"+destination.getPort());

                    InetAddress sourceAddress = null;
                    sourceAddress = InetAddress.getByName(source.getLocalAddr());
                    int sourcePort = source.getPort();

                    ArrayList args = new ArrayList();
                    args.add(Constants.OP_REPLICATION);
                    args.add(omniFile);
                    args.add(destination.getLocalAddr());
                    args.add(destination.getPort());
                    Request response = new Request(Constants.CMD.cmdRepositoryAddress, args);
                    omniServer.sendUDPMessage(omniServer.getDatagramSocket(), sourceAddress, sourcePort, response);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("* There is a possibility that the file is available on all the repositories *");
        }
    }

    public synchronized OmniRepository getDownloadSource(final OmniFile omniFile) {
        OmniRepository lessWorkLoadedRepository = getLessWorkLoadedRepository();

        if(lessWorkLoadedRepository.fileExists(omniFile)) {
            System.out.println(lessWorkLoadedRepository.getLocalAddr()+":"+lessWorkLoadedRepository.getPort());
            return lessWorkLoadedRepository;
        } else {
            System.out.println("Searching the sources for: " + omniFile.getFileName());
            return getSource(omniFile,true);
         }
    }

    /**
     *
     * @param omniFile
     * @param fileExists
     * @return The less workloaded Repository where the file exists or where the file not exists
     */
    private synchronized OmniRepository getSource(final OmniFile omniFile,final boolean fileExists) {
        PriorityQueue<OmniRepository> temporaryPQ = new PriorityQueue<OmniRepository>(10,new AvailabilityComparator());
        for(OmniRepository omniRepository : repositories) {
            if(fileExists) {
                if (omniRepository.fileExists(omniFile)) {
                    System.out.println("File exists on: " + omniRepository.getLocalAddr() + ":" + omniRepository.getPort());
                    temporaryPQ.offer(omniRepository);
                }
            } else {
                if (!omniRepository.fileExists(omniFile)) {
                    System.out.println("This repo needs the file: " + omniRepository.getLocalAddr() + ":" + omniRepository.getPort());
                    temporaryPQ.offer(omniRepository);
                }
            }
        }
        return temporaryPQ.peek();
    }

    public synchronized int getNumberOfReplicas(final OmniFile omniFile) {
        int replicas = 0;
        for(OmniRepository omniRepository : repositories) {
            if(omniRepository.fileExists(omniFile))
                replicas++;
        }
        return replicas;
    }

    public synchronized OmniRepository getLessWorkLoadedRepository() {
        return availability.peek();
    }

    private class AvailabilityComparator implements Comparator<OmniRepository> {
        @Override
        public int compare(OmniRepository o1, OmniRepository o2) {
            if(o1.getOppNum() < o2.getOppNum())
                return -1;
            if(o1.getOppNum() > o2.getOppNum())
                return 1;
            return 0;
        }
    }
}

