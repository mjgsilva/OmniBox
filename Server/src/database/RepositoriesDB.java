
package database;

import shared.Constants;
import shared.OmniFile;
import shared.OmniRepository;

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

    public void addRepo(OmniRepository omniRepository) {
        repositories.remove(omniRepository);
        availability.remove(omniRepository);
        repositories.add(omniRepository);
        putTimer(omniRepository);
        availability.offer(omniRepository);
    }

    private void putTimer(OmniRepository omniRepository) {
        Calendar now = Calendar.getInstance();
        timers.put(omniRepository,System.currentTimeMillis());
    }

    public void removeExpiredRepositories() {
        for(OmniRepository omniRepository : repositories) {
            if(timers.get(omniRepository) < System.currentTimeMillis() - Constants.EXPIRE_TIME) {
                repositories.remove(omniRepository);
                timers.remove(omniRepository);
                availability.remove(omniRepository);
            }
        }
    }

    public OmniRepository getDownloadSource(OmniFile omniFile) {
        OmniRepository lessWorkloadedRepository = getLessWorkloadedRepository();

        if(lessWorkloadedRepository.fileExists(omniFile)) {
            return lessWorkloadedRepository;
        } else {
            PriorityQueue<OmniRepository> temporaryPQ = new PriorityQueue<OmniRepository>(10,new AvailabilityComparator());
            for(OmniRepository omniRepository : repositories) {
                if(omniRepository.fileExists(omniFile))
                    temporaryPQ.offer(omniRepository);
            }
            return temporaryPQ.peek();
         }
    }

    public OmniRepository getLessWorkloadedRepository() {
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

