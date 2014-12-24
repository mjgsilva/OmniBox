
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
        availability = new PriorityBlockingQueue<OmniRepository>(10,new AvailabilityComparator());
    }

    /**
     * Add Repository
     *
     * Given an OmniRepository this method checks if the repository already
     * exists. If not, the rebuildFileSet is executed in order to assure that
     * the file's collection remains updated. To keep the repository's collection
     * updated the given repository is inserted after removing the old one, instead of
     * updating based on performance analysis.
     *
     * @param omniRepository
     */
    public synchronized void addRepo(final OmniRepository omniRepository) {
        if(!repositories.contains(omniRepository)) {
            omniServer.rebuildFileSet(omniRepository);
            omniServer.sendServiceNotification();
        }
        repositories.remove(omniRepository);
        availability.remove(omniRepository);
        repositories.add(omniRepository);
        putTimer(omniRepository);
        availability.offer(omniRepository);
    }

    /**
     * Put Timer
     *
     * Given an OmniRepository this method inserts or updates the unixtime.
     * This time represents the last communication between the repository and
     * the server.
     *
     * @param omniRepository
     */
    private synchronized void putTimer(final OmniRepository omniRepository) {
        timers.put(omniRepository, System.currentTimeMillis());
    }

    public synchronized int getNumberOfRepositories() {
        return repositories.size();
    }

    public synchronized HashSet<OmniRepository> getRepositories() { return repositories; }

    /**
     * Remove Expired Repositories
     *
     * If the last communication time between the repository and the server is above
     * the expected time, the repository should be removed - probably the repository
     * is down.
     */
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

    /**
     * Delete Broadcast
     *
     * Notifies all the repositories to remove a file
     *
     * @param response
     */
    public synchronized void deleteBroadcast(final Request response) {
        for(OmniRepository omniRepository : repositories) {
            try {
                InetAddress repositoryAddress = InetAddress.getByName(omniRepository.getLocalAddr());
                int repositoryPort = omniRepository.getPort();

                DatagramSocket tempSocket = new DatagramSocket();
                omniRepository.setNotifyWatcher(false);
                omniRepository.sendUDPMessage(tempSocket, repositoryAddress, repositoryPort, response);
                tempSocket.close();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        for(OmniRepository omniRepository : repositories) {
                omniRepository.setNotifyWatcher(true);
        }

    }

    /**
     * Replication Process
     *
     * Given an OmniFile, this method selects (if exists) a source and a destination.
     * Source is a repository that hosts the requested OmniFile, and the destination
     * is a repository where the file does not exist. An UDP notification is sent to
     * to the source in order to upload the file to the destination.
     *
     * @param omniFile
     */
    public void replicationProcess(final OmniFile omniFile) {
        OmniRepository source;
        OmniRepository destination;

        synchronized (this) {
            source = getDownloadSource(omniFile);
            destination = getSource(omniFile, false);
        }

        if(destination != null) { // == null; File already exists in all the repositories
            if(source != null) {
                try {
                    System.out.println(source.getLocalAddr()+":"+source.getPort() + "->" + destination.getLocalAddr()+":"+destination.getPort());

                    InetAddress sourceAddress = null;
                    sourceAddress = InetAddress.getByName(source.getLocalAddr());
                    int sourcePort = source.getPort();

                    ArrayList<Object> args = new ArrayList<Object>();
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
        }
    }

    /**
     * Get Download Source
     *
     * Checks if the less work loaded repository owns the file, and if not, checks for
     * a repository where the file is hosted.
     *
     * @param omniFile
     * @return the Repository that hosts the file and at the same time is dealing with
     * a lower number of operations.
     */
    public synchronized OmniRepository getDownloadSource(final OmniFile omniFile) {
        OmniRepository lessWorkLoadedRepository = getLessWorkLoadedRepository();

        if(lessWorkLoadedRepository.fileExists(omniFile)) {
            System.out.println(lessWorkLoadedRepository.getLocalAddr()+":"+lessWorkLoadedRepository.getPort());
            return lessWorkLoadedRepository;
        } else {
            return getSource(omniFile,true);
         }
    }

    /**
     * Get Source
     *
     * This is a dynamic method. Given a flag it search for a less work loaded repository. True or false
     * defines the search type.
     *
     * @param omniFile
     * @param fileExists - Flag to define the search type. True means searching for a repository
     *                   where the file exists; False means searching for a repository where the file
     *                   does not exist.
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


    /**
     * Get Number of Replicas
     *
     * @param omniFile
     * @return the number of repositories that host the file
     */
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

    /**
     * Internal class that defines the comparator for the Priority Queue
     */
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

