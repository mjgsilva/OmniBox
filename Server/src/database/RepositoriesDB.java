
package database;

import server.OmniServer;
import shared.Constants;
import shared.OmniFile;
import shared.OmniRepository;
import shared.Request;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
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
        System.out.println("* Adding repo *");
        System.out.println(omniRepository.getLocalAddr()+":"+omniRepository.getPort());
        System.out.println("* Removing repo *");
        System.out.println("Contains? " + repositories.contains(omniRepository));
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

    //TODO: Stackoverflow - Iterator + Remove -> Use javadoc to justify
    /*public synchronized void removeExpiredRepositories() {
        for(OmniRepository omniRepository : repositories) {
            if(timers.get(omniRepository) < System.currentTimeMillis() - Constants.EXPIRE_TIME) {
                repositories.remove(omniRepository);
                timers.remove(omniRepository);
                availability.remove(omniRepository);
            }
        }
    } */

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
                InetAddress repositoryAddress = omniRepository.getLocalAddr();
                int repositoryPort = omniRepository.getPort();

                DatagramSocket tempSocket = new DatagramSocket();
                omniRepository.sendUDPMessage(tempSocket,repositoryAddress,repositoryPort,response);
                //tempSocket.close(); //TODO: Review this
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

                System.out.println("* Replicating *");
                System.out.println(source.getLocalAddr()+":"+source.getPort() + "->" + destination.getLocalAddr()+":"+destination.getPort());

                InetAddress sourceAddress = source.getLocalAddr();
                int sourcePort = source.getPort();

                ArrayList args = new ArrayList();
                args.add(Constants.OP_REPLICATION);
                args.add(omniFile);
                System.out.println("Sending this as address -> " + destination.getLocalAddr().getHostAddress());
                args.add(destination.getLocalAddr().getHostAddress()); //TODO: Ask mister Serrano
                args.add(destination.getPort());
                Request response = new Request(Constants.CMD.cmdRepositoryAddress, args);

                try {
                    omniServer.sendUDPMessage(omniServer.getDatagramSocket(), sourceAddress, sourcePort, response);
                    //    tempSocket.close(); //TODO: Review this
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
            System.out.println(lessWorkLoadedRepository.getLocalAddr().getHostAddress()+":"+lessWorkLoadedRepository.getPort());
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
            System.out.println("Number of Repos: " + repositories.size());
            if(fileExists) {
                System.out.println("Searching for *file exists*");
                System.out.println("OmniRep: " + omniRepository.getLocalAddr()+":"+omniRepository.getPort());
                System.out.println("OmniRep filelist size:" + omniRepository.getFileList().size());
                for(OmniFile omniFileFromList : omniRepository.getFileList())
                {
                    System.out.println(omniFileFromList.getFileName()+":"+omniFileFromList.getFileExtension()+":"+omniFileFromList.getFileSize());
                }
                if (omniRepository.fileExists(omniFile)) {
                    System.out.println("File exists on: " + omniRepository.getLocalAddr() + ":" + omniRepository.getPort());
                    temporaryPQ.offer(omniRepository);
                } else {
                    System.out.println("*file failed exists*");
                }
            } else {
                System.out.println("Searching for *file NOT exists*");
                if (!omniRepository.fileExists(omniFile))
                    temporaryPQ.offer(omniRepository);
            }
        }
        return temporaryPQ.peek();
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

