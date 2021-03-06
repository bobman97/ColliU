package event;

import com.colliu.colliu.Master;
import java.util.ArrayList;
import java.util.Date;

/** This class handles the Event Object.
 * All methods for adding, getting and manipulating the info will be called from this class.
 */
public class EventController {

  ArrayList<Event> events;
  Master master;

  public EventController(Master masterClass) {
    master = masterClass;
    events = new ArrayList<>();
  }

  public Boolean addEvent(String name, Date eventDate, String location, int courseId) {
    return events.add(new Event(events.size(), name, eventDate, location, courseId));
  }

  /*
  ***********
    GETTERS
  ***********
   */

  /*
  This method takes an Array of integers with the user's courses
  It then loops through this array and compares it with events that haven't expired
  Then it returns an array of the Id of all those events.
   */
  Integer[] getEvents(Integer[] usersCourses) {
    ArrayList<Integer> recommendedEvents = new ArrayList<>();
    // Loop through all the courses that was sent with method
    for (Integer usersCourse : usersCourses) {
      // Loop through all the events:
      for (Event event : this.events) {
        // If event is for this course and the event is not in the past:
        if (event.getCourse() == usersCourse && event.getDate().after(new Date())) {
          recommendedEvents.add(event.getId());
        }
      }
    }
    //Convert arraylist into normal array and return:
    return recommendedEvents.toArray(new Integer[0]);
  }

  public Integer[] getNotifications(Integer[] courses, Date lastLogin) { // Will return an array of all event-IDs that have not been seen
    Integer[] recommendedEvents = getEvents(courses);
    ArrayList<Integer> notSeenEvents = new ArrayList<>();
    for (int eventId : recommendedEvents) {
      Date eventCreated = events.get(eventId).getCreationDate();
      boolean isActive = events.get(eventId).isActive();
      if (eventCreated.after(lastLogin) && isActive) {
        notSeenEvents.add(eventId);
      }
    }
    return notSeenEvents.toArray(new Integer[0]);
  }
}
