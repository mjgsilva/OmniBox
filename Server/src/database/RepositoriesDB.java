
package database;

import shared.Constants;
import shared.OmniFile;
import shared.OmniRepository;
import shared.Request;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by OmniBox on 08/11/14.
 */
public class RepositoriesDB {
    private HashSet<OmniRepository> repositories;
    private HashMap<OmniRepository,Long> timers;
    private PriorityBlockingQueue<OmniRepository> availability;

    public RepositoriesDB() {
        repositories = new HashSet<OmniRepository>();
        timers = new HashMap<OmniRepository, Long>();
        availability = new PriorityBlockingQueue(10,new AvailabilityComparator());
    }

    public synchronized void addRepo(final OmniRepository omniRepository) {
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

    public synchronized void removeExpiredRepositories() {
        for(OmniRepository omniRepository : repositories) {
            if(timers.get(omniRepository) < System.currentTimeMillis() - Constants.EXPIRE_TIME) {
                repositories.remove(omniRepository);
                timers.remove(omniRepository);
                availability.remove(omniRepository);
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
                tempSocket.close(); //TODO: Review this
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
            destination = getSource(omniFile,false);
        }

        if(destination != null) { // == null; File already exists in all the repositories
            InetAddress sourceAddress = source.getLocalAddr();
            int sourcePort = source.getPort();

            ArrayList args = new ArrayList();
            args.add(omniFile);
            args.add(destination.getLocalAddr().getHostAddress()); //TODO: Ask mister Serrano
            args.add(source.getPort());
            Request response = new Request(Constants.CMD.cmdRepositoryAddress, args);

            try {
                DatagramSocket tempSocket = new DatagramSocket();
                source.sendUDPMessage(tempSocket, sourceAddress, sourcePort, response);
                tempSocket.close(); //TODO: Review this
            } catch (InterruptedException e) {
            } catch (IOException e) {
            }
        }
    }

    public synchronized OmniRepository getDownloadSource(final OmniFile omniFile) {
        OmniRepository lessWorkLoadedRepository = getLessWorkLoadedRepository();

        if(lessWorkLoadedRepository.fileExists(omniFile)) {
            return lessWorkLoadedRepository;
        } else {
            return getSource(omniFile,true);
         }
    }

    /**
     *
     * @param omniFile
     * @param fileExists
     * @return The workloaded Repository where the file exists or where the file not exists
     */
    private synchronized OmniRepository getSource(final OmniFile omniFile,final boolean fileExists) {
        PriorityQueue<OmniRepository> temporaryPQ = new PriorityQueue<OmniRepository>(10,new AvailabilityComparator());
        for(OmniRepository omniRepository : repositories) {
            if(fileExists) {
                if (omniRepository.fileExists(omniFile))
                    temporaryPQ.offer(omniRepository);
            } else {
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

